package com.gp.common.mybatisplus.merchantpay.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.common.core.log.ErrorTelegramUtil;
import com.gp.common.base.exception.MyPayException;
import com.gp.common.base.utils.IpUtil;
import com.gp.common.base.utils.MD5Util;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.mybatisplus.entity.MerchantPay;
import com.gp.common.mybatisplus.entity.OrderAmount;
import com.gp.common.mybatisplus.entity.OrderLawWithdraw;
import com.gp.common.mybatisplus.entity.PayChannel;
import com.gp.common.mybatisplus.merchantpay.constants.PayMerchantCons;
import com.gp.common.mybatisplus.merchantpay.constants.PayTypeCons;
import com.gp.common.mybatisplus.merchantpay.constants.YLPayTypeAttribute;
import com.gp.common.mybatisplus.merchantpay.constants.ZFBPayTypeAttribute;
import com.gp.common.mybatisplus.merchantpay.service.PayWService;
import com.gp.common.mybatisplus.pay.constant.PayConst;
import com.gp.common.mybatisplus.pay.mpay.WalletCreateOrderResult;
import com.gp.common.mybatisplus.pay.mpay.WalletParams;
import com.gp.common.mybatisplus.pay.mpay.order.*;
import com.gp.common.mybatisplus.service.PayChannelService;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 支付宝支付服务
 * 支持代收（充值）
 *
 * @author axing
 */
@Slf4j
@Service
public class AlipayPAYService extends PayWService {

    /** 支付类型 → 收款参数解析器，新增类型只需在此注册一行 */
    private static final Map<String, Function<JSONObject, Map<String, String>>> WITHDRAW_PARSERS = new HashMap<>();
    static {
        WITHDRAW_PARSERS.put(PayTypeCons.PAY_ZFB, ZFBPayTypeAttribute::parse);
        WITHDRAW_PARSERS.put(PayTypeCons.PAY_YL,  YLPayTypeAttribute::parse);
    }

    @Resource
    private ErrorTelegramUtil errorTelegramUtil;
    @Resource
    private PayChannelService payChannelService;

    // status成功码 - 1代表下单成功
    public static Integer statusSuccess = 1;

    // 支付下单（代收）
    public static String payUrl = "/Pay_Index.html";
    // 支付订单查询
    public static String queryPayUrl = "/Pay_Trade_query.html";

    // 代付提交地址
    public static String withdrawUrl = "/Payment_index.html";
    // 代付结果查询
    public static String queryWithdrawUrl = "/Payment_Dfpay_query.html";
    // 余额查询
    public static String queryMoney = "/Pay_Trade_querymoney.html";
    // 代付提交
    // 代付结果查询

    @Override
    protected void doRegister() {
        registerPayWService(CollUtil.newArrayList(
                PayMerchantCons.ALIPAY_PAY));
    }

    @Override
    public String queryMoney(MerchantPay merchantPay) {
        return "暂不支持";
    }


    public String getSuccessResponse() {
        return "OK";
    }


    public String getFailResponse() {
        return "ERROR";
    }

    /**
     * 支付宝支付 订单查询（充值查单）
     */
    @Override
    public OrderInfoDo queryOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String merchantKey = filterByApp(walletParams, "merchantKey"); // 支付密钥
        String mchId = filterByApp(walletParams, "merchatCode");
        // 构建查询请求参数
        String payOrderId = queryOrderInfo.getOrderNo();
        // 构建请求参数（参与签名的参数）
        Map<String, Object> signParams = new TreeMap<>();
        signParams.put("pay_memberid", mchId);
        signParams.put("pay_orderid", payOrderId);
        // 生成签名
        String sign = signAlipay(signParams, merchantKey);
        // 构建完整请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("pay_memberid", mchId);
        params.put("pay_orderid", payOrderId);
        params.put("pay_md5sign", sign);

        log.info("支付宝支付订单查询请求参数: {}", JSON.toJSONString(params));

        // 发送查询请求（POST form-urlencoded格式）
        JSONObject resultJsonObject = doPostFormRequest(domainName + queryPayUrl, params);
        log.info("支付宝支付订单查询返回结果: {}", resultJsonObject.toJSONString());

        // 验证响应签名
        Map<String, Object> responseMap = new HashMap<>();
        resultJsonObject.forEach((key, value) -> responseMap.put(key, value));
        if (!verifyAlipayQueryResponse(responseMap, merchantKey)) {
            log.error("支付宝支付订单查询-响应签名验证失败");
            throw new MyPayException("订单查询响应签名验证失败");
        }

        // 构建返回结果
        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
        orderInfo.setCurrencyId(PayConst.currency_id);

        // 判断订单状态: returncode="00"表示请求成功，trade_state="SUCCESS"表示支付成功
        Integer statusSys = 0; // 默认未支付
        String returncode = resultJsonObject.getString("returncode");
        String transaction_id = resultJsonObject.getString("transaction_id");
        if ("00".equals(returncode)) {
            String tradeState = resultJsonObject.getString("trade_state");
            if ("SUCCESS".equals(tradeState)) {
                statusSys = 1; // 已支付
            }
        }

        // 提取查单金额
        BigDecimal queryAmount = resultJsonObject.getBigDecimal("amount");
        orderInfo.setAmount(queryAmount);

        orderInfo.setOrderStatus(statusSys);
        orderInfo.setOrderNo(payOrderId);
        orderInfo.setMerchantOrderNo(transaction_id);
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    /**
     * 支付宝支付 代付订单查询（提现查单）
     */
    @Override
    public OrderInfoDo queryWithdrawOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String withdrawDomainName = filterByApp(walletParams, "withdrawDomainName");
        String withdrawMerchantKey = filterByApp(walletParams, "withdrawMerchantKey"); // 代付密钥
        String withdrawMchId = filterByApp(walletParams, "withdrawMerchantCode");

        // 构建查询请求参数
        String payOrderId = queryOrderInfo.getOrderNo();

        // 构建请求参数（参与签名的参数）
        Map<String, Object> signParams = new TreeMap<>();
        signParams.put("payment_memberid", withdrawMchId);
        signParams.put("payment_orderid", payOrderId);

        // 生成签名
        String sign = signAlipay(signParams, withdrawMerchantKey);

        // 构建完整请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("payment_memberid", withdrawMchId);
        params.put("payment_orderid", payOrderId);
        params.put("payment_md5sign", sign);

        log.info("支付宝支付代付订单查询请求参数: {}", JSON.toJSONString(params));

        // 发送查询请求（POST form-urlencoded格式）
        JSONObject resultJsonObject = doPostFormRequest(withdrawDomainName + queryWithdrawUrl, params);
        log.info("支付宝支付代付订单查询返回结果: {}", resultJsonObject.toJSONString());

        // 验证响应签名
        Map<String, Object> responseMap = new HashMap<>();
        resultJsonObject.forEach((key, value) -> responseMap.put(key, value));
        if (!verifyAlipayWithdrawQueryResponse(responseMap, withdrawMerchantKey)) {
            log.error("支付宝支付代付订单查询-响应签名验证失败");
            throw new MyPayException("代付订单查询响应签名验证失败");
        }

        // 构建返回结果
        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
        orderInfo.setCurrencyId(PayConst.currency_id);

        // 判断订单状态: status="success"且refCode="1"表示已打款成功
        Integer statusSys = 0; // 默认未支付
        String status = resultJsonObject.getString("status");
        String transaction_id = resultJsonObject.getString("transaction_id");
        if ("success".equals(status)) {
            String refCode = resultJsonObject.getString("refCode");
            if ("1".equals(refCode)) {
                statusSys = 1; // 已打款
            } else if ("2".equals(refCode)) {
                statusSys = 2; // 失败，未打款
            } else if ("3".equals(refCode) || "4".equals(refCode) || "6".equals(refCode)) {
                statusSys = 0; // 处理中/待处理/待审核
            } else if ("5".equals(refCode)) {
                statusSys = 2; // 审核驳回
            }
        }

        orderInfo.setOrderStatus(statusSys);
        orderInfo.setOrderNo(payOrderId);
        orderInfo.setMerchantOrderNo(transaction_id);
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    /**
     * 支付宝支付 创建支付订单（代收/充值）
     *
     * @param createOrder 订单信息
     * @return 订单结果
     */
    @SneakyThrows
    @Override
    public WalletCreateOrderResult createOrder(CreateOrder createOrder) {
        String payParam = createOrder.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String merchantKey = filterByApp(walletParams, "merchantKey"); // 支付密钥
        String mchId = filterByApp(walletParams, "merchatCode"); // 商户号
        String rechargeNotifyUrl = filterByApp(walletParams, "rechargeNotifyUrl"); // 服务端通知
        String extParam = createOrder.getExtParam();
        JSONObject jsonObject = JSONObject.parseObject(extParam);
        //获取通道编码
        String bankCode = createOrder.getPayChannelCode();
        // 构建下单参数
        String payOrderId = createOrder.getOrderNo();
        BigDecimal amount = createOrder.getOriginalAmount().setScale(2, RoundingMode.DOWN); // 金额保留2位小数

        // 格式化提交时间：2016-12-26 18:18:18
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String payApplyDate = sdf.format(new Date());

        // 获取付款人IP
        String payIp = IpUtil.getRealIpAddr();
        if (StrUtil.isBlank(payIp)) {
            payIp = "127.0.0.1";
        }

        // 构建请求参数（TreeMap自动按key排序）
        Map<String, Object> params = new TreeMap<>();
        params.put("pay_amount", amount.toString());
        params.put("pay_applydate", payApplyDate);
        params.put("pay_bankcode", bankCode);
        params.put("pay_memberid", mchId);
        params.put("pay_notifyurl", rechargeNotifyUrl);
        params.put("pay_orderid", payOrderId);

        // 生成签名并添加到参数中
        String sign = signAlipay(params, merchantKey);
        params.put("pay_md5sign", sign);
        params.put("pay_ip", payIp); // pay_ip不参与签名，签名后再加入

        log.info("支付宝支付创建订单请求参数: {}", JSON.toJSONString(params));

        // 发送请求（POST form-urlencoded格式）
        JSONObject resultJsonObject = doPostFormRequest(domainName + payUrl, params);
        log.info("支付宝支付创建订单返回结果: {}", resultJsonObject.toJSONString());

        // 判断订单状态: status=1为下单成功
        Integer status = resultJsonObject.getInteger("status");
        if (statusSuccess.equals(status)) {
            // 优先取h5_url，如果没有则取pay_url
            String payUrlValue = resultJsonObject.getString("h5_url");
            if (StrUtil.isBlank(payUrlValue)) {
                payUrlValue = resultJsonObject.getString("pay_url");
            }

            if (StrUtil.isNotBlank(payUrlValue)) {
                // 返回结果
                WalletCreateOrderResult result = new WalletCreateOrderResult();
                result.setUrl(payUrlValue);
                return result;
            } else {
                throw new MyPayException("支付宝支付下单返回数据为空，未找到支付链接");
            }
        } else {
            String msg = StrUtil.format("商户编码: {}, 支付宝支付下单异常: status={}, msg={}",
                    createOrder.getPayChannelCode(), status, resultJsonObject.getString("msg"));
            errorTelegramUtil.dealErrorMsg(msg);
            throw new MyPayException(MessagesUtils.get("bot.pay.ZFYC"));
        }
    }

    @Override
    public JSONObject callbackPayParam(MerchantPay merchantPay, OrderAmount orderAmount) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderid", orderAmount.getOrderNo());
        return jsonObject;
    }

    /**
     * 支付宝支付 创建代付订单（提现）
     */
    @SneakyThrows
    @Override
    public WithdrawResultDo withdraw(WithdrawOrder withdrawOrder) {
        String payParam = withdrawOrder.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String withdrawDomainName = filterByApp(walletParams, "withdrawDomainName"); // 代付域名
        String withdrawMerchantKey = filterByApp(walletParams, "withdrawMerchantKey"); // 代付密钥
        String withdrawMchId = filterByApp(walletParams, "withdrawMerchantCode"); // 代付商户号
        String withdrawNotifyUrl = filterByApp(walletParams, "withdrawNotifyUrl"); // 代付回调地址

//        String extParam = withdrawOrder.getWithdrawParam();
        Map<String, Object> extParamMap = withdrawOrder.getWithdrawParam();
        if (MapUtil.isEmpty(extParamMap)) {
            throw new MyPayException("订单缺少支付卡信息");
        }
//        JSONObject jsonObject = JSONObject.parseObject(extParam);
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(extParamMap));

        //解析支付卡获取
        PayChannel payChannel = withdrawOrder.getPayChannel();
        //通道编码
        String type = payChannel.getPayMethod(); // 代付通道编码

        // 构建代付参数
        String payOrderId = withdrawOrder.getOrderNo();
        BigDecimal amount = withdrawOrder.getAmount().setScale(2, RoundingMode.DOWN); // 金额保留2位小数

        //支付类型编码
        String payTypeCode = withdrawOrder.getPayType().getCode();

        // 根据支付类型解析收款方信息
        Function<JSONObject, Map<String, String>> parser = WITHDRAW_PARSERS.get(payTypeCode);
        if (parser == null) {
            throw new MyPayException("不支持的支付类型: " + payTypeCode);
        }
        Map<String, String> account = parser.apply(jsonObject);
        String accountName    = account.getOrDefault("accountName", "");
        String accountNumber  = account.getOrDefault("accountNumber", "");
        String bankName       = account.getOrDefault("bankName", "支付宝");
        String bankBranchName = account.getOrDefault("bankBranchName", "支付宝");

        // 构建请求参数
        Map<String, Object> params = new TreeMap<>();
        params.put("payment_amount", amount.toString());
        params.put("payment_bankcode", type);
        params.put("payment_bankname", bankName);
        params.put("payment_accountname", accountName);
        params.put("payment_cardnumber", accountNumber);
        params.put("payment_memberid", withdrawMchId);
        params.put("payment_notifyurl", withdrawNotifyUrl);
        params.put("payment_orderid", payOrderId);
        params.put("payment_subbranch", bankBranchName);
        params.put("payment_province", "广州");
        params.put("payment_city", "广州市");

        // 生成签名并添加到参数中
        String sign = signAlipay(params, withdrawMerchantKey);
        params.put("payment_md5sign", sign);

        log.info("支付宝支付创建代付订单请求参数: {}", JSON.toJSONString(params));

        // 发送请求（POST form-urlencoded格式）
        JSONObject resultJsonObject = doPostFormRequest(withdrawDomainName + withdrawUrl, params);
        log.info("支付宝支付创建代付订单返回结果: {}", resultJsonObject.toJSONString());

        // 判断订单状态: status=success为提交成功
        String status = resultJsonObject.getString("status");
        WithdrawResultDo withdrawResultDo = new WithdrawResultDo();

        if ("success".equals(status)) {
            // 代付提交成功
            String transactionId = resultJsonObject.getString("transaction_id"); // 平台流水号

            WithdrawResultDo.Result result = new WithdrawResultDo.Result();
            result.setOrderNo(payOrderId);
            result.setUpOrderNo(transactionId);
            result.setUpInfo(resultJsonObject.toJSONString());
            result.setCurrencyId(PayConst.currency_id);
            result.setAmount(amount);
            result.setCreateTime(System.currentTimeMillis());

            withdrawResultDo.setCode(0);
            withdrawResultDo.setMsg("代付订单提交成功");
            withdrawResultDo.setData(result);
        } else {
            // 代付提交失败
            String msg = resultJsonObject.getString("msg");
            String errorMsg = StrUtil.format("商户编码: {}, 支付宝支付代付订单提交失败: status={}, msg={}",
                    withdrawOrder.getMerchantCode(), status, msg);
            errorTelegramUtil.dealErrorMsg(errorMsg);
            throw new MyPayException(msg != null ? msg : "代付订单提交失败");
        }

        return withdrawResultDo;
    }

    @Override
    public JSONObject callbackWithdrawParam(MerchantPay merchantPay, OrderLawWithdraw orderLawWithdraw) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_trade_no", orderLawWithdraw.getOrderNo());
        return jsonObject;
    }

    @Override
    public JSONObject getNotifyRequestParam(HttpServletRequest request) {
        Map<String, String> map = RequestUntil.doParamCompatible(request);
        return JSONObject.from(map);
    }


    @Override
    public PayCallbackResult callbackPay(MerchantPay merchantPay, JSONObject jsonObject) {
        try {
            String orderNo = jsonObject.getString("orderid");
            if (StrUtil.isBlank(orderNo)) {
                log.error("支付宝支付充值回调 - 订单号为空, params: {}", jsonObject);
                return PayCallbackResult.failed(getFailResponse());
            }

            String payParam = merchantPay.getParamStr();
            List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
            String merchantKey = filterByApp(walletParams, "merchantKey");

            // 1. 签名验证（仅三方真实推送时有 sign，后台手动回调跳过）
            Map<String, Object> callbackParams = new TreeMap<>();
            jsonObject.forEach((key, value) -> callbackParams.put(key, value));
            boolean hasSign = StrUtil.isNotBlank((String) callbackParams.get("sign"));
            if (hasSign && !verifyAlipayQueryResponse(callbackParams, merchantKey)) {
                log.error("支付宝支付充值回调 - 签名验证失败, orderNo: {}", orderNo);
                return PayCallbackResult.failed(getFailResponse());
            }
            if (!hasSign) {
                log.info("支付宝支付充值回调 - 无签名字段，跳过签名校验（后台手动回调），orderNo: {}", orderNo);
            }

            // 2. 主动查单验证
            OrderInfoDo.OrderInfo orderInfo;
            try {
                QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
                queryOrderInfo.setOrderNo(orderNo);
                queryOrderInfo.setPayParam(payParam);
                OrderInfoDo orderInfoDo = queryOrder(queryOrderInfo);
                if (orderInfoDo == null || orderInfoDo.getData() == null) {
                    log.error("支付宝支付充值回调查单 - 查单返回为空, orderNo: {}", orderNo);
                    return PayCallbackResult.failed(getFailResponse());
                }
                orderInfo = orderInfoDo.getData();
            } catch (Exception e) {
                log.error("支付宝支付充值回调查单异常, orderNo: {}", orderNo, e);
                return PayCallbackResult.failed(getFailResponse());
            }

            // 3. 订单状态校验
            if (orderInfo.getOrderStatus() != 1) {
                log.error("支付宝支付充值回调查单 - 订单未完成, orderNo: {}, status: {}", orderNo, orderInfo.getOrderStatus());
                return PayCallbackResult.failed(getFailResponse());
            }

            // 4. 回调金额与查单金额比对
            BigDecimal callbackAmount = jsonObject.getBigDecimal("amount");
            BigDecimal queryAmount = orderInfo.getAmount();
            if (callbackAmount != null && queryAmount != null
                    && callbackAmount.compareTo(queryAmount) != 0) {
                log.error("支付宝支付充值回调 - 金额不匹配, orderNo: {}, callbackAmount: {}, queryAmount: {}",
                        orderNo, callbackAmount, queryAmount);
                return PayCallbackResult.failed(getFailResponse());
            }

            // 5. 币种校验
            if (orderInfo.getCurrencyId() != null
                    && !PayConst.currency_id.equals(orderInfo.getCurrencyId())) {
                log.error("支付宝支付充值回调 - 币种不匹配, orderNo: {}, currencyId: {}", orderNo, orderInfo.getCurrencyId());
                return PayCallbackResult.failed(getFailResponse());
            }

            // 6. 回传三方单号和实际金额
            log.info("支付宝支付充值回调 - 订单验证成功: {}", orderNo);
            return PayCallbackResult.builder()
                    .orderNo(orderNo)
                    .merchantOrderNo(orderInfo.getMerchantOrderNo())
                    .amount(queryAmount != null ? queryAmount : callbackAmount)
                    .orderStatus(1)
                    .responseMsg(getSuccessResponse())
                    .isSuccess(true)
                    .build();
        } catch (Exception e) {
            log.error("支付宝支付充值回调异常", e);
            return PayCallbackResult.failed(getFailResponse());
        }
    }

    @Override
    public PayCallbackResult callbackWithdraw(MerchantPay merchantPay, JSONObject jsonObject) {
        try {
            String orderNo = jsonObject.getString("out_trade_no");
            String payParam = merchantPay.getParamStr();
            // 主动查单
            try {
                QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
                queryOrderInfo.setOrderNo(orderNo);
                queryOrderInfo.setPayParam(payParam);
                OrderInfoDo orderInfoDo = queryWithdrawOrder(queryOrderInfo);
                if (orderInfoDo != null && orderInfoDo.getData() != null) {
                    if (orderInfoDo.getData().getOrderStatus() == 1) {
                        return PayCallbackResult.success(orderNo, getSuccessResponse());
                    } else if (orderInfoDo.getData().getOrderStatus() == 2) {
                        return PayCallbackResult.builder().orderNo(orderNo).orderStatus(2)
                                .responseMsg(getSuccessResponse())
                                .isSuccess(true).build();
                    }
                }
                return PayCallbackResult.builder().orderNo(orderNo).orderStatus(0).responseMsg(getSuccessResponse())
                        .isSuccess(false).build();
            } catch (Exception e) {
                log.error("支付宝支付提现回调查单异常", e);
                return PayCallbackResult.failed(getFailResponse());
            }
        } catch (Exception e) {
            log.error("支付宝支付提现回调异常", e);
            return PayCallbackResult.failed(getFailResponse());
        }
    }

    @Override
    public JSONObject reverseCheckOrderWithdraw(MerchantPay merchantPay, Map<String, Object> requestMap) {
        return null;
    }

    /**
     * 支付宝支付 签名算法
     * 1. 将所有参与签名的字段按ASCII码排序
     * 2. 按照key1=value1&key2=value2进行组合
     * 3. 最后加上商户密钥（&key=商户密钥）
     * 4. 进行md5运算，结果转为大写
     *
     * @param params 参与签名的请求参数（不包含sign、pay_productname、pay_ip）
     * @param key    商户密钥
     * @return 签名字符串（大写）
     */
    public static String signAlipay(Map<String, Object> params, String key) {
        // 过滤空值，并按key排序（TreeMap已经排序）
        String signStr = params.entrySet().stream()
                .filter(entry -> {
                    Object value = entry.getValue();
                    if (value == null)
                        return false;
                    if (value instanceof String && StrUtil.isBlank((String) value))
                        return false;
                    return true;
                })
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        // 拼接商户密钥
        signStr = signStr + "&key=" + key;

        log.info("支付宝支付签名字符串: {}", signStr);
        return MD5Util.getStr(signStr).toUpperCase();
    }

    /**
     * 支付宝支付 订单查询响应验签
     * 验证订单查询响应的签名
     * 响应参数：memberid, orderid, amount, time_end, transaction_id, returncode,
     * trade_state（参与签名）, sign（不参与签名）
     *
     * @param params 响应参数（包含sign）
     * @param key    商户密钥
     * @return 验证结果
     */
    public static boolean verifyAlipayQueryResponse(Map<String, Object> params, String key) {
        String receivedSign = (String) params.get("sign");
        if (StrUtil.isBlank(receivedSign)) {
            return false;
        }

        // 构建参与签名的参数（排除sign）
        Map<String, Object> signParams = new TreeMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String keyName = entry.getKey();
            // 排除sign等不参与签名的字段
            if (!"sign".equals(keyName)) {
                signParams.put(keyName, entry.getValue());
            }
        }

        // 计算签名
        String calculatedSign = signAlipay(signParams, key);

        log.debug("支付宝支付订单查询响应验签 - 接收签名: {}, 计算签名: {}", receivedSign, calculatedSign);
        return receivedSign.equalsIgnoreCase(calculatedSign);
    }

    /**
     * 支付宝支付 代付订单查询响应验签
     * 验证代付订单查询响应的签名
     * 响应参数：status, msg, mchid, out_trade_no, amount, transaction_id, refCode,
     * refMsg, success_time（参与签名）, payment_md5sign（不参与签名）
     *
     * @param params 响应参数（包含payment_md5sign）
     * @param key    商户密钥
     * @return 验证结果
     */
    public static boolean verifyAlipayWithdrawQueryResponse(Map<String, Object> params, String key) {
        String receivedSign = (String) params.get("payment_md5sign");
        if (StrUtil.isBlank(receivedSign)) {
            return false;
        }

        // 构建参与签名的参数（排除payment_md5sign）
        Map<String, Object> signParams = new TreeMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String keyName = entry.getKey();
            // 排除payment_md5sign等不参与签名的字段
            if (!"payment_md5sign".equals(keyName)) {
                Object value = entry.getValue();
                // 只有非空值才参与签名
                if (value != null && !(value instanceof String && StrUtil.isBlank((String) value))) {
                    signParams.put(keyName, value);
                }
            }
        }

        // 计算签名
        String calculatedSign = signAlipay(signParams, key);

        log.debug("支付宝支付代付订单查询响应验签 - 接收签名: {}, 计算签名: {}", receivedSign, calculatedSign);
        return receivedSign.equalsIgnoreCase(calculatedSign);
    }

    /**
     * 支付宝支付 POST form-urlencoded请求
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 返回结果
     */
    public static JSONObject doPostFormRequest(String url, Map<String, Object> params) {
        return PayWService.doPostFormJson(url, params, 5000, "码云支付");
    }

}
