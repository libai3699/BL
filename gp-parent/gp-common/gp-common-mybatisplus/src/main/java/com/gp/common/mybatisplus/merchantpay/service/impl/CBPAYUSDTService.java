package com.gp.common.mybatisplus.merchantpay.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.gp.common.base.exception.MyPayException;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.mybatisplus.entity.MerchantPay;
import com.gp.common.mybatisplus.merchantpay.constants.PayMerchantCons;
import com.gp.common.mybatisplus.merchantpay.service.PayWService;
import com.gp.common.mybatisplus.pay.constant.PayConst;
import com.gp.common.mybatisplus.pay.mpay.WalletCreateOrderResult;
import com.gp.common.mybatisplus.pay.mpay.WalletParams;
import com.gp.common.mybatisplus.pay.mpay.order.CreateOrder;
import com.gp.common.mybatisplus.pay.mpay.order.OrderInfoDo;
import com.gp.common.mybatisplus.pay.mpay.order.PayCallbackResult;
import com.gp.common.mybatisplus.pay.mpay.order.QueryOrderInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CB支付 USDT充值服务（独立商户）
 */
@Slf4j
@Service
public class CBPAYUSDTService extends CBPAYService {

    // USDT充值下单
    public static final String USDT_PAY_URL = "/cooperate/deposit/order";
    // USDT充值订单查询
    public static final String USDT_QUERY_PAY_URL = "/cooperate/deposit/order/query";

    @Override
    protected void doRegister() {
        registerPayWService(CollUtil.newArrayList(PayMerchantCons.CB_PAY_USDT));
    }

    @Override
    public String queryMoney(com.gp.common.mybatisplus.entity.MerchantPay merchantPay) {
        return "USDT充值商户暂不支持余额查询";
    }

    @Override
    public String getSuccessResponse() {
        return "SUCCESS";
    }

    @Override
    public String getFailResponse() {
        return "FAIL";
    }

    @Override
    public PayCallbackResult callbackPay(MerchantPay merchantPay, JSONObject jsonObject) {
        String orderCode = jsonObject.getString("orderCode");
        String belowOrderCode = firstNotBlank(jsonObject.getString("belowOrderCode"), jsonObject.getString("customerOrderCode"));
        String network = jsonObject.getString("network");
        String depositUserUid = jsonObject.getString("depositUserUid");
        String amount = jsonObject.getString("amount");
        String rate = jsonObject.getString("rate");
        String cnyAmount = jsonObject.getString("cnyAmount");
        String status = jsonObject.getString("status");
        String userCode = jsonObject.getString("userCode");
        String receivedSign = jsonObject.getString("sign");

        String payParam = merchantPay.getParamStr();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String merchantKey = filterByApp(walletParams, "merchantKey");
        String localOrderNo = firstNotBlank(belowOrderCode, orderCode);

        if (!StrUtil.isBlank(receivedSign)) {
            if (!verifyUSDTNotify(orderCode, belowOrderCode, network, depositUserUid, amount, rate, cnyAmount, status, userCode, receivedSign, merchantKey)) {
                log.error("CB-USDT充值回调验签失败，body={}", jsonObject.toJSONString());
                return PayCallbackResult.failed(getFailResponse());
            }

            if (!"3".equals(status)) {
                log.info("CB-USDT充值回调状态非成功，status={}, belowOrderCode={}", status, belowOrderCode);
                return PayCallbackResult.failed(getFailResponse());
            }

            if (!isOrderPaidByQuery(localOrderNo, payParam, "回调二次确认")) {
                return PayCallbackResult.failed(getFailResponse());
            }
        } else {
            // 无签名时，不校验状态，直接主动查单
            if (!isOrderPaidByQuery(localOrderNo, payParam, "无签名场景")) {
                return PayCallbackResult.failed(getFailResponse());
            }
        }
        return buildSuccessResult(localOrderNo, orderCode);
    }

    @Override
    public OrderInfoDo queryOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = firstNotBlank(filterByApp(walletParams, "usdtDomainName"),
                filterByApp(walletParams, "domainName"),
                "http://crypto.cbpay2c.com");
        String merchantKey = filterByApp(walletParams, "merchantKey");
        String mchId = filterByApp(walletParams, "merchatCode");

        String customerOrderCode = queryOrderInfo.getOrderNo();
        String orderCode = "";
        String sign = signCBQuery(orderCode, customerOrderCode, mchId, merchantKey);

        Map<String, Object> params = new HashMap<>();
        params.put("userCode", mchId);
        params.put("orderCode", orderCode);
        params.put("customerOrderCode", customerOrderCode);
        params.put("sign", sign);

        log.info("CB-USDT订单查询请求参数: {}", JSON.toJSONString(params));
        JSONObject resultJsonObject = doPostJsonRequest(domainName + USDT_QUERY_PAY_URL, params);
        log.info("CB-USDT订单查询返回结果: {}", resultJsonObject.toJSONString());

        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
        orderInfo.setCurrencyId(PayConst.currency_id);

        Integer statusSys = 0;
        Integer code = resultJsonObject.getInteger("code");
        if (codeSuccess.equals(code)) {
            JSONObject data = resultJsonObject.getJSONObject("data");
            if (data != null) {
                String status = data.getString("status");
                if ("3".equals(status)) {
                    statusSys = 1;
                } else if ("4".equals(status)) {
                    statusSys = 2;
                }
            }
        }
        orderInfo.setOrderStatus(statusSys);
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    @SneakyThrows
    @Override
    public WalletCreateOrderResult createOrder(CreateOrder createOrder) {
        String payParam = createOrder.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = firstNotBlank(filterByApp(walletParams, "usdtDomainName"),
                filterByApp(walletParams, "domainName"),
                "http://crypto.cbpay2c.com");
        String merchantKey = filterByApp(walletParams, "merchantKey");
        String mchId = filterByApp(walletParams, "merchatCode");
        String rechargeNotifyUrl = filterByApp(walletParams, "rechargeNotifyUrl");
        String returnUrl = filterByApp(walletParams, "returnUrl");
        if (StrUtil.isBlank(returnUrl)) {
            returnUrl = rechargeNotifyUrl;
        }
        String rate = filterByApp(walletParams, "rate");

        String orderCode = createOrder.getOrderNo();
        String amount = createOrder.getOriginalAmount().setScale(2, RoundingMode.DOWN).toString();
        String network = firstNotBlank(filterByApp(walletParams, "network"), "USDT-TRC20");
        long timestamp = System.currentTimeMillis();
        String depositUserUid = createOrder.getUserId() != null ? createOrder.getUserId().toString() : orderCode;
        String sign = signUSDT(orderCode, amount, rate, rechargeNotifyUrl, returnUrl, network, timestamp, mchId, merchantKey);

        Map<String, Object> params = new HashMap<>();
        params.put("timestamp", timestamp);
        params.put("network", network);
        params.put("amount", amount);
        params.put("depositUserUid", depositUserUid);
        params.put("notifyUrl", rechargeNotifyUrl);
        params.put("returnUrl", returnUrl);
        if (StrUtil.isNotBlank(rate)) {
            params.put("rate", rate);
        }
        params.put("customerOrderCode", orderCode);
        params.put("uid", mchId);
        params.put("sign", sign);

        log.info("CB-USDT创建订单请求参数: {}", JSON.toJSONString(params));
        JSONObject resultJsonObject = doPostJsonRequest(domainName + USDT_PAY_URL, params);
        log.info("CB-USDT创建订单返回结果: {}", resultJsonObject.toJSONString());

        Integer code = resultJsonObject.getInteger("code");
        if (codeSuccess.equals(code)) {
            JSONObject data = resultJsonObject.getJSONObject("data");
            if (data != null) {
                String payUrlValue = data.getString("url");
                if (StrUtil.isNotBlank(payUrlValue)) {
                    WalletCreateOrderResult result = new WalletCreateOrderResult();
                    result.setUrl(payUrlValue);
                    return result;
                }
            }
            throw new MyPayException("CB-USDT下单返回数据为空，未找到支付链接");
        }

        String msg = StrUtil.format("商户编码: {}, CB-USDT下单异常: code={}, message={}",
                createOrder.getPayMerchantCode(), code, resultJsonObject.getString("message"));
        errorTelegramUtil.dealErrorMsg(msg);
        throw new MyPayException(MessagesUtils.get("bot.pay.ZFYC"));
    }

    private static String firstNotBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (StrUtil.isNotBlank(value)) {
                return value;
            }
        }
        return "";
    }

    /**
     * CB-USDT下单签名（文档规则）
     * 传rate: customerOrderCode&amount&rate&notifyUrl&returnUrl&network&timestamp&uid&key
     * 不传rate: customerOrderCode&amount&notifyUrl&returnUrl&network&timestamp&uid&key
     */
    private static String signUSDT(String customerOrderCode, String amount, String rate, String notifyUrl, String returnUrl,
                                   String network, long timestamp, String uid, String key) {
        String signStr;
        if (StrUtil.isNotBlank(rate)) {
            signStr = customerOrderCode + "&" + amount + "&" + rate + "&" + notifyUrl + "&" + returnUrl
                    + "&" + network + "&" + timestamp + "&" + uid + "&" + key;
        } else {
            signStr = customerOrderCode + "&" + amount + "&" + notifyUrl + "&" + returnUrl
                    + "&" + network + "&" + timestamp + "&" + uid + "&" + key;
        }
        log.info("CB-USDT下单签名字符串: {}", signStr);
        return com.gp.common.base.utils.MD5Util.getStr(signStr).toUpperCase();
    }

    /**
     * CB-USDT充值回调验签（文档规则）
     * orderCode&belowOrderCode&network&depositUserUid&amount&rate&cnyAmount&status&userCode&key
     */
    private static boolean verifyUSDTNotify(String orderCode, String belowOrderCode, String network, String depositUserUid,
                                            String amount, String rate, String cnyAmount, String status, String userCode,
                                            String receivedSign, String key) {
        if (StrUtil.hasBlank(orderCode, belowOrderCode, network, depositUserUid, amount, cnyAmount, status, userCode, receivedSign, key)) {
            return false;
        }
        String signStr = orderCode + "&" + belowOrderCode + "&" + network + "&" + depositUserUid + "&" + amount + "&"
                + StrUtil.blankToDefault(rate, "") + "&" + cnyAmount + "&" + status + "&" + userCode + "&" + key;
        String calculatedSign = com.gp.common.base.utils.MD5Util.getStr(signStr).toUpperCase();
        log.info("CB-USDT充值回调验签串: {}", signStr);
        log.info("CB-USDT充值回调验签 - 接收签名: {}, 计算签名: {}", receivedSign, calculatedSign);
        return receivedSign.equalsIgnoreCase(calculatedSign);
    }

    private boolean isOrderPaidByQuery(String orderNo, String payParam, String scene) {
        try {
            QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
            queryOrderInfo.setOrderNo(orderNo);
            queryOrderInfo.setPayParam(payParam);
            OrderInfoDo orderInfoDo = queryOrder(queryOrderInfo);
            if (orderInfoDo == null || orderInfoDo.getData() == null || orderInfoDo.getData().getOrderStatus() != 1) {
                log.error("CB-USDT{}查单未支付，orderNo={}", scene, orderNo);
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("CB-USDT{}查单异常，orderNo={}", scene, orderNo, e);
            return false;
        }
    }

    private PayCallbackResult buildSuccessResult(String orderNo, String merchantOrderNo) {
        return PayCallbackResult.builder()
                .orderNo(orderNo)
                .merchantOrderNo(merchantOrderNo)
                .orderStatus(1)
                .responseMsg(getSuccessResponse())
                .isSuccess(true)
                .build();
    }

    private static JSONObject doPostJsonRequest(String url, Map<String, Object> params) {
        return PayWService.doPostJsonJson(url, params, 10000, "CB-USDT支付");
    }
}
