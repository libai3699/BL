package com.gp.common.base.exception;

import lombok.Data;

import java.io.Serializable;

/**
 * 参数异常
 * @author axing
 * @version 1.0
 * @date 2021/8/19 17:06
 */
@Data
public class AssertException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 异常状态码
     */
    private Integer code;

    /**自定义message内容*/
    private String msg;

    public AssertException(String message) {
        super(message);
        this.msg=message;
        this.code = 500;
    }
    public AssertException(Integer code, String message) {
        super(message);
        this.msg=message;
        this.code = code;
    }

}
