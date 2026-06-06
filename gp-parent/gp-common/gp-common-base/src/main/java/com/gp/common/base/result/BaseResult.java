package com.gp.common.base.result;

import com.gp.common.base.context.ErrorContext;
import lombok.Data;

/**
 * 返回结果集基类
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Data
public abstract class BaseResult implements java.io.Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 8835869623271753897L;

    /** 是否业务处理成功 */
    protected boolean         success;

    /** 错误上下文 */
    protected ErrorContext errorContext;

    /**
     * 默认构造函数。
     */
    public BaseResult() {
        success = false;
        errorContext = new ErrorContext();
    }

    /**
     * 构造函数。
     *
     * @param success       业务处理结果
     * @param errorContext  错误上下文
     */
    public BaseResult(boolean success, ErrorContext errorContext) {
        this.success = success;
        this.errorContext = errorContext;
    }

}
