package com.gp.service.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.hutool.core.util.StrUtil;
import cn.licoy.encryptbody.annotation.encrypt.AESEncryptBody;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.common.core.exception.ServiceException;
import com.common.core.log.TelegramUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.enums.CodeEnum;
import com.gp.common.base.exception.*;
import com.gp.common.base.result.ApiResult;
import com.gp.common.base.utils.AssertUtil;
import com.gp.common.base.utils.MessagesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.annotation.Resource;
import javax.security.auth.login.AccountExpiredException;
import javax.servlet.http.HttpServletRequest;
import java.nio.file.AccessDeniedException;

/**
 * 全局异常处理器
 *
 * @author ruoyi
 */
@RestControllerAdvice
@AESEncryptBody
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @Resource
    private TelegramUtil telegramUtil;

    @ExceptionHandler(NoHandlerFoundException.class)

    public ApiResult handlerNoFoundException(Exception e) {
        log.error(e.getMessage(), e);

        return ApiResult.error(AssertUtil.getCode(), MessagesUtils.get("path.not.exist"));
    }

    @ExceptionHandler(ParamFlowException.class)
    public ApiResult handleParamFlowException(ParamFlowException e) {
//        log.error(e.getMessage());
        return ApiResult.error(AssertUtil.getCode(), MessagesUtils.get("system.busy"));
    }


    @ExceptionHandler(AccessDeniedException.class)

    public ApiResult handleAuthorizationException(AccessDeniedException e) {
        log.error(e.getMessage());
        return ApiResult.error(AssertUtil.getCode(), MessagesUtils.get("no.permission"));
    }

    @ExceptionHandler(AccountExpiredException.class)

    public ApiResult handleAccountExpiredException(AccountExpiredException e) {
        log.error(e.getMessage(), e);
        return ApiResult.error(AssertUtil.getCode(), e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)

    public ApiResult handleBusinessException(BusinessException e) {
        log.error(e.getMessage());
        return ApiResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(VerificationException.class)
    public ApiResult handleVerificationException(VerificationException e, HttpServletRequest request) {
        return ApiResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)

    public ApiResult handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage());
        return ApiResult.error(AssertUtil.getCode(), e.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    public ApiResult handleDataAccessException(DataAccessException e, HttpServletRequest request) {
        log.error(e.getMessage());
        //处理发送机器人
        telegramUtil.dealMsg(e, request);
        return ApiResult.error(AssertUtil.getCode(), MessagesUtils.get("system.busy"));
    }


    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)

    public ApiResult validatedBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return ApiResult.error(AssertUtil.getCode(), message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BizRuntimeException.class)

    public ApiResult handleBizRuntimeException(BizRuntimeException e) {
        log.error(e.getMessage(), e);
        return ApiResult.error(AssertUtil.getCode(), e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)

    public ApiResult validExceptionHandler(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return ApiResult.error(AssertUtil.getCode(), message);
    }

    @ExceptionHandler(ServiceException.class)
    public ApiResult handleServiceException(ServiceException e) {
        return ApiResult.error(e.getCode(), e.getMessage());
    }


    /**
     * 自定义验证异常
     */
    @ExceptionHandler(NotLoginException.class)
    public ApiResult notLoginExceptionHandler(NotLoginException e, HttpServletRequest request) {
        String language = request.getHeader("Accept-Language");
        if(StrUtil.isNotEmpty(language)){
            MessagesUtils.setLang(language);
        }
        CodeEnum codeEnum = CodeEnum.getCodeEnum(Integer.parseInt(e.getType()));
        if (codeEnum == null) {
            return ApiResult.error(e.getMessage());
        } else {
            return ApiResult.error(codeEnum.getCode(),MessagesUtils.get(codeEnum.getMessage()));
        }
    }


    /**
     * 自定义验证异常
     */
    @ExceptionHandler(AssertException.class)
    public ApiResult assertExceptionHandler(AssertException e) {
        return ApiResult.error(e.getCode(), e.getMsg());
    }


    /**
     * hutool参数验证异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResult assertIllegalArgumentException(IllegalArgumentException e) {
        return ApiResult.error(CodeEnum.Error.getCode(), e.getMessage());
    }


    /**
     * 自定义验证异常
     */
    @ExceptionHandler(GameException.class)
    public ApiResult GameExceptionHandler(GameException e, HttpServletRequest request) {
        telegramUtil.dealMsg(e, request);
        return ApiResult.error(e.getCode(), e.getMessage());
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(GameLoginException.class)
    public ApiResult GameLoginExceptionHandler(GameLoginException e, HttpServletRequest request) {
        telegramUtil.dealMsg(e, request);
        return ApiResult.error(MessagesUtils.get("game.url.error"));
    }

    /**
     * 最终异常
     *
     * @param e
     * @return {@link ApiResult}
     */
    @ExceptionHandler(Exception.class)
    public ApiResult handleException(Exception e, HttpServletRequest request) {
        telegramUtil.dealMsg(e, request);
        return ApiResult.error(AssertUtil.getCode(), e.getMessage());
    }

    @ExceptionHandler(WalletException.class)
    public ApiResult handleWalletException(WalletException e, HttpServletRequest request) {
        telegramUtil.dealG(e.getUserId());
        return ApiResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(ConfigException.class)
    public ApiResult handleConfigException(ConfigException e, HttpServletRequest request) {
        telegramUtil.dealConfigKey(e.getCongfigKey());
        return ApiResult.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(ConfigPriceException.class)
    public ApiResult handleConfigPriceException(ConfigPriceException e, HttpServletRequest request) {
        telegramUtil.dealWarnMsg(e.getMessage() + e.getInfo()+returnMsg());
        return ApiResult.error(e.getCode(), e.getMessage());
    }

    public  String returnMsg(){
        String dbCode = CecuUtil.getDbCode();
        return "此产品编码dbCode为："+dbCode;
    }

}
