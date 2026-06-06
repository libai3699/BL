package com.gp.common.base.constant;

/**
 * 操作日志写入前通过 {@link org.slf4j.MDC} 传递业务上下文，{@code LogAspect} 读出后写入 {@code sys_oper_log}。
 */
public final class OperLogMdcKeys {

    private OperLogMdcKeys() {
    }

    /** 关联业务单号（有则写入 biz_order_no） */
    public static final String BIZ_ORDER_NO = "operBizOrderNo";

    /** 关联业务用户 ID（有则写入 biz_user_id，一般为订单归属终端用户 {@code t_user.user_id}） */
    public static final String BIZ_USER_ID = "operBizUserId";

    /** 可读描述（写入 biz_detail，过长时在切面截断） */
    public static final String BIZ_DETAIL = "operBizDetail";
}
