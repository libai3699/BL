package com.gp.framework.web.exception;

import cn.licoy.encryptbody.annotation.encrypt.AESEncryptBody;
import com.common.core.constant.HttpStatus;
import com.common.core.log.TelegramUtil;
import com.common.core.result.AjaxResult;
import com.common.core.util.StringUtils;
import com.gp.common.base.enums.CodeEnum;
import com.gp.common.base.exception.AssertException;
import com.gp.common.base.exception.BusinessException;
import com.gp.common.base.result.ApiResult;
import com.gp.common.base.utils.AssertUtil;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.exception.BaseException;
import com.gp.common.exception.CustomException;
import com.gp.common.exception.DemoModeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 *
 * @author ruoyi
 */
@RestControllerAdvice
@AESEncryptBody
public class GlobalExceptionHandler
{

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Resource
    private TelegramUtil telegramUtil;
    /**
     * 基础异常
     */
    @ExceptionHandler(BaseException.class)
    public AjaxResult baseException(BaseException e)
    {
        return AjaxResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ApiResult handleDataAccessException(DataAccessException e, HttpServletRequest request) {
        log.error(e.getMessage());
        //处理发送机器人
        telegramUtil.dealMsg(e, request);
        return ApiResult.error(AssertUtil.getCode(), MessagesUtils.get("system.busy"));
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(CustomException.class)
    public AjaxResult businessException(CustomException e)
    {
        if (StringUtils.isNull(e.getCode()))
        {
            return AjaxResult.error(e.getMessage());
        }
        return AjaxResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public AjaxResult handlerNoFoundException(Exception e)
    {
        log.error(e.getMessage(), e);
        return AjaxResult.error(HttpStatus.NOT_FOUND, MessagesUtils.get("path.not.exist"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public AjaxResult handleAuthorizationException(AccessDeniedException e)
    {
        log.error(e.getMessage());
        return AjaxResult.error(HttpStatus.FORBIDDEN, MessagesUtils.get("no.permission"));
    }

    @ExceptionHandler(AccountExpiredException.class)
    public AjaxResult handleAccountExpiredException(AccountExpiredException e)
    {
        log.error(e.getMessage(), e);
        return AjaxResult.error(AssertUtil.getCode(), e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public AjaxResult handleUsernameNotFoundException(UsernameNotFoundException e)
    {
        log.error(e.getMessage(), e);
        return AjaxResult.error(AssertUtil.getCode(), e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public AjaxResult validatedBindException(BindException e)
    {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return AjaxResult.error(AssertUtil.getCode(), message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object validExceptionHandler(MethodArgumentNotValidException e)
    {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return AjaxResult.error(AssertUtil.getCode(), message);
    }


    /**
     * 演示模式异常
     */
    @ExceptionHandler(DemoModeException.class)
    public AjaxResult demoModeException(DemoModeException e)
    {
        return AjaxResult.error(AssertUtil.getCode(), MessagesUtils.get("no.operation.allowed"));
    }

    @ExceptionHandler(AssertException.class)
    public ApiResult assertExceptionHandler(AssertException e) {
        log.error(e.getMessage(), e);
        return ApiResult.error(e.getCode(), e.getMsg());
    }


    /**
     * hutool参数验证异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResult assertIllegalArgumentException(IllegalArgumentException e) {
        log.error(e.getMessage(), e);
        return ApiResult.error(CodeEnum.Error.getCode(), e.getMessage());
    }
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResult handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage());
        return ApiResult.error(AssertUtil.getCode(), e.getMessage());
    }
    @ExceptionHandler(BusinessException.class)
    public ApiResult handleBusinessException(BusinessException e) {
        log.error(e.getMessage());
        return ApiResult.error(e.getCode(), e.getMessage());
    }

    /**
     * 最终异常
     * @param e
     * @return {@link AjaxResult}
     */
    @ExceptionHandler(Exception.class)
    public AjaxResult handleException(Exception e, HttpServletRequest request) {
    {
        log.error(e.getMessage(), e);
        telegramUtil.dealMsg(e,request);
        return AjaxResult.error(AssertUtil.getCode(), e.getMessage());
    }

}
}
