package com.common.core.id.util;


import com.common.core.id.common.IdNoConstants;
import com.common.core.id.common.IdNoTypeEnum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 单号生成工具类
 *
 * @author mq
 */
public class IdNoSerialUtil {

    /**
     * 生成单号前缀
     */
    public static String getFormNoPrefix(IdNoTypeEnum idNoTypeEnum) {
        //格式化时间
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(idNoTypeEnum.getDatePattern());
        StringBuffer sb = new StringBuffer();
        sb.append(idNoTypeEnum.getPrefix());
        sb.append(formatter.format(LocalDateTime.now()));
        return sb.toString();
    }

    /**
     * 构建流水号缓存Key
     *
     * @param serialPrefix 流水号前缀
     * @return 流水号缓存Key
     */
    public static String getCacheKey(String serialPrefix) {
        return IdNoConstants.ORDER_NO.concat(serialPrefix);
    }

    /**
     * 补全流水号
     *
     * @param serialPrefix      单号前缀
     * @param incrementalSerial 当天自增流水号
     * @author mengqiang
     * @date 2019/1/1
     */
    public static String completionSerial(String serialPrefix, Long incrementalSerial,
                                          IdNoTypeEnum idNoTypeEnum) {
        StringBuffer sb = new StringBuffer(serialPrefix);

        //需要补0的长度=流水号长度 -当日自增计数长度
        int length = idNoTypeEnum.getSerialLength() - String.valueOf(incrementalSerial).length();
        //补零
        for (int i = 0; i < length; i++) {
            sb.append("0");
        }
        //redis当日自增数
        sb.append(incrementalSerial);
        return sb.toString();
    }


    /**
     * 补全随机数
     *
     * @param serialWithPrefix 当前单号
     * @param idNoTypeEnum   单号生成枚举
     * @author mengqiang
     * @date 2019/1/1
     */
    public static String completionRandom(String serialWithPrefix, IdNoTypeEnum idNoTypeEnum) {
        StringBuffer sb = new StringBuffer(serialWithPrefix);
        //随机数长度
        int length = idNoTypeEnum.getRandomLength();
        if (length > 0) {
            Random random = new Random();
            for (int i = 0; i < length; i++) {
                //十以内随机数补全
                sb.append(random.nextInt(10));
            }
        }
        return sb.toString();
    }
}
