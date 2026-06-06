package com.gp.common.base.model;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;


/**
 * 消耗时间日志对象
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@UtilityClass
public class LOG {

    @Slf4j(topic="used_time")
    @UtilityClass
    public class StopWatch {
        public Logger Log = log;
    }
}
