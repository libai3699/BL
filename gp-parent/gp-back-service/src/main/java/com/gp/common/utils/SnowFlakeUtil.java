package com.gp.common.utils;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @author axing
 * @version 1.0
 * @date 2021/8/4 13:07
 */
public class SnowFlakeUtil {
    private long machineId ;
    private long dataCenterId ;


    public SnowFlakeUtil(long machineId, long dataCenterId) {
        this.machineId = machineId;
        this.dataCenterId = dataCenterId;
    }

    /**
     * 成员类，SnowFlakeUtil的实例对象的保存域
     */
    private static class IdGenHolder {
        private static final SnowFlakeUtil instance = new SnowFlakeUtil();
    }

    /**
     * 外部调用获取SnowFlakeUtil的实例对象，确保不可变
     */
    public static SnowFlakeUtil get() {
        return IdGenHolder.instance;
    }

    /**
     * 初始化构造，无参构造有参函数，默认节点都是0
     */
    public SnowFlakeUtil() {
        this(0L, 0L);
    }

    private Snowflake snowflake = IdUtil.createSnowflake(machineId,dataCenterId);

    public synchronized long id(){
        return snowflake.nextId();
    }

    public static Long getId() {
        return SnowFlakeUtil.get().id();
    }

    public static void main(String[] args) {
//        System.out.println(IdConstants.MEMBER_ID +SnowFlakeUtil.getId());
//        System.out.println(IdConstants.WAGER_ORDER_ID +SnowFlakeUtil.getId());
        System.out.println(StringUtils.right(String.valueOf(SnowFlakeUtil.getId()),10));
    }
}
