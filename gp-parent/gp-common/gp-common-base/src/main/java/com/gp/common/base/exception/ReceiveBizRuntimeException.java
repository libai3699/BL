package com.gp.common.base.exception;

import com.gp.common.base.model.ExcMessageModel;
import lombok.Data;


/**
 * 业务异常
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Data
public class ReceiveBizRuntimeException extends RuntimeException {

    private ExcMessageModel excMessageModel;

    /**自定义message内容*/
    private String definedMessage;

    public ReceiveBizRuntimeException(String message) {
        super(message);
        this.definedMessage=message;
    }

    public ReceiveBizRuntimeException(ExcMessageModel excMessageModel) {
        this.excMessageModel = excMessageModel;
    }

    public ReceiveBizRuntimeException(Throwable cause, String message, ExcMessageModel excMessageModel) {
        super(message, cause);
        this.definedMessage=message;
        this.excMessageModel = excMessageModel;
    }

    public ReceiveBizRuntimeException(String message, ExcMessageModel excMessageModel) {
        super(message);
        this.definedMessage=message;
        this.excMessageModel = excMessageModel;
    }

    public ReceiveBizRuntimeException(Throwable cause, String message) {
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
