package com.common.core.exception;

import com.google.common.annotations.VisibleForTesting;
import com.gp.common.base.enums.CodeEnum;
import com.gp.common.base.utils.MessagesUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@link ServiceException} 工具类
 *
 * 目的在于，格式化异常信息提示。
 *
 *
 * 1. 异常提示信息，写在枚举类中，例如说，cn.iocoder.oceans.user.api.constants.ErrorCodeEnum 类 + ServiceExceptionConfiguration
 * 2. 异常提示信息，写在 .properties 等等配置文件
 * 3. 异常提示信息，写在 Apollo 等等配置中心中，从而实现可动态刷新
 * 4. 异常提示信息，存储在 db 等等数据库中，从而实现可动态刷新
 */
@Slf4j
public class ServiceExceptionUtil {



    public static ServiceException exception(CodeEnum errorCode) {
        return new ServiceException(errorCode.getCode(), MessagesUtils.get(errorCode.getMessage()));
    }




}
