package com.gp.common.mybatisplus.merchantpay.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.ttl.TtlRunnable;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.common.core.constant.MqEventTypeConstants;
import com.common.core.constant.OrderConstant;
import com.common.core.enums.AmountChangeTypeEnum;
import com.common.core.util.RedisLockUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.OrderWithdrawOrderStatusConstants;
import com.gp.common.base.constant.RedisLockKey;
import com.gp.common.base.constant.WithdrawStatusConstants;
import com.gp.common.base.utils.BigDecimalUtils;
import com.gp.common.base.utils.Decimal;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.base.utils.SnowIdUtil;
import com.gp.common.mybatisplus.configuration.BotConfiguration;
import com.gp.common.mybatisplus.configuration.FinancialTelegramUtil;
import com.gp.common.mybatisplus.dto.ReqPayWithdrawNew;
import com.gp.common.mybatisplus.entity.*;
import com.gp.common.mybatisplus.function.AmountConsumer;
import com.gp.common.mybatisplus.manage.UserAmountChangeManage;
import com.gp.common.mybatisplus.merchantpay.constants.PayTypeCons;
import com.gp.common.mybatisplus.mq.OrderEntity;
import com.gp.common.mybatisplus.mqService.MqSendEntityService;
import com.gp.common.mybatisplus.service.*;
import com.gp.common.mybatisplus.until.OrderLawWithdrawSign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 提现业务服务
 */
@Service
@Slf4j
public class WithdrawService {

    @Resource
    private UserService userService;
    @Resource
    private ConfigAmountService configAmountService;
    @Resource
    private MerchantPayService merchantPayService;
    @Resource
    private OrderLawWithdrawSign orderLawWithdrawSign;
    @Resource
    private UserAmountChangeManage userAmountChangeManage;
    @Resource
    private MybatisBotService mybatisBotService;
    //    @Resource
    //    private MessageSend messageSend;
    @Resource
    private OrderLawWithdrawService orderLawWithdrawService;
    @Resource
    private CurrencyService currencyService;
    @Resource
    private MqSendEntityService mqSendEntityService;
    @Resource
    private UserPayMethodService userPayMethodService;
    @Resource
    private PayTypeService payTypeService;
    @Resource
    private RedisLockUtil redisLockUtil;

    /**
     * 处理提现请求
     */
    public boolean processWithdraw(ReqPayWithdrawNew reqPayWithdrawNew, TUser tUser) {
        //校验支付密码
        userService.dealPassWord(tUser, reqPayWithdrawNew.getPayPassword(), 0);
        //校验提现状态
        validateWithdrawStatus(tUser);
        //判断之前是不是存在未审核的提现订单
        List<OrderLawWithdraw> orderLawWithdraws = orderLawWithdrawService.list(Wrappers.lambdaQuery(OrderLawWithdraw.class)
                .eq(OrderLawWithdraw::getUserId, tUser.getUserId())
                .eq(OrderLawWithdraw::getOrderStatus, 0)
        );
        if (CollUtil.isNotEmpty(orderLawWithdraws)) {
            throw new RuntimeException("当前有未提现订单未处理完,请该笔处理完再提现 !");
        }

        //查询该支付类型是否需要支付卡,需要的话是否绑定
        PayType payType = payTypeService.getByCodeWithCache(reqPayWithdrawNew.getPayTypeCode());
        if (payType == null || payType.getIsWithdraw() != 1) {
            throw new RuntimeException("当前支付类型不存在或不支持提现 !");
        }
        UserPayMethod userPayMethod = resolveWithdrawPayMethod(reqPayWithdrawNew, tUser);

        //查询用户该类型的支付卡

        if (payType.getBindType() == 1 && userPayMethod == null) {
            throw new RuntimeException("未绑定对应的支付卡 !");
        }

        //校验提现金额是否合规
        BigDecimal amount = validateWithdrawAmount(reqPayWithdrawNew.getMoney());
        BigDecimal fee = BigDecimal.ZERO;
        BigDecimal realAmount = amount.subtract(fee);

        //当前汇率
        BigDecimal exchangeRate = payType.getExchangeRate();
        //到账法币金额
        BigDecimal lawAmount = realAmount.multiply(exchangeRate);
        String orderNo = SnowIdUtil.getId(OrderConstant.LawOrderWithdraw);
        Currency currency = CurrencyService.usdtCurrency;
        AmountConsumer consumer = (oldUser, newUser) -> {
            OrderLawWithdraw orderLawWithdraw = new OrderLawWithdraw();
            orderLawWithdraw.setOrderNo(orderNo);
            orderLawWithdraw.setCurrencyId(currency.getId());
            orderLawWithdraw.setItemId(currency.getItemId());
            orderLawWithdraw.setItemName(currency.getItemName());
            orderLawWithdraw.setChainTag(currency.getChainTag());
            orderLawWithdraw.setUserId(tUser.getUserId());
            orderLawWithdraw.setTgUserId(tUser.getUserTgId());
            orderLawWithdraw.setShareholderId(tUser.getShareholderId());
            orderLawWithdraw.setChannelId(tUser.getChannelId());
            //用户提现信息
            if (userPayMethod != null) {
                orderLawWithdraw.setBindData(userPayMethod.getBindData());
            }
            orderLawWithdraw.setAmount(amount);
            orderLawWithdraw.setFee(fee);
            orderLawWithdraw.setRealAmount(realAmount);
            orderLawWithdraw.setRealLawAmount(lawAmount);
            orderLawWithdraw.setRatio(exchangeRate);
            //财务出款时添加
            //            orderLawWithdraw.setPayMerchantId();
            //            orderLawWithdraw.setPayChannelId();
            orderLawWithdraw.setPayTypeId(payType.getId().longValue());
            orderLawWithdraw.setLanKey(LocaleContextHolder.getLocale().toString());
            orderLawWithdraw.setCreateTime(new Date());
            orderLawWithdraw.setOrderStatus(OrderWithdrawOrderStatusConstants.PENDING_REVIEW);
            orderLawWithdrawSign.dealSign(orderLawWithdraw);
            orderLawWithdrawService.save(orderLawWithdraw);
        };
        userAmountChangeManage.changeBalance(tUser.getUserId(), currency, amount, AmountChangeTypeEnum.userWithdraw,
                orderNo, MessagesUtils.get("bot.me.me.msg.YHTX"), tUser.getUserTgUsername(), null, null, consumer);
        sendWithdrawMessage(tUser, orderNo, amount);
        return true;
    }

    private UserPayMethod resolveWithdrawPayMethod(ReqPayWithdrawNew reqPayWithdrawNew, TUser tUser) {
        String address = StrUtil.trimToNull(reqPayWithdrawNew.getAddress());
        if (address != null) {
            UserPayMethod userPayMethod = new UserPayMethod();
            Map<String, Object> bindData = new HashMap<>();
            bindData.put("address", address);
            userPayMethod.setBindData(bindData);
            return userPayMethod;
        }
        return userPayMethodService.getOne(Wrappers.lambdaQuery(UserPayMethod.class)
                .eq(UserPayMethod::getUserId, tUser.getUserId())
                .eq(UserPayMethod::getPayTypeCode, reqPayWithdrawNew.getPayTypeCode())
                .eq(UserPayMethod::getUseType, 2)
        );
    }

    @Resource
    private PayBlockchainService payBlockchainService;

    /**
     * 处理提现请求
     */
    public boolean processBlackWithdraw(ReqPayWithdrawNew reqPayWithdrawNew, TUser tUser) {
        //校验支付密码
        userService.dealPassWord(tUser, reqPayWithdrawNew.getPayPassword(), 0);
        //校验提现状态
        validateWithdrawStatus(tUser);
        //判断之前是不是存在未审核的提现订单
        List<OrderLawWithdraw> orderLawWithdraws = orderLawWithdrawService.list(Wrappers.lambdaQuery(OrderLawWithdraw.class)
                .eq(OrderLawWithdraw::getUserId, tUser.getUserId())
                .eq(OrderLawWithdraw::getOrderStatus, 0)
        );
        if (CollUtil.isNotEmpty(orderLawWithdraws)) {
            throw new RuntimeException("当前有未提现订单未处理完,请该笔处理完再提现 !");
        }
        PayBlockchain payBlockchain = payBlockchainService.getById(reqPayWithdrawNew.getBlockId());
        if (payBlockchain == null || payBlockchain.getStatus() != 1 || payBlockchain.getIsWithdraw() != 1) {
            throw new RuntimeException("当前冷钱包类型已关闭 !");
        }
        //必然存在的支付类型，内置的
        PayType payType = payTypeService.getByCodeWithCache(PayTypeCons.BLACK_PAY);
        //查询冷钱包该币种，
        //校验提现金额是否合规
        BigDecimal amount = validateWithdrawAmount(reqPayWithdrawNew.getMoney());
        BigDecimal fee = BigDecimal.ZERO;
        BigDecimal realAmount = amount.subtract(fee);

        //查询该通道是否开启

        //当前汇率
        BigDecimal exchangeRate = payBlockchain.getPlatformRate();
        //到账法币金额
        BigDecimal lawAmount = realAmount.multiply(exchangeRate);
        String orderNo = SnowIdUtil.getId(OrderConstant.LawOrderWithdraw);
        Currency currency = CurrencyService.usdtCurrency;
        HashMap<String, Object> bindData = new HashMap<>();
        bindData.put("item", payBlockchain.getItem());
        bindData.put("chain", payBlockchain.getChain());
        bindData.put("icon", payBlockchain.getIcon());
        bindData.put("addr", reqPayWithdrawNew.getBlockAddr());
        AmountConsumer consumer = (oldUser, newUser) -> {
            OrderLawWithdraw orderLawWithdraw = new OrderLawWithdraw();
            orderLawWithdraw.setOrderNo(orderNo);
            orderLawWithdraw.setCurrencyId(currency.getId());
            //            orderLawWithdraw.setItemId(null);
            orderLawWithdraw.setItemName(payBlockchain.getItem());
            orderLawWithdraw.setChainTag(payBlockchain.getChain());
            orderLawWithdraw.setUserId(tUser.getUserId());
            orderLawWithdraw.setTgUserId(tUser.getUserTgId());
            orderLawWithdraw.setShareholderId(tUser.getShareholderId());
            orderLawWithdraw.setChannelId(tUser.getChannelId());
            //用户提现信息
            orderLawWithdraw.setBindData(bindData);
            orderLawWithdraw.setAmount(amount);
            orderLawWithdraw.setFee(fee);
            orderLawWithdraw.setRealAmount(realAmount);
            orderLawWithdraw.setRealLawAmount(lawAmount);
            orderLawWithdraw.setRatio(exchangeRate);
            //财务出款时添加
            //            orderLawWithdraw.setPayMerchantId();
            //            orderLawWithdraw.setPayChannelId();
            orderLawWithdraw.setPayTypeId(payType.getId().longValue());
            orderLawWithdraw.setLanKey(LocaleContextHolder.getLocale().toString());
            orderLawWithdraw.setCreateTime(new Date());
            orderLawWithdraw.setOrderStatus(OrderWithdrawOrderStatusConstants.PENDING_REVIEW);
            orderLawWithdrawSign.dealSign(orderLawWithdraw);
            orderLawWithdrawService.save(orderLawWithdraw);
        };
        userAmountChangeManage.changeBalance(tUser.getUserId(), currency, amount, AmountChangeTypeEnum.userWithdraw,
                orderNo, MessagesUtils.get("bot.me.me.msg.YHTX"), tUser.getUserTgUsername(), null, null, consumer);
        sendWithdrawMessage(tUser, orderNo, amount);
        return true;
    }

    /**
     * 校验提现状态
     */
    private void validateWithdrawStatus(TUser tUser) {
        Assert.isTrue(tUser.getFreezeStatus() == WithdrawStatusConstants.UNFROZEN,
                MessagesUtils.get("bot.amount.YHYDJTXQLXKF"));
    }

    /**
     * 验证提现金额
     */
    private BigDecimal validateWithdrawAmount(BigDecimal money) {
        BigDecimal amount = money.abs().setScale(2, BigDecimal.ROUND_DOWN);
        Assert.isTrue(amount.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(BigDecimalUtils.digit10()) <= 0,
                MessagesUtils.get("bot.withdraw.TXJEBZQ"));
        Assert.isTrue(Decimal.of(amount).ge(configAmountService.minWithdrawAmount()),
                StrUtil.format(MessagesUtils.get("bot.me.mode.msg.ZXTXJE"), configAmountService.minWithdrawAmount(), MessagesUtils.get("bot.money" +
                        ".unit")));
        return amount;
    }

    /**
     * 构建提现订单
     */
    private OrderLawWithdraw buildOrderLawWithdraw(String orderNo, Currency currency, TUser tUser, BigDecimal amount,
                                                   BigDecimal fee, UserPayMethod userPayMethod) {
        OrderLawWithdraw orderLawWithdraw = new OrderLawWithdraw();
        orderLawWithdraw.setOrderNo(orderNo);
        orderLawWithdraw.setCurrencyId(currency.getId());
        orderLawWithdraw.setItemId(currency.getItemId());
        orderLawWithdraw.setItemName(currency.getItemName());
        orderLawWithdraw.setChainTag(currency.getChainTag());
        orderLawWithdraw.setUserId(tUser.getUserId());
        orderLawWithdraw.setTgUserId(tUser.getUserTgId());
        orderLawWithdraw.setShareholderId(tUser.getShareholderId());
        orderLawWithdraw.setChannelId(tUser.getChannelId());
        //用户提现信息
        orderLawWithdraw.setBindData(userPayMethod.getBindData());
        orderLawWithdraw.setAmount(amount);
        orderLawWithdraw.setFee(fee);
        orderLawWithdraw.setRealAmount(amount.subtract(fee));
        orderLawWithdraw.setLanKey(LocaleContextHolder.getLocale().toString());
        orderLawWithdraw.setCreateTime(new Date());
        orderLawWithdraw.setOrderStatus(OrderWithdrawOrderStatusConstants.PENDING_REVIEW);
        return orderLawWithdraw;
    }

    /**
     * 处理提现回调业务逻辑
     *
     * @param mchOrderNo  第三方订单号
     * @param upOrderNo   上游订单号
     * @param orderStatus 订单状态 1:成功 2:失败
     * @param notifyData  回调数据
     * @param productId   产品ID
     */
    public String processWithdrawCallback(String mchOrderNo, String upOrderNo, Integer orderStatus,
                                          JSONObject notifyData, String productId) {
        String lockKey = StrUtil.format(RedisLockKey.notifyLock, CecuUtil.getDbCode(), mchOrderNo);
        return redisLockUtil.tryLock(lockKey, 10, java.util.concurrent.TimeUnit.SECONDS, () -> {
            // 查询提现订单
            OrderLawWithdraw orderLawWithdraw = orderLawWithdrawService.getOne(Wrappers.lambdaQuery(OrderLawWithdraw.class)
                    .eq(OrderLawWithdraw::getOrderNo, mchOrderNo));

            if (orderLawWithdraw == null) {
                log.info("提现回调 - 订单不存在: {}", mchOrderNo);
                return "fail";
            }

            // 只有在“提交成功”（已下单给第三方）状态下才处理回调
            if (orderLawWithdraw.getOrderStatus() != OrderWithdrawOrderStatusConstants.ORDER_PLACED_SUCCESS) {
                log.info("提现回调 - 订单状态无需处理: {}, status: {}", mchOrderNo, orderLawWithdraw.getOrderStatus());
                return "success";
            }

            if (orderStatus == 1) { // 成功
                return handleWithdrawSuccess(orderLawWithdraw, upOrderNo, notifyData, productId);
            } else if (orderStatus == 2) { // 失败
                return handleWithdrawFail(orderLawWithdraw, upOrderNo, notifyData, productId);
            }

            return "success";
        });
    }

    private String handleWithdrawSuccess(OrderLawWithdraw orderLawWithdraw, String upOrderNo, JSONObject notifyData,
                                         String productId) {
        TUser tUser = userService.getById(orderLawWithdraw.getUserId());
        if (tUser == null) {
            throw new RuntimeException("提现用户不存在 !");
        }

        // 提现成功，修改订单状态
        orderLawWithdraw.setOrderStatus(OrderWithdrawOrderStatusConstants.WITHDRAWAL_SUCCESS);
        orderLawWithdraw.setUpOrderNo(upOrderNo);
        orderLawWithdraw.setUpNotifyInfo(notifyData.toJSONString());
        orderLawWithdraw.setWithdrawTime(new Date());
        orderLawWithdrawSign.dealSign(orderLawWithdraw);
        orderLawWithdrawService.updateById(orderLawWithdraw);

        // 异步任务处理
        CompletableFuture.runAsync(TtlRunnable.get(() -> {
            MessagesUtils.setLang(tUser.getLanKey());
            tUser.setUserAvatar(mybatisBotService.getMybatisBotConfig().getWithdrawPic());
            String msg = new StringBuilder()
                    .append("<bot.amount.DDH>").append(" ").append(orderLawWithdraw.getOrderNo()).append("\n")
                    .append("<bot.amount.JE>").append(" ").append(BigDecimalUtils.trim(orderLawWithdraw.getAmount()))
                    .append("\n")
                    .toString();
            //            messageSend.sendMsgAsy(tUser, "<bot.withdraw.TXCGSFZDZ>", msg, UserMessageConstant.WITHDRAWAL_ORDERS,
            //                    orderLawWithdraw.getOrderNo());

            mqSendEntityService.sendOrderEntity(OrderEntity.builder()
                    .userId(tUser.getUserId()).channelId(tUser.getChannelId())
                    .type(MqEventTypeConstants.USER_AUTO_LAW_WITHDRAWAL)
                    .money(orderLawWithdraw.getAmount()).orderNo(orderLawWithdraw.getOrderNo())
                    .productId(productId)
                    .build());

            // 提现成功 - 发送财务群通知 + 播报群通知
            try {
                FinancialTelegramUtil financialBot = BotConfiguration.getFinancialBot(productId);
                financialBot.sendTXFWMsg(tUser.getUserId(), orderLawWithdraw.getAmount(),
                        orderLawWithdraw.getOrderNo(), null);
            } catch (Exception e) {
                log.warn("提现成功通知发送失败, 订单号: {}", orderLawWithdraw.getOrderNo(), e);
            }
        }));

        log.info("提现回调处理完成 (成功) - 订单号: {}", orderLawWithdraw.getOrderNo());
        return "success";
    }

    private String handleWithdrawFail(OrderLawWithdraw orderLawWithdraw, String upOrderNo, JSONObject notifyData,
                                      String productId) {
        TUser tUser = userService.getById(orderLawWithdraw.getUserId());
        if (tUser == null) {
            throw new RuntimeException("提现用户不存在 !");
        }

        // 提现失败，用户金额退还
        AmountConsumer consumer = (oldUserWallet, newUserWallet) -> {
            orderLawWithdraw.setOrderStatus(OrderWithdrawOrderStatusConstants.WITHDRAWAL_FAILURE);
            orderLawWithdraw.setUpOrderNo(upOrderNo);
            orderLawWithdraw.setUpNotifyInfo(notifyData.toJSONString());
            orderLawWithdrawSign.dealSign(orderLawWithdraw);
            orderLawWithdrawService.updateById(orderLawWithdraw);
        };

        Currency currency = currencyService.getById(orderLawWithdraw.getCurrencyId());
        userAmountChangeManage.changeBalance(tUser.getUserId(),
                currency,
                orderLawWithdraw.getAmount(), AmountChangeTypeEnum.withdrawFail, orderLawWithdraw.getOrderNo(),
                "fail", tUser.getUserTgUsername(), null, null, consumer);

        CompletableFuture.runAsync(TtlRunnable.get(() -> {
            MessagesUtils.setLang(tUser.getLanKey());
            tUser.setUserAvatar(mybatisBotService.getMybatisBotConfig().getWithdrawPic());
            String title = "<bot.pay.TXYC>";
            String msg = new StringBuilder()
                    .append("<bot.amount.DDH>").append(" ").append(orderLawWithdraw.getOrderNo()).append("\n")
                    .append("<bot.amount.JE>").append(" ").append(BigDecimalUtils.trim(orderLawWithdraw.getAmount()))
                    .append(" <bot.money.unit>").append("\n")
                    .toString();
            //            messageSend.sendMsgAsy(tUser, title, msg, UserMessageConstant.WITHDRAWAL_ORDERS,
            //                    orderLawWithdraw.getOrderNo());
        }));

        log.info("提现回调处理完成 (失败退款) - 订单号: {}", orderLawWithdraw.getOrderNo());
        return "success";
    }

    /**
     * 发送提现消息
     */
    private void sendWithdrawMessage(TUser tUser, String orderNo, BigDecimal amount) {
        MessagesUtils.setLang(tUser.getLanKey());
        tUser.setUserAvatar(mybatisBotService.getMybatisBotConfig().getWithdrawPic());
        String msg = "<bot.me.me.msg.TXTSSJ>\n<bot.withdraw.DDH> " + orderNo + "\n<bot.amount.JE> "
                + BigDecimalUtils.trim(amount) + " <bot.money.unit>\n";
        //        messageSend.sendMsgAsy(tUser, "<bot.me.me.msg.TXXXTJCG>", msg, UserMessageConstant.WITHDRAWAL_ORDERS, orderNo);
    }
}
