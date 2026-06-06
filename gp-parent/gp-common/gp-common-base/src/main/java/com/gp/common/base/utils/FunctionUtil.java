package com.gp.common.base.utils;

import com.gp.common.base.function.PureFunction;
import com.gp.common.base.model.LOG;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;


/**
 * 消耗时间统计工具类
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Slf4j
@UtilityClass
public class FunctionUtil {

    public void catchEx(PureFunction function) {
        doFuncNotThrow(function, "catch exception and not throw.");
    }

    public void doFuncNotThrow(PureFunction function, String message) {
        try {
            function.action();
        } catch (Exception e) {
            log.error(message, e);
        }
    }

    @SneakyThrows
    public void counterTime(PureFunction function, String message) {
        try {
            StopWatch sw=new StopWatch("starting counter time for method");
            sw.start(message+" doFunc counter time");
            function.action();
            sw.stop();
            Long tm = sw.getTotalTimeMillis();
            if(tm.compareTo(500L)==1) {
                LogUtil.info(LOG.StopWatch.Log,String.format("[ %s ] time used: [ total: %s ms ] , [ time info: %s ]", message, tm, sw.prettyPrint()));
            }
        } catch (Exception e) {
            log.error("counterTime is error!", e);
            throw e;
        }
    }

}
