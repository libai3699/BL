package com.gp.common.mybatisplus.until;

import com.gp.common.mybatisplus.entity.OrderAmount;
import com.gp.common.mybatisplus.entity.OrderLawWithdraw;

/**
 * 订单类操作日志可读摘要（导出范围、审核动作等），充值与法币提现共用内部工具方法。
 */
public final class OrderAmountExportOperLogUtil {

    private OrderAmountExportOperLogUtil() {
    }

    /**
     * 示例：运营-admin -导出了2025-01-01 00:00:00-2025-01-31 23:59:59 充值订单<br>
     * 角色名为空时（如管理员账号未挂角色）默认展示「管理员」。
     */
    public static String buildRechargeExportOperLogDetail(String roleNames, String userName, OrderAmount q) {
        return buildExportRangeDetail(roleNames, userName, resolveRechargeTimeRange(q), "充值订单");
    }

    /**
     * 示例：运营-admin -导出了2025-01-01 00:00:00-2025-01-31 23:59:59 法币提现订单<br>
     * 角色名为空时默认「管理员」。
     */
    public static String buildLawWithdrawExportOperLogDetail(String roleNames, String userName, OrderLawWithdraw q) {
        return buildExportRangeDetail(roleNames, userName, resolveLawWithdrawTimeRange(q), "法币提现订单");
    }

    /**
     * 示例：管理员-admin -订单风险审核 审核通过 (订单号: WDxxx, 用户ID: 456, 用户昵称: 张三)<br>
     * 括号内为订单号与<strong>订单归属终端用户</strong>；用户 ID 亦通过 MDC {@code operBizUserId} 写入 {@code biz_user_id}。
     * 导出接口勿写业务用户相关 MDC。
     *
     * @param orderUserId       订单归属用户 ID（{@code t_user.user_id}）
     * @param orderUserNickname 订单用户展示名（优先飞机名称，否则飞机用户名）
     */
    public static String buildOrderActionOperLogDetail(String roleNames, String userName, String actionTitle,
            String orderNo, String extra, Long orderUserId, String orderUserNickname) {
        String role = nz(roleNames, "管理员");
        String user = nz(userName, "—");
        String on = nz(orderNo, "—");
        StringBuilder sb = new StringBuilder();
        sb.append(role).append("-").append(user).append(" -").append(actionTitle);
        if (extra != null && !extra.trim().isEmpty()) {
            sb.append(" ").append(extra.trim());
        }
        sb.append(" (订单号: ").append(on);
        sb.append(", 用户ID: ").append(orderUserId != null ? orderUserId.toString() : "—");
        sb.append(", 用户昵称: ").append(nz(orderUserNickname, "—")).append(")");
        return sb.toString();
    }

    private static String buildExportRangeDetail(String roleNames, String userName, String[] range,
            String orderKindSuffix) {
        String role = nz(roleNames, "管理员");
        String user = nz(userName, "—");
        String start = range[0];
        String end = range[1];
        return role + "-" + user + " -导出了" + start + "-" + end + " " + orderKindSuffix;
    }

    private static String nz(String s, String fallback) {
        if (s == null) {
            return fallback;
        }
        String t = s.trim();
        return t.isEmpty() ? fallback : t;
    }

    /** 优先下单时间 createTimes，否则支付时间 payTimes；无则 「—」「—」 */
    private static String[] resolveRechargeTimeRange(OrderAmount q) {
        if (q == null) {
            return new String[]{"—", "—"};
        }
        String[] fromCreate = pickPair(q.getCreateTimes());
        if (fromCreate != null) {
            return fromCreate;
        }
        String[] fromPay = pickPair(q.getPayTimes());
        if (fromPay != null) {
            return fromPay;
        }
        return new String[]{"—", "—"};
    }

    /** 优先下单时间 createTimes，其次审核时间 checkTimes、提现时间 withdrawTimes；无则 「—」「—」 */
    private static String[] resolveLawWithdrawTimeRange(OrderLawWithdraw q) {
        if (q == null) {
            return new String[]{"—", "—"};
        }
        String[] fromCreate = pickPair(q.getCreateTimes());
        if (fromCreate != null) {
            return fromCreate;
        }
        String[] fromCheck = pickPair(q.getCheckTimes());
        if (fromCheck != null) {
            return fromCheck;
        }
        String[] fromWithdraw = pickPair(q.getWithdrawTimes());
        if (fromWithdraw != null) {
            return fromWithdraw;
        }
        return new String[]{"—", "—"};
    }

    private static String[] pickPair(String[] arr) {
        if (arr == null || arr.length == 0) {
            return null;
        }
        String a0 = trimToNull(arr[0]);
        if (arr.length >= 2) {
            String a1 = trimToNull(arr[1]);
            if (a0 != null && a1 != null) {
                return new String[]{a0, a1};
            }
            if (a0 != null) {
                return new String[]{a0, a0};
            }
            return null;
        }
        if (a0 != null) {
            return new String[]{a0, a0};
        }
        return null;
    }

    private static String trimToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
