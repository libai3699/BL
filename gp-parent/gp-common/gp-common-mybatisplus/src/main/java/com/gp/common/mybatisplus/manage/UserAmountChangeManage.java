package com.gp.common.mybatisplus.manage;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.enums.AmountChangeTypeEnum;
import com.common.core.util.TxExecutor;
import com.common.datasource.util.CecuUtil;
import com.gp.common.mybatisplus.config.RedisOnlineService;
import com.gp.common.mybatisplus.entity.Currency;
import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.entity.UserWallet;
import com.gp.common.mybatisplus.function.AmountConsumer;
import com.gp.common.mybatisplus.function.ExtConsumer;
import com.gp.common.mybatisplus.mapper.UserMapper;
import com.gp.common.mybatisplus.param.ChangeExtValueVo;
import com.gp.common.mybatisplus.param.TransferDto;
import com.gp.common.mybatisplus.param.UserWalletDto;
import com.gp.common.mybatisplus.pay.constant.PayConst;
import com.gp.common.mybatisplus.service.UserService;
import lombok.SneakyThrows;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class UserAmountChangeManage extends ServiceImpl<UserMapper, TUser> {

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private UserService userService;
    @Resource
    private RedisOnlineService redisOnlineService;
    //重新结算标识
    public static final String sportSettlement = "sportSettlement";


    /**
     * 获取用户处理金额的锁
     *
     * @param userId     用户ID
     * @param currencyId 用户币种ID
     * @return
     */
    public static String getLockStr(Long userId, Integer currencyId) {
        return StrUtil.format("tg:balanceL:{}:{}:{}", CecuUtil.getDbCode(), userId, currencyId);
    }

    @SneakyThrows
    public UserWallet changeBalance(Long userId, Currency currency, BigDecimal amount, AmountChangeTypeEnum amountChangeTypeEnum,
                                    String orderId, String remark, String operator,
                                    String extParam1, String extParam2, AmountConsumer amountConsumer) {
        RLock lock = redissonClient.getLock(getLockStr(userId, currency.getId()));
        if (lock.tryLock(10, TimeUnit.SECONDS)) {
            try {
                return TxExecutor.run(() -> userService.changeAmount(userId, currency, amount, amountChangeTypeEnum, orderId, remark, operator, extParam1, extParam2, amountConsumer));
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            throw new RuntimeException("请稍后再试 !");
        }

    }

    /**
     * 电子类
     *
     * @param userId           用户ID
     * @param currency         币种
     * @param betAmount        投注金额
     * @param awardAmount      彩金金额 (为最终退回金额(本金 + 奖金))
     * @param amountChangeEnum 1 赢, 2 输, 3 和
     * @param orderId          订单号
     * @param remark           备注
     * @param operator         操作人
     * @param extParam1        扩展参数1
     * @param extParam2        扩展参数2
     * @param amountConsumer   附带执行的程序
     * @return
     */
    @SneakyThrows
    public UserWalletDto changePGGameBalance(Long userId, Currency currency, BigDecimal betAmount, BigDecimal awardAmount,
                                             String orderId, String remark, String operator, String extParam1, String extParam2, AmountConsumer amountConsumer) {
        redisOnlineService.userOnline(userId.toString());
        RLock lock = redissonClient.getLock(getLockStr(userId, currency.getId()));
        if (lock.tryLock(10, TimeUnit.SECONDS)) {
            try {
                return TxExecutor.run(() -> userService.changePGGameBalance(userId, currency, betAmount, awardAmount, orderId, remark, operator, extParam1, extParam2, amountConsumer));
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            throw new RuntimeException("请稍后再试 !");
        }

    }

    /**
     * JDB 捕鱼类
     *
     * @param userId      用户ID
     * @param currency    币种
     * @param betAmount   投注金额
     * @param awardAmount 彩金金额 (为最终退回金额(本金 + 奖金))
     * @param mb          最小金额
     * @param orderId     订单号
     * @param remark      备注
     **/
    @SneakyThrows
    public UserWalletDto changePGGameBalanceMb(Long userId, Currency currency, BigDecimal betAmount, BigDecimal awardAmount, BigDecimal mb,
                                               String orderId, String remark, String operator, String extParam1, String extParam2, AmountConsumer amountConsumer) {
        redisOnlineService.userOnline(userId.toString());
        RLock lock = redissonClient.getLock(getLockStr(userId, currency.getId()));
        if (lock.tryLock(10, TimeUnit.SECONDS)) {
            try {
                return TxExecutor.run(() -> userService.changePGGameBalanceMb(userId, currency, betAmount, awardAmount, mb, orderId, remark, operator, extParam1, extParam2, amountConsumer));
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            throw new RuntimeException("请稍后再试 !");
        }

    }

    /**
     * PG类（传入已查询的TUser，避免重复DB查询）
     */
    @SneakyThrows
    public UserWalletDto changePGGameBalance(Long userId, Currency currency, BigDecimal betAmount, BigDecimal awardAmount,
                                             String orderId, String remark, String operator, String extParam1, String extParam2,
                                             AmountConsumer amountConsumer, TUser tUser) {
        redisOnlineService.userOnline(userId.toString());
        RLock lock = redissonClient.getLock(getLockStr(userId, currency.getId()));
        if (lock.tryLock(10, TimeUnit.SECONDS)) {
            try {
                return TxExecutor.run(() -> userService.changePGGameBalance(userId, currency, betAmount, awardAmount, orderId, remark, operator, extParam1, extParam2, amountConsumer, tUser));
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            throw new RuntimeException("请稍后再试 !");
        }
    }

    /**
     * 电子类（传入已查询的TUser，避免重复DB查询）
     */
    @SneakyThrows
    public UserWalletDto changeSpribeGameBalance(Long userId, Currency currency, Integer accountType, BigDecimal amount,
                                                 String orderId, String remark, String operator, String extParam1, String extParam2,
                                                 AmountConsumer amountConsumer, TUser tUser) {
        redisOnlineService.userOnline(userId.toString());
        RLock lock = redissonClient.getLock(getLockStr(userId, currency.getId()));
        if (lock.tryLock(10, TimeUnit.SECONDS)) {
            try {
                return TxExecutor.run(() -> userService.changeSpribeGameBalance(userId, currency, accountType, amount, orderId, remark, operator, extParam1, extParam2, amountConsumer, tUser));
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            throw new RuntimeException("请稍后再试 !");
        }
    }

    /**
     * 电子类
     *
     * @param userId           用户ID
     * @param currency         币种
     * @param betAmount        投注金额
     * @param amount           彩金金额 (为最终退回金额(本金 + 奖金))
     * @param amountChangeEnum 1 赢, 2 输, 3 和
     * @param orderId          订单号
     * @param remark           备注
     * @param operator         操作人
     * @param extParam1        扩展参数1
     * @param extParam2        扩展参数2
     * @param amountConsumer   附带执行的程序
     * @return
     */
    @SneakyThrows
    public UserWalletDto changeSpribeGameBalance(Long userId, Currency currency, Integer accountType, BigDecimal amount,
                                                 String orderId, String remark, String operator, String extParam1, String extParam2, AmountConsumer amountConsumer) {
        redisOnlineService.userOnline(userId.toString());
        RLock lock = redissonClient.getLock(getLockStr(userId, currency.getId()));
        if (lock.tryLock(10, TimeUnit.SECONDS)) {
            try {
                return TxExecutor.run(() -> userService.changeSpribeGameBalance(userId, currency, accountType, amount, orderId, remark, operator, extParam1, extParam2, amountConsumer));
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            throw new RuntimeException("请稍后再试 !");
        }

    }

    /**
     * 批量的 一次性多个
     *
     * @param userId           用户ID
     * @param currency         币种
     * @param betAmount        投注金额
     * @param amount           彩金金额 (为最终退回金额(本金 + 奖金))
     * @param amountChangeEnum 1 赢, 2 输, 3 和
     * @param orderId          订单号
     * @param remark           备注
     * @param operator         操作人
     * @param extParam1        扩展参数1
     * @param extParam2        扩展参数2
     * @param amountConsumer   附带执行的程序
     * @return
     */
    @SneakyThrows
    public UserWalletDto changeSpribeGameBalanceBatch(Long userId, Currency currency, Integer accountType, List<Integer> accountTypes, BigDecimal handleMoney, List<BigDecimal> amounts,
                                                      List<String> orderIds, String remark, String operator, String extParam1, String extParam2, AmountConsumer amountConsumer) {
        return changeSpribeGameBalanceBatch(userId, currency, accountType, accountTypes, handleMoney, amounts, orderIds, remark, operator, extParam1, extParam2, amountConsumer, null);
    }

    @SneakyThrows
    public UserWalletDto changeSpribeGameBalanceBatch(Long userId, Currency currency, Integer accountType, List<Integer> accountTypes, BigDecimal handleMoney, List<BigDecimal> amounts,
                                                      List<String> orderIds, String remark, String operator, String extParam1, String extParam2, AmountConsumer amountConsumer, TUser tUser) {
        redisOnlineService.userOnline(userId.toString());
        RLock lock = redissonClient.getLock(getLockStr(userId, currency.getId()));
        if (lock.tryLock(10, TimeUnit.SECONDS)) {
            try {
                TUser finalTUser = tUser;
                return TxExecutor.run(() -> userService.changeSpribeGameBalanceBatch(userId, currency, accountType, accountTypes, handleMoney, amounts, orderIds, remark, operator, extParam1, extParam2, amountConsumer, finalTUser));
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            throw new RuntimeException("请稍后再试 !");
        }

    }


    /**
     * wp 彩票
     *
     * @param userId    用户ID
     * @param currency  币种
     * @param amount    金额
     * @param orderId   订单号
     * @param remark    备注
     * @param operator  操作人
     * @param extParam1 扩展参数1
     * @param extParam2 扩展参数2
     * @return
     */
    @SneakyThrows
    public UserWalletDto changeSpribeGameBalanceWp(Long userId, Currency currency, Integer accountType, BigDecimal amount,
                                                   String orderId, String remark, String operator, String extParam1, String extParam2) {
        redisOnlineService.userOnline(userId.toString());
        RLock lock = redissonClient.getLock(getLockStr(userId, currency.getId()));
        if (lock.tryLock(10, TimeUnit.SECONDS)) {
            try {
                return TxExecutor.run(() -> userService.changeSpribeGameBalanceWp(userId, currency, accountType, amount,orderId, remark, operator, extParam1, extParam2));
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        } else {
            throw new RuntimeException("请稍后再试 !");
        }

    }

    @SneakyThrows
    public TransferDto transferBalance(Long fromId, Long transferUserId, List<ChangeExtValueVo> changeExtValueVo, ExtConsumer extConsumer, Currency currency, BigDecimal amount, String orderNoSend, String orderNoReceive) {
        List<RLock> locks = new ArrayList<>();
        try {
            RLock extLock = null;

            // 额外属性锁
            for (ChangeExtValueVo extValueVo : changeExtValueVo) {
                extLock = redissonClient.getLock(getLockStr(extValueVo.getUserId(), extValueVo.getExtType()));
            }
            if (extLock != null) {
                if (!extLock.tryLock(10, TimeUnit.SECONDS)) {
                    throw new RuntimeException("无法获取用户额外属性锁，请稍后再试 !");
                }
                locks.add(extLock);
            }
            // 金额锁
            RLock fromLock = redissonClient.getLock(UserAmountChangeManage.getLockStr(fromId, PayConst.currency_id));
            RLock toLock = redissonClient.getLock(UserAmountChangeManage.getLockStr(transferUserId, PayConst.currency_id));
            if (!fromLock.tryLock(10, TimeUnit.SECONDS)) {
                throw new RuntimeException("无法获取用户锁，请稍后再试 !");
            }
            if (!toLock.tryLock(10, TimeUnit.SECONDS)) {
                throw new RuntimeException("无法获取用户锁，请稍后再试 !");
            }
            locks.add(fromLock);
            locks.add(toLock);

            //Long fromId, Long transferUserId, List<ChangeExtValueVo> changeExtValueVo, ExtConsumer extConsumer, Currency currency, BigDecimal amount, String orderNo
            // 执行转账操作
            return TxExecutor.run(() -> userService.transferBalance(fromId, transferUserId, changeExtValueVo, extConsumer, currency, amount, orderNoSend, orderNoReceive));

        } finally {
            // 释放所有用户的锁
            for (RLock lock : locks) {
                if (lock != null && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }


    }
}
