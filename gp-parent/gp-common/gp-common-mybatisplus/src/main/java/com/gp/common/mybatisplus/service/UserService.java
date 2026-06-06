package com.gp.common.mybatisplus.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.ttl.TtlRunnable;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.constant.MqEventTypeConstants;
import com.common.core.constant.OrderConstant;
import com.common.core.enums.AmountChangeTypeEnum;
import com.common.core.enums.ComputeWithdrawBetEnum;
import com.common.core.enums.OrderTypeEnum;
import com.common.core.enums.UserSourceEnum;
import com.common.core.util.AmazonService;
import com.common.core.util.RedisUtil;
import com.common.core.util.StringUtils;
import com.common.datasource.util.CecuUtil;
import com.common.rabbitmq.model.MessageBody;
import com.common.rabbitmq.service.RabbitMqTemplate;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.gp.common.base.constant.*;
import com.gp.common.base.constant.mq.MqEnum;
import com.gp.common.base.enums.CodeEnum;
import com.gp.common.base.exception.BusinessException;
import com.gp.common.base.exception.GameException;
import com.gp.common.base.feign.SendNotifyVo;
import com.gp.common.base.utils.*;
import com.gp.common.mybatisplus.config.RedisOnlineService;
import com.gp.common.mybatisplus.configuration.BotConfiguration;
import com.gp.common.mybatisplus.configuration.FinancialTelegramUtil;
import com.gp.common.mybatisplus.dto.UserExtDTO;
import com.gp.common.mybatisplus.entity.*;
import com.gp.common.mybatisplus.entity.Currency;
import com.gp.common.mybatisplus.excel.poi.CsvZipExportUtil;
import com.gp.common.mybatisplus.function.AmountConsumer;
import com.gp.common.mybatisplus.function.ExtConsumer;
import com.gp.common.mybatisplus.manage.UserAmountChangeManage;
import com.gp.common.mybatisplus.manage.UserExtChangeManage;
import com.gp.common.mybatisplus.manage.UserManage;
import com.gp.common.mybatisplus.mapper.ChannelMapper;
import com.gp.common.mybatisplus.mapper.OrderAmountMapper;
import com.gp.common.mybatisplus.mapper.UserMerchantTagMapper;
import com.gp.common.mybatisplus.mapper.UserMapper;
import com.gp.common.mybatisplus.merchantpay.constants.PayMerchantCons;
import com.gp.common.mybatisplus.mq.OrderEntity;
import com.gp.common.mybatisplus.mqService.MqSendEntityService;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import com.gp.common.mybatisplus.param.*;
import com.gp.common.mybatisplus.pay.constant.PayConst;
import com.gp.common.mybatisplus.pay.service.PayService;
import com.gp.common.mybatisplus.until.CheckYZMUtil;
import com.gp.common.mybatisplus.until.UserExtChangeSign;
import com.gp.common.mybatisplus.until.UserExtSign;
import com.gp.common.mybatisplus.until.UserSign;
import com.gp.common.mybatisplus.vo.UserIdAmountVO;
import com.gp.common.mybatisplus.vo.UserListWagerExtAggVO;
import com.gp.common.mybatisplus.vo.UserRwAddressHitVO;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService extends ServiceImpl<UserMapper, TUser> {
    @Resource
    private UserMapper userMapper;

    @Resource
    private UserMerchantTagMapper userMerchantTagMapper;

    @Resource
    private AmountChangeService amountChangeService;
    @Resource
    private UserWalletService userWalletService;
    @Resource
    private OrderWithdrawService orderWithdrawService;
    @Resource
    private MerchantPayService merchantPayService;
    @Resource
    private UserSign userSign;
    @Resource
    private UserManage userManage;
    @Resource
    private UserExtService userExtService;
    @Resource
    private ChannelService channelService;
    @Resource
    private ShareholderService shareholderService;
    @Resource
    private OrderPersonService orderPersonService;
    @Resource
    private UserExtChangeManage userExtChangeManage;
    @Resource
    private UserAmountChangeManage userAmountChangeManage;

    @Resource
    private ComputeWithdrawBetService computeWithdrawBetService;

    @Autowired
    private ConfigAmountService configAmountService;

    @Autowired
    private OrderLawWithdrawService orderLawWithdrawService;

    @Resource
    private ChannelMapper channelMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Resource
    private MqSendEntityService mqSendEntityService;
    @Resource
    private MNacosParam mNacosParam;
    @Resource
    private ConfigRiskService configRiskService;

    @Resource
    private BotItemService botItemService;
    @Resource
    public MybatisBotService mybatisBotService;
    @Resource
    private OrderAmountMapper orderAmountMapper;

    @Resource
    private MerchantService merchantService;

    @Resource
    private CecuUtil cecuUtil;
    @Resource
    private UserExtSign userExtSign;
    @Resource
    private UserExtChangeSign userExtChangeSign;
    @Resource
    private UserExtChangeService userExtChangeService;
    public static final String userKeyFormat = "tg:front:{}";

    @Resource
    private RedisOnlineService redisOnlineService;
    @Resource
    private CheckYZMUtil checkYZMUtil;
    @Resource
    private UserSuperOperationRecordService userSuperOperationRecordService;

    @Resource
    private UserChannelOperationRecordService userChannelOperationRecordService;

    @Resource
    private UserChannelService userChannelService;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private UserDefaultChannelService userDefaultChannelService;

    @Resource
    private ThreadPoolTaskExecutor esThreadPoolTaskExecutor;

    @Resource
    private RabbitMqTemplate rabbitMqTemplate;

    @Resource
    private AmazonService amazonService;
    @Resource
    private UserWebService userWebService;

    /**
     * 等于说是飞机登录
     *
     * @param tgUserId
     * @return
     */
    public TUser getByUserTgId(Long tgUserId) {
        return this.getOne(Wrappers.lambdaQuery(TUser.class).eq(TUser::getUserTgId, tgUserId));

    }

    public TUser initUser(Long tgUserId, String useTgName, String useTgUsername, String pic, Currency currency,
                          UserSourceEnum userSourceEnum,
                          Long channelId, Long upUserId, String email,
                          String password, String lanKey, Boolean sendRedPacket, Boolean defaultLottery) {

        //初始化的时候也要加密
        useTgName = StrUtil.sub(useTgName, 0, 10);
        TUser tUser = userManage.addSignAndSave(tgUserId, useTgName, useTgUsername, pic, userSourceEnum, channelId, upUserId, email,
                password,
                lanKey);

        //初始化用户钱包
        initWallet(tUser, currency);

        //赠送转盘 次数
        giveWheel(tUser);
        //看看是不是
        //判断是不是彩票模式
        Boolean lotteryModel = configRiskService.isLotteryModel();
        if (lotteryModel && defaultLottery) {
            bindUserDefaultLottery(upUserId, channelId, tUser);
        }

        if (sendRedPacket) {
            //发送消息
            if (tUser.getUserTgId() != null) {
                Integer isNew = tUser.getIsNew();

                if (isNew == 1) {
                    mqSendEntityService.sendNewRedPacket(tUser.getUserTgId(), CecuUtil.getDbCode(), lanKey);
                    //发完改状态
                    TUser tUserUpdate = new TUser();
                    tUserUpdate.setUserId(tUser.getUserId());
                    tUserUpdate.setIsNew(0);
                    userMapper.updateById(tUserUpdate);
                }

            }
        }

        return tUser;
    }

    //先看看上级是否有默认比例
    public void bindUserDefaultLottery(Long upUserId, Long channelId, TUser tUser) {
        if (upUserId == null) {
            return;
        }
        UserDefaultChannel upDefaultChannel = userDefaultChannelService.queryByUserId(upUserId);
        if (upDefaultChannel != null) {
            UserChannel userChannel = new UserChannel();
            userChannel.setUserId(tUser.getUserId());
            userChannel.setChannelId(channelId);
            userChannel.setPid(upUserId);
            userChannel.setPPath(tUser.getPPath());
            userChannel.setSuperUserRebate1(upDefaultChannel.getSuperUserRebate1());
            userChannel.setSuperUserRebate2(upDefaultChannel.getSuperUserRebate2());
            userChannel.setSuperUserRebate3(upDefaultChannel.getSuperUserRebate3());
            userChannel.setSuperUserRebate4(upDefaultChannel.getSuperUserRebate4());
            userChannel.setSuperUserRebate5(upDefaultChannel.getSuperUserRebate5());
            userChannel.setSuperUserRebate6(upDefaultChannel.getSuperUserRebate6());
            userChannel.setSuperUserRebate7(upDefaultChannel.getSuperUserRebate7());
            userChannel.setSuperUserRebate8(upDefaultChannel.getSuperUserRebate8());
            userChannel.setSuperUserRebate9(upDefaultChannel.getSuperUserRebate9());
            userChannel.setDividendStatus(upDefaultChannel.getDividendStatus());
            userChannel.setDividendRebate(upDefaultChannel.getDividendRebate());
            userChannelService.saveOrUpdate(userChannel);
        }

    }

    private void giveWheel(TUser user) {
        //不再查了 给放redis吧
        BigDecimal wheelNum = configAmountService.giveWheelNum();
        ExtConsumer extConsumer = () -> {
        };
        if (wheelNum.compareTo(BigDecimal.ZERO) > 0) {
            List<ChangeExtValueVo> arr = CollUtil.newArrayList();
            ChangeExtValueVo changeExtValueVo = new ChangeExtValueVo();
            changeExtValueVo.setUserId(user.getUserId());
            changeExtValueVo.setExtType(UserExtTypeCons.转盘次数);
            changeExtValueVo.setUpdateValue(wheelNum);
            changeExtValueVo.setOrderType(BaseGameInfoCons.UserExtOrderType.新人赠送);
            changeExtValueVo.setAccountType(AccountChangeTypeConstants.INCOME);
            changeExtValueVo.setChangeType(BaseGameInfoCons.UserExtChangeType.转盘增加);
            changeExtValueVo.setOperator(StrUtil.sub(user.getUserTgName(), 0, 10));
            arr.add(changeExtValueVo);
            userExtChangeManage.changeExtOrAmount(arr, extConsumer, AmountChangeTypeEnum.newPeopleAward, OrderTypeEnum.newPeopleAward,
                    false, 1,
                    BigDecimal.ZERO);
            //发送个消息吧

        }

    }

    @SneakyThrows
    public UserWallet initWallet(TUser tUser, Currency currency) {
        UserWallet userWallet = new UserWallet();
        userWallet.setUserId(tUser.getUserId());
        userWallet.setUserTgId(tUser.getUserTgId());
        userWallet.setUserTgName(tUser.getUserTgName());
        userWallet.setUserTgUsername(tUser.getUserTgUsername());
        userWallet.setCurrencyId(currency.getId());
        userWallet.setItemId(currency.getItemId());
        userWallet.setChainTag(currency.getChainTag());
        userWallet.setAmount(BigDecimal.ZERO);
        userWallet.setCreateBy("admin");
        userWallet.setLastAmountId(null);
        //放到查看地址的时候去申请
        userSign.dealWalletSign(userWallet);
        try {
            userWalletService.save(userWallet);
        } catch (Exception e) {
            log.warn("用户id: {}, 币种id: {}, 已存在", userWallet.getUserId(), userWallet.getCurrencyId());
            userWallet = userWalletService.getOne(Wrappers.lambdaQuery(UserWallet.class)
                    .eq(UserWallet::getUserId, userWallet.getUserId())
                    .eq(UserWallet::getCurrencyId, userWallet.getCurrencyId())
            );
        }
        return userWallet;
    }

    public String createUSDTAddress(UserWallet usdtWallet, Currency currency) {
        // 先检查是否配置了 XPay
        MerchantPay xpayConfig = merchantPayService.queryMerchantPayByType(PayMerchantCons.XPAY);

        if (xpayConfig != null) {
            // 如果配置了 XPay，每次都从 XPay 获取地址，不存储
            log.info("检测到 XPay 配置，从 XPay 获取充值地址 - userId: {}, currency: {}, chainTag: {}",
                    usdtWallet.getUserId(), currency.getCurrency(), currency.getChainTag());

            // 根据 chainTag 映射协议ID
            Integer protocolId = getProtocolIdByChainTag(currency.getChainTag());
            if (protocolId == null) {
                log.warn("XPay 不支持该链: {}, 回退到原有逻辑", currency.getChainTag());
                return getOrCreateWalletPayAddress(usdtWallet, currency);
            }
            String dbCode = CecuUtil.getDbCode();
            // 直接从 XPay 获取地址，不存储到数据库
            String xpayParamStr = xpayConfig.getParamStr();
            return PayService.initUserAndGetPayTokenXPay(
                    usdtWallet.getUserId(),
                    xpayParamStr,
                    protocolId,
                    dbCode
            );
        }

        // 没有配置 XPay，使用原有的 WalletPay 逻辑（需要存储）
        log.info("未检测到 XPay 配置，使用 WalletPay 创建充值地址 - userId: {}, currency: {}",
                usdtWallet.getUserId(), currency.getCurrency());
        return getOrCreateWalletPayAddress(usdtWallet, currency);
    }

    /**
     * 获取或创建 WalletPay 充值地址（原有逻辑，需要存储到数据库）
     *
     * @param usdtWallet 用户钱包
     * @param currency   币种信息
     * @return 充值地址
     */
    private String getOrCreateWalletPayAddress(UserWallet usdtWallet, Currency currency) {
        String maskAddr = usdtWallet.getMaskAddr();
        if (StrUtil.isEmpty(maskAddr)) {
            // 创建新地址
            maskAddr = createAddressWithWalletPay(usdtWallet, currency);

            // 更新钱包地址
            usdtWallet.setMaskAddr(maskAddr);
            //再去签名
            userSign.dealWalletSign(usdtWallet);
            Long userId = usdtWallet.getUserId();
            RLock amountLock = redissonClient.getLock(UserAmountChangeManage.getLockStr(userId, PayConst.currency_id));
            try {
                if (!amountLock.tryLock(3, TimeUnit.SECONDS)) {
                    throw new RuntimeException("无法获取用户锁，请稍后再试 !");
                }
                userWalletService.updateById(usdtWallet);
            } catch (InterruptedException e) {
                log.error("获取用户锁失败", e);
                return maskAddr;
            } finally {
                if (amountLock.isHeldByCurrentThread()) {
                    amountLock.unlock();
                }
            }
        }
        return maskAddr;
    }

    /**
     * 使用 WalletPay 创建充值地址（原有逻辑）
     *
     * @param usdtWallet 用户钱包
     * @param currency   币种信息
     * @return 充值地址
     */
    private String createAddressWithWalletPay(UserWallet usdtWallet, Currency currency) {
        String paramStr = merchantPayService.queryCreateAddress();
        return PayService.initUserAndGetPayToken(
                usdtWallet.getUserId(),
                paramStr,
                currency.getItemId(),
                currency.getChainTag(),
                currency.getItemName()
        );
    }

    /**
     * 根据链标签映射 XPay 协议ID
     *
     * @param chainTag 链标签（如 trc20, erc20, bep20）
     * @return XPay 协议ID，如果不支持则返回 null
     */
    private Integer getProtocolIdByChainTag(String chainTag) {
        if (StrUtil.isBlank(chainTag)) {
            return null;
        }

        // 统一转小写处理
        String chain = chainTag.toLowerCase().trim();

        // 映射关系（根据 XPay 文档）
        switch (chain) {
            case "trc20":
            case "trc-20":
                return 1;  // TRC-20 (TRON)
            case "erc20":
            case "erc-20":
                return 2;  // ERC-20 (Ethereum)
            case "bep20":
            case "bep-20":
                return 3;  // BEP-20 (BSC)
            default:
                log.warn("XPay 暂不支持的链类型: {}", chainTag);
                return null;
        }
    }

    /**
     * 只有上分,下分,充值,购买 动的是金额,   提现和返现动的是积分
     * 用户金额变动, 不能直接使用,防止高并发,使用UserAmountChangeManage
     *
     * @param userId               用户Id
     * @param currency
     * @param amount               变动金额
     * @param amountChangeTypeEnum 张变类型
     * @param orderNo              订单号
     * @param remark               备注
     * @param operator             操作人
     * @param extParam1            扩展参数1
     * @param extParam2            扩展参数2
     * @param amountConsumer       张变成功后修改
     * @return
     */
    public UserWallet changeAmount(Long userId, Currency currency, BigDecimal amount, AmountChangeTypeEnum amountChangeTypeEnum,
                                   String orderNo,
                                   String remark, String operator, String extParam1,
                                   String extParam2, AmountConsumer amountConsumer) {
        //当金额为超过4位的小数时,直接向下取整
        amount = amount.abs().setScale(currency.getDigit(), BigDecimal.ROUND_DOWN);
        //金额不能为零
        Assert.isFalse(Decimal.of(amount).eq(BigDecimal.ZERO), MessagesUtils.get("bot.common.JEError"));
        TUser tUser = this.getById(userId);
        UserWallet oldTUser = userWalletService.getUsdtWallet(tUser, currency);
        //账变后的用户信息
        UserWallet newTUser = new UserWallet();
        BigDecimal oldTUserAmount = oldTUser.getAmount();
        BeanUtils.copyProperties(oldTUser, newTUser);
        //收入还是支出 1 收入, 2 支出
        int accountType = AccountChangeTypeConstants.INCOME;
        //内部地址外部地址
        int exchangeType = ExchangeTypeConstants.INTERNAL;
        //订单类型
        Integer orderType = null;
        boolean result = false;
        if (Objects.equals(amountChangeTypeEnum, AmountChangeTypeEnum.personAmountUp)) {
            //手动上分
            accountType = AccountChangeTypeConstants.INCOME;
            orderType = OrderTypeEnum.personAmount.getType();
            newTUser.setAmount(newTUser.getAmount().add(amount));
            amountConsumer.accept(oldTUser, newTUser);
        } else if (Objects.equals(amountChangeTypeEnum, AmountChangeTypeEnum.personAmountDown)) {
            //手动下分
            orderType = OrderTypeEnum.personAmount.getType();
            //支出
            accountType = AccountChangeTypeConstants.EXPENSE;
            //判断金额是否充足
            if (Decimal.of(newTUser.getAmount()).lt(amount)) {
                throw new BusinessException(MessagesUtils.get("bot.changeAmount.XFJEDYDQJE"));
            }
            newTUser.setAmount(newTUser.getAmount().subtract(amount));
            amountConsumer.accept(oldTUser, newTUser);
        } else if (Objects.equals(amountChangeTypeEnum, AmountChangeTypeEnum.userWithdraw)) {
            //用户提现
            //支出
            //看看用户的提现打码量是否大于
            judgeWithdrawStatement(userId);
            accountType = AccountChangeTypeConstants.EXPENSE;
            exchangeType = ExchangeTypeConstants.EXTERNAL;
            orderType = OrderTypeEnum.userWithdraw.getType();
            //判断金额是否充足
            if (Decimal.of(newTUser.getAmount()).lt(amount)) {

                throw new BusinessException(MessagesUtils.get("bot.changeAmount.TXJEDYDQJE"));
            }
            newTUser.setAmount(newTUser.getAmount().subtract(amount));
            amountConsumer.accept(oldTUser, newTUser);

        } else if (Objects.equals(amountChangeTypeEnum, AmountChangeTypeEnum.withdrawReject)) {
            //没有订单
            exchangeType = ExchangeTypeConstants.INTERNAL;
            //支出
            accountType = AccountChangeTypeConstants.INCOME;
            orderType = OrderTypeEnum.userWithdraw.getType();
            newTUser.setAmount(newTUser.getAmount().add(amount));
            amountConsumer.accept(oldTUser, newTUser);
        } else if (Objects.equals(amountChangeTypeEnum, AmountChangeTypeEnum.withdrawFail)) {
            //没有订单
            exchangeType = ExchangeTypeConstants.INTERNAL;
            //支出
            accountType = AccountChangeTypeConstants.INCOME;
            orderType = OrderTypeEnum.userWithdraw.getType();
            newTUser.setAmount(newTUser.getAmount().add(amount));
            amountConsumer.accept(oldTUser, newTUser);

        } else if (Objects.equals(amountChangeTypeEnum, AmountChangeTypeEnum.checkFail)) {
            //没有订单
            exchangeType = ExchangeTypeConstants.INTERNAL;
            //支出
            accountType = AccountChangeTypeConstants.INCOME;
            orderType = OrderTypeEnum.userWithdraw.getType();
            newTUser.setAmount(newTUser.getAmount().add(amount));
            amountConsumer.accept(oldTUser, newTUser);
        } else if (Objects.equals(amountChangeTypeEnum, AmountChangeTypeEnum.gameBet)) {
            //游戏投注
            exchangeType = ExchangeTypeConstants.INTERNAL;
            accountType = AccountChangeTypeConstants.EXPENSE;
            orderType = OrderTypeEnum.gameOrder.getType();
            if (Decimal.of(newTUser.getAmount()).lt(amount)) {
                throw new BusinessException(MessagesUtils.get("bot.changeAmount.YEBZ"));
            }
            newTUser.setAmount(newTUser.getAmount().subtract(amount));
            amountConsumer.accept(oldTUser, newTUser);
        } else if (Objects.equals(amountChangeTypeEnum, AmountChangeTypeEnum.gameAward)) {
            //游戏反奖
            exchangeType = ExchangeTypeConstants.INTERNAL;
            accountType = AccountChangeTypeConstants.EXPENSE;
            orderType = OrderTypeEnum.gameOrder.getType();
            newTUser.setAmount(newTUser.getAmount().add(amount));
            amountConsumer.accept(oldTUser, newTUser);
        } else if (Objects.equals(amountChangeTypeEnum, AmountChangeTypeEnum.gameCancel)) {
            //游戏反奖
            exchangeType = ExchangeTypeConstants.INTERNAL;
            accountType = AccountChangeTypeConstants.INCOME;
            orderType = OrderTypeEnum.gameOrder.getType();
            newTUser.setAmount(newTUser.getAmount().add(amount));
            amountConsumer.accept(oldTUser, newTUser);
        } else if (Objects.equals(amountChangeTypeEnum, AmountChangeTypeEnum.rebateAward)) {
            //反水奖励
            exchangeType = ExchangeTypeConstants.INTERNAL;
            accountType = AccountChangeTypeConstants.INCOME;
            orderType = OrderTypeEnum.rebateAward.getType();
            newTUser.setAmount(newTUser.getAmount().add(amount));
            amountConsumer.accept(oldTUser, newTUser);
        } else if (Objects.equals(amountChangeTypeEnum, AmountChangeTypeEnum.bonusAward)) {
            //彩金
            exchangeType = ExchangeTypeConstants.INTERNAL;
            accountType = AccountChangeTypeConstants.INCOME;
            orderType = OrderTypeEnum.bonusAward.getType();
            newTUser.setAmount(newTUser.getAmount().add(amount));
            amountConsumer.accept(oldTUser, newTUser);
        } else {
            amountConsumer.accept(oldTUser, newTUser);
        }
        //还有一个是活动归为彩金订单吧
        //定时的话需要吧 转盘的钱加上 这个支出和收入
        Long lastAmountId = oldTUser.getLastAmountId();
        userSign.checkAmountChangeSign(newTUser.getUserId(), oldTUserAmount, null, lastAmountId);
        AmountChange amountChange = new AmountChange();
        amountChange.setUserId(newTUser.getUserId());
        amountChange.setTgUserId(newTUser.getUserTgId());
        TUser user = this.getById(newTUser.getUserId());
        amountChange.setChannelId(user.getChannelId());
        amountChange.setSuperUserId(user.getSuperUserId());
        amountChange.setCurrencyId(currency.getId());
        amountChange.setItemId(currency.getItemId());
        amountChange.setChainTag(currency.getChainTag());
        amountChange.setItemName(currency.getItemName());
        amountChange.setOrderNo(orderNo);
        amountChange.setOrderType(orderType);
        amountChange.setExchangeType(exchangeType);
        amountChange.setAccountType(accountType);
        amountChange.setType(amountChangeTypeEnum.getType());
        amountChange.setAmount(amount);
        amountChange.setOldAmount(oldTUser.getAmount());
        amountChange.setNewAmount(newTUser.getAmount());
        amountChange.setRemark(remark);
        amountChange.setOperator(StrUtil.sub(operator, 0, 10));
        userSign.dealAmountChangeSign(amountChange);
        result = amountChangeService.save(amountChange);
        //添加账变
        if (result) {
            //记录活跃用户
            redisOnlineService.recordTodayActivePeopleNum(String.valueOf(userId));
            //添加账变日志
            newTUser.setLastAmountId(amountChange.getId());
            userSign.dealWalletSign(newTUser);
            result = userWalletService.updateById(newTUser);
            if (Objects.equals(amountChangeTypeEnum, AmountChangeTypeEnum.personAmountDown)) {
                try {
                    BigDecimal finalAmount = amount;
                    BigDecimal usdtAmount = userWalletService.getUsdtAmount(user, CurrencyService.usdtCurrency);
                    FinancialTelegramUtil financialBot = BotConfiguration.getFinancialBot(CecuUtil.getDbCode());
                    //1.真实提现;2.扣除积分
                    if ("1".equals(extParam1)) {

                    }
                    String dbCode = CecuUtil.getDbCode();
                    CompletableFuture.runAsync(TtlRunnable.get(() -> {
                        cecuUtil.cutDbByCode(dbCode);
                        financialBot.sendCZFWMsg(userId, finalAmount, BigDecimal.ZERO, usdtAmount, amountChangeTypeEnum, orderNo, null,
                                operator,
                                remark, extParam1);
                    }));
                } catch (Exception e) {

                }
            }
            //执行之后的处理逻辑
            //            amountConsumer.accept(oldTUser, newTUser);
            return newTUser;
        } else {
            throw new RuntimeException("账变未成功 !");
        }
    }

    private void judgeWithdrawStatement(Long userId) {
        UserExt userExtWithdraw = userExtService.queryUSerExt(userId, UserExtTypeCons.提现打码量);
        UserExt userExtStatement = userExtService.queryUSerExt(userId, UserExtTypeCons.打码量);
        BigDecimal amountWithdraw = userExtWithdraw.getAmount();
        BigDecimal amountStatement = userExtStatement.getAmount();
        if (amountStatement.compareTo(amountWithdraw) < 0) {
            Assert.isFalse(true, StrUtil.format(MessagesUtils.get("bot.withdraw.DMLWDDYQ"),
                    BigDecimalUtils.trim(userExtWithdraw.getAmount()),
                    BigDecimalUtils.trim(amountStatement)));
        }

    }

    public TUser getUserLanguageAndSetByUserID(Long userID) {
        TUser tUser = this.getById(userID);
        if (tUser != null) {
            MessagesUtils.setLang(tUser.getLanKey());
        }
        return tUser;
    }

    /**
     * 查询用户
     *
     * @param userId 用户ID
     * @return 用户
     */

    public TUser selectTUserById(Long userId) {
        TUser user = userMapper.selectTUserById(userId);
        Long channelId = user.getChannelId();
        if (Objects.nonNull(channelId) && !new Long("0").equals(channelId)) {
            Channel channel = channelService.selectChannelById(channelId);
            if (Objects.nonNull(channel)) {
                user.setChannelCode(channel.getChannelCode());
                user.setChannelName(channel.getChannelName());
            }
        }
        //查询股东信息
        Shareholder shareholder = shareholderService.getById(user.getShareholderId());
        if (Objects.nonNull(shareholder)) {
            user.setShareholderName(shareholder.getName());
        }
        //查询上级用户信息
        TUser superUser = this.getById(user.getSuperUserId());
        if (Objects.nonNull(superUser)) {
            user.setSuperUserTgId(superUser.getUserTgId());
            user.setUsername(superUser.getUsername());
            user.setSuperUserTgName(superUser.getUserTgName());
            user.setSuperUserTgUsername(superUser.getUserTgUsername());
        }
        LambdaQueryWrapper<UserWallet> userWalletLambdaQueryWrapper = Wrappers.lambdaQuery(UserWallet.class);
        userWalletLambdaQueryWrapper.eq(UserWallet::getUserId, userId);
        userWalletLambdaQueryWrapper.eq(UserWallet::getCurrencyId, 6);
        userWalletLambdaQueryWrapper.last("limit 1");
        UserWallet userWallet = userWalletService.getOne(userWalletLambdaQueryWrapper);
        if (Objects.nonNull(userWallet)) {
            user.setUsdtBalanceAmount(userWallet.getAmount());
        }
        return user;
    }

    /**
     * 查询用户列表
     *
     * @param param 用户
     * @return 用户
     */

    public List<TUser> selectTUserList(QueryUserParam param) {
        List<TUser> list = userMapper.selectTUserList(param);
        if (CollectionUtils.isNotEmpty(list)) {
            List<Long> userIds = list.stream()
                    .map(TUser::getUserId)
                    .collect(Collectors.toList());

            LambdaQueryWrapper<UserExt> userExtLambdaQueryWrapper = Wrappers.lambdaQuery(UserExt.class);
            userExtLambdaQueryWrapper.in(UserExt::getType, 2, 6, 7);
            userExtLambdaQueryWrapper.in(UserExt::getUserId, userIds);
            List<UserExt> userExts = userExtService.list(userExtLambdaQueryWrapper);
            Map<String, UserExt> userExtMap = null;
            if (CollectionUtils.isNotEmpty(userExts)) {
                userExtMap = userExts.stream()
                        .collect(Collectors.toMap(
                                userExt -> userExt.getUserId() + "_" + userExt.getType(), // 构建键 userId + type
                                userExt -> userExt));
            }

            LambdaQueryWrapper<UserWallet> userWalletLambdaQueryWrapper = Wrappers.lambdaQuery(UserWallet.class);
            userWalletLambdaQueryWrapper.eq(UserWallet::getCurrencyId, 6);
            userWalletLambdaQueryWrapper.in(UserWallet::getUserId, userIds);
            List<UserWallet> userWallets = userWalletService.list(userWalletLambdaQueryWrapper);
            Map<Long, UserWallet> userWalletMap = null;
            if (CollectionUtils.isNotEmpty(userWallets)) {
                userWalletMap = userWallets.stream()
                        .collect(Collectors.toMap(
                                UserWallet::getUserId,
                                userWallet -> userWallet));
            }
            //查询渠道  排除0
            Set<Long> chainIdSet = list.stream().map(TUser::getChannelId).filter(channelId -> channelId != 0).collect(Collectors.toSet());
            Map<Long, Channel> channelMap = new HashMap<>();
            if (!chainIdSet.isEmpty()) {
                LambdaQueryWrapper<Channel> channelLambda = Wrappers.lambdaQuery(Channel.class);
                channelLambda.in(Channel::getId, chainIdSet);
                channelMap = channelService.list(channelLambda).stream()
                        .collect(Collectors.toMap(Channel::getId, channel -> channel, (existing, replacement) -> existing));
            }
            //查询股东 排除0
            Set<Long> shareholderIdSet =
                    list.stream().map(TUser::getShareholderId).filter(shareholderId -> shareholderId != null && shareholderId != 0).collect(Collectors.toSet());
            Map<Long, Shareholder> shareholderMap = new HashMap<>();
            if (!shareholderIdSet.isEmpty()) {
                LambdaQueryWrapper<Shareholder> shareholderLambda = Wrappers.lambdaQuery(Shareholder.class);
                shareholderLambda.in(Shareholder::getId, shareholderIdSet);
                shareholderMap = shareholderService.list(shareholderLambda).stream()
                        .collect(Collectors.toMap(Shareholder::getId, shareholder -> shareholder, (existing, replacement) -> existing));
            }

            //获取所有关联web账号
            LambdaQueryWrapper<UserWeb> userWebLambdaQueryWrapper = Wrappers.lambdaQuery(UserWeb.class);
            userWebLambdaQueryWrapper.in(UserWeb::getUserId, userIds);
            List<UserWeb> userWebHashlist = userWebService.list(userWebLambdaQueryWrapper);
            Map<Long, UserWeb> userWebMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(userWebHashlist)){
                userWebMap = userWebHashlist.stream()
                        .collect(Collectors.toMap(UserWeb::getUserId, userWeb -> userWeb, (existing, replacement) -> existing));

            }

            for (TUser user : list) {
                Long channelId = user.getChannelId();
                if (MapUtils.isNotEmpty(channelMap) && !new Long("0").equals(channelId)) {
                    Channel channel = channelMap.get(channelId);
                    if (Objects.nonNull(channel)) {
                        user.setChannelCode(channel.getChannelCode());
                        user.setChannelName(channel.getChannelName());
                    }
                }
                Long shareholderId = user.getShareholderId();
                if (null != shareholderId && MapUtils.isNotEmpty(shareholderMap) && !new Long("0").equals(shareholderId)) {
                    Shareholder shareholder = shareholderMap.get(shareholderId);
                    if (Objects.nonNull(shareholder)) {
                        user.setShareholderName(shareholder.getName());
                    }
                }
                Long userId = user.getUserId();
                if (null != userId && MapUtils.isNotEmpty(userWebMap) && !new Long("0").equals(userId)) {
                    UserWeb userWeb = userWebMap.get(userId);
                    if (Objects.nonNull(userWeb)) {
                        user.setWebAccount(userWeb.getAccount());
                    }
                }
                user.setCumulativeBetVolume(BigDecimal.ZERO);
                user.setCumulativeRecharge(BigDecimal.ZERO);
                user.setBonusAmount(BigDecimal.ZERO);
                if (Objects.nonNull(userExtMap)) {
                    if (userExtMap.containsKey(user.getUserId() + "_" + 2L)) {
                        user.setCumulativeBetVolume(userExtMap.get(user.getUserId() + "_" + 2L).getAmount());
                    }
                    if (userExtMap.containsKey(user.getUserId() + "_" + 6L)) {
                        user.setBonusAmount(userExtMap.get(user.getUserId() + "_" + 6L).getAmount());
                    }
                    if (userExtMap.containsKey(user.getUserId() + "_" + 7L)) {
                        user.setCumulativeRecharge(userExtMap.get(user.getUserId() + "_" + 7L).getAmount());
                    }
                }
                user.setUsdtBalanceAmount(BigDecimal.ZERO);
                if (Objects.nonNull(userWalletMap) && userWalletMap.containsKey(user.getUserId())) {
                    user.setUsdtBalanceAmount(userWalletMap.get(user.getUserId()).getAmount());
                }
            }
        }

        return list;
    }

    /**
     * 控制后台用户列表专用：按当前页 user_id 批量查库（固定 ~5 次），回填提现汇总 / 客损 / 未完成打码 / 充提差。<br>
     * 条件与原逐个 {@code list(OrderPerson/Withdraw/LawWithdraw/UserExt/UserCountOrder)} 一致。
     */
    public void enrichUserListWithdrawCustomerLossWager(List<TUser> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        List<Long> userIds = list.stream()
                .map(TUser::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (userIds.isEmpty()) {
            return;
        }
        // 人工下分真实提现 + 链上提现成功 + 法币提现成功（均为 USDT currency_id=6）
        Map<Long, BigDecimal> personMap = userIdAmountMap(userMapper.sumOrderPersonWithdrawAmountByUserIds(userIds));
        Map<Long, BigDecimal> withdrawMap = userIdAmountMap(userMapper.sumOrderWithdrawAmountByUserIds(userIds));
        Map<Long, BigDecimal> lawMap = userIdAmountMap(userMapper.sumOrderLawWithdrawAmountByUserIds(userIds));
        // 客损：t_user_count_order 按用户汇总
        Map<Long, BigDecimal> lossMap = userIdAmountMap(userMapper.sumCustomerLossAmountByUserIds(userIds));
        // t_user_ext：type3 提现打码量 − type2 累计打码量
        Map<Long, UserListWagerExtAggVO> wagerMap = new HashMap<>(userIds.size() * 2);
        List<UserListWagerExtAggVO> wagerRows = userMapper.sumUserExtWagerByUserIds(userIds);
        if (CollectionUtils.isNotEmpty(wagerRows)) {
            for (UserListWagerExtAggVO row : wagerRows) {
                if (row.getUserId() != null) {
                    wagerMap.put(row.getUserId(), row);
                }
            }
        }
        for (TUser user : list) {
            Long uid = user.getUserId();
            BigDecimal orderPersonAmount = uid != null ? personMap.getOrDefault(uid, BigDecimal.ZERO) : BigDecimal.ZERO;
            BigDecimal orderWithdrawAmount = uid != null ? withdrawMap.getOrDefault(uid, BigDecimal.ZERO) : BigDecimal.ZERO;
            BigDecimal currencyLawWithdrawAmounts = uid != null ? lawMap.getOrDefault(uid, BigDecimal.ZERO) : BigDecimal.ZERO;
            user.setTotalWithdrawAmount(orderPersonAmount.add(orderWithdrawAmount).add(currencyLawWithdrawAmounts));
            user.setCustomerLossAmount(uid != null ? lossMap.getOrDefault(uid, BigDecimal.ZERO) : BigDecimal.ZERO);
            UserListWagerExtAggVO w = uid != null ? wagerMap.get(uid) : null;
            if (w != null) {
                BigDecimal withdrawWager = w.getWithdrawWager() != null ? w.getWithdrawWager() : BigDecimal.ZERO;
                BigDecimal cumWager = w.getCumWager() != null ? w.getCumWager() : BigDecimal.ZERO;
                user.setUnfinishedWagerAmount(withdrawWager.subtract(cumWager));
            } else {
                user.setUnfinishedWagerAmount(BigDecimal.ZERO);
            }
            // 充提差：累计充值(selectTUserList 已填) − 上面提现合计
            BigDecimal recharge = user.getCumulativeRecharge() != null ? user.getCumulativeRecharge() : BigDecimal.ZERO;
            BigDecimal withdraw = user.getTotalWithdrawAmount() != null ? user.getTotalWithdrawAmount() : BigDecimal.ZERO;
            user.setDifference(recharge.subtract(withdraw));
        }
    }

    /** 批量查询结果转 userId → 金额，便于 getOrDefault */
    private static Map<Long, BigDecimal> userIdAmountMap(List<UserIdAmountVO> rows) {
        if (CollectionUtils.isEmpty(rows)) {
            return Collections.emptyMap();
        }
        Map<Long, BigDecimal> map = new HashMap<>(rows.size() * 2);
        for (UserIdAmountVO row : rows) {
            if (row.getUserId() != null && row.getAmount() != null) {
                map.put(row.getUserId(), row.getAmount());
            }
        }
        return map;
    }

    public List<TUser> exportUserBasicInfo(QueryUserParam param) {
        return userMapper.exportUserBasicInfo(param);
    }

    /**
     * 查询用户数量
     *
     * @param param 用户
     * @return 用户数量
     */
    public Long selectTUserCount(QueryUserParam param) {
        return userMapper.selectTUserCount(param);
    }

    /**
     * 按冲提地址反查用户（独立弹窗接口，最多 500 条）。
     */
    public List<UserRwAddressHitVO> listUsersByRwAddress(String rwAddress) {
        if (StrUtil.isBlank(rwAddress)) {
            return Collections.emptyList();
        }
        return userMapper.selectUsersByRwAddress(rwAddress.trim());
    }

    /**
     * 修改用户
     *
     * @param tUser 用户
     * @return 结果
     */

    public Boolean updateTUser(TUser tUser) {
        boolean result = this.updateById(tUser);
        return result;
    }

    /**
     * 仅修改商户标签：每用户在 {@code t_user_merchant_tag} 一行，{@code merchant_tag_codes} 存逗号分隔串（与前端一致）
     *
     * @param merchantTagCodes 逗号分隔；null 或仅空白表示删除该行
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateMerchantTagCodesOnly(Long userId, String merchantTagCodes, String updateBy) {
        if (userId == null) {
            return false;
        }
        String normalized = merchantTagCodes == null ? null : merchantTagCodes.trim();
        if (normalized != null && normalized.isEmpty()) {
            normalized = null;
        }
        LinkedHashSet<String> codes = new LinkedHashSet<>();
        if (normalized != null) {
            for (String p : normalized.split(",")) {
                String t = p.trim();
                if (!t.isEmpty()) {
                    codes.add(t);
                }
            }
        }
        Date now = DateUtils.getNowDate();
        String by = updateBy != null ? updateBy : "";
        userMerchantTagMapper.delete(Wrappers.lambdaQuery(TUserMerchantTag.class).eq(TUserMerchantTag::getUserId, userId));
        if (!codes.isEmpty()) {
            TUserMerchantTag row = new TUserMerchantTag();
            row.setUserId(userId);
            row.setMerchantTagCodes(String.join(",", codes));
            row.setCreateTime(now);
            row.setUpdateTime(now);
            row.setUpdateBy(by);
            userMerchantTagMapper.insert(row);
        }
        int rows = userMapper.touchUserUpdateAudit(userId, now, by);
        return rows > 0;
    }

    public String convertSeconds(long seconds) {
        long days = seconds / (24 * 3600);
        long hours = (seconds % (24 * 3600)) / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        StringBuilder result = new StringBuilder();
        if (days > 0) {
            result.append(days).append(" " + MessagesUtils.get("bot.time.day"));
        }
        if (hours > 0) {
            result.append(hours).append(" " + MessagesUtils.get("bot.time.hour"));
        }
        if (minutes > 0) {
            result.append(minutes).append(" " + MessagesUtils.get("bot.time.minute"));
        }
        if (remainingSeconds > 0) {
            result.append(remainingSeconds).append(" " + MessagesUtils.get("bot.time.second"));
        }

        return result.toString();
    }

    public int updateChannelByUserId(TUser param, String username) {
        // 先查询用户修改前的原始信息
        TUser originalUser = this.getById(param.getUserId());
        //渠道修改记录
        UserChannelOperationRecord record = new UserChannelOperationRecord();
        record.setUserId(originalUser.getUserId());
        record.setCreateBy(username);
        record.setUpdateBy(username);
        record.setCreateTime(DateUtils.getNowDate());
        if (null != originalUser.getChannelId() && 0 != originalUser.getChannelId()) {
            Channel channel = channelService.getById(originalUser.getChannelId());
            if (null != channel) {
                record.setOriginalChannelId(channel.getId());
                record.setOriginalChannelName(channel.getChannelName());
                if (null != channel.getShareholderId() && 0 != channel.getShareholderId()) {
                    Shareholder shareholder = shareholderService.getById(channel.getShareholderId());
                    if (null != shareholder) {
                        record.setOriginalShareholderId(shareholder.getId());
                        record.setOriginalShareholderName(shareholder.getName());
                    }
                }
            }
        }

        //修改渠道 绑定当前渠道的股东id
        Channel channel = channelMapper.selectOne(Wrappers.lambdaUpdate(Channel.class).eq(Channel::getId, param.getChannelId()));
        if (null != channel) {
            record.setNewChannelId(channel.getId());
            record.setNewChannelName(channel.getChannelName());
            if (null != channel.getShareholderId() && 0 != channel.getShareholderId()) {
                Shareholder shareholder = shareholderService.getById(channel.getShareholderId());
                if (null != shareholder) {
                    record.setNewShareholderId(shareholder.getId());
                    record.setNewShareholderName(shareholder.getName());
                }
            }
        }
        if (null != channel && channel.getShareholderId() != null) {
            param.setShareholderId(channel.getShareholderId());
        } else {
            param.setShareholderId(0L);
        }
        userChannelOperationRecordService.save(record);
        param.setUpdateBy(username);
        Boolean r = this.updateTUser(param);
        return r ? 1 : 0;
    }

    public UserWalletDto changePGGameBalance(Long userId, Currency currency, BigDecimal betAmount, BigDecimal awardAmount, String orderId,
                                             String remark, String operator, String extParam1,
                                             String extParam2, AmountConsumer amountConsumer) {
        return changePGGameBalance(userId, currency, betAmount, awardAmount, orderId, remark, operator, extParam1, extParam2, amountConsumer, null);
    }

    public UserWalletDto changePGGameBalance(Long userId, Currency currency, BigDecimal betAmount, BigDecimal awardAmount, String orderId,
                                             String remark, String operator, String extParam1,
                                             String extParam2, AmountConsumer amountConsumer, TUser tUser) {

        betAmount = betAmount.abs();
        awardAmount = awardAmount.abs();
        if (tUser == null) {
            tUser = this.getById(userId);
        }
        UserWallet oldUserWallet = userWalletService.getUsdtWallet(tUser, currency);
        //账变后的用户信息
        UserWallet newUserWallet = new UserWallet();
        BeanUtils.copyProperties(oldUserWallet, newUserWallet);
        boolean isAmountConsumed = false; // 标志变量，记录是否已执行过 amountConsumer.accept
        //投注账变 投注金额必须大于0
        AmountChange betAmountChange = null;
        if (Decimal.of(betAmount).gt(BigDecimal.ZERO)) {
            //下注扣钱
            //判断金额是否充足
            if (Decimal.of(oldUserWallet.getAmount()).lt(betAmount)) {
                throw new GameException(GameException.lowFunds, MessagesUtils.get("bot.changeAmount.XFJEDYDQJE"));
            }
            //用户金额变更
            BigDecimal userAmount = newUserWallet.getAmount().subtract(betAmount);
            newUserWallet.setAmount(userAmount);
            amountConsumer.accept(oldUserWallet, newUserWallet);
            isAmountConsumed = true; // 标记为已处理
            //游戏永远是内部地址
            betAmountChange = doPg(currency, betAmount, AmountChangeTypeEnum.gameBet, orderId, remark, operator, oldUserWallet,
                    newUserWallet,
                    AccountChangeTypeConstants.EXPENSE, betAmountChange, tUser);
        }

        //返奖账变 返奖金额必须大于0
        if (Decimal.of(awardAmount).gt(BigDecimal.ZERO)) {
            //第二次的时候,第一次的新的变成了老的
            oldUserWallet = newUserWallet;
            newUserWallet = new UserWallet();
            BeanUtils.copyProperties(oldUserWallet, newUserWallet);

            //用户金额变更
            BigDecimal userAmount = newUserWallet.getAmount().add(awardAmount);
            newUserWallet.setAmount(userAmount);
            if (!isAmountConsumed) {
                amountConsumer.accept(oldUserWallet, newUserWallet); // 仅当没有处理过才执行
            }
            isAmountConsumed = true;
            doPg(currency, awardAmount, AmountChangeTypeEnum.gameAward, orderId, remark, operator, oldUserWallet, newUserWallet,
                    AccountChangeTypeConstants.INCOME, betAmountChange, tUser);

        }
        if (!isAmountConsumed) {
            amountConsumer.accept(oldUserWallet, newUserWallet); // 仅当没有处理过才执行
        }

        //执行之后的处理逻辑
        //        amountConsumer.accept(oldUserWallet, newUserWallet);

        UserWalletDto userWalletDto = new UserWalletDto();
        userWalletDto.setOldWallet(oldUserWallet);
        userWalletDto.setNewWallet(newUserWallet);
        return userWalletDto;
    }

    public UserWalletDto changePGGameBalanceMb(Long userId, Currency currency, BigDecimal betAmount, BigDecimal awardAmount, BigDecimal mb,
                                               String orderId, String remark, String operator,
                                               String extParam1, String extParam2, AmountConsumer amountConsumer) {
        betAmount = betAmount.abs();
        awardAmount = awardAmount.abs();
        TUser tUser = this.getById(userId);
        UserWallet oldUserWallet = userWalletService.getUsdtWallet(tUser, currency);
        //账变后的用户信息
        UserWallet newUserWallet = new UserWallet();
        BeanUtils.copyProperties(oldUserWallet, newUserWallet);
        boolean isAmountConsumed = false; // 标志变量，记录是否已执行过 amountConsumer.accept
        //投注账变 投注金额必须大于0
        AmountChange betAmountChange = null;

        if (Decimal.of(betAmount).gt(BigDecimal.ZERO)) {
            //下注扣钱
            //判断金额是否充足
            if (Decimal.of(oldUserWallet.getAmount()).lt(betAmount)) {
                throw new GameException(GameException.lowFunds, MessagesUtils.get("bot.changeAmount.XFJEDYDQJE"));
            }
            if (Decimal.of(oldUserWallet.getAmount()).lt(mb)) {
                throw new GameException(GameException.lowFunds, MessagesUtils.get("bot.changeAmount.XFJEDYDQJE"));
            }
            //用户金额变更
            BigDecimal userAmount = newUserWallet.getAmount().subtract(betAmount);
            newUserWallet.setAmount(userAmount);
            amountConsumer.accept(oldUserWallet, newUserWallet);
            isAmountConsumed = true; // 标记为已处理
            //游戏永远是内部地址
            betAmountChange = doPg(currency, betAmount, AmountChangeTypeEnum.gameBet, orderId, remark, operator, oldUserWallet,
                    newUserWallet,
                    AccountChangeTypeConstants.EXPENSE, betAmountChange, tUser);
        }

        //返奖账变 返奖金额必须大于0
        if (Decimal.of(awardAmount).gt(BigDecimal.ZERO)) {
            //第二次的时候,第一次的新的变成了老的
            oldUserWallet = newUserWallet;
            newUserWallet = new UserWallet();
            BeanUtils.copyProperties(oldUserWallet, newUserWallet);

            //用户金额变更
            BigDecimal userAmount = newUserWallet.getAmount().add(awardAmount);
            newUserWallet.setAmount(userAmount);
            if (!isAmountConsumed) {
                amountConsumer.accept(oldUserWallet, newUserWallet); // 仅当没有处理过才执行
            }
            isAmountConsumed = true;
            doPg(currency, awardAmount, AmountChangeTypeEnum.gameAward, orderId, remark, operator, oldUserWallet, newUserWallet,
                    AccountChangeTypeConstants.INCOME, betAmountChange, tUser);

        }
        if (!isAmountConsumed) {
            amountConsumer.accept(oldUserWallet, newUserWallet); // 仅当没有处理过才执行
        }

        //执行之后的处理逻辑
        //        amountConsumer.accept(oldUserWallet, newUserWallet);

        UserWalletDto userWalletDto = new UserWalletDto();
        userWalletDto.setOldWallet(oldUserWallet);
        userWalletDto.setNewWallet(newUserWallet);
        return userWalletDto;
    }

    private AmountChange doPg(Currency currency, BigDecimal betAmount, AmountChangeTypeEnum amountChangeTypeEnum, String orderId,
                              String remark,
                              String operator, UserWallet oldTUser, UserWallet newTUser, int accountType, AmountChange betAmountChange,
                              TUser tUser) {
        //生成签名
        Long lastAmountId = oldTUser.getLastAmountId();
        int exchangeType = ExchangeTypeConstants.INTERNAL;
        //游戏订单
        Integer orderType = OrderTypeEnum.gameOrder.getType();

        //添加账变日志
        userSign.checkAmountChangeSign(newTUser.getUserId(), oldTUser.getAmount(), betAmountChange, lastAmountId);
        AmountChange amountChange = new AmountChange();
        amountChange.setUserId(newTUser.getUserId());
        amountChange.setTgUserId(newTUser.getUserTgId());
        amountChange.setChannelId(tUser.getChannelId());
        amountChange.setSuperUserId(tUser.getSuperUserId());
        amountChange.setCurrencyId(currency.getId());
        amountChange.setItemId(currency.getItemId());
        amountChange.setChainTag(currency.getChainTag());
        amountChange.setItemName(currency.getItemName());
        amountChange.setOrderNo(orderId);
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
        boolean result = amountChangeService.save(amountChange);

        //添加账变
        if (result) {
            //记录活跃用户
            redisOnlineService.recordTodayActivePeopleNum(String.valueOf(newTUser.getUserId()));
            //游戏永远是内部地址
            newTUser.setLastAmountId(amountChange.getId());
            userSign.dealWalletSign(newTUser);
            //修改钱包
            userWalletService.updateById(newTUser);
            return amountChange;
        } else {
            throw new GameException(GameException.operationFailed, MessagesUtils.get("bot.changeAmount.XFJEDYDQJE"));
        }
    }

    private AmountChange doPgBatch(Currency currency, List<BigDecimal> betAmounts, AmountChangeTypeEnum amountChangeTypeEnum,
                                   List<String> orderIds
            , String remark,
                                   String operator, UserWallet oldTUser, UserWallet newTUser, int accountType, List<Integer> accountTypes,
                                   AmountChange betAmountChange) {
        Long lastAmountId = oldTUser.getLastAmountId();
        int exchangeType = ExchangeTypeConstants.INTERNAL;
        //游戏订单
        Integer orderType = OrderTypeEnum.gameOrder.getType();
        //添加账变日志
        userSign.checkAmountChangeSign(newTUser.getUserId(), oldTUser.getAmount(), betAmountChange, lastAmountId);
        ArrayList<AmountChange> arr = CollUtil.newArrayList();
        BigDecimal oldMoney = oldTUser.getAmount();
        Date date = new Date();
        for (int i = 0; i < betAmounts.size(); i++) {
            //循环的时候一次给 加一秒
            BigDecimal changeMoney = betAmounts.get(i);
            if (changeMoney.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }
            //一次加一秒
            Date createTime = DateUtils.addSeconds(date, i + 1);
            //生成签名
            Integer accountAmountType = accountTypes.get(i);
            BigDecimal newAmount = accountAmountType == AccountChangeTypeConstants.INCOME ? oldMoney.add(betAmounts.get(i)) :
                    oldMoney.subtract(betAmounts.get(i));
            AmountChange amountChange = new AmountChange();
            amountChange.setUserId(newTUser.getUserId());
            amountChange.setTgUserId(newTUser.getUserTgId());
            TUser user = this.getById(newTUser.getUserId());
            amountChange.setChannelId(user.getChannelId());
            amountChange.setSuperUserId(user.getSuperUserId());
            amountChange.setCurrencyId(currency.getId());
            amountChange.setItemId(currency.getItemId());
            amountChange.setChainTag(currency.getChainTag());
            amountChange.setItemName(currency.getItemName());
            amountChange.setOrderNo(orderIds.get(i));
            amountChange.setOrderType(orderType);
            amountChange.setExchangeType(exchangeType);
            amountChange.setAccountType(accountAmountType);
            amountChange.setType(amountChangeTypeEnum.getType());
            amountChange.setAmount(betAmounts.get(i));
            amountChange.setOldAmount(oldMoney);
            amountChange.setNewAmount(newAmount);
            amountChange.setRemark(remark);
            amountChange.setOperator(StrUtil.sub(operator, 0, 10));
            amountChange.setCreateTime(createTime);
            userSign.dealAmountChangeSign(amountChange);
            oldMoney = newAmount;
            arr.add(amountChange);
        }
        boolean result = false;
        if (CollUtil.isNotEmpty(arr)) {
            result = amountChangeService.saveBatch(arr);
            if (result) {
                //记录活跃用户
                redisOnlineService.recordTodayActivePeopleNum(String.valueOf(newTUser.getUserId()));
                //arr 获取最后一个
                AmountChange amountChange = arr.get(arr.size() - 1);
                //游戏永远是内部地址
                newTUser.setLastAmountId(amountChange.getId());
                userSign.dealWalletSign(newTUser);
                //修改钱包
                userWalletService.updateById(newTUser);
                return amountChange;
            } else {
                throw new GameException(GameException.operationFailed, MessagesUtils.get("bot.changeAmount.XFJEDYDQJE"));
            }
        } else {
            return null;
        }

        //添加账变

    }

    public Integer queryLowerNum(Long userId, Integer type) {
        LambdaQueryWrapper<TUser> q = new LambdaQueryWrapper<>();
        q.eq(TUser::getSuperUserId, userId);

        q.ge(type == 1, TUser::getCreateTime, DateUtils.getStartTimeByDate(new Date()));
        return this.count(q);
    }

    /**
     * 上分
     *
     * @param param
     * @param tUser
     * @param currency
     * @return
     */
    public Map<String, Object> upScoreByUserId(UpAndDownScoreParam param, TUser tUser, Currency currency, String operator, String remark) {
        //类型(1 上分, 2 下分)
        Integer orderType = TransferTypeConstants.TOP_UP;
        String orderNo = SnowIdUtil.getId(OrderConstant.OrderPerson);
        if (Objects.isNull(param.getBonusAmount())) {
            param.setBonusAmount(BigDecimal.ZERO);
        }
        if (Objects.isNull(param.getAmount())) {
            param.setAmount(BigDecimal.ZERO);
        }

        List<ChangeExtValueVo> arr = CollUtil.newArrayList();
        if (
                Objects.nonNull(param.getBonusAmount())
                        && param.getBonusAmount().compareTo(BigDecimal.ZERO) == 1
        ) {
            //需要增加一个彩金记录
            ChangeExtValueVo changeExtValueVoBonus = new ChangeExtValueVo();
            changeExtValueVoBonus.setUserId(tUser.getUserId());
            changeExtValueVoBonus.setExtType(UserExtTypeCons.彩金);
            changeExtValueVoBonus.setUpdateValue(param.getBonusAmount());
            changeExtValueVoBonus.setOrderNo(orderNo);
            changeExtValueVoBonus.setOrderType(BaseGameInfoCons.UserExtOrderType.人工上分下分订单);
            changeExtValueVoBonus.setAccountType(AccountChangeTypeConstants.INCOME);
            changeExtValueVoBonus.setChangeType(BaseGameInfoCons.UserExtChangeType.彩金增加);
            changeExtValueVoBonus.setOperator(StrUtil.sub(operator, 0, 10));
            changeExtValueVoBonus.setRemark(remark);
            arr.add(changeExtValueVoBonus);
            //彩金细分
            ChangeExtValueVo changeExtValueVoBonusSmall = new ChangeExtValueVo();
            changeExtValueVoBonusSmall.setUserId(tUser.getUserId());
            changeExtValueVoBonusSmall.setExtType(UserExtTypeCons.充值彩金);
            changeExtValueVoBonusSmall.setUpdateValue(param.getBonusAmount());
            changeExtValueVoBonusSmall.setOrderNo(orderNo);
            changeExtValueVoBonusSmall.setOrderType(BaseGameInfoCons.UserExtOrderType.人工上分下分订单);
            changeExtValueVoBonusSmall.setAccountType(AccountChangeTypeConstants.INCOME);
            changeExtValueVoBonusSmall.setChangeType(BaseGameInfoCons.UserExtChangeType.彩金增加);
            changeExtValueVoBonusSmall.setOperator(StrUtil.sub(operator, 0, 10));
            changeExtValueVoBonusSmall.setRemark(remark);
            arr.add(changeExtValueVoBonusSmall);
        }
        //打码量
        ChangeExtValueVo changeExtValueVoStatement = new ChangeExtValueVo();
        changeExtValueVoStatement.setUserId(tUser.getUserId());
        changeExtValueVoStatement.setExtType(UserExtTypeCons.提现打码量);
        changeExtValueVoStatement.setUpdateValue(
                computeWithdrawBetService.computeWithdrawBetService(
                        param.getAmount()
                        , param.getBonusAmount()
                        , ComputeWithdrawBetEnum.personAmountUp
                )
        );
        changeExtValueVoStatement.setOrderNo(orderNo);
        changeExtValueVoStatement.setOrderType(BaseGameInfoCons.UserExtOrderType.人工上分下分订单);
        changeExtValueVoStatement.setAccountType(AccountChangeTypeConstants.INCOME);
        changeExtValueVoStatement.setChangeType(BaseGameInfoCons.UserExtChangeType.人工上分);
        changeExtValueVoStatement.setOperator(StrUtil.sub(operator, 0, 10));
        changeExtValueVoStatement.setRemark(remark);
        arr.add(changeExtValueVoStatement);

        //累计充值
        ChangeExtValueVo changeExtValueVoRecharge = new ChangeExtValueVo();
        changeExtValueVoRecharge.setUserId(tUser.getUserId());
        changeExtValueVoRecharge.setExtType(UserExtTypeCons.累计充值);
        changeExtValueVoRecharge.setUpdateValue(param.getAmount());
        changeExtValueVoRecharge.setOrderNo(orderNo);
        changeExtValueVoRecharge.setOrderType(BaseGameInfoCons.UserExtOrderType.充值订单);
        changeExtValueVoRecharge.setAccountType(AccountChangeTypeConstants.INCOME);
        changeExtValueVoRecharge.setChangeType(BaseGameInfoCons.UserExtChangeType.累计充值增加);
        changeExtValueVoRecharge.setOperator(StrUtil.sub(operator, 0, 10));
        changeExtValueVoRecharge.setRemark(remark);
        arr.add(changeExtValueVoRecharge);

        UserWallet userWallet = userExtChangeManage.changeRechargeAmount(
                arr, () -> {
                    //1. 生成订单 t_order_person
                    OrderPerson orderPerson = new OrderPerson();
                    orderPerson.setOrderNo(orderNo);
                    orderPerson.setCurrencyId(param.getCurrencyId());
                    orderPerson.setItemId(currency.getItemId());
                    orderPerson.setItemName(currency.getItemName());
                    orderPerson.setChainTag(currency.getChainTag());
                    orderPerson.setExchangeType(ExchangeTypeConstants.INTERNAL);
                    orderPerson.setOrderType(orderType);
                    orderPerson.setUserId(tUser.getUserId());
                    orderPerson.setTgUserId(tUser.getUserTgId());
                    orderPerson.setShareholderId(tUser.getShareholderId());
                    orderPerson.setChannelId(tUser.getChannelId());
                    orderPerson.setAmount(param.getAmount());
                    orderPerson.setBonusAmount(param.getBonusAmount());
                    orderPerson.setRemark(param.getRemark());
                    orderPerson.setCreateBy(param.getCreateBy());
                    orderPerson.setTradeImg(param.getTradeImg());
                    orderPersonService.save(orderPerson);
                }, currency
                , param.getAmount(), param.getBonusAmount()
                , OrderTypeEnum.personAmount, AmountChangeTypeEnum.personAmountUp, null
        );

        String title = new StringBuilder()
                .append(EmojiCons.对号).append(" ").append("<bot.notity.CZCGTX>").toString();

        String content = new StringBuilder()
                .append("<bot.notity.CZCG>")
                .append(" ")
                .append(BigDecimalUtils.trim(param.getAmount()))
                .append(" <bot.money.unit>")
                .append("\n")
                .append("<bot.notity.ZSCJ>")
                .append(" ")
                .append(BigDecimalUtils.trim(param.getBonusAmount()))
                .append(" <bot.money.unit>")
                .append("\n")
                .append("<bot.notity.DQYE>")
                .append(" ")
                .append(BigDecimalUtils.trim(userWallet.getAmount()))
                .append(" ")
                .append(" <bot.money.unit>")
                .toString();
        SendNotifyVo sendNotifyVo = new SendNotifyVo();
        sendNotifyVo.setUserTgId(tUser.getUserTgId());
        sendNotifyVo.setTitle(title);
        sendNotifyVo.setContent(content);
        //      sendNotifyVo.setUserAvatar(tUser);
        sendNotifyVo.setLanKey(tUser.getLanKey());

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("sendNotifyVo", sendNotifyVo);
        resultMap.put("orderNo", orderNo);

        return resultMap;
    }

    /**
     * 下分操作
     *
     * @param param
     * @param tUser
     * @param currency
     * @return
     */
    public SendNotifyVo downScoreByUserId(UpAndDownScoreParam param, TUser tUser, Currency currency, String productId) {
        //类型(1 上分, 2 下分)
        Integer orderType = TransferTypeConstants.WITHDRAW;
        String orderNo = SnowIdUtil.getId(OrderConstant.OrderPerson);

        UserWallet userWallet = userAmountChangeManage.changeBalance(
                tUser.getUserId(), currency
                , param.getAmount(), AmountChangeTypeEnum.personAmountDown
                , orderNo, param.getRemark(), param.getCreateBy()
                , param.getLowerSubtype() + "", null, (oldUser, newUser) -> {
                    //1. 生成订单 t_order_person
                    OrderPerson orderPerson = new OrderPerson();
                    orderPerson.setOrderNo(orderNo);
                    orderPerson.setCurrencyId(param.getCurrencyId());
                    orderPerson.setItemId(currency.getItemId());
                    orderPerson.setItemName(currency.getItemName());
                    orderPerson.setChainTag(currency.getChainTag());
                    orderPerson.setExchangeType(ExchangeTypeConstants.INTERNAL);
                    orderPerson.setOrderType(orderType);
                    orderPerson.setUserId(tUser.getUserId());
                    orderPerson.setTgUserId(tUser.getUserTgId());
                    orderPerson.setShareholderId(tUser.getShareholderId());
                    orderPerson.setChannelId(tUser.getChannelId());
                    orderPerson.setAmount(param.getAmount());
                    orderPerson.setBonusAmount(param.getBonusAmount());
                    orderPerson.setRemark(param.getRemark());
                    orderPerson.setCreateBy(param.getCreateBy());
                    orderPerson.setLowerSubtype(param.getLowerSubtype());
                    orderPerson.setTradeImg(param.getTradeImg());
                    orderPersonService.save(orderPerson);
                }
        );
        //通知用户
        String title = new StringBuilder()
                .append(EmojiCons.对号).append(" ").append(MessagesUtils.get("bot.notity.CJKC")).toString();

        String content = new StringBuilder()
                .append(MessagesUtils.get("bot.notity.XFCG"))
                .append(" ")
                .append(BigDecimalUtils.trim(param.getAmount()))
                .append("\n")
                .append(MessagesUtils.get("bot.notity.DQYE"))
                .append(" ")
                .append(BigDecimalUtils.trim(userWallet.getAmount()))
                .append(" ")
                .append(" <bot.money.unit>")
                .toString();
        SendNotifyVo sendNotifyVo = new SendNotifyVo();
        sendNotifyVo.setUserTgId(tUser.getUserTgId());
        sendNotifyVo.setUserAvatar(mybatisBotService.getMybatisBotConfig().getDownMoneyPic());
        sendNotifyVo.setTitle(title);
        sendNotifyVo.setContent(content);
        sendNotifyVo.setLanKey(tUser.getLanKey());
        mqSendEntityService.sendOrderEntity(OrderEntity.builder().userId(tUser.getUserId()).channelId(tUser.getChannelId())
                .type(MqEventTypeConstants.BACKEND_MANUAL_DEBIT).money(param.getAmount()).bonusAmount(param.getBonusAmount()).orderNo(orderNo)
                .productId(productId)
                .build());
        return sendNotifyVo;

    }

    public List<Long> getCountByChannelId(Long id) {
        return baseMapper.getCountByChannelId(id);
    }

    public List<Long> getCountByChannelIds(ArrayList<Long> arr) {
        return baseMapper.getCountByChannelIds(arr);
    }

    public List<Long> getCountByChannelIdToday(Long id, Date date) {
        return baseMapper.getCountByChannelIdToday(id, date);
    }

    public List<Long> getCountByChannelIdsToday(ArrayList<Long> arr, Date date) {
        return baseMapper.getCountByChannelIdsToday(arr, date);
    }

    public UserWalletDto changeSpribeGameBalance(Long userId, Currency currency, Integer accountType, BigDecimal amount, String orderId,
                                                 String remark, String operator, String extParam1,
                                                 String extParam2, AmountConsumer amountConsumer) {
        return changeSpribeGameBalance(userId, currency, accountType, amount, orderId, remark, operator, extParam1, extParam2, amountConsumer, null);
    }

    public UserWalletDto changeSpribeGameBalance(Long userId, Currency currency, Integer accountType, BigDecimal amount, String orderId,
                                                 String remark, String operator, String extParam1,
                                                 String extParam2, AmountConsumer amountConsumer, TUser tUser) {
        amount = amount.abs();
        if (tUser == null) {
            tUser = this.getById(userId);
        }
        UserWallet oldWallet = userWalletService.getUsdtWallet(tUser, currency);
        //账变后的用户信息
        UserWallet newWallet = new UserWallet();
        BeanUtils.copyProperties(oldWallet, newWallet);
        //当金额大于零,执行金额变动操作   投注账变 投注金额必须大于0
        if (Decimal.of(amount).gt(BigDecimal.ZERO)) {
            if (accountType.equals(AccountChangeTypeConstants.INCOME)) {
                //上分
                BigDecimal userAmount = newWallet.getAmount().add(amount);
                newWallet.setAmount(userAmount);
                amountConsumer.accept(oldWallet, newWallet);
                //游戏永远是内部地址
                doPg(currency, amount, AmountChangeTypeEnum.gameAward, orderId, remark, operator, oldWallet, newWallet,
                        AccountChangeTypeConstants.INCOME, null, tUser);

            } else {
                AmountChangeTypeEnum gameBet = AmountChangeTypeEnum.gameBet;
                if (UserAmountChangeManage.sportSettlement.equals(extParam1)) {
                    //体育结算,不校验用户余额,可以为负值
                    gameBet = AmountChangeTypeEnum.gameAward;
                } else {
                    //否则就是下注,校验金额,判断金额是否充足
                    if (Decimal.of(oldWallet.getAmount()).lt(amount)) {
                        throw new GameException(GameException.lowFunds, MessagesUtils.get("bot.changeAmount.XFJEDYDQJE"));
                    }
                }
                BigDecimal userAmount = newWallet.getAmount().subtract(amount);
                newWallet.setAmount(userAmount);
                amountConsumer.accept(oldWallet, newWallet);
                //游戏永远是内部地址
                doPg(currency, amount, gameBet, orderId, remark, operator, oldWallet, newWallet, AccountChangeTypeConstants.EXPENSE, null, tUser);
            }
        } else {
            amountConsumer.accept(oldWallet, newWallet);
        }
        //执行之后的处理逻辑
        //        amountConsumer.accept(oldWallet, newWallet);
        UserWalletDto userWalletDto = new UserWalletDto();
        userWalletDto.setOldWallet(oldWallet);
        userWalletDto.setNewWallet(newWallet);
        return userWalletDto;
    }

    public UserWalletDto changeSpribeGameBalanceBatch(Long userId, Currency currency, Integer accountType, List<Integer> accountTypes,
                                                      BigDecimal handleMoney, List<BigDecimal> amounts,
                                                      List<String> orderIds, String remark, String operator, String extParam1,
                                                      String extParam2,
                                                      AmountConsumer amountConsumer) {
        return changeSpribeGameBalanceBatch(userId, currency, accountType, accountTypes, handleMoney, amounts, orderIds, remark, operator, extParam1, extParam2, amountConsumer, null);
    }

    public UserWalletDto changeSpribeGameBalanceBatch(Long userId, Currency currency, Integer accountType, List<Integer> accountTypes,
                                                      BigDecimal handleMoney, List<BigDecimal> amounts,
                                                      List<String> orderIds, String remark, String operator, String extParam1,
                                                      String extParam2,
                                                      AmountConsumer amountConsumer, TUser tUser) {
        BigDecimal amount = handleMoney;
        if (tUser == null) {
            tUser = this.getById(userId);
        }
        UserWallet oldWallet = userWalletService.getUsdtWallet(tUser, currency);
        //账变后的用户信息
        UserWallet newWallet = new UserWallet();
        BeanUtils.copyProperties(oldWallet, newWallet);
        //当金额大于零,执行金额变动操作   投注账变 投注金额必须大于0
        if (Decimal.of(amount).gt(BigDecimal.ZERO)) {
            if (accountType.equals(AccountChangeTypeConstants.INCOME)) {
                //上分
                BigDecimal userAmount = newWallet.getAmount().add(amount);
                newWallet.setAmount(userAmount);
                amountConsumer.accept(oldWallet, newWallet);
                //游戏永远是内部地址
                doPgBatch(currency, amounts, AmountChangeTypeEnum.gameAward, orderIds, remark, operator, oldWallet, newWallet,
                        AccountChangeTypeConstants.INCOME, accountTypes, null);

            } else {
                AmountChangeTypeEnum gameBet = AmountChangeTypeEnum.gameBet;
                if (UserAmountChangeManage.sportSettlement.equals(extParam1)) {
                    //体育结算,不校验用户余额,可以为负值
                    gameBet = AmountChangeTypeEnum.gameAward;
                } else {
                    //否则就是下注,校验金额,判断金额是否充足
                    if (Decimal.of(oldWallet.getAmount()).lt(amount)) {
                        throw new GameException(GameException.lowFunds, MessagesUtils.get("bot.changeAmount.XFJEDYDQJE"));
                    }
                }
                BigDecimal userAmount = newWallet.getAmount().subtract(amount);
                newWallet.setAmount(userAmount);
                amountConsumer.accept(oldWallet, newWallet);
                //游戏永远是内部地址
                doPgBatch(currency, amounts, gameBet, orderIds, remark, operator, oldWallet, newWallet, AccountChangeTypeConstants.EXPENSE,
                        accountTypes, null);
            }
        } else {
            amountConsumer.accept(oldWallet, newWallet);
        }
        //执行之后的处理逻辑
        //        amountConsumer.accept(oldWallet, newWallet);
        UserWalletDto userWalletDto = new UserWalletDto();
        userWalletDto.setOldWallet(oldWallet);
        userWalletDto.setNewWallet(newWallet);
        return userWalletDto;
    }

    public UserWalletDto changeSpribeGameBalanceWp(Long userId, Currency currency, Integer accountType, BigDecimal amount, String orderId
            , String remark,
                                                   String operator, String extParam1, String extParam2) {
        amount = amount.abs();
        TUser tUser = this.getById(userId);
        UserWallet oldWallet = userWalletService.getUsdtWallet(tUser, currency);
        //账变后的用户信息
        UserWallet newWallet = new UserWallet();
        BeanUtils.copyProperties(oldWallet, newWallet);
        //当金额大于零,执行金额变动操作   投注账变 投注金额必须大于0
        if (Decimal.of(amount).gt(BigDecimal.ZERO)) {
            if (accountType.equals(AccountChangeTypeConstants.INCOME)) {
                //上分
                BigDecimal userAmount = newWallet.getAmount().add(amount);
                newWallet.setAmount(userAmount);
                //游戏永远是内部地址
                doPg(currency, amount, AmountChangeTypeEnum.walletTransfer, orderId, remark, operator, oldWallet, newWallet,
                        AccountChangeTypeConstants.INCOME, null, tUser);

            } else {
                AmountChangeTypeEnum gameBet = AmountChangeTypeEnum.walletOut;
                //下注,校验金额,判断金额是否充足
                if (Decimal.of(oldWallet.getAmount()).lt(amount)) {
                    throw new GameException(GameException.lowFunds, MessagesUtils.get("bot.changeAmount.XFJEDYDQJE"));
                }
                BigDecimal userAmount = newWallet.getAmount().subtract(amount);
                newWallet.setAmount(userAmount);
                //游戏永远是内部地址
                doPg(currency, amount, gameBet, orderId, remark, operator, oldWallet, newWallet, AccountChangeTypeConstants.EXPENSE, null, tUser);
            }
        }
        //执行之后的处理逻辑
        UserWalletDto userWalletDto = new UserWalletDto();
        userWalletDto.setOldWallet(oldWallet);
        userWalletDto.setNewWallet(newWallet);
        return userWalletDto;
    }

    public BigDecimal queryWithdrawAll(Long userId) {
        LambdaQueryWrapper<OrderPerson> orderPersonQuery = Wrappers.lambdaQuery(OrderPerson.class);
        orderPersonQuery.eq(OrderPerson::getUserId, userId);
        //6 => trc20-usdt
        orderPersonQuery.eq(OrderPerson::getCurrencyId, 6);
        //类型(1 上分, 2 下分)
        orderPersonQuery.eq(OrderPerson::getOrderType, 2);
        //下分类型(1.真实提现;2.扣除积分)
        orderPersonQuery.eq(OrderPerson::getLowerSubtype, 1);
        List<OrderPerson> orderPersonList = orderPersonService.list(orderPersonQuery);
        BigDecimal orderPersonAmount = orderPersonList.stream().map(OrderPerson::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        LambdaQueryWrapper<OrderWithdraw> orderWithdrawQuery = Wrappers.lambdaQuery(OrderWithdraw.class);
        orderWithdrawQuery.eq(OrderWithdraw::getUserId, userId);
        orderWithdrawQuery.eq(OrderWithdraw::getCurrencyId, 6);
        orderWithdrawQuery.eq(OrderWithdraw::getOrderStatus, 4);
        List<OrderWithdraw> orderWithdrawList = orderWithdrawService.list(orderWithdrawQuery);
        BigDecimal orderWithdrawAmount = orderWithdrawList.stream().map(OrderWithdraw::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        LambdaQueryWrapper<OrderLawWithdraw> orderLawWithdrawQuery = Wrappers.lambdaQuery(OrderLawWithdraw.class);
        orderLawWithdrawQuery.eq(OrderLawWithdraw::getUserId, userId);
        orderLawWithdrawQuery.eq(OrderLawWithdraw::getCurrencyId, 6);
        orderLawWithdrawQuery.eq(OrderLawWithdraw::getOrderStatus, 4);
        List<OrderLawWithdraw> orderLawWithdraws = orderLawWithdrawService.list(orderLawWithdrawQuery);
        BigDecimal orderLawWithdrawsAmount = orderLawWithdraws.stream().map(OrderLawWithdraw::getAmount).reduce(BigDecimal.ZERO,
                BigDecimal::add);

        //计算用户提现总额
        return orderPersonAmount.add(orderWithdrawAmount).add(orderLawWithdrawsAmount);
    }

    /**
     * type 0 短信 1 邮箱
     *
     * @param key  登录了是userId 没登录是ip
     * @param type
     */
    public void dealSmsCode(String key, Integer type, String dbCode) {
        Integer numError = 20;
        String redisKey = "";
        String str = "";
        if (type == 0) {
            redisKey = StrUtil.format(RedisKey.sms_user_send_count, dbCode, key);
            numError = Integer.parseInt(configRiskService.sms_send_error_times());
        }
        if (type == 1) {
            redisKey = StrUtil.format(RedisKey.mail_user_send_count, dbCode, key);
            numError = Integer.parseInt(configRiskService.mail_send_error_times());
        }
        //获取发送次数
        String o = redisUtil.strGet(redisKey);
        if (StrUtil.isNotBlank(o)) {
            Integer num = Integer.parseInt(o);
            if (num >= numError) {
                long expire = redisUtil.getExpire(redisKey);
                String s = convertSeconds(expire);
                throw new BusinessException(StringUtils.format(MessagesUtils.get("bot.password.SHCS"), s));
            }
        }

    }

    /**
     * @param tUser    获取用户名
     * @param password
     */
    public void setPassword(TUser tUser, String password) {
        String salt = tUser.getSalt();
        if (StrUtil.isEmpty(salt)) {
            salt = MD5Util.getSalt();
        }
        tUser.setSalt(salt);
        tUser.setPassword(MD5Util.encryptSalt(password, salt));
    }

    @Transactional(rollbackFor = Exception.class)
    public int setUserChannel(TUser param, TUser upUser, String username) {
        List<TUser> updateBatch = new ArrayList<>();
        // 先查询用户修改前的原始信息
        if (upUser != null) {
            if (null != upUser.getPPath() && !"".equals(upUser.getPPath())) {
                param.setPPath(upUser.getPPath() + "," + upUser.getUserId());
            } else {
                param.setPPath(String.valueOf(upUser.getUserId()));
            }
            param.setSuperUserId(upUser.getUserId());
            param.setSuperUserTgId(upUser.getUserTgId());
            param.setChannelId(upUser.getChannelId());
            param.setUpdateBy(username);
            //修改渠道彩票代理的上级用户
            UserChannel userChannel = userChannelService.getOne(Wrappers.lambdaQuery(UserChannel.class).eq(UserChannel::getUserId,
                    param.getUserId()));
            if (userChannel != null) {
                userChannel.setPid(param.getSuperUserId());
                userChannel.setPPath(param.getPPath());
                userChannel.setChannelId(upUser.getChannelId());
                userChannelService.updateById(userChannel);
            }
        }
        updateBatch.add(param);
        //处理所有相关下级
        List<TUser> tUserList = this.queryAllTermUser(param.getUserId(), null, null);
        if (CollectionUtils.isNotEmpty(tUserList)) {
            //根据父级ID分组
            Map<Long, List<TUser>> map = tUserList.stream().collect(Collectors.groupingBy(TUser::getSuperUserId));
            updatePPath(param, map, updateBatch, username);
        }
        boolean b = this.updateBatchById(updateBatch);

        return b ? 1 : 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public int setUserSupervisor(TUser param, String username) {
        //不能互为上级
        TUser byId = this.getById(param.getSuperUserId());
        if (byId != null && Objects.equals(byId.getSuperUserId(), param.getUserId())) {
            throw new RuntimeException("不能互为上级");
        }
        //处理所有相关下级
        List<TUser> tUserList = this.queryAllTermUser(param.getUserId(), null, null);
        //不能是下级用户
        if (CollectionUtils.isNotEmpty(tUserList)) {
            long count = tUserList.stream().filter(x -> x.getUserId().equals(param.getSuperUserId())).count();
            if (count > 0) {
                throw new RuntimeException("不能是下级用户");
            }
        }

        List<TUser> updateBatch = new ArrayList<>();
        // 先查询用户修改前的原始信息
        TUser originalUser = this.getById(param.getUserId());
        //判断当前用户是否有渠道
        if (originalUser.getChannelId() != null) {
            //判断当前用户是否是渠道用户
            Channel channel = channelService.selectChannelById(originalUser.getChannelId());
            if (channel != null && channel.getUserId().equals(param.getUserId())) {
                throw new RuntimeException("渠道用户就是源头，无上级");
            }
        }
        UserSuperOperationRecord record = new UserSuperOperationRecord();
        record.setUserId(originalUser.getUserId());
        record.setCreateBy(username);
        record.setUpdateBy(username);
        record.setCreateTime(DateUtils.getNowDate());
        if (originalUser.getSuperUserId() != null) {
            //获取原上级用户
            record.setOriginalUserId(originalUser.getSuperUserId());
            TUser superUser = this.getById(originalUser.getSuperUserId());
            if (superUser != null) {
                record.setOriginalUserTgName(superUser.getUserTgName());
            }
        }
        TUser upUser = this.getById(param.getSuperUserId());
        if (upUser != null) {
            if (null != upUser.getPPath() && !"".equals(upUser.getPPath())) {
                param.setPPath(upUser.getPPath() + "," + upUser.getUserId());
            } else {
                param.setPPath(String.valueOf(upUser.getUserId()));
            }
            param.setSuperUserId(upUser.getUserId());
            param.setSuperUserTgId(upUser.getUserTgId());
            param.setChannelId(upUser.getChannelId());
            param.setUpdateBy(username);
            record.setNewUserId(upUser.getUserId());
            record.setNewUserTgName(upUser.getUserTgName());
            //修改渠道彩票代理的上级用户
            UserChannel userChannel = userChannelService.getOne(Wrappers.lambdaQuery(UserChannel.class).eq(UserChannel::getUserId,
                    param.getUserId()));
            if (userChannel != null) {
                userChannel.setPid(param.getSuperUserId());
                userChannel.setPPath(param.getPPath());
                userChannel.setChannelId(upUser.getChannelId());
                userChannelService.updateById(userChannel);
            }
        }
        updateBatch.add(param);

        if (CollectionUtils.isNotEmpty(tUserList)) {
            //根据父级ID分组
            Map<Long, List<TUser>> map = tUserList.stream().collect(Collectors.groupingBy(TUser::getSuperUserId));
            updatePPath(param, map, updateBatch, username);
        }
        boolean b = this.updateBatchById(updateBatch);

        //添加修改记录
        userSuperOperationRecordService.save(record);
        //找上级
        CompletableFuture.runAsync(TtlRunnable.get(() -> {
            Long upUserUserId = upUser.getUserId();
            //查询默认
            UserDefaultChannel userDefaultChannel = userDefaultChannelService.queryByUserId(upUserUserId);
            if (userDefaultChannel != null) {
                TUser newUser = this.getById(param.getUserId());
                userDefaultChannelService.setDefaultRebateConfigItem(newUser, userDefaultChannel);
            }
        }));

        return b ? 1 : 0;
    }

    private void updatePPath(TUser upUser, Map<Long, List<TUser>> map, List<TUser> updateBatch, String username) {
        List<TUser> tUserList = map.get(upUser.getUserId());
        if (CollectionUtils.isNotEmpty(tUserList)) {
            for (TUser tUser : tUserList) {
                if (null != upUser.getPPath() && !"".equals(upUser.getPPath())) {
                    tUser.setPPath(upUser.getPPath() + "," + upUser.getUserId());
                } else {
                    tUser.setPPath(String.valueOf(upUser.getUserId()));
                }
                tUser.setSuperUserId(upUser.getUserId());
                tUser.setSuperUserTgId(upUser.getUserTgId());
                tUser.setUpdateBy(username);
                tUser.setChannelId(upUser.getChannelId());
                updateBatch.add(tUser);
                updatePPath(tUser, map, updateBatch, username);
            }
        }

    }

    /**
     * 得到签名参数
     * @param
     * @return
     */

    /**
     * type = 0 显示密码错误 1 显示久密码错误
     */
    public void dealPassWord(TUser user, String passWord, Integer type) {
        //密码未设置判断
        AssertUtil.isFalse(user.getPayPassword() == null, CodeEnum.ERR_502.getCode(), MessagesUtils.get(CodeEnum.ERR_502.getMessage()));
        //锁
        String dbCode = CecuUtil.getDbCode();
        String key = StrUtil.format(RedisKey.password_user_fail_count, dbCode, user.getUserId());
        //获取错误次数
        int numError = Integer.parseInt(configRiskService.password_error_times());
        String o = redisUtil.strGet(key);
        if (StrUtil.isNotBlank(o)) {
            int num = Integer.parseInt(o);
            if (num >= numError) {
                long expire = redisUtil.getExpire(key);
                String s = convertSeconds(expire);
                throw new BusinessException(StringUtils.format(MessagesUtils.get("bot.password.SHCS"), s));
            }
        }

        String encrypt = MD5Util.encryptSalt(passWord, user.getSalt());
        //校验支付密码
        if (Objects.equals(encrypt, user.getPayPassword())) {
            redisUtil.del(key);
        } else {
            long currencyNum = redisUtil.incr(key, 1);
            //错误之后的封锁时长
            int time = Integer.parseInt(configRiskService.ban_password_error_time());
            redisUtil.expire(key, time * 60, TimeUnit.MINUTES);
            //提示信息
            String str = "";
            if (type == null) {
                type = 0;
            }
            if (type == 0) {
                str = MessagesUtils.get("bot.user.ZFMM");
            } else {
                str = MessagesUtils.get("bot.user.JMM") + " ";
            }
            //一旦超过3次提示他 不能还有几次提示封号  你已经输入了错误几次,如果超过5次将冻结1个小时
            if (currencyNum < 3) {
                Assert.isTrue(false, str + MessagesUtils.get("bot.user.CW"));
            } else if (currencyNum < 5) {
                String s = convertSeconds(time * 60 * 60);
                Assert.isTrue(false, StringUtils.format(MessagesUtils.get("bot.user.SCMM"), MessagesUtils.get(str), currencyNum, numError
                        , s));
            } else {
                long expire = redisUtil.getExpire(key);
                String s = convertSeconds(expire);
                throw new BusinessException(StringUtils.format(MessagesUtils.get("bot.password.SHCS"), s));
            }
        }

    }

    public Integer judgeUserSecondRecharge(Long userId) {
        LambdaQueryWrapper<OrderAmount> queryNumVipWrapper = Wrappers.lambdaQuery(OrderAmount.class)
                .eq(OrderAmount::getUserId, userId)
                .eq(OrderAmount::getCurrencyId, BaseGameInfoCons.Currrency.UsdtCurrencyId)
                .eq(OrderAmount::getOrderStatus, OrderAmountOrderStatusConstants.PAYMENT_COMPLETED);
        Integer lastNum1 = orderAmountMapper.selectCount(queryNumVipWrapper);

        LambdaQueryWrapper<OrderPerson> orderPersonQuery = Wrappers.lambdaQuery(OrderPerson.class)
                .eq(OrderPerson::getUserId, userId)
                //类型(1 上分, 2 下分)
                .eq(OrderPerson::getOrderType, 1)
                .eq(OrderPerson::getCurrencyId, BaseGameInfoCons.Currrency.UsdtCurrencyId)
                .gt(OrderPerson::getAmount, BigDecimal.ZERO);//充值金额大于0才算
        Integer lastNum2 = orderPersonService.count(orderPersonQuery);

        return lastNum1 + lastNum2;
    }

    /**
     * 随机获取用户
     *
     * @param i
     * @return
     */
    public List<TUser> randomUser(Integer i) {
        return userMapper.randomUser(i);
    }

    public int updateBotStatusByUserId(TUser param) {
        Boolean r = this.updateTUser(param);
        return r ? 1 : 0;
    }

    /**
     * 判断商户积分是否充足
     *
     * @param amount 充值金额
     * @param code   商户编码
     */
    public void judgeUserScore(BigDecimal amount, String code) {

        Merchant merchant = merchantService.getByCodeMerchant(code);
        //        log.info("判断商户积分是否充足，商户信息:{}", merchant);
        if (ObjectUtils.isEmpty(merchant)) {
            throw new BusinessException(StringUtils.format(MessagesUtils.get("bot.merchant.doesNotExist")));
        } else {
            //比较商户积分是否充足
            if (merchant.getAmount().compareTo(amount) <= 0) {
                throw new BusinessException(StringUtils.format(MessagesUtils.get("bot.merchant.insufficientPoints")));
            }
        }
        cecuUtil.cutDbByCode(code);
    }

    /******
     *  发送mq扣除商户积分
     * @param amount    充值金额
     * @param code      商户编码
     * @param type      帐变类型(1 人工上分2 人工下分3 用户充值4 用户提现7 手动上分8 充值上分9 转盘抽取10 活动奖励11 反水12 返奖13红包14 代理领取)
     * @param orderType 订单类型1.人工上下分订单2充值订单3提现订单 5手动上分订单6 充值上分订单7 转盘抽取订单8 活动奖励订单)9 反水订单10 返奖订单11 返奖订单 12 代理奖金
     * @param username  操作人
     */
    public void sendJudgeUserScoreMq(BigDecimal amount, String code, Integer type, Integer orderType, String username) {
        try {
            UpAndDownMerchantScoreParam merchantScoreParam = new UpAndDownMerchantScoreParam();
            merchantScoreParam.setCode(code);
            merchantScoreParam.setAmount(amount);
            merchantScoreParam.setOrderType(orderType);
            merchantScoreParam.setType(type);
            merchantScoreParam.setExchangeType(ExchangeTypeConstants.INTERNAL);
            merchantScoreParam.setUsername(username);
            //发送mq
            SpringUtil.getBean(RabbitMqTemplate.class).sendMq(MqEnum.merchantScoreMq.getExchange(), MqEnum.merchantScoreMq.getKey(),
                    MessageBody.builder().data(JSON.toJSON(merchantScoreParam)).build());
        } catch (Exception e) {
            log.info("发送消息异常");
        }
        cecuUtil.cutDbByCode(code);
    }

    public List<TUser> queryAllTermUser(Long userId, Date startTime, Date endTime) {
        LambdaQueryWrapper<TUser> q = new LambdaQueryWrapper<>();
        // 使用 apply 方法，并用占位符避免SQL注入
        q.apply("FIND_IN_SET({0}, p_path)", userId);
        if (startTime != null && endTime != null) {
            q.between(TUser::getCreateTime, startTime, endTime);
        }
        return userMapper.selectList(q);
    }

    public void judgeUpUser(Long upUserId) {
        String dbCode = CecuUtil.getDbCode();
        if (checkYZMUtil.checkUp(dbCode)) {
            if (upUserId == null) {
                Assert.isFalse(true, MessagesUtils.get("bot.user.FDLLJ"));
            }
        }
    }

    public Long judgeAddUpUserId() {
        return configRiskService.defaultUpUserId();
    }

    public TransferDto transferBalance(Long fromId, Long toId, List<ChangeExtValueVo> changeExtValueVos, ExtConsumer extConsumer,
                                       Currency currency, BigDecimal amount, String orderNoSend, String orderNoReceive) {
        amount = amount.abs().setScale(currency.getDigit(), BigDecimal.ROUND_DOWN);
        judgeWithdrawStatement(fromId);
        for (ChangeExtValueVo changeExtValueVo : changeExtValueVos) {
            BigDecimal amountExt = changeExtValueVo.getUpdateValue();
            orderNoSend = changeExtValueVo.getOrderNo();
            Long userId = changeExtValueVo.getUserId();
            //变动金额零,跳过本次
            if (Decimal.of(amountExt).eq(BigDecimal.ZERO)) {
                continue;
            }

            userExtService.changeWithdrawStatement(changeExtValueVo, userId);
            amountExt = changeExtValueVo.getUpdateValue();
            //额外属性变更
            UserExt oldUserExt = userExtService.queryUSerExt(changeExtValueVo.getUserId(), changeExtValueVo.getExtType());
            String operator = changeExtValueVo.getOperator();
            String remark = changeExtValueVo.getRemark();
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
            Boolean result = userExtService.updateById(newUserExt);

            if (result) {
                //添加账变
                UserExtChange userExtChange = new UserExtChange();
                userExtChange.setExtType(newUserExt.getType());
                userExtChange.setUserId(newUserExt.getUserId());
                userExtChange.setTgUserId(newUserExt.getUserTgId());
                TUser user = this.getById(newUserExt.getUserId());
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
        }
        //金额不能为零
        Assert.isFalse(Decimal.of(amount).eq(BigDecimal.ZERO), MessagesUtils.get("bot.common.JEError"));
        //查询用户之前信息
        UserWallet oldFromUser = userWalletService.getUsdtWallet(this.getById(fromId), currency);
        BigDecimal oldFromUserAmount = oldFromUser.getAmount();
        //账变后的用户信息
        UserWallet newFromUser = new UserWallet();
        BeanUtils.copyProperties(oldFromUser, newFromUser);

        //查询用户之前信息
        UserWallet oldToUser = userWalletService.getUsdtWallet(this.getById(toId), currency);
        BigDecimal oldToUserAmount = oldToUser.getAmount();
        //账变后的用户信息
        UserWallet newToUser = new UserWallet();
        BeanUtils.copyProperties(oldToUser, newToUser);
        //收入还是支出 1 收入, 2 支出
        int fromAccountType = AccountChangeTypeConstants.INCOME;
        int toAccountType = AccountChangeTypeConstants.INCOME;

        boolean fromResult = false;
        boolean toResult = false;

        //发送者下分
        //支出
        fromAccountType = AccountChangeTypeConstants.EXPENSE;
        //判断金额是否充足
        if (Decimal.of(newFromUser.getAmount()).lt(amount)) {
            throw new BusinessException(MessagesUtils.get("bot.changeAmount.YEBZ"));
        }
        newFromUser.setAmount(newFromUser.getAmount().subtract(amount));
        //接受者上分
        //手动上分
        toAccountType = AccountChangeTypeConstants.INCOME;
        newToUser.setAmount(newToUser.getAmount().add(amount));
        Long lastFromAmountId = oldFromUser.getLastAmountId();
        userSign.checkAmountChangeSign(newFromUser.getUserId(), oldFromUserAmount, null, lastFromAmountId);
        AmountChange fromAmountChange = new AmountChange();
        fromAmountChange.setUserId(newFromUser.getUserId());
        fromAmountChange.setTgUserId(newFromUser.getUserTgId());
        TUser fromUser = this.getById(newFromUser.getUserId());
        fromAmountChange.setChannelId(fromUser.getChannelId());
        fromAmountChange.setSuperUserId(fromUser.getSuperUserId());
        fromAmountChange.setCurrencyId(currency.getId());
        fromAmountChange.setItemId(currency.getItemId());
        fromAmountChange.setChainTag(currency.getChainTag());
        fromAmountChange.setItemName(currency.getItemName());
        fromAmountChange.setOrderNo(orderNoSend);
        fromAmountChange.setOrderType(OrderTypeEnum.transfer.getType());
        fromAmountChange.setExchangeType(ExchangeTypeConstants.INTERNAL);
        fromAmountChange.setAccountType(fromAccountType);
        fromAmountChange.setType(AmountChangeTypeEnum.bonusGive.getType());
        fromAmountChange.setAmount(amount);
        fromAmountChange.setOldAmount(oldFromUser.getAmount());
        fromAmountChange.setNewAmount(newFromUser.getAmount());
        fromAmountChange.setRemark("");
        fromAmountChange.setOperator(StrUtil.sub(newToUser.getUserTgName(), 0, 10));
        userSign.dealAmountChangeSign(fromAmountChange);
        fromResult = amountChangeService.save(fromAmountChange);
        Long lastToAmountId = oldToUser.getLastAmountId();
        userSign.checkAmountChangeSign(newToUser.getUserId(), oldToUserAmount, null, lastToAmountId);
        //添接收人账变日志
        AmountChange toAmountChange = new AmountChange();
        toAmountChange.setUserId(newToUser.getUserId());
        toAmountChange.setTgUserId(newToUser.getUserTgId());
        TUser toUser = this.getById(newToUser.getUserId());
        toAmountChange.setChannelId(toUser.getChannelId());
        toAmountChange.setSuperUserId(toUser.getSuperUserId());
        toAmountChange.setCurrencyId(currency.getId());
        toAmountChange.setItemId(currency.getItemId());
        toAmountChange.setChainTag(currency.getChainTag());
        toAmountChange.setItemName(currency.getItemName());
        toAmountChange.setOrderNo(orderNoReceive);
        toAmountChange.setOrderType(OrderTypeEnum.transfer.getType());
        toAmountChange.setExchangeType(ExchangeTypeConstants.INTERNAL);
        toAmountChange.setAccountType(toAccountType);
        toAmountChange.setType(AmountChangeTypeEnum.bonusReceive.getType());
        toAmountChange.setAmount(amount);
        toAmountChange.setOldAmount(oldToUser.getAmount());
        toAmountChange.setNewAmount(newToUser.getAmount());
        toAmountChange.setRemark("");
        toAmountChange.setOperator(StrUtil.sub(newFromUser.getUserTgName(), 0, 10));
        userSign.dealAmountChangeSign(toAmountChange);
        toResult = amountChangeService.save(toAmountChange);

        //添加账变
        if (fromResult && toResult) {
            //记录活跃用户
            redisOnlineService.recordTodayActivePeopleNum(String.valueOf(newFromUser.getUserId()));
            redisOnlineService.recordTodayActivePeopleNum(String.valueOf(newToUser.getUserId()));
            //添加发送人账变日志
            newToUser.setLastAmountId(toAmountChange.getId());
            newFromUser.setLastAmountId(fromAmountChange.getId());
            userSign.dealWalletSign(newToUser);
            userSign.dealWalletSign(newFromUser);

            toResult = userWalletService.updateById(newToUser);

            fromResult = userWalletService.updateById(newFromUser);

            if (fromResult && toResult) {

            } else {
                throw new RuntimeException("账变未成功 !");
            }
            extConsumer.accept();

            TransferDto transferDto = new TransferDto();
            transferDto.setFromUser(newFromUser);
            transferDto.setToUser(newToUser);
            String key = StrUtil.format(RedisKey.transfer, CecuUtil.getDbCode(), oldFromUser.getUserId());
            redisUtil.incr(key, 1);
            redisUtil.expire(key, DateUtils.getTodayEndTime());

            return transferDto;
        } else {
            throw new RuntimeException("账变未成功 !");
        }
    }

    public Integer queryDirectUser(Long userId) {
        LambdaQueryWrapper<TUser> q = new LambdaQueryWrapper<>();
        q.eq(TUser::getSuperUserId, userId);
        return userMapper.selectCount(q);
    }

    public List<TUser> queryLowUser(Long userId) {
        LambdaQueryWrapper<TUser> q = new LambdaQueryWrapper<>();
        q.eq(TUser::getSuperUserId, userId);
        return userMapper.selectList(q);
    }

    /**
     * 获取所有下级用户及自己的ID
     *
     * @param userId 用户ID
     * @return
     */
    public List<Long> getAllSubUserId(Long userId) {
        List<Long> userIds = new ArrayList<>();
        userIds.add(userId);
        LambdaQueryWrapper<TUser> lambdaQuery = Wrappers.lambdaQuery(TUser.class);
        lambdaQuery.select(TUser::getUserId).last("where find_in_set(" + userId + ",p_path)");
        List<TUser> users = userMapper.selectList(lambdaQuery);
        List<Long> subUserIds = users.stream().map(TUser::getUserId).collect(Collectors.toList());
        userIds.addAll(subUserIds);
        return userIds;
    }

    @Transactional(rollbackFor = Exception.class)
    public void exchangeUserTgId(Long userIdOne, Long userIdTwo) throws Exception {
        TUser tUserOne = this.getById(userIdOne);
        TUser tUserTwo = this.getById(userIdTwo);
        //重新生成
        Long userTgIdOne = tUserOne.getUserTgId();
        Long userTgIdTwo = tUserTwo.getUserTgId();
        if (userTgIdOne == null || userTgIdTwo == null) {
            throw new Exception("其中一个飞机号为空 无法交换");
        }
        String userTgUsernameOne = tUserOne.getUserTgUsername();
        String userTgUsernameTwo = tUserTwo.getUserTgUsername();
        String avatarOne = tUserOne.getUserAvatar();
        String avatarTwo = tUserTwo.getUserAvatar();
        String userTgNameOne = tUserOne.getUserTgName();
        String userTgNameTwo = tUserTwo.getUserTgName();
        tUserOne.setUserTgId(-3l);
        this.updateById(tUserOne);
        tUserTwo.setUserTgId(-2l);
        this.updateById(tUserTwo);
        //先更新在去看主键冲突
        tUserOne.setUserTgId(userTgIdTwo);
        tUserOne.setUserTgUsername(userTgUsernameTwo);
        tUserOne.setUserTgName(userTgNameTwo);
        tUserOne.setUserAvatar(avatarTwo);
        userSign.dealUserSign(tUserOne);
        this.updateById(tUserOne);
        tUserTwo.setUserTgId(userTgIdOne);
        tUserTwo.setUserTgUsername(userTgUsernameOne);
        tUserTwo.setUserTgName(userTgNameOne);
        tUserTwo.setUserAvatar(avatarOne);
        userSign.dealUserSign(tUserTwo);
        this.updateById(tUserTwo);
        LambdaQueryWrapper<UserWallet> qOne = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<UserWallet> qTwo = new LambdaQueryWrapper<>();
        qOne.eq(UserWallet::getUserId, userIdOne);
        qTwo.eq(UserWallet::getUserId, userIdTwo);
        //钱包也带换
        List<UserWallet> oneWallets = userWalletService.list(qOne);
        List<UserWallet> twoWallets = userWalletService.list(qTwo);
        if (oneWallets.size() > 1) {
            //报错
            throw new Exception("用户钱包数量超过1个, 请检查");
        }
        if (twoWallets.size() > 1) {
            //报错
            throw new Exception("用户钱包数量超过1个, 请检查");
        }
        oneWallets.forEach(e -> {
            e.setUserTgId(-3l);
            userWalletService.updateById(e);

        });

        twoWallets.forEach(e -> {
            e.setUserTgId(-2l);
            userWalletService.updateById(e);
        });
        oneWallets.forEach(e -> {
            e.setUserTgId(userTgIdTwo);
            e.setUserTgName(userTgNameTwo);
            e.setUserTgUsername(userTgUsernameTwo);
            userSign.dealWalletSign(e);
            userWalletService.updateById(e);
        });
        twoWallets.forEach(e -> {
            e.setUserTgId(userTgIdOne);
            e.setUserTgName(userTgNameOne);
            e.setUserTgUsername(userTgUsernameOne);
            userSign.dealWalletSign(e);
            userWalletService.updateById(e);
        });
        //还有额外属性
        LambdaQueryWrapper<UserExt> qEOne = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<UserExt> qETwo = new LambdaQueryWrapper<>();
        qEOne.eq(UserExt::getUserId, userIdOne);
        qETwo.eq(UserExt::getUserId, userIdTwo);
        List<UserExt> oneUserExts = userExtService.list(qEOne);
        List<UserExt> twoUserExts = userExtService.list(qETwo);

        oneUserExts.forEach(e -> {
            e.setUserTgId(-3l);
            userExtService.updateById(e);

        });

        twoUserExts.forEach(e -> {
            e.setUserTgId(-2l);
            userExtService.updateById(e);
        });
        oneUserExts.forEach(e -> {
            e.setUserTgId(userTgIdTwo);
            e.setUserTgName(userTgNameTwo);
            e.setUserTgUsername(userTgUsernameTwo);
            userExtSign.dealSign(e);
            userExtService.updateById(e);
        });
        twoUserExts.forEach(e -> {
            e.setUserTgId(userTgIdOne);
            e.setUserTgName(userTgNameOne);
            e.setUserTgUsername(userTgUsernameOne);
            userExtSign.dealSign(e);
            userExtService.updateById(e);
        });

    }

    public List<TUser> selectUserList(QueryUserParam param) {
        return userMapper.selectUserList(param);
    }

    /**
     * 导出用户列表
     *
     * @param param     参数
     * @param key       Redis_key
     * @param fileAdmin 文件前缀
     * @param dbCode    数据库编号
     */
    public void exportUserList(QueryUserParam param, String key, String fileAdmin, String dbCode) {
        // 异步执行任务
        CompletableFuture.runAsync(() -> {
            //手动切库
            cecuUtil.cutDbByCode(dbCode);
            List<TUser> list = new ArrayList<>();
            this.getAllUser(1, 100, param, list);
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(list)) {
                list.forEach(x -> {
                    if (StringUtils.isNotEmpty(x.getUserAvatar())) {
                        x.setUserAvatar(AdminUtil.perfectAdmin(fileAdmin, x.getUserAvatar()));
                    }
                });
            }
            CsvZipExportUtil<TUser> util = new CsvZipExportUtil<TUser>(amazonService, fileAdmin, TUser.class);
            String zipPath = util.exportCsvZipPath(list, "user");
            Map<String, String> map = new HashMap<>();
            map.put("state", "已完成");
            map.put("zipPath", zipPath);
            //保留30分钟
            redisUtil.set(key, map, 60 * 30);
        });
    }

    /**
     * 更新关注等级
     *
     * @param param 参数
     */
    public Boolean updateAttentionLevel(UpdateAttentionLevelParam param) {
        LambdaUpdateWrapper<TUser> updateWrapper = Wrappers.lambdaUpdate(TUser.class);
        updateWrapper.eq(TUser::getUserId, param.getUserId())
                .set(TUser::getFollowStatus, param.getFollowStatus());
        return this.update(updateWrapper);
    }
    @Resource
    private UserCountOrderService userCountOrderService;

    private void getAllUser(int pageNum, int pageSize, QueryUserParam param, List<TUser> allList) {
        // 使用循环代替递归，避免栈溢出
        while (true) {
            try {
                PageHelper.startPage(pageNum, pageSize, "user_id desc");
                List<TUser> list = this.selectTUserList(param);

                // 判断是否还有数据
                if (CollectionUtils.isEmpty(list)) {
                    break; // 没有更多数据时退出循环
                }
                List<Long> userIds = list.stream().map(TUser::getUserId).collect(Collectors.toList());
                LambdaQueryWrapper<OrderPerson> orderPersonQuery = Wrappers.lambdaQuery(OrderPerson.class);
                orderPersonQuery.in(OrderPerson::getUserId, userIds);
                // 6 => trc20-usdt
                orderPersonQuery.eq(OrderPerson::getCurrencyId, 6);
                // 类型(1 上分, 2 下分)
                orderPersonQuery.eq(OrderPerson::getOrderType, 2);
                // 下分类型(1.真实提现;2.扣除积分)
                orderPersonQuery.eq(OrderPerson::getLowerSubtype, 1);
                List<OrderPerson> orderPersonList = orderPersonService.list(orderPersonQuery);
                Map<Long, BigDecimal> orderPersonMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(orderPersonList)) {
                    orderPersonMap = orderPersonList.stream().collect(Collectors.groupingBy(OrderPerson::getUserId,
                            Collectors.mapping(OrderPerson::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
                }

                LambdaQueryWrapper<OrderWithdraw> orderWithdrawQuery = Wrappers.lambdaQuery(OrderWithdraw.class);
                orderWithdrawQuery.in(OrderWithdraw::getUserId, userIds);
                orderWithdrawQuery.eq(OrderWithdraw::getCurrencyId, 6);
                orderWithdrawQuery.eq(OrderWithdraw::getOrderStatus, 4);
                List<OrderWithdraw> orderWithdrawList = orderWithdrawService.list(orderWithdrawQuery);
                Map<Long, BigDecimal> orderWithdrawMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(orderWithdrawList)) {
                    orderWithdrawMap = orderWithdrawList.stream().collect(Collectors.groupingBy(OrderWithdraw::getUserId,
                            Collectors.mapping(OrderWithdraw::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
                }

                // 法币提现订单
                LambdaQueryWrapper<OrderLawWithdraw> orderLawWithdrawQuery = Wrappers.lambdaQuery(OrderLawWithdraw.class);
                orderLawWithdrawQuery.in(OrderLawWithdraw::getUserId, userIds);
                orderLawWithdrawQuery.eq(OrderLawWithdraw::getCurrencyId, 6);
                orderLawWithdrawQuery.eq(OrderLawWithdraw::getOrderStatus, 4);
                List<OrderLawWithdraw> orderLawWithdrawList = orderLawWithdrawService.list(orderLawWithdrawQuery);
                Map<Long, BigDecimal> orderLawWithdrawMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(orderLawWithdrawList)) {
                    orderLawWithdrawMap = orderLawWithdrawList.stream().collect(Collectors.groupingBy(OrderLawWithdraw::getUserId,
                            Collectors.mapping(OrderLawWithdraw::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
                }
                LambdaQueryWrapper<UserExt> query = Wrappers.lambdaQuery(UserExt.class);
                query.in(UserExt::getUserId, userIds);
                List<UserExt> userExtAllList = userExtService.list(query);
                Map<Long, List<UserExt>> userExtMap = new HashMap<>();
                if (CollectionUtils.isNotEmpty(userExtAllList)) {
                    userExtMap = userExtAllList.stream().collect(Collectors.groupingBy(UserExt::getUserId));
                }
                for (TUser user : list) {
                    BigDecimal orderPersonAmount = BigDecimal.ZERO;
                    if (MapUtils.isNotEmpty(orderPersonMap) && orderPersonMap.containsKey(user.getUserId())) {
                        orderPersonAmount = orderPersonMap.get(user.getUserId());
                    }

                    BigDecimal orderWithdrawAmount = BigDecimal.ZERO;
                    if (MapUtils.isNotEmpty(orderWithdrawMap) && orderWithdrawMap.containsKey(user.getUserId())) {
                        orderWithdrawAmount = orderWithdrawMap.get(user.getUserId());
                    }

                    BigDecimal currencyLawWithdrawAmounts = BigDecimal.ZERO;
                    if (MapUtils.isNotEmpty(orderLawWithdrawMap) && orderLawWithdrawMap.containsKey(user.getUserId())) {
                        currencyLawWithdrawAmounts = orderLawWithdrawMap.get(user.getUserId());
                    }
                    // 计算用户提现总额
                    user.setTotalWithdrawAmount(orderPersonAmount.add(orderWithdrawAmount).add(currencyLawWithdrawAmounts));
                    // 客损
                    BigDecimal customerLossAmount = userCountOrderService.querySumCustomerLossAmount(user.getUserId());
                    user.setCustomerLossAmount(customerLossAmount);

                    if (MapUtils.isNotEmpty(userExtMap) && userExtMap.containsKey(user.getUserId())) {
                        List<UserExt> userExtList = userExtMap.get(user.getUserId());
                        BigDecimal withdrawAmount = userExtList.stream()
                                .filter(x -> Objects.equals(x.getType(), BaseGameInfoCons.UserExtType.提现打码量)).findAny()
                                .map(UserExt::getAmount).orElse(BigDecimal.ZERO);
                        BigDecimal totalAmount = userExtList.stream()
                                .filter(x -> Objects.equals(x.getType(), BaseGameInfoCons.UserExtType.打码量)).findAny()
                                .map(UserExt::getAmount).orElse(BigDecimal.ZERO);
                        // 未完成打码量 = 提现打码量 - 累计打码量
                        user.setUnfinishedWagerAmount(withdrawAmount.subtract(totalAmount));
                    } else {
                        user.setUnfinishedWagerAmount(BigDecimal.ZERO);
                    }
                }
                allList.addAll(list);

                // 获取分页信息
                Page<TUser> localPage = PageHelper.getLocalPage();
                if (localPage == null || localPage.getPages() <= pageNum) {
                    break; // 当前页已是最后一页时退出循环
                }

                pageNum++; // 更新页码继续下一轮查询
            } catch (Exception e) {
                // 异常处理：记录日志并终止流程
                log.error("查询用户列表失败，pageNum={}, pageSize={}", pageNum, pageSize, e);
                throw new RuntimeException("查询用户列表失败", e);
            }
        }
    }

    public void exportUserLists(QueryUserParam param, String key, String fileAdmin, String dbCode) {

        CompletableFuture.runAsync(() -> {

            cecuUtil.cutDbByCode(dbCode);

            int page = 1;
            int pageSize = 500;

            List<TUser> allList = new ArrayList<>();
            try {
            while (true) {
                final int currentPage = page;
                PageHelper.startPage(currentPage, pageSize);
                List<TUser> baseList = userMapper.selectUserBasePage(param);
//                // 立即获取分页信息（在调用其他数据库操作之前）
                    Page<TUser> localPage = PageHelper.getLocalPage();

                log.info("第 {} 页数据，共 {} 条，localPage={}", currentPage, baseList.size(), localPage);

                if (CollectionUtils.isEmpty(baseList)) {
                    log.info("数据为空，退出循环");
                    break;
                }
                // 处理数据
                fillUserFinanceDataBatch(baseList);

                baseList.forEach(x -> {
                    if (StringUtils.isNotEmpty(x.getUserAvatar())) {
                        x.setUserAvatar(AdminUtil.perfectAdmin(fileAdmin, x.getUserAvatar()));
                    }
                });

                allList.addAll(baseList);

                // 判断是否是最后一页
                if (localPage == null) {
                    log.warn("localPage 为 null，使用 baseList.size() 判断");
                    if (baseList.size() < pageSize) {
                        log.info("数据少于 pageSize，退出循环");
                        break;
                    }
                } else {
                    if (currentPage >= localPage.getPages()) {
                        log.info("已是最后一页，退出循环");
                        break;
                    }
                }
                page++;
                // 添加日志
                log.info("继续处理第 {} 页", page);
            }
                log.info("所有数据处理完成，总共 {} 条", allList.size());

            CsvZipExportUtil<TUser> util =
                    new CsvZipExportUtil<>(amazonService, fileAdmin, TUser.class);

            String zipPath = util.exportCsvZipPath(allList, "user");

            Map<String, String> map = new HashMap<>();
            map.put("state", "已完成");
            map.put("zipPath", zipPath);

            redisUtil.set(key, map, 60 * 30);
            } catch (Exception e) {
                log.error("导出用户数据失败", e);
                redisUtil.del(key);
            }
        });
    }

    private void fillUserFinanceDataBatch(List<TUser> baseList) {
        if (CollectionUtils.isEmpty(baseList)) {
            return;
        }

        // 当前批次所有用户ID
        List<Long> ids = baseList.stream()
                .map(TUser::getUserId)
                .collect(Collectors.toList());

        // 查询用户扩展表聚合数据
        // 聚合 bonus, cumulativeBetVolume, cumulativeRecharge, unfinishedWager
        Map<Long, UserExtDTO> extMap = userExtService.queryMap(ids);

        // 查询用户钱包余额
        Map<Long, BigDecimal> walletMap = userWalletService.queryMap(ids);

        // 查询用户总提现金额
        Map<Long, BigDecimal> withdrawMap = orderPersonService.queryWithdrawMap(ids);

        // 查询用户累计客户亏损
        Map<Long, BigDecimal> customerLossMap = userCountOrderService.queryCustomerLossMap(ids);

        // 填充每个用户数据
        for (TUser user : baseList) {
            Long uid = user.getUserId();

            // t_user_ext 聚合
            UserExtDTO ext = extMap.get(uid);
            if (ext != null) {
                user.setBonusAmount(ext.getBonusAmount() != null ? ext.getBonusAmount() : BigDecimal.ZERO);
                user.setCumulativeBetVolume(ext.getCumulativeBetVolume() != null ? ext.getCumulativeBetVolume() : BigDecimal.ZERO);
                user.setCumulativeRecharge(ext.getCumulativeRecharge() != null ? ext.getCumulativeRecharge() : BigDecimal.ZERO);
                BigDecimal unfinished = ext.getUnfinishedWagerAmount() != null ? ext.getUnfinishedWagerAmount() : BigDecimal.ZERO;
                user.setUnfinishedWagerAmount(unfinished);
            } else {
                user.setBonusAmount(BigDecimal.ZERO);
                user.setCumulativeBetVolume(BigDecimal.ZERO);
                user.setCumulativeRecharge(BigDecimal.ZERO);
                user.setUnfinishedWagerAmount(BigDecimal.ZERO);
            }

            // 钱包余额
            user.setUsdtBalanceAmount(walletMap.getOrDefault(uid, BigDecimal.ZERO));

            // 总提现
            user.setTotalWithdrawAmount(withdrawMap.getOrDefault(uid, BigDecimal.ZERO));

            // 客户亏损
            user.setCustomerLossAmount(customerLossMap.getOrDefault(uid, BigDecimal.ZERO));
        }
    }
}
