package com.gp.common.mybatisplus.merchantpay.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.ttl.TtlRunnable;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.common.core.constant.MqEventTypeConstants;
import com.common.core.constant.OrderConstant;

import com.common.core.enums.AmountChangeTypeEnum;
import com.common.core.enums.ComputeWithdrawBetEnum;
import com.common.core.enums.OrderTypeEnum;
import com.common.core.util.RedisLockUtil;
import com.common.core.util.RedisUtil;
import com.common.datasource.util.CecuUtil;
import com.common.mybatisplus.domain.PageDTO;
import com.common.mybatisplus.domain.PageDomain;
import com.common.mybatisplus.utils.TableSupportUtil;
import com.gp.common.base.constant.*;
import com.gp.common.base.pay.dto.BetAmountDto;
import com.gp.common.base.pay.dto.MPayRechargeDto;
import com.gp.common.base.pay.param.RechargeWithdrawRecordParam;
import com.gp.common.base.pay.vo.BetAmountVo;
import com.gp.common.base.pay.vo.RechargeWithdrawRecordVo;
import com.gp.common.base.utils.*;
import com.gp.common.mybatisplus.entity.*;
import com.gp.common.mybatisplus.function.ExtConsumer;
import com.gp.common.mybatisplus.manage.UserExtChangeManage;
import com.gp.common.mybatisplus.merchantpay.constants.PayMerchantCons;
import com.gp.common.mybatisplus.merchantpay.constants.PayTypeCons;
import com.gp.common.mybatisplus.merchantpay.service.impl.WalletPayService;
import com.gp.common.mybatisplus.mq.OrderEntity;
import com.gp.common.mybatisplus.mqService.MqSendEntityService;
import com.gp.common.mybatisplus.nacos.BotConfig;
import com.gp.common.mybatisplus.param.ChangeExtValueVo;
import com.gp.common.mybatisplus.pay.constant.PayConst;
import com.gp.common.mybatisplus.pay.mpay.WalletCreateOrderResult;
import com.gp.common.mybatisplus.pay.mpay.notity.RechargeEntity;
import com.gp.common.mybatisplus.pay.mpay.order.CreateOrder;
import com.gp.common.mybatisplus.pay.mpay.order.QueryRechargeOrder;
import com.gp.common.mybatisplus.pay.mpay.order.RechargeOrderInfoDo;
import com.gp.common.mybatisplus.merchantpay.service.impl.XPayService;
import com.common.core.merchantpay.xpay.XPayNotifyRequest;
import com.common.core.merchantpay.xpay.XPayResponse;
import com.gp.common.base.exception.BusinessException;
import com.gp.common.mybatisplus.service.*;
import com.gp.common.mybatisplus.until.HttpUtils;
import com.gp.common.mybatisplus.until.OrderAmountSign;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 充值业务服务
 */
@Service
@Slf4j
public class RechargeService {

    /** 待支付订单下发 payUrl 的时间窗：下单超过此时长后三方支付链接多半已失效，避免用户点到无效页 */
    private static final long RECHARGE_PAY_URL_VALID_MS = 10 * 60 * 1000L;

    @Resource
    private PriceRechargeService priceRechargeService;
    @Resource
    private ConfigRiskService configRiskService;
    @Resource
    private ComputeWithdrawBetService computeWithdrawBetService;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private OrderPersonService orderPersonService;
    @Resource
    private OrderLawWithdrawService orderLawWithdrawService;
    @Resource
    private OrderWithdrawService orderWithdrawService;
    @Resource
    private OrderAmountService orderAmountService;
    @Resource
    private MerchantPayService merchantPayService;
    @Resource
    private UserService userService;
    @Resource
    private ConfigAmountService configAmountService;
    @Resource
    private OrderAmountSign orderAmountSign;
    @Resource
    private CurrencyService currencyService;
    @Resource
    private UserExtChangeManage userExtChangeManage;
//    @Resource
//    private MessageSend messageSend;
    @Resource
    private MqSendEntityService mqSendEntityService;
    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Resource
    private MybatisBotService mybatisBotService;
    @Resource
    private HttpUtils httpUtils;
    @Resource
    private PayUntil payUntil;
    @Resource
    private BotItemService botItemService;
    @Resource
    private CecuUtil cecuUtil;
    @Resource
    private PayTypeService payTypeService;
    @Resource
    private PayChannelService payChannelService;
    @Resource
    private RedisLockUtil redisLockUtil;

    /**
     * 计算提现打码量和彩金
     */
    public BetAmountVo calculateBetAmount(BetAmountDto betAmountDto, Long userId) {
        BigDecimal needAmount = betAmountDto.getAmount();
        if (needAmount != null) {
            betAmountDto.setAmount(needAmount.abs().setScale(2, BigDecimal.ROUND_DOWN));
        }
        BigDecimal priceAmount;
        BigDecimal awardAmount;
        BigDecimal receivedAmount;
        Long id = betAmountDto.getId();
        if (id != null) {
            // 套餐类
            PriceRecharge one = priceRechargeService.getById(id);
            priceAmount = one.getPrice();
            awardAmount = one.getBonus();
            receivedAmount = priceAmount.add(awardAmount);

            // 检查充值次数限制
            String key = StrUtil.format(RedisKey.mPayRechargeNum, CecuUtil.getDbCode(), userId);
            String rechargeCount = redisUtil.strGet(key);
            ConfigRisk configRisks = configRiskService.getOne(new LambdaQueryWrapper<ConfigRisk>()
                    .eq(ConfigRisk::getConfigKey, ConfigRiskService.rechargeGiveBonus).last("limit 1"));
            Integer rechargeNum = configRisks != null ? Integer.parseInt(configRisks.getConfigValue()) : 0;
            if (rechargeNum != 0 && rechargeCount != null && Integer.parseInt(rechargeCount) >= rechargeNum) {
                awardAmount = BigDecimal.ZERO;
            }
        } else {
            // 非套餐类
            priceAmount = betAmountDto.getAmount();
            awardAmount = BigDecimal.ZERO;
            receivedAmount = priceAmount;
        }
        BigDecimal withdrawBetService = computeWithdrawBetService.computeWithdrawBetService(priceAmount, awardAmount,
                ComputeWithdrawBetEnum.userRecharge);
        BetAmountVo betAmountVo = new BetAmountVo();
        betAmountVo.setRechargeAmount(priceAmount);
        betAmountVo.setAwardAmount(awardAmount);
        betAmountVo.setReceivedAmount(receivedAmount);
        betAmountVo.setWithdrawBetAmount(withdrawBetService);
        return betAmountVo;
    }

    /**
     * 获取充值或提现记录
     */
    public PageDTO<RechargeWithdrawRecordVo> getRechargeWithdrawRecord(RechargeWithdrawRecordParam param, Long userId) {
        Date start = null;
        Date end = null;
        if (param.getStartTime() != null && param.getEndTime() != null) {
            start = param.getStartTime();
            end = param.getEndTime();
        } else if (param.getTimeSection() != null && param.getTimeSection() != 0) {
            HashMap<String, Date> timeMap = DateUtils.changeTimeSection(param.getTimeSection());
            start = timeMap.get("startTime");
            end = timeMap.get("endTime");
        }
        PageDomain pageDomain = TableSupportUtil.buildPageRequest();
        String itemName = CurrencyService.usdtCurrency.getItemName();

        return param.getType() == 0
                ? getRechargeRecords(userId, start, end, itemName, pageDomain.getPageNum(), pageDomain.getPageSize(),
                        param)
                : getWithdrawRecords(userId, start, end, itemName, pageDomain.getPageNum(), pageDomain.getPageSize());
    }

    /**
     * 获取充值记录 - 合并 t_order_amount 和 t_order_person(上分) 两个表
     */
    private PageDTO<RechargeWithdrawRecordVo> getRechargeRecords(Long userId, Date start, Date end, String itemName,
            int pageNum, int pageSize, RechargeWithdrawRecordParam filterParam) {
        boolean onlyUnpaidRecharge = filterParam != null && Boolean.TRUE.equals(filterParam.getOnlyPayable());

        // 查询 OrderAmount
        // onlyPayable=true：仅「仍可去支付」的待支付单（有 pay_url 且下单时间在有效窗内），不包含已超时仍库表待支付的订单
        LambdaQueryWrapper<OrderAmount> amountQuery = new LambdaQueryWrapper<OrderAmount>()
                .eq(OrderAmount::getUserId, userId)
                .ge(start != null, OrderAmount::getCreateTime, start)
                .le(end != null, OrderAmount::getCreateTime, end);
        if (onlyUnpaidRecharge) {
            Date payWindowStart = new Date(System.currentTimeMillis() - RECHARGE_PAY_URL_VALID_MS);
            amountQuery.eq(OrderAmount::getOrderStatus, OrderAmountOrderStatusConstants.PENDING_PAYMENT)
                    .isNotNull(OrderAmount::getPayUrl)
                    .ne(OrderAmount::getPayUrl, "")
                    .ge(OrderAmount::getCreateTime, payWindowStart);
        }
        amountQuery.orderByDesc(OrderAmount::getCreateTime);
        List<OrderAmount> amountList = orderAmountService.list(amountQuery);

        // 查询 OrderPerson (只查上分记录 order_type=1)；未付单列表不含系统上分
        List<OrderPerson> personList;
        if (onlyUnpaidRecharge) {
            personList = new ArrayList<>();
        } else {
            LambdaQueryWrapper<OrderPerson> personQuery = new LambdaQueryWrapper<OrderPerson>()
                    .eq(OrderPerson::getUserId, userId)
                    .eq(OrderPerson::getOrderType, 1)
                    .ge(start != null, OrderPerson::getCreateTime, start)
                    .le(end != null, OrderPerson::getCreateTime, end)
                    .orderByDesc(OrderPerson::getCreateTime);
            personList = orderPersonService.list(personQuery);
        }

        // 合并两个列表到 VO
        List<RechargeWithdrawRecordVo> allRecords = new ArrayList<>();
        // 一次Redis交互获取全量Map，循环内O(1)内存查找
        Map<Long, PayType> payTypeMap = payTypeService.getMapByIdCached();
        amountList.forEach(item -> {
            //查询支付类型
            PayType payType = payTypeMap.get(item.getPayTypeId());
            String merchantName = payType != null ? payType.getName() : "";
            String lawAmount = (item.getLawAmount() == null ? "0" : BigDecimalUtils.trim(item.getLawAmount()));
            String amountStr = (item.getTotalAmount() == null ? "0" : BigDecimalUtils.trim(item.getTotalAmount()));

            RechargeWithdrawRecordVo vo = new RechargeWithdrawRecordVo();
            vo.setOrderNo(item.getOrderNo());
            vo.setMethod(merchantName);
            vo.setAmountStr(lawAmount);
            vo.setArrivalAmountStr(amountStr);
            vo.setCreateTime(item.getCreateTime());
            long ageMs = item.getCreateTime() == null ? Long.MAX_VALUE
                    : System.currentTimeMillis() - item.getCreateTime().getTime();
            // 用户端：仅「待支付 + 10 分钟内有有效链接」视为可去支付( status=0 + payUrl )；其余待支付超时单按「已取消」展示(status=2)，不可点
            boolean canGoPay = item.getOrderStatus() != null
                    && item.getOrderStatus() == OrderAmountOrderStatusConstants.PENDING_PAYMENT
                    && StrUtil.isNotBlank(item.getPayUrl())
                    && ageMs >= 0
                    && ageMs <= RECHARGE_PAY_URL_VALID_MS;
            if (canGoPay) {
                vo.setStatus(OrderAmountOrderStatusConstants.PENDING_PAYMENT);
                vo.setPayUrl(item.getPayUrl());
            } else if (item.getOrderStatus() != null
                    && item.getOrderStatus() == OrderAmountOrderStatusConstants.PENDING_PAYMENT) {
                vo.setStatus(OrderAmountOrderStatusConstants.CANCELLED);
            } else {
                vo.setStatus(item.getOrderStatus());
            }
            allRecords.add(vo);
        });

        personList.forEach(item -> {
            BigDecimal totalAmount = item.getBonusAmount().add(item.getAmount());
            String amountStr = BigDecimalUtils.trim(totalAmount);

            RechargeWithdrawRecordVo vo = new RechargeWithdrawRecordVo();
            vo.setOrderNo(item.getOrderNo());
            vo.setMethod("system");
            vo.setAmountStr(amountStr);
            vo.setArrivalAmountStr(amountStr);
            vo.setCreateTime(item.getCreateTime());
            vo.setStatus(1);
            allRecords.add(vo);
        });

        allRecords.sort((a, b) -> {
            if (a.getCreateTime() == null)
                return 1;
            if (b.getCreateTime() == null)
                return -1;
            return b.getCreateTime().compareTo(a.getCreateTime());
        });

        return buildPageResult(allRecords, pageNum, pageSize);
    }

    /**
     * 获取提现记录 - 合并 t_order_withdraw、t_order_law_withdraw 和 t_order_person(下分) 三个表
     */
    private PageDTO<RechargeWithdrawRecordVo> getWithdrawRecords(Long userId, Date start, Date end, String itemName,
            int pageNum, int pageSize) {
        LambdaQueryWrapper<OrderLawWithdraw> lawWithdrawQuery = new LambdaQueryWrapper<>();
        lawWithdrawQuery.eq(OrderLawWithdraw::getUserId, userId)
                .ge(start != null, OrderLawWithdraw::getCreateTime, start)
                .le(end != null, OrderLawWithdraw::getCreateTime, end)
                .orderByDesc(OrderLawWithdraw::getCreateTime);
        List<OrderLawWithdraw> lawWithdrawList = orderLawWithdrawService.list(lawWithdrawQuery);

        LambdaQueryWrapper<OrderPerson> personQuery = new LambdaQueryWrapper<>();
        personQuery.eq(OrderPerson::getUserId, userId)
                .eq(OrderPerson::getOrderType, 2)
                .eq(OrderPerson::getLowerSubtype, 1)
                .ge(start != null, OrderPerson::getCreateTime, start)
                .le(end != null, OrderPerson::getCreateTime, end)
                .orderByDesc(OrderPerson::getCreateTime);
        List<OrderPerson> personList = orderPersonService.list(personQuery);

        List<RechargeWithdrawRecordVo> allRecords = new ArrayList<>();

//        for (OrderWithdraw item : withdrawList) {
//            RechargeWithdrawRecordVo vo = new RechargeWithdrawRecordVo();
//            vo.setOrderNo(item.getOrderNo());
//            MerchantPay merchantPay = getMerchantByCode(item.getMerchantCode());
//            String merchantName = merchantPay != null ? merchantPay.getName() : item.getMerchantCode();
//            vo.setMethod(merchantName);
//            vo.setAmountStr(item.getLawAmount() == null ? "0"
//                    : BigDecimalUtils.trim(item.getLawAmount()));
//            if (item.getLawAmount() == null) vo.setArrivalAmountStr("0");
//            else vo.setArrivalAmountStr(BigDecimalUtils.trim(item.getLawAmount()));
//            vo.setCreateTime(item.getCreateTime());
//            Integer orderStatus = item.getOrderStatus();
//            vo.setStatus((orderStatus == 0 || orderStatus == 1 || orderStatus == 2 || orderStatus == 6) ? 0
//                    : (orderStatus == 4 ? 1 : 2));
//            allRecords.add(vo);
//        }

        // 一次Redis交互获取全量Map，循环内O(1)内存查找
        Map<Long, PayType> payTypeMap = payTypeService.getMapByIdCached();
        for (OrderLawWithdraw item : lawWithdrawList) {
            RechargeWithdrawRecordVo vo = new RechargeWithdrawRecordVo();
            vo.setOrderNo(item.getOrderNo());;

            PayType payType = payTypeMap.get(item.getPayTypeId());
            String merchantName = payType != null ? payType.getName() : "";
//            String currency = merchantPay != null && merchantPay.getCurrency() != null ? merchantPay.getCurrency()
//                    : itemName;
            vo.setMethod(merchantName);
            vo.setAmountStr(
                    item.getAmount() == null ? "0" : BigDecimalUtils.trim(item.getAmount()) );
            vo.setArrivalAmountStr(item.getRealLawAmount() == null ? "0"
                    : BigDecimalUtils.trim(item.getRealLawAmount()) );
            vo.setCreateTime(item.getCreateTime());
            Integer orderStatus = item.getOrderStatus();
            vo.setStatus((orderStatus == 0 || orderStatus == 1 || orderStatus == 2 || orderStatus == 6) ? 0
                    : (orderStatus == 4 ? 1 : 2));
            allRecords.add(vo);
        }

        for (OrderPerson item : personList) {
            RechargeWithdrawRecordVo vo = new RechargeWithdrawRecordVo();
            vo.setOrderNo(item.getOrderNo());
            vo.setMethod("system");
            vo.setAmountStr(
                    item.getAmount() == null ? "0" : BigDecimalUtils.trim(item.getAmount()));
            vo.setArrivalAmountStr(
                    item.getAmount() == null ? "0" : BigDecimalUtils.trim(item.getAmount()));
            vo.setCreateTime(item.getCreateTime());
            vo.setStatus(1);
            allRecords.add(vo);
        }

        allRecords.sort((a, b) -> {
            if (a.getCreateTime() == null)
                return 1;
            if (b.getCreateTime() == null)
                return -1;
            return b.getCreateTime().compareTo(a.getCreateTime());
        });

        return buildPageResult(allRecords, pageNum, pageSize);
    }

    /**
     * 构建分页结果
     */
    private PageDTO<RechargeWithdrawRecordVo> buildPageResult(List<RechargeWithdrawRecordVo> allRecords, int pageNum,
            int pageSize) {
        int total = allRecords.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<RechargeWithdrawRecordVo> records = fromIndex < total ? allRecords.subList(fromIndex, toIndex)
                : new ArrayList<>();

        PageDTO<RechargeWithdrawRecordVo> resultPage = new PageDTO<>();
        resultPage.setTotal((long) total);
        resultPage.setSize(pageSize);
        resultPage.setCurrent(pageNum);
        resultPage.setPages((total + pageSize - 1) / pageSize);
        resultPage.setHasPreviousPage(pageNum > 1);
        resultPage.setHasNextPage(pageNum < resultPage.getPages());
        resultPage.setRecords(records);
        return resultPage;
    }

    /**
     * 根据商户code获取商户信息
     */
    private MerchantPay getMerchantByCode(String merchantCode) {
        if (StrUtil.isBlank(merchantCode))
            return null;
        try {
            return merchantPayService.queryMerchantPayByCode(merchantCode);
        } catch (Exception e) {
            log.error("查询商户信息失败: merchantCode={}", merchantCode, e);
            return null;
        }
    }

    /**
     * myPay充值
     */
//    public String mpayRecharge(MPayRechargeDto rechargeDto, TUser tUser) throws Exception {
//        BigDecimal amount = validateAndGetAmount(rechargeDto);
//        BonusAndBetAmount bonusAndBet = calculateBonusAndBet(rechargeDto, tUser, amount);
//        validateAmountRange(amount);
//        return createPaymentOrder(tUser, amount, bonusAndBet);
//    }

    private BigDecimal validateAndGetAmount(MPayRechargeDto rechargeDto) {
        Long id = rechargeDto.getId();
        if (id == null) {
            Assert.notNull(rechargeDto.getAmount(), MessagesUtils.get("bot.recharge.CZJEBZQ"));
            BigDecimal amount = rechargeDto.getAmount().abs().setScale(2, BigDecimal.ROUND_DOWN);
            Assert.isTrue(amount.compareTo(configAmountService.minRechargeAmount()) >= 0,
                    MessagesUtils.get("bot.recharge.CZJEBZQ"));
            return amount;
        } else {
            PriceRecharge priceRecharge = priceRechargeService.getById(id);
            Assert.notNull(priceRecharge, MessagesUtils.get("bot.recharge.CZTCBCZ"));
            return priceRecharge.getPrice();
        }
    }

    /**
     * 计算彩金和打码量
     */
    private BonusAndBetAmount calculateBonusAndBet(MPayRechargeDto rechargeDto, TUser tUser, BigDecimal amount) {
        Long id = rechargeDto.getId();
        if (id != null) {
            PriceRecharge priceRecharge = priceRechargeService.getById(id);
            BigDecimal bonus = checkRechargeLimit(tUser, priceRecharge.getBonus());
            BigDecimal withdrawStatement = computeWithdrawBetService.computeWithdrawBetService(amount, bonus,
                    ComputeWithdrawBetEnum.userRecharge);
            return new BonusAndBetAmount(bonus, withdrawStatement);
        } else {
            return new BonusAndBetAmount(BigDecimal.ZERO, amount);
        }
    }

    /**
     * 检查充值次数限制
     */
    private BigDecimal checkRechargeLimit(TUser tUser, BigDecimal bonus) {
        String key = StrUtil.format(RedisKey.mPayRechargeNum, CecuUtil.getDbCode(), tUser.getUserId());
        String rechargeCount = redisUtil.strGet(key);
        ConfigRisk configRisks = configRiskService.getOne(new LambdaQueryWrapper<ConfigRisk>()
                .eq(ConfigRisk::getConfigKey, ConfigRiskService.rechargeGiveBonus).last("limit 1"));
        Integer rechargeLimit = configRisks != null ? Integer.parseInt(configRisks.getConfigValue()) : 0;
        if (rechargeLimit != 0 && rechargeCount != null && Integer.parseInt(rechargeCount) >= rechargeLimit) {
            return BigDecimal.ZERO;
        }
        return bonus;
    }

    /**
     * 验证金额范围
     */
    private void validateAmountRange(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0 || amount.compareTo(BigDecimalUtils.digit10()) > 0) {
            Assert.isFalse(true, MessagesUtils.get("bot.recharge.CZJEBZQ"));
        }
    }

//    /**
//     * 创建支付订单
//     */
//    private String createPaymentOrder(TUser tUser, BigDecimal amount, BonusAndBetAmount bonusAndBet) throws Exception {
//        Currency currency = CurrencyService.usdtCurrency;
//        userService.judgeUserScore(bonusAndBet.bonus.add(amount), CecuUtil.getDbCode());
//        MerchantPay merchantPay = merchantPayService.queryMerchantPayByType(PayMerchantCons.WALLET_PAY);
//        PayChannel payChannel = payChannelService.getOne(Wrappers.lambdaQuery(PayChannel.class)
//                .eq(PayChannel::getPayPlatformId, merchantPay.getId())
//                .eq(PayChannel::getPayMethod, PayTypeCons.USDT_MERCHANT)
//                .last("LIMIT 1"));
//        Assert.notNull(payChannel, "未配置 MPay 小程序支付通道(USDT_MERCHANT)");
//        BigDecimal rate = payChannel.getRate() != null ? payChannel.getRate() : merchantPay.getRate();
//        String orderNo = SnowIdUtil.getId(OrderConstant.OrderAmount);
//
//        CreateOrder createOrder = new CreateOrder();
//        createOrder.setOrderNo(orderNo);
//        createOrder.setPayMerchantCode(merchantPay.getCode());
//        createOrder.setPayChannelCode("");
//        createOrder.setPayParam(merchantPay.getParamStr());
//        createOrder.setPayChannel(payChannel);
//        // 与 TgPayService + XPay 小程序一致：法币(USDT) = 平台充值金额×汇率，保留 2 位向下取整
//        BigDecimal lawAmount = amount.multiply(rate).setScale(2, BigDecimal.ROUND_DOWN);
//        createOrder.setAmount(lawAmount);
//        createOrder.setLanKey(tUser.getLanKey());
//        WalletCreateOrderResult orderResult = PayWService.getPayService(createOrder.getPayMerchantCode()).createOrder(createOrder);
//        OrderAmount orderAmount = buildOrderAmount(orderNo, tUser, amount, bonusAndBet, currency, merchantPay,
//                orderResult, lawAmount);
//        orderAmountSign.dealSign(orderAmount);
//        orderAmountService.save(orderAmount);
//        return orderResult.getUrl();
//    }


    /**
     * 构建订单信息
     */
    private OrderAmount buildOrderAmount(String orderNo, TUser tUser, BigDecimal amount,
            BonusAndBetAmount bonusAndBet, Currency currency,
            MerchantPay merchantPay, WalletCreateOrderResult orderResult, BigDecimal lawAmount) {
        OrderAmount orderAmount = new OrderAmount();
        orderAmount.setOrderNo(orderNo);
        orderAmount.setMerchantCode(merchantPay.getCode());
        orderAmount.setExchangeType(ExchangeTypeConstants.EXTERNAL);
        // 用户信息
        orderAmount.setUserId(tUser.getUserId());
        orderAmount.setTgUserId(tUser.getUserTgId());
        orderAmount.setShareholderId(tUser.getShareholderId());
        orderAmount.setChannelId(tUser.getChannelId());
        orderAmount.setUsername(tUser.getUsername());
        // 金额信息
        orderAmount.setAmount(amount);
        orderAmount.setBonusAmount(bonusAndBet.bonus);
        orderAmount.setLawAmount(lawAmount);
        orderAmount.setWithdrawStatement(bonusAndBet.withdrawStatement);
        orderAmount.setTotalAmount(amount.add(bonusAndBet.bonus));
        // 货币信息
        orderAmount.setCurrencyId(currency.getId());
        orderAmount.setItemId(currency.getItemId());
        orderAmount.setItemName(currency.getItemName());
        orderAmount.setChainTag(currency.getChainTag());
        // 订单状态
        orderAmount.setOrderStatus(OrderAmountOrderStatusConstants.PENDING_PAYMENT);
        orderAmount.setUpOrderNo(orderResult.getUpOrderNo());
        orderAmount.setUpPayInfo(orderResult.getUpInfo());
        orderAmount.setPayUrl(orderResult.getUrl());
        //渠道信息
        orderAmount.setPayMerchantId(merchantPay.getId());
        //订单的通道信息
//        orderAmount.setPayChannelId(payChannel.getId());
        //订单的支付类型
//        orderAmount.setPayTypeId(payChannel.getPayTypeId());
        return orderAmount;
    }

    /**
     * 彩金和打码量数据类
     */
    private static class BonusAndBetAmount {
        final BigDecimal bonus;
        final BigDecimal withdrawStatement;

        BonusAndBetAmount(BigDecimal bonus, BigDecimal withdrawStatement) {
            this.bonus = bonus;
            this.withdrawStatement = withdrawStatement;
        }
    }

    /**
     * 处理充值回调逻辑
     *
     * @param mchOrderNo 订单号
     * @param notifyData 回调数据
     * @param payName    支付名称
     * @return 处理结果 success/fail
     */
    public String processRechargeCallback(String mchOrderNo,String upOrderNo, JSONObject notifyData, String payName) {
        String lockKey = StrUtil.format(RedisLockKey.notifyLock, CecuUtil.getDbCode(), mchOrderNo);
        return redisLockUtil.tryLock(lockKey, 10, java.util.concurrent.TimeUnit.SECONDS, () -> {
            // 查询订单
            OrderAmount orderAmount = orderAmountService.getOne(Wrappers.lambdaQuery(OrderAmount.class)
                    .eq(OrderAmount::getOrderNo, mchOrderNo));
            if (orderAmount == null) {
                log.info("充值回调 - 订单不存在: {}", mchOrderNo);
                return "fail";
            }

            // 已处理通知成功，不要让上游继续回调
            if (orderAmount.getOrderStatus() != OrderAmountOrderStatusConstants.PENDING_PAYMENT) {
                log.info("充值回调 - 订单已处理: {}", mchOrderNo);
                return "success";
            }

            // 查询用户
            TUser tUser = userService.getById(orderAmount.getUserId());
            if (tUser == null) {
                throw new RuntimeException("充值用户不存在!");
            }

            Boolean isNewUser = payUntil.getIsNewUser(tUser.getUserId());
            String orderNo = orderAmount.getOrderNo();

        // 用户金额上分
        com.gp.common.mybatisplus.function.ExtConsumer extConsumer = () -> {
            orderAmount.setOrderStatus(OrderAmountOrderStatusConstants.PAYMENT_COMPLETED);
            orderAmount.setUpPayInfo(notifyData.toJSONString());
            orderAmount.setPayTime(new Date());
            orderAmount.setUpOrderNo(upOrderNo);
            orderAmountSign.dealSign(orderAmount);
            orderAmountService.updateById(orderAmount);
        };

        Currency currency = currencyService.getById(PayConst.currency_id);
        // 构建扩展值变更列表
        List<ChangeExtValueVo> arr = CollUtil.newArrayList();
        // 彩金记录
        ChangeExtValueVo changeExtValueVoBonus = new ChangeExtValueVo();
        changeExtValueVoBonus.setUserId(tUser.getUserId());
        changeExtValueVoBonus.setExtType(UserExtTypeCons.彩金);
        changeExtValueVoBonus.setUpdateValue(orderAmount.getBonusAmount());
        changeExtValueVoBonus.setOrderNo(orderNo);
        changeExtValueVoBonus.setOrderType(BaseGameInfoCons.UserExtOrderType.充值订单);
        changeExtValueVoBonus.setAccountType(AccountChangeTypeConstants.INCOME);
        changeExtValueVoBonus.setChangeType(BaseGameInfoCons.UserExtChangeType.彩金增加);
        changeExtValueVoBonus.setOperator(tUser.getUserTgName());
        arr.add(changeExtValueVoBonus);

        // 彩金细分
        ChangeExtValueVo changeExtValueVoBonusSmall = new ChangeExtValueVo();
        changeExtValueVoBonusSmall.setUserId(tUser.getUserId());
        changeExtValueVoBonusSmall.setExtType(UserExtTypeCons.充值彩金);
        changeExtValueVoBonusSmall.setUpdateValue(orderAmount.getBonusAmount());
        changeExtValueVoBonusSmall.setOrderNo(orderNo);
        changeExtValueVoBonusSmall.setOrderType(BaseGameInfoCons.UserExtOrderType.充值订单);
        changeExtValueVoBonusSmall.setAccountType(AccountChangeTypeConstants.INCOME);
        changeExtValueVoBonusSmall.setChangeType(BaseGameInfoCons.UserExtChangeType.彩金增加);
        changeExtValueVoBonusSmall.setOperator(tUser.getUserTgName());
        arr.add(changeExtValueVoBonusSmall);

        // 打码量
        ChangeExtValueVo changeExtValueVoStatement = new ChangeExtValueVo();
        changeExtValueVoStatement.setUserId(tUser.getUserId());
        changeExtValueVoStatement.setExtType(UserExtTypeCons.提现打码量);
        changeExtValueVoStatement.setUpdateValue(orderAmount.getWithdrawStatement());
        changeExtValueVoStatement.setOrderNo(orderNo);
        changeExtValueVoStatement.setOrderType(BaseGameInfoCons.UserExtOrderType.充值订单);
        changeExtValueVoStatement.setAccountType(AccountChangeTypeConstants.INCOME);
        changeExtValueVoStatement.setChangeType(BaseGameInfoCons.UserExtChangeType.提现打码量增加);
        changeExtValueVoStatement.setOperator(tUser.getUserTgName());
        arr.add(changeExtValueVoStatement);

        // 累计充值
        ChangeExtValueVo changeExtValueVoRecharge = new ChangeExtValueVo();
        changeExtValueVoRecharge.setUserId(tUser.getUserId());
        changeExtValueVoRecharge.setExtType(UserExtTypeCons.累计充值);
        changeExtValueVoRecharge.setUpdateValue(orderAmount.getAmount());
        changeExtValueVoRecharge.setOrderNo(orderNo);
        changeExtValueVoRecharge.setOrderType(BaseGameInfoCons.UserExtOrderType.充值订单);
        changeExtValueVoRecharge.setAccountType(AccountChangeTypeConstants.INCOME);
        changeExtValueVoRecharge.setChangeType(BaseGameInfoCons.UserExtChangeType.累计充值增加);
        changeExtValueVoRecharge.setOperator(tUser.getUserTgName());
        arr.add(changeExtValueVoRecharge);

        MessagesUtils.setLang(tUser.getLanKey());
        String dbCode = CecuUtil.getDbCode();

        BigDecimal bonusAmount = orderAmount.getBonusAmount();
        BigDecimal amount = orderAmount.getAmount();
        BigDecimal award = bonusAmount.add(amount);

        try {
            userService.judgeUserScore(award, dbCode);
        } catch (Exception e) {
            log.error("充值回调 - 判断用户积分失败", e);
            return "fail";
        }

        payUntil.dealUserWithdrawBet(tUser);
        UserWallet userWallet = userExtChangeManage.changeRechargeAmount(arr, extConsumer, currency,
                orderAmount.getAmount(), orderAmount.getBonusAmount(), OrderTypeEnum.userRecharge,
                AmountChangeTypeEnum.userRecharge, payName);

        // 发送商户积分扣减
        userService.sendJudgeUserScoreMq(award, dbCode, MerchantChangeTypeConstants.RECHARGE_SCORE,
                MerchantOrderTypeConstant.RECHARGE_SCORE, payName + "充值");

        // 异步处理后续逻辑
        threadPoolTaskExecutor.execute(TtlRunnable.get(() -> {
            cecuUtil.cutDbByCode(dbCode);
            if (orderAmount.getBonusAmount() != null && orderAmount.getBonusAmount().compareTo(BigDecimal.ZERO) > 0) {
                String key = StrUtil.format(RedisKey.HCPayRechargeNum, dbCode, tUser.getUserId());
                redisUtil.incr(key, 1);
                redisUtil.expire(key, DateUtils.getTodayEndTime());
            }

            // 处理充值金额
            payUntil.dealMoney(tUser, orderAmount.getAmount(), isNewUser);

            // 发送自动充值事件
            mqSendEntityService.sendOrderEntity(OrderEntity.builder()
                    .userId(tUser.getUserId())
                    .channelId(tUser.getChannelId())
                    .type(MqEventTypeConstants.USER_AUTO_RECHARGE)
                    .money(orderAmount.getAmount())
                    .bonusAmount(orderAmount.getBonusAmount())
                    .orderNo(orderAmount.getOrderNo())
                    .productId(dbCode)
                    .build());

            MessagesUtils.setLang(tUser.getLanKey());
            tUser.setUserAvatar(mybatisBotService.getMybatisBotConfig().getRechargePic());

            // 通知用户
            String title = EmojiCons.对号 + " " + "<bot.notity.CZCGTX>";
            String content = "<bot.amount.JE> " + BigDecimalUtils.trim(orderAmount.getAmount()) + " <bot.money.unit>\n"
                    +
                    "<bot.amount.ZSCJ> " + BigDecimalUtils.trim(orderAmount.getBonusAmount()) + " <bot.money.unit>\n" +
                    "<bot.notity.DQYE> " + BigDecimalUtils.trim(userWallet.getAmount()) + " <bot.money.unit>";
//            messageSend.sendMsgAsy(tUser, title, content, UserMessageConstant.RECHARGE_ORDERS, orderNo);
        }));

        // 发送TikTok和Facebook事件
        BotConfig botById = botItemService.getBotById(Long.valueOf(dbCode));
        if (botById != null) {
            httpUtils.sendTikTokEvent(tUser.getUserId().toString(), orderAmount.getAmount(), tUser);
            httpUtils.sendTFacebookEEvent(tUser.getUserId().toString(), orderAmount.getAmount(), tUser,
                    botById.getFrontAdminH5());
        }

            log.info("充值回调 - 订单处理成功: {}", mchOrderNo);
            return "success";
        });
    }

    /**
     * Mpay 直充入口（状态校验 + 主动查单 + 上分）
     */
    public String processMpayDirectRecharge(RechargeEntity rechargeEntity, MerchantPay merchantPay) {
        if (!rechargeEntity.getStatus().equals(PayConst.success)) {
            log.info("Mpay直充回调 - 状态非成功: status={}", rechargeEntity.getStatus());
            return "OK";
        }
        if (rechargeEntity.getOrderType() != NotifyTypeConstants.DEPOSIT) {
            log.info("Mpay直充回调 - 非充值类型: orderType={}", rechargeEntity.getOrderType());
            return "OK";
        }
        // 主动查单验证
        QueryRechargeOrder queryRechargeOrder = new QueryRechargeOrder();
        queryRechargeOrder.setUpOrderNo(rechargeEntity.getUpOrderNo());
        queryRechargeOrder.setPayParam(merchantPay.getParamStr());
        queryRechargeOrder.setClientUserId(rechargeEntity.getClientUserId());
        RechargeOrderInfoDo.RechargeOrderInfo rechargeOrderInfo =
                PayWService.getPayService(PayMerchantCons.WALLET_PAY).queryRechargeOrder(queryRechargeOrder);

        return processDirectRechargeCallback(
                rechargeEntity.getUpOrderNo(),
                Long.valueOf(rechargeEntity.getClientUserId()),
                rechargeOrderInfo.getAmount(),
                rechargeOrderInfo.getTxid(),
                rechargeOrderInfo.getFromAddress(),
                rechargeOrderInfo.getToAddress(),
                new Date(rechargeOrderInfo.getCreateTime() * 1000L),
                merchantPay,
                rechargeEntity,
                rechargeOrderInfo,
                "Mpay直充"
        );
    }

    /**
     * XPay 直充入口（pid解析 + 主动查单 + 上分）
     */
    public String processXPayDirectRecharge(XPayNotifyRequest notifyRequest, MerchantPay merchantPay) {
        String pid = notifyRequest.getPid();
        if (StrUtil.isBlank(pid) || !pid.contains("_")) {
            log.error("XPay直充回调 - pid格式错误: pid={}", pid);
            throw new BusinessException("pid格式错误!");
        }
        Long userId = Long.valueOf(pid.substring(pid.lastIndexOf("_") + 1));

        // 主动查单验证
        XPayService xpayService = (XPayService) PayWService.getPayService(PayMerchantCons.XPAY);
        XPayResponse rechargeStatus = xpayService.queryRechargeStatus(
                notifyRequest.getOrder_id(), merchantPay.getParamStr());
        if (rechargeStatus == null || rechargeStatus.getStatus() != 2) {
            log.error("XPay直充回调 - 订单验证失败: order_id={}, status={}",
                    notifyRequest.getOrder_id(), rechargeStatus != null ? rechargeStatus.getStatus() : "null");
            throw new BusinessException("订单验证失败!");
        }

        return processDirectRechargeCallback(
                notifyRequest.getOrder_id(),
                userId,
                new BigDecimal(notifyRequest.getAmount()),
                notifyRequest.getTx_hash(),
                notifyRequest.getFrom_address(),
                notifyRequest.getAddress(),
                new Date(notifyRequest.getTimestamp() * 1000L),
                merchantPay,
                notifyRequest,
                rechargeStatus,
                "XPay直充"
        );
    }

    /**
     * 直充回调处理逻辑（Mpay/XPay 链上直充，无预建订单，需新建订单+上分）
     *
     * @param upOrderNo    上游订单号
     * @param userId       充值用户ID
     * @param usdAmount    USD金额（链上原始金额）
     * @param rate         汇率
     * @param txId         链上交易hash
     * @param fromAddress  来源地址
     * @param toAddress    目标地址
     * @param payTime      支付时间
     * @param merchantPay  商户配置
     * @param notifyData   回调原始数据（用于记录）
     * @param queryData    查单结果数据（用于记录）
     * @param payName      支付名称（日志/通知用）
     * @return success/fail
     */
    public String processDirectRechargeCallback(String upOrderNo, Long userId,
                                                BigDecimal usdAmount,
                                                String txId, String fromAddress, String toAddress,
                                                Date payTime, MerchantPay merchantPay,
                                                Object notifyData, Object queryData, String payName) {
        String dbCode = CecuUtil.getDbCode();

        // 防重复回调
        OrderAmount existOrder = orderAmountService.getOne(
                Wrappers.lambdaQuery(OrderAmount.class).eq(OrderAmount::getUpOrderNo, upOrderNo));
        if (existOrder != null) {
            log.info("直充回调 - 订单已处理: upOrderNo={}, orderNo={}", upOrderNo, existOrder.getOrderNo());
            return "success";
        }

        TUser tUser = userService.getById(userId);
        if (tUser == null) {
            log.error("直充回调 - 用户不存在: userId={}", userId);
            return "fail";
        }

        PayChannel payChannel = payChannelService.getOne(Wrappers.lambdaQuery(PayChannel.class)
                .eq(PayChannel::getPayPlatformId, merchantPay.getId())
                .eq(PayChannel::getPayMethod, WalletPayService.USDT_BLACK)
                .last("LIMIT 1"));
        BigDecimal rate = payChannel != null ? payChannel.getRate() : merchantPay.getRate();
        PayType payType = null;
        if (payChannel != null) {
            payType = payTypeService.getByIdWithCache(payChannel.getPayTypeId());
        }
        PayType finalPayType = payType;

        Boolean isNewUser = payUntil.getIsNewUser(tUser.getUserId());
        Currency currency = currencyService.getById(PayConst.currency_id);
        String orderNo = SnowIdUtil.getId(OrderConstant.OrderAmount);
        BigDecimal finalUsdAmount = usdAmount.setScale(2, BigDecimal.ROUND_DOWN);
        BigDecimal amount = finalUsdAmount.divide(rate, 2, BigDecimal.ROUND_DOWN);

        OrderAmount orderAmount = new OrderAmount();
        ExtConsumer extConsumer = () -> {
            orderAmount.setOrderNo(orderNo);
            orderAmount.setItemId(currency.getItemId());
            orderAmount.setCurrencyId(currency.getId());
            orderAmount.setChainTag(currency.getChainTag());
            orderAmount.setItemName(currency.getItemName());
            orderAmount.setTxId(txId);
            orderAmount.setExchangeType(ExchangeTypeConstants.EXTERNAL);
            orderAmount.setUserId(tUser.getUserId());
            orderAmount.setTgUserId(tUser.getUserTgId());
            orderAmount.setShareholderId(tUser.getShareholderId());
            orderAmount.setChannelId(tUser.getChannelId());
            orderAmount.setUsername(tUser.getUsername());
            orderAmount.setAmount(amount);
            orderAmount.setLawAmount(finalUsdAmount);
            orderAmount.setMerchantCode(merchantPay.getCode());
            orderAmount.setPayMerchantId(merchantPay.getId());
            orderAmount.setPayMerchantName(merchantPay.getName());
            if (payChannel != null) {
                orderAmount.setPayChannelId(payChannel.getId());
                orderAmount.setPayChannelName(payChannel.getName());
            }
            if (finalPayType != null) {
                orderAmount.setPayTypeId(Long.valueOf(finalPayType.getId()));
                orderAmount.setPayTypeName(finalPayType.getName());
            }
            orderAmount.setBonusAmount(BigDecimal.ZERO);
            orderAmount.setWithdrawStatement(amount);
            orderAmount.setTotalAmount(amount);
            orderAmount.setFromAddress(fromAddress);
            orderAmount.setToAddress(toAddress);
            orderAmount.setOrderStatus(OrderAmountOrderStatusConstants.PAYMENT_COMPLETED);
            orderAmount.setUpOrderNo(upOrderNo);
            orderAmount.setPayTime(payTime);
            orderAmount.setRemark(payName);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("notifyData", notifyData);
            jsonObject.put("queryData", queryData);
            orderAmount.setUpPayInfo(jsonObject.toJSONString());
            orderAmountSign.dealSign(orderAmount);
            orderAmountService.save(orderAmount);
        };

        List<ChangeExtValueVo> arr = CollUtil.newArrayList();

        ChangeExtValueVo statementVo = new ChangeExtValueVo();
        statementVo.setUserId(tUser.getUserId());
        statementVo.setExtType(UserExtTypeCons.提现打码量);
        statementVo.setUpdateValue(computeWithdrawBetService.computeWithdrawBetService(
                amount, BigDecimal.ZERO, ComputeWithdrawBetEnum.userRecharge));
        statementVo.setOrderNo(orderNo);
        statementVo.setOrderType(BaseGameInfoCons.UserExtOrderType.充值订单);
        statementVo.setAccountType(AccountChangeTypeConstants.INCOME);
        statementVo.setChangeType(BaseGameInfoCons.UserExtChangeType.提现打码量增加);
        statementVo.setOperator(tUser.getUserTgName());
        arr.add(statementVo);

        ChangeExtValueVo rechargeVo = new ChangeExtValueVo();
        rechargeVo.setUserId(tUser.getUserId());
        rechargeVo.setExtType(UserExtTypeCons.累计充值);
        rechargeVo.setUpdateValue(amount);
        rechargeVo.setOrderNo(orderNo);
        rechargeVo.setOrderType(BaseGameInfoCons.UserExtOrderType.充值订单);
        rechargeVo.setAccountType(AccountChangeTypeConstants.INCOME);
        rechargeVo.setChangeType(BaseGameInfoCons.UserExtChangeType.累计充值增加);
        rechargeVo.setOperator(tUser.getUserTgName());
        arr.add(rechargeVo);

        try {
            userService.judgeUserScore(amount, dbCode);
        } catch (Exception e) {
            log.error("直充回调 - 判断用户积分失败: userId={}", userId, e);
            return "fail";
        }

        payUntil.dealUserWithdrawBet(tUser);
        UserWallet userWallet = userExtChangeManage.changeRechargeAmount(arr, extConsumer, currency,
                amount, BigDecimal.ZERO, OrderTypeEnum.userRecharge,
                AmountChangeTypeEnum.userRecharge, merchantPay.getName());

        userService.sendJudgeUserScoreMq(amount, dbCode, MerchantChangeTypeConstants.RECHARGE_SCORE,
                MerchantOrderTypeConstant.RECHARGE_SCORE, payName);

        MessagesUtils.setLang(tUser.getLanKey());
        tUser.setUserAvatar(mybatisBotService.getMybatisBotConfig().getRechargePic());

        String title = EmojiCons.对号 + " " + "<bot.notity.CZCGTX>";
        String content = "<bot.amount.JE> " + BigDecimalUtils.trim(amount) + " <bot.money.unit>\n"
                + "<bot.notity.DQYE> " + BigDecimalUtils.trim(userWallet.getAmount()) + " <bot.money.unit>";
//        messageSend.sendMsgAsy(tUser, title, content, UserMessageConstant.RECHARGE_ORDERS, orderNo);

        mqSendEntityService.sendOrderEntity(OrderEntity.builder()
                .userId(tUser.getUserId()).channelId(tUser.getChannelId())
                .type(MqEventTypeConstants.USER_AUTO_RECHARGE)
                .money(orderAmount.getAmount()).bonusAmount(orderAmount.getBonusAmount())
                .orderNo(orderAmount.getOrderNo()).productId(dbCode)
                .build());

        payUntil.dealMoney(tUser, amount, isNewUser);

        log.info("直充回调 - 订单处理成功: upOrderNo={}, orderNo={}, userId={}", upOrderNo, orderNo, userId);
        return "success";
    }
}
