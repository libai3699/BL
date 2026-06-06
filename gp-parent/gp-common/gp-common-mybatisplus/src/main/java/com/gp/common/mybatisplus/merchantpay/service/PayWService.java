package com.gp.common.mybatisplus.merchantpay.service;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.common.core.log.ErrorTelegramUtil;
import com.common.core.prop.ProxyProp;
import com.gp.common.base.exception.BusinessException;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.mybatisplus.entity.MerchantPay;
import com.gp.common.mybatisplus.entity.OrderAmount;
import com.gp.common.mybatisplus.entity.OrderLawWithdraw;
import com.gp.common.mybatisplus.pay.mpay.WalletBase;
import com.gp.common.mybatisplus.pay.mpay.WalletCreateOrderResult;
import com.gp.common.mybatisplus.pay.mpay.WalletParams;
import com.gp.common.mybatisplus.pay.mpay.createAddress.CreateAddress;
import com.gp.common.mybatisplus.pay.mpay.order.*;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class PayWService {

    /**
     * 获取回调请求参数 Map
     * 每个支付平台解析方式不同（JSON, Form-data 等），由具体子类实现
     */
    public abstract JSONObject getNotifyRequestParam(HttpServletRequest request);

    private static Map<String, PayWService> apiMap = new ConcurrentHashMap<>();

    protected PayWService() {
        doRegister();
    }

    @Resource
    private ErrorTelegramUtil errorTelegramUtil;

    /**
     * 注册支付的service
     *
     * @param key channelCode+comCode
     */
    protected void registerPayWService(String key) {
        apiMap.put(key, this);
    }

    /**
     * 注册支付的service
     *
     * @param keys channelCode+comCode
     */
    protected void registerPayWService(List<String> keys) {
        for (String key : keys) {
            apiMap.put(key, this);
        }
    }

    protected abstract void doRegister();
    /**
     * 查询商户余额
     */
    public abstract String queryMoney(MerchantPay merchantPay);


    /**
     * 查询订单详情, 查询注单详情,如果是成功或者失败的,就直接修改订单状态
     *
     */
    public abstract OrderInfoDo queryOrder(QueryOrderInfo queryOrderInfo);

    /**
     * 创建支付订单拿到支付参数
     *
     */
    public abstract WalletCreateOrderResult createOrder(CreateOrder createOrder);

    public abstract JSONObject callbackPayParam(MerchantPay merchantPay, OrderAmount orderAmount);
    /**
     * 充值回调
     */
    public abstract PayCallbackResult callbackPay(MerchantPay merchantPay, JSONObject notifyData);

    /**
     * 提现
     */
    public abstract WithdrawResultDo withdraw(WithdrawOrder withdrawOrder);


    public abstract JSONObject callbackWithdrawParam(MerchantPay merchantPay, OrderLawWithdraw orderLawWithdraw);

    /**
     * 提现回调
     */
    public abstract PayCallbackResult callbackWithdraw(MerchantPay merchantPay, JSONObject notifyData);

    /**
     * 提现反查
     */
    public abstract JSONObject reverseCheckOrderWithdraw(MerchantPay merchantPay, Map<String, Object> requestMap);

    /**
     * 提现订单查询 查询订单所处的状态,然后直接修改订单状态
     */
    public abstract OrderInfoDo queryWithdrawOrder(QueryOrderInfo queryOrderInfo);


    // mpay专属
    public String createAddress(CreateAddress createAddress) {
        return null;
    }

    // mpay专属
    public WithdrawResultDo LWithdraw(WithdrawOrder withdrawOrder) {
        return null;
    }

    // mpay订单
    public RechargeOrderInfoDo.RechargeOrderInfo queryRechargeOrder(QueryRechargeOrder queryRechargeOrder) {
        return null;
    }


    /**
     * 获取支付的service
     *
     * @param payCode
     * @return 对应快递公司的开放服务
     */
    public static PayWService getPayService(String payCode) {
        // 官方渠道需要拼接快递公司编码，第三方渠道统一使用渠道编码即可
        PayWService service = apiMap.get(payCode);
        if (service == null) {
            throw new BusinessException(MessagesUtils.get("bot.pay.WZDZFFF"));
        }
        return service;
    }

    public static String filterByApp(List<WalletParams> walletParams, String key) {
        for (WalletParams param : walletParams) {
            if (key.equals(param.getKey())) {
                // 这里是 key 等于 "appkey" 的操作逻辑
                return param.getValue();
            }
        }
        return "";
    }

    // ============================================================================
    // 上游 HTTP 通用工具方法
    //
    // 设计原则:
    //   1. 三个核心方法 (返回 String): doPostForm / doPostJson / doGet
    //      — 接受 timeoutMs + label, 异常统一抛 RuntimeException("{label} 上游访问异常, ...")
    //      — label 用于日志前缀和异常消息, 与 UpstreamErrorClassifier 配合识别 uncertain 异常.
    //
    //   2. 三个便利方法: doPostFormJson / doPostJsonObj / doGetObj
    //      — 直接解析为 fastjson JSONObject 或泛型 T.
    //
    //   3. 现有方法签名 (doRequest / doGetRequest / doPostFormRequest) 保留,
    //      内部委托新核心方法, 调用方零改动.
    //
    //   4. 各 PayService 子类应优先使用新核心/便利方法, 不再维护私有的 do*Request 副本.
    // ============================================================================

    /** 默认 label, 用于未指定 label 的兼容方法 */
    private static final String DEFAULT_LABEL = "Mywallet";

    /**
     * 通用 POST form-urlencoded.
     *
     * @param url       请求地址
     * @param params    表单参数
     * @param timeoutMs 超时毫秒数
     * @param label     日志/异常前缀 (建议传商户标识, 如 "支付宝支付"、"YeePay")
     * @return 响应体字符串
     */
    public static String doPostForm(String url, Map<String, Object> params, int timeoutMs, String label) {
        log.info("{} POST form url={}, params={}", label, url, JSON.toJSONString(params));
        String result;
        try {
            HttpRequest req = HttpRequest.post(url)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .form(params)
                    .timeout(timeoutMs);
            applyProxy(req);
            result = req.execute().body();
        } catch (Exception e) {
            String errorMsg = StrUtil.format("{} 上游访问异常, 访问地址: {}", label, url);
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
        log.info("{} POST form 返回: {}", label, result);
        return result;
    }

    /**
     * 通用 POST JSON body.
     *
     * @param body 任意 POJO/Map/JSONObject/已序列化字符串, 自动转 JSON 字符串
     */
    public static String doPostJson(String url, Object body, int timeoutMs, String label) {
        return doPostJson(url, body, timeoutMs, label, null);
    }

    /**
     * 通用 POST JSON body, 支持自定义 header.
     */
    public static String doPostJson(String url, Object body, int timeoutMs, String label,
            Map<String, List<String>> headers) {
        String bodyStr = (body instanceof CharSequence) ? body.toString() : JSON.toJSONString(body);
        log.info("{} POST json url={}, body={}", label, url, bodyStr);
        String result;
        try {
            HttpRequest req = HttpRequest.post(url)
                    .header("Content-Type", "application/json")
                    .body(bodyStr)
                    .timeout(timeoutMs);
            if (headers != null && !headers.isEmpty()) {
                req.header(headers);
            }
            applyProxy(req);
            result = req.execute().body();
        } catch (Exception e) {
            String errorMsg = StrUtil.format("{} 上游访问异常, 访问地址: {}", label, url);
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
        log.info("{} POST json 返回: {}", label, result);
        return result;
    }

    /**
     * 通用 GET (query params from JSONObject).
     */
    public static String doGet(String url, JSONObject queryJson, int timeoutMs, String label) {
        log.info("{} GET url={}, params={}", label, url, queryJson == null ? "{}" : queryJson.toJSONString());
        String result;
        try {
            String requestUrl = url;
            if (queryJson != null && !queryJson.isEmpty()) {
                requestUrl = url + "?" + URLUtil.buildQuery(queryJson, CharsetUtil.CHARSET_UTF_8);
            }
            HttpRequest req = HttpRequest.get(requestUrl).timeout(timeoutMs);
            applyProxy(req);
            result = req.execute().body();
        } catch (Exception e) {
            String errorMsg = StrUtil.format("{} 上游访问异常, 访问地址: {}", label, url);
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
        log.info("{} GET 返回: {}", label, result);
        return result;
    }

    /** 应用全局代理配置 (host 为空则直连) */
    private static void applyProxy(HttpRequest req) {
        ProxyProp proxyProp = SpringUtil.getBean(ProxyProp.class);
        if (StrUtil.isNotBlank(proxyProp.getHost())) {
            req.setProxy(new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(proxyProp.getHost(), proxyProp.getPort())));
        }
    }

    // ---------- 便利方法: 直接解析为 JSONObject / 泛型 T ----------
    // 注: 项目内 JSONObject 默认指 fastjson2 (见类顶部 import). 各 PayService 子类同样使用 fastjson2.
    // doPostJsonObj / doGetObj 接收 Class<T>, 走 fastjson 解析为指定 POJO (与 doRequest 兼容方法保持一致).

    /** POST form -> JSONObject (fastjson2) */
    public static JSONObject doPostFormJson(String url, Map<String, Object> params,
            int timeoutMs, String label) {
        return com.alibaba.fastjson2.JSON.parseObject(doPostForm(url, params, timeoutMs, label));
    }

    /** POST JSON -> 泛型 T */
    public static <T> T doPostJsonObj(String url, Object body, int timeoutMs, String label, Class<T> clazz) {
        return JSON.parseObject(doPostJson(url, body, timeoutMs, label), clazz);
    }

    /** POST JSON (带 header) -> 泛型 T */
    public static <T> T doPostJsonObj(String url, Object body, int timeoutMs, String label,
            Map<String, List<String>> headers, Class<T> clazz) {
        return JSON.parseObject(doPostJson(url, body, timeoutMs, label, headers), clazz);
    }

    /** POST JSON -> JSONObject (fastjson2) */
    public static JSONObject doPostJsonJson(String url, Object body, int timeoutMs, String label) {
        return com.alibaba.fastjson2.JSON.parseObject(doPostJson(url, body, timeoutMs, label));
    }

    /** GET -> 泛型 T */
    public static <T> T doGetObj(String url, JSONObject queryJson, int timeoutMs, String label, Class<T> clazz) {
        return JSON.parseObject(doGet(url, queryJson, timeoutMs, label), clazz);
    }

    // ---------- 兼容方法: 保留原有签名, 内部委托新核心方法 (调用方无需改动) ----------

    public static <T> T doRequest(String url, WalletBase walletBase, Class<T> clazz) {
        return doPostJsonObj(url, walletBase, 5000, DEFAULT_LABEL, clazz);
    }

    public static <T> T doRequest(String url, JSONObject jsonObject, Class<T> clazz) {
        return doPostJsonObj(url, jsonObject, 5000, DEFAULT_LABEL, clazz);
    }

    public static <T> T doRequest(String url, JSONObject jsonObject, Map<String, List<String>> headers,
            Class<T> clazz) {
        return doPostJsonObj(url, jsonObject, 10000, DEFAULT_LABEL, headers, clazz);
    }

    public static <T> T doGetRequest(String url, JSONObject jsonObject, Class<T> clazz) {
        return doGetObj(url, jsonObject, 5000, DEFAULT_LABEL, clazz);
    }

    public static <T> T doPostFormRequest(String url, WalletBase walletBase, Class<T> clazz) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = JSON.parseObject(JSON.toJSONString(walletBase), Map.class);
        return JSON.parseObject(doPostForm(url, map, 10000, DEFAULT_LABEL), clazz);
    }

}
