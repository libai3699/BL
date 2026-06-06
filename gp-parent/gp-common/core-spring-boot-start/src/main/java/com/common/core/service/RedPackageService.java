package com.common.core.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.common.core.util.RedisUtil;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.exception.BusinessException;
import com.gp.common.base.utils.BigDecimalUtils;
import com.gp.common.base.utils.MessagesUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 模拟抢红包
 *
 * @author 晓风残月Lx
 * @date 2023/4/5 16:00
 */

@Slf4j
@Data
@Service
public class RedPackageService {
    @Resource
    private RedisUtil redisUtil;

    /**
     * 拆红包的算法 ---> 二倍均值算法
     *
     * @param totalMoney
     * @param redpackageNumber
     * @return
     */
    public Integer[] splitRedPackageAlgorithm(int totalMoney, int redpackageNumber) {
        Integer[] redPackageNums = new Integer[redpackageNumber];
        // 已经被抢夺的红包金额
        int useMoney = 0;

        for (int i = 0; i < redpackageNumber; i++) {
            if (i == redpackageNumber - 1) {
                // 最后一个红包
                redPackageNums[i] = totalMoney - useMoney;
            } else {
                // 二倍均值算法，每次拆分后塞进子红包的金额
                // 金额 = 随机取件（0，（剩余红包金额 / 未被抢的剩余红包数 N ）* 2 ）
                int avgMoney = ((totalMoney - useMoney) / (redpackageNumber - i)) * 2;
                // 整数红包最低为1
                // 修复：当avgMoney <= 1时，直接使用1，避免Random范围错误
                if (avgMoney <= 1) {
                    redPackageNums[i] = 1;
                } else {
                    redPackageNums[i] = 1 + RandomUtil.randomInt(0, avgMoney - 1);
                }
            }
            useMoney = useMoney + redPackageNums[i];
        }
        return redPackageNums;
    }

    /**
     * 发红包
     *
     * @param totalMoney
     * @param redPackageNumber
     */
    public RedPackageDto sendRedPackage(BigDecimal totalMoney, int redPackageNumber, Integer digit) {
        // 1. 拆红包，将总金额totalMoney拆分成 redpackageNumber 个子红包
        BigDecimal[] splitRedPackages = splitRedPackageDouble(totalMoney, redPackageNumber, digit); // 拆分红包算法（二倍均值算法）通过后获得的多个子红包数组

        log.info("=== 发红包 sendRedPackage ===");
        log.info("总金额: {}, 红包数: {}, 精度: {}", totalMoney, redPackageNumber, digit);
        log.info("拆分后的红包数组: {}", (Object) splitRedPackages);
        for (int i = 0; i < splitRedPackages.length; i++) {
            log.info("红包[{}]: 值={}, 类型={}", i, splitRedPackages[i], splitRedPackages[i].getClass().getName());
        }

        // 2. 发红包并保存进 list 结构 并且设置过期时间
        String redPackageKey = IdUtil.fastSimpleUUID();
        String key = RedisKey.RED_PACKAGE_KEY + redPackageKey;
        log.info("Redis Key: {}", key);
        redisUtil.batchListPutAll(key, CollUtil.newArrayList(splitRedPackages));
        log.info("红包已存入Redis");
        redisUtil.expire(key, 1, TimeUnit.DAYS);
        RedPackageDto redPackageDto = new RedPackageDto();
        redPackageDto.setRedKey(redPackageKey);
        // redPackageDto.setRedList(splitRedPackages);
        // 3.发红包ok，返回前台显示
        return redPackageDto;
    }

    /**
     * 等额红包
     *
     * @param totalMoney
     * @param redPackageNumber
     */
    public RedPackageDto sendSameRedPackage(BigDecimal totalMoney, int redPackageNumber, Integer digit) {
        // 1. 拆红包，将总金额totalMoney拆分成 redpackageNumber 个子红包
        BigDecimal[] splitRedPackages = splitSameRedPackageDouble(totalMoney, redPackageNumber, digit);

        log.info("=== 发等额红包 sendSameRedPackage ===");
        log.info("总金额: {}, 红包数: {}, 精度: {}", totalMoney, redPackageNumber, digit);
        log.info("拆分后的红包数组: {}", (Object) splitRedPackages);
        for (int i = 0; i < splitRedPackages.length; i++) {
            log.info("红包[{}]: 值={}, 类型={}", i, splitRedPackages[i], splitRedPackages[i].getClass().getName());
        }

        // 2. 发红包并保存进 list 结构 并且设置过期时间
        String redPackageKey = IdUtil.fastSimpleUUID();
        String key = RedisKey.RED_PACKAGE_KEY + redPackageKey;
        log.info("Redis Key: {}", key);
        redisUtil.batchListPutAll(key, CollUtil.newArrayList(splitRedPackages));
        log.info("红包已存入Redis");
        redisUtil.expire(key, 1, TimeUnit.DAYS);
        RedPackageDto redPackageDto = new RedPackageDto();
        redPackageDto.setRedKey(redPackageKey);
        // redPackageDto.setRedList(splitRedPackages);
        // 3.发红包ok，返回前台显示
        return redPackageDto;
    }

    /**
     * 拆红包的算法 ---> 二倍均值算法
     *
     * @param totalMoney
     * @param redpackageNumber
     * @return
     */
    public BigDecimal[] splitRedPackageDouble(BigDecimal totalMoney, int redpackageNumber, Integer digit) {
        // 修复int溢出问题：完全使用BigDecimal计算，避免整数溢出
        BigDecimal[] redPackageNum2s = new BigDecimal[redpackageNumber];
        // 已经被抢夺的红包金额
        BigDecimal useMoney = BigDecimal.ZERO;
        // 最小单位
        BigDecimal minUnit = BigDecimal.ONE.divide(BigDecimal.TEN.pow(digit));

        for (int i = 0; i < redpackageNumber; i++) {
            if (i == redpackageNumber - 1) {
                // 最后一个红包，分配剩余所有金额
                redPackageNum2s[i] = totalMoney.subtract(useMoney);
            } else {
                // 二倍均值算法，每次拆分后塞进子红包的金额
                // 金额 = 随机取件（0，（剩余红包金额 / 未被抢的剩余红包数 N ）* 2 ）
                BigDecimal remainMoney = totalMoney.subtract(useMoney);
                int remainCount = redpackageNumber - i;
                BigDecimal avgMoney = remainMoney.divide(BigDecimal.valueOf(remainCount), digit, BigDecimal.ROUND_DOWN)
                        .multiply(BigDecimal.valueOf(2));
                
                // 红包最低金额为最小单位
                if (avgMoney.compareTo(minUnit) <= 0) {
                    redPackageNum2s[i] = minUnit;
                } else {
                    // 计算随机金额：minUnit + random(0, avgMoney - minUnit)
                    // 将avgMoney转换为最小单位的倍数来计算随机数
                    BigDecimal maxUnits = avgMoney.divide(minUnit, 0, BigDecimal.ROUND_DOWN);
                    int maxUnitsInt = maxUnits.intValue();
                    
                    if (maxUnitsInt <= 1) {
                        redPackageNum2s[i] = minUnit;
                    } else {
                        int randomUnits = 1 + RandomUtil.randomInt(0, maxUnitsInt - 1);
                        redPackageNum2s[i] = minUnit.multiply(BigDecimal.valueOf(randomUnits));
                    }
                }
                
                // 加上已经用的
                useMoney = useMoney.add(redPackageNum2s[i]);
            }
        }
        return redPackageNum2s;
    }

    private static BigDecimal[] splitSameRedPackageDouble(BigDecimal totalMoney, int redpackageNumber, Integer digit) {
        log.info("等额拆分红包 - 总金额: {}, 红包数: {}, 精度: {}", totalMoney, redpackageNumber, digit);

        BigDecimal[] redPackageNum2s = new BigDecimal[redpackageNumber];

        // 如果未指定精度或红包数为0，默认精度为2
        int scale = (digit != null && digit > 0) ? digit : 2;

        // 每人的红包金额（向下取整）
        BigDecimal singleAmount = redpackageNumber == 0 ? BigDecimal.ZERO
                : totalMoney.divide(BigDecimal.valueOf(redpackageNumber), scale, BigDecimal.ROUND_DOWN);

        log.info("单个红包基础金额（向下取整）: {}", singleAmount);

        // 先给每个红包分配基础金额
        BigDecimal distributedTotal = BigDecimal.ZERO;
        for (int i = 0; i < redpackageNumber; i++) {
            redPackageNum2s[i] = singleAmount;
            distributedTotal = distributedTotal.add(singleAmount);
        }

        log.info("分配基础金额后总和: {}, 原始总额: {}", distributedTotal, totalMoney);

        // 计算余数
        BigDecimal remainder = totalMoney.subtract(distributedTotal);
        log.info("余数: {}", remainder);

        // 将余数平均分配到前面的红包中（每次加最小单位）
        if (remainder.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal minUnit = BigDecimal.ONE.divide(BigDecimal.TEN.pow(scale));
            log.info("最小单位: {}", minUnit);

            int index = 0;
            while (remainder.compareTo(BigDecimal.ZERO) > 0) {
                redPackageNum2s[index] = redPackageNum2s[index].add(minUnit);
                remainder = remainder.subtract(minUnit);
                log.info("第{}个红包加上最小单位后: {}, 剩余余数: {}", index, redPackageNum2s[index], remainder);
                index = (index + 1) % redpackageNumber;
            }
        }

        // 验证总额
        BigDecimal sum = Arrays.stream(redPackageNum2s).reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("拆分完成 - 验证总和: {}, 原始总额: {}, 是否相等: {}", sum, totalMoney, sum.compareTo(totalMoney) == 0);

        return redPackageNum2s;
    }

    public BigDecimal robRedPackage(String redPackageKey, String userId) {
        log.info("=== 抢红包 robRedPackage ===");
        log.info("红包Key: {}, 用户ID: {}", redPackageKey, userId);

        // 1.验证某个用户是否抢过红包，不可以多抢
        Object redPackage = redisUtil.hget(RedisKey.RED_PACKAGE_CONSUME_KEY + redPackageKey, userId);
        // 2.没有抢过红包可以抢，如果抢过返回 -2 表示抢过
        if (null == redPackage) {
            // 2.1 从 list 结构中出队一个作为抢的红包
            Object partRedPackage = redisUtil.lleftPop(RedisKey.RED_PACKAGE_KEY + redPackageKey);
            log.info("从Redis取出的红包 - 值: {}, 类型: {}", partRedPackage,
                     partRedPackage != null ? partRedPackage.getClass().getName() : "null");

            if (partRedPackage != null) {
                // 2.2 抢到红包后 需要记录到hash结构中 记录每人抢到的红包
                redisUtil.hset(RedisKey.RED_PACKAGE_CONSUME_KEY + redPackageKey, userId, partRedPackage);
                log.info("用户" + userId + "\t 抢到了多少钱的红包：" + partRedPackage);

                // 转换为 BigDecimal
                BigDecimal amount;
                if (partRedPackage instanceof BigDecimal) {
                    amount = (BigDecimal) partRedPackage;
                    log.info("直接使用BigDecimal，无需转换");
                } else {
                    amount = new BigDecimal(partRedPackage + "");
                    log.info("从字符串转换: {} -> {}", partRedPackage, amount);
                }

                // TODO 后续异步进mysql或者MQ进一步做统计处理，每一年你发出多少红包，抢到了多少红包
                return amount;
            } else {
                // 2. 抢完了
                log.warn("红包已抢完");
                throw new BusinessException(MessagesUtils.get("bot.red.HNBQW"));
            }
        } else {
            // 3. 抢过了红包
            log.info("用户已抢过红包，金额: {}", redPackage);
            throw new BusinessException(MessagesUtils.get("bot.red.YJLQG") + "-" + MessagesUtils.get("bot.amount.JE")
                    + BigDecimalUtils.trim(new BigDecimal(redPackage + "")));
        }

    }

    public static int transformNumberInt(int num) {
        return (int) Math.pow(10, num);
    }

    public static Double transformNumberDouble(int num) {
        int intValue = (int) Math.pow(10, num);
        return Math.pow(10, num);
    }

    @Data
    public static class RedPackageDto {

        private String redKey;
        private Double[] redList;
        private Integer[] redList2;
    }



}
