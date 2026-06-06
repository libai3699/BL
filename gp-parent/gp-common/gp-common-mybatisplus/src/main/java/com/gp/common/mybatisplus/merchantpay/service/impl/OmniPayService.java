package com.gp.common.mybatisplus.merchantpay.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.common.core.log.ErrorTelegramUtil;
import com.gp.common.base.exception.MyPayException;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.mybatisplus.entity.MerchantPay;
import com.gp.common.mybatisplus.entity.OrderAmount;
import com.gp.common.mybatisplus.entity.OrderLawWithdraw;
import com.gp.common.mybatisplus.merchantpay.constants.PayMerchantCons;
import com.gp.common.mybatisplus.merchantpay.service.PayWService;
import com.gp.common.mybatisplus.pay.constant.PayBindDataKeyEnum;
import com.gp.common.mybatisplus.pay.constant.PayConst;
import com.gp.common.mybatisplus.pay.mpay.WalletCreateOrderResult;
import com.gp.common.mybatisplus.pay.mpay.WalletParams;
import com.gp.common.mybatisplus.pay.mpay.order.*;
import com.gp.common.mybatisplus.until.RequestUntil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * OmniPay 支付对接服务
 * 对接文档版本: 1.1
 *
 * 签名规则:
 * - 充值签名: MD5(merNo + tradeNo + orderAmount + ApiKey) => 小写32位
 * - 代付签名: MD5(merNo + tradeNo + bankCode + orderAmount + key) => 小写32位
 * - 充值回调签名: MD5(tradeNo + topupAmount + key) => 小写32位
 * - 代付回调签名: MD5(tradeNo + orderAmount + key) => 小写32位
 * - 余额查询签名: MD5(merNo + datetime + key) => 小写32位
 * - 订单查询签名: MD5(merNo + tradeNo + key) => 小写32位
 *
 * null字段需转为空字符串拼接
 */
@Slf4j
@Service
public class OmniPayService extends PayWService {

    @Resource
    private ErrorTelegramUtil errorTelegramUtil;

    @Override
    protected void doRegister() {
        registerPayWService(CollUtil.newArrayList(PayMerchantCons.OMNI_PAY));
    }

    // 支付下单（代收）
    public static String payUrl = "/pay/createOrder";
    // 支付订单查询
    public static String queryPayUrl = "/inquiry/payOrder";
    // 下发（代付）
    public static String remitUrl = "/payout/createOrder";
    // 下发订单查询
    public static String queryRemitUrl = "/inquiry/payoutOrder";
    // 余额查询
    public static String queryBalanceUrl = "/inquiry/getMerBalance";

    // ==================== 工具方法 ====================

    private String nullToEmpty(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    /**
     * 充值签名: MD5(merNo + tradeNo + orderAmount + ApiKey)
     */
    private String signDeposit(String merNo, String tradeNo, String orderAmount, String apiKey) {
        String raw = nullToEmpty(merNo) + nullToEmpty(tradeNo) + nullToEmpty(orderAmount) + nullToEmpty(apiKey);
        String sign = DigestUtil.md5Hex(raw).toLowerCase();
        log.info("OmniPay 充值签名原始串: {}, 签名: {}", raw, sign);
        return sign;
    }

    /**
     * 代付签名: MD5(merNo + tradeNo + bankCode + orderAmount + key)
     */
    private String signWithdraw(String merNo, String tradeNo, String bankCode, String orderAmount, String apiKey) {
        String raw = nullToEmpty(merNo) + nullToEmpty(tradeNo) + nullToEmpty(bankCode)
                + nullToEmpty(orderAmount) + nullToEmpty(apiKey);
        String sign = DigestUtil.md5Hex(raw).toLowerCase();
        log.info("OmniPay 代付签名原始串: {}, 签名: {}", raw, sign);
        return sign;
    }

    /**
     * 回调签名验证（充值）: MD5(tradeNo + topupAmount + key)
     */
    private boolean verifyDepositNotifySign(String tradeNo, String topupAmount, String receivedSign, String apiKey) {
        String raw = nullToEmpty(tradeNo) + nullToEmpty(topupAmount) + nullToEmpty(apiKey);
        String calculated = DigestUtil.md5Hex(raw).toLowerCase();
        log.info("OmniPay 充值回调验签 - raw: {}, calculated: {}, received: {}", raw, calculated, receivedSign);
        return calculated.equals(receivedSign);
    }

    /**
     * 回调签名验证（代付）: MD5(tradeNo + orderAmount + key)
     */
    private boolean verifyWithdrawNotifySign(String tradeNo, String orderAmount, String receivedSign, String apiKey) {
        String raw = nullToEmpty(tradeNo) + nullToEmpty(orderAmount) + nullToEmpty(apiKey);
        String calculated = DigestUtil.md5Hex(raw).toLowerCase();
        log.info("OmniPay 代付回调验签 - raw: {}, calculated: {}, received: {}", raw, calculated, receivedSign);
        return calculated.equals(receivedSign);
    }

    /**
     * 查询签名: MD5(merNo + tradeNo + key)
     */
    private String signQuery(String merNo, String tradeNo, String apiKey) {
        String raw = nullToEmpty(merNo) + nullToEmpty(tradeNo) + nullToEmpty(apiKey);
        return DigestUtil.md5Hex(raw).toLowerCase();
    }

    /**
     * 余额签名: MD5(merNo + datetime + key)
     */
    private String signBalance(String merNo, String datetime, String apiKey) {
        String raw = nullToEmpty(merNo) + nullToEmpty(datetime) + nullToEmpty(apiKey);
        return DigestUtil.md5Hex(raw).toLowerCase();
    }

    private String doFormPost(String url, Map<String, Object> params) {
        // 注: 异常类型从 MyPayException 改为 RuntimeException(由 PayWService 统一抛出),
        // 与 UpstreamErrorClassifier 配合识别 uncertain 网络异常.
        return PayWService.doPostForm(url, params, 20000, "OmniPay");
    }

    // ==================== 回调参数解析 ====================

    @Override
    public JSONObject getNotifyRequestParam(HttpServletRequest request) {
        try {
            String contentType = request.getContentType();
            log.info("OmniPay 回调 ContentType: {}", contentType);

            // OmniPay 实际使用 multipart/form-data 发送回调
            // 必须先判断并读取 body，否则 doParamCompatible 内部会消费 InputStream
            if (contentType != null && contentType.toLowerCase().contains("multipart")) {
                String rawBody = RequestUntil.readRequestBody(request);
                log.info("OmniPay 回调 rawBody(multipart): {}", rawBody);
                if (StrUtil.isNotBlank(rawBody) && rawBody.contains("Content-Disposition")) {
                    JSONObject result = parseMultipartBody(rawBody);
                    log.info("OmniPay multipart解析结果: {}", result.toJSONString());
                    if (!result.isEmpty()) {
                        return result;
                    }
                }
            }

            // form-urlencoded 或 JSON：doParamCompatible 内部会自动处理 XssWrapper / 普通请求
            Map<String, String> paramMap = RequestUntil.doParamCompatible(request);
            log.info("OmniPay 回调 paramMap: {}", JSON.toJSONString(paramMap));
            if (!paramMap.isEmpty()) {
                return JSON.parseObject(JSON.toJSONString(paramMap));
            }

            // 兜底：body JSON 格式（multipart 走不到这里）
            String body = RequestUntil.readRequestBody(request);
            log.info("OmniPay 回调原始Body: {}", body);
            if (StrUtil.isNotBlank(body)) {
                String trimmed = body.trim();
                if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
                    JSONObject result = JSON.parseObject(body);
                    if (result != null && !result.isEmpty()) {
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            log.error("OmniPay 回调参数解析异常", e);
        }
        return new JSONObject();
    }
    /**
     * 手动解析 multipart/form-data 格式的 body
     * OmniPay 回调使用此格式发送数据，通过正则提取每个字段的 name 和 value
     */
    private JSONObject parseMultipartBody(String body) {
        JSONObject result = new JSONObject();
        try {
            log.info("OmniPay parseMultipartBody body长度: {}", body == null ? 0 : body.length());
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                    "Content-Disposition:\\s*form-data;\\s*name=\"([^\"]+)\"\\s*\\r?\\n\\r?\\n([^\\-]*)");
            java.util.regex.Matcher matcher = pattern.matcher(body);
            int count = 0;
            while (matcher.find()) {
                count++;
                String name = matcher.group(1).trim();
                String value = matcher.group(2).trim();
                log.info("OmniPay multipart字段[{}]: name={}, value={}", count, name,
                        value.length() > 50 ? value.substring(0, 50) + "..." : value);
                if (!name.isEmpty()) {
                    result.put(name, value);
                }
            }
            log.info("OmniPay parseMultipartBody 共解析{}个字段, result={}", count, result.toJSONString());
        } catch (Exception e) {
            log.error("OmniPay multipart解析异常", e);
        }
        return result;
    }

    // ==================== 创建充值订单 ====================


    @SneakyThrows
    @Override
    public WalletCreateOrderResult createOrder(CreateOrder createOrder) {
        String payParam = createOrder.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);

        String domainName = filterByApp(walletParams, "domainName");
        String merNo = filterByApp(walletParams, "merchatCode");
        String apiKey = filterByApp(walletParams, "merchantKey");
        String notifyUrl = filterByApp(walletParams, "rechargeNotifyUrl");
        String cType = filterByApp(walletParams, "cType");
        if (StrUtil.isBlank(cType)) {
            cType = "BankToBank";
        }

        String orderAmount = createOrder.getOriginalAmount().setScale(0, RoundingMode.DOWN).toString();
        String tradeNo = createOrder.getOrderNo();

        // 生成签名: MD5(merNo + tradeNo + orderAmount + ApiKey)
        String sign = signDeposit(merNo, tradeNo, orderAmount, apiKey);

        Map<String, Object> bindData = createOrder.getBindData();
        String playerName = PayBindDataKeyEnum.PLAYER_NAME.getValue(bindData);
        if (StrUtil.isBlank(playerName)) {
            playerName = createOrder.getRemark() != null ? createOrder.getRemark() : "player";
        }

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("merNo", merNo);
        params.put("tradeNo", tradeNo);
        params.put("cType", cType);
        params.put("orderAmount", orderAmount);
        params.put("playerName", playerName);
        params.put("notifyUrl", notifyUrl);

        params.put("sign", sign);

        log.info("OmniPay 充值下单请求参数: {}", JSON.toJSONString(params));

        String url = (domainName.endsWith("/") ? domainName.substring(0, domainName.length() - 1) : domainName) + payUrl;
        String response = doFormPost(url, params);
        log.info("OmniPay 充值下单返回结果: {}", response);

        JSONObject resultJson = JSON.parseObject(response);
        if (resultJson != null && resultJson.getIntValue("Success") == 1) {
            WalletCreateOrderResult result = new WalletCreateOrderResult();
            // 优先取PayPage支付页面
            String payPage = resultJson.getString("PayPage");
            if (StrUtil.isNotBlank(payPage)) {
                result.setUrl(payPage);
            }
            result.setUpOrderNo(resultJson.getString("oid"));
            result.setUpInfo(response);
            return result;
        }

        String errorMsg = resultJson != null ? resultJson.getString("Message") : "Unknown Error";
        String errorLog = StrUtil.format("OmniPay 充值下单失败: {}", response);
        log.error(errorLog);
        errorTelegramUtil.dealErrorMsg(errorLog);
        throw new MyPayException(StrUtil.isNotBlank(errorMsg) ? errorMsg : MessagesUtils.get("bot.pay.ZFYC"));
    }

    // ==================== 充值回调 ====================

    @Override
    public PayCallbackResult callbackPay(MerchantPay merchantPay, JSONObject notifyData) {
        try {
            log.info("OmniPay 充值回调数据: {}", notifyData.toJSONString());

            List<WalletParams> walletParams = JSON.parseArray(merchantPay.getParamStr(), WalletParams.class);
            String apiKey = filterByApp(walletParams, "merchantKey");

            String tradeNo = notifyData.getString("tradeNo");
            String topupAmount = notifyData.getString("topupAmount");
            Integer tradeStatus = notifyData.getInteger("tradeStatus");
            String receivedSign = notifyData.getString("sign");

            // 手动回调（后台查单）时 sign 为 null，跳过验签，直接走查单确认
            if (receivedSign != null) {
                // 验签: MD5(tradeNo + topupAmount + key)
                if (!verifyDepositNotifySign(tradeNo, topupAmount, receivedSign, apiKey)) {
                    log.error("OmniPay 充值回调签名校验失败");
                    return PayCallbackResult.failed("fail");
                }

                // tradeStatus: 1=存款成功
                if (tradeStatus == null || tradeStatus != 1) {
                    log.info("OmniPay 充值回调订单状态非成功, tradeStatus: {}", tradeStatus);
                    return PayCallbackResult.failed("SUCCESS");
                }
            } else {
                log.info("OmniPay 充值手动回调(sign为空), 跳过验签, 直接查单确认, tradeNo: {}", tradeNo);
            }

            // 主动查单二次确认
            QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
            queryOrderInfo.setOrderNo(tradeNo);
            queryOrderInfo.setPayParam(merchantPay.getParamStr());
            OrderInfoDo orderInfoDo = queryOrder(queryOrderInfo);
            if (orderInfoDo == null || orderInfoDo.getData() == null || orderInfoDo.getData().getOrderStatus() != 1) {
                log.error("OmniPay 充值回调查单确认失败, tradeNo: {}", tradeNo);
                return PayCallbackResult.failed("fail");
            }

            // 手动回调时 topupAmount 可能为空，从查单结果获取实际金额
            BigDecimal actualAmount;
            if (topupAmount != null) {
                actualAmount = new BigDecimal(topupAmount);
            } else if (orderInfoDo.getData().getActualAmount() != null) {
                actualAmount = orderInfoDo.getData().getActualAmount();
            } else {
                actualAmount = null;
            }

            return PayCallbackResult.builder()
                    .orderNo(tradeNo)
                    .merchantOrderNo(tradeNo)
                    .orderStatus(1)
                    .amount(actualAmount)
                    .responseMsg("SUCCESS")
                    .isSuccess(true)
                    .build();

        } catch (Exception e) {
            log.error("OmniPay 充值回调异常", e);
            return PayCallbackResult.failed("fail");
        }
    }

    @Override
    public JSONObject callbackPayParam(MerchantPay merchantPay, OrderAmount orderAmount) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tradeNo", orderAmount.getOrderNo());
        return jsonObject;
    }

    // ==================== 充值查单 ====================

    @Override
    public OrderInfoDo queryOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);

        String domainName = filterByApp(walletParams, "domainName");
        String merNo = filterByApp(walletParams, "merchatCode");
        String apiKey = filterByApp(walletParams, "merchantKey");

        // 签名: MD5(merNo + tradeNo + key)
        String sign = signQuery(merNo, queryOrderInfo.getOrderNo(), apiKey);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("merNo", merNo);
        params.put("tradeNo", queryOrderInfo.getOrderNo());
        params.put("sign", sign);

        log.info("OmniPay 充值查单请求参数: {}", JSON.toJSONString(params));
        String url = (domainName.endsWith("/") ? domainName.substring(0, domainName.length() - 1) : domainName) + queryPayUrl;
        String response = doFormPost(url, params);
        log.info("OmniPay 充值查单返回结果: {}", response);

        JSONObject resultJson = JSON.parseObject(response);

        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
        orderInfo.setCurrencyId(PayConst.currency_id);

        if (resultJson != null && resultJson.getIntValue("Success") == 1) {
            // status: 1=存款成功
            Integer status = resultJson.getInteger("status");
            orderInfo.setOrderStatus(status != null && status == 1 ? 1 : 0);
            if (resultJson.containsKey("topupAmount")) {
                orderInfo.setActualAmount(resultJson.getBigDecimal("topupAmount"));
            }
            orderInfo.setMerchantOrderNo(queryOrderInfo.getOrderNo());
        } else {
            orderInfo.setOrderStatus(0);
        }

        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    // ==================== 代付（提现） ====================

    @SneakyThrows
    @Override
    public WithdrawResultDo withdraw(WithdrawOrder withdrawOrder) {
        String payParam = withdrawOrder.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);

        String domainName = filterByApp(walletParams, "domainName");
        String merNo = filterByApp(walletParams, "merchatCode");
        String apiKey = filterByApp(walletParams, "merchantKey");
        String notifyUrl = filterByApp(walletParams, "withdrawNotifyUrl");
        String cType = filterByApp(walletParams, "withdrawCType");
        if (StrUtil.isBlank(cType)) {
            cType = "Payout";
        }

        String orderAmount = withdrawOrder.getAmount().setScale(2, RoundingMode.HALF_UP).toString();
        String tradeNo = withdrawOrder.getOrderNo();

        // 从提现参数获取银行卡信息
        Map<String, Object> bindData = withdrawOrder.getBindData();
        if (bindData == null || bindData.isEmpty()) {
            bindData = withdrawOrder.getWithdrawParam();
        }
        String bankCode = PayBindDataKeyEnum.BANK_CODE.getValue(bindData);
        String bankCardNo = PayBindDataKeyEnum.ACCOUNT.getValue(bindData);
        String accountName = PayBindDataKeyEnum.PLAYER_NAME.getValue(bindData);
        String bankBranch = PayBindDataKeyEnum.BANK_BRANCH.getValue(bindData);
        String playerEmail = PayBindDataKeyEnum.EMAIL.getValue(bindData);

        // 签名: MD5(merNo + tradeNo + bankCode + orderAmount + key)
        String sign = signWithdraw(merNo, tradeNo, bankCode, orderAmount, apiKey);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("merNo", merNo);
        params.put("tradeNo", tradeNo);
        params.put("cType", cType);
        params.put("bankCode", bankCode);
        params.put("bankCardNo", bankCardNo);
        params.put("orderAmount", orderAmount);
        params.put("accountName", accountName);
        params.put("openProvince", "1");
        params.put("openCity", "1");
        params.put("notifyUrl", notifyUrl);
        params.put("playerEmail", playerEmail);
        if (StrUtil.isNotBlank(bankBranch)) {
            params.put("bankBranch", bankBranch);
        }
        params.put("sign", sign);

        log.info("OmniPay 代付请求参数: {}", JSON.toJSONString(params));

        String url = (domainName.endsWith("/") ? domainName.substring(0, domainName.length() - 1) : domainName) + remitUrl;
        String response = doFormPost(url, params);
        log.info("OmniPay 代付返回结果: {}", response);

        JSONObject resultJson = JSON.parseObject(response);
        WithdrawResultDo withdrawResultDo = new WithdrawResultDo();

        if (resultJson != null && resultJson.getIntValue("Success") == 1) {
            WithdrawResultDo.Result result = new WithdrawResultDo.Result();
            result.setOrderNo(withdrawOrder.getOrderNo());
            result.setUpOrderNo(resultJson.getString("oid"));
            result.setAmount(withdrawOrder.getAmount());
            result.setCurrencyId(PayConst.currency_id);
            result.setUpInfo(response);

            withdrawResultDo.setCode(0);
            withdrawResultDo.setData(result);
            withdrawResultDo.setMsg("成功");
        } else {
            String msg = resultJson != null ? resultJson.getString("Message") : "Unknown Error";
            log.error("OmniPay 代付失败: {}", response);
            errorTelegramUtil.dealErrorMsg("OmniPay 代付失败: " + response);
            throw new MyPayException(StrUtil.isNotBlank(msg) ? msg : "OmniPay代付失败");
        }
        return withdrawResultDo;
    }

    // ==================== 代付回调 ====================

    @Override
    public PayCallbackResult callbackWithdraw(MerchantPay merchantPay, JSONObject notifyData) {
        try {
            log.info("OmniPay 代付回调数据: {}", notifyData.toJSONString());

            List<WalletParams> walletParams = JSON.parseArray(merchantPay.getParamStr(), WalletParams.class);
            String apiKey = filterByApp(walletParams, "merchantKey");

            String tradeNo = notifyData.getString("tradeNo");
            String orderAmount = notifyData.getString("orderAmount");
            Integer tradeStatus = notifyData.getInteger("tradeStatus");
            String receivedSign = notifyData.getString("sign");

            // 验签: MD5(tradeNo + orderAmount + key)
            if (!verifyWithdrawNotifySign(tradeNo, orderAmount, receivedSign, apiKey)) {
                log.error("OmniPay 代付回调签名校验失败");
                return PayCallbackResult.failed("fail");
            }

            // tradeStatus: 1=出款成功, -1=出款失败, -2=建单失败
            int orderStatus;
            if (tradeStatus != null && tradeStatus == 1) {
                orderStatus = 1; // 成功
            } else if (tradeStatus != null && (tradeStatus == -1 || tradeStatus == -2)) {
                orderStatus = 2; // 失败
            } else {
                // 0=处理中, 8=API审核, 9=Manual audit => 不做最终处理
                log.info("OmniPay 代付回调订单处理中, tradeStatus: {}", tradeStatus);
                return PayCallbackResult.failed("SUCCESS");
            }

            return PayCallbackResult.builder()
                    .orderNo(tradeNo)
                    .merchantOrderNo(tradeNo)
                    .orderStatus(orderStatus)
                    .responseMsg("SUCCESS")
                    .isSuccess(true)
                    .build();

        } catch (Exception e) {
            log.error("OmniPay 代付回调异常", e);
            return PayCallbackResult.failed("fail");
        }
    }

    @Override
    public JSONObject callbackWithdrawParam(MerchantPay merchantPay, OrderLawWithdraw orderLawWithdraw) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("tradeNo", orderLawWithdraw.getOrderNo());
        return jsonObject;
    }

    // ==================== 代付查单 ====================

    @Override
    public OrderInfoDo queryWithdrawOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);

        String domainName = filterByApp(walletParams, "domainName");
        String merNo = filterByApp(walletParams, "merchatCode");
        String apiKey = filterByApp(walletParams, "merchantKey");

        // 签名: MD5(merNo + tradeNo + key)
        String sign = signQuery(merNo, queryOrderInfo.getOrderNo(), apiKey);

        Map<String, Object> params = new LinkedHashMap<>();
        params.put("merNo", merNo);
        params.put("tradeNo", queryOrderInfo.getOrderNo());
        params.put("sign", sign);

        log.info("OmniPay 代付查单请求参数: {}", JSON.toJSONString(params));
        String url = (domainName.endsWith("/") ? domainName.substring(0, domainName.length() - 1) : domainName) + queryRemitUrl;
        String response = doFormPost(url, params);
        log.info("OmniPay 代付查单返回结果: {}", response);

        JSONObject resultJson = JSON.parseObject(response);

        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
        orderInfo.setCurrencyId(PayConst.currency_id);

        if (resultJson != null && resultJson.getIntValue("Success") == 1) {
            // status: 1=出款成功, -1=出款失败, -2=建单失败
            Integer status = resultJson.getInteger("status");
            if (status != null && status == 1) {
                orderInfo.setOrderStatus(1);
            } else if (status != null && (status == -1 || status == -2)) {
                orderInfo.setOrderStatus(2); // 失败
            } else {
                orderInfo.setOrderStatus(0); // 处理中
            }
            if (resultJson.containsKey("topupAmount")) {
                orderInfo.setActualAmount(resultJson.getBigDecimal("topupAmount"));
            }
            orderInfo.setMerchantOrderNo(queryOrderInfo.getOrderNo());
        } else {
            orderInfo.setOrderStatus(0);
        }

        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    // ==================== 余额查询 ====================

    @Override
    public String queryMoney(MerchantPay merchantPay) {
        try {
            List<WalletParams> walletParams = JSON.parseArray(merchantPay.getParamStr(), WalletParams.class);

            String domainName = filterByApp(walletParams, "domainName");
            String merNo = filterByApp(walletParams, "merchatCode");
            String apiKey = filterByApp(walletParams, "merchantKey");

            // 时间戳格式: YYYYMMDDhhmmss
            String datetime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

            // 签名: MD5(merNo + datetime + key)
            String sign = signBalance(merNo, datetime, apiKey);

            Map<String, Object> params = new LinkedHashMap<>();
            params.put("merNo", merNo);
            params.put("datetime", datetime);
            params.put("sign", sign);

            log.info("OmniPay 余额查询请求参数: {}", JSON.toJSONString(params));
            String url = (domainName.endsWith("/") ? domainName.substring(0, domainName.length() - 1) : domainName) + queryBalanceUrl;
            String response = doFormPost(url, params);
            log.info("OmniPay 余额查询返回结果: {}", response);

            JSONObject resultJson = JSON.parseObject(response);
            if (resultJson != null && resultJson.getIntValue("Success") == 1) {
                return resultJson.getString("Balance");
            }
            return "查询失败: " + (resultJson != null ? resultJson.getString("Message") : "Unknown");
        } catch (Exception e) {
            log.error("OmniPay 余额查询异常", e);
            return "查询异常";
        }
    }

    // ==================== 反查（不支持） ====================

    @Override
    public JSONObject reverseCheckOrderWithdraw(MerchantPay merchantPay, Map<String, Object> requestMap) {
        return null;
    }
}
