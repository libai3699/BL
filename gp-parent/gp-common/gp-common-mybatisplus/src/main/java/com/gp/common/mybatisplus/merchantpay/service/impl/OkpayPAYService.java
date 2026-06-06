package com.gp.common.mybatisplus.merchantpay.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.common.core.log.ErrorTelegramUtil;
import com.gp.common.base.exception.MyPayException;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.mybatisplus.entity.MerchantPay;
import com.gp.common.mybatisplus.entity.OrderAmount;
import com.gp.common.mybatisplus.entity.OrderLawWithdraw;
import com.gp.common.mybatisplus.merchantpay.constants.PayMerchantCons;
import com.gp.common.mybatisplus.merchantpay.service.PayWService;
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
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OkpayPAYService extends PayWService {

    @Resource
    private ErrorTelegramUtil errorTelegramUtil;

    @Override
    protected void doRegister() {
        registerPayWService(CollUtil.newArrayList(PayMerchantCons.OKPAY));
    }

    @Override
    public String queryMoney(MerchantPay merchantPay) {
        return "暂不支持";
    }

    // ==================== 签名 ====================

    public static String signOkpay(Map<String, Object> params, String merchantId, String token) {
        Map<String, Object> signMap = new TreeMap<>(params);
        signMap.put("id", merchantId);

        String signStr = signMap.entrySet().stream()
                .filter(entry -> entry.getValue() != null
                        && StrUtil.isNotBlank(entry.getValue().toString())
                        && !"0".equals(entry.getValue().toString()))
                .map(entry -> entry.getKey() + "=" + entry.getValue().toString())
                .collect(Collectors.joining("&"));

        signStr += "&token=" + token;
        log.info("Okpay 签名待签字符串: {}", signStr);
        return DigestUtil.md5Hex(signStr).toUpperCase();
    }

    public boolean verifyNotifySign(Map<String, Object> params, String payParam) {
        String receivedSign = (String) params.get("sign");
        if (StrUtil.isBlank(receivedSign)) return false;

        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String token = filterByApp(walletParams, "merchantKey");

        TreeMap<String, Object> sorted = new TreeMap<>(params);
        sorted.remove("sign");

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : sorted.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            JSONObject nested = null;
            if (value instanceof JSONObject) {
                nested = (JSONObject) value;
            } else if (value instanceof String && ((String) value).startsWith("{")) {
                try {
                    nested = JSON.parseObject((String) value);
                } catch (Exception ignored) {
                }
            }

            if (nested != null) {
                for (String subKey : nested.keySet()) {
                    Object subVal = nested.get(subKey);
                    String strVal = subVal != null ? String.valueOf(subVal) : "";
                    if (StrUtil.isBlank(strVal)) continue;
                    if (sb.length() > 0) sb.append("&");
                    sb.append(key).append("[").append(subKey).append("]=").append(strVal);
                }
            } else {
                String strVal = value != null ? String.valueOf(value) : "";
                if (StrUtil.isBlank(strVal)) continue;
                if (sb.length() > 0) sb.append("&");
                sb.append(key).append("=").append(strVal);
            }
        }
        sb.append("&token=").append(token);

        String calculatedSign = DigestUtil.md5Hex(sb.toString()).toUpperCase();
        log.info("Okpay 计算签名: {}, 接收签名: {}", calculatedSign, receivedSign);
        return receivedSign.equalsIgnoreCase(calculatedSign);
    }

    // ==================== 回调参数解析 ====================

    @Override
    public JSONObject getNotifyRequestParam(HttpServletRequest request) {
        try {
            String body = RequestUntil.readRequestBody(request);
            log.info("Okpay 回调原始Body: {}", body);
            if (StrUtil.isNotBlank(body)) {
                JSONObject result = JSON.parseObject(body);
                if (result != null && !result.isEmpty()) {
                    return result;
                }
            }
            Map<String, String> paramMap = RequestUntil.doParamCompatible(request);
            if (!paramMap.isEmpty()) {
                return JSON.parseObject(JSON.toJSONString(paramMap));
            }
        } catch (Exception e) {
            log.error("OkpayNotify param error", e);
        }
        return new JSONObject();
    }

    // ==================== 充值回调 ====================

    @Override
    public PayCallbackResult callbackPay(MerchantPay merchantPay, JSONObject notifyData) {
        try {
            log.info("Okpay 充值回调数据: {}", notifyData.toJSONString());

            boolean isCheckStatus = !notifyData.containsKey("sign");

            if (!isCheckStatus && !verifyNotifySign(notifyData, merchantPay.getParamStr())) {
                log.error("Okpay 充值回调签名校验失败");
                return PayCallbackResult.failed("{\"status\": \"error\"}");
            }

            String uniqueId;
            String orderId = null;

            if (isCheckStatus) {
                uniqueId = notifyData.getString("unique_id");
            } else {
                Object dataObj = notifyData.get("data");
                JSONObject dataRecord = parseDataRecord(dataObj);
                if (dataRecord == null) {
                    log.error("Okpay 充值回调业务数据为空");
                    return PayCallbackResult.failed("{\"status\": \"error\"}");
                }

                uniqueId = dataRecord.getString("unique_id");
                orderId = dataRecord.getString("order_id");
                Integer status = dataRecord.getInteger("status");
                String type = dataRecord.getString("type");

                if (!"deposit".equalsIgnoreCase(type)) {
                    log.info("Okpay 回调非充值类型: {}", type);
                    return PayCallbackResult.failed("{\"status\": \"success\"}");
                }

                if (status != null && status != 1) {
                    log.info("Okpay 回调订单未支付成功, status: {}", status);
                    return PayCallbackResult.failed("{\"status\": \"success\"}");
                }
            }

            // 主动查单二次确认
            QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
            queryOrderInfo.setOrderNo(uniqueId);
            queryOrderInfo.setPayParam(merchantPay.getParamStr());
            OrderInfoDo orderInfoDo = queryOrder(queryOrderInfo);
            if (orderInfoDo == null || orderInfoDo.getData() == null || orderInfoDo.getData().getOrderStatus() != 1) {
                log.error("Okpay 充值回调查单确认失败, uniqueId: {}", uniqueId);
                return PayCallbackResult.failed("{\"status\": \"error\"}");
            }

            if (orderId == null && orderInfoDo.getData().getMerchantOrderNo() != null) {
                orderId = orderInfoDo.getData().getMerchantOrderNo();
            }

            return PayCallbackResult.builder()
                    .orderNo(uniqueId)
                    .merchantOrderNo(orderId)
                    .orderStatus(1)
                    .responseMsg("{\"status\": \"success\"}")
                    .isSuccess(true)
                    .build();

        } catch (Exception e) {
            log.error("Okpay 充值回调异常", e);
            return PayCallbackResult.failed("{\"status\": \"error\"}");
        }
    }

    @Override
    public JSONObject callbackPayParam(MerchantPay merchantPay, OrderAmount orderAmount) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("unique_id", orderAmount.getOrderNo());
        return jsonObject;
    }

    // ==================== 充值查单 ====================

    @Override
    public OrderInfoDo queryOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);

        String domainName = filterByApp(walletParams, "domainName");
        if (StrUtil.isBlank(domainName)) domainName = "https://api.okaypay.me/shop/";
        String merchantId = filterByApp(walletParams, "merchatCode");
        String token = filterByApp(walletParams, "merchantKey");

        Map<String, Object> params = new TreeMap<>();
        params.put("unique_id", queryOrderInfo.getOrderNo());

        String sign = signOkpay(params, merchantId, token);
        params.put("id", merchantId);
        params.put("sign", sign);

        log.info("Okpay 查单请求参数: {}", JSON.toJSONString(params));
        String url = domainName.endsWith("/") ? domainName + "checkDeposit" : domainName + "/checkDeposit";
        String response = doPost(url, params);
        log.info("Okpay 查单返回结果: {}", response);

        JSONObject resultJson = JSON.parseObject(response);

        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
        orderInfo.setCurrencyId(PayConst.currency_id);

        if (resultJson != null && resultJson.containsKey("data")) {
            JSONObject dataRecord = parseDataRecord(resultJson.get("data"));
            if (dataRecord != null) {
                Integer status = dataRecord.getInteger("status");
                orderInfo.setOrderStatus(status != null && status == 1 ? 1 : 0);
                orderInfo.setMerchantOrderNo(dataRecord.getString("order_id"));
                orderInfoDo.setData(orderInfo);
                return orderInfoDo;
            }
        }

        orderInfo.setOrderStatus(0);
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    // ==================== 创建充值订单 ====================

    @SneakyThrows
    @Override
    public WalletCreateOrderResult createOrder(CreateOrder createOrder) {
        String payParam = createOrder.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);

        String domainName = filterByApp(walletParams, "domainName");
        if (StrUtil.isBlank(domainName)) domainName = "https://api.okaypay.me/shop/";
        String merchantId = filterByApp(walletParams, "merchatCode");
        String token = filterByApp(walletParams, "merchantKey");
        String callbackUrl = filterByApp(walletParams, "rechargeNotifyUrl");
        String returnUrl = filterByApp(walletParams, "returnUrl");

        String extParam = createOrder.getExtParam();
        String coin = "USDT";
        if (StrUtil.isNotBlank(extParam)) {
            JSONObject extJson = JSONObject.parseObject(extParam);
            coin = extJson.getString("coin");
            if (StrUtil.isBlank(coin)) coin = extJson.getString("type");
        }

        Map<String, Object> params = new TreeMap<>();
        params.put("unique_id", createOrder.getOrderNo());
        params.put("name", StrUtil.isNotBlank(createOrder.getRemark()) ? createOrder.getRemark()
                : "Order " + createOrder.getOrderNo());
        params.put("amount", createOrder.getOriginalAmount().setScale(2, RoundingMode.DOWN).toString());
        params.put("coin", coin != null ? coin.toUpperCase() : "USDT");
        params.put("callback_url", callbackUrl);
        if (StrUtil.isNotBlank(returnUrl)) {
            params.put("return_url", returnUrl);
        }
        params.put("status", "0");

        String sign = signOkpay(params, merchantId, token);
        params.put("id", merchantId);
        params.put("sign", sign);

        log.info("Okpay 下单请求参数: {}", JSON.toJSONString(params));
        String url = domainName.endsWith("/") ? domainName + "payLink" : domainName + "/payLink";
        String response = doPost(url, params);
        log.info("Okpay 下单返回结果: {}", response);

        JSONObject resultJson = JSON.parseObject(response);
        if (resultJson != null && resultJson.containsKey("data")) {
            JSONObject dataRecord = parseDataRecord(resultJson.get("data"));
            if (dataRecord != null && dataRecord.containsKey("pay_url")) {
                WalletCreateOrderResult result = new WalletCreateOrderResult();
                result.setUrl(dataRecord.getString("pay_url"));
                result.setUpOrderNo(dataRecord.getString("order_id"));
                result.setUpInfo(response);
                return result;
            }
        }

        String errorLog = StrUtil.format("Okpay 下单失败: {}", response);
        log.error(errorLog);
        errorTelegramUtil.dealErrorMsg(errorLog);
        throw new MyPayException(MessagesUtils.get("bot.pay.ZFYC"));
    }

    // ==================== 代付 ====================

    @SneakyThrows
    @Override
    public WithdrawResultDo withdraw(WithdrawOrder withdrawOrder) {
        String payParam = withdrawOrder.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);

        String domainName = filterByApp(walletParams, "domainName");
        if (StrUtil.isBlank(domainName)) domainName = "https://api.okaypay.me/shop/";
        String merchantId = filterByApp(walletParams, "merchatCode");
        String token = filterByApp(walletParams, "merchantKey");
        String callbackUrl = filterByApp(walletParams, "withdrawNotifyUrl");

        String tgId = null;
        if (withdrawOrder.getTgUserId() != null) {
            tgId = withdrawOrder.getTgUserId().toString();
        } else {
            Map<String, Object> wMap = withdrawOrder.getWithdrawParam();
            if (wMap != null) {
                Object tgIdObj = wMap.get("tgId");
                if (tgIdObj != null) tgId = tgIdObj.toString();
                if (StrUtil.isBlank(tgId)) {
                    Object bankObj = wMap.get("bankAccount");
                    if (bankObj != null) tgId = bankObj.toString();
                }
            }
        }

        Map<String, Object> params = new TreeMap<>();
        params.put("unique_id", withdrawOrder.getOrderNo());
        params.put("amount", withdrawOrder.getAmount().setScale(2, RoundingMode.HALF_UP).toString());
        params.put("to_user_id", tgId);
        params.put("coin", "USDT");
        params.put("name", "Withdraw " + withdrawOrder.getOrderNo());
        if (StrUtil.isNotBlank(callbackUrl)) {
            params.put("callback_url", callbackUrl);
        }
        String sign = signOkpay(params, merchantId, token);
        params.put("id", merchantId);
        params.put("sign", sign);

        log.info("Okpay 代付请求参数: {}", JSON.toJSONString(params));
        String url = domainName.endsWith("/") ? domainName + "transfer" : domainName + "/transfer";
        String response = doPost(url, params);
        log.info("Okpay 代付返回结果: {}", response);

        JSONObject resultJson = JSON.parseObject(response);
        WithdrawResultDo withdrawResultDo = new WithdrawResultDo();

        if (resultJson != null && ("success".equalsIgnoreCase(resultJson.getString("status"))
                || 10000 == resultJson.getIntValue("code"))) {
            WithdrawResultDo.Result result = new WithdrawResultDo.Result();
            result.setOrderNo(withdrawOrder.getOrderNo());
            result.setUpOrderNo(resultJson.getString("order_id"));
            result.setAmount(withdrawOrder.getAmount());
            result.setCurrencyId(PayConst.currency_id);
            result.setUpInfo(response);

            withdrawResultDo.setCode(0);
            withdrawResultDo.setData(result);
            withdrawResultDo.setMsg("成功");
        } else {
            String msg = resultJson != null ? resultJson.getString("msg") : "Unknown Error";
            log.error("Okpay 代付失败: {}", response);
            errorTelegramUtil.dealErrorMsg("Okpay 代付失败: " + response);
            throw new MyPayException(msg);
        }
        return withdrawResultDo;
    }

    // ==================== 代付查单/回调 ====================

    @Override
    public OrderInfoDo queryWithdrawOrder(QueryOrderInfo queryOrderInfo) {
        return queryOrder(queryOrderInfo);
    }

    @Override
    public PayCallbackResult callbackWithdraw(MerchantPay merchantPay, JSONObject notifyData) {
        try {
            log.info("Okpay 代付回调数据: {}", notifyData.toJSONString());

            if (!verifyNotifySign(notifyData, merchantPay.getParamStr())) {
                log.error("Okpay 代付回调签名校验失败");
                return PayCallbackResult.failed("{\"status\": \"error\"}");
            }

            Object dataObj = notifyData.get("data");
            JSONObject dataRecord = parseDataRecord(dataObj);
            if (dataRecord == null) {
                log.error("Okpay 代付回调业务数据为空");
                return PayCallbackResult.failed("{\"status\": \"error\"}");
            }

            String uniqueId = dataRecord.getString("unique_id");
            String orderId = dataRecord.getString("order_id");
            Integer status = dataRecord.getInteger("status");

            int orderStatus = (status != null && status == 1) ? 1 : 0;

            return PayCallbackResult.builder()
                    .orderNo(uniqueId)
                    .merchantOrderNo(orderId)
                    .orderStatus(orderStatus)
                    .responseMsg("{\"status\": \"success\"}")
                    .isSuccess(true)
                    .build();

        } catch (Exception e) {
            log.error("Okpay 代付回调异常", e);
            return PayCallbackResult.failed("{\"status\": \"error\"}");
        }
    }

    @Override
    public JSONObject callbackWithdrawParam(MerchantPay merchantPay, OrderLawWithdraw orderLawWithdraw) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("unique_id", orderLawWithdraw.getOrderNo());
        return jsonObject;
    }

    @Override
    public JSONObject reverseCheckOrderWithdraw(MerchantPay merchantPay, Map<String, Object> requestMap) {
        return null;
    }

    // ==================== 工具方法 ====================

    private JSONObject parseDataRecord(Object dataObj) {
        if (dataObj instanceof String) {
            String dataStr = (String) dataObj;
            if (dataStr.startsWith("[")) {
                JSONArray arr = JSON.parseArray(dataStr);
                return arr != null && !arr.isEmpty() ? arr.getJSONObject(0) : null;
            } else {
                return JSON.parseObject(dataStr);
            }
        } else if (dataObj instanceof JSONObject) {
            return (JSONObject) dataObj;
        } else if (dataObj instanceof JSONArray) {
            JSONArray arr = (JSONArray) dataObj;
            return !arr.isEmpty() ? arr.getJSONObject(0) : null;
        }
        return null;
    }

    private String doPost(String url, Map<String, Object> params) {
        // 注: 异常类型从 MyPayException 改为 RuntimeException(由 PayWService 统一抛出),
        // 与 UpstreamErrorClassifier 配合识别 uncertain 网络异常.
        return PayWService.doPostForm(url, params, 20000, "Okpay");
    }
}
