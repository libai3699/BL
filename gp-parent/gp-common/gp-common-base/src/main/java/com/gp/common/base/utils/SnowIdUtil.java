package com.gp.common.base.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Random;


/**
 * @author axing
 * @version 1.0
 * @date 2021/8/4 13:07
 */
public class SnowIdUtil {

    private Snowflake snowflake;
    /**
     * 初始化构造，无参构造有参函数，默认节点都是0
     */
    public SnowIdUtil() {
        this(0L, 0L);
    }

    public SnowIdUtil(long machineId, long dataCenterId) {
        this.snowflake = IdUtil.getSnowflake(machineId, dataCenterId);
    }

    /**
     * 成员类，SnowFlakeUtil的实例对象的保存域
     */
    private static class IdGenHolder {
        private static final Random random = new Random();
        // 随机生成 workerId 和 datacenterId，范围在 0 到 31
        private static final long workerId = random.nextInt(32);       // 生成 0-31 之间的随机 workerId
        private static final long datacenterId = random.nextInt(32);   // 生成 0-31 之间的随机 datacenterId
        private static final SnowIdUtil instance = new SnowIdUtil(workerId, datacenterId);
    }

    /**
     * 外部调用获取SnowFlakeUtil的实例对象，确保不可变
     */
    public static SnowIdUtil get() {
        return IdGenHolder.instance;
    }


    private long id() {
        return snowflake.nextId();
    }

    public static Long getId() {
        return SnowIdUtil.get().id();
    }

    /**
     * 获取订单号, 前缀, 雪花ID, 6位随机字符
     *
     * @param prefix
     * @return
     */
    public static String getId(String prefix) {
        return StringUtils.join(prefix, getId(), RandomStringUtils.randomAlphanumeric(6));
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            System.out.println(getId("ST"));
        }
    }

}
