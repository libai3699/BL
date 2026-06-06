package com.gp.common.mybatisplus.merchantpay.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.common.core.log.ErrorTelegramUtil;
import com.gp.common.mybatisplus.entity.MerchantPay;
import com.gp.common.mybatisplus.entity.OrderAmount;
import com.gp.common.mybatisplus.entity.OrderLawWithdraw;
import com.gp.common.mybatisplus.merchantpay.constants.PayMerchantCons;
import com.gp.common.mybatisplus.pay.mpay.WalletParams;

import com.gp.common.mybatisplus.pay.mpay.WalletCreateOrderResult;
import com.gp.common.mybatisplus.pay.mpay.order.*;
import com.gp.common.mybatisplus.merchantpay.service.PayWService;
import com.gp.common.mybatisplus.pay.constant.PayConst;
import com.gp.common.base.exception.MyPayException;
import com.gp.common.base.utils.MD5Util;
import com.gp.common.mybatisplus.until.RequestUntil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 恒信支付(HXPay)
 * 支持代收（充值）和代付（提现）
 *
 * @author axing
 */
@Slf4j
@Service
public class HXPAYService extends PayWService {

    @Resource
    private ErrorTelegramUtil errorTelegramUtil;

    // 代收(PayIn)
    public static String PAYIN_CREATE_URI = "/payin/pay";
    public static String PAYIN_QUERY_URI = "/payin/pay/checkstatus";

    // 代付(PayOut)
    public static String PAYOUT_CREATE_URI = "/payout/Apicnpayoutpak23ereq2ml/api";
    public static String PAYOUT_QUERY_URI = "/payout/Apicnpayoutpak23ereq2ml/order";
    public static String PAYOUT_BALANCE_URI = "/payout/Apicnpayoutpak23ereq2ml/balance";

    @Override
    protected void doRegister() {
        registerPayWService(CollUtil.newArrayList(
                PayMerchantCons.HENGX_PAY));
    }

    @Override
    public String queryMoney(MerchantPay merchantPay) {
        return "暂不支持";
    }

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
            log.error("HXPayNotify param error", e);
        }
        return JSON.parseObject(jsonStr);
    }

    /**
     * 恒信支付 订单查询（充值查单）
     */
    @Override
    public OrderInfoDo queryOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String merchantPayKey = filterByApp(walletParams, "merchantPayKey"); // 支付密钥
        String appId = filterByApp(walletParams, "merchantCode"); // 商户ID (appId)
        String domainName = filterByApp(walletParams, "domainName"); // 支付域名

        String orderId = queryOrderInfo.getOrderNo();

        // 构建请求参数
        Map<String, Object> params = new TreeMap<>();
        params.put("appId", appId);
        params.put("orderId", orderId);

        // 生成签名 (secret_token)
        String sign = signRequest(params, merchantPayKey, "secret_token");
        params.put("sign", sign);

        log.info("恒信支付查询订单请求参数: {}", JSON.toJSONString(params));

        // 发送请求
        JSONObject result = doPostJsonRequest(domainName + PAYIN_QUERY_URI, params);
        log.info("恒信支付查询订单返回结果: {}", result.toJSONString());

        // 构建返回结果
        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();

        // code 2 查询成功
        Integer code = result.getInteger("code");
        Integer statusSys = 0; // 默认0-未支付
        String upOrderNo = null;

        if (code != null && code == 2) {
            Integer status = result.getInteger("status");
            upOrderNo = result.getString("ptOrderId"); // 平台单号
            // status 1 支付成功
            if (status != null && status == 1) {
                statusSys = 1; // 已支付
            }
        }
        orderInfo.setOrderNo(orderId);
        orderInfo.setCurrencyId(PayConst.currency_id);
        orderInfo.setOrderStatus(statusSys);
        orderInfo.setMerchantOrderNo(upOrderNo);
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    @Override
    public PayCallbackResult callbackPay(MerchantPay merchantPay, JSONObject jsonObject) {
        try {
            String orderNo = jsonObject.getString("orderId");
            log.info("恒信支付充值回调 - 订单号: {}", orderNo);

            // ===== 主动查单逻辑 =====
            QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
            queryOrderInfo.setOrderNo(orderNo);
            queryOrderInfo.setPayParam(merchantPay.getParamStr());

            OrderInfoDo orderInfoDo = queryOrder(queryOrderInfo);
            if (orderInfoDo == null || orderInfoDo.getData() == null || orderInfoDo.getData().getOrderStatus() != 1) {
                log.error("恒信支付充值回调查单 - 订单未完成");
                return PayCallbackResult.failed("fail");
            }

            return PayCallbackResult.builder()
                    .orderNo(orderNo)
                    .merchantOrderNo(orderInfoDo.getData().getMerchantOrderNo())
                    .orderStatus(1)
                    .responseMsg("success")
                    .isSuccess(true)
                    .build();

        } catch (Exception e) {
            log.error("恒信支付充值回调异常", e);
            return PayCallbackResult.failed("fail");
        }
    }

    /**
     * 恒信支付 代付订单查询
     */
    @Override
    public OrderInfoDo queryWithdrawOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String appId = filterByApp(walletParams, "merchantCode"); // 提现商户ID
        String withdrawDomainName = filterByApp(walletParams, "withdrawDomainName"); // 代付域名

        String orderId = queryOrderInfo.getOrderNo();

        // 构建请求参数
        Map<String, Object> params = new TreeMap<>();
        params.put("appId", appId);
        params.put("orderId", orderId);

        log.info("恒信支付代付查询请求参数: {}", JSON.toJSONString(params));

        // 发送请求
        JSONObject result = doPostJsonRequest(withdrawDomainName + PAYOUT_QUERY_URI, params);
        log.info("恒信支付代付查询返回结果: {}", result.toJSONString());

        // 构建返回结果
        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();

        // code 2 查询成功
        Integer code = result.getInteger("code");
        Integer statusSys = 0; // 默认处理中
        String upOrderNo = null;

        if (code != null && code == 2) {
            JSONObject data = result.getJSONObject("data");
            if (data != null) {
                Integer status = data.getInteger("status");
                upOrderNo = data.getString("ptOrderId");
                // 1 代付成功 2 代付失败 3 代付中
                if (status != null) {
                    if (status == 1) {
                        statusSys = 1; // 成功
                    } else if (status == 2) {
                        statusSys = 2; // 失败
                    } else {
                        statusSys = 0; // 处理中
                    }
                }
            }
        }
        orderInfo.setOrderNo(orderId);
        orderInfo.setCurrencyId(PayConst.currency_id);
        orderInfo.setOrderStatus(statusSys);
        orderInfo.setMerchantOrderNo(upOrderNo);
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    @Override
    public PayCallbackResult callbackWithdraw(MerchantPay merchantPay, JSONObject jsonObject) {
        try {
            String orderNo = jsonObject.getString("orderId");
            log.info("恒信支付提现回调 - 订单号: {}", orderNo);

            // ===== 主动查单逻辑 =====
            QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
            queryOrderInfo.setOrderNo(orderNo);
            queryOrderInfo.setPayParam(merchantPay.getParamStr());

            OrderInfoDo orderInfoDo = queryWithdrawOrder(queryOrderInfo);
            if (orderInfoDo == null || orderInfoDo.getData() == null) {
                log.error("恒信支付提现回调查单 - 查询结果为空");
                return PayCallbackResult.failed("fail");
            }

            Integer orderStatus = orderInfoDo.getData().getOrderStatus();
            String merchantOrderNo = orderInfoDo.getData().getMerchantOrderNo();

            if (orderStatus == 1) {
                return PayCallbackResult.builder()
                        .orderNo(orderNo)
                        .merchantOrderNo(merchantOrderNo)
                        .orderStatus(1)
                        .responseMsg("success")
                        .isSuccess(true)
                        .build();
            } else if (orderStatus == 2) {
                return PayCallbackResult.builder()
                        .orderNo(orderNo)
                        .merchantOrderNo(merchantOrderNo)
                        .orderStatus(2)
                        .responseMsg("success")
                        .isSuccess(true) // 业务层处理失败退款
                        .build();
            } else {
                return PayCallbackResult.builder()
                        .orderNo(orderNo)
                        .merchantOrderNo(merchantOrderNo)
                        .orderStatus(0)
                        .responseMsg("success")
                        .isSuccess(true)
                        .build();
            }
        } catch (Exception e) {
            log.error("恒信支付提现回调异常", e);
            return PayCallbackResult.failed("fail");
        }
    }

    /**
     * 恒信支付 创建支付订单（代收/充值）
     */
    @SneakyThrows
    @Override
    public WalletCreateOrderResult createOrder(CreateOrder createOrder) {
        String payParam = createOrder.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);

        String appId = filterByApp(walletParams, "merchantCode"); // 商户ID
        String merchantPayKey = filterByApp(walletParams, "merchantPayKey"); // 密钥
        String notifyUrl = filterByApp(walletParams, "rechargeNotifyUrl"); // 异步回调
        String domainName = filterByApp(walletParams, "domainName"); // 支付域名

        // 扩展参数获取 bankCode/type
        String extParam = createOrder.getExtParam();
        JSONObject extObj = JSONObject.parseObject(extParam);
        String type = extObj.getString("type");

        String orderId = createOrder.getOrderNo();
        BigDecimal money = createOrder.getOriginalAmount().setScale(2, RoundingMode.DOWN); // 单位元

        Map<String, Object> params = new TreeMap<>();
        params.put("appId", appId);
        params.put("orderId", orderId);
        params.put("money", money.toString());
        params.put("notifyUrl", notifyUrl);
        params.put("returnUrl", "https://www.baidu.com");
        params.put("type", type);
        // 签名
        String sign = signRequest(params, merchantPayKey, "secret_token");
        params.put("sign", sign);

        log.info("恒信支付创建订单请求参数: {}", JSON.toJSONString(params));

        JSONObject result = doPostJsonRequest(domainName + PAYIN_CREATE_URI, params);
        log.info("恒信支付创建订单返回结果: {}", result.toJSONString());

        // code 2 创建订单成功
        Integer code = result.getInteger("code");
        if (code != null && code == 2) {
            String url = result.getString("url");
            if (StrUtil.isNotBlank(url)) {
                WalletCreateOrderResult createResult = new WalletCreateOrderResult();
                createResult.setUrl(url);
                return createResult;
            } else {
                throw new MyPayException("恒信支付下单成功但未返回url");
            }
        } else {
            String msg = result.getString("msg");
            String errParams = StrUtil.format("商户编码: {}, 恒信支付下单异常: code={}, msg={}",
                    createOrder.getPayChannelCode(), code, msg);
            errorTelegramUtil.dealErrorMsg(errParams);
            throw new MyPayException(msg != null ? msg : "下单失败");
        }
    }

    @Override
    public JSONObject callbackPayParam(MerchantPay merchantPay, OrderAmount orderAmount) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderId", orderAmount.getOrderNo());
        return jsonObject;
    }


    /**
     * 恒信支付 创建代付订单（提现）
     */
    @SneakyThrows
    @Override
    public WithdrawResultDo withdraw(WithdrawOrder withdrawOrder) {
        String payParam = withdrawOrder.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);

        String appId = filterByApp(walletParams, "merchantCode"); // 代付商户ID
        String key = filterByApp(walletParams, "withdrawMerchantKey"); // 代付密钥
        String withdrawDomainName = filterByApp(walletParams, "withdrawDomainName"); // 代付域名
        String withdrawNotifyUrl = filterByApp(walletParams, "withdrawNotifyUrl"); // 代付域名

        // 解析 withdrawParam
//        String withdrawParam = withdrawOrder.getWithdrawParam();
        Map<String, Object> withdrawParam = withdrawOrder.getWithdrawParam();
        String phone = "13800138000"; // 默认或随机
        String email = "abc@gmail.com"; // 默认或随机

//        JSONObject wObj = JSONObject.parseObject(withdrawParam);
        JSONObject wObj = JSON.parseObject(JSON.toJSONString(withdrawParam));
        String name = wObj.getString("payeeName"); // 姓名
        String zfbAccount = wObj.getString("zfbAccount"); // 开户行
        String beneficiary = wObj.getString("beneficiary"); // 卡号
        String type = wObj.getString("type");

        BigDecimal amount = withdrawOrder.getAmount().setScale(2, RoundingMode.DOWN);
        String orderId = withdrawOrder.getOrderNo();

        Map<String, Object> params = new TreeMap<>();
        params.put("appId", appId);
        params.put("orderId", orderId);
        params.put("name", name);
        params.put("amount", amount.toString());
        params.put("phone", phone);
        params.put("account", StrUtil.isNotEmpty(beneficiary) ? beneficiary : zfbAccount);
        params.put("bankName", "ss");
        params.put("email", email);
        params.put("notifyUrl", withdrawNotifyUrl);
        params.put("type", type); // 1支付宝 2银行卡

        // 签名 (payoutapi_key)
        String sign = signRequest(params, key, "payoutapi_key");
        params.put("sign", sign);

        log.info("恒信支付代付下单请求参数: {}", JSON.toJSONString(params));

        JSONObject result = doPostJsonRequest(withdrawDomainName + PAYOUT_CREATE_URI, params);
        log.info("恒信支付代付下单返回结果: {}", result.toJSONString());

        Integer code = result.getInteger("code");

        WithdrawResultDo withdrawResultDo = new WithdrawResultDo();
        if (code != null && code == 2) {
            String ptOrderId = result.getString("ptOrderId");

            WithdrawResultDo.Result res = new WithdrawResultDo.Result();
            res.setOrderNo(orderId);
            res.setUpOrderNo(ptOrderId);
            res.setUpInfo(result.toJSONString());
            res.setCurrencyId(PayConst.currency_id);
            res.setAmount(amount);
            res.setCreateTime(System.currentTimeMillis());

            withdrawResultDo.setCode(0); // 0 表示成功提交
            withdrawResultDo.setMsg("提交成功");
            withdrawResultDo.setData(res);
            return withdrawResultDo;
        } else {
            String msg = result.getString("msg");
            if (code == null) {
                throw new MyPayException("代付请求异常: " + msg);
            }
            // Code 1 明确失败
            String errorMsg = StrUtil.format("商户编码: {}, 恒信支付代付失败: code={}, msg={}",
                    withdrawOrder.getMerchantCode(), code, msg);
            errorTelegramUtil.dealErrorMsg(errorMsg);
            throw new MyPayException(msg);
        }
    }

    @Override
    public JSONObject callbackWithdrawParam(MerchantPay merchantPay, OrderLawWithdraw orderLawWithdraw) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderId", orderLawWithdraw.getOrderNo());
        return jsonObject;
    }

    @Override
    public JSONObject reverseCheckOrderWithdraw(MerchantPay merchantPay, Map<String, Object> requestMap) {
        return null;
    }

    /**
     * 签名算法
     */
    private String signRequest(Map<String, Object> params, String key, String suffixKeyName) {
        Map<String, Object> sortedParams = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : sortedParams.entrySet()) {
            if (entry.getValue() != null) {
                if (sb.length() > 0) {
                    sb.append("&");
                }
                sb.append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        sb.append("&").append(suffixKeyName).append("=").append(key);
        String signStr = sb.toString();
        log.info("Sign String: {}", signStr);
        return MD5Util.getStr(signStr).toLowerCase();
    }

    /**
     * 发送 POST JSON 请求
     */
    private JSONObject doPostJsonRequest(String url, Map<String, Object> params) {
        return PayWService.doPostJsonJson(url, params, 15000, "恒信");
    }
}
