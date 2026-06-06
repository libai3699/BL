package com.gp.common.mybatisplus.merchantpay;

import cn.hutool.http.HttpException;
import com.alibaba.fastjson.JSONException;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * 上游支付/提现调用异常分类器 —— 区分"网络/HTTP 不确定异常"与"明确业务失败".
 *
 * <p>不确定异常 (uncertain): 网络抖动 / 超时 / 域名 4xx / 服务端 5xx / 上游响应非 JSON.
 * 此类异常无法判定上游是否真正处理了请求, 调用方应保留下单状态等待对账, 不可直接退款,
 * 否则可能造成"上游已成功 + 本地已退款"的双花.
 *
 * <p>明确失败 (非 uncertain): 上游 HTTP 200 但业务码失败 ({@code MyPayException}) /
 * 我方参数校验失败 / 程序异常等. 调用方应回滚余额, 防止资金挂账.
 */
public final class UpstreamErrorClassifier {

    /**
     * cause 链向上回溯深度上限, 防止异常环导致的无限循环.
     */
    private static final int MAX_CAUSE_DEPTH = 5;

    /**
     * 判定异常是否属于"上游不确定异常".
     *
     * @param e 异常对象 (允许 null)
     * @return true = 不确定 (网络/HTTP 异常, 应保留下单状态); false = 明确失败 (应回滚)
     */
    public static boolean isUncertain(Throwable e) {
        if (e == null) {
            return false;
        }
        Throwable cur = e;
        for (int depth = 0; cur != null && depth < MAX_CAUSE_DEPTH; depth++) {
            if (matches(cur)) {
                return true;
            }
            Throwable next = cur.getCause();
            if (next == cur) {
                break;
            }
            cur = next;
        }
        return false;
    }

    private static boolean matches(Throwable e) {
        if (e instanceof HttpException) {
            return true;
        }
        if (e instanceof SocketTimeoutException
                || e instanceof ConnectException
                || e instanceof UnknownHostException
                || e instanceof NoRouteToHostException) {
            return true;
        }
        if (e instanceof JSONException) {
            // 上游返回非合法 JSON (常见为 4xx HTML 页面 / 5xx 网关错误页面),
            // 无法判定业务结果, 视为不确定.
            return true;
        }
        String msg = e.getMessage();
        if (msg != null) {
            // PayWService.doRequest 把 Hutool HttpException 包装为 RuntimeException, 消息固定包含此片段.
            if (msg.contains("访问异常")) {
                return true;
            }
            // 兜底: 通过消息判断典型网络错误关键词 (Hutool/JDK 不同版本的网络异常文本).
            if (msg.contains("Connection timed out")
                    || msg.contains("Connection refused")
                    || msg.contains("Read timed out")
                    || msg.contains("connect timed out")) {
                return true;
            }
        }
        return false;
    }

    private UpstreamErrorClassifier() {}
}
