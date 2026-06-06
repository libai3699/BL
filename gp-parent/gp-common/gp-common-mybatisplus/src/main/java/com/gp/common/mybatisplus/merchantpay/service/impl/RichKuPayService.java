package com.gp.common.mybatisplus.merchantpay.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.common.core.log.ErrorTelegramUtil;
import com.common.core.prop.ProxyProp;
import com.gp.common.base.exception.MyPayException;
import com.gp.common.base.utils.MD5Util;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.mybatisplus.entity.MerchantPay;
import com.gp.common.mybatisplus.entity.OrderAmount;
import com.gp.common.mybatisplus.entity.OrderLawWithdraw;
import com.gp.common.mybatisplus.entity.PayChannel;
import com.gp.common.mybatisplus.merchantpay.constants.PayMerchantCons;
import com.gp.common.mybatisplus.merchantpay.service.PayWService;
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
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.gp.common.mybatisplus.pay.constant.PayBindDataKeyEnum;


/**
 * Rich Ku 支付集成
 */
@Slf4j
@Service
public class RichKuPayService extends PayWService {

    @Resource
    private ErrorTelegramUtil errorTelegramUtil;

    @Override
    protected void doRegister() {
        registerPayWService(PayMerchantCons.RICHKU_PAY_RECHARGE);
        registerPayWService(PayMerchantCons.RICHKU_PAY_WITHDRAW_BYJ);
        registerPayWService(PayMerchantCons.RICHKU_PAY_WITHDRAW_ZFB);
    }

    @Override
    public String queryMoney(MerchantPay merchantPay) {
        return "暂不支持";
    }

    // ==================== 签名算法 ====================

    public static String sign(Map<String, Object> params, String secret) {
        TreeMap<String, Object> sortedMap = new TreeMap<>(params);
        StringBuilder signStr = new StringBuilder();
        for (Map.Entry<String, Object> entry : sortedMap.entrySet()) {
            Object value = entry.getValue();
            if (value != null && StrUtil.isNotEmpty(value.toString())) {
                if (signStr.length() > 0) signStr.append("&");
                signStr.append(entry.getKey()).append("=").append(value);
            }
        }
        signStr.append("&key=").append(secret);
        log.info("RichKu 签名字符串: {}", signStr.toString());
        String sign = MD5Util.getStr(signStr.toString()).toUpperCase();
        log.info("RichKu 签名结果: {}", sign);
        return sign;
    }

    @Override
    public JSONObject getNotifyRequestParam(HttpServletRequest request) {
        try {
            Map<String, String> paramMap = RequestUntil.doParamCompatible(request);
            return JSON.parseObject(JSON.toJSONString(paramMap));
        } catch (Exception e) {
            log.error("RichKu Notify param error", e);
        }
        return new JSONObject();
    }

    // ==================== 充值下单 ====================

    @SneakyThrows
    @Override
    public WalletCreateOrderResult createOrder(CreateOrder createOrder) {
        String payParam = createOrder.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        
        String domainName = filterByApp(walletParams, "domainName");
        String memberId = filterByApp(walletParams, "memberId");
        String secret = filterByApp(walletParams, "secret");
        String notifyUrl = filterByApp(walletParams, "rechargeNotifyUrl");
        String callbackUrl = filterByApp(walletParams, "rechargeCallbackUrl");
        String bankCode = createOrder.getPayChannelCode();
        Map<String, Object> params = new TreeMap<>();
        params.put("pay_memberid", memberId);
        params.put("pay_orderid", createOrder.getOrderNo());
        params.put("pay_applydate", DateUtil.now());
        params.put("pay_bankcode", bankCode);
        params.put("pay_notifyurl", notifyUrl);
        params.put("pay_callbackurl", callbackUrl);
        params.put("pay_amount", createOrder.getOriginalAmount().setScale(2, RoundingMode.HALF_UP).toString());

        params.put("pay_md5sign", sign(params, secret));
        params.put("pay_productname", "Recharge");
        params.put("pay_ip", "127.0.0.1");

        String url = domainName + "/Pay_Index.html";
        String result = doPost(url, params);
        log.info("RichKu 充值下单返回结果: {}", result);

        if (result.contains("<form")) {
            WalletCreateOrderResult walletResult = new WalletCreateOrderResult();
            walletResult.setUrl(result);
            walletResult.setForm(true);
            return walletResult;
        }

        JSONObject resultJson = JSON.parseObject(result);
        if (resultJson != null && ("1".equals(resultJson.getString("status")) || resultJson.containsKey("pay_url"))) {
            WalletCreateOrderResult walletResult = new WalletCreateOrderResult();
            walletResult.setUrl(resultJson.getString("pay_url")); 
            walletResult.setUpOrderNo(resultJson.getString("order_id"));
            walletResult.setUpInfo(result);
            return walletResult;
        } else {
            throw new MyPayException("RichKu 下单失败: " + (resultJson != null ? resultJson.getString("msg") : result));
        }
    }

    // ==================== 充值回调 ====================

    @Override
    public PayCallbackResult callbackPay(MerchantPay merchantPay, JSONObject notifyData) {
        try {
            // notifyData 为 null 时是手动查单触发（checkStatus），改走主动查单流程
            if (notifyData == null || notifyData.isEmpty() || notifyData.getString("sign") == null) {
                String orderNo = notifyData != null ? notifyData.getString("orderid") : null;
                log.info("RichKu 手动查单触发(sign为空) orderNo={}", orderNo);
                QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
                queryOrderInfo.setOrderNo(orderNo);
                queryOrderInfo.setPayParam(merchantPay.getParamStr());
                OrderInfoDo orderInfoDo = queryOrder(queryOrderInfo);
                if (orderInfoDo != null && orderInfoDo.getData() != null && orderInfoDo.getData().getOrderStatus() == 1) {
                    return PayCallbackResult.builder()
                            .orderNo(orderNo)
                            .merchantOrderNo(orderInfoDo.getData().getMerchantOrderNo())
                            .orderStatus(1)
                            .amount(orderInfoDo.getData().getActualAmount())
                            .responseMsg("OK")
                            .isSuccess(true)
                            .build();
                }
                return PayCallbackResult.failed("查单未成功");
            }

            log.info("RichKu 充值回调数据: {}", notifyData.toJSONString());
            String secret = filterByApp(JSON.parseArray(merchantPay.getParamStr(), WalletParams.class), "secret");

            Map<String, Object> signMap = new TreeMap<>();
            signMap.put("memberid", notifyData.get("memberid"));
            signMap.put("orderid", notifyData.get("orderid"));
            signMap.put("amount", notifyData.get("amount"));
            signMap.put("transaction_id", notifyData.get("transaction_id"));
            signMap.put("datetime", notifyData.get("datetime"));
            signMap.put("returncode", notifyData.get("returncode"));

            if (sign(signMap, secret).equalsIgnoreCase(notifyData.getString("sign"))) {
                if ("00".equals(notifyData.getString("returncode"))) {
                    return PayCallbackResult.builder()
                            .orderNo(notifyData.getString("orderid"))
                            .merchantOrderNo(notifyData.getString("transaction_id"))
                            .orderStatus(1)
                            .responseMsg("OK")
                            .isSuccess(true)
                            .build();
                }
            }
            return PayCallbackResult.failed("签名错误或状态非法");
        } catch (Exception e) {
            log.error("RichKu 回调处理异常", e);
            return PayCallbackResult.failed("Error");
        }
    }

    // ==================== 充值查单 ====================

    @Override
    public OrderInfoDo queryOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String memberId = filterByApp(walletParams, "memberId");
        String secret = filterByApp(walletParams, "secret");

        Map<String, Object> params = new TreeMap<>();
        params.put("pay_memberid", memberId);
        params.put("pay_orderid", queryOrderInfo.getOrderNo());
        params.put("pay_md5sign", sign(params, secret));

        String url = domainName + "/Pay_Trade_query.html";
        try {
            String result = doPost(url, params);
            log.info("RichKu 查单返回结果: {}", result);
            JSONObject resultJson = JSON.parseObject(result);
            if (resultJson != null && "00".equals(resultJson.getString("returncode"))) {
                OrderInfoDo orderInfoDo = new OrderInfoDo();
                OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
                orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
                orderInfo.setMerchantOrderNo(resultJson.getString("transaction_id"));
                orderInfo.setAmount(resultJson.getBigDecimal("amount"));
                orderInfo.setOrderStatus("SUCCESS".equalsIgnoreCase(resultJson.getString("trade_state")) ? 1 : 0);
                orderInfoDo.setData(orderInfo);
                return orderInfoDo;
            }
        } catch (Exception e) {
            log.error("RichKu 查单异常", e);
        }
        return null;
    }

    // ==================== 工具方法 ====================

    private String doPost(String url, Map<String, Object> params) {
        // 注: 异常类型从 MyPayException 改为 RuntimeException(由 PayWService 统一抛出),
        // 与 UpstreamErrorClassifier 配合识别 uncertain 网络异常.
        return PayWService.doPostForm(url, params, 20000, "RichKu");
    }

    @Override
    public JSONObject callbackPayParam(MerchantPay merchantPay, OrderAmount orderAmount) {
        // 手动查单时传入 orderid，供 callbackPay 识别订单
        JSONObject json = new JSONObject();
        if (orderAmount != null) {
            json.put("orderid", orderAmount.getOrderNo());
        }
        return json;
    }

    // ==================== 代付（提现）====================

    @Override
    public WithdrawResultDo withdraw(WithdrawOrder withdrawOrder) {
        String payParam = withdrawOrder.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);

        String domainName = filterByApp(walletParams, "domainName");
        String memberId = filterByApp(walletParams, "memberId");
        String secret = filterByApp(walletParams, "secret");
        String notifyUrl = filterByApp(walletParams, "withdrawNotifyUrl");
        PayChannel payChannel = withdrawOrder.getPayChannel();
        Map<String, Object> bindData = withdrawOrder.getBindData();
        if (bindData == null || bindData.isEmpty()) {
            bindData = withdrawOrder.getWithdrawParam();
        }
        String bankName = PayBindDataKeyEnum.BANK_CODE.getValue(bindData);
        String cardNumber = PayBindDataKeyEnum.ACCOUNT.getValue(bindData);
        String accountName = PayBindDataKeyEnum.PLAYER_NAME.getValue(bindData);
        String subbranch = PayBindDataKeyEnum.BANK_BRANCH.getValue(bindData);
        // 省市固定默认值
        String province = "广东省";
        String city = "广州市";

        log.info("payChannel {}", JSON.toJSONString(payChannel));
        log.info("payment_bankcode {}", JSON.toJSONString(payChannel.getCode()));
        String amount = withdrawOrder.getAmount().setScale(2, java.math.RoundingMode.HALF_UP).toString();
        String orderNo = withdrawOrder.getOrderNo();
        String payChannelCode = withdrawOrder.getPayChannelCode();
        // 签名参数（同充值：TreeMap ASCII排序 + &key=secret + MD5大写）
        Map<String, Object> signParams = new TreeMap<>();
        signParams.put("payment_memberid", memberId);
        signParams.put("payment_orderid", orderNo);
        signParams.put("payment_amount", amount);
        signParams.put("payment_bankcode", payChannelCode);
        signParams.put("payment_notifyurl", notifyUrl);
        signParams.put("payment_bankname", StrUtil.isBlank(bankName) ? "支付宝" : bankName);
        signParams.put("payment_subbranch", StrUtil.isBlank(subbranch) ?  "支付宝" : subbranch);
        signParams.put("payment_accountname", accountName);
        signParams.put("payment_cardnumber", cardNumber);
        signParams.put("payment_province", province);
        signParams.put("payment_city", city);

        String md5sign = sign(signParams, secret);

        // 请求参数（与签名参数一致，加上 payment_md5sign）
        Map<String, Object> params = new TreeMap<>(signParams);
        params.put("payment_md5sign", md5sign);

        String url = domainName + "/Payment_index.html";
        log.info("RichKu 代付请求参数: {}", JSON.toJSONString(params));
        String result = doPost(url, params);
        log.info("RichKu 代付返回结果: {}", result);

        JSONObject resultJson = JSON.parseObject(result);
        WithdrawResultDo withdrawResultDo = new WithdrawResultDo();

        if (resultJson != null && "success".equalsIgnoreCase(resultJson.getString("status"))) {
            WithdrawResultDo.Result res = new WithdrawResultDo.Result();
            res.setOrderNo(orderNo);
            res.setUpOrderNo(resultJson.getString("transaction_id"));
            res.setAmount(withdrawOrder.getAmount());
            res.setUpInfo(result);
            withdrawResultDo.setCode(0);
            withdrawResultDo.setData(res);
            withdrawResultDo.setMsg("成功");
        } else {
            String msg = resultJson != null ? resultJson.getString("msg") : "Unknown";
            log.error("RichKu 代付失败: {}", result);
            throw new MyPayException(StrUtil.isNotBlank(msg) ? msg : "RichKu代付失败");
        }
        return withdrawResultDo;
    }

    // ==================== 代付回调 ====================

    @Override
    public JSONObject callbackWithdrawParam(MerchantPay merchantPay, OrderLawWithdraw orderLawWithdraw) {
        JSONObject json = new JSONObject();
        if (orderLawWithdraw != null) {
            json.put("out_trade_no", orderLawWithdraw.getOrderNo());
        }
        return json;
    }

    @Override
    public PayCallbackResult callbackWithdraw(MerchantPay merchantPay, JSONObject notifyData) {
        try {
            log.info("RichKu 代付回调数据: {}", notifyData != null ? notifyData.toJSONString() : "null");

            // 手动触发查单
            if (notifyData == null || notifyData.getString("payment_md5sign") == null) {
                String orderNo = notifyData != null ? notifyData.getString("out_trade_no") : null;
                log.info("RichKu 代付手动查单 orderNo={}", orderNo);
                QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
                queryOrderInfo.setOrderNo(orderNo);
                queryOrderInfo.setPayParam(merchantPay.getParamStr());
                OrderInfoDo orderInfoDo = queryWithdrawOrder(queryOrderInfo);
                if (orderInfoDo != null && orderInfoDo.getData() != null) {
                    int status = orderInfoDo.getData().getOrderStatus();
                    return PayCallbackResult.builder()
                            .orderNo(orderNo)
                            .merchantOrderNo(orderInfoDo.getData().getMerchantOrderNo())
                            .orderStatus(status)
                            .responseMsg("OK")
                            .isSuccess(true)
                            .build();
                }
                return PayCallbackResult.failed("查单未成功");
            }

            // 正常回调验签
            List<WalletParams> walletParams = JSON.parseArray(merchantPay.getParamStr(), WalletParams.class);
            String secret = filterByApp(walletParams, "secret");

            Map<String, Object> signMap = new TreeMap<>();
            for (String key : notifyData.keySet()) {
                if (!"payment_md5sign".equals(key)) {
                    signMap.put(key, notifyData.get(key));
                }
            }

            String calcSign = sign(signMap, secret);
            String recvSign = notifyData.getString("payment_md5sign");
            if (!calcSign.equalsIgnoreCase(recvSign)) {
                log.error("RichKu 代付回调验签失败 calc={} recv={}", calcSign, recvSign);
                return PayCallbackResult.failed("签名错误");
            }

            String status = notifyData.getString("status");
            String refCode = notifyData.getString("refCode");
            String orderNo = notifyData.getString("out_trade_no");

            // refCode: 1=成功, 2=失败, 5=驳回, 3/4/6=处理中
            int orderStatus;
            if ("success".equalsIgnoreCase(status) && "1".equals(refCode)) {
                orderStatus = 1; // 打款成功
            } else if ("2".equals(refCode) || "5".equals(refCode)) {
                orderStatus = 2; // 失败
            } else {
                log.info("RichKu 代付处理中 refCode={}", refCode);
                return PayCallbackResult.failed("OK"); // 处理中，回复OK但不做最终处理
            }

            return PayCallbackResult.builder()
                    .orderNo(orderNo)
                    .merchantOrderNo(notifyData.getString("transaction_id"))
                    .orderStatus(orderStatus)
                    .responseMsg("OK")
                    .isSuccess(true)
                    .build();

        } catch (Exception e) {
            log.error("RichKu 代付回调异常", e);
            return PayCallbackResult.failed("Error");
        }
    }

    // ==================== 代付查单 ====================

    @Override
    public OrderInfoDo queryWithdrawOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String memberId = filterByApp(walletParams, "memberId");
        String secret = filterByApp(walletParams, "secret");


        Map<String, Object> signParams = new TreeMap<>();
        signParams.put("payment_memberid", memberId);
        signParams.put("payment_orderid", queryOrderInfo.getOrderNo());
        String md5sign = sign(signParams, secret);

        Map<String, Object> params = new TreeMap<>(signParams);
        params.put("payment_md5sign", md5sign);

        String url = domainName + "/Payment_Dfpay_query.html";
        log.info("RichKu 代付查单请求: {}", JSON.toJSONString(params));
        String result = doPost(url, params);
        log.info("RichKu 代付查单返回: {}", result);

        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());

        try {
            JSONObject resultJson = JSON.parseObject(result);
            if (resultJson != null && "success".equalsIgnoreCase(resultJson.getString("status"))) {
                String refCode = resultJson.getString("refCode");
                orderInfo.setMerchantOrderNo(resultJson.getString("transaction_id"));
                if ("1".equals(refCode)) {
                    orderInfo.setOrderStatus(1); // 已打款
                } else if ("2".equals(refCode) || "5".equals(refCode)) {
                    orderInfo.setOrderStatus(2); // 失败
                } else {
                    orderInfo.setOrderStatus(0); // 处理中
                }
            } else {
                orderInfo.setOrderStatus(0);
            }
        } catch (Exception e) {
            log.error("RichKu 代付查单解析异常", e);
            orderInfo.setOrderStatus(0);
        }

        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    @Override
    public JSONObject reverseCheckOrderWithdraw(MerchantPay merchantPay, Map<String, Object> requestMap) { return null; }
}
