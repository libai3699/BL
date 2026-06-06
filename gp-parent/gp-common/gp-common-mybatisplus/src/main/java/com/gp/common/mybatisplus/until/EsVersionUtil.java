package com.gp.common.mybatisplus.until;

public class EsVersionUtil {
    public static long generateVersion(int stageType) {
        // 阶段前缀放在高位，保证结算 > 下注 > 取消
        long stagePrefix = stageType * 1_0000_0000_0000_0000L; // 16位高位
        return stagePrefix + System.currentTimeMillis();
    }
}
