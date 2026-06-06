package com.gp.common.base.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StopWatch;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author axing
 * @version 1.0
 * @date 2021/11/8 13:14
 */
@Slf4j
@Data
public class WatchTimeUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    public static <T> T doProcess(Supplier<T> supplier, String watchName) {
        StopWatch sw=new StopWatch("执行时间");
        sw.start(watchName);
        T t = supplier.get();
        sw.stop();
        log.info("\n{}执行时间: {}秒\n",sw.prettyPrint(),sw.getTotalTimeSeconds());
        return t;
    }

    public static <T> void doProcess(Consumer<T> supplier, T t, String watchName) {
        StopWatch sw=new StopWatch("执行时间");
        sw.start(watchName);
        supplier.accept(t);
        sw.stop();
        log.info("\n{}执行时间: {}秒\n",sw.prettyPrint(),sw.getTotalTimeSeconds());
    }

    public static <T> void doProcess(Consumer<T> supplier,String watchName) {
        StopWatch sw=new StopWatch("执行时间");
        sw.start(watchName);
        supplier.accept(null);
        sw.stop();
        log.info("\n{}执行时间: {}秒\n",sw.prettyPrint(),sw.getTotalTimeSeconds());
    }

}
