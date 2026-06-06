package com.gp.common.base.result;

import com.gp.common.base.enums.ErrorCodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 结果集
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IBaseResult<R> extends BaseResult implements Serializable {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    /**
     * code : 0
     * data : {}
     * message : 请求成功！
     * 正常返回这样的。code 为0是成功，-1失败，-99登录过期未登录， -98第三方登录第一次登录跳转到注册页
     * 20200107： 为20人脸重复签到提醒
     */
    private Integer code = 200;
    private String message = "操作成功";
    private R resultObj;

    /**
     * 成功
     *
     * @param resultObj 结果obj
     * @return {@link IBaseResult}
     */
    public static <R> IBaseResult success(R resultObj) {
        return new IBaseResult(200, "success", resultObj);
    }

    /**
     * 成功
     *
     * @param resultObj 结果obj
     * @return {@link IBaseResult}
     */
    public static <R> IBaseResult success(R resultObj, String message) {
        return new IBaseResult(200, message, resultObj);
    }

    /**
     * 成功
     *
     * @param errorCodeType 错误信息
     * @return {@link IBaseResult}
     */
    public static <R> IBaseResult success(ErrorCodeType errorCodeType, R resultObj) {
        return new IBaseResult(Integer.valueOf(errorCodeType.getCode()), errorCodeType.getMessage(), resultObj);
    }

    /**
     * 错误
     *
     * @param message 错误信息
     * @return {@link IBaseResult}
     */
    public static IBaseResult error(String message) {
        IBaseResult baseResult=new IBaseResult(-1, message, null);
        baseResult.setSuccess(false);
        return baseResult;
    }


    /**
     * 错误
     *
     * @param message 错误信息
     * @return {@link IBaseResult}
     */
    public static <R> IBaseResult error(int code, String message) {
        IBaseResult baseResult=new IBaseResult(code, message, null);
        baseResult.setSuccess(false);
        return baseResult;
    }

    /**
     * 错误
     *
     * @param errorCodeType 错误信息
     * @return {@link IBaseResult}
     */
    public static <R> IBaseResult error(ErrorCodeType errorCodeType, Object resultObj) {
        IBaseResult baseResult=new IBaseResult(Integer.valueOf(errorCodeType.getCode()), errorCodeType.getMessage(), resultObj);
        baseResult.setSuccess(false);
        return baseResult;
    }

    /**
     * 错误-返回数据
     *
     * @return {@link IBaseResult}
     */
    public static <R> IBaseResult errorD(int code, String message, Object resultObj) {
        IBaseResult baseResult=new IBaseResult(code, message, resultObj);
        baseResult.setSuccess(false);
        return baseResult;
    }

}
