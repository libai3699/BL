package com.gp.common.mybatisplus.merchantpay.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
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
import com.gp.common.mybatisplus.merchantpay.constants.PayTypeCons;
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
import java.math.RoundingMode;
import java.util.*;

/**
 * CB支付服务
 * 支持代收（充值）和代付（提现）
 *
 * @author axing
 */
@Slf4j
@Service
public class CBPAYService extends PayWService {

    @Resource
    protected ErrorTelegramUtil errorTelegramUtil;

    // code成功码 - 200代表下单成功
    public static Integer codeSuccess = 200;

    // 支付下单（代收）
    public static String payUrl = "/system/api/pay";
    // 支付订单查询
    public static String queryPayUrl = "/system/api/query/pay-order";
    // 下发（代付）
    public static String remitUrl = "/system/api/remit";
    // 下发订单查询
    public static String queryRemitUrl = "/system/api/query/remit-order";
    // 余额查询
    public static String queryMoney = "/system/api/balance/{}";

    public String getSuccessResponse() {
        return "success";
    }

    public String getFailResponse() {
        return "fail";
    }

    @Override
    protected void doRegister() {
        registerPayWService(CollUtil.newArrayList(
                PayMerchantCons.CB_PAY));
    }

    @Override
    public JSONObject getNotifyRequestParam(HttpServletRequest request) {
        Map<String, String> map = RequestUntil.doParamCompatible(request);
        return JSONObject.from(map);
    }


    @Override
    public String queryMoney(MerchantPay merchantPay) {
        List<WalletParams> walletParams = JSON.parseArray(merchantPay.getParamStr(), WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String mchId = filterByApp(walletParams, "merchatCode"); // 商户
        String merchantKey = filterByApp(walletParams, "merchantKey"); // 支付密钥
        // 构建请求参数
        long timestamp = System.currentTimeMillis();
        //MD5(userCode&timestamp&key)
        String sign = MD5Util.getStr(mchId + "&" + timestamp + "&" + merchantKey).toUpperCase();
        JSONObject paramMap = new JSONObject();
        paramMap.put("timestamp", timestamp);
        paramMap.put("sign", sign);
        JSONObject resultJsonObject = doGetRequest(domainName + StrUtil.format(queryMoney, mchId), paramMap, JSONObject.class);
        log.info("CB支付余额查询返回结果: {}", resultJsonObject.toJSONString());
        return Optional.ofNullable(resultJsonObject.getJSONObject("data"))
                .map(o -> o.getBigDecimal("balance"))
//                .map(b -> b.divide(BigDecimal.valueOf(100), 2, RoundingMode.DOWN)) // 分→元，并向下保留2位
                .map(b -> b.setScale(2, RoundingMode.DOWN).toPlainString())
                .orElse("余额查询失败");
    }

    /**
     * CB支付 订单查询（充值查单）
     * 签名：MD5(orderCode&customerOrderCode&userCode&key)
     */
    @Override
    public OrderInfoDo queryOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String merchantKey = filterByApp(walletParams, "merchantKey"); // 支付密钥
        String mchId = filterByApp(walletParams, "merchatCode"); // 商户号

        // 构建查询请求参数
        String customerOrderCode = queryOrderInfo.getOrderNo(); // 商户订单号
        String orderCode = ""; // 平台订单号（支付接口返回的orderNo，没值传空字符串）

        // 生成签名：MD5(orderCode&customerOrderCode&userCode&key)
        String sign = signCBQuery(orderCode, customerOrderCode, mchId, merchantKey);

        // 构建请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("userCode", mchId);
        params.put("orderCode", orderCode);
        params.put("customerOrderCode", customerOrderCode);
        params.put("sign", sign);

        log.info("CB支付订单查询请求参数: {}", JSON.toJSONString(params));

        // 发送查询请求（POST form-urlencoded格式）
        JSONObject resultJsonObject = doPostFormRequest(domainName + queryPayUrl, params);
        log.info("CB支付订单查询返回结果: {}", resultJsonObject.toJSONString());

        // 构建返回结果
        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
        orderInfo.setCurrencyId(PayConst.currency_id);

        // 判断订单状态: code=200表示请求成功，status="3"表示已支付
        Integer statusSys = 0; // 默认未支付
        Integer code = resultJsonObject.getInteger("code");
        if (codeSuccess.equals(code)) {
            JSONObject data = resultJsonObject.getJSONObject("data");
            if (data != null) {
                String status = data.getString("status");
                // status: 1-初始 2-待支付 3-已支付 4-失败
                if ("3".equals(status)) {
                    statusSys = 1; // 已支付
                } else if ("4".equals(status)) {
                    statusSys = 2; // 已取消/失败
                }
            }
        }

        orderInfo.setOrderStatus(statusSys);
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    /**
     * CB支付 下发订单查询（提现查单）
     * 签名：MD5(orderCode&customerOrderCode&userCode&key)
     */
    @Override
    public OrderInfoDo queryWithdrawOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String merchantKey = filterByApp(walletParams, "withdrawMerchantKey"); // 提现密钥（如果没有则使用支付密钥）
        if (StrUtil.isBlank(merchantKey)) {
            merchantKey = filterByApp(walletParams, "merchantKey");
        }
        String mchId = filterByApp(walletParams, "merchatCode"); // 商户号

        // 构建查询请求参数
        String customerOrderCode = queryOrderInfo.getOrderNo(); // 商户订单号
        String orderCode = ""; // 平台订单号（下发接口返回的orderNo，没值传空字符串）

        // 生成签名：MD5(orderCode&customerOrderCode&userCode&key)
        String sign = signCBQuery(orderCode, customerOrderCode, mchId, merchantKey);

        // 构建请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("userCode", mchId);
        params.put("orderCode", orderCode);
        params.put("customerOrderCode", customerOrderCode);
        params.put("sign", sign);

        log.info("CB支付下发订单查询请求参数: {}", JSON.toJSONString(params));

        // 发送查询请求（POST form-urlencoded格式）
        JSONObject resultJsonObject = doPostFormRequest(domainName + queryRemitUrl, params);
        log.info("CB支付下发订单查询返回结果: {}", resultJsonObject.toJSONString());

        // 构建返回结果
        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
        orderInfo.setCurrencyId(PayConst.currency_id);

        // 判断订单状态: code=200表示请求成功，status="2"表示成功
        Integer statusSys = 0; // 默认未支付
        Integer code = resultJsonObject.getInteger("code");
        if (codeSuccess.equals(code)) {
            JSONObject data = resultJsonObject.getJSONObject("data");
            if (data != null) {
                String status = data.getString("status");
                // status: 1-初始 2-成功 3-失败
                if ("2".equals(status)) {
                    statusSys = 1; // 成功/已支付
                } else if ("3".equals(status)) {
                    statusSys = 2; // 失败/已取消
                }
            }
        }

        orderInfo.setOrderStatus(statusSys);
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    /**
     * CB支付 创建支付订单（代收/充值）
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

        // 构建下单参数
        String orderCode = createOrder.getOrderNo();
        String amount = createOrder.getOriginalAmount().setScale(2, RoundingMode.DOWN).toString(); // 金额保留2位小数，转为字符串
        //通道编码，默认3
        String payType = createOrder.getPayChannelCode();
        if (StrUtil.isBlank(payType)) {
            payType = firstNotBlank(filterByApp(walletParams, "payType"), "3");
        }

        // 生成签名：MD5(orderCode&amount&payType&userCode&key)，结果转大写
        String sign = signCB(orderCode, amount, payType, mchId, merchantKey);

        // 构建请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("userCode", mchId);
        params.put("orderCode", orderCode);
        params.put("amount", amount);
        params.put("payType", payType);
        params.put("callbackUrl", rechargeNotifyUrl);
        params.put("sign", sign);

        log.info("CB支付创建订单请求参数: {}", JSON.toJSONString(params));

        // 发送请求（POST form-urlencoded格式）
        JSONObject resultJsonObject = doPostFormRequest(domainName + payUrl, params);
        log.info("CB支付创建订单返回结果: {}", resultJsonObject.toJSONString());

        // 判断订单状态: code=200为下单成功
        Integer code = resultJsonObject.getInteger("code");
        if (codeSuccess.equals(code)) {
            // 从data对象中获取支付链接
            JSONObject data = resultJsonObject.getJSONObject("data");
            if (data != null) {
                String payUrlValue = data.getString("url");
                if (StrUtil.isNotBlank(payUrlValue)) {
                    // 返回结果
                    WalletCreateOrderResult result = new WalletCreateOrderResult();
                    result.setUrl(payUrlValue);
                    return result;
                }
            }
            throw new MyPayException("CB支付下单返回数据为空，未找到支付链接");
        } else {
            String msg = StrUtil.format("商户编码: {}, CB支付下单异常: code={}, message={}",
                    createOrder.getPayChannelCode(), code, resultJsonObject.getString("message"));
            errorTelegramUtil.dealErrorMsg(msg);
            throw new MyPayException(MessagesUtils.get("bot.pay.ZFYC"));
        }
    }

    @Override
    public JSONObject callbackPayParam(MerchantPay merchantPay, OrderAmount orderAmount) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderCode", orderAmount.getOrderNo());
        return jsonObject;
    }

    @Override
    public PayCallbackResult callbackPay(MerchantPay merchantPay, JSONObject jsonObject) {
        try {
            String orderNo = jsonObject.getString("orderCode");
            String payParam = merchantPay.getParamStr();
            OrderInfoDo orderInfoDo = null;
            // 主动查单
            try {
                QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
                queryOrderInfo.setOrderNo(orderNo);
                queryOrderInfo.setPayParam(payParam);
                orderInfoDo = queryOrder(queryOrderInfo);
                if (orderInfoDo == null || orderInfoDo.getData() == null
                        || orderInfoDo.getData().getOrderStatus() != 1) {
                    log.error("CB支付充值回调查单 - 订单未完成");
                    return PayCallbackResult.failed(getFailResponse());
                }
            } catch (Exception e) {
                log.error("CB支付充值回调查单异常", e);
                return PayCallbackResult.failed(getFailResponse());
            }

            //TODO 再传一下,金额, 币种,这些参数, 之后处理上分时校验校验
            log.info("CB支付充值回调 - 订单验证成功: {}", orderNo);
            return PayCallbackResult.success(orderNo, getSuccessResponse());
        } catch (Exception e) {
            log.error("CB支付充值回调异常", e);
            return PayCallbackResult.failed(getFailResponse());
        }
    }

    @Override
    public JSONObject callbackWithdrawParam(MerchantPay merchantPay, OrderLawWithdraw orderLawWithdraw) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("customerOrderCode", orderLawWithdraw.getOrderNo());
        return jsonObject;
    }

    @Override
    public PayCallbackResult callbackWithdraw(MerchantPay merchantPay, JSONObject jsonObject) {
        try {
            String orderNo = jsonObject.getString("customerOrderCode");
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
                        return PayCallbackResult.builder().orderNo(orderNo).orderStatus(2).responseMsg(getSuccessResponse())
                                .isSuccess(true).build();
                    }
                }
                return PayCallbackResult.builder().orderNo(orderNo).orderStatus(0).responseMsg(getSuccessResponse())
                        .isSuccess(false).build();
            } catch (Exception e) {
                log.error("CB支付提现回调查单异常", e);
                return PayCallbackResult.failed(getFailResponse());
            }
        } catch (Exception e) {
            log.error("CB支付提现回调异常", e);
            return PayCallbackResult.failed(getFailResponse());
        }
    }

    /**
     * CB支付 创建下发订单（提现）
     * 签名：MD5(orderCode&amount&address&userCode&key)
     */
    @SneakyThrows
    @Override
    public WithdrawResultDo withdraw(WithdrawOrder withdrawOrder) {
        String payParam = withdrawOrder.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String merchantKey = filterByApp(walletParams, "withdrawMerchantKey"); // 提现密钥（如果没有则使用支付密钥）
        if (StrUtil.isBlank(merchantKey)) {
            merchantKey = filterByApp(walletParams, "merchantKey");
        }
        String mchId = filterByApp(walletParams, "merchatCode"); // 商户号
        String withdrawNotifyUrl = filterByApp(walletParams, "withdrawNotifyUrl"); // 回调地址（可选）

        // 获取用户的支付卡
        Map<String, Object> withdrawParamMap = withdrawOrder.getWithdrawParam();
        if (MapUtil.isEmpty(withdrawParamMap)) {
            throw new MyPayException("订单缺少支付卡信息");
        }

        // yee 风格：支付类型直接校验，提现地址按统一别名规则提取
        String payTypeCode = withdrawOrder.getPayType().getCode();
        if (!PayTypeCons.PAY_CB.equals(payTypeCode) && !PayTypeCons.PAY_KD.equals(payTypeCode)) {
            throw new MyPayException("不支持的支付类型: " + payTypeCode);
        }
        String address = PayBindDataKeyEnum.ACCOUNT.getValue(withdrawParamMap);
        if (StrUtil.isBlank(address)) {
            throw new MyPayException("下发订单缺少钱包地址参数");
        }

        // 商户订单号
        String orderCode = withdrawOrder.getOrderNo();
        //提现只能向下省略,不能四舍五入,不能向下取证,盘口只能赚钱不能亏钱
        String amount = withdrawOrder.getAmount().setScale(2, RoundingMode.DOWN).toString(); // 金额保留2位小数，转为字符串

        // 生成签名：MD5(orderCode&amount&address&userCode&key)
        String sign = signCBRemit(orderCode, amount, address, mchId, merchantKey);

        // 构建请求参数
        Map<String, Object> params = new HashMap<>();
        params.put("userCode", mchId);
        params.put("orderCode", orderCode);
        params.put("amount", amount);
        params.put("address", address);
        if (StrUtil.isNotBlank(withdrawNotifyUrl)) {
            params.put("callbackUrl", withdrawNotifyUrl);
        }
        params.put("sign", sign);

        log.info("CB支付创建下发订单请求参数: {}", JSON.toJSONString(params));

        // 发送请求（POST form-urlencoded格式）
        JSONObject resultJsonObject = doPostFormRequest(domainName + remitUrl, params);
        log.info("CB支付创建下发订单返回结果: {}", resultJsonObject.toJSONString());

        // 判断订单状态: code=200为下单成功
        Integer code = resultJsonObject.getInteger("code");
        if (!codeSuccess.equals(code)) {
            String msg = StrUtil.format("商户编码: {}, CB支付下发下单异常: code={}, message={}",
                    withdrawOrder.getMerchantCode(), code, resultJsonObject.getString("message"));
            errorTelegramUtil.dealErrorMsg(msg);
            throw new MyPayException(MessagesUtils.get("bot.pay.ZFYC"));
        }

        // 获取data
        JSONObject data = resultJsonObject.getJSONObject("data");
        if (data == null) {
            throw new MyPayException("CB支付下发下单返回数据为空");
        }

        // 构造返回结果
        WithdrawResultDo.Result result = new WithdrawResultDo.Result();
        result.setOrderNo(withdrawOrder.getOrderNo());
        //上游订单号,有的话就加,没有就不加
        result.setUpOrderNo(data.getString("orderCode"));
        result.setUpInfo(resultJsonObject.toJSONString());
        result.setCurrencyId(PayConst.currency_id);
        result.setAmount(withdrawOrder.getAmount());
        result.setCreateTime(System.currentTimeMillis());

        WithdrawResultDo withdrawResultDo = new WithdrawResultDo();
        withdrawResultDo.setData(result);

        return withdrawResultDo;
    }

    @Override
    public JSONObject reverseCheckOrderWithdraw(MerchantPay merchantPay, Map<String, Object> requestMap) {
        return null;
    }

    /**
     * CB支付 支付下单签名算法
     * MD5(orderCode&amount&payType&userCode&key)，结果转大写
     *
     * @param orderCode 商户订单号
     * @param amount    支付金额
     * @param payType   支付类型（固定值3）
     * @param userCode  商户号
     * @param key       商户密钥
     * @return 签名字符串（大写）
     */
    public static String signCB(String orderCode, String amount, String payType, String userCode, String key) {
        // 按照固定顺序拼接：orderCode&amount&payType&userCode&key
        String signStr = orderCode + "&" + amount + "&" + payType + "&" + userCode + "&" + key;

        log.info("CB支付支付下单签名字符串: {}", signStr);
        return MD5Util.getStr(signStr).toUpperCase();
    }

    /**
     * CB支付 支付查询/下发查询签名算法
     * MD5(orderCode&customerOrderCode&userCode&key)，结果转大写
     *
     * @param orderCode         平台订单号（可为空字符串）
     * @param customerOrderCode 商户订单号
     * @param userCode          商户号
     * @param key               商户密钥
     * @return 签名字符串（大写）
     */
    public static String signCBQuery(String orderCode, String customerOrderCode, String userCode, String key) {
        // 按照固定顺序拼接：orderCode&customerOrderCode&userCode&key
        String signStr = orderCode + "&" + customerOrderCode + "&" + userCode + "&" + key;

        log.info("CB支付查询签名字符串: {}", signStr);
        return MD5Util.getStr(signStr).toUpperCase();
    }

    /**
     * CB支付 下发签名算法
     * MD5(orderCode&amount&address&userCode&key)，结果转大写
     *
     * @param orderCode 商户订单号
     * @param amount    下发金额
     * @param address   钱包地址
     * @param userCode  商户号
     * @param key       商户密钥
     * @return 签名字符串（大写）
     */
    public static String signCBRemit(String orderCode, String amount, String address, String userCode, String key) {
        // 按照固定顺序拼接：orderCode&amount&address&userCode&key
        String signStr = orderCode + "&" + amount + "&" + address + "&" + userCode + "&" + key;

        log.info("CB支付下发签名字符串: {}", signStr);
        return MD5Util.getStr(signStr).toUpperCase();
    }

    /**
     * CB支付 支付回调验签
     * 签名：MD5(orderCode&amount&userCode&status&key)
     *
     * @param orderCode    订单号
     * @param amount       金额
     * @param userCode     商户号
     * @param status       支付状态
     * @param receivedSign 接收到的签名
     * @param key          商户密钥
     * @return 验证结果
     */
    public static boolean verifyPayNotify(String orderCode, String amount, String userCode, String status,
                                          String receivedSign, String key) {
        if (StrUtil.isBlank(receivedSign)) {
            return false;
        }
        // 计算签名：MD5(orderCode&amount&userCode&status&key)
        String signStr = orderCode + "&" + amount + "&" + userCode + "&" + status + "&" + key;
        String calculatedSign = MD5Util.getStr(signStr).toUpperCase();
        log.debug("CB支付回调验签 - 接收签名: {}, 计算签名: {}", receivedSign, calculatedSign);
        return receivedSign.equalsIgnoreCase(calculatedSign);
    }

    /**
     * CB支付 下发回调验签
     * 签名：MD5(orderCode&customerOrderCode&amount&userCode&status&key)
     *
     * @param orderCode         平台订单号
     * @param customerOrderCode 商户订单号
     * @param amount            金额
     * @param userCode          商户号
     * @param status            下发状态
     * @param receivedSign      接收到的签名
     * @param key               商户密钥
     * @return 验证结果
     */
    public static boolean verifyRemitNotify(String orderCode, String customerOrderCode, String amount, String userCode,
                                            String status, String receivedSign, String key) {
        if (StrUtil.isBlank(receivedSign)) {
            return false;
        }
        // 计算签名：MD5(orderCode&customerOrderCode&amount&userCode&status&key)
        String signStr = orderCode + "&" + customerOrderCode + "&" + amount + "&" + userCode + "&" + status + "&" + key;
        String calculatedSign = MD5Util.getStr(signStr).toUpperCase();
        log.debug("CB支付下发回调验签 - 接收签名: {}, 计算签名: {}", receivedSign, calculatedSign);
        return receivedSign.equalsIgnoreCase(calculatedSign);
    }

    /**
     * CB支付 POST form-urlencoded请求
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 返回结果
     */
    public static JSONObject doPostFormRequest(String url, Map<String, Object> params) {
        return PayWService.doPostFormJson(url, params, 10000, "CB支付");
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

}
