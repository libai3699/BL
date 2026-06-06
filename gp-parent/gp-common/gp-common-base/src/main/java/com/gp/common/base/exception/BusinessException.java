package com.gp.common.base.exception;

import com.gp.common.base.enums.CodeEnum;
import com.gp.common.base.model.ExcMessageModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 业务异常
 * @author axing
 * @version 1.0
 * @date 2021/8/19 17:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 异常状态码
     */
    private Integer code;

    private ExcMessageModel excMessageModel;

    /**自定义message内容*/
    private String definedMessage;

    public BusinessException(String message) {
        super(message);
        this.definedMessage=message;
        this.code = -CodeEnum.ERR_MONEY_INSUFFICIENT.getCode();
    }
    public BusinessException(Integer code,String message) {
        super(message);
        this.definedMessage=message;
        this.code = code;
    }

    public BusinessException(ExcMessageModel excMessageModel) {
        this.excMessageModel = excMessageModel;
    }

    public BusinessException(Throwable cause, String message, ExcMessageModel excMessageModel) {
        super(message, cause);
        this.definedMessage=message;
        this.excMessageModel = excMessageModel;
    }

    public BusinessException(String message, ExcMessageModel excMessageModel) {
        super(message);
        this.definedMessage=message;
        this.excMessageModel = excMessageModel;
    }

    public BusinessException(Throwable cause, String message) {
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
