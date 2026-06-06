package com.gp.common.mybatisplus.merchantpay.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.common.core.log.ErrorTelegramUtil;
import com.gp.common.base.exception.MyPayException;
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
import com.gp.common.mybatisplus.pay.mpay.order.CreateOrder;
import com.gp.common.mybatisplus.pay.mpay.order.OrderInfoDo;
import com.gp.common.mybatisplus.pay.mpay.order.PayCallbackResult;
import com.gp.common.mybatisplus.pay.mpay.order.QueryOrderInfo;
import com.gp.common.mybatisplus.pay.mpay.order.QueryRechargeOrder;
import com.gp.common.mybatisplus.pay.mpay.order.RechargeOrderInfoDo;
import com.gp.common.mybatisplus.pay.mpay.order.WithdrawOrder;
import com.gp.common.mybatisplus.pay.mpay.order.WithdrawResultDo;
import com.gp.common.mybatisplus.until.RequestUntil;
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
 * FLL 钱包支付:
 * - 充值下单: /api/gate/pub/paylink
 * - 代付下单: /api/gate/pub/transfer
 * - 代付查单: /api/gate/pub/transfer_query
 * 签名规则: 参数按 key 排序后拼接，末尾拼接 key=密钥，再做 md5 小写。
 */
@Slf4j
@Service
public class FllPayService extends PayWService {

    /** 默认网关域名（后台未配置 domainName 时兜底使用）。 */
    private static final String DEFAULT_BASE_URL = "https://aliyun.xg805.com";
    /** 充值下单接口：创建支付链接。 */
    private static final String PATH_PAY_LINK = "/api/gate/pub/paylink";
    /** 商户信息接口：用于查询余额。 */
    private static final String PATH_INFO = "/api/gate/pub/info";
    /** 代付下单接口：发起转账。 */
    private static final String PATH_TRANSFER = "/api/gate/pub/transfer";
    /** 代付查单接口：查询转账状态。 */
    private static final String PATH_TRANSFER_QUERY = "/api/gate/pub/transfer_query";
    /** 上游统一成功码。 */
    private static final String SUCCESS_CODE = "20000";

    @Resource
    private ErrorTelegramUtil errorTelegramUtil;

    @Override
    protected void doRegister() {
        registerPayWService(CollUtil.newArrayList(PayMerchantCons.FLL_PAY));
    }

    @Override
    public JSONObject getNotifyRequestParam(HttpServletRequest request) {
        try {
            // FLL 回调优先走 query 参数（文档是 GET 回调），兼容 JSON body 兜底。
            Map<String, String> paramMap = RequestUntil.doParamCompatible(request);
            if (paramMap != null && !paramMap.isEmpty()) {
                return JSON.parseObject(JSON.toJSONString(paramMap));
            }
            String body = RequestUntil.readRequestBody(request);
            if (StrUtil.isNotBlank(body)) {
                return JSON.parseObject(body);
            }
        } catch (Exception e) {
            log.error("FLLPay 获取回调参数失败", e);
        }
        return new JSONObject();
    }

    @Override
    public String queryMoney(MerchantPay merchantPay) {
        try {
            Config cfg = parseConfig(merchantPay.getParamStr());
            Map<String, Object> req = new TreeMap<>();
            req.put("app_id", cfg.appId);
            req.put("sign", sign(req, cfg.key));
            JSONObject resp = doPostJson(cfg.baseUrl + PATH_INFO, req);
            if (isSuccess(resp)) {
                JSONObject data = resp.getJSONObject("data");
                return data != null ? data.getString("balance") : "0";
            }
            return "查询失败";
        } catch (Exception e) {
            log.error("FLLPay 查询余额异常", e);
            return "查询失败";
        }
    }

    @Override
    public OrderInfoDo queryOrder(QueryOrderInfo queryOrderInfo) {
        // 代收不支持主动查单：上游未提供充值查单接口，仅依赖回调或后台人工补单。
        log.info("FLLPay 充值查单不支持，返回处理中: orderNo={}", queryOrderInfo.getOrderNo());
        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
        orderInfo.setCurrencyId(PayConst.currency_id);
        orderInfo.setOrderStatus(0);
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    @Override
    public WalletCreateOrderResult createOrder(CreateOrder createOrder) {
        try {
            Config cfg = parseConfig(createOrder.getPayParam());
            // 充值参数必须按文档字段名传递，签名不包含 sign 本身。
            Map<String, Object> req = new TreeMap<>();
            req.put("app_id", cfg.appId);
            req.put("biz_order_id", createOrder.getOrderNo());
            req.put("amount", createOrder.getOriginalAmount().setScale(2, RoundingMode.DOWN).toPlainString());
            req.put("desc", StrUtil.blankToDefault(createOrder.getRemark(), "recharge"));
            req.put("return_url", cfg.returnUrl);
            req.put("coin", cfg.coin);
            req.put("sign", sign(req, cfg.key));

            JSONObject resp = doPostJson(cfg.baseUrl + PATH_PAY_LINK, req);
            if (!isSuccess(resp)) {
                String msg = "FLLPay 充值下单失败: " + resp;
                errorTelegramUtil.dealErrorMsg(msg);
                throw new MyPayException(MessagesUtils.get("bot.pay.ZFYC"));
            }

            JSONObject data = resp.getJSONObject("data");
            if (data == null || StrUtil.isBlank(data.getString("pay_url"))) {
                throw new MyPayException("FLLPay 下单成功但未返回 pay_url");
            }

            WalletCreateOrderResult result = new WalletCreateOrderResult();
            result.setUrl(data.getString("pay_url"));
            result.setUpOrderNo(data.getString("order_id"));
            result.setUpInfo(resp.toJSONString());
            return result;
        } catch (MyPayException e) {
            throw e;
        } catch (Exception e) {
            log.error("FLLPay 充值下单异常", e);
            throw new MyPayException(MessagesUtils.get("bot.pay.ZFYC"));
        }
    }

    @Override
    public JSONObject callbackPayParam(MerchantPay merchantPay, OrderAmount orderAmount) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("biz_order_id", orderAmount.getOrderNo());
        return jsonObject;
    }

    @Override
    public PayCallbackResult callbackPay(MerchantPay merchantPay, JSONObject notifyData) {
        try {
            String bizOrderId = notifyData.getString("biz_order_id");
            String receivedSign = notifyData.getString("sign");
            if (StrUtil.isBlank(bizOrderId)) {
                return PayCallbackResult.failed("fail");
            }
            // 后台补单(checkStatus)只会传 biz_order_id，不带 sign；FLL 又无充值查单接口，这里按人工补单放行。
            if (StrUtil.isBlank(receivedSign)) {
                log.warn("FLLPay 充值回调无签名，按后台人工补单处理: biz_order_id={}", bizOrderId);
                return PayCallbackResult.builder()
                        .orderNo(bizOrderId)
                        .merchantOrderNo(bizOrderId)
                        .orderStatus(1)
                        .responseMsg("success")
                        .isSuccess(true)
                        .build();
            }
            Config cfg = parseConfig(merchantPay.getParamStr());
            // 文档特别说明充值回调验签固定为: md5("biz_order_id=xxx&key=密钥")
            String expected = DigestUtil.md5Hex("biz_order_id=" + bizOrderId + "&key=" + cfg.key).toLowerCase();
            if (!expected.equalsIgnoreCase(receivedSign)) {
                log.error("FLLPay 充值回调验签失败 expected={}, received={}", expected, receivedSign);
                return PayCallbackResult.failed("fail");
            }
            return PayCallbackResult.builder()
                    .orderNo(bizOrderId)
                    .merchantOrderNo(bizOrderId)
                    .orderStatus(1)
                    .responseMsg("ok")
                    .isSuccess(true)
                    .build();
        } catch (Exception e) {
            log.error("FLLPay 充值回调异常", e);
            return PayCallbackResult.failed("fail");
        }
    }

    @Override
    public WithdrawResultDo withdraw(WithdrawOrder withdrawOrder) {
        try {
            Config cfg = parseConfig(withdrawOrder.getPayParam());

            // 收款 TGID 优先从 bindData 取，兼容历史 withdrawParam 字段。
            String recvTgId = resolveRecvTgId(withdrawOrder);
            if (StrUtil.isBlank(recvTgId)) {
                throw new MyPayException("FLLPay 代付缺少 recv_tgid");
            }

            Map<String, Object> req = new TreeMap<>();
            req.put("app_id", cfg.appId);
            req.put("biz_order_id", withdrawOrder.getOrderNo());
            req.put("desc", "withdraw");
            req.put("amount", withdrawOrder.getAmount().setScale(2, RoundingMode.DOWN).toPlainString());
            // 文档 recv_tgid 为 Long 类型；这里尽量传数值，避免上游严格类型校验导致 internel error。
            Object recvTgIdVal = recvTgId;
            if (StrUtil.isNumeric(recvTgId)) {
                try {
                    recvTgIdVal = Long.parseLong(recvTgId);
                } catch (Exception ignored) {
                    recvTgIdVal = recvTgId;
                }
            }
            req.put("recv_tgid", recvTgIdVal);
            req.put("coin", cfg.coin);
            req.put("sign", sign(req, cfg.key));

            JSONObject resp = doPostJson(cfg.baseUrl + PATH_TRANSFER, req);
            if (!isSuccess(resp)) {
                String msg = "FLLPay 代付下单失败: code=" + (resp != null ? resp.getString("code") : "null")
                        + ", msg=" + (resp != null ? resp.getString("msg") : "null")
                        + ", body=" + resp;
                errorTelegramUtil.dealErrorMsg(msg);
                throw new MyPayException(MessagesUtils.get("bot.pay.TXSB"));
            }

            WithdrawResultDo resultDo = new WithdrawResultDo();
            WithdrawResultDo.Result result = new WithdrawResultDo.Result();
            result.setOrderNo(withdrawOrder.getOrderNo());
            result.setUpOrderNo(withdrawOrder.getOrderNo());
            result.setAmount(withdrawOrder.getAmount());
            result.setCurrencyId(PayConst.currency_id);
            result.setUpInfo(resp.toJSONString());
            resultDo.setCode(0);
            resultDo.setData(result);
            resultDo.setMsg("success");
            return resultDo;
        } catch (MyPayException e) {
            throw e;
        } catch (Exception e) {
            log.error("FLLPay 代付异常", e);
            throw new MyPayException(MessagesUtils.get("bot.pay.TXSB"));
        }
    }

    @Override
    public JSONObject callbackWithdrawParam(MerchantPay merchantPay, OrderLawWithdraw orderLawWithdraw) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("biz_order_id", orderLawWithdraw.getOrderNo());
        return jsonObject;
    }

    @Override
    public PayCallbackResult callbackWithdraw(MerchantPay merchantPay, JSONObject notifyData) {
        try {
            String bizOrderId = notifyData.getString("biz_order_id");
            String receivedSign = notifyData.getString("sign");
            if (StrUtil.isBlank(bizOrderId)) {
                return PayCallbackResult.failed("fail");
            }

            // 后台补单(checkStatus)不带 sign，需主动查单判断终态，避免误判。
            if (StrUtil.isBlank(receivedSign)) {
                QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
                queryOrderInfo.setOrderNo(bizOrderId);
                queryOrderInfo.setPayParam(merchantPay.getParamStr());
                OrderInfoDo orderInfoDo = queryWithdrawOrder(queryOrderInfo);
                if (orderInfoDo == null || orderInfoDo.getData() == null) {
                    return PayCallbackResult.failed("fail");
                }
                int status = orderInfoDo.getData().getOrderStatus();
                if (status == 1 || status == 2) {
                    return PayCallbackResult.builder()
                            .orderNo(bizOrderId)
                            .merchantOrderNo(bizOrderId)
                            .orderStatus(status)
                            .responseMsg("success")
                            .isSuccess(true)
                            .build();
                }
                return PayCallbackResult.failed("fail");
            }

            Config cfg = parseConfig(merchantPay.getParamStr());
            // 代付回调签名规则与请求一致，但 action 字段明确不参与签名。
            if (!verifySign(notifyData, cfg.key, CollUtil.newHashSet("sign", "action"))) {
                return PayCallbackResult.failed("fail");
            }

            Integer status = notifyData.getInteger("status");
            int orderStatus = 0;
            if (status != null && status == 1) {
                orderStatus = 1;
            } else if (status != null && status == 2) {
                orderStatus = 2;
            }
            if (orderStatus == 0) {
                return PayCallbackResult.failed("fail");
            }
            return PayCallbackResult.builder()
                    .orderNo(bizOrderId)
                    .merchantOrderNo(bizOrderId)
                    .orderStatus(orderStatus)
                    .responseMsg("ok")
                    .isSuccess(true)
                    .build();
        } catch (Exception e) {
            log.error("FLLPay 代付回调异常", e);
            return PayCallbackResult.failed("fail");
        }
    }

    @Override
    public JSONObject reverseCheckOrderWithdraw(MerchantPay merchantPay, Map<String, Object> requestMap) {
        return null;
    }

    @Override
    public OrderInfoDo queryWithdrawOrder(QueryOrderInfo queryOrderInfo) {
        try {
            Config cfg = parseConfig(queryOrderInfo.getPayParam());
            Map<String, Object> req = new TreeMap<>();
            req.put("app_id", cfg.appId);
            req.put("biz_order_id", queryOrderInfo.getOrderNo());
            req.put("sign", sign(req, cfg.key));
            JSONObject resp = doPostJson(cfg.baseUrl + PATH_TRANSFER_QUERY, req);

            OrderInfoDo orderInfoDo = new OrderInfoDo();
            OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
            orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
            orderInfo.setCurrencyId(PayConst.currency_id);

            int status = 0;
            if (isSuccess(resp)) {
                JSONObject data = resp.getJSONObject("data");
                Integer upStatus = data == null ? null : data.getInteger("status");
                // 上游: 1=成功, 2=订单不存在(需重提)，映射到系统: 1=成功, 2=失败。
                if (upStatus != null && upStatus == 1) {
                    status = 1;
                } else if (upStatus != null && upStatus == 2) {
                    status = 2;
                }
            }
            orderInfo.setOrderStatus(status);
            orderInfoDo.setData(orderInfo);
            return orderInfoDo;
        } catch (Exception e) {
            log.error("FLLPay 代付查单异常", e);
            OrderInfoDo orderInfoDo = new OrderInfoDo();
            OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
            orderInfo.setOrderNo(queryOrderInfo.getOrderNo());
            orderInfo.setOrderStatus(0);
            orderInfoDo.setData(orderInfo);
            return orderInfoDo;
        }
    }

    @Override
    public RechargeOrderInfoDo.RechargeOrderInfo queryRechargeOrder(QueryRechargeOrder queryRechargeOrder) {
        return null;
    }

    private static String resolveRecvTgId(WithdrawOrder withdrawOrder) {
        if (withdrawOrder == null) {
            return "";
        }
        // 优先用订单上的用户TGID（最稳定，避免绑定参数缺失/脏数据）。
        if (withdrawOrder.getTgUserId() != null) {
            return String.valueOf(withdrawOrder.getTgUserId());
        }
        Map<String, Object> bindData = withdrawOrder.getBindData();
        Map<String, Object> withdrawParam = withdrawOrder.getWithdrawParam();
        String recvTgId = PayBindDataKeyEnum.ACCOUNT.getValue(bindData);
        if (StrUtil.isBlank(recvTgId)) {
            recvTgId = PayBindDataKeyEnum.ACCOUNT.getValue(withdrawParam);
        }
        if (StrUtil.isBlank(recvTgId) && bindData != null && bindData.get("tgId") != null) {
            recvTgId = String.valueOf(bindData.get("tgId"));
        }
        if (StrUtil.isBlank(recvTgId) && withdrawParam != null && withdrawParam.get("recv_tgid") != null) {
            recvTgId = String.valueOf(withdrawParam.get("recv_tgid"));
        }
        return recvTgId;
    }

    private static JSONObject doPostJson(String url, Object body) {
        return PayWService.doPostJsonJson(url, body, 20000, "FLLPay");
    }

    private static String normalizeBaseUrl(String domainName) {
        if (StrUtil.isBlank(domainName)) {
            return DEFAULT_BASE_URL;
        }
        String s = domainName.trim();
        while (s.endsWith("/")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    private static boolean isSuccess(JSONObject resp) {
        if (resp == null) {
            return false;
        }
        return SUCCESS_CODE.equals(resp.getString("code"))
                || (resp.getInteger("code") != null && resp.getInteger("code") == 20000);
    }

    private static String sign(Map<String, Object> params, String key) {
        Map<String, Object> sorted = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder();
        // 统一签名实现: 忽略 sign/空值，按 key 字典序拼接后追加 key=密钥。
        for (Map.Entry<String, Object> entry : sorted.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();
            if ("sign".equalsIgnoreCase(k)) {
                continue;
            }
            if (v == null) {
                continue;
            }
            String sv = String.valueOf(v);
            if (StrUtil.isBlank(sv)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(k).append("=").append(sv);
        }
        sb.append("&key=").append(key);
        return DigestUtil.md5Hex(sb.toString()).toLowerCase();
    }

    private static boolean verifySign(JSONObject data, String key, java.util.Set<String> ignoreKeys) {
        Map<String, Object> map = new HashMap<>();
        data.forEach((k, v) -> {
            if (ignoreKeys != null && ignoreKeys.contains(k)) {
                return;
            }
            map.put(k, v);
        });
        String expected = sign(map, key);
        String received = data.getString("sign");
        return StrUtil.isNotBlank(received) && expected.equalsIgnoreCase(received);
    }

    private static Config parseConfig(String payParam) {
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        // 参数名做了多别名兼容，避免后台配置项命名历史差异导致运行时失败。
        String appId = firstNotBlank(
                filterByApp(walletParams, "app_id"),
                filterByApp(walletParams, "appId"),
                filterByApp(walletParams, "merchatCode"));
        String key = firstNotBlank(
                filterByApp(walletParams, "merchantKey"),
                filterByApp(walletParams, "key"),
                filterByApp(walletParams, "appSecret"));
        String baseUrl = normalizeBaseUrl(firstNotBlank(
                filterByApp(walletParams, "domainName"),
                filterByApp(walletParams, "baseUrl")));
        String returnUrl = firstNotBlank(
                filterByApp(walletParams, "returnUrl"),
                filterByApp(walletParams, "rechargeCallbackUrl"),
                "https://t.me");
        String coin = firstNotBlank(filterByApp(walletParams, "coin"), "USDT");
        if (StrUtil.hasBlank(appId, key)) {
            throw new MyPayException("FLLPay 配置缺失(app_id/merchantKey)");
        }
        return new Config(baseUrl, appId, key, coin, returnUrl);
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

    private static final class Config {
        private final String baseUrl;
        private final String appId;
        private final String key;
        private final String coin;
        private final String returnUrl;

        private Config(String baseUrl, String appId, String key, String coin, String returnUrl) {
            this.baseUrl = baseUrl;
            this.appId = appId;
            this.key = key;
            this.coin = coin;
            this.returnUrl = returnUrl;
        }
    }
}
