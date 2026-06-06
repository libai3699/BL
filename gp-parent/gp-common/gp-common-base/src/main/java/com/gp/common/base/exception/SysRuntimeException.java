package com.gp.common.base.exception;

import com.gp.common.base.model.ExcMessageModel;
import lombok.Data;

/**
 * 系统异常
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Data
public class SysRuntimeException extends RuntimeException {

    private ExcMessageModel excMessageModel;

    /**自定义message内容*/
    private String definedMessage;

    public SysRuntimeException(String message) {
        super(message);
        this.definedMessage=message;
    }

    public SysRuntimeException(ExcMessageModel excMessageModel) {
        this.excMessageModel = excMessageModel;
    }

    public SysRuntimeException(Throwable cause, String message, ExcMessageModel excMessageModel) {
        super(message, cause);
        this.definedMessage=message;
        this.excMessageModel = excMessageModel;
    }
    public SysRuntimeException(Throwable cause, ExcMessageModel excMessageModel) {
        super(cause);
        this.excMessageModel = excMessageModel;
    }

    public SysRuntimeException(String message, ExcMessageModel excMessageModel) {
        super(message);
        this.definedMessage=message;
        this.excMessageModel = excMessageModel;
    }

    public SysRuntimeException(Throwable cause, String message) {
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
