package com.gp.common.mybatisplus.merchantpay.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.common.core.log.ErrorTelegramUtil;
import com.gp.common.base.exception.MyPayException;
import com.gp.common.base.utils.MD5Util;
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
import com.gp.common.mybatisplus.pay.mpay.createAddress.CreateAddress;
import com.gp.common.mybatisplus.pay.mpay.order.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * YeePay 支付服务（越南）
 * 支持代收（充值）和代付（提现）
 *
 * @author axing
 */
@Slf4j
@Service
public class YeepayService extends PayWService {

    @Resource
    private ErrorTelegramUtil errorTelegramUtil;

    // code成功码 - 1代表成功
    public static Integer codeSuccess = 1;

    // 支付下单（代收）
    public static String payUrl = "/Index";
    // 支付订单查询
    public static String queryPayUrl = "/Look/pay_order";
    // 代付下单
    public static String withdrawUrl = "/payment";
    // 代付订单查询
    public static String queryWithdrawUrl = "/Look/payment_order";

    @Override
    protected void doRegister() {
        registerPayWService(CollUtil.newArrayList(
                PayMerchantCons.YeePay
        ));
    }

    @Override
    public JSONObject getNotifyRequestParam(HttpServletRequest request) {
        try {
            // YeePay 回调使用 JSON 格式
            String body = cn.hutool.core.io.IoUtil.readUtf8(request.getInputStream());
            if (StrUtil.isNotBlank(body)) {
                return JSON.parseObject(body);
            }
        } catch (Exception e) {
            log.error("YeePay 获取回调请求参数异常", e);
        }
        return new JSONObject();
    }

    @Override
    public String queryMoney(MerchantPay merchantPay) {
        return "暂不支持";
    }

    /**
     * YeePay 充值查单
     */
    @Override
    public OrderInfoDo queryOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String merchantKey = filterByApp(walletParams, "merchantKey");
        String merchantNum = filterByApp(walletParams, "merchantNum");

        String merchantOrder = queryOrderInfo.getOrderNo();
        String findDate = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Map<String, Object> params = new HashMap<>();
        params.put("merchant_num", merchantNum);
        params.put("merchant_order", merchantOrder);
        params.put("find_date", findDate);

        String sign = signVietnam(params, merchantKey);
        params.put("sign", sign);

        log.info("YeePay 充值查单请求参数: {}", JSON.toJSONString(params));

        String baseUrl = domainName.endsWith("/") ? domainName.substring(0, domainName.length() - 1) : domainName;
        JSONObject result = doYeePostFormRequest(baseUrl + queryPayUrl, params);
        log.info("YeePay 充值查单返回: {}", result.toJSONString());

        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
        orderInfo.setCurrencyId(PayConst.currency_id);

        Integer statusSys = 0;
        Integer code = result.getInteger("code");
        if (codeSuccess.equals(code)) {
            JSONObject data = result.getJSONObject("data");
            if (data != null) {
                Integer state = data.getInteger("state");
                // state: 1=成功, 2=待支付
                if (state != null && state == 1) {
                    statusSys = 1;
                }
            }
        }

        orderInfo.setOrderStatus(statusSys);
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    /**
     * YeePay 提现查单
     */
    @Override
    public OrderInfoDo queryWithdrawOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String merchantKey = filterByApp(walletParams, "withdrawMerchantKey");
        if (StrUtil.isBlank(merchantKey)) {
            merchantKey = filterByApp(walletParams, "merchantKey");
        }
        String merchantNum = filterByApp(walletParams, "merchantNum");

        String merchantOrder = queryOrderInfo.getOrderNo();
        String findDate = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Map<String, Object> params = new HashMap<>();
        params.put("merchant_num", merchantNum);
        params.put("merchant_order", merchantOrder);
        params.put("find_date", findDate);

        String sign = signVietnam(params, merchantKey);
        params.put("sign", sign);

        log.info("YeePay 提现查单请求参数: {}", JSON.toJSONString(params));

        String baseUrl = domainName.endsWith("/") ? domainName.substring(0, domainName.length() - 1) : domainName;
        JSONObject result = doYeePostFormRequest(baseUrl + queryWithdrawUrl, params);
        log.info("YeePay 提现查单返回: {}", result.toJSONString());

        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
        orderInfo.setCurrencyId(PayConst.currency_id);

        Integer statusSys = 0;
        Integer code = result.getInteger("code");
        if (codeSuccess.equals(code)) {
            JSONObject data = result.getJSONObject("data");
            if (data != null) {
                Integer state = data.getInteger("state");
                // state: 1=成功, 2=驳回, 3=处理中
                if (state != null && state == 1) {
                    statusSys = 1;
                } else if (state != null && state == 2) {
                    statusSys = 2;
                }
            }
        }

        orderInfo.setOrderStatus(statusSys);
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    /**
     * YeePay 创建充值订单
     */
    @SneakyThrows
    @Override
    public WalletCreateOrderResult createOrder(CreateOrder createOrder) {
        String payParam = createOrder.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String merchantKey = filterByApp(walletParams, "merchantKey");
        String uid = filterByApp(walletParams, "uid");
        String merchantNum = filterByApp(walletParams, "merchantNum");
        String rechargeNotifyUrl = filterByApp(walletParams, "rechargeNotifyUrl");
        String callbackUrl = filterByApp(walletParams, "callbackUrl");

        String orderNo = createOrder.getOrderNo();
        String amount = createOrder.getOriginalAmount().setScale(0, RoundingMode.HALF_UP).toString(); // 原始币种金额（越南盾无小数）
        String payDate = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String userInfo = createOrder.getUserId() != null ? createOrder.getUserId().toString() : "USER";

        Map<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        params.put("merchant_num", merchantNum);
        params.put("merchant_order", orderNo);
        params.put("coin", amount);
        params.put("pay_notifyurl", rechargeNotifyUrl != null ? rechargeNotifyUrl : "");
        params.put("pay_callbackurl", callbackUrl != null ? callbackUrl : "");
        params.put("pay_date", payDate);
        params.put("extend", "");
        params.put("pay_type", 7);
        params.put("userinfo", userInfo);

        String sign = signVietnam(params, merchantKey);
        params.put("sign", sign);

        log.info("YeePay 创建充值订单请求参数: {}", JSON.toJSONString(params));

        String baseUrl = domainName.endsWith("/") ? domainName.substring(0, domainName.length() - 1) : domainName;
        JSONObject resultJson = doYeePostFormRequest(baseUrl + payUrl, params);
        log.info("YeePay 创建充值订单返回: {}", resultJson.toJSONString());

        Integer code = resultJson.getInteger("code");
        if (codeSuccess.equals(code)) {
            String payUrlValue = resultJson.getString("payurl");
            if (StrUtil.isNotBlank(payUrlValue)) {
                WalletCreateOrderResult result = new WalletCreateOrderResult();
                result.setUrl(payUrlValue);
                return result;
            }
            throw new MyPayException("YeePay 下单未获取到支付链接");
        } else {
            String msg = resultJson.getString("message");
            String errLog = StrUtil.format("商户编码: {}, YeePay 充值下单异常: code={}, message={}",
                    createOrder.getPayChannelCode(), code, msg);
            errorTelegramUtil.dealErrorMsg(errLog);
            throw new MyPayException(StrUtil.isNotBlank(msg) ? msg : MessagesUtils.get("bot.pay.ZFYC"));
        }
    }

    @Override
    public JSONObject callbackPayParam(MerchantPay merchantPay, OrderAmount orderAmount) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("merchant_order", orderAmount.getOrderNo());
        return jsonObject;
    }

    @Override
    public PayCallbackResult callbackPay(MerchantPay merchantPay, JSONObject notifyData) {
        try {
            String orderNo = notifyData.getString("merchant_order");
            if (StrUtil.isBlank(orderNo)) {
                log.error("YeePay 充值回调 - 订单号为空");
                return PayCallbackResult.failed("orderNo is empty");
            }

            String payParam = merchantPay.getParamStr();
            List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
            String merchantKey = filterByApp(walletParams, "merchantKey");

            // 1. 验签
            String receivedSign = notifyData.getString("sign");
            Map<String, Object> callbackParams = new HashMap<>();
            notifyData.forEach(callbackParams::put);
            if (!verifyVietnamNotify(callbackParams, receivedSign, merchantKey)) {
                log.error("YeePay 充值回调 - 签名验证失败, orderNo: {}", orderNo);
                return PayCallbackResult.failed("sign error");
            }

            // 2. 主动查单复核
            QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
            queryOrderInfo.setOrderNo(orderNo);
            queryOrderInfo.setPayParam(payParam);
            OrderInfoDo orderInfoDo = queryOrder(queryOrderInfo);
            if (orderInfoDo == null || orderInfoDo.getData() == null
                    || orderInfoDo.getData().getOrderStatus() != 1) {
                log.error("YeePay 充值回调查单 - 订单未成功, orderNo: {}", orderNo);
                return PayCallbackResult.failed("order not paid");
            }

            log.info("YeePay 充值回调 - 处理成功: {}", orderNo);
            return PayCallbackResult.builder()
                    .orderNo(orderNo)
                    .merchantOrderNo(notifyData.getString("transaction_id"))
                    .orderStatus(1)
                    .responseMsg("SUCCESS")
                    .isSuccess(true)
                    .build();
        } catch (Exception e) {
            log.error("YeePay 充值回调异常", e);
            return PayCallbackResult.failed("exception");
        }
    }

    /**
     * YeePay 创建代付订单（提现）
     */
    @SneakyThrows
    @Override
    public WithdrawResultDo withdraw(WithdrawOrder withdrawOrder) {
        String payParam = withdrawOrder.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String merchantKey = filterByApp(walletParams, "withdrawMerchantKey");
        if (StrUtil.isBlank(merchantKey)) {
            merchantKey = filterByApp(walletParams, "merchantKey");
        }
        String uid = filterByApp(walletParams, "uid");
        String merchantNum = filterByApp(walletParams, "merchantNum");
        String withdrawNotifyUrl = filterByApp(walletParams, "withdrawNotifyUrl");

        Map<String, Object> bindData = withdrawOrder.getBindData();
        if (bindData == null || bindData.isEmpty()) {
            bindData = withdrawOrder.getWithdrawParam();
        }

        String targetBank = PayBindDataKeyEnum.ACCOUNT.getValue(bindData);
        String bankName = PayBindDataKeyEnum.BANK_CODE.getValue(bindData);
        if (StrUtil.isBlank(bankName)) {
            bankName = PayBindDataKeyEnum.BANK_BRANCH.getValue(bindData);
        }
        String targetBankUser = PayBindDataKeyEnum.PLAYER_NAME.getValue(bindData);

        if (StrUtil.isBlank(targetBank) || StrUtil.isBlank(bankName) || StrUtil.isBlank(targetBankUser)) {
            throw new MyPayException("YeePay 代付缺少必要参数：收款账号、银行编码或持卡人姓名");
        }

        String orderNo = withdrawOrder.getOrderNo();
        String amount = withdrawOrder.getAmount().setScale(0, RoundingMode.HALF_UP).toString();
        String orderDate = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String userInfo = withdrawOrder.getUserId() != null ? withdrawOrder.getUserId().toString() : "USER";

        Map<String, Object> params = new HashMap<>();
        params.put("uid", uid);
        params.put("merchant_num", merchantNum);
        params.put("order", orderNo);
        params.put("coin", amount);
        params.put("userinfo", userInfo);
        params.put("target_bank", targetBank);
        params.put("bank_name", bankName);
        params.put("target_bank_user", targetBankUser);
        params.put("extend", "");
        params.put("order_date", orderDate);
        params.put("notifyurl", withdrawNotifyUrl != null ? withdrawNotifyUrl : "");

        String sign = signVietnam(params, merchantKey);
        params.put("sign", sign);

        log.info("YeePay 创建代付订单请求参数: {}", JSON.toJSONString(params));

        String baseUrl = domainName.endsWith("/") ? domainName.substring(0, domainName.length() - 1) : domainName;
        JSONObject resultJson = doYeePostFormRequest(baseUrl + withdrawUrl, params);
        log.info("YeePay 创建代付订单返回: {}", resultJson.toJSONString());

        Integer code = resultJson.getInteger("code");
        if (codeSuccess.equals(code)) {
            WithdrawResultDo withdrawResultDo = new WithdrawResultDo();
            WithdrawResultDo.Result result = new WithdrawResultDo.Result();
            result.setOrderNo(orderNo);
            result.setUpOrderNo(resultJson.getString("serial_number"));
            result.setUpInfo(resultJson.toJSONString());
            result.setCurrencyId(PayConst.currency_id);
            result.setAmount(withdrawOrder.getAmount());
            result.setCreateTime(System.currentTimeMillis());
            withdrawResultDo.setData(result);
            withdrawResultDo.setMsg("成功");
            return withdrawResultDo;
        } else {
            String msg = resultJson.getString("message");
            String errLog = StrUtil.format("商户编码: {}, YeePay 代付异常 message={}"
                   , code, msg);
            errorTelegramUtil.dealErrorMsg(errLog);
            throw new MyPayException(StrUtil.isNotBlank(msg) ? msg : MessagesUtils.get("bot.pay.ZFYC"));
        }
    }

    @Override
    public JSONObject callbackWithdrawParam(MerchantPay merchantPay, OrderLawWithdraw orderLawWithdraw) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("merchant_order", orderLawWithdraw.getOrderNo());
        return jsonObject;
    }

    @Override
    public PayCallbackResult callbackWithdraw(MerchantPay merchantPay, JSONObject notifyData) {
        try {
            String orderNo = notifyData.getString("merchant_order");
            String payParam = merchantPay.getParamStr();
            List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
            String withdrawKey = filterByApp(walletParams, "withdrawMerchantKey");
            if (StrUtil.isBlank(withdrawKey)) {
                withdrawKey = filterByApp(walletParams, "merchantKey");
            }

            // 1. 验签（后台手动查单时sign为空，跳过验签直接查单确认）
            String receivedSign = notifyData.getString("sign");
            if (receivedSign != null) {
                Map<String, Object> callbackParams = new HashMap<>();
                notifyData.forEach(callbackParams::put);
                if (!verifyVietnamNotify(callbackParams, receivedSign, withdrawKey)) {
                    log.error("YeePay 提现回调 - 签名验证失败, orderNo: {}", orderNo);
                    return PayCallbackResult.failed("sign error");
                }
            } else {
                log.info("YeePay 提现手动回调(sign为空), 跳过验签, orderNo: {}", orderNo);
            }

            // 2. 主动查单确认
            QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
            queryOrderInfo.setOrderNo(orderNo);
            queryOrderInfo.setPayParam(payParam);
            OrderInfoDo orderInfoDo = queryWithdrawOrder(queryOrderInfo);
            if (orderInfoDo != null && orderInfoDo.getData() != null) {
                OrderInfoDo.OrderInfo info = orderInfoDo.getData();
                log.info("YeePay 提现回调查单: orderNo={}, status={}", orderNo, info.getOrderStatus());
                return PayCallbackResult.builder()
                        .orderNo(orderNo)
                        .merchantOrderNo(notifyData.getString("serial_number"))
                        .orderStatus(info.getOrderStatus())
                        .responseMsg("SUCCESS")
                        .isSuccess(true)
                        .build();
            }
            return PayCallbackResult.failed("query fail");
        } catch (Exception e) {
            log.error("YeePay 提现回调异常", e);
            return PayCallbackResult.failed("exception");
        }
    }

    @Override
    public JSONObject reverseCheckOrderWithdraw(MerchantPay merchantPay, Map<String, Object> requestMap) {
        return null;
    }

    // mpay 接口兼容（不使用）
    @Override
    public RechargeOrderInfoDo.RechargeOrderInfo queryRechargeOrder(QueryRechargeOrder queryRechargeOrder) {
        return null;
    }



    // ========================= 工具方法 =========================

    /**
     * YeePay POST form-urlencoded 请求
     */
    public static JSONObject doYeePostFormRequest(String url, Map<String, Object> params) {
        return PayWService.doPostFormJson(url, params, 10000, "YeePay");
    }

    /**
     * 越南YeePay 签名算法
     * 参数按ASCII升序排列，排除sign/空值/0值，末尾追加&key=密钥，MD5后转大写
     */
    public static String signVietnam(Map<String, Object> params, String signKey) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        TreeMap<String, Object> sortedMap = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : sortedMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if ("sign".equals(key)) continue;
            if (value == null || "".equals(value) || "0".equals(String.valueOf(value))) continue;
            if (sb.length() > 0) sb.append("&");
            sb.append(key).append("=").append(value);
        }
        sb.append("&key=").append(signKey);
        log.info("YeePay 签名字符串: {}", sb);
        return MD5Util.getStr(sb.toString()).toUpperCase();
    }

    /**
     * 回调验签
     */
    public static boolean verifyVietnamNotify(Map<String, Object> params, String receivedSign, String signKey) {
        if (StrUtil.isBlank(receivedSign)) return false;
        String calculatedSign = signVietnam(params, signKey);
        log.debug("YeePay 验签 - 接收: {}, 计算: {}", receivedSign, calculatedSign);
        return receivedSign.equalsIgnoreCase(calculatedSign);
    }
}
