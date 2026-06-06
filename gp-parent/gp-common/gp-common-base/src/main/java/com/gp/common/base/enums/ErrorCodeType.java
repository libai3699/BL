package com.gp.common.base.enums;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.gp.common.base.context.ResultCodeLevel;
import com.gp.common.base.context.ResultCodeType;

/**
 * 异常枚举
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
public enum ErrorCodeType {

    //前端显示的默认异常
    DEFAULT_EXCP(ResultCodeLevel.WARN, ResultCodeType.SYS_EXCP, "9002", "System Is Busy, Try Again Later", "系统繁忙,稍后再试"),

    ;


    private String codeLevel;
    private String codeType;
    private String code;
    private String message;
    private String desc;

    ErrorCodeType(String codeLevel, String codeType, String code, String message, String desc) {
        this.codeLevel = codeLevel;
        this.codeType = codeType;
        this.code = code;
        this.message = message;
        this.desc = desc;
    }

    public String getCodeLevel() {
        return codeLevel;
    }

    public void setCodeLevel(String codeLevel) {
        this.codeLevel = codeLevel;
    }

    public String getCodeType() {
        return codeType;
    }

    public void setCodeType(String codeType) {
        this.codeType = codeType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static void main(String[] args) {
        DateTime time = DateUtil.parse("2022-06-24");
        String format = DateUtil.format(time, "yyyy-MM-dd HH:mm:ss");
        System.out.println(format);
    }
}
