package com.common.elasticsearch.util;

import cn.hutool.core.date.DatePattern;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author axing
 * @version 1.0
 * @date 2024/10/2/002 14:45
 */
public class EsDateUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * es存的日期数据是UTC的,如果使用日期作为参数,可以通过这个方法进行转换
     * @param date
     * @return
     */
    public static String getUtcDateStr(Date date){
        Instant instant = date.toInstant();
        return DateTimeFormatter.ofPattern(DatePattern.UTC_MS_PATTERN)
                        .withZone(ZoneOffset.UTC).format(instant);
    }


}
