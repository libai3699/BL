package com.gp.common.mybatisplus.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TtlRunnable;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.enums.AmountChangeTypeEnum;
import com.common.core.enums.OrderTypeEnum;
import com.common.datasource.util.CecuUtil;
import com.common.rabbitmq.service.RabbitMqTemplate;
import com.gp.common.base.constant.AccountChangeTypeConstants;
import com.gp.common.base.constant.BaseGameInfoCons;
import com.gp.common.base.constant.ExchangeTypeConstants;
import com.gp.common.base.exception.BusinessException;
import com.gp.common.base.utils.*;
import com.gp.common.mybatisplus.config.RedisOnlineService;
import com.gp.common.mybatisplus.configuration.BotConfiguration;
import com.gp.common.mybatisplus.configuration.FinancialTelegramUtil;
import com.gp.common.mybatisplus.dto.UserExtDTO;
import com.gp.common.mybatisplus.entity.*;
import com.gp.common.mybatisplus.entity.Currency;
import com.gp.common.mybatisplus.function.ExtConsumer;
import com.gp.common.mybatisplus.mapper.UserExtMapper;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import com.gp.common.mybatisplus.param.ChangeExtValueVo;
import com.gp.common.mybatisplus.until.EsVersionUtil;
import com.gp.common.mybatisplus.until.UserExtChangeSign;
import com.gp.common.mybatisplus.until.UserExtSign;
import com.gp.common.mybatisplus.until.UserSign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户额外数据Service业务层处理
 *
 * @author axing
 * @date 2024-05-13
 */
@Service
@Slf4j
public class UserExtService extends ServiceImpl<UserExtMapper, UserExt> {
    @Resource
    private UserExtMapper userExtMapper;
    @Resource
    private UserService userService;
    @Resource
    private UserExtSign userExtSign;
    @Resource
    private MNacosParam mNacosParam;
    @Resource
    private UserExtChangeService userExtChangeService;

    @Resource
    private UserExtChangeSign userExtChangeSign;
    @Resource
    private UserWalletService userWalletService;
    @Resource
    private UserSign userSign;
    @Resource
    private AmountChangeService amountChangeService;

    @Resource
    private MerchantService merchantService;
    @Resource
    public CecuUtil cecuUtil;
    @Resource
    private DailyActiveUserService dailyActiveUserService;

    @Resource
    private ConfigRiskService configRiskService;

    @Resource
    private RedisOnlineService redisOnlineService;

    @Resource
    private ThreadPoolTaskExecutor esThreadPoolTaskExecutor;
    @Resource
    private RabbitMqTemplate rabbitMqTemplate;

    /**
     * 查询用户额外数据
     *
     * @param id 用户额外数据ID
     * @return 用户额外数据
     */

    public UserExt selectUserExtById(Long id) {
        return userExtMapper.selectUserExtById(id);
    }

    /**
     * 查询用户额外数据列表
     *
     * @param param 用户额外数据
     * @return 用户额外数据
     */

    public List<UserExt> selectUserExtList(UserExt param) {
        return userExtMapper.selectUserExtList(param);
    }

    /**
     * 新增用户额外数据
     *
     * @param param 用户额外数据
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertUserExt(UserExt param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改用户额外数据
     *
     * @param param 用户额外数据
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserExt(UserExt param) {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除用户额外数据
     *
     * @param ids 需要删除的用户额外数据ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserExtByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除用户额外数据信息
     *
     * @param id 用户额外数据ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserExtById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    public UserExt queryUSerExt(Long userId, Integer type) {
        TUser tUser = userService.getById(userId);
        if (tUser == null) {
            return null;
        }
        LambdaQueryWrapper<UserExt> q = new LambdaQueryWrapper<>();
        q.eq(UserExt::getUserId, userId);
        q.eq(UserExt::getType, type);
        UserExt userExt = this.userExtMapper.selectOne(q);
        Currency currency = CurrencyService.usdtCurrency;
        if (userExt == null) {
            userExt = new UserExt();
            userExt.setUserId(userId);
            userExt.setUserTgId(tUser.getUserTgId());
            userExt.setUserTgName(tUser.getUserTgName());
            userExt.setUserTgUsername(tUser.getUserTgUsername());
            userExt.setType(type);
            userExt.setAmount(BigDecimal.ZERO);
            userExt.setSalt(MD5Util.getSalt());
            userExt.setCurrencyId(currency.getId());
            userExt.setItemId(currency.getItemId());
            userExt.setChainTag(currency.getChainTag());
            userExt.setItemName(currency.getItemName());
            userExtSign.dealSign(userExt);
            try {
                this.userExtMapper.insert(userExt);
            } catch (Exception e) {
                log.debug("已经游记录了");
            }

        }
        return userExt;
    }

    public Integer reduceUserExt(Long userId, Integer type, BigDecimal num) {
        String signSecretKey = mNacosParam.getSignSecretKey();
        long signTime = System.currentTimeMillis();
        return this.baseMapper.reduceUserExt(userId, type, num, signSecretKey, signTime);
    }

    public void changeExtValue(List<ChangeExtValueVo> changeExtValueVos, ExtConsumer extConsumer) {
        LocalDateTime start1 = LocalDateTime.now();
        log.info("changeExtValue开始,时间{}", start1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        List<UserExt> updateUserExtList = new ArrayList<>();
        List<UserExtChange> userExtChangeList = new ArrayList<>();
        for (ChangeExtValueVo changeExtValueVo : changeExtValueVos) {
            if (Decimal.of(changeExtValueVo.getUpdateValue()).eq(BigDecimal.ZERO)) {
                continue;
            }
            UserExt oldUserExt = queryUSerExt(changeExtValueVo.getUserId(), changeExtValueVo.getExtType());
            UserExt newUserExt = new UserExt();
            BigDecimal oldTUserAmount = oldUserExt.getAmount();
            BeanUtils.copyProperties(oldUserExt, newUserExt);
            newUserExt.setAmount(newUserExt.getAmount().add(changeExtValueVo.getUpdateValue()));
            userExtSign.dealSign(newUserExt);
            updateUserExtList.add(newUserExt);
            if (changeExtValueVo.getChangeType().equals(BaseGameInfoCons.UserExtChangeType.打码量增加) && configRiskService.isAgentModel()) {
                TUser byId = userService.getById(changeExtValueVo.getUserId());
                DailyActiveUser dailyActiveUser = new DailyActiveUser();
                dailyActiveUser.setUserId(newUserExt.getUserId());
                dailyActiveUser.setTurnoverAmount(changeExtValueVo.getUpdateValue());
                dailyActiveUser.setActiveDate(DateUtils.getNowDate());
                dailyActiveUser.setParentUserId(byId.getSuperUserId());
                dailyActiveUser.setIsNew(byId.getIsNew());
                dailyActiveUser.setPPath(byId.getPPath());
                dailyActiveUserService.insertDailyActiveUser(dailyActiveUser);
            }

            UserExtChange userExtChange = new UserExtChange();
            userExtChange.setExtType(newUserExt.getType());
            userExtChange.setUserId(newUserExt.getUserId());
            userExtChange.setTgUserId(newUserExt.getUserTgId());
            TUser user = userService.getById(newUserExt.getUserId());
            userExtChange.setSuperUserId(user.getSuperUserId());
            userExtChange.setChannelId(user.getChannelId());
            userExtChange.setOrderNo(changeExtValueVo.getOrderNo());
            userExtChange.setOrderType(changeExtValueVo.getOrderType());
            userExtChange.setAccountType(changeExtValueVo.getAccountType());
            userExtChange.setType(changeExtValueVo.getChangeType());
            userExtChange.setAmount(changeExtValueVo.getUpdateValue().abs());
            userExtChange.setOldAmount(oldTUserAmount);
            userExtChange.setNewAmount(newUserExt.getAmount());
            userExtChange.setRemark(changeExtValueVo.getRemark());
            userExtChange.setOperator(StrUtil.sub(changeExtValueVo.getOperator(), 0, 10));
            userExtChangeSign.dealSign(userExtChange);
            userExtChange.setUpdateVersion(EsVersionUtil.generateVersion(1));
            userExtChangeList.add(userExtChange);
        }
        // 批量更新和插入
        if (!updateUserExtList.isEmpty()) {
            this.updateBatchById(updateUserExtList);
        }
        if (!userExtChangeList.isEmpty()) {
            userExtChangeService.saveBatch(userExtChangeList);
        }

        log.info("changeExtValue开始,用时{}", Duration.between(start1, LocalDateTime.now()).getSeconds());
        if (extConsumer != null) {
            extConsumer.accept();
        }
    }

    public void changeExtValueSingle(ChangeExtValueVo changeExtValueVo, ExtConsumer extConsumer) {
        LocalDateTime start1 = LocalDateTime.now();
        log.info("changeExtValue开始,时间{}", start1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        if (Decimal.of(changeExtValueVo.getUpdateValue()).eq(BigDecimal.ZERO)) {
            return;
        }
        UserExt oldUserExt = queryUSerExt(changeExtValueVo.getUserId(), changeExtValueVo.getExtType());
        if (oldUserExt == null) {
            return;
        }
        UserExt newUserExt = new UserExt();
        BigDecimal oldTUserAmount = oldUserExt.getAmount();
        BeanUtils.copyProperties(oldUserExt, newUserExt);
        newUserExt.setAmount(newUserExt.getAmount().add(changeExtValueVo.getUpdateValue()));
        userExtSign.dealSign(newUserExt);
        this.updateById(newUserExt);

        if (changeExtValueVo.getChangeType().equals(BaseGameInfoCons.UserExtChangeType.打码量增加) && configRiskService.isAgentModel()) {
            TUser byId = userService.getById(changeExtValueVo.getUserId());
            DailyActiveUser dailyActiveUser = new DailyActiveUser();
            dailyActiveUser.setUserId(newUserExt.getUserId());
            dailyActiveUser.setTurnoverAmount(changeExtValueVo.getUpdateValue());
            dailyActiveUser.setActiveDate(DateUtils.getNowDate());
            dailyActiveUser.setParentUserId(byId.getSuperUserId());
            dailyActiveUser.setIsNew(byId.getIsNew());
            dailyActiveUser.setPPath(byId.getPPath());
            dailyActiveUserService.insertDailyActiveUser(dailyActiveUser);
        }

        UserExtChange userExtChange = new UserExtChange();
        userExtChange.setExtType(newUserExt.getType());
        userExtChange.setUserId(newUserExt.getUserId());
        userExtChange.setTgUserId(newUserExt.getUserTgId());
        TUser user = userService.getById(newUserExt.getUserId());
        userExtChange.setSuperUserId(user.getSuperUserId());
        userExtChange.setChannelId(user.getChannelId());
        userExtChange.setOrderNo(changeExtValueVo.getOrderNo());
        userExtChange.setOrderType(changeExtValueVo.getOrderType());
        userExtChange.setAccountType(changeExtValueVo.getAccountType());
        userExtChange.setType(changeExtValueVo.getChangeType());
        userExtChange.setAmount(changeExtValueVo.getUpdateValue().abs());
        userExtChange.setOldAmount(oldTUserAmount);
        userExtChange.setNewAmount(newUserExt.getAmount());
        userExtChange.setRemark(changeExtValueVo.getRemark());
        userExtChange.setOperator(StrUtil.sub(changeExtValueVo.getOperator(), 0, 10));
        userExtChangeSign.dealSign(userExtChange);
        userExtChangeService.save(userExtChange);

        log.info("changeExtValue开始,用时{}", Duration.between(start1, LocalDateTime.now()).getSeconds());
        if (extConsumer != null) {
            extConsumer.accept();
        }
    }

    public UserExt changeExtValue(ChangeExtValueVo changeExtValueVo, ExtConsumer extConsumer) {
        UserExt oldUserExt = queryUSerExt(changeExtValueVo.getUserId(), changeExtValueVo.getExtType());
        //变动金额零,跳过本次
        if (Decimal.of(changeExtValueVo.getUpdateValue()).eq(BigDecimal.ZERO)) {
            return oldUserExt;
        }
        //账变后的用户信息
        UserExt newUserExt = new UserExt();
        BigDecimal oldTUserAmount = oldUserExt.getAmount();
        BeanUtils.copyProperties(oldUserExt, newUserExt);
        newUserExt.setAmount(newUserExt.getAmount().add(changeExtValueVo.getUpdateValue()));

        //生成签名
        userExtSign.dealSign(newUserExt);
        boolean result = this.updateById(newUserExt);

        if (result) {
            //添加账变
            UserExtChange userExtChange = new UserExtChange();
            userExtChange.setExtType(newUserExt.getType());
            userExtChange.setUserId(newUserExt.getUserId());
            userExtChange.setTgUserId(newUserExt.getUserTgId());
            TUser user = userService.getById(newUserExt.getUserId());
            userExtChange.setSuperUserId(user.getSuperUserId());
            userExtChange.setChannelId(user.getChannelId());
            userExtChange.setOrderNo(changeExtValueVo.getOrderNo());
            userExtChange.setOrderType(changeExtValueVo.getOrderType());
            userExtChange.setAccountType(changeExtValueVo.getAccountType());
            userExtChange.setType(changeExtValueVo.getChangeType());
            userExtChange.setAmount(changeExtValueVo.getUpdateValue().abs());
            userExtChange.setOldAmount(oldTUserAmount);
            userExtChange.setNewAmount(newUserExt.getAmount());
            userExtChange.setRemark(changeExtValueVo.getRemark());
            userExtChange.setOperator(StrUtil.sub(changeExtValueVo.getOperator(), 0, 10));
            userExtChangeSign.dealSign(userExtChange);
            userExtChangeService.save(userExtChange);
        } else {
            throw new RuntimeException("额外属性账变未成功 !");
        }
        //执行附加程序
        extConsumer.accept();
        return newUserExt;
    }

    public void changeRebateToAmount(ChangeExtValueVo changeExtValueVo, ExtConsumer extConsumer) {
        BigDecimal amount = changeExtValueVo.getUpdateValue();
        //不取绝对值了直接为负的去掉
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        //变动金额零,跳过本次

        //额外属性变更
        UserExt oldUserExt = queryUSerExt(changeExtValueVo.getUserId(), changeExtValueVo.getExtType());
        //账变后的用户信息
        UserExt newUserExt = new UserExt();
        BeanUtils.copyProperties(oldUserExt, newUserExt);
        //减少反水,返佣
        newUserExt.setAmount(newUserExt.getAmount().subtract(amount));
        //生成签名
        userExtSign.dealSign(newUserExt);
        boolean result = this.updateById(newUserExt);
        if (result) {
            //添加账变
            UserExtChange userExtChange = new UserExtChange();
            userExtChange.setExtType(newUserExt.getType());
            userExtChange.setUserId(newUserExt.getUserId());
            userExtChange.setTgUserId(newUserExt.getUserTgId());
            TUser user = userService.getById(newUserExt.getUserId());
            userExtChange.setSuperUserId(user.getSuperUserId());
            userExtChange.setChannelId(user.getChannelId());
            userExtChange.setOrderNo(changeExtValueVo.getOrderNo());
            userExtChange.setOrderType(changeExtValueVo.getOrderType());
            userExtChange.setAccountType(changeExtValueVo.getAccountType());
            userExtChange.setType(changeExtValueVo.getChangeType());
            userExtChange.setAmount(amount);
            userExtChange.setOldAmount(oldUserExt.getAmount());
            userExtChange.setNewAmount(newUserExt.getAmount());
            userExtChange.setRemark(changeExtValueVo.getRemark());
            userExtChange.setOperator(StrUtil.sub(changeExtValueVo.getOperator(), 0, 10));
            userExtChangeSign.dealSign(userExtChange);
            userExtChangeService.save(userExtChange);
        } else {
            throw new RuntimeException("额外属性账变未成功 !");
        }
        //用户金额变更
        Currency currency = CurrencyService.usdtCurrency;
        TUser user = userService.getById(changeExtValueVo.getUserId());
        UserWallet oldTUserWallet = userWalletService.getUsdtWallet(user, currency);
        //账变后的用户信息
        UserWallet newTUserWallet = new UserWallet();
        BeanUtils.copyProperties(oldTUserWallet, newTUserWallet);
        newTUserWallet.setAmount(newTUserWallet.getAmount().add(amount));
        //定时的话需要吧 转盘的钱加上 这个支出和收入
        Long lastAmountId = oldTUserWallet.getLastAmountId();
        userSign.checkAmountChangeSign(newTUserWallet.getUserId(), oldTUserWallet.getAmount(), null, lastAmountId);
        AmountChange amountChange = new AmountChange();
        amountChange.setUserId(newTUserWallet.getUserId());
        amountChange.setChannelId(user.getChannelId());
        amountChange.setSuperUserId(user.getSuperUserId());
        amountChange.setTgUserId(newTUserWallet.getUserTgId());
        amountChange.setCurrencyId(BaseGameInfoCons.Currrency.UsdtCurrencyId);
        amountChange.setItemId(BaseGameInfoCons.Currrency.itemId);
        amountChange.setChainTag(BaseGameInfoCons.Currrency.chainTag);
        amountChange.setItemName(BaseGameInfoCons.Currrency.itemName);
        amountChange.setOrderNo(changeExtValueVo.getOrderNo());
        amountChange.setOrderType(OrderTypeEnum.rebateAward.getType());
        amountChange.setExchangeType(ExchangeTypeConstants.INTERNAL);
        amountChange.setAccountType(AccountChangeTypeConstants.INCOME);
        //判断是反水还是返佣
        if (changeExtValueVo.getChangeType().equals(BaseGameInfoCons.UserExtChangeType.反水领取)) {
            amountChange.setType(AmountChangeTypeEnum.rebateAward.getType());
        } else if (changeExtValueVo.getChangeType().equals(BaseGameInfoCons.UserExtChangeType.返佣领取)) {
            amountChange.setType(AmountChangeTypeEnum.commissionAward.getType());
        } else if (changeExtValueVo.getChangeType().equals(BaseGameInfoCons.UserExtChangeType.代理工资领取)) {
            amountChange.setType(AmountChangeTypeEnum.agentAmountReceive.getType());
        }
        amountChange.setAmount(amount);
        amountChange.setOldAmount(oldTUserWallet.getAmount());
        amountChange.setNewAmount(newTUserWallet.getAmount());
        amountChange.setRemark(changeExtValueVo.getRemark());
        amountChange.setOperator(StrUtil.sub(changeExtValueVo.getOperator(), 0, 10));
        userSign.dealAmountChangeSign(amountChange);
        result = amountChangeService.save(amountChange);

        //添加账变
        if (result) {
            //记录活跃用户
            redisOnlineService.recordTodayActivePeopleNum(String.valueOf(changeExtValueVo.getUserId()));
            //添加账变日志
            newTUserWallet.setLastAmountId(amountChange.getId());
            userSign.dealWalletSign(newTUserWallet);
            userWalletService.updateById(newTUserWallet);

        } else {
            throw new RuntimeException("账变未成功 !");
        }

        //执行之后的处理逻辑
        extConsumer.accept();
    }

    public void changeExtOrAmount(List<ChangeExtValueVo> changeExtValueVos, ExtConsumer extConsumer, AmountChangeTypeEnum amountChangeTypeEnum,
                                  OrderTypeEnum orderTypeEnum, Boolean isChangeWallet, Integer addWallet, BigDecimal addWalletAmount) {
        String orderNo = "";
        Long userId = 0l;
        for (ChangeExtValueVo changeExtValueVo : changeExtValueVos) {
            BigDecimal amount = changeExtValueVo.getUpdateValue();
            orderNo = changeExtValueVo.getOrderNo();
            userId = changeExtValueVo.getUserId();
            //变动金额小于等于零,跳过本次
            if (Decimal.of(amount).le(BigDecimal.ZERO)) {
                continue;
            }
            changeWithdrawStatement(changeExtValueVo, userId);
            amount = changeExtValueVo.getUpdateValue();
            //额外属性变更
            UserExt oldUserExt = queryUSerExt(changeExtValueVo.getUserId(), changeExtValueVo.getExtType());
            //账变后的用户信息
            UserExt newUserExt = new UserExt();
            BeanUtils.copyProperties(oldUserExt, newUserExt);

            if (Objects.equals(AccountChangeTypeConstants.EXPENSE, changeExtValueVo.getAccountType())) {
                if (Decimal.of(newUserExt.getAmount()).lt(amount)) {
                    throw new RuntimeException("额外属性账变未成功 !");
                }
                newUserExt.setAmount(newUserExt.getAmount().subtract(amount));
                //如果额外属性小于0 了 则不对

            }

            if (Objects.equals(AccountChangeTypeConstants.INCOME, changeExtValueVo.getAccountType())) {
                newUserExt.setAmount(newUserExt.getAmount().add(amount));
            }

            //生成签名
            userExtSign.dealSign(newUserExt);
            Boolean result = this.updateById(newUserExt);

            if (result) {
                //添加账变
                UserExtChange userExtChange = new UserExtChange();
                userExtChange.setExtType(newUserExt.getType());
                userExtChange.setUserId(newUserExt.getUserId());
                userExtChange.setTgUserId(newUserExt.getUserTgId());
                TUser tUser = userService.getById(newUserExt.getUserId());
                userExtChange.setChannelId(tUser.getChannelId());
                userExtChange.setSuperUserId(tUser.getSuperUserId());
                userExtChange.setOrderNo(changeExtValueVo.getOrderNo());
                userExtChange.setOrderType(changeExtValueVo.getOrderType());
                userExtChange.setAccountType(changeExtValueVo.getAccountType());
                userExtChange.setType(changeExtValueVo.getChangeType());
                userExtChange.setAmount(amount);
                userExtChange.setOldAmount(oldUserExt.getAmount());
                userExtChange.setNewAmount(newUserExt.getAmount());
                userExtChange.setRemark(changeExtValueVo.getRemark());
                userExtChange.setOperator(StrUtil.sub(changeExtValueVo.getOperator(), 0, 10));
                userExtChangeSign.dealSign(userExtChange);
                userExtChangeService.save(userExtChange);
            } else {
                throw new RuntimeException("额外属性账变未成功 !");
            }
        }

        if (isChangeWallet) {
            if (Decimal.of(addWalletAmount).le(BigDecimal.ZERO)) {
                //执行之后的处理逻辑先生成订单然后return
                extConsumer.accept();
                return;
            }
            //用户金额变更
            Currency currency = CurrencyService.usdtCurrency;
            TUser user = userService.getById(userId);
            UserWallet oldTUserWallet = userWalletService.getUsdtWallet(user, currency);
            //账变后的用户信息
            UserWallet newTUserWallet = new UserWallet();
            BeanUtils.copyProperties(oldTUserWallet, newTUserWallet);
            if (addWallet > 0) {
                newTUserWallet.setAmount(newTUserWallet.getAmount().add(addWalletAmount));
            }
            if (addWallet < 0) {
                if (Decimal.of(newTUserWallet.getAmount()).lt(addWalletAmount)) {
                    throw new BusinessException(MessagesUtils.get("bot.changeAmount.YEBZ"));
                }
                newTUserWallet.setAmount(newTUserWallet.getAmount().subtract(addWalletAmount));
            }
            //定时的话需要吧 转盘的钱加上 这个支出和收入
            Long lastAmountId = oldTUserWallet.getLastAmountId();
            userSign.checkAmountChangeSign(newTUserWallet.getUserId(), oldTUserWallet.getAmount(), null, lastAmountId);
            AmountChange amountChange = new AmountChange();
            amountChange.setUserId(newTUserWallet.getUserId());
            amountChange.setChannelId(user.getChannelId());
            amountChange.setSuperUserId(user.getSuperUserId());
            amountChange.setTgUserId(newTUserWallet.getUserTgId());
            amountChange.setCurrencyId(BaseGameInfoCons.Currrency.UsdtCurrencyId);
            amountChange.setItemId(BaseGameInfoCons.Currrency.itemId);
            amountChange.setChainTag(BaseGameInfoCons.Currrency.chainTag);
            amountChange.setItemName(BaseGameInfoCons.Currrency.itemName);
            amountChange.setOrderNo(orderNo);
            amountChange.setOrderType(orderTypeEnum.getType());
            amountChange.setExchangeType(ExchangeTypeConstants.INTERNAL);
            amountChange.setAccountType(addWallet > 0 ? AccountChangeTypeConstants.INCOME : AccountChangeTypeConstants.EXPENSE);
            amountChange.setType(amountChangeTypeEnum.getType());
            amountChange.setAmount(addWalletAmount);
            amountChange.setOldAmount(oldTUserWallet.getAmount());
            amountChange.setNewAmount(newTUserWallet.getAmount());
            userSign.dealAmountChangeSign(amountChange);
            Boolean result = amountChangeService.save(amountChange);

            //添加账变
            if (result) {
                //记录活跃用户
                redisOnlineService.recordTodayActivePeopleNum(String.valueOf(userId));
                //添加账变日志
                newTUserWallet.setLastAmountId(amountChange.getId());
                userSign.dealWalletSign(newTUserWallet);
                userWalletService.updateById(newTUserWallet);

            }

        }

        //执行之后的处理逻辑
        extConsumer.accept();

    }

    public void changeWithdrawStatement(ChangeExtValueVo changeExtValueVo, Long userId) {
        if (Objects.equals(changeExtValueVo.getChangeType(), BaseGameInfoCons.UserExtChangeType.提现打码量增加) || Objects.equals(changeExtValueVo.getChangeType(), BaseGameInfoCons.UserExtChangeType.人工上分)) {
            //提现打码量增加的时候
            BigDecimal userStatement = this.queryUSerExt(userId, BaseGameInfoCons.UserExtType.打码量).getAmount();
            BigDecimal original = this.queryUSerExt(userId, BaseGameInfoCons.UserExtType.提现打码量).getAmount();
            BigDecimal finalAmount = (userStatement.compareTo(original) > 0 ? userStatement : original).add(changeExtValueVo.getUpdateValue());
            BigDecimal money = finalAmount.subtract(original);
            changeExtValueVo.setUpdateValue(money);
            changeExtValueVo.setRemark("当前累计打码量: " + BigDecimalUtils.trim(userStatement) + ", 提现打码量 " + BigDecimalUtils.trim(original) + ", 使用" + (userStatement.compareTo(original) >= 0 ? "累计打码量" :
                    "提现打码量"));
        } else {
            log.info("我没进来");
        }
    }

    public UserWallet changeRechargeAmount(List<ChangeExtValueVo> changeExtValueVos, ExtConsumer extConsumer, Currency currency, BigDecimal amount,
                                           BigDecimal bonusAmount, OrderTypeEnum orderTypeEnum, AmountChangeTypeEnum amountChangeTypeEnum,
                                           String merchantCode) {
        String orderNo = "";
        Long userId = 0l;
        String operator = null;
        String remark = null;
        for (ChangeExtValueVo changeExtValueVo : changeExtValueVos) {
            BigDecimal amountExt = changeExtValueVo.getUpdateValue();
            orderNo = changeExtValueVo.getOrderNo();
            userId = changeExtValueVo.getUserId();
            //变动金额零,跳过本次
            if (Decimal.of(amountExt).eq(BigDecimal.ZERO)) {
                continue;
            }
            changeWithdrawStatement(changeExtValueVo, userId);
            amountExt = changeExtValueVo.getUpdateValue();
            //额外属性变更
            UserExt oldUserExt = queryUSerExt(changeExtValueVo.getUserId(), changeExtValueVo.getExtType());
            operator = changeExtValueVo.getOperator();
            remark = changeExtValueVo.getRemark();
            //账变后的用户信息
            UserExt newUserExt = new UserExt();
            BeanUtils.copyProperties(oldUserExt, newUserExt);
            if (Objects.equals(AccountChangeTypeConstants.EXPENSE, changeExtValueVo.getAccountType())) {
                if (Decimal.of(newUserExt.getAmount()).lt(amountExt)) {
                    throw new RuntimeException("额外属性账变未成功 !");
                }
                newUserExt.setAmount(newUserExt.getAmount().subtract(amountExt));
                //如果额外属性小于0 了 则不对

            }
            if (Objects.equals(AccountChangeTypeConstants.INCOME, changeExtValueVo.getAccountType())) {
                newUserExt.setAmount(newUserExt.getAmount().add(amountExt));
            }
            //生成签名
            userExtSign.dealSign(newUserExt);
            Boolean result = this.updateById(newUserExt);

            if (result) {
                //添加账变
                UserExtChange userExtChange = new UserExtChange();
                userExtChange.setExtType(newUserExt.getType());
                userExtChange.setUserId(newUserExt.getUserId());
                userExtChange.setTgUserId(newUserExt.getUserTgId());
                TUser user = userService.getById(newUserExt.getUserId());
                userExtChange.setSuperUserId(user.getSuperUserId());
                userExtChange.setChannelId(user.getChannelId());
                userExtChange.setOrderNo(changeExtValueVo.getOrderNo());
                userExtChange.setOrderType(changeExtValueVo.getOrderType());
                userExtChange.setAccountType(changeExtValueVo.getAccountType());
                userExtChange.setType(changeExtValueVo.getChangeType());
                //变更后金额-变更前金额
                userExtChange.setAmount(newUserExt.getAmount().subtract(oldUserExt.getAmount()));
//                userExtChange.setAmount(amount);
                userExtChange.setOldAmount(oldUserExt.getAmount());
                userExtChange.setNewAmount(newUserExt.getAmount());
                userExtChange.setRemark(changeExtValueVo.getRemark());
                userExtChange.setOperator(StrUtil.sub(changeExtValueVo.getOperator(), 0, 10));
                userExtChangeSign.dealSign(userExtChange);
                userExtChangeService.save(userExtChange);
            } else {
                throw new RuntimeException("额外属性账变未成功 !");
            }
        }

        //当金额为超过4位的小数时,直接向下取整
        amount = amount.abs().setScale(currency.getDigit(), BigDecimal.ROUND_DOWN);
        //金额不能为零
//        Assert.isFalse(Decimal.of(amount).eq(BigDecimal.ZERO), MessagesUtils.get("bot.common.JEError"));

        UserWallet oldTUser = userWalletService.getUsdtWallet(userService.getById(userId), currency);
        //账变后的用户信息
        UserWallet newTUser = new UserWallet();
        BeanUtils.copyProperties(oldTUser, newTUser);
        //充值金额
        AmountChange betAmountChange = null;
        if (Decimal.of(amount).gt(BigDecimal.ZERO)) {
            BigDecimal userAmount = newTUser.getAmount().add(amount);
            betAmountChange = doRechargePg(currency, amount, orderTypeEnum, amountChangeTypeEnum, orderNo, oldTUser, newTUser,
                    AccountChangeTypeConstants.INCOME, betAmountChange, userAmount, operator, remark);
        }
        //彩金金额
        if (Decimal.of(bonusAmount).gt(BigDecimal.ZERO)) {
            //第二次的时候,第一次的新的变成了老的
            oldTUser = newTUser;
            newTUser = new UserWallet();
            BeanUtils.copyProperties(oldTUser, newTUser);

            BigDecimal userAmount = newTUser.getAmount().add(bonusAmount);
            doRechargePg(currency, bonusAmount, OrderTypeEnum.bonusAward, AmountChangeTypeEnum.bonusAward, orderNo, oldTUser, newTUser,
                    AccountChangeTypeConstants.INCOME, betAmountChange, userAmount, operator, remark);

        }

        //hyh
        try {
            BigDecimal finalAmount = amount;
            Long finalUserId = userId;
            String finalOrderNo = orderNo;
            String finalOperator = operator;
            TUser user = userService.getById(userId);
            BigDecimal usdtAmount = userWalletService.getUsdtAmount(user, CurrencyService.usdtCurrency);
            FinancialTelegramUtil financialBot = BotConfiguration.getFinancialBot(CecuUtil.getDbCode());
            String finalRemark = remark;
            String dbCode = CecuUtil.getDbCode();
            CompletableFuture.runAsync(TtlRunnable.get(() -> {
                cecuUtil.cutDbByCode(dbCode);
                financialBot.sendCZFWMsg(finalUserId, finalAmount, bonusAmount, usdtAmount, amountChangeTypeEnum, finalOrderNo, merchantCode,
                        finalOperator, finalRemark, null);
            }));
        } catch (Exception e) {
            log.info("发送财务信息出错", e);
        }
        //执行之后的处理逻辑
        extConsumer.accept();
        return newTUser;

    }

    private AmountChange doRechargePg(Currency currency, BigDecimal betAmount, OrderTypeEnum orderTypeEnum,
                                      AmountChangeTypeEnum amountChangeTypeEnum, String orderNo, UserWallet oldTUser, UserWallet newTUser,
                                      int accountType, AmountChange betAmountChange, BigDecimal currentAmount, String operator, String remark) {
        //用户金额变更
        newTUser.setAmount(currentAmount);
        //生成签名
        Long lastAmountId = oldTUser.getLastAmountId();

        //还需要去增加提现打码量
        int exchangeType = ExchangeTypeConstants.INTERNAL;
        //游戏订单
        Integer orderType = orderTypeEnum.getType();

        //添加账变日志
        userSign.checkAmountChangeSign(newTUser.getUserId(), oldTUser.getAmount(), betAmountChange, lastAmountId);
        AmountChange amountChange = new AmountChange();
        amountChange.setUserId(newTUser.getUserId());
        TUser user = userService.getById(newTUser.getUserId());
        amountChange.setChannelId(user.getChannelId());
        amountChange.setSuperUserId(user.getSuperUserId());
        amountChange.setTgUserId(newTUser.getUserTgId());
        amountChange.setCurrencyId(currency.getId());
        amountChange.setItemId(currency.getItemId());
        amountChange.setChainTag(currency.getChainTag());
        amountChange.setItemName(currency.getItemName());
        amountChange.setOrderNo(orderNo);
        amountChange.setOrderType(orderType);
        amountChange.setExchangeType(exchangeType);
        amountChange.setAccountType(accountType);
        amountChange.setType(amountChangeTypeEnum.getType());
        amountChange.setAmount(betAmount);
        amountChange.setOldAmount(oldTUser.getAmount());
        amountChange.setNewAmount(newTUser.getAmount());
        amountChange.setRemark(remark);
        amountChange.setOperator(StrUtil.sub(operator, 0, 10));
        userSign.dealAmountChangeSign(amountChange);
        Boolean result = amountChangeService.save(amountChange);

        //添加账变
        if (result) {
            //记录活跃用户
            redisOnlineService.recordTodayActivePeopleNum(String.valueOf(newTUser.getUserId()));
            newTUser.setLastAmountId(amountChange.getId());
            userSign.dealWalletSign(newTUser);
            //修改钱包
            userWalletService.updateById(newTUser);
            //游戏永远是内部地址
            return amountChange;
        } else {
            throw new RuntimeException("账变未成功 !");
        }
    }

    /****
     * 获取用户额外属性统计
     *  未领取反水(type4)/返佣(type5)
     * @param userIdList
     * @return
     */
    public Map<String, Object> userExtCountEndMap(List<Long> userIdList) {
        return userExtMapper.userExtCountEndMap(userIdList);
    }

    /****
     * 通过渠道id关联统计该渠道下用户的未领取反水(type4)/返佣(type5)
     */
    public Map<String, Object> userExtCountEndMapByChannelId(Long channelId) {
        return userExtMapper.userExtCountEndMapByChannelId(channelId);
    }

    /**
     * 批量查询用户额外属性
     *
     * @param userIds 用户ID列表
     * @param type 额外属性类型
     * @return 用户ID与额外属性的映射Map
     */
    public Map<Long, UserExt> batchQueryUserExtMap(List<Long> userIds, Integer type) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashMap<>();
        }

        try {
            LambdaQueryWrapper<UserExt> query = new LambdaQueryWrapper<>();
            query.eq(UserExt::getType, type)
                 .in(UserExt::getUserId, userIds);
            List<UserExt> userExts = this.list(query);

            if (userExts != null && !userExts.isEmpty()) {
                return userExts.stream()
                        .collect(Collectors.toMap(UserExt::getUserId, ext -> ext, (v1, v2) -> v1));
            }
        } catch (Exception e) {
            // 记录异常但不抛出，返回空Map
        }

        return new HashMap<>();
    }

    public Map<Long, UserExtDTO> queryMap(List<Long> userIds) {
        if(CollectionUtils.isEmpty(userIds)){
            return Collections.emptyMap();
        }
        List<UserExtDTO> list = userExtMapper.selectUserExtBatch(userIds);
        return list.stream().collect(Collectors.toMap(UserExtDTO::getUserId, Function.identity()));
    }
}
