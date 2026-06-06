package com.gp.common.mybatisplus.manage;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.enums.AmountChangeTypeEnum;
import com.common.core.enums.OrderTypeEnum;
import com.gp.common.mybatisplus.pay.constant.PayConst;
import com.common.core.util.TxExecutor;
import com.common.datasource.util.CecuUtil;
import com.gp.common.mybatisplus.entity.Currency;
import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.entity.UserExt;
import com.gp.common.mybatisplus.entity.UserWallet;
import com.gp.common.mybatisplus.function.ExtConsumer;
import com.gp.common.mybatisplus.mapper.UserMapper;
import com.gp.common.mybatisplus.param.ChangeExtValueVo;
import com.gp.common.mybatisplus.service.UserExtService;
import lombok.SneakyThrows;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
public class UserExtChangeManage extends ServiceImpl<UserMapper, TUser> {

    private static final long LOCK_TIMEOUT = 10;

    @Resource
    private RedissonClient redissonClient;
    @Resource
    private UserExtService userExtService;


    /**
     * 获取用户处理金额的锁
     *
     * @param userId
     * @return
     */
    public static String getLockStr(Long userId, Integer type) {
        return StrUtil.format("tg:userExt:{}:{}:{}", CecuUtil.getDbCode(), userId, type);
    }

    /**
     * 按 lockKey 自然排序获取多把锁，执行业务后释放，防止死锁
     */
    private <T> T lockAndExecute(List<String> lockKeys, Supplier<T> action) throws InterruptedException {
        lockKeys.sort(Comparator.naturalOrder());
        List<RLock> acquired = new ArrayList<>(lockKeys.size());
        try {
            for (String key : lockKeys) {
                RLock lock = redissonClient.getLock(key);
                if (!lock.tryLock(LOCK_TIMEOUT, TimeUnit.SECONDS)) {
                    throw new RuntimeException("无法获取用户锁，请稍后再试 !");
                }
                acquired.add(lock);
            }
            return action.get();
        } finally {
            for (RLock lock : acquired) {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    /**
     * 单锁快捷方法
     */
    private <T> T lockAndExecute(String lockKey, Supplier<T> action) throws InterruptedException {
        RLock lock = redissonClient.getLock(lockKey);
        if (!lock.tryLock(LOCK_TIMEOUT, TimeUnit.SECONDS)) {
            throw new RuntimeException("无法获取用户锁，请稍后再试 !");
        }
        try {
            return action.get();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @SneakyThrows
    public void changeExtValue(List<ChangeExtValueVo> changeExtValueVos, ExtConsumer extConsumer) {
        List<String> lockKeys = new ArrayList<>();
        for (ChangeExtValueVo vo : changeExtValueVos) {
            lockKeys.add(getLockStr(vo.getUserId(), vo.getExtType()));
        }
        lockAndExecute(lockKeys, () -> {
            TxExecutor.run(() -> userExtService.changeExtValue(changeExtValueVos, extConsumer));
            return null;
        });
    }

    @SneakyThrows
    public void changeExtValueSingle(ChangeExtValueVo changeExtValueVo, ExtConsumer extConsumer) {
        lockAndExecute(getLockStr(changeExtValueVo.getUserId(), changeExtValueVo.getExtType()), () -> {
            TxExecutor.run(() -> userExtService.changeExtValueSingle(changeExtValueVo, extConsumer));
            return null;
        });
    }

    @SneakyThrows
    public void changeRebateToAmount(ChangeExtValueVo changeExtValueVo, ExtConsumer extConsumer) {
        List<String> lockKeys = new ArrayList<>();
        lockKeys.add(getLockStr(changeExtValueVo.getUserId(), changeExtValueVo.getExtType()));
        lockKeys.add(UserAmountChangeManage.getLockStr(changeExtValueVo.getUserId(), PayConst.currency_id));
        lockAndExecute(lockKeys, () -> {
            TxExecutor.run(() -> userExtService.changeRebateToAmount(changeExtValueVo, extConsumer));
            return null;
        });
    }

    @SneakyThrows
    public UserExt changeExtValue(ChangeExtValueVo changeExtValueVo, ExtConsumer extConsumer) {
        return lockAndExecute(getLockStr(changeExtValueVo.getUserId(), changeExtValueVo.getExtType()), () ->
                TxExecutor.run(() -> userExtService.changeExtValue(changeExtValueVo, extConsumer))
        );
    }

    @SneakyThrows
    public void changeExtOrAmount(List<ChangeExtValueVo> changeExtValueVo, ExtConsumer extConsumer, AmountChangeTypeEnum amountChangeTypeEnum, OrderTypeEnum orderTypeEnum, Boolean isChangeWallet, Integer addWallet, BigDecimal addWalletAmount) {
        List<String> lockKeys = new ArrayList<>();
        Long userId = null;
        for (ChangeExtValueVo extValueVo : changeExtValueVo) {
            lockKeys.add(getLockStr(extValueVo.getUserId(), extValueVo.getExtType()));
            userId = extValueVo.getUserId();
        }
        if (isChangeWallet && userId != null) {
            lockKeys.add(UserAmountChangeManage.getLockStr(userId, PayConst.currency_id));
        }
        lockAndExecute(lockKeys, () -> {
            TxExecutor.run(() -> userExtService.changeExtOrAmount(changeExtValueVo, extConsumer, amountChangeTypeEnum, orderTypeEnum, isChangeWallet, addWallet, addWalletAmount));
            return null;
        });
    }

    /***
     *
     * @param changeExtValueVo
     * @param extConsumer
     * @param currency
     * @param amount
     * @param bonusAmount
     * @param orderTypeEnum
     * @param amountChangeTypeEnum
     * @return
     */
    @SneakyThrows
    public UserWallet changeRechargeAmount(List<ChangeExtValueVo> changeExtValueVo, ExtConsumer extConsumer, Currency currency,
                                           BigDecimal amount, BigDecimal bonusAmount,
                                           OrderTypeEnum orderTypeEnum, AmountChangeTypeEnum amountChangeTypeEnum, String merchantCode) {
        List<String> lockKeys = new ArrayList<>();
        Long userId = null;
        for (ChangeExtValueVo extValueVo : changeExtValueVo) {
            lockKeys.add(getLockStr(extValueVo.getUserId(), extValueVo.getExtType()));
            userId = extValueVo.getUserId();
        }
        if (userId != null) {
            lockKeys.add(UserAmountChangeManage.getLockStr(userId, PayConst.currency_id));
        }
        return lockAndExecute(lockKeys, () ->
                TxExecutor.run(() -> userExtService.changeRechargeAmount(changeExtValueVo, extConsumer, currency, amount, bonusAmount, orderTypeEnum, amountChangeTypeEnum, merchantCode))
        );
    }
}
