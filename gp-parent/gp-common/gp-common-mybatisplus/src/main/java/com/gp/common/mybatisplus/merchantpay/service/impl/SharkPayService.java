package com.gp.common.mybatisplus.merchantpay.service.impl;

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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 鲨鱼支付：代收下单、查单、异步通知（application/x-www-form-urlencoded）。
 * 文档：参数去空后 ASCII 排序，key1=value1&key2=value2，末尾拼接 &key=商户秘钥，MD5 小写。
 */
@Slf4j
@Service
public class SharkPayService extends PayWService {

    private static final int CODE_SUCCESS = 200;

    private static final String PATH_NEW_ORDER = "/api/newOrder";
    private static final String PATH_QUERY_ORDER_V2 = "/api/queryOrderV2";

    @Resource
    private ErrorTelegramUtil errorTelegramUtil;

    @Override
    protected void doRegister() {
        registerPayWService(PayMerchantCons.SHARK_PAY);
    }

    @Override
    public JSONObject getNotifyRequestParam(HttpServletRequest request) {
        Map<String, String> map = RequestUntil.doParamCompatible(request);
        return JSONObject.from(map);
    }

    @Override
    public String queryMoney(MerchantPay merchantPay) {
        return "暂不支持";
    }

    /**
     * 与 PHP 示例一致：去空、按键排序、拼接 &key=秘钥、MD5 小写。
     */
    public static String sign(Map<String, Object> params, String secret) {
        String signStr = params.entrySet().stream()
                .filter(e -> {
                    Object v = e.getValue();
                    if (v == null) {
                        return false;
                    }
                    if (v instanceof String && StrUtil.isBlank((String) v)) {
                        return false;
                    }
                    return true;
                })
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("&"));
        signStr = signStr + "&key=" + secret;
        log.info("鲨鱼支付签名字符串: {}", signStr);
        return MD5Util.getStr(signStr).toLowerCase();
    }

    private static String normalizeGatewayBase(String domainName) {
        if (StrUtil.isBlank(domainName)) {
            return "";
        }
        String s = domainName.trim();
        while (s.endsWith("/")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    /** 商户订单号 10–50 位：不足左侧补 0（仅长度约束，不改变原有唯一性字段时可满足网关校验）。 */
    private static String ensureOrderIdLength(String orderNo) {
        if (StrUtil.isBlank(orderNo)) {
            return orderNo;
        }
        if (orderNo.length() >= 10) {
            return orderNo;
        }
        return StrUtil.padPre(orderNo, 10, '0');
    }

    public static JSONObject doPostForm(String url, Map<String, Object> params) {
        return PayWService.doPostFormJson(url, params, 20000, "鲨鱼支付");
    }

    @SneakyThrows
    @Override
    public WalletCreateOrderResult createOrder(CreateOrder createOrder) {
        String payParam = createOrder.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);

        String domainName = normalizeGatewayBase(filterByApp(walletParams, "domainName"));
        String merchantId = filterByApp(walletParams, "merchantId");
        String merchantKey = filterByApp(walletParams, "payMerchantKey");
        if (StrUtil.isBlank(merchantKey)) {
            merchantKey = filterByApp(walletParams, "merchantKey");
        }
        String notifyUrl = filterByApp(walletParams, "rechargeNotifyUrl");
        String returnUrl = filterByApp(walletParams, "rechargeCallbackUrl");

        if (StrUtil.isBlank(domainName) || StrUtil.isBlank(merchantId) || StrUtil.isBlank(merchantKey)) {
            throw new MyPayException("鲨鱼支付配置不完整(domainName/merchantId/payMerchantKey)");
        }

        String orderId = ensureOrderIdLength(createOrder.getOrderNo());
        String orderAmount = createOrder.getOriginalAmount().setScale(2, RoundingMode.HALF_UP).toPlainString();
        String channelType = createOrder.getPayChannelCode();

        Map<String, Object> params = new TreeMap<>();
        params.put("merchantId", merchantId);
        params.put("orderId", orderId);
        params.put("orderAmount", orderAmount);
        params.put("channelType", channelType);
        params.put("notifyUrl", notifyUrl);
        params.put("isForm", "2");
        if (StrUtil.isNotBlank(returnUrl)) {
            params.put("returnUrl", returnUrl);
        }
        Map<String, Object> bindData = createOrder.getBindData();
        if (bindData != null) {
            Object ip = bindData.get("clientIp");
            if (ip == null) {
                ip = bindData.get("ip");
            }
            if (ip != null && StrUtil.isNotBlank(String.valueOf(ip))) {
                params.put("payer_ip", String.valueOf(ip));
            }
            Object payerId = bindData.get("payer_id");
            if (payerId != null && StrUtil.isNotBlank(String.valueOf(payerId))) {
                params.put("payer_id", String.valueOf(payerId));
            }
        }
        if (StrUtil.isNotBlank(createOrder.getRemark())) {
            params.put("order_title", createOrder.getRemark());
        }

        params.put("sign", sign(params, merchantKey));

        JSONObject resultJson = doPostForm(domainName + PATH_NEW_ORDER, params);
        Integer code = resultJson.getInteger("code");
        if (code != null && code == CODE_SUCCESS) {
            JSONObject data = resultJson.getJSONObject("data");
            if (data == null) {
                throw new MyPayException("鲨鱼支付下单成功但 data 为空");
            }
            String payUrl = data.getString("payUrl");
            if (StrUtil.isBlank(payUrl)) {
                payUrl = data.getString("pay_url");
            }
            if (StrUtil.isBlank(payUrl)) {
                throw new MyPayException("鲨鱼支付下单未返回 payUrl");
            }
            WalletCreateOrderResult result = new WalletCreateOrderResult();
            result.setUrl(payUrl);
            result.setUpInfo(resultJson.toJSONString());
            return result;
        }
        String msg = StrUtil.format("鲨鱼支付下单失败: code={}, msg={}, body={}",
                code, resultJson.getString("msg"), resultJson.toJSONString());
        log.error(msg);
        errorTelegramUtil.dealErrorMsg(msg);
        throw new MyPayException(MessagesUtils.get("bot.pay.ZFYC"));
    }

    @Override
    public OrderInfoDo queryOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = normalizeGatewayBase(filterByApp(walletParams, "domainName"));
        String merchantId = filterByApp(walletParams, "merchantId");
        String merchantKey = filterByApp(walletParams, "payMerchantKey");
        if (StrUtil.isBlank(merchantKey)) {
            merchantKey = filterByApp(walletParams, "merchantKey");
        }

        String orderId = ensureOrderIdLength(queryOrderInfo.getOrderNo());

        Map<String, Object> params = new TreeMap<>();
        params.put("merchantId", merchantId);
        params.put("orderId", orderId);
        params.put("sign", sign(params, merchantKey));

        JSONObject resultJson = doPostForm(domainName + PATH_QUERY_ORDER_V2, params);

        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
        orderInfo.setCurrencyId(PayConst.currency_id);

        Integer code = resultJson.getInteger("code");
        if (code == null || code != CODE_SUCCESS) {
            orderInfo.setOrderStatus(0);
            orderInfoDo.setData(orderInfo);
            return orderInfoDo;
        }

        JSONObject data = resultJson.getJSONObject("data");
        if (data == null) {
            orderInfo.setOrderStatus(0);
            orderInfoDo.setData(orderInfo);
            return orderInfoDo;
        }

        String payStatus = data.getString("status");
        String amtStr = data.getString("amount");
        if (StrUtil.isNotBlank(amtStr)) {
            try {
                orderInfo.setAmount(new BigDecimal(amtStr));
                orderInfo.setActualAmount(orderInfo.getAmount());
            } catch (Exception ignored) {
                // ignore
            }
        }

        if ("paid".equalsIgnoreCase(payStatus)) {
            orderInfo.setOrderStatus(1);
        } else {
            orderInfo.setOrderStatus(0);
        }

        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    @Override
    public JSONObject callbackPayParam(MerchantPay merchantPay, OrderAmount orderAmount) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderId", orderAmount.getOrderNo());
        return jsonObject;
    }

    @Override
    public PayCallbackResult callbackPay(MerchantPay merchantPay, JSONObject notifyData) {
        try {
            String payParam = merchantPay.getParamStr();
            List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
            String merchantKey = filterByApp(walletParams, "payMerchantKey");
            if (StrUtil.isBlank(merchantKey)) {
                merchantKey = filterByApp(walletParams, "merchantKey");
            }

            if (notifyData == null || StrUtil.isBlank(notifyData.getString("sign"))) {
                String orderNo = notifyData != null ? notifyData.getString("orderId") : null;
                if (StrUtil.isBlank(orderNo)) {
                    return PayCallbackResult.failed("fail");
                }
                QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
                queryOrderInfo.setOrderNo(orderNo);
                queryOrderInfo.setPayParam(payParam);
                OrderInfoDo orderInfoDo = queryOrder(queryOrderInfo);
                if (orderInfoDo != null && orderInfoDo.getData() != null
                        && Integer.valueOf(1).equals(orderInfoDo.getData().getOrderStatus())) {
                    return PayCallbackResult.success(orderNo, "ok");
                }
                return PayCallbackResult.failed("fail");
            }

            String recvSign = notifyData.getString("sign");
            Map<String, Object> signMap = new TreeMap<>();
            for (String key : notifyData.keySet()) {
                if ("sign".equalsIgnoreCase(key)) {
                    continue;
                }
                signMap.put(key, notifyData.get(key));
            }
            String calc = sign(signMap, merchantKey);
            if (!calc.equalsIgnoreCase(recvSign)) {
                log.error("鲨鱼支付回调验签失败 calc={} recv={}", calc, recvSign);
                return PayCallbackResult.failed("fail");
            }

            if (!"ok".equalsIgnoreCase(notifyData.getString("status"))) {
                log.warn("鲨鱼支付回调 status 非 ok: {}", notifyData.getString("status"));
                return PayCallbackResult.failed("fail");
            }

            String orderNo = notifyData.getString("orderId");
            QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
            queryOrderInfo.setOrderNo(orderNo);
            queryOrderInfo.setPayParam(payParam);
            OrderInfoDo orderInfoDo = queryOrder(queryOrderInfo);
            if (orderInfoDo == null || orderInfoDo.getData() == null
                    || !Integer.valueOf(1).equals(orderInfoDo.getData().getOrderStatus())) {
                log.error("鲨鱼支付回调查单未确认已支付: {}", orderNo);
                return PayCallbackResult.failed("fail");
            }

            log.info("鲨鱼支付充值回调成功: {}", orderNo);
            return PayCallbackResult.success(orderNo, "ok");
        } catch (Exception e) {
            log.error("鲨鱼支付充值回调异常", e);
            return PayCallbackResult.failed("fail");
        }
    }

    @Override
    public WithdrawResultDo withdraw(WithdrawOrder withdrawOrder) {
        throw new MyPayException("鲨鱼支付不支持代付");
    }

    @Override
    public JSONObject callbackWithdrawParam(MerchantPay merchantPay, OrderLawWithdraw orderLawWithdraw) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderId", orderLawWithdraw != null ? orderLawWithdraw.getOrderNo() : "");
        return jsonObject;
    }

    @Override
    public PayCallbackResult callbackWithdraw(MerchantPay merchantPay, JSONObject notifyData) {
        return PayCallbackResult.failed("fail");
    }

    @Override
    public JSONObject reverseCheckOrderWithdraw(MerchantPay merchantPay, Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public OrderInfoDo queryWithdrawOrder(QueryOrderInfo queryOrderInfo) {
        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
        orderInfo.setOrderStatus(0);
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }
}
