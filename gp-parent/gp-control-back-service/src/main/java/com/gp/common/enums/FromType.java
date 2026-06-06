package com.gp.common.enums;

/**
 * 消息审核状态
 *
 * @author ruoyi
 */
public enum FromType
{
    SOSO(0, "搜搜机器人"), GROUP(1, "群组爬虫"),WEB(2, "群组爬虫"),MEMBER(3, "用户上传");

    private final int code;
    private final String info;

    FromType(int code, String info)
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
