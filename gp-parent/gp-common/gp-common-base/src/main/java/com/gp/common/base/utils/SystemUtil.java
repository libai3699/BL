package com.gp.common.base.utils;

import lombok.Data;

/**
 * 系统参数设置
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Data
public class SystemUtil {
    //系统启动时间
    public static final String appStartupTime = "app.startup.time";

    public static Long getAppStartupTime() {
        return Long.valueOf(System.getProperty(appStartupTime));
    }
}
