package com.gp.common.base.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 日志打印工具类
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Slf4j
public class LogUtil {

    /**
     * 摘要日志的内容分隔符
     */
    public static final String SEP = ",";

    /**
     * 摘要日志的内容分隔符
     */
    public static final String MARK = " >>>>>>>>>>>>>>>>>>>>> ";

    /**
     * 修饰符
     */
    private static final char RIGHT_TAG = ']';

    /**
     * 修饰符
     */
    private static final char LEFT_TAG = '[';

    /**
     * 完成
     */
    private static final String SUCCESS = " FINISH ";

    public static final String UNIQUE_ID = "traceId";
    public static final String FLAG = "flag";

    public static void createMDC() {
        try {
            if (StringUtils.isEmpty(LogUtil.getMDC())) {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                String uuid = new UUID(random.nextInt(), random.nextInt()).toString();
                MDC.put(UNIQUE_ID, uuid);
            }
        } catch (Exception e) {
            log.warn("createMDC is exception!");
        }
    }

    public static void createMDC(String traceId) {
        try {
            MDC.put(UNIQUE_ID, traceId);
        } catch (Exception e) {
            log.warn("createMDC is exception!");
        }
    }

    public static void setFlag() {
        try {
            MDC.put(FLAG, FLAG);
        } catch (Exception e) {
            log.warn("setFlag is exception!");
        }
    }

    public static String getMDC() {
        try {
            return MDC.get(UNIQUE_ID);
        } catch (Exception e) {
            log.warn("MDC.get(UNIQUE_ID) is exception!");
        }
        return null;
    }

    public static void clearFlag() {
        MDC.remove(FLAG);
    }

    public static void clearMDC() {
        if(StringUtils.isEmpty(MDC.get(FLAG))) {
            MDC.clear();
        }
    }

    /**
     * 打印error日志。
     * <p>
     * ERROR日志记录尽量使用{@link}，避免日志的重复记录
     *
     * @param logger 日志对象
     * @param objs   任意个要输出到日志的参数
     */
    public static void error(Logger logger, Throwable e, Object... objs) {
        logger.error(getLogString(objs), e);
    }

    public static void error(Logger logger, Object... objs) {
        logger.error(getLogString(objs));
    }

    public static void error(Throwable e, Object... objs) {
        log.error(getLogString(objs), e);
    }

    public static void error(Object... objs) {
        log.error(getLogString(objs));
    }

    /**
     * 打印warn日志。
     * <p>
     * ERROR日志记录尽量使用{@link}，避免日志的重复记录
     *
     * @param logger 日志对象
     * @param objs   任意个要输出到日志的参数
     */
    public static void warn(Logger logger, Object... objs) {
        logger.warn(getLogString(objs));
    }

    public static void warn(Object... objs) {
        log.warn(getLogString(objs));
    }

    /**
     * 打印warn日志。
     * <p>
     * ERROR日志记录尽量使用{@link}，避免日志的重复记录
     *
     * @param logger 日志对象
     * @param objs   任意个要输出到日志的参数
     */
    public static void info(Logger logger, Object... objs) {
        logger.info(getLogInfoString(objs));
    }

    public static void info(Object... objs) {
        log.info(getLogInfoString(objs));
    }

    /**
     * 生成输出到日志的字符串
     *
     * @param objs 任意个要输出到日志的参数
     * @return 日志字符串
     */
    public static String getLogString(Object... objs) {
        StringBuilder log = new StringBuilder();
        log.append(LEFT_TAG);
        // 预留扩展位
        log.append(MARK).append(RIGHT_TAG);

        if (objs != null) {
            for (Object o : objs) {
                log.append(LEFT_TAG);
                log.append(o);
                log.append(RIGHT_TAG);
            }
        }
        return log.toString();
    }

    /**
     * 生成输出到日志的字符串
     *
     * @param objs 任意个要输出到日志的参数
     * @return 日志字符串
     */
    public static String getLogInfoString(Object... objs) {
        StringBuilder log = new StringBuilder();
        log.append(LEFT_TAG);
        // 预留扩展位
        log.append(SUCCESS).append(RIGHT_TAG);

        if (objs != null) {
            for (Object o : objs) {
                log.append(LEFT_TAG);
                log.append(o);
                log.append(RIGHT_TAG);
            }
        }
        return log.toString();
    }


}
