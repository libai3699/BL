package com.gp.common.base.model;

import lombok.*;

/**
 * 异常消息模型
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExcMessageModel {

    /**
     * 错误码级别
     */
    private String codeLevel;
    /**
     * 错误码类型
     */
    private String codeType;
    /**
     * 错误码
     */
    private String code;
    /**
     * 消息内容 EN
     */
    private String message;
    /**
     * 消息内容 CHS
     */
    private String desc;
}
