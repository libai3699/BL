package com.gp.common.base.exception;

import com.gp.common.base.model.ExcMessageModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 配置异常
 * @author axing
 * @version 1.0
 * @date 2021/8/19 17:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConfigException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 异常状态码
     */
    private Integer code;

    private ExcMessageModel excMessageModel;

    /**自定义message内容*/
    private String definedMessage;
    /**配置key*/
    private String congfigKey;

    public ConfigException(String message, String configKey) {
        super(message);
        this.definedMessage=message;
        this.congfigKey=configKey;
        this.code = 500;
    }

    public ConfigException(ExcMessageModel excMessageModel) {
        this.excMessageModel = excMessageModel;
    }

    public ConfigException(Throwable cause, String message, ExcMessageModel excMessageModel) {
        super(message, cause);
        this.definedMessage=message;
        this.excMessageModel = excMessageModel;
    }

    public ConfigException(String message, ExcMessageModel excMessageModel) {
        super(message);
        this.definedMessage=message;
        this.excMessageModel = excMessageModel;
    }

    public ConfigException(Throwable cause, String message) {
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
