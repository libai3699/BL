package com.gp.common.enums;

/**
 * 消息审核状态
 *
 * @author ruoyi
 */
public enum CheckStatus
{
    NOCHECK(0, "未审核"), PASS(1, "审核通过"), NOTPASS(2, "审核未通过");

    private final int code;
    private final String info;

    CheckStatus(int code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public int getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }
}
