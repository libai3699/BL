package com.gp.common.mybatisplus.pay.constant;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

import java.util.Map;

/**
 * 支付绑定数据Key枚举
 * 用于通用规划 bindData 中的字段提取
 */
@Getter
public enum PayBindDataKeyEnum {

    /** 姓名 */
    PLAYER_NAME("playerName", new String[]{"playerName", "accountName", "realName"}),

    /** 手机号 */
    MOBILE("mobile", new String[]{"mobile", "phone", "phoneNumber"}),

    /** 邮箱 */
    EMAIL("email", new String[]{"email"}),

    /** 银行卡号/账号 */
    ACCOUNT("account", new String[]{"account", "bankAccount", "cardNo", "address", "beneficiary"}),

    /** 银行编码 */
    BANK_CODE("bankCode", new String[]{"bankCode", "bank"}),

    /** 银行支行 */
    BANK_BRANCH("bankBranch", new String[]{"bankBranch"}),

    /** 二维码地址 */
    QR_CODE("qrCode", new String[]{"qrCode"});

    private final String defaultKey;
    private final String[] aliases;

    PayBindDataKeyEnum(String defaultKey, String[] aliases) {
        this.defaultKey = defaultKey;
        this.aliases = aliases;
    }

    /**
     * 从绑定数据中获取值
     * @param bindData 绑定数据Map
     * @return 匹配到的值，若未找到或为空则返回空字符串
     */
    public String getValue(Map<String, Object> bindData) {
        if (bindData == null || bindData.isEmpty()) {
            return "";
        }
        // 1. 尝试默认Key
        Object val = bindData.get(defaultKey);
        if (val != null && StrUtil.isNotBlank(val.toString())) {
            return val.toString();
        }
        // 2. 尝试别名
        if (aliases != null) {
            for (String alias : aliases) {
                val = bindData.get(alias);
                if (val != null && StrUtil.isNotBlank(val.toString())) {
                    return val.toString();
                }
            }
        }
        return "";
    }
}
