package com.gp.common.base.result;

import com.gp.common.base.enums.CodeEnum;
import com.gp.common.base.utils.MessagesUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 操作消息提醒
 *
 * @author axing
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("返回对象")
public class ApiResult<T>
{
    private static final long serialVersionUID = 1L;


    @ApiModelProperty("状态码")
    private Integer code;
    @ApiModelProperty("返回内容")
    private String msg;
    @ApiModelProperty("数据对象")
    private T data;


    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg 返回内容
     */
    public ApiResult(int code, String msg)
    {
        this.code = code;
        this.msg = msg;
    }


    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static ApiResult success()
    {
        return ApiResult.successMsg(MessagesUtils.get(CodeEnum.OK.getMessage()));
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static <T> ApiResult<T> success(T data)
    {
        return ApiResult.success(MessagesUtils.get(CodeEnum.OK.getMessage()), data);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static ApiResult<Void> successMsg(String msg)
    {
        return ApiResult.success(msg, null);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static <T> ApiResult<T> success(String msg, T data)
    {
        return new ApiResult<>(CodeEnum.OK.getCode(), msg, data);
    }

    /**
     * 返回错误消息
     *
     * @return
     */
    public static ApiResult error()
    {
        return ApiResult.error(MessagesUtils.get(CodeEnum.Fail.getMessage()));
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static ApiResult error(String msg)
    {
        return ApiResult.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static <T> ApiResult<T> error(String msg, T data)
    {
        return new ApiResult<>(CodeEnum.Error.getCode(), msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg 返回内容
     * @return 警告消息
     */
    public static ApiResult error(int code, String msg)
    {
        return new ApiResult<>(code, msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg 返回内容
     * @return 警告消息
     */
    public static <T> ApiResult error(int code, String msg, T data)
    {
        return new ApiResult<>(code, msg, data);
    }
}
