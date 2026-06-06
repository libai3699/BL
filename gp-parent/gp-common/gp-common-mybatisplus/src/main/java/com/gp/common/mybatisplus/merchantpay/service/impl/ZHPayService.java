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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
public class ZHPayService extends PayWService {

    @Resource
    private ErrorTelegramUtil errorTelegramUtil;

    public static String rechargeUrl = "/payApi/PayApi/CreateOrder";
    public static String rechargeQueryUrl = "/payApi/PayApi/QueryOrder";
    public static String withdrawUrl = "/payApi/PayApi/CreatePayOutOrder";
    public static String withdrawQueryUrl = "/payApi/PayApi/QueryOrder";

    public static int codeSuccess = 200;
    public static int tradeStatus = 2;

    @Override
    protected void doRegister() {
        registerPayWService(CollUtil.newArrayList(
                PayMerchantCons.ZH_PAY_8005,
                PayMerchantCons.ZH_PAY_8006,
                PayMerchantCons.ZH_PAY_8007,
                PayMerchantCons.ZH_PAY_4
        ));
    }

    @Override
    public String queryMoney(MerchantPay merchantPay) {
        return "暂不支持";
    }

    // ==================== 签名 ====================

    public static String sign(JSONObject jsonObject, String secret) {
        TreeMap<String, Object> sortedMap = new TreeMap<>();
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            Object value = entry.getValue();
            if (value != null && StrUtil.isNotEmpty(value.toString())) {
                sortedMap.put(entry.getKey(), value);
            }
        }

        StringBuilder signStr = new StringBuilder();
        for (Map.Entry<String, Object> entry : sortedMap.entrySet()) {
            if (signStr.length() > 0) {
                signStr.append("&");
            }
            signStr.append(entry.getKey()).append("=").append(entry.getValue());
        }
        signStr.append("&key=").append(secret);

        log.info("ZH签名字符串: {}", signStr.toString());
        return MD5Util.getStr(signStr.toString()).toLowerCase();
    }

    // ==================== 回调参数解析 ====================

    @Override
    public JSONObject getNotifyRequestParam(HttpServletRequest request) {
        String jsonStr = "";
        try {
            Map<String, String> paramMap = RequestUntil.doParamCompatible(request);
            if (paramMap.isEmpty()) {
                jsonStr = RequestUntil.readRequestBody(request);
            } else {
                jsonStr = JSON.toJSONString(paramMap);
            }
        } catch (Exception e) {
            log.error("ZH Notify param error", e);
        }
        return JSON.parseObject(jsonStr);
    }

    // ==================== 充值回调 ====================

    @Override
    public PayCallbackResult callbackPay(MerchantPay merchantPay, JSONObject notifyData) {
        try {
            log.info("ZH 充值回调数据: {}", notifyData.toJSONString());

            String payParam = merchantPay.getParamStr();
            List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
            String secret = filterByApp(walletParams, "secret");
            String domainName = filterByApp(walletParams, "domainName");
            String tradeNo = filterByApp(walletParams, "tradeNo");

            String orderNo = notifyData.getString("order_no");

            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("order_type", "pay_in");
            queryParams.put("trade_no", Integer.parseInt(tradeNo));
            queryParams.put("order_no", orderNo);

            JSONObject queryJson = new JSONObject(queryParams);
            String signStr = sign(queryJson, secret);
            queryParams.put("sign", signStr);

            JSONObject queryResult = doRequest(domainName + rechargeQueryUrl, new JSONObject(queryParams), JSONObject.class);
            log.info("ZH 充值查单结果: {}", queryResult.toJSONString());

            if (codeSuccess == queryResult.getIntValue("code") && tradeStatus == queryResult.getIntValue("status")) {
                String mchOrderNo = queryResult.getString("order_no");
                return PayCallbackResult.builder()
                        .orderNo(mchOrderNo)
                        .merchantOrderNo(queryResult.getString("dis_order_no"))
                        .orderStatus(1)
                        .responseMsg("success")
                        .isSuccess(true)
                        .build();
            }

            return PayCallbackResult.failed("fail");
        } catch (Exception e) {
            log.error("ZH 充值回调异常", e);
            return PayCallbackResult.failed("fail");
        }
    }

    @Override
    public JSONObject callbackPayParam(MerchantPay merchantPay, OrderAmount orderAmount) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("order_no", orderAmount.getOrderNo());
        return jsonObject;
    }

    // ==================== 充值查单 ====================

    @Override
    public OrderInfoDo queryOrder(QueryOrderInfo queryOrderInfo) {
        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
        orderInfo.setCurrencyId(PayConst.currency_id);
        orderInfo.setOrderStatus(1);
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    // ==================== 创建充值订单 ====================

    @SneakyThrows
    @Override
    public WalletCreateOrderResult createOrder(CreateOrder createOrder) {
        String payParam = createOrder.getPayParam();
        BigDecimal amount = createOrder.getAmount().multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.DOWN);

        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String tradeNo = filterByApp(walletParams, "tradeNo");
        String appId = filterByApp(walletParams, "appId");
        String secret = filterByApp(walletParams, "secret");
        String payCode = filterByApp(walletParams, "payCode");
        String rechargeNotifyUrl = filterByApp(walletParams, "rechargeNotifyUrl");

        Map<String, Object> params = new HashMap<>();
        params.put("trade_no", Integer.parseInt(tradeNo));
        params.put("app_id", Integer.parseInt(appId));
        params.put("pay_code", Integer.parseInt(payCode));
        params.put("price", amount.intValue());
        params.put("order_no", createOrder.getOrderNo());
        params.put("pay_notice_url", rechargeNotifyUrl);
        params.put("user_id", createOrder.getOrderNo());

        JSONObject paramJson = new JSONObject(params);
        String signStr = sign(paramJson, secret);
        params.put("sign", signStr);

        JSONObject resultJsonObject = doRequest(domainName + rechargeUrl, new JSONObject(params), JSONObject.class);
        Integer code = resultJsonObject.getInteger("code");

        if (code != null && code.equals(codeSuccess)) {
            WalletCreateOrderResult result = new WalletCreateOrderResult();
            result.setUrl(resultJsonObject.getString("pay_url"));
            result.setUpInfo(resultJsonObject.toJSONString());
            return result;
        } else {
            String msg = StrUtil.format("ZH 充值下单异常: {}", resultJsonObject.toJSONString());
            log.error(msg);
            errorTelegramUtil.dealErrorMsg(msg);
            throw new MyPayException(MessagesUtils.get("bot.pay.ZFYC"));
        }
    }

    // ==================== 代付 ====================

    @SneakyThrows
    @Override
    public WithdrawResultDo withdraw(WithdrawOrder withdrawOrder) {
        String payParam = withdrawOrder.getPayParam();
        BigDecimal amount = withdrawOrder.getAmount().multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.DOWN);

        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String tradeNo = filterByApp(walletParams, "tradeNo");
        String appId = filterByApp(walletParams, "appId");
        String secret = filterByApp(walletParams, "secret");
        String outCode = filterByApp(walletParams, "outCode");
        String withdrawNotifyUrl = filterByApp(walletParams, "withdrawNotifyUrl");

        Map<String, Object> wMap = withdrawOrder.getWithdrawParam();
        String accountNo = wMap != null ? String.valueOf(wMap.getOrDefault(PayExtDto.accountNo, "")) : "";
        String accountName = wMap != null ? String.valueOf(wMap.getOrDefault(PayExtDto.accountName, "")) : "";
        String bankCode = wMap != null ? String.valueOf(wMap.getOrDefault(PayExtDto.bankCode, "")) : "";

        Map<String, Object> params = new HashMap<>();
        params.put("trade_no", Integer.parseInt(tradeNo));
        params.put("order_no", withdrawOrder.getOrderNo());
        params.put("app_id", Integer.parseInt(appId));
        params.put("pay_code", Integer.parseInt(outCode));
        params.put("price", amount.intValue());
        params.put("account_no", accountNo);
        params.put("account_type", "BANK");
        params.put("account_name", accountName);
        params.put("bank_code", bankCode);
        params.put("pay_notice_url", withdrawNotifyUrl);

        JSONObject requestJson = new JSONObject(params);
        String signStr = sign(requestJson, secret);
        params.put("sign", signStr);

        JSONObject resultJsonObject = doRequest(domainName + withdrawUrl, new JSONObject(params), JSONObject.class);
        Integer code = resultJsonObject.getInteger("code");

        if (code != null && code.equals(codeSuccess)) {
            WithdrawResultDo withdrawResultDo = new WithdrawResultDo();
            WithdrawResultDo.Result result = new WithdrawResultDo.Result();
            result.setUpInfo(resultJsonObject.toJSONString());
            result.setCurrencyId(PayConst.currency_id);
            result.setUpOrderNo(resultJsonObject.getString("dis_order_no"));
            withdrawResultDo.setData(result);
            return withdrawResultDo;
        } else {
            String msg = StrUtil.format("ZH 代付异常: {}", resultJsonObject.toJSONString());
            log.error(msg);
            errorTelegramUtil.dealErrorMsg(msg);
            throw new MyPayException(MessagesUtils.get("bot.pay.ZFYC"), resultJsonObject.toJSONString());
        }
    }

    // ==================== 代付查单/回调 ====================

    @Override
    public OrderInfoDo queryWithdrawOrder(QueryOrderInfo queryOrderInfo) {
        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
        orderInfo.setCurrencyId(PayConst.currency_id);
        orderInfo.setOrderStatus(1);
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    @Override
    public PayCallbackResult callbackWithdraw(MerchantPay merchantPay, JSONObject notifyData) {
        try {
            log.info("ZH 代付回调数据: {}", notifyData.toJSONString());

            String payParam = merchantPay.getParamStr();
            List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
            String secret = filterByApp(walletParams, "secret");
            String domainName = filterByApp(walletParams, "domainName");
            String tradeNo = filterByApp(walletParams, "tradeNo");

            String orderNo = notifyData.getString("order_no");

            Map<String, Object> queryParams = new HashMap<>();
            queryParams.put("order_type", "pay_out");
            queryParams.put("trade_no", Integer.parseInt(tradeNo));
            queryParams.put("order_no", orderNo);

            JSONObject queryJson = new JSONObject(queryParams);
            String signStr = sign(queryJson, secret);
            queryParams.put("sign", signStr);

            JSONObject queryResult = doRequest(domainName + withdrawQueryUrl, new JSONObject(queryParams), JSONObject.class);
            log.info("ZH 代付查单结果: {}", queryResult.toJSONString());

            if (codeSuccess == queryResult.getIntValue("code")) {
                String mchOrderNo = queryResult.getString("order_no");
                String upOrderNo = queryResult.getString("dis_order_no");
                Integer status = queryResult.getInteger("status");

                // status=2 成功, 其他为失败
                int orderStatus = (tradeStatus == status) ? 1 : 2;

                return PayCallbackResult.builder()
                        .orderNo(mchOrderNo)
                        .merchantOrderNo(upOrderNo)
                        .orderStatus(orderStatus)
                        .responseMsg("success")
                        .isSuccess(true)
                        .build();
            }

            return PayCallbackResult.failed("fail");
        } catch (Exception e) {
            log.error("ZH 代付回调异常", e);
            return PayCallbackResult.failed("fail");
        }
    }

    @Override
    public JSONObject callbackWithdrawParam(MerchantPay merchantPay, OrderLawWithdraw orderLawWithdraw) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("order_no", orderLawWithdraw.getOrderNo());
        return jsonObject;
    }

    @Override
    public JSONObject reverseCheckOrderWithdraw(MerchantPay merchantPay, Map<String, Object> requestMap) {
        return null;
    }
}
