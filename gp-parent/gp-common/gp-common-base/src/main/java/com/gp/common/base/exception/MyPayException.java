package com.gp.common.base.exception;

import lombok.Data;


/**
 * 业务异常
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Data
public class MyPayException extends RuntimeException {
    /**
     * 异常状态码
     */
    private Integer code;

    /**自定义message内容*/
    private String msg;
    private String msgReal;

    public MyPayException(String message) {
        super(message);
        this.msg=message;
        this.code = 500;
    }
    public MyPayException(String message, String msgReal) {
        super(message);
        this.msg=message;
        this.msgReal=msgReal;
        this.code = 500;
    }
    public MyPayException(Integer code, String message) {
        super(message);
        this.msg=message;
        this.code = code;
    }

}
