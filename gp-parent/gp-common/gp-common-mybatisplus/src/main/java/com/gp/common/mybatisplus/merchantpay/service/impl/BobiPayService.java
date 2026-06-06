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
import com.gp.common.mybatisplus.merchantpay.constants.BobiPayTypeAttribute;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 波币钱包支付服务
 * 支持代收（充值）和代付（提现）
 *
 * @author axing
 */
@Slf4j
@Service
public class BobiPayService extends PayWService {

    /** 支付类型 → 提现参数解析器，新增类型只需在此注册一行 */
    private static final Map<String, Function<JSONObject, Map<String, String>>> WITHDRAW_PARSERS = new HashMap<>();
    static {
        WITHDRAW_PARSERS.put(BobiPayTypeAttribute.payTypeCode, BobiPayTypeAttribute::parse);
    }

    @Resource
    private ErrorTelegramUtil errorTelegramUtil;

    // code成功码 - 0代表正常
    public static Integer codeSuccess = 0;

    // 支付下单（代收）
    public static String payUrl = "/bobi_api/pay_independent";
    // 代付下单
    public static String cashUrl = "/bobi_api/cash";
    // 支付订单查询
    public static String queryPayUrl = "/bobi_api/query_pay";
    // 代付订单查询
    public static String queryCashUrl = "/bobi_api/query_cash";

    public String getSuccessResponse() {
        return "success";
    }

    public String getFailResponse() {
        return "fail";
    }

    @Override
    protected void doRegister() {
        registerPayWService(CollUtil.newArrayList(
                PayMerchantCons.BOBI_PAY));
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
     * 波币钱包 支付订单查询（充值查单）
     */
    @Override
    public OrderInfoDo queryOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String merchantKey = filterByApp(walletParams, "payMerchantKey"); // 支付密钥
        String mchId = filterByApp(walletParams, "mchId");

        // 构建查询请求参数
        String cpOrderId = queryOrderInfo.getOrderNo();

        // 构建请求参数（按key排序）
        Map<String, Object> params = new TreeMap<>();
        params.put("cp_order_id", cpOrderId);
        params.put("mch_id", mchId); // 商户号

        // 生成签名
        String sign = signRequest(params, merchantKey);
        params.put("sign", sign);

        log.info("波币钱包支付订单查询请求参数: {}", JSON.toJSONString(params));

        // 发送查询请求（POST form-urlencoded格式）
        JSONObject resultJsonObject = doPostFormRequest(domainName + queryPayUrl, params);
        log.info("波币钱包支付订单查询返回结果: {}", resultJsonObject.toJSONString());

        // 构建返回结果
        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
        orderInfo.setCurrencyId(PayConst.currency_id);

        // 判断订单状态: code=0正常，status: 0-未支付, 1-已支付, 2-已取消
        // 返回格式: {"code":0, "status":1, ...} (status直接在根级别)
        Integer statusSys = 0; // 默认未支付
        Integer code = resultJsonObject.getInteger("code");
        if (codeSuccess.equals(code)) {
            Integer status = resultJsonObject.getInteger("status");
            if (status != null && status == 1) { // 1-已支付
                statusSys = 1; // 已支付
            } else if (status != null && status == 2) { // 2-已取消
                statusSys = 2; // 已取消
            }
        }

        orderInfo.setOrderStatus(statusSys);
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    /**
     * 波币钱包 代付订单查询（提现查单）
     */
    @Override
    public OrderInfoDo queryWithdrawOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String merchantKey = filterByApp(walletParams, "withdrawMerchantKey"); // 提现密钥
        String mchId = filterByApp(walletParams, "mchId");

        // 构建查询请求参数
        String cpOrderId = queryOrderInfo.getOrderNo();

        // 构建请求参数（按key排序）
        Map<String, Object> params = new TreeMap<>();
        params.put("cp_order_id", cpOrderId);
        params.put("mch_id", mchId); // 商户号

        // 生成签名
        String sign = signRequest(params, merchantKey);
        params.put("sign", sign);

        log.info("波币钱包代付订单查询请求参数: {}", JSON.toJSONString(params));

        // 发送查询请求（POST form-urlencoded格式）
        JSONObject resultJsonObject = doPostFormRequest(domainName + queryCashUrl, params);
        log.info("波币钱包代付订单查询返回结果: {}", resultJsonObject.toJSONString());

        // 构建返回结果
        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
        orderInfo.setCurrencyId(PayConst.currency_id);

        // 判断订单状态: code=0正常，status: 0转账中/未支付, 1成功/已支付, 2失败/已取消
        // 返回格式: {"code":0, "status":1, ...} (status直接在根级别)
        Integer statusSys = 0; // 默认未支付
        Integer code = resultJsonObject.getInteger("code");
        if (codeSuccess.equals(code)) {
            Integer status = resultJsonObject.getInteger("status");
            if (status != null && status == 1) { // 1-成功/已支付
                statusSys = 1; // 已支付
            } else if (status != null && status == 2) { // 2-失败/已取消
                statusSys = 2; // 已取消
            }
        }

        orderInfo.setOrderStatus(statusSys);
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    /**
     * 波币钱包 创建支付订单（代收/充值）
     *
     * @param createOrder 订单信息
     * @return 订单结果
     */
    @SneakyThrows
    @Override
    public WalletCreateOrderResult createOrder(CreateOrder createOrder) {
        String payParam = createOrder.getPayParam();
        // 金额转换为bobi（整数，不保留小数点）
        BigDecimal amountYuan = createOrder.getOriginalAmount().setScale(2, RoundingMode.DOWN);
        long amountBobi = amountYuan.longValue();

        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String merchantKey = filterByApp(walletParams, "payMerchantKey"); // 支付密钥
        String rechargeNotifyUrl = filterByApp(walletParams, "rechargeNotifyUrl");
        String mchId = filterByApp(walletParams, "mchId");

        // 构建下单参数
        String cpOrderId = createOrder.getOrderNo();
        long time = System.currentTimeMillis() / 1000; // 10位时间戳
        // 构建请求参数（使用TreeMap自动排序）
        Map<String, Object> params = new TreeMap<>();
        params.put("callback_url", rechargeNotifyUrl); // 回调地址，注意参数名是 callback_url 不是 notify_url
        params.put("cp_order_id", cpOrderId);
        params.put("currency_id", 1); // 币种id（暂时仅支持CNY，值为1）
        params.put("mch_id", mchId); // 商户号
        params.put("money", amountBobi); // 金额（单位：bobi），注意参数名是 money 不是 amount
        params.put("time", time);

        // 可选参数：商品展示名
        // 生成签名
        String sign = signRequest(params, merchantKey);
        params.put("sign", sign);

        log.info("波币钱包创建支付订单请求参数: {}", JSON.toJSONString(params));

        // 波币钱包要求使用 POST form-urlencoded 提交
        JSONObject resultJsonObject = doPostFormRequest(domainName + payUrl, params);
        log.info("波币钱包创建支付订单返回结果: {}", resultJsonObject.toJSONString());

        // 波币钱包返回格式: {"code":0, "pay_url":"支付链接"} (pay_url直接在根级别)
        Integer code = resultJsonObject.getInteger("code");
        if (codeSuccess.equals(code)) {
            String payUrlValue = resultJsonObject.getString("pay_url");
            if (StrUtil.isNotBlank(payUrlValue)) {
                // 返回结果
                WalletCreateOrderResult result = new WalletCreateOrderResult();
                result.setUrl(payUrlValue);
                return result;
            } else {
                throw new MyPayException("波币钱包支付下单返回数据为空，未找到pay_url");
            }
        } else {
            String msg = StrUtil.format("商户编码: {}, 波币钱包支付下单异常: code={}, msg={}",
                    createOrder.getPayChannelCode(), code, resultJsonObject.getString("msg"));
            errorTelegramUtil.dealErrorMsg(msg);
            throw new MyPayException(MessagesUtils.get("bot.pay.ZFYC"));
        }
    }

    @Override
    public JSONObject callbackPayParam(MerchantPay merchantPay, OrderAmount orderAmount) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cp_order_id", orderAmount.getOrderNo());
        return jsonObject;
    }

    @Override
    public PayCallbackResult callbackPay(MerchantPay merchantPay, JSONObject jsonObject) {
        try {
            String orderNo = jsonObject.getString("cp_order_id");
            String payParam = merchantPay.getParamStr();
            // 主动查单
            try {
                QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
                queryOrderInfo.setOrderNo(orderNo);
                queryOrderInfo.setPayParam(payParam);
                OrderInfoDo orderInfoDo = queryOrder(queryOrderInfo);
                if (orderInfoDo == null || orderInfoDo.getData() == null
                        || orderInfoDo.getData().getOrderStatus() != 1) {
                    log.error("波币钱包充值回调查单 - 订单未完成");
                    return PayCallbackResult.failed(getFailResponse());
                }
            } catch (Exception e) {
                log.error("波币钱包充值回调查单异常", e);
                return PayCallbackResult.failed(getFailResponse());
            }

            log.info("波币钱包充值回调 - 订单验证成功: {}", orderNo);
            return PayCallbackResult.success(orderNo, getSuccessResponse());
        } catch (Exception e) {
            log.error("波币钱包充值回调异常", e);
            return PayCallbackResult.failed(getFailResponse());
        }
    }

    /**
     * 波币钱包 创建代付订单（提现）
     */
    @SneakyThrows
    @Override
    public WithdrawResultDo withdraw(WithdrawOrder withdrawOrder) {
        String payParam = withdrawOrder.getPayParam();
        // 金额转换为bobi（整数，不保留小数点）
        BigDecimal amountYuan = withdrawOrder.getAmount().setScale(0, RoundingMode.DOWN);
        long amountBobi = amountYuan.longValue();

        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String merchantKey = filterByApp(walletParams, "withdrawMerchantKey"); // 提现密钥
        String withdrawNotifyUrl = filterByApp(walletParams, "withdrawNotifyUrl");
        String mchId = filterByApp(walletParams, "mchId");

        // 解析提现参数，获取用户在bobi平台的钱包地址
        Map<String, Object> withdrawParam = withdrawOrder.getWithdrawParam();
        if (MapUtil.isEmpty(withdrawParam)) {
            throw new MyPayException("订单缺少支付卡信息");
        }
        JSONObject withdrawInfo = JSON.parseObject(JSON.toJSONString(withdrawParam));
        // 根据支付类型解析提现卡信息
        String payTypeCode = withdrawOrder.getPayType().getCode();
        Function<JSONObject, Map<String, String>> parser = WITHDRAW_PARSERS.get(payTypeCode);
        if (parser == null) {
            throw new MyPayException("不支持的支付类型: " + payTypeCode);
        }
        Map<String, String> account = parser.apply(withdrawInfo);
        String userAdress = account.getOrDefault("address", "");
        if (StrUtil.isBlank(userAdress)) {
            throw new MyPayException("代付订单缺少用户钱包地址参数");
        }

        // 构建下单参数
        String cpOrderId = withdrawOrder.getOrderNo();

        // 构建请求参数（使用TreeMap自动排序）
        Map<String, Object> params = new TreeMap<>();
        params.put("callback_url", withdrawNotifyUrl); // 回调地址，注意参数名是 callback_url 不是 notify_url
        params.put("cp_order_id", cpOrderId);
        params.put("currency_id", 1); // 币种id（暂时仅支持CNY，值为1）
        params.put("mch_id", mchId); // 商户号
        params.put("money", amountBobi); // 金额（单位：bobi），注意参数名是 money 不是 amount
        params.put("user_adress", userAdress); // 用户在bobi平台的钱包地址（注意拼写是 adress 不是 address）

        // 生成签名
        String sign = signRequest(params, merchantKey);
        params.put("sign", sign);

        log.info("波币钱包创建代付订单请求参数: {}", JSON.toJSONString(params));

        // 波币钱包要求使用 POST form-urlencoded 提交
        JSONObject resultJsonObject = doPostFormRequest(domainName + cashUrl, params);
        log.info("波币钱包创建代付订单返回结果: {}", resultJsonObject.toJSONString());

        // 波币钱包返回格式: {"code":0, "msg":"请求成功", "order_id":"R1656321260001867",
        // "cp_order_id":"CA562778364899", "status":1, "money":111, "user_adress":"...",
        // "cb_status":0}
        // status: 0转账中 1成功 2失败
        // cb_status: 0未发起 1回调成功 2回调失败
        Integer code = resultJsonObject.getInteger("code");
        if (!codeSuccess.equals(code)) {
            String msg = StrUtil.format("商户编码: {}, 波币钱包代付下单异常: code={}, msg={}",
                    withdrawOrder.getMerchantCode(), code, resultJsonObject.getString("msg"));
            errorTelegramUtil.dealErrorMsg(msg);
            throw new MyPayException(MessagesUtils.get("bot.pay.ZFYC"));
        }

        // 提取bobi平台订单号
        String orderId = resultJsonObject.getString("order_id"); // 平台流水号
        log.info("波币钱包代付订单创建成功, 商户订单号: {}, bobi订单号: {}", withdrawOrder.getOrderNo(), orderId);

        WithdrawResultDo.Result result = new WithdrawResultDo.Result();
        result.setOrderNo(withdrawOrder.getOrderNo());
        result.setUpOrderNo(orderId);
        result.setUpInfo(resultJsonObject.toJSONString());
        result.setCurrencyId(PayConst.currency_id);
        result.setAmount(amountYuan);
        result.setCreateTime(System.currentTimeMillis());

        WithdrawResultDo withdrawResultDo = new WithdrawResultDo();
        withdrawResultDo.setData(result);
        return withdrawResultDo;
    }

    @Override
    public JSONObject callbackWithdrawParam(MerchantPay merchantPay, OrderLawWithdraw orderLawWithdraw) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cp_order_id", orderLawWithdraw.getOrderNo());
        return jsonObject;
    }

    @Override
    public PayCallbackResult callbackWithdraw(MerchantPay merchantPay, JSONObject notifyData) {
        try {
            String orderNo = notifyData.getString("cp_order_id");
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
                log.error("波币钱包提现回调查单异常", e);
                return PayCallbackResult.failed(getFailResponse());
            }
        } catch (Exception e) {
            log.error("波币钱包提现回调异常", e);
            return PayCallbackResult.failed(getFailResponse());
        }
    }

    @Override
    public JSONObject reverseCheckOrderWithdraw(MerchantPay merchantPay, Map<String, Object> requestMap) {
        return null;
    }

    /**
     * 波币钱包 签名算法
     * 1. 将除sign以外的所有请求参数按照ASCII码的升序进行排序
     * 2. 按照key1=value1&key2=value2进行组合
     * 3. 最后加上商户秘钥（&pri_key=商户秘钥）
     * 4. 进行md5运算，结果转为小写
     *
     * @param params 请求参数（不包含sign）
     * @param key    商户密钥
     * @return 签名字符串
     */
    public static String signRequest(Map<String, Object> params, String key) {
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

        // 拼接商户密钥（注意：参数名是 pri_key，不是 key）
        signStr = signStr + "&pri_key=" + key;

        log.info("波币钱包签名字符串: {}", signStr);
        return MD5Util.getStr(signStr).toLowerCase();
    }

    /**
     * 波币钱包 POST form-urlencoded请求
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 返回结果
     */
    public static JSONObject doPostFormRequest(String url, Map<String, Object> params) {
        return PayWService.doPostFormJson(url, params, 10000, "波币钱包");
    }

}
