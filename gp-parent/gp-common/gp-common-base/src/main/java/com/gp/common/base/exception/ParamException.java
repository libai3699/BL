package com.gp.common.base.exception;

import com.gp.common.base.model.ExcMessageModel;
import lombok.Data;

import java.io.Serializable;

/**
 * 参数异常
 * @author axing
 * @version 1.0
 * @date 2021/8/19 17:06
 */
@Data
public class ParamException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 异常状态码
     */
    private Integer code;

    private ExcMessageModel excMessageModel;

    /**自定义message内容*/
    private String definedMessage;

    public ParamException(String message) {
        super(message);
        this.definedMessage=message;
        this.code = 500;
    }
    public ParamException(Integer code,String message) {
        super(message);
        this.definedMessage=message;
        this.code = code;
    }

    public ParamException(ExcMessageModel excMessageModel) {
        this.excMessageModel = excMessageModel;
    }

    public ParamException(Throwable cause, String message, ExcMessageModel excMessageModel) {
        super(message, cause);
        this.definedMessage=message;
        this.excMessageModel = excMessageModel;
    }

    public ParamException(String message, ExcMessageModel excMessageModel) {
        super(message);
        this.definedMessage=message;
        this.excMessageModel = excMessageModel;
    }

    public ParamException(Throwable cause, String message) {
        super(message,cause);
        this.definedMessage=message;
    }

    public String getDefinedMessage() {
        if(null==this.definedMessage){
            this.definedMessage="";
        }
        return definedMessage;
    }
}
