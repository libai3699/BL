package com.gp.common.base.result;

import lombok.Data;

/**
 * 标准结果码
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Data
public class ResultCode implements java.io.Serializable {

    /**
     * 序列ID
     */
    private static final long serialVersionUID = 3951948353107763580L;

    /**
     * 结果码固定前缀
     */
    protected static final String PREFIX = "idx_";

    /**
     * 结果码级别，INFO-1,WARN-3,ERROR-5,FATAL-7，参见<code>ResultCodeLevel</code>定义
     */
    private String codeLevel;

    /**
     * 结果码类型，SUCCESS-0,BIZ_ERROR-1,SYS_ERROR-2,THIRD_ERROR-3，参见<code>ResultCodeType</code>定义
     */
    private String codeType;

    /**
     * 系统编号
     */
    private String systemCode = "";

    /**
     * 系统名称
     */
    private String systemName = "";

    /**
     * 具体结果码
     */
    private String errorCode;

    /**
     * 错误英文简称
     */
    private String errorName;

    /**
     * 结果码信息描述，可空
     */
    private String description;

    /**
     * 构造方法。
     *
     * @param codeLevel   结果码级别
     * @param codeType    结果码类型
     * @param errorCode   具体结果码
     * @param errorName   错误英文简称
     * @param description 结果码信息描述
     */
    public ResultCode(String systemName, String codeLevel, String codeType, String errorCode, String errorName, String description) {
        this.systemName = systemName;
        this.codeLevel = codeLevel;
        this.codeType = codeType;
        this.errorCode = errorCode;
        this.errorName = errorName;
        this.description = description;
    }

}
