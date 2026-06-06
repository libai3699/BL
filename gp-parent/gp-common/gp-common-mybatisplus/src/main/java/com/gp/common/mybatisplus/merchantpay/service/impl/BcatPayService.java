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
import com.gp.common.mybatisplus.until.RequestUntil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * BcatPay 支付服务
 * 支持代收（充值）和代付（提现）
 * 支持地区：缅甸、巴西、日本、孟加拉、老挝、巴基斯坦、菲律宾、印尼、尼日利亚
 *
 * @author axing
 */
@Slf4j
@Service
public class BcatPayService extends PayWService {

    @Resource
    private ErrorTelegramUtil errorTelegramUtil;

    // code成功码 - 200代表成功
    public static Integer codeSuccess = 200;

    // 创建支付（代收）
    public static String payUrl = "/api/receiveOrder";
    // 支付结果查询
    public static String queryPayUrl = "/api/queryorder";
    // 创建代付（提现）
    public static String remitUrl = "/api/payment";
    // 代付结果查询
    public static String queryRemitUrl = "/api/querypayment";

    @Override
    protected void doRegister() {
        registerPayWService(CollUtil.newArrayList(
                PayMerchantCons.BcatPay
        ));
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
     * BcatPay 充值查单
     */
    @Override
    public OrderInfoDo queryOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String merchantKey = filterByApp(walletParams, "merchantKey");
        String merchantNum = filterByApp(walletParams, "merchantNum");

        String ordersn = queryOrderInfo.getOrderNo();

        Map<String, Object> params = new HashMap<>();
        params.put("mcid", merchantNum);
        params.put("ordersn", ordersn);

        String sign = signBcatPay(params, merchantKey);
        params.put("sign", sign);

        log.info("BcatPay 充值查单请求参数: {}", JSON.toJSONString(params));

        JSONObject result = doBcatPostFormRequest(domainName + queryPayUrl, params);
        log.info("BcatPay 充值查单返回: {}", result.toJSONString());

        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
        orderInfo.setCurrencyId(PayConst.currency_id);

        Integer statusSys = 0;
        Integer code = result.getInteger("code");
        if (codeSuccess.equals(code)) {
            JSONObject data = result.getJSONObject("data");
            if (data != null) {
                Integer status = data.getInteger("status");
                // status: 0=待支付, 1=已支付, 2=已退款, 3=失败
                if (status != null && status == 1) {
                    statusSys = 1;
                } else if (status != null && (status == 2 || status == 3)) {
                    statusSys = 2;
                }
            }
        }

        orderInfo.setOrderStatus(statusSys);
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    /**
     * BcatPay 提现查单
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

        String ordersn = queryOrderInfo.getOrderNo();

        Map<String, Object> params = new HashMap<>();
        params.put("mcid", merchantNum);
        params.put("ordersn", ordersn);

        String sign = signBcatPay(params, merchantKey);
        params.put("sign", sign);

        log.info("BcatPay 提现查单请求参数: {}", JSON.toJSONString(params));

        JSONObject result = doBcatPostFormRequest(domainName + queryRemitUrl, params);
        log.info("BcatPay 提现查单返回: {}", result.toJSONString());

        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
        orderInfo.setCurrencyId(PayConst.currency_id);

        Integer statusSys = 0;
        Integer code = result.getInteger("code");
        if (codeSuccess.equals(code)) {
            JSONObject data = result.getJSONObject("data");
            if (data != null) {
                Integer status = data.getInteger("status");
                // status: 0=处理中, 1=成功, 2=已拒绝
                if (status != null && status == 1) {
                    statusSys = 1;
                } else if (status != null && status == 2) {
                    statusSys = 2;
                }
            }
        }

        orderInfo.setOrderStatus(statusSys);
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    /**
     * BcatPay 创建充值订单
     */
    @SneakyThrows
    @Override
    public WalletCreateOrderResult createOrder(CreateOrder createOrder) {
        String payParam = createOrder.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String domainName = filterByApp(walletParams, "domainName");
        String merchantKey = filterByApp(walletParams, "merchantKey");
        String merchantNum = filterByApp(walletParams, "merchantNum");
        String rechargeNotifyUrl = filterByApp(walletParams, "rechargeNotifyUrl");
        String callbackUrl = filterByApp(walletParams, "callbackUrl");

        String orderNo = createOrder.getOrderNo();
        String amount = createOrder.getOriginalAmount().setScale(2, RoundingMode.DOWN).toString(); // 原始币种金额（必须为整数）

        // 获取收款通道ID（tdid）：优先从 extParam 读取，其次取配置
        String tdid = null;
        String extParam = createOrder.getExtParam();
        if (StrUtil.isNotBlank(extParam)) {
            try {
                JSONObject extJson = JSONObject.parseObject(extParam);
                tdid = extJson.getString("type");
            } catch (Exception e) {
                log.warn("BcatPay 解析extParam失败: {}", e.getMessage());
            }
        }
        if (StrUtil.isBlank(tdid)) {
            tdid = filterByApp(walletParams, "tdid");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("mcid", merchantNum);
        params.put("orderno", orderNo);
        params.put("price", amount);
        params.put("tdid", tdid);
        if (StrUtil.isNotBlank(rechargeNotifyUrl)) {
            params.put("callback_url", rechargeNotifyUrl);
        }
        if (StrUtil.isNotBlank(callbackUrl)) {
            params.put("returnUrl", callbackUrl);
        }

        String sign = signBcatPay(params, merchantKey);
        params.put("sign", sign);

        log.info("BcatPay 创建充值订单请求参数: {}", JSON.toJSONString(params));

        JSONObject resultJson = doBcatPostFormRequest(domainName + payUrl, params);
        log.info("BcatPay 创建充值订单返回: {}", resultJson.toJSONString());

        Integer code = resultJson.getInteger("code");
        if (codeSuccess.equals(code)) {
            String payUrlValue = resultJson.getString("data");
            if (StrUtil.isNotBlank(payUrlValue)) {
                WalletCreateOrderResult result = new WalletCreateOrderResult();
                result.setUrl(payUrlValue);
                result.setUpOrderNo(resultJson.getString("ordersn"));
                return result;
            }
            throw new MyPayException("BcatPay 下单未获取到支付链接");
        } else {
            String msg = resultJson.getString("msg");
            String errLog = StrUtil.format("商户编码: {}, BcatPay 充值下单异常: code={}, msg={}",
                    createOrder.getPayChannelCode(), code, msg);
            errorTelegramUtil.dealErrorMsg(errLog);
            throw new MyPayException(StrUtil.isNotBlank(msg) ? msg : MessagesUtils.get("bot.pay.ZFYC"));
        }
    }

    @Override
    public JSONObject callbackPayParam(MerchantPay merchantPay, OrderAmount orderAmount) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ordersn", orderAmount.getOrderNo());
        return jsonObject;
    }

    @Override
    public PayCallbackResult callbackPay(MerchantPay merchantPay, JSONObject notifyData) {
        try {
            String orderNo = notifyData.getString("ordersn");
            if (StrUtil.isBlank(orderNo)) {
                log.error("BcatPay 充值回调 - 订单号为空");
                return PayCallbackResult.failed("orderNo is empty");
            }

            String payParam = merchantPay.getParamStr();
            List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
            String merchantKey = filterByApp(walletParams, "merchantKey");

            // 1. 验签
            String receivedSign = notifyData.getString("sign");
            Map<String, Object> callbackParams = new HashMap<>();
            notifyData.forEach(callbackParams::put);
            if (!verifyBcatPayNotify(callbackParams, receivedSign, merchantKey)) {
                log.error("BcatPay 充值回调 - 签名验证失败, orderNo: {}", orderNo);
                return PayCallbackResult.failed("sign error");
            }

            // 2. 主动查单复核
            QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
            queryOrderInfo.setOrderNo(orderNo);
            queryOrderInfo.setPayParam(payParam);
            OrderInfoDo orderInfoDo = queryOrder(queryOrderInfo);
            if (orderInfoDo == null || orderInfoDo.getData() == null
                    || orderInfoDo.getData().getOrderStatus() != 1) {
                log.error("BcatPay 充值回调查单 - 订单未成功, orderNo: {}", orderNo);
                return PayCallbackResult.failed("order not paid");
            }

            log.info("BcatPay 充值回调 - 处理成功: {}", orderNo);
            return PayCallbackResult.builder()
                    .orderNo(orderNo)
                    .merchantOrderNo(notifyData.getString("platformSn"))
                    .orderStatus(1)
                    .responseMsg("SUCCESS")
                    .isSuccess(true)
                    .build();
        } catch (Exception e) {
            log.error("BcatPay 充值回调异常", e);
            return PayCallbackResult.failed("exception");
        }
    }

    /**
     * BcatPay 创建代付订单（提现）
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
        String merchantNum = filterByApp(walletParams, "merchantNum");
        String withdrawNotifyUrl = filterByApp(walletParams, "withdrawNotifyUrl");
        String tdid = filterByApp(walletParams, "tdid");

        Map<String, Object> bindData = withdrawOrder.getBindData();
        if (bindData == null || bindData.isEmpty()) {
            bindData = withdrawOrder.getWithdrawParam();
        }

        String targetBank = PayBindDataKeyEnum.ACCOUNT.getValue(bindData);       // 收款账号
        String bankCode = PayBindDataKeyEnum.BANK_CODE.getValue(bindData);       // 银行代码
        String targetBankUser = PayBindDataKeyEnum.PLAYER_NAME.getValue(bindData); // 持卡人姓名

        if (StrUtil.isBlank(targetBank) || StrUtil.isBlank(bankCode) || StrUtil.isBlank(targetBankUser)) {
            throw new MyPayException("BcatPay 代付缺少必要参数：收款账号、银行编码或持卡人姓名");
        }

        String orderNo = withdrawOrder.getOrderNo();
        String amount = withdrawOrder.getAmount().setScale(0, RoundingMode.HALF_UP).toString();

        Map<String, Object> params = new HashMap<>();
        params.put("mcid", merchantNum);
        params.put("orderno", orderNo);
        params.put("type", "1");
        params.put("price", amount);
        params.put("tdid", tdid);
        params.put("zh", targetBank);        // 收款账户
        params.put("mc", targetBankUser);    // 收款人姓名
        params.put("bankName", "HST-UB");    // 银行名称（老挝代付固定值）
        params.put("bankCode", bankCode);    // 银行代码
        params.put("callback_url", withdrawNotifyUrl != null ? withdrawNotifyUrl : "");

        String sign = signBcatPay(params, merchantKey);
        params.put("sign", sign);

        log.info("BcatPay 创建代付订单请求参数: {}", JSON.toJSONString(params));

        JSONObject resultJson = doBcatPostFormRequest(domainName + remitUrl, params);
        log.info("BcatPay 创建代付订单返回: {}", resultJson.toJSONString());

        Integer code = resultJson.getInteger("code");
        if (codeSuccess.equals(code)) {
            WithdrawResultDo withdrawResultDo = new WithdrawResultDo();
            WithdrawResultDo.Result result = new WithdrawResultDo.Result();
            result.setOrderNo(orderNo);
            result.setUpOrderNo(resultJson.getString("ordersn"));
            result.setUpInfo(resultJson.toJSONString());
            result.setCurrencyId(PayConst.currency_id);
            result.setAmount(withdrawOrder.getAmount());
            result.setCreateTime(System.currentTimeMillis());
            withdrawResultDo.setData(result);
            withdrawResultDo.setMsg("成功");
            return withdrawResultDo;
        } else {
            String msg = resultJson.getString("msg");
            String errLog = StrUtil.format("商户编码: {}, BcatPay 代付异常: msg={}",
                     code, msg);
            errorTelegramUtil.dealErrorMsg(errLog);
            throw new MyPayException(StrUtil.isNotBlank(msg) ? msg : MessagesUtils.get("bot.pay.ZFYC"));
        }
    }

    @Override
    public JSONObject callbackWithdrawParam(MerchantPay merchantPay, OrderLawWithdraw orderLawWithdraw) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ordersn", orderLawWithdraw.getOrderNo());
        return jsonObject;
    }

    @Override
    public PayCallbackResult callbackWithdraw(MerchantPay merchantPay, JSONObject notifyData) {
        try {
            String orderNo = notifyData.getString("ordersn");
            String payParam = merchantPay.getParamStr();
            List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
            String withdrawKey = filterByApp(walletParams, "withdrawMerchantKey");
            if (StrUtil.isBlank(withdrawKey)) {
                withdrawKey = filterByApp(walletParams, "merchantKey");
            }

            // 1. 验签
            String receivedSign = notifyData.getString("sign");
            Map<String, Object> callbackParams = new HashMap<>();
            notifyData.forEach(callbackParams::put);
            if (!verifyBcatPayNotify(callbackParams, receivedSign, withdrawKey)) {
                log.error("BcatPay 提现回调 - 签名验证失败, orderNo: {}", orderNo);
                return PayCallbackResult.failed("sign error");
            }

            // 2. 主动查单确认
            QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
            queryOrderInfo.setOrderNo(orderNo);
            queryOrderInfo.setPayParam(payParam);
            OrderInfoDo orderInfoDo = queryWithdrawOrder(queryOrderInfo);
            if (orderInfoDo != null && orderInfoDo.getData() != null) {
                OrderInfoDo.OrderInfo info = orderInfoDo.getData();
                log.info("BcatPay 提现回调查单: orderNo={}, status={}", orderNo, info.getOrderStatus());
                return PayCallbackResult.builder()
                        .orderNo(orderNo)
                        .merchantOrderNo(notifyData.getString("platformSn"))
                        .orderStatus(info.getOrderStatus())
                        .responseMsg("SUCCESS")
                        .isSuccess(true)
                        .build();
            }
            return PayCallbackResult.failed("query fail");
        } catch (Exception e) {
            log.error("BcatPay 提现回调异常", e);
            return PayCallbackResult.failed("exception");
        }
    }

    @Override
    public JSONObject reverseCheckOrderWithdraw(MerchantPay merchantPay, Map<String, Object> requestMap) {
        return null;
    }

    // ========================= 工具方法 =========================

    /**
     * BcatPay POST form-urlencoded 请求
     */
    public static JSONObject doBcatPostFormRequest(String url, Map<String, Object> params) {
        return PayWService.doPostFormJson(url, params, 10000, "BcatPay");
    }

    /**
     * BcatPay 签名算法
     * 参数按ASCII升序排列（排除sign和空值），末尾追加&key=密钥，MD5小写
     */
    public static String signBcatPay(Map<String, Object> params, String signKey) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        if (StrUtil.isBlank(signKey)) {
            throw new IllegalArgumentException("BcatPay 签名密钥不能为空");
        }
        TreeMap<String, Object> sortedMap = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : sortedMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if ("sign".equals(key)) continue;
            if (value == null || "".equals(value)) continue;
            if (sb.length() > 0) sb.append("&");
            sb.append(key).append("=").append(value);
        }
        sb.append("&key=").append(signKey);
        String signStr = sb.toString();
        log.info("BcatPay 签名字符串: {}", signStr);
        return MD5Util.getStr(signStr); // 小写MD5
    }

    /**
     * BcatPay 回调验签
     */
    public static boolean verifyBcatPayNotify(Map<String, Object> params, String receivedSign, String signKey) {
        if (StrUtil.isBlank(receivedSign)) return false;
        String calculatedSign = signBcatPay(params, signKey);
        log.debug("BcatPay 验签 - 接收: {}, 计算: {}", receivedSign, calculatedSign);
        return receivedSign.equalsIgnoreCase(calculatedSign);
    }
}
