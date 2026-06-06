package com.gp.common.base.exception;

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
public class GameException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 余额不足
     */
    public static final Integer lowFunds = 301;
    /**
     * 操作失败
     */
    public static final Integer operationFailed = 302;

    /**
     * 异常状态码
     */
    private Integer code = -1;

    /**自定义message内容*/
    private String msg;

    public GameException(String message) {
        super(message);
        this.msg=message;
        this.code = -1;
    }
    public GameException(Integer code, String message) {
        super(message);
        this.msg=message;
        this.code = code;
    }
}
