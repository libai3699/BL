package com.gp.common.mybatisplus.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.constant.*;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.config.RedisOnlineService;
import com.gp.common.mybatisplus.entity.Currency;
import com.gp.common.mybatisplus.entity.*;
import com.gp.common.mybatisplus.mapper.*;
import com.gp.common.mybatisplus.vo.ConsumOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class CountOrderServiceEx extends ServiceImpl<CountOrderMapper, CountOrder> {

    @Resource
    private UserService userService;

    @Resource
    private OrderAmountMapper orderAmountMapper;

    @Resource
    private OrderAmountService orderAmountService;

    @Resource
    private OrderWithdrawMapper orderWithdrawMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserWalletMapper userWalletMapper;

    @Resource
    private CurrencyService currencyService;

    @Resource
    private OrderTermService orderTermService;

    @Resource
    private UserCountOrderService userCountOrderService;

    @Resource
    private UserExtChangeService userExtChangeService;

    @Resource
    private PlayWheelTermService playWheelTermService;

    @Resource
    private ActivityAwardReceiveService activityAwardReceiveService;

    @Resource
    private AmountChangeService amountChangeService;

    @Resource
    private OrderPersonService orderPersonService;

    @Resource
    private OrderLawWithdrawService orderLawWithdrawService;

    @Resource
    private ChannelCountOrderService channelCountOrderService;

    @Resource
    private OrderWithdrawService orderWithdrawService;

    @Autowired
    private OrderTermMapper orderTermMapper;

    @Resource
    private RedisOnlineService redisOnlineService;

//    public void dataStat(String uuid, String day) {
//        String startTime = day + " 00:00:00";
//        String endTime = day + " 23:59:59";
//        List<Currency> currencyList = currencyService.getOpenCurrencys();
//        List<TUser> userList = userService.list();
//        log.info("uuid ={}, 执行countOrderJob ->统计数据, 开始时间={}, 结束时间={}", uuid, startTime, endTime);
//        Map<Long, ChannelCountOrder> channelCountOrderMap = new HashMap<>();
//        for (Currency currency : currencyList) {
//            Map<String, Object> countData = new HashMap<>();
//            this.initCountData(countData);
//            for (TUser user : userList) {
//                this.handleUserCountOrderData(
//                        day, user, currency, startTime, endTime
//                        , countData, channelCountOrderMap
//                );
//            }
//            CountOrder countOrder = new CountOrder();
//            countOrder.setDayStr(day);
//            countOrder.setCurrencyId(currency.getId());
//            countOrder.setItemId(currency.getItemId());
//            countOrder.setChainTag(currency.getChainTag());
//
//            /** MPAY充值金额 */
//            countOrder.setMpayRechargeAmount(new BigDecimal(countData.get("currencyMpayRechargeAmount").toString()));
//            /** MPAY充值充值数量 */
//            countOrder.setMpayRechargeNum(Integer.valueOf(countData.get("currencyMpayRechargeNum").toString()));
//            /** 区块链直冲金额 */
//            countOrder.setBlockchainRechargeAmount(new BigDecimal(countData.get("currencyBlockchainRechargeAmount").toString()));
//            /** 区块链直冲数量 */
//            countOrder.setBlockchainRechargeNum(Integer.valueOf(countData.get("currencyBlockchainRechargeNum").toString()));
//            /** pix充值金额 */
//            countOrder.setPixpayRechargeAmount(new BigDecimal(countData.get("currencyPixpayRechargeAmount").toString()));
//            /** pix充值数量 */
//            countOrder.setPixpayRechargeNum(Integer.valueOf(countData.get("currencyPixpayRechargeNum").toString()));
//
//            //统计充值数据
//            countOrder.setRechargeNum(Integer.valueOf(countData.get("currencyRechargeNum").toString()));
//            countOrder.setRechargeAmount(new BigDecimal(countData.get("currencyRechargeAmount").toString()));
//            //统计提现数据
//            countOrder.setWithdrawNum(Integer.valueOf(countData.get("currencyWithdrawNum").toString()));
//            countOrder.setWithdrawAmount(new BigDecimal(countData.get("currencyWithdrawAmount").toString()));
//            countOrder.setFee(new BigDecimal(countData.get("currencyFee").toString()));
//            countOrder.setRealAmount(new BigDecimal(countData.get("currencyRealAmount").toString()));
//            //游戏投注总数量
//            countOrder.setBetNum(Integer.valueOf(countData.get("currencyBetNum").toString()));
//            //游戏投注总金额(不分输赢)
//            countOrder.setBetAmount(new BigDecimal(countData.get("currencyBetAmount").toString()));
//            //结算总金额
//            countOrder.setSettleAmount(new BigDecimal(countData.get("currencySettleAmount").toString()));
//            //有效投注总金额
//            countOrder.setEfficientBetAmount(new BigDecimal(countData.get("currencyEfficientBetAmount").toString()));
//            //输赢总金额
//            countOrder.setWinLoseAmount(new BigDecimal(countData.get("currencyWinLoseAmount").toString()));
//            //待返水总金额
//            countOrder.setWaitRebateAmount(new BigDecimal(countData.get("currencyWaitRebateAmount").toString()));
//            //待返佣总金额-下级用户返水返给自己的金额
//            countOrder.setWaitReturnCommissionAmount(new BigDecimal(countData.get("currencyWaitReturnCommissionAmount").toString()));
//            //已返水总金额
//            countOrder.setAlreadyRebateAmount(new BigDecimal(countData.get("currencyAlreadyRebateAmount").toString()));
//            //已返佣总金额
//            countOrder.setAlreadyReturnCommissionAmount(new BigDecimal(countData.get("currencyAlreadyReturnCommissionAmount").toString()));
//            //转盘活动奖励总金额
//            countOrder.setPlayWheelTermAward(new BigDecimal(countData.get("currencyPlayWheelTermAward").toString()));
//            //参入转盘活动总次数
//            countOrder.setPlayWheelTermNum(Integer.valueOf(countData.get("currencyPlayWheelTermNum").toString()));
//            //彩金金额
//            countOrder.setBonusAmount(new BigDecimal(countData.get("currencyBonusAmount").toString()));
//            //pix彩金金额
//            countOrder.setPixBonusAmount(new BigDecimal(countData.get("currencypixBonusAmount").toString()));
//            //后台客服手动上分金额
//            countOrder.setCustomerRechargeAmount(new BigDecimal(countData.get("currencyCustomerRechargeAmount").toString()));
//            //后台客服手动上分彩金金额
//            countOrder.setCustomerBonusAmount(new BigDecimal(countData.get("currencyCustomerBonusAmount").toString()));
//            //后台客服手动上分订单数量
//            countOrder.setCustomerRechargeNum(Integer.valueOf(countData.get("currencyCustomerRechargeNum").toString()));
//            //后台客服手动下分金额-下分类型是真实提现
//            countOrder.setCustomerRealCashWithdrawalAmount(new BigDecimal(countData.get("currencyCustomerRealCashWithdrawalAmount").toString()));
//            //后台客服手动下分订单数量-下分类型是真实提现
//            countOrder.setCustomerRealCashWithdrawalNum(Integer.valueOf(countData.get("currencyCustomerRealCashWithdrawalNum").toString()));
//            //后台客服手动下分实际金额-下分类型是真实提现
//            countOrder.setCustomerRealCashWithdrawalRealAmount(new BigDecimal(countData.get("currencyCustomerRealCashWithdrawalRealAmount").toString()));
//            //后台客服手动下分金额-下分类型是扣除积分
//            countOrder.setCustomerPointsDeductedAmount(new BigDecimal(countData.get("currencyCustomerPointsDeductedAmount").toString()));
//            //后台客服手动下分订单数量-下分类型是扣除积分
//            countOrder.setCustomerPointsDeductedNum(Integer.valueOf(countData.get("currencyCustomerPointsDeductedNum").toString()));
//            //法币提现订单数量
//            countOrder.setLawWithdrawNum(Integer.valueOf(countData.get("currencyLawWithdrawNum").toString()));
//            //法币提现金额
//            countOrder.setLawWithdrawAmount(new BigDecimal(countData.get("currencyLawWithdrawAmount").toString()));
//
//            //用户活动奖励领取奖励-彩金金额
//            countOrder.setActivityAwardBonusAmount(new BigDecimal(countData.get("currencyActivityAwardBonusAmount").toString()));
//            //用户活动奖励领取次数-奖励彩金
//            countOrder.setReceiveBonusNum(Integer.valueOf(countData.get("currencyReceiveBonusNum").toString()));
//            //用户活动奖励领取奖励-转盘总次数
//            countOrder.setActivityAwardTotalNum(Integer.valueOf(countData.get("currencyActivityAwardTotalNum").toString()));
//            //用户活动奖励领取次数-奖励转盘次数
//            countOrder.setReceiveTurntableNum(Integer.valueOf(countData.get("currencyReceiveTurntableNum").toString()));
//            //用户收入
//            countOrder.setUserEarnings(new BigDecimal(countData.get("currencyUserEarnings").toString()));
//            //用户支出
//            countOrder.setUserExpenses(new BigDecimal(countData.get("currencyUserExpenses").toString()));
//            //客损[计算公式 等于 输赢 加 所有送的金额 减 手动下分(扣除积分)]
//            countOrder.setCustomerLossAmount(new BigDecimal(countData.get("currencyCustomerLossAmount").toString()));
//            //统计用户结余(钱包余额)
//            countOrder.setUserAmount(new BigDecimal(countData.get("currencyUserAmount").toString()));
//            //打码量
//            countOrder.setCodeAmount(new BigDecimal(countData.get("currencyCodeAmount").toString()));
//
//            //当日 充值人数
//            countOrder.setRechargePeopleNum(
//                    this.getPeopleNum(day, currency.getId())
//            );
//
//            //当日 提现人数
//            countOrder.setWithdrawalPeopleNum(
//                    this.getWithdrawPeopleNum(day, currency.getId())
//            );
//
//            //3.新增  人数
//            //统计用户新增
//            LambdaQueryWrapper<TUser> userCountWrapper = Wrappers.lambdaQuery(TUser.class);
//            userCountWrapper.between(TUser::getCreateTime, startTime, endTime);
//            Integer userCount = userMapper.selectCount(userCountWrapper);
//            countOrder.setUserNum(userCount);
//
//            //CountOrder 使用的首存/二存/复存数据查询
//            this.findFirstNumOrMoneyByCountOrder(uuid, day, currency.getId(), countOrder);
//
//            //处理留存相关数据-countOrder
//            this.processingRetainedDataByCountOrder(countOrder, startTime, endTime);
//
//            countOrder.setCalculationsTime(new Date());
//
//            //活跃人数
//            countOrder.setActivePeopleNum(this.getActiveUserCount(day));
//
//            Map<String, Object> reslutMap = orderTermMapper.selectUnsettledBetsOrderTermList(startTime);
//            //未结算注单金额
//            countOrder.setUnsettledBetsAmount(
//                    new BigDecimal(reslutMap.get("betAmount").toString())
//            );
//            //未结算注单数量
//            countOrder.setUnsettledBetsNum(
//                    Integer.valueOf(reslutMap.get("count").toString())
//            );
//
//            LambdaUpdateWrapper<CountOrder> orderWrapper = Wrappers.lambdaUpdate(CountOrder.class);
//            orderWrapper.eq(CountOrder::getDayStr, day);
//            orderWrapper.eq(CountOrder::getCurrencyId, currency.getId());
//            this.saveOrUpdate(countOrder, orderWrapper);
//        }
//        if (channelCountOrderMap.size() > 0) {
//            channelCountOrderMap.forEach((channelId, channelCountOrder) -> {
//
//                //获取当前渠道下的所有用户
//                LambdaQueryWrapper<TUser> userQuery = Wrappers.lambdaQuery(TUser.class);
//                userQuery.eq(TUser::getChannelId, channelId);
//                List<TUser> channelUserList = userService.list(userQuery);
//                List<Long> channelUserIdList = channelUserList.stream().map(TUser::getUserId).collect(Collectors.toList());
//
//                if (CollectionUtils.isEmpty(channelUserIdList)) {
//                    return;
//                }
//
//                //当日 充值人数
//                channelCountOrder.setRechargePeopleNum(
//                        this.getPeopleNumByChannelId(day, channelId, channelCountOrder.getCurrencyId())
//                );
//
//                //当日 提现人数
//                channelCountOrder.setWithdrawalPeopleNum(
//                        this.getWithdrawPeopleNumByChannelId(day, channelId, channelCountOrder.getCurrencyId())
//                );
//
//                //3.新增  人数
//                //统计用户新增
//                LambdaQueryWrapper<TUser> userCountWrapper = Wrappers.lambdaQuery(TUser.class);
//                userCountWrapper.between(TUser::getCreateTime, startTime, endTime);
//                userCountWrapper.eq(TUser::getChannelId, channelId);
//                Integer userCount = userMapper.selectCount(userCountWrapper);
//                channelCountOrder.setUserNum(userCount);
//
//                this.findFirstNumOrMoneyByChannelCountOrder(
//                        uuid, day, channelCountOrder.getCurrencyId(), channelId, channelCountOrder
//                );
//
//                this.processingRetainedDataByChannelCountOrder(
//                        channelId, channelCountOrder, startTime, endTime
//                );
//
//                channelCountOrder.setUpdateTime(new Date());
//                channelCountOrder.setCalculationsTime(new Date());
//                //活跃人数
//                channelCountOrder.setActivePeopleNum(this.getChannelActiveUserCount(channelId, day));
//
//                LambdaUpdateWrapper<ChannelCountOrder> channelCountOrderQuery = Wrappers.lambdaUpdate(ChannelCountOrder.class);
//                channelCountOrderQuery.eq(ChannelCountOrder::getDayStr, day);
//                channelCountOrderQuery.eq(ChannelCountOrder::getChannelId, channelId);
//                channelCountOrderQuery.eq(ChannelCountOrder::getCurrencyId, channelCountOrder.getCurrencyId());
//                channelCountOrderService.saveOrUpdate(channelCountOrder, channelCountOrderQuery);
//            });
//        }
//    }

    /**
     * 获取当前渠道下的 充值人数
     */
    public Integer getPeopleNumByChannelId(String nowDay, Long channelId, Integer currencyId) {
        String startTime = nowDay + " 00:00:00";
        String endTime = nowDay + " 23:59:59";
        if (Objects.isNull(channelId) || Objects.isNull(currencyId)) {
            return 0;
        }
        //获取当前渠道下的所有用户
        LambdaQueryWrapper<TUser> userQuery = Wrappers.lambdaQuery(TUser.class);
        userQuery.eq(TUser::getChannelId, channelId);
        List<TUser> channelUserList = userService.list(userQuery);
        List<Long> channelUserIdList = channelUserList.stream().map(TUser::getUserId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(channelUserIdList)) {
            return 0;
        }

        //当日 充值人数
        LambdaQueryWrapper<OrderAmount> todayOrderAmountQuery = Wrappers.lambdaQuery(OrderAmount.class);
        todayOrderAmountQuery.eq(OrderAmount::getOrderStatus, OrderAmountOrderStatusConstants.PAYMENT_COMPLETED);
        todayOrderAmountQuery.eq(OrderAmount::getCurrencyId, currencyId);
        todayOrderAmountQuery.between(OrderAmount::getCreateTime, startTime, endTime);
        todayOrderAmountQuery.in(OrderAmount::getUserId, channelUserIdList);
        List<OrderAmount> todayOrderAmounts = orderAmountService.list(todayOrderAmountQuery);
        List<Long> todayOrderAmountUserIds = Optional.ofNullable(todayOrderAmounts)
                .orElse(Collections.emptyList())
                .stream()
                .map(OrderAmount::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        LambdaQueryWrapper<OrderPerson> todayUpPointOrderPersonQuery = Wrappers.lambdaQuery(OrderPerson.class);
        //类型(1 上分, 2 下分)
        todayUpPointOrderPersonQuery.eq(OrderPerson::getOrderType, 1);
        todayUpPointOrderPersonQuery.eq(OrderPerson::getCurrencyId, currencyId);
        todayUpPointOrderPersonQuery.between(OrderPerson::getCreateTime, startTime, endTime);
        todayUpPointOrderPersonQuery.gt(OrderPerson::getAmount, BigDecimal.ZERO);//充值金额大于0才算
        todayUpPointOrderPersonQuery.in(OrderPerson::getUserId, channelUserIdList);
        List<OrderPerson> todayUpPointOrderPersons = orderPersonService.list(todayUpPointOrderPersonQuery);
        List<Long> todayUpPointOrderPersonUserIds = Optional.ofNullable(todayUpPointOrderPersons)
                .orElse(Collections.emptyList())
                .stream()
                .map(OrderPerson::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        List<Long> todayRechargePeopleUserIdList = Stream.concat(todayOrderAmountUserIds.stream(), todayUpPointOrderPersonUserIds.stream())
                .distinct()
                .collect(Collectors.toList());
        //当日 充值人数 = (t_order_amount 中的user_id  + t_order_person(order_type = 1 类型(1 上分, 2 下分)) 的user_id ) 去重
        return todayRechargePeopleUserIdList.size();
    }

    /**
     * 获取整个充值人数
     */
    public Integer getPeopleNum(String nowDay, Integer currencyId) {
        String startTime = nowDay + " 00:00:00";
        String endTime = nowDay + " 23:59:59";

        // 当日 充值人数
        LambdaQueryWrapper<OrderAmount> todayOrderAmountQuery = Wrappers.lambdaQuery(OrderAmount.class);
        todayOrderAmountQuery.eq(OrderAmount::getOrderStatus, OrderAmountOrderStatusConstants.PAYMENT_COMPLETED);
        todayOrderAmountQuery.eq(OrderAmount::getCurrencyId, currencyId);
        todayOrderAmountQuery.between(OrderAmount::getCreateTime, startTime, endTime);

        List<OrderAmount> todayOrderAmounts = orderAmountService.list(todayOrderAmountQuery);
        List<Long> todayOrderAmountUserIds = (todayOrderAmounts != null)
                ? todayOrderAmounts.stream().map(OrderAmount::getUserId).collect(Collectors.toList())
                : Collections.emptyList();

        // 类似地设置 OrderPerson 查询
        LambdaQueryWrapper<OrderPerson> todayUpPointOrderPersonQuery = Wrappers.lambdaQuery(OrderPerson.class);
        todayUpPointOrderPersonQuery.eq(OrderPerson::getOrderType, 1);
        todayUpPointOrderPersonQuery.eq(OrderPerson::getCurrencyId, currencyId);
        todayUpPointOrderPersonQuery.between(OrderPerson::getCreateTime, startTime, endTime);
        todayUpPointOrderPersonQuery.gt(OrderPerson::getAmount, BigDecimal.ZERO);//充值金额大于0才算

        List<OrderPerson> todayUpPointOrderPersons = orderPersonService.list(todayUpPointOrderPersonQuery);
        List<Long> todayUpPointOrderPersonUserIds = (todayUpPointOrderPersons != null)
                ? todayUpPointOrderPersons.stream().map(OrderPerson::getUserId).collect(Collectors.toList())
                : Collections.emptyList();

        // 合并流并去重
        List<Long> todayRechargePeopleUserIdList = Stream.concat(
                        todayOrderAmountUserIds.stream(),
                        todayUpPointOrderPersonUserIds.stream())
                .distinct()
                .collect(Collectors.toList());

        // 返回结果
        return todayRechargePeopleUserIdList.size();
    }

    /**
     * 充值人数（自然日闭区间 [startDay, endDay]）：支付完成 ∪ 上分(amount&gt;0)，user_id 去重。
     * OrderAmount 按 pay_time（与 {@code CountOrderServicJob#getPeopleNum} 一致）；OrderPerson 按 create_time。
     *
     * @param startDay yyyy-MM-dd
     * @param endDay   yyyy-MM-dd
     */
    public Integer getPeopleNumBetween(String startDay, String endDay, Integer currencyId) {
        if (currencyId == null || startDay == null || endDay == null || startDay.isEmpty() || endDay.isEmpty()) {
            return 0;
        }
        String startTime = startDay + " 00:00:00";
        String endTime = endDay + " 23:59:59";

        LambdaQueryWrapper<OrderAmount> orderAmountQuery = Wrappers.lambdaQuery(OrderAmount.class);
        orderAmountQuery.eq(OrderAmount::getOrderStatus, OrderAmountOrderStatusConstants.PAYMENT_COMPLETED);
        orderAmountQuery.eq(OrderAmount::getCurrencyId, currencyId);
        orderAmountQuery.between(OrderAmount::getPayTime, startTime, endTime);
        List<OrderAmount> orderAmounts = orderAmountService.list(orderAmountQuery);
        List<Long> orderAmountUserIds = Optional.ofNullable(orderAmounts)
                .orElse(Collections.emptyList())
                .stream()
                .map(OrderAmount::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        LambdaQueryWrapper<OrderPerson> orderPersonQuery = Wrappers.lambdaQuery(OrderPerson.class);
        orderPersonQuery.eq(OrderPerson::getOrderType, 1);
        orderPersonQuery.eq(OrderPerson::getCurrencyId, currencyId);
        orderPersonQuery.between(OrderPerson::getCreateTime, startTime, endTime);
        orderPersonQuery.gt(OrderPerson::getAmount, BigDecimal.ZERO);

        List<OrderPerson> orderPersons = orderPersonService.list(orderPersonQuery);
        List<Long> orderPersonUserIds = Optional.ofNullable(orderPersons)
                .orElse(Collections.emptyList())
                .stream()
                .map(OrderPerson::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return Stream.concat(orderAmountUserIds.stream(), orderPersonUserIds.stream()).distinct().collect(Collectors.toList()).size();
    }

    /**
     * 指定渠道：闭区间 [startDay,endDay] 内充值人数去重（口径同 {@link #getPeopleNumBetween}，订单限定为该渠道用户）。
     * {@code channelId == null} 时等价于不按渠道过滤（全站 {@link #getPeopleNumBetween}），与汇总 SQL 不传 channel 条件时一致；
     * {@code channelId == 0} 表示仅 {@code t_user.channel_id = 0}，与 {@code t_channel_count_order.channel_id = 0} 的笔数口径一致。
     */
    public Integer getPeopleNumBetweenByChannelId(String startDay, String endDay, Long channelId, Integer currencyId) {
        if (currencyId == null || startDay == null || endDay == null || startDay.isEmpty() || endDay.isEmpty()) {
            return 0;
        }
        if (channelId == null) {
            return getPeopleNumBetween(startDay, endDay, currencyId);
        }
        if (channelId < 0) {
            return 0;
        }
        String startTime = startDay + " 00:00:00";
        String endTime = endDay + " 23:59:59";

        LambdaQueryWrapper<TUser> userQuery = Wrappers.lambdaQuery(TUser.class);
        userQuery.eq(TUser::getChannelId, channelId);
        List<TUser> channelUserList = userService.list(userQuery);
        List<Long> channelUserIdList = Optional.ofNullable(channelUserList)
                .orElse(Collections.emptyList())
                .stream()
                .map(TUser::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(channelUserIdList)) {
            return 0;
        }

        LambdaQueryWrapper<OrderAmount> orderAmountQuery = Wrappers.lambdaQuery(OrderAmount.class);
        orderAmountQuery.eq(OrderAmount::getOrderStatus, OrderAmountOrderStatusConstants.PAYMENT_COMPLETED);
        orderAmountQuery.eq(OrderAmount::getCurrencyId, currencyId);
        orderAmountQuery.between(OrderAmount::getPayTime, startTime, endTime);
        orderAmountQuery.in(OrderAmount::getUserId, channelUserIdList);
        List<OrderAmount> orderAmounts = orderAmountService.list(orderAmountQuery);
        List<Long> orderAmountUserIds = Optional.ofNullable(orderAmounts)
                .orElse(Collections.emptyList())
                .stream()
                .map(OrderAmount::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        LambdaQueryWrapper<OrderPerson> orderPersonQuery = Wrappers.lambdaQuery(OrderPerson.class);
        orderPersonQuery.eq(OrderPerson::getOrderType, 1);
        orderPersonQuery.eq(OrderPerson::getCurrencyId, currencyId);
        orderPersonQuery.between(OrderPerson::getCreateTime, startTime, endTime);
        orderPersonQuery.gt(OrderPerson::getAmount, BigDecimal.ZERO);
        orderPersonQuery.in(OrderPerson::getUserId, channelUserIdList);
        List<OrderPerson> orderPersons = orderPersonService.list(orderPersonQuery);
        List<Long> orderPersonUserIds = Optional.ofNullable(orderPersons)
                .orElse(Collections.emptyList())
                .stream()
                .map(OrderPerson::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return Stream.concat(orderAmountUserIds.stream(), orderPersonUserIds.stream()).distinct().collect(Collectors.toList()).size();
    }

    /**
     * 指定股东：闭区间 [startDay,endDay] 内充值人数去重（口径同 {@link #getPeopleNumBetween}）。
     * {@code shareholderId == null} 时等价于全站；{@code shareholderId == 0} 仅 {@code t_user.shareholder_id = 0}，与汇总表按股东 0 筛选一致。
     */
    public Integer getPeopleNumBetweenByShareholderId(String startDay, String endDay, Long shareholderId, Integer currencyId) {
        if (currencyId == null || startDay == null || endDay == null || startDay.isEmpty() || endDay.isEmpty()) {
            return 0;
        }
        if (shareholderId == null) {
            return getPeopleNumBetween(startDay, endDay, currencyId);
        }
        if (shareholderId < 0) {
            return 0;
        }
        String startTime = startDay + " 00:00:00";
        String endTime = endDay + " 23:59:59";

        LambdaQueryWrapper<TUser> userQuery = Wrappers.lambdaQuery(TUser.class);
        userQuery.eq(TUser::getShareholderId, shareholderId);
        List<TUser> users = userService.list(userQuery);
        List<Long> userIdList = Optional.ofNullable(users)
                .orElse(Collections.emptyList())
                .stream()
                .map(TUser::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(userIdList)) {
            return 0;
        }

        LambdaQueryWrapper<OrderAmount> orderAmountQuery = Wrappers.lambdaQuery(OrderAmount.class);
        orderAmountQuery.eq(OrderAmount::getOrderStatus, OrderAmountOrderStatusConstants.PAYMENT_COMPLETED);
        orderAmountQuery.eq(OrderAmount::getCurrencyId, currencyId);
        orderAmountQuery.between(OrderAmount::getPayTime, startTime, endTime);
        orderAmountQuery.in(OrderAmount::getUserId, userIdList);
        List<OrderAmount> orderAmounts = orderAmountService.list(orderAmountQuery);
        List<Long> orderAmountUserIds = Optional.ofNullable(orderAmounts)
                .orElse(Collections.emptyList())
                .stream()
                .map(OrderAmount::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        LambdaQueryWrapper<OrderPerson> orderPersonQuery = Wrappers.lambdaQuery(OrderPerson.class);
        orderPersonQuery.eq(OrderPerson::getOrderType, 1);
        orderPersonQuery.eq(OrderPerson::getCurrencyId, currencyId);
        orderPersonQuery.between(OrderPerson::getCreateTime, startTime, endTime);
        orderPersonQuery.gt(OrderPerson::getAmount, BigDecimal.ZERO);
        orderPersonQuery.in(OrderPerson::getUserId, userIdList);
        List<OrderPerson> orderPersons = orderPersonService.list(orderPersonQuery);
        List<Long> orderPersonUserIds = Optional.ofNullable(orderPersons)
                .orElse(Collections.emptyList())
                .stream()
                .map(OrderPerson::getUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return Stream.concat(orderAmountUserIds.stream(), orderPersonUserIds.stream()).distinct().collect(Collectors.toList()).size();
    }

    /**
     * 获取当前渠道下的 提现人数
     */
    public Integer getWithdrawPeopleNumByChannelId(String nowDay, Long channelId, Integer currencyId) {
        String startTime = nowDay + " 00:00:00";
        String endTime = nowDay + " 23:59:59";
        if (Objects.isNull(channelId) || Objects.isNull(currencyId)) {
            return 0;
        }
        //获取当前渠道下的所有用户
        LambdaQueryWrapper<TUser> userQuery = Wrappers.lambdaQuery(TUser.class);
        userQuery.eq(TUser::getChannelId, channelId);
        List<TUser> channelUserList = userService.list(userQuery);
        List<Long> channelUserIdList = channelUserList.stream().map(TUser::getUserId).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(channelUserIdList)) {
            return 0;
        }
        LambdaQueryWrapper<OrderWithdraw> todayOrderWithdrawQuery = Wrappers.lambdaQuery(OrderWithdraw.class);
        todayOrderWithdrawQuery.eq(OrderWithdraw::getOrderStatus, OrderWithdrawOrderStatusConstants.WITHDRAWAL_SUCCESS);
        todayOrderWithdrawQuery.eq(OrderWithdraw::getCurrencyId, currencyId);
        todayOrderWithdrawQuery.between(OrderWithdraw::getCreateTime, startTime, endTime);
        todayOrderWithdrawQuery.in(OrderWithdraw::getUserId, channelUserIdList);
        List<OrderWithdraw> todayOrderWithdraws = orderWithdrawService.list(todayOrderWithdrawQuery);
        List<Long> todayOrderWithdrawUserIds = todayOrderWithdraws == null
                ? Collections.emptyList()
                : todayOrderWithdraws.stream()
                .map(OrderWithdraw::getUserId)
                .collect(Collectors.toList());
        LambdaQueryWrapper<OrderPerson> todayDownPointOrderPersonQuery = Wrappers.lambdaQuery(OrderPerson.class);
        //类型(1 上分, 2 下分)
        todayDownPointOrderPersonQuery.eq(OrderPerson::getOrderType, 2);
        //下分类型(1.真实提现;2.扣除积分)
        todayDownPointOrderPersonQuery.eq(OrderPerson::getLowerSubtype, 1);
        todayDownPointOrderPersonQuery.eq(OrderPerson::getCurrencyId, currencyId);
        todayDownPointOrderPersonQuery.between(OrderPerson::getCreateTime, startTime, endTime);
        todayDownPointOrderPersonQuery.in(OrderPerson::getUserId, channelUserIdList);
        List<OrderPerson> todayDownPointOrderPersons = orderPersonService.list(todayDownPointOrderPersonQuery);
        List<Long> todayDownPointOrderPersonUserIds = todayDownPointOrderPersons == null
                ? Collections.emptyList()
                : todayDownPointOrderPersons.stream()
                .map(OrderPerson::getUserId)
                .collect(Collectors.toList());
        LambdaQueryWrapper<OrderLawWithdraw> todayOrderLawWithdrawQuery = Wrappers.lambdaQuery(OrderLawWithdraw.class);
        todayOrderLawWithdrawQuery.eq(OrderLawWithdraw::getOrderStatus, OrderWithdrawOrderStatusConstants.WITHDRAWAL_SUCCESS);
        todayOrderLawWithdrawQuery.eq(OrderLawWithdraw::getCurrencyId, currencyId);
        todayOrderLawWithdrawQuery.between(OrderLawWithdraw::getCreateTime, startTime, endTime);
        todayOrderLawWithdrawQuery.in(OrderLawWithdraw::getUserId, channelUserIdList);
        List<OrderLawWithdraw> todayOrderLawWithdraws = orderLawWithdrawService.list(todayOrderLawWithdrawQuery);
        List<Long> todayOrderLawWithdrawsUserIds = todayOrderLawWithdraws == null
                ? Collections.emptyList()
                : todayOrderLawWithdraws.stream()
                .map(OrderLawWithdraw::getUserId)
                .collect(Collectors.toList());
        List<Long> todayWithdrawPeopleUserIdList = Stream.of(
                        todayOrderWithdrawUserIds.stream(),
                        todayDownPointOrderPersonUserIds.stream(),
                        todayOrderLawWithdrawsUserIds.stream()
                )
                .flatMap(stream -> stream)
                .distinct()
                .collect(Collectors.toList());
        //当日 提现人数  = (t_order_withdraw 中的user_id
        // + t_order_person(order_type = 2  类型(1 上分, 2 下分) && lower_subtype = 1 下分类型(1.真实提现;2.扣除积分) ) 的user_id
        // + t_order_law_withdraw 中的user_id ) 去重
        return todayWithdrawPeopleUserIdList.size();
    }

    /**
     * 获取整个提现人数
     */
    public Integer getWithdrawPeopleNum(String nowDay, Integer currencyId) {
        String startTime = nowDay + " 00:00:00";
        String endTime = nowDay + " 23:59:59";

        LambdaQueryWrapper<OrderWithdraw> todayOrderWithdrawQuery = Wrappers.lambdaQuery(OrderWithdraw.class);
        todayOrderWithdrawQuery.eq(OrderWithdraw::getOrderStatus, OrderWithdrawOrderStatusConstants.WITHDRAWAL_SUCCESS);
        todayOrderWithdrawQuery.eq(OrderWithdraw::getCurrencyId, currencyId);
        todayOrderWithdrawQuery.between(OrderWithdraw::getCreateTime, startTime, endTime);
        List<OrderWithdraw> todayOrderWithdraws = orderWithdrawService.list(todayOrderWithdrawQuery);
        List<Long> todayOrderWithdrawUserIds = todayOrderWithdraws == null
                ? Collections.emptyList()
                : todayOrderWithdraws.stream()
                .map(OrderWithdraw::getUserId)
                .collect(Collectors.toList());
        LambdaQueryWrapper<OrderPerson> todayDownPointOrderPersonQuery = Wrappers.lambdaQuery(OrderPerson.class);
        //类型(1 上分, 2 下分)
        todayDownPointOrderPersonQuery.eq(OrderPerson::getOrderType, 2);
        //下分类型(1.真实提现;2.扣除积分)
        todayDownPointOrderPersonQuery.eq(OrderPerson::getLowerSubtype, 1);
        todayDownPointOrderPersonQuery.eq(OrderPerson::getCurrencyId, currencyId);
        todayDownPointOrderPersonQuery.between(OrderPerson::getCreateTime, startTime, endTime);
        List<OrderPerson> todayDownPointOrderPersons = orderPersonService.list(todayDownPointOrderPersonQuery);
        List<Long> todayDownPointOrderPersonUserIds = todayDownPointOrderPersons == null
                ? Collections.emptyList()
                : todayDownPointOrderPersons.stream()
                .map(OrderPerson::getUserId)
                .collect(Collectors.toList());
        LambdaQueryWrapper<OrderLawWithdraw> todayOrderLawWithdrawQuery = Wrappers.lambdaQuery(OrderLawWithdraw.class);
        todayOrderLawWithdrawQuery.eq(OrderLawWithdraw::getOrderStatus, OrderWithdrawOrderStatusConstants.WITHDRAWAL_SUCCESS);
        todayOrderLawWithdrawQuery.eq(OrderLawWithdraw::getCurrencyId, currencyId);
        todayOrderLawWithdrawQuery.between(OrderLawWithdraw::getCreateTime, startTime, endTime);
        List<OrderLawWithdraw> todayOrderLawWithdraws = orderLawWithdrawService.list(todayOrderLawWithdrawQuery);
        List<Long> todayOrderLawWithdrawsUserIds = todayOrderLawWithdraws == null
                ? Collections.emptyList()
                : todayOrderLawWithdraws.stream()
                .map(OrderLawWithdraw::getUserId)
                .collect(Collectors.toList());
        List<Long> todayWithdrawPeopleUserIdList = Stream.of(
                        todayOrderWithdrawUserIds.stream(),
                        todayDownPointOrderPersonUserIds.stream(),
                        todayOrderLawWithdrawsUserIds.stream()
                )
                .flatMap(stream -> stream)
                .distinct()
                .collect(Collectors.toList());
        //当日 提现人数  = (t_order_withdraw 中的user_id
        // + t_order_person(order_type = 2  类型(1 上分, 2 下分) && lower_subtype = 1 下分类型(1.真实提现;2.扣除积分) ) 的user_id
        // + t_order_law_withdraw 中的user_id ) 去重
        // 返回结果
        return todayWithdrawPeopleUserIdList.size();
    }

    /**
     * 该渠道下的活跃人数
     */
    public Integer getChannelActiveUserCount(Long channelId, String nowDay) {
        //获取当前渠道下的所有用户
        LambdaQueryWrapper<TUser> userQuery = Wrappers.lambdaQuery(TUser.class);
        if (Objects.nonNull(channelId)) {
            userQuery.eq(TUser::getChannelId, channelId);
        }
        List<TUser> channelUserList = userService.list(userQuery);
        if (CollectionUtils.isEmpty(channelUserList)) {
            return 0;
        }
        List<Long> channelUserIdList = channelUserList.stream().map(TUser::getUserId).collect(Collectors.toList());

        //金额有变动的 -> 就是活跃的
        String startTime = nowDay + " 00:00:00";
        String endTime = nowDay + " 23:59:59";

        LambdaQueryWrapper<AmountChange> amountChangeQuery = Wrappers.lambdaQuery(AmountChange.class);
        amountChangeQuery.between(AmountChange::getCreateTime, startTime, endTime);
        amountChangeQuery.in(AmountChange::getUserId, channelUserIdList);
        List<AmountChange> amountChangeList = amountChangeService.list(amountChangeQuery);
        List<Long> activePeopleUserIds = amountChangeList.stream().map(AmountChange::getUserId).distinct().collect(Collectors.toList());
        log.info("渠道活跃人数统计, channelId={}, nowDay={}, activePeopleUserIds.size={}", channelId, nowDay, activePeopleUserIds.size());
        return activePeopleUserIds.size();
    }

    /**
     * 活跃人数查询
     *
     * @param nowDay
     * @return
     */
    public Integer getActiveUserCount(String nowDay) {
        //金额有变动的 -> 就是活跃的
//        String startTime = nowDay + " 00:00:00";
//        String endTime = nowDay + " 23:59:59";
//        LambdaQueryWrapper<AmountChange> amountChangeQuery = Wrappers.lambdaQuery(AmountChange.class);
//        amountChangeQuery.between(AmountChange::getCreateTime, startTime, endTime);
//        List<AmountChange> amountChangeList = amountChangeService.list(amountChangeQuery);
//        if (CollectionUtils.isEmpty(amountChangeList)) {
//            return 0;
//        }
//        List<Long> activePeopleUserIds = amountChangeList.stream().map(AmountChange::getUserId)
//                .distinct()
//                .collect(Collectors.toList());
//        log.info(
//                "活跃人数总统计, nowDay={}, activePeopleUserIds={}, activePeopleUserIds.size={}"
//                , nowDay, activePeopleUserIds.stream().map(String::valueOf).collect(Collectors.joining(","))
//                , activePeopleUserIds.size()
//        );
//        return activePeopleUserIds.size();
        return Math.toIntExact(redisOnlineService.getTodayActiveCount());
    }


    /**
     * 处理留存相关数据-countOrder
     */
    public void processingRetainedDataByCountOrder(
            CountOrder countOrder, String startTime, String endTime
    ) {
        DecimalFormat df = new DecimalFormat("0.00");
        //4.三日留存    三天前注册人数中当日有打码量的人数  除以 三天前注册的用户人数
        String threeRemain = "0";
        //三天前注册人数中当日有打码量的人数
        int threeRemainHaveOrderTermNum = 0;

        Date threeDayEndTime = DateUtil.offsetMinute(DateUtils.parseDate(startTime), -3);
        LambdaQueryWrapper<TUser> threeDayUserQueryWrapper = Wrappers.lambdaQuery(TUser.class);
        threeDayUserQueryWrapper.lt(TUser::getCreateTime, threeDayEndTime);
        List<TUser> threeDayUserList = userService.list(threeDayUserQueryWrapper);
        if (CollectionUtils.isNotEmpty(threeDayUserList)) {
            List<Long> threeDayUserIdList = threeDayUserList.stream().map(TUser::getUserId).collect(Collectors.toList());
            LambdaQueryWrapper<OrderTerm> query = Wrappers.lambdaQuery(OrderTerm.class);
            query.between(OrderTerm::getCreateTime, startTime, endTime);
            query.in(OrderTerm::getUserId, threeDayUserIdList);
            List<OrderTerm> orderTermList = orderTermService.list(query);
            if (CollectionUtils.isNotEmpty(orderTermList)) {
                Map<Long, List<OrderTerm>> orderTermListMap = orderTermList.stream()
                        .collect(Collectors.groupingBy(OrderTerm::getUserId));
                threeRemainHaveOrderTermNum = orderTermListMap.size();
                threeRemain = df.format((float) threeRemainHaveOrderTermNum / threeDayUserList.size() * 100);
            }
        }
        countOrder.setThreeRemain(new BigDecimal(threeRemain));
        countOrder.setThreeRemainNum(threeRemainHaveOrderTermNum);

        //5.七日留存   七天前注册人数中当日有打码量的人数  除以 七天前注册的用户人数
        String sevenRemain = "0";
        //七天前注册人数中当日有打码量的人数
        int sevenRemainHaveOrderTermNum = 0;

        Date sevenDayEndTime = DateUtil.offsetMinute(DateUtils.parseDate(startTime), -7);
        LambdaQueryWrapper<TUser> sevenDayUserQueryWrapper = Wrappers.lambdaQuery(TUser.class);
        sevenDayUserQueryWrapper.lt(TUser::getCreateTime, sevenDayEndTime);
        List<TUser> sevenDayUserList = userService.list(sevenDayUserQueryWrapper);
        if (CollectionUtils.isNotEmpty(sevenDayUserList)) {
            List<Long> sevenDayUserIdList = sevenDayUserList.stream().map(TUser::getUserId).collect(Collectors.toList());
            LambdaQueryWrapper<OrderTerm> query = Wrappers.lambdaQuery(OrderTerm.class);
            query.between(OrderTerm::getCreateTime, startTime, endTime);
            query.in(OrderTerm::getUserId, sevenDayUserIdList);
            List<OrderTerm> orderTermList = orderTermService.list(query);
            if (CollectionUtils.isNotEmpty(orderTermList)) {
                Map<Long, List<OrderTerm>> orderTermListMap = orderTermList.stream()
                        .collect(Collectors.groupingBy(OrderTerm::getUserId));
                sevenRemainHaveOrderTermNum = orderTermListMap.size();
                sevenRemain = df.format((float) sevenRemainHaveOrderTermNum / sevenDayUserList.size() * 100);
            }
        }
        countOrder.setSevenRemain(new BigDecimal(sevenRemain));
        countOrder.setSevenRemainNum(sevenRemainHaveOrderTermNum);
    }

    /**
     * 处理留存相关数据 - ChannelCountOrder
     */
    public void processingRetainedDataByChannelCountOrder(
            Long channelId, ChannelCountOrder channelCountOrder
            , String startTime, String endTime
    ) {
        DecimalFormat df = new DecimalFormat("0.00");
        //4.三日留存    三天前注册人数中当日有打码量的人数  除以 三天前注册的用户人数
        String threeRemain = "0";
        //三天前注册人数中当日有打码量的人数
        int threeRemainHaveOrderTermNum = 0;

        Date threeDayEndTime = DateUtil.offsetMinute(DateUtils.parseDate(startTime), -3);
        LambdaQueryWrapper<TUser> threeDayUserQueryWrapper = Wrappers.lambdaQuery(TUser.class);
        threeDayUserQueryWrapper.lt(TUser::getCreateTime, threeDayEndTime);
        threeDayUserQueryWrapper.eq(TUser::getChannelId, channelId);
        List<TUser> threeDayUserList = userService.list(threeDayUserQueryWrapper);
        if (CollectionUtils.isNotEmpty(threeDayUserList)) {
            List<Long> threeDayUserIdList = threeDayUserList.stream().map(TUser::getUserId).collect(Collectors.toList());
            LambdaQueryWrapper<OrderTerm> query = Wrappers.lambdaQuery(OrderTerm.class);
            query.between(OrderTerm::getCreateTime, startTime, endTime);
            query.in(OrderTerm::getUserId, threeDayUserIdList);
            List<OrderTerm> orderTermList = orderTermService.list(query);
            if (CollectionUtils.isNotEmpty(orderTermList)) {
                Map<Long, List<OrderTerm>> orderTermListMap = orderTermList.stream()
                        .collect(Collectors.groupingBy(OrderTerm::getUserId));
                threeRemainHaveOrderTermNum = orderTermListMap.size();
                threeRemain = df.format((float) threeRemainHaveOrderTermNum / threeDayUserList.size() * 100);
            }
        }
        channelCountOrder.setThreeRemain(new BigDecimal(threeRemain));
        channelCountOrder.setThreeRemainNum(threeRemainHaveOrderTermNum);

        //5.七日留存   七天前注册人数中当日有打码量的人数  除以 七天前注册的用户人数
        String sevenRemain = "0";
        //七天前注册人数中当日有打码量的人数
        int sevenRemainHaveOrderTermNum = 0;
        Date sevenDayEndTime = DateUtil.offsetMinute(DateUtils.parseDate(startTime), -7);
        LambdaQueryWrapper<TUser> sevenDayUserQueryWrapper = Wrappers.lambdaQuery(TUser.class);
        sevenDayUserQueryWrapper.lt(TUser::getCreateTime, sevenDayEndTime);
        sevenDayUserQueryWrapper.eq(TUser::getChannelId, channelId);
        List<TUser> sevenDayUserList = userService.list(sevenDayUserQueryWrapper);
        if (CollectionUtils.isNotEmpty(sevenDayUserList)) {
            List<Long> sevenDayUserIdList = sevenDayUserList.stream().map(TUser::getUserId).collect(Collectors.toList());
            LambdaQueryWrapper<OrderTerm> query = Wrappers.lambdaQuery(OrderTerm.class);
            query.between(OrderTerm::getCreateTime, startTime, endTime);
            query.in(OrderTerm::getUserId, sevenDayUserIdList);
            List<OrderTerm> orderTermList = orderTermService.list(query);
            if (CollectionUtils.isNotEmpty(orderTermList)) {
                Map<Long, List<OrderTerm>> orderTermListMap = orderTermList.stream()
                        .collect(Collectors.groupingBy(OrderTerm::getUserId));
                sevenRemainHaveOrderTermNum = orderTermListMap.size();
                sevenRemain = df.format((float) sevenRemainHaveOrderTermNum / sevenDayUserList.size() * 100);
            }
        }
        channelCountOrder.setSevenRemain(new BigDecimal(sevenRemain));
        channelCountOrder.setSevenRemainNum(sevenRemainHaveOrderTermNum);
    }

    /**
     * 处理留存相关数据-countOrder  new
     */
    public void processingRetainedDataByCountOrderNew(
            String uuid, CountOrder countOrder, String startTime, OrderTerm orderTerm
    ) {
        log.info("处理留存相关数据-countOrder:{},startTime:{},orderTerm:{},uuid={}", countOrder.toString(), startTime, orderTerm.toString(), uuid);
        //三天前注册人数
        Date threeDayEndTime = DateUtil.offsetDay(DateUtils.parseDate(startTime), -3);
        LambdaQueryWrapper<TUser> threeDayUserQueryWrapper = Wrappers.lambdaQuery(TUser.class);
        threeDayUserQueryWrapper.lt(TUser::getCreateTime, threeDayEndTime);
        int threeDayCount = userService.count(threeDayUserQueryWrapper);
        log.info("处理留存相关数据-countOrder,三天前注册人数:{},uuid={}", threeDayCount, uuid);
        if (threeDayCount > 0) {
            countOrder.setThreeTotal(threeDayCount);
            //判断该用户是否是3天前注册的
            TUser tUser = userService.selectTUserById(orderTerm.getUserId());
            if (threeDayEndTime.compareTo(tUser.getCreateTime()) > 0) {
                //判断是否首次注单
                LambdaQueryWrapper<OrderTerm> queryThree = Wrappers.lambdaQuery(OrderTerm.class);
                queryThree.between(OrderTerm::getCreateTime, startTime, orderTerm.getCreateTime()).eq(OrderTerm::getUserId,
                        orderTerm.getUserId());
                int countThree = orderTermService.count(queryThree);
                log.info("处理留存相关数据-countOrder,三天前判断是否首次注单:{},uuid={}", countThree, uuid);
                if (countThree == 1) {
                    countOrder.setThreeRemainNum(1);
                }
            }
        }

        //7天前注册人数
        Date sevenDayEndTime = DateUtil.offsetDay(DateUtils.parseDate(startTime), -7);
        LambdaQueryWrapper<TUser> evenDayUserQueryWrapper = Wrappers.lambdaQuery(TUser.class);
        evenDayUserQueryWrapper.lt(TUser::getCreateTime, sevenDayEndTime);
        int sevenDayCount = userService.count(evenDayUserQueryWrapper);
        log.info("处理留存相关数据-countOrder,7天前注册人数:{},uuid={}", sevenDayCount, uuid);
        if (sevenDayCount > 0) {
            countOrder.setSevenTotal(sevenDayCount);
            //判断该用户是否是7天前注册的
            TUser tUser = userService.selectTUserById(orderTerm.getUserId());

            if (sevenDayEndTime.compareTo(tUser.getCreateTime()) > 0) {
                //判断是否首次注单
                LambdaQueryWrapper<OrderTerm> queryThree = Wrappers.lambdaQuery(OrderTerm.class);
                queryThree.between(OrderTerm::getCreateTime, startTime, orderTerm.getCreateTime()).eq(OrderTerm::getUserId,
                        orderTerm.getUserId());
                int countThree = orderTermService.count(queryThree);
                log.info("处理留存相关数据-countOrder,三天前判断是否首次注单:{},uuid={}", countThree, uuid);
                if (countThree == 1) {
                    countOrder.setSevenRemainNum(1);
                }
            }
        }
        log.info("处理留存相关数据-countOrder:{},uuid={}", countOrder, uuid);
    }

    /**
     * 处理留存相关数据-ChannelCountOrder new
     */
    public void processingRetainedDataByChannelCountOrderNew(String uuId, Long channelId, ChannelCountOrder channelCountOrder,
                                                             String startTime, OrderTerm orderTerm) {

        log.info("处理留存相关数据-ChannelCountOrder channelId:{},channelCountOrder:{},startTime:{},orderTerm:{},uuid={}",
                channelId, channelCountOrder.toString(), startTime, orderTerm.toString(), uuId);
        //三天前注册人数
        Date threeDayEndTime = DateUtil.offsetDay(DateUtils.parseDate(startTime), -3);
        LambdaQueryWrapper<TUser> threeDayUserQueryWrapper = Wrappers.lambdaQuery(TUser.class);
        threeDayUserQueryWrapper.lt(TUser::getCreateTime, threeDayEndTime).eq(TUser::getChannelId, channelId);
        int threeDayCount = userService.count(threeDayUserQueryWrapper);
        log.info("处理留存相关数据-ChannelCountOrder,3天前注册人数:{},uuid={}", threeDayCount, uuId);
        if (threeDayCount > 0) {
            channelCountOrder.setThreeTotal(threeDayCount);
            //判断该用户是否是3天前注册的
            TUser tUser = userService.selectTUserById(orderTerm.getUserId());
            if (threeDayEndTime.compareTo(tUser.getCreateTime()) > 0) {
                //判断是否首次注单
                LambdaQueryWrapper<OrderTerm> queryThree = Wrappers.lambdaQuery(OrderTerm.class);
                queryThree.between(OrderTerm::getCreateTime, startTime, orderTerm.getCreateTime())
                        .eq(OrderTerm::getUserId, orderTerm.getUserId());
                int countThree = orderTermService.count(queryThree);
                log.info("处理留存相关数据-ChannelCountOrder,三天前判断是否首次注单:{},uuid={}", countThree, uuId);
                if (countThree == 1) {
                    channelCountOrder.setThreeRemainNum(1);
                }
            }
        }

        //7天前注册人数
        Date sevenDayEndTime = DateUtil.offsetDay(DateUtils.parseDate(startTime), -7);
        LambdaQueryWrapper<TUser> evenDayUserQueryWrapper = Wrappers.lambdaQuery(TUser.class);
        evenDayUserQueryWrapper.lt(TUser::getCreateTime, sevenDayEndTime).eq(TUser::getChannelId, channelId);
        int sevenDayCount = userService.count(evenDayUserQueryWrapper);
        log.info("处理留存相关数据-ChannelCountOrder,7天前注册人数:{},uuid={}", sevenDayCount, uuId);
        if (sevenDayCount > 0) {
            channelCountOrder.setSevenTotal(sevenDayCount);
            //判断该用户是否是7天前注册的
            TUser tUser = userService.selectTUserById(orderTerm.getUserId());
            if (sevenDayEndTime.compareTo(tUser.getCreateTime()) > 0) {
                //判断是否首次注单
                LambdaQueryWrapper<OrderTerm> queryThree = Wrappers.lambdaQuery(OrderTerm.class);
                queryThree.between(OrderTerm::getCreateTime, startTime, orderTerm.getCreateTime()).eq(OrderTerm::getUserId,
                        orderTerm.getUserId());
                int countThree = orderTermService.count(queryThree);
                log.info("处理留存相关数据-ChannelCountOrder,7天前判断是否首次注单:{},uuid={}", countThree, uuId);
                if (countThree == 1) {
                    channelCountOrder.setSevenRemainNum(1);
                }
            }
        }
        log.info("处理留存相关数据-ChannelCountOrder结果:{},uuid={}", channelCountOrder, uuId);

    }

    /**
     * CountOrder 使用的首存/二存/复存数据查询
     *
     * @param currencyId
     * @return
     */
    public ConsumOrderVO findFirstNumOrMoneyByCountOrder(String uuid, String day, Integer currencyId, CountOrder countOrder) {
        ConsumOrderVO consumOrderVO = new ConsumOrderVO();

        String startTime = day + " 00:00:00";
        String endTime = day + " 23:59:59";
        log.info("uuid ={}, findFirstNumOrMoneyByCountOrder, day={}, currencyId={}, countOrder={}", uuid, day, currencyId,
                JSON.toJSONString(countOrder));

        //统计首存用户新增
        LambdaQueryWrapper<OrderAmount> queryVipWrapper = Wrappers.lambdaQuery(OrderAmount.class)
                .between(OrderAmount::getPayTime, startTime, endTime)
                //已支付状态常量
                .eq(OrderAmount::getOrderStatus, OrderAmountOrderStatusConstants.PAYMENT_COMPLETED)
                .eq(OrderAmount::getCurrencyId, currencyId)
                .orderByAsc(OrderAmount::getPayTime); // 按支付时间升序排序
        List<OrderAmount> todayOrderAmountOrders = orderAmountMapper.selectList(queryVipWrapper);

        //用户自己充值的订单数据
        List<TopUpOrderData> orderAmountDataList = todayOrderAmountOrders == null
                ? Collections.emptyList()
                : todayOrderAmountOrders.stream()
                .map(it -> TopUpOrderData.builder()
                        .orderId(it.getId())
                        .userId(it.getUserId())
                        .amount(it.getAmount())
                        .payTime(it.getPayTime())
                        .orderType(1)
                        .build())
                .collect(Collectors.toList());

        //后台上分的订单
        LambdaQueryWrapper<OrderPerson> queryOrderPersonWrapper = Wrappers.lambdaQuery(OrderPerson.class)
                .between(OrderPerson::getCreateTime, startTime, endTime)
                //类型(1 上分, 2 下分)
                .eq(OrderPerson::getOrderType, 1)
                .eq(OrderPerson::getCurrencyId, currencyId)
                .gt(OrderPerson::getAmount, BigDecimal.ZERO)//充值金额大于0才算
                .orderByAsc(OrderPerson::getCreateTime); // 按创建时间升序排序
        List<OrderPerson> orderPersonList = orderPersonService.list(queryOrderPersonWrapper);

        List<TopUpOrderData> orderPersonDataList = orderPersonList == null
                ? Collections.emptyList()
                : orderPersonList.stream()
                .map(it -> TopUpOrderData.builder()
                        .orderId(it.getId())
                        .userId(it.getUserId())
                        .amount(it.getAmount())
                        .payTime(it.getCreateTime())
                        .orderType(2)
                        .build())
                .collect(Collectors.toList());

        List<TopUpOrderData> topUpOrderData = new ArrayList<>();
        topUpOrderData.addAll(orderAmountDataList);
        topUpOrderData.addAll(orderPersonDataList);
        topUpOrderData = topUpOrderData.stream()
                .sorted(Comparator.comparing(TopUpOrderData::getPayTime)).collect(Collectors.toList());

        Map<Long, BigDecimal> userAmountMap = new HashMap<>(); // 存储每位首存用户的金额
        Map<Long, Map<String, Object>> secondDataMap = new HashMap<>(); // 存储用户第二次存款的金额和数量
        Map<Long, Map<String, Object>> restoreDataMap = new HashMap<>(); //存储 用户复存的金额 和 数量
        // 按时间顺序查询今天的订单列表
        for (TopUpOrderData order : topUpOrderData) {
            Long userId = order.getUserId();
            // 如果该用户不在Map中，表示第一次出现
            if (!userAmountMap.containsKey(userId)) {
                // 查询数据库，判断今天00:00之前是否有订单记录
                LambdaQueryWrapper<OrderAmount> queryNumVipWrapper = Wrappers.lambdaQuery(OrderAmount.class)
                        .eq(OrderAmount::getUserId, userId)
                        .eq(OrderAmount::getCurrencyId, currencyId)
                        .eq(OrderAmount::getOrderStatus, OrderAmountOrderStatusConstants.PAYMENT_COMPLETED)
                        .lt(OrderAmount::getPayTime, order.getPayTime());
                Integer lastNum1 = orderAmountMapper.selectCount(queryNumVipWrapper);

                LambdaQueryWrapper<OrderPerson> orderPersonQuery = Wrappers.lambdaQuery(OrderPerson.class)
                        .eq(OrderPerson::getUserId, userId)
                        //类型(1 上分, 2 下分)
                        .eq(OrderPerson::getOrderType, 1)
                        .eq(OrderPerson::getCurrencyId, currencyId)
                        .gt(OrderPerson::getAmount, BigDecimal.ZERO)//充值金额大于0才算
                        .lt(OrderPerson::getCreateTime, order.getPayTime());
                Integer lastNum2 = orderPersonService.count(orderPersonQuery);

                // 如果今天之前没有订单记录，将该用户的金额存入Map
                if ((lastNum1 + lastNum2) == 0) {
                    userAmountMap.put(userId, order.getAmount());
                }
            }

            int countNum = 0;
            //订单类型 1:用户自动充值;2:手动上分
            if (order.getOrderType() == 1) {
                //查询 当前订单之前 是否有其他支付过的订单
                LambdaQueryWrapper<OrderAmount> queryWrapper = Wrappers.lambdaQuery(OrderAmount.class)
                        .eq(OrderAmount::getUserId, userId)
                        .eq(OrderAmount::getCurrencyId, currencyId)
                        .eq(OrderAmount::getOrderStatus, OrderAmountOrderStatusConstants.PAYMENT_COMPLETED)
                        .ne(OrderAmount::getId, order.getOrderId()) // id != 当前orderId
                        .lt(OrderAmount::getPayTime, order.getPayTime());
                Integer countNum1 = orderAmountMapper.selectCount(queryWrapper);

                LambdaQueryWrapper<OrderPerson> orderPersonQuery = Wrappers.lambdaQuery(OrderPerson.class)
                        .eq(OrderPerson::getUserId, userId)
                        //类型(1 上分, 2 下分)
                        .eq(OrderPerson::getOrderType, 1)
                        .eq(OrderPerson::getCurrencyId, currencyId)
                        .gt(OrderPerson::getAmount, BigDecimal.ZERO)//充值金额大于0才算
                        .lt(OrderPerson::getCreateTime, order.getPayTime());
                Integer countNum2 = orderPersonService.count(orderPersonQuery);

                countNum = countNum1 + countNum2;
            } else {
                LambdaQueryWrapper<OrderPerson> orderPersonQuery = Wrappers.lambdaQuery(OrderPerson.class)
                        .eq(OrderPerson::getUserId, userId)
                        //类型(1 上分, 2 下分)
                        .eq(OrderPerson::getOrderType, 1)
                        .ne(OrderPerson::getId, order.getOrderId()) // id != 当前orderId
                        .eq(OrderPerson::getCurrencyId, currencyId)
                        .gt(OrderPerson::getAmount, BigDecimal.ZERO)//充值金额大于0才算
                        .lt(OrderPerson::getCreateTime, order.getPayTime());
                Integer countNum2 = orderPersonService.count(orderPersonQuery);

                //查询 当前订单之前 是否有其他支付过的订单
                LambdaQueryWrapper<OrderAmount> queryWrapper = Wrappers.lambdaQuery(OrderAmount.class)
                        .eq(OrderAmount::getUserId, userId)
                        .eq(OrderAmount::getCurrencyId, currencyId)
                        .eq(OrderAmount::getOrderStatus, OrderAmountOrderStatusConstants.PAYMENT_COMPLETED)
                        .lt(OrderAmount::getPayTime, order.getPayTime());
                Integer countNum1 = orderAmountMapper.selectCount(queryWrapper);

                countNum = countNum1 + countNum2;
            }
            if (countNum == 1) {
                //说明之前有一笔充值订单, 二存只统计一次
                if (secondDataMap.containsKey(userId)) { //包含
                    Map<String, Object> data = secondDataMap.get(userId);
                    int count = Integer.parseInt(data.get("count").toString());
                    data.put("count", count + 1);
                    BigDecimal countAmount = new BigDecimal(data.get("countAmount").toString());
                    data.put("countAmount", countAmount.add(order.getAmount()));
                    secondDataMap.put(userId, data);
                } else { //不包含
                    Map<String, Object> data = new HashMap<>();
                    data.put("count", 1);
                    data.put("countAmount", order.getAmount());
                    secondDataMap.put(userId, data);
                }
            }

            if (countNum > 1) {
                if (restoreDataMap.containsKey(userId)) { //包含
                    Map<String, Object> data = restoreDataMap.get(userId);
                    int count = Integer.parseInt(data.get("count").toString());
                    data.put("count", count + 1);
                    BigDecimal countAmount = new BigDecimal(data.get("countAmount").toString());
                    data.put("countAmount", countAmount.add(order.getAmount()));
                    restoreDataMap.put(userId, data);
                } else { //不包含
                    Map<String, Object> data = new HashMap<>();
                    data.put("count", 1);
                    data.put("countAmount", order.getAmount());
                    restoreDataMap.put(userId, data);
                }
            }

        }
        log.info("uuid ={}, 首存数据, userAmountMap={}", uuid,JSON.toJSONString(userAmountMap));
        log.info("uuid ={}, 二存数据, secondDataMap={}", uuid,JSON.toJSONString(secondDataMap));
        log.info("uuid ={}, 复存数据, restoreDataMap={}",uuid, JSON.toJSONString(restoreDataMap));
        consumOrderVO.setUserAmountMap(userAmountMap);
        consumOrderVO.setSecondDataMap(secondDataMap);
        consumOrderVO.setRestoreDataMap(restoreDataMap);

        //1.首存  用户第一充值 金额 或 数量  当日
        // 统计首次充值用户的总金额
        BigDecimal firstAmount = consumOrderVO.getUserAmountMap().values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int firstNum = consumOrderVO.getUserAmountMap().size();
        countOrder.setFirstAmount(firstAmount);//首存金额
        countOrder.setFirstNum(firstNum);//首存
        //2.二存以上  第二次充值以上的 金额 或 数量 当日
        //统计第二次存款
        AtomicInteger secondNumAtomic = new AtomicInteger(0);
        AtomicReference<BigDecimal> secondAmountAtomic = new AtomicReference<>(BigDecimal.ZERO);
        consumOrderVO.getSecondDataMap().forEach((k, v) -> {
            Object countObj = v.get("count");
            if (Objects.nonNull(countObj)) {
                int count = Integer.parseInt(String.valueOf(countObj));
                secondNumAtomic.addAndGet(count);
            }
            Object countAmountObj = v.get("countAmount");
            if (Objects.nonNull(countAmountObj)) {
                BigDecimal countAmount = new BigDecimal(String.valueOf(countAmountObj));
                secondAmountAtomic.updateAndGet(
                        y -> y.add(
                                Optional.of(countAmount).orElse(BigDecimal.ZERO)
                        )
                );
            }
        });
        int secondNum = secondNumAtomic.get();
        countOrder.setSecondNum(secondNum);//二存充值用户人数
        BigDecimal secondAmount = secondAmountAtomic.get();
        countOrder.setSecondAmount(secondAmount);//二存充值金额

        //2.复存的 金额 或 数量 当日
        //统计复存
        AtomicReference<BigDecimal> restoreAmountAtomic = new AtomicReference<>(BigDecimal.ZERO);
        consumOrderVO.getRestoreDataMap().forEach((k, v) -> { //k -> 用户id; v -> map(count:复存的次数; countAmount:复存的金额)
            Object countAmountObj = v.get("countAmount");
            if (Objects.nonNull(countAmountObj)) {
                BigDecimal countAmount = new BigDecimal(String.valueOf(countAmountObj));
                restoreAmountAtomic.updateAndGet(
                        y -> y.add(
                                Optional.of(countAmount).orElse(BigDecimal.ZERO)
                        )
                );
            }
        });
        int restoreNum = consumOrderVO.getRestoreDataMap().size();
        countOrder.setRestoreNum(restoreNum);//复存,充值用户人数
        BigDecimal restoreAmount = restoreAmountAtomic.get();
        countOrder.setRestoreAmount(restoreAmount);//复存,充值金额

        return consumOrderVO;
    }

    /**
     * ChannelCountOrder 使用的首存/二存/复存数据查询
     *
     * @param currencyId
     * @param channelId
     * @return
     */
    public ConsumOrderVO findFirstNumOrMoneyByChannelCountOrder(
            String uuid, String day, Integer currencyId, Long channelId, ChannelCountOrder channelCountOrder
    ) {
        ConsumOrderVO consumOrderVO = new ConsumOrderVO();
        String startTime = day + " 00:00:00";
        String endTime = day + " 23:59:59";

        log.info("uuid ={}, findFirstNumOrMoneyByChannelCountOrder, day={}, currencyId={}, channelCountOrder={}"
                , uuid, day, currencyId, JSON.toJSONString(channelCountOrder));

        //统计首存用户新增
        LambdaQueryWrapper<OrderAmount> queryVipWrapper = Wrappers.lambdaQuery(OrderAmount.class)
                .between(OrderAmount::getPayTime, startTime, endTime)
                //已支付状态常量
                .eq(OrderAmount::getOrderStatus, OrderAmountOrderStatusConstants.PAYMENT_COMPLETED)
                .eq(OrderAmount::getCurrencyId, currencyId)
                .orderByAsc(OrderAmount::getPayTime); // 按支付时间升序排序
        List<OrderAmount> todayOrderAmountOrders = orderAmountMapper.selectList(queryVipWrapper);

        //用户自己充值的订单数据
        List<TopUpOrderData> orderAmountDataList = todayOrderAmountOrders == null ? Collections.emptyList()
                : todayOrderAmountOrders.stream()
                .map(it -> TopUpOrderData.builder()
                        .orderId(it.getId())
                        .userId(it.getUserId())
                        .amount(it.getAmount())
                        .payTime(it.getPayTime())
                        .orderType(1)
                        .build())
                .collect(Collectors.toList());

        //后台上分的订单
        LambdaQueryWrapper<OrderPerson> queryOrderPersonWrapper = Wrappers.lambdaQuery(OrderPerson.class)
                .between(OrderPerson::getCreateTime, startTime, endTime)
                //类型(1 上分, 2 下分)
                .eq(OrderPerson::getOrderType, 1)
                .eq(OrderPerson::getCurrencyId, currencyId)
                .gt(OrderPerson::getAmount, BigDecimal.ZERO)//充值金额大于0才算
                .orderByAsc(OrderPerson::getCreateTime); // 按创建时间升序排序
        List<OrderPerson> orderPersonList = orderPersonService.list(queryOrderPersonWrapper);

        List<TopUpOrderData> orderPersonDataList = orderPersonList == null
                ? Collections.emptyList()
                : orderPersonList.stream()
                .map(it -> TopUpOrderData.builder()
                        .orderId(it.getId())
                        .userId(it.getUserId())
                        .amount(it.getAmount())
                        .payTime(it.getCreateTime())
                        .orderType(2)
                        .build())
                .collect(Collectors.toList());

        List<TopUpOrderData> topUpOrderData = new ArrayList<>();
        topUpOrderData.addAll(orderAmountDataList);
        topUpOrderData.addAll(orderPersonDataList);
        topUpOrderData = topUpOrderData.stream()
                .sorted(Comparator.comparing(TopUpOrderData::getPayTime)).collect(Collectors.toList());

        Map<Long, BigDecimal> userAmountMap = new HashMap<>(); // 存储每位首存用户的金额
        Map<Long, Map<String, Object>> secondDataMap = new HashMap<>(); // 存储用户第二次存款的金额和数量
        Map<Long, Map<String, Object>> restoreDataMap = new HashMap<>(); //存储 用户复存的金额 和 数量
        for (TopUpOrderData order : topUpOrderData) {
            Long userId = order.getUserId();
            TUser user = userService.getById(userId);
            if (null == user) {
                log.error("uuid ={}, findFirstNumOrMoneyByChannelCountOrder->该用户不存在, day={}, currencyId={}, userId={}"
                        , uuid, day, currencyId, userId);
                continue;
            }
            if (null == user.getChannelId()) {
                log.error("uuid ={}, findFirstNumOrMoneyByChannelCountOrder->该用户渠道ID为空, day={}, currencyId={}, userId={}"
                        , uuid, day, currencyId, userId);
                user.setChannelId(0L);
            }
            if (!user.getChannelId().equals(channelId)) {
                continue;
            }
            // 如果该用户不在Map中，表示第一次出现
            if (!userAmountMap.containsKey(userId)) {
                // 查询数据库，判断今天00:00之前是否有订单记录
                LambdaQueryWrapper<OrderAmount> queryNumVipWrapper = Wrappers.lambdaQuery(OrderAmount.class)
                        .eq(OrderAmount::getUserId, userId)
                        .eq(OrderAmount::getCurrencyId, currencyId)
                        .eq(OrderAmount::getOrderStatus, OrderAmountOrderStatusConstants.PAYMENT_COMPLETED)
                        .lt(OrderAmount::getPayTime, order.getPayTime());
                Integer lastNum1 = orderAmountMapper.selectCount(queryNumVipWrapper);

                LambdaQueryWrapper<OrderPerson> orderPersonQuery = Wrappers.lambdaQuery(OrderPerson.class)
                        .eq(OrderPerson::getUserId, userId)
                        //类型(1 上分, 2 下分)
                        .eq(OrderPerson::getOrderType, 1)
                        .eq(OrderPerson::getCurrencyId, currencyId)
                        .gt(OrderPerson::getAmount, BigDecimal.ZERO)//充值金额大于0才算
                        .lt(OrderPerson::getCreateTime, order.getPayTime());
                Integer lastNum2 = orderPersonService.count(orderPersonQuery);

                // 如果今天之前没有订单记录，将该用户的金额存入Map
                if ((lastNum1 + lastNum2) == 0) {
                    userAmountMap.put(userId, order.getAmount());
                }
            }

            int countNum = 0;
            //订单类型 1:用户自动充值;2:手动上分
            if (order.getOrderType() == 1) {
                //查询 当前订单之前 是否有其他支付过的订单
                LambdaQueryWrapper<OrderAmount> queryWrapper = Wrappers.lambdaQuery(OrderAmount.class)
                        .eq(OrderAmount::getUserId, userId)
                        .eq(OrderAmount::getCurrencyId, currencyId)
                        .eq(OrderAmount::getOrderStatus, OrderAmountOrderStatusConstants.PAYMENT_COMPLETED)
                        .ne(OrderAmount::getId, order.getOrderId()) // id != 当前orderId
                        .lt(OrderAmount::getPayTime, order.getPayTime());
                Integer countNum1 = orderAmountMapper.selectCount(queryWrapper);

                LambdaQueryWrapper<OrderPerson> orderPersonQuery = Wrappers.lambdaQuery(OrderPerson.class)
                        .eq(OrderPerson::getUserId, userId)
                        //类型(1 上分, 2 下分)
                        .eq(OrderPerson::getOrderType, 1)
                        .eq(OrderPerson::getCurrencyId, currencyId)
                        .gt(OrderPerson::getAmount, BigDecimal.ZERO)//充值金额大于0才算
                        .lt(OrderPerson::getCreateTime, order.getPayTime());
                Integer countNum2 = orderPersonService.count(orderPersonQuery);

                countNum = countNum1 + countNum2;
            } else {
                LambdaQueryWrapper<OrderPerson> orderPersonQuery = Wrappers.lambdaQuery(OrderPerson.class)
                        .eq(OrderPerson::getUserId, userId)
                        //类型(1 上分, 2 下分)
                        .eq(OrderPerson::getOrderType, 1)
                        .ne(OrderPerson::getId, order.getOrderId()) // id != 当前orderId
                        .eq(OrderPerson::getCurrencyId, currencyId)
                        .gt(OrderPerson::getAmount, BigDecimal.ZERO)//充值金额大于0才算
                        .lt(OrderPerson::getCreateTime, order.getPayTime());
                Integer countNum2 = orderPersonService.count(orderPersonQuery);

                //查询 当前订单之前 是否有其他支付过的订单
                LambdaQueryWrapper<OrderAmount> queryWrapper = Wrappers.lambdaQuery(OrderAmount.class)
                        .eq(OrderAmount::getUserId, userId)
                        .eq(OrderAmount::getCurrencyId, currencyId)
                        .eq(OrderAmount::getOrderStatus, OrderAmountOrderStatusConstants.PAYMENT_COMPLETED)
                        .lt(OrderAmount::getPayTime, order.getPayTime());
                Integer countNum1 = orderAmountMapper.selectCount(queryWrapper);

                countNum = countNum1 + countNum2;
            }
            if (countNum == 1) {  //说明之前有一笔充值订单, 二存只统计一次
                if (secondDataMap.containsKey(userId)) { //包含
                    Map<String, Object> data = secondDataMap.get(userId);
                    int count = Integer.parseInt(data.get("count").toString());
                    data.put("count", count + 1);
                    BigDecimal countAmount = new BigDecimal(data.get("countAmount").toString());
                    data.put("countAmount", countAmount.add(order.getAmount()));
                    secondDataMap.put(userId, data);
                } else { //不包含
                    Map<String, Object> data = new HashMap<>();
                    data.put("count", 1);
                    data.put("countAmount", order.getAmount());
                    secondDataMap.put(userId, data);
                }
            }

            if (countNum > 1) {
                if (restoreDataMap.containsKey(userId)) { //包含
                    Map<String, Object> data = restoreDataMap.get(userId);
                    int count = Integer.parseInt(data.get("count").toString());
                    data.put("count", count + 1);
                    BigDecimal countAmount = new BigDecimal(data.get("countAmount").toString());
                    data.put("countAmount", countAmount.add(order.getAmount()));
                    restoreDataMap.put(userId, data);
                } else { //不包含
                    Map<String, Object> data = new HashMap<>();
                    data.put("count", 1);
                    data.put("countAmount", order.getAmount());
                    restoreDataMap.put(userId, data);
                }
            }

        }
        log.info("uuid ={}, 首存数据, 渠道id={}, userAmountMap={}", uuid, channelId, JSON.toJSONString(userAmountMap));
        log.info("uuid ={}, 二存数据, 渠道id={}, secondDataMap={}", uuid, channelId, JSON.toJSONString(secondDataMap));
        log.info("uuid ={}, 复存数据, 渠道id={}, restoreDataMap={}", uuid, channelId, JSON.toJSONString(restoreDataMap));
        consumOrderVO.setUserAmountMap(userAmountMap);
        consumOrderVO.setSecondDataMap(secondDataMap);
        consumOrderVO.setRestoreDataMap(restoreDataMap);

        //1.首存  用户第一充值 金额 或 数量  当日
        // 统计首次充值用户的总金额
        BigDecimal firstAmount = consumOrderVO.getUserAmountMap().values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int firstNum = consumOrderVO.getUserAmountMap().size();
        channelCountOrder.setFirstAmount(firstAmount);//首存金额
        channelCountOrder.setFirstNum(firstNum);//首存
        //2.二存  第二次充值的 金额 或 数量 当日
        //统计第二次存款
        AtomicInteger secondNumAtomic = new AtomicInteger(0);
        AtomicReference<BigDecimal> secondAmountAtomic = new AtomicReference<>(BigDecimal.ZERO);
        consumOrderVO.getSecondDataMap().forEach((k, v) -> { //k -> 用户id; v -> map(count:二充的次数; countAmount:二充的金额)
            Object countObj = v.get("count");
            if (Objects.nonNull(countObj)) {
                int count = Integer.parseInt(String.valueOf(countObj));
                secondNumAtomic.addAndGet(count);
            }
            Object countAmountObj = v.get("countAmount");
            if (Objects.nonNull(countAmountObj)) {
                BigDecimal countAmount = new BigDecimal(String.valueOf(countAmountObj));
                secondAmountAtomic.updateAndGet(
                        y -> y.add(
                                Optional.of(countAmount).orElse(BigDecimal.ZERO)
                        )
                );
            }
        });
        int secondNum = secondNumAtomic.get();
        channelCountOrder.setSecondNum(secondNum);//二存,充值用户人数
        BigDecimal secondAmount = secondAmountAtomic.get();
        channelCountOrder.setSecondAmount(secondAmount);//二存,充值金额

        //2.复存的 金额 或 数量 当日
        //统计复存
        AtomicReference<BigDecimal> restoreAmountAtomic = new AtomicReference<>(BigDecimal.ZERO);
        consumOrderVO.getRestoreDataMap().forEach((k, v) -> { //k -> 用户id; v -> map(count:复存的次数; countAmount:复存的金额)
            Object countAmountObj = v.get("countAmount");
            if (Objects.nonNull(countAmountObj)) {
                BigDecimal countAmount = new BigDecimal(String.valueOf(countAmountObj));
                restoreAmountAtomic.updateAndGet(
                        y -> y.add(
                                Optional.of(countAmount).orElse(BigDecimal.ZERO)
                        )
                );
            }
        });
        int restoreNum = consumOrderVO.getRestoreDataMap().size();
        channelCountOrder.setRestoreNum(restoreNum);//复存,充值用户人数
        BigDecimal restoreAmount = restoreAmountAtomic.get();
        channelCountOrder.setRestoreAmount(restoreAmount);//复存,充值金额

        return consumOrderVO;
    }

    /**
     * 初始化数据
     *
     * @param countData
     */
    private void initCountData(Map<String, Object> countData) {
        //统计充值数据
        int currencyRechargeNum = 0;
        countData.put("currencyRechargeNum", currencyRechargeNum);
        //统计 充值金额
        BigDecimal currencyRechargeAmount = BigDecimal.ZERO;
        countData.put("currencyRechargeAmount", currencyRechargeAmount);
        //统计 彩金金额
        BigDecimal currencyBonusAmount = BigDecimal.ZERO;
        countData.put("currencyBonusAmount", currencyBonusAmount);
        //统计 pix彩金金额
        BigDecimal currencypixBonusAmount = BigDecimal.ZERO;
        countData.put("currencypixBonusAmount", currencypixBonusAmount);
        //统计 后台客服手动上分订单数量
        int currencyCustomerRechargeNum = 0;
        countData.put("currencyCustomerRechargeNum", currencyCustomerRechargeNum);
        //统计 后台客服手动上分金额
        BigDecimal currencyCustomerRechargeAmount = BigDecimal.ZERO;
        countData.put("currencyCustomerRechargeAmount", currencyCustomerRechargeAmount);
        //统计 后台客服手动上分彩金金额
        BigDecimal currencyCustomerBonusAmount = BigDecimal.ZERO;
        countData.put("currencyCustomerBonusAmount", currencyCustomerBonusAmount);
        //统计提现数据
        //提现订单数
        int currencyWithdrawNum = 0;
        countData.put("currencyWithdrawNum", currencyWithdrawNum);
        //统计 后台客服手动下分订单数量-下分类型是真实提现
        int currencyCustomerRealCashWithdrawalNum = 0;
        countData.put("currencyCustomerRealCashWithdrawalNum", currencyCustomerRealCashWithdrawalNum);
        //统计 法币订单数
        int currencyLawWithdrawNum = 0;
        countData.put("currencyLawWithdrawNum", currencyLawWithdrawNum);
        //统计 后台客服手动下分订单数量-下分类型是扣除积分
        int currencyCustomerPointsDeductedNum = 0;
        countData.put("currencyCustomerPointsDeductedNum", currencyCustomerPointsDeductedNum);
        //提现金额
        BigDecimal currencyWithdrawAmount = BigDecimal.ZERO;
        countData.put("currencyWithdrawAmount", currencyWithdrawAmount);
        //统计 后台客服手动下分金额-下分类型是真实提现
        BigDecimal currencyCustomerRealCashWithdrawalAmount = BigDecimal.ZERO;
        countData.put("currencyCustomerRealCashWithdrawalAmount", currencyCustomerRealCashWithdrawalAmount);
        //统计 法币提现金额
        BigDecimal currencyLawWithdrawAmount = BigDecimal.ZERO;
        countData.put("currencyLawWithdrawAmount", currencyLawWithdrawAmount);
        //统计 后台客服手动下分金额-下分类型是扣除积分
        BigDecimal currencyCustomerPointsDeductedAmount = BigDecimal.ZERO;
        countData.put("currencyCustomerPointsDeductedAmount", currencyCustomerPointsDeductedAmount);
        //手续费
        BigDecimal currencyFee = BigDecimal.ZERO;
        countData.put("currencyFee", currencyFee);
        //实际到账金额
        BigDecimal currencyRealAmount = BigDecimal.ZERO;
        countData.put("currencyRealAmount", currencyRealAmount);
        //统计 后台客服手动下分实际金额-下分类型是真实提现
        BigDecimal currencyCustomerRealCashWithdrawalRealAmount = BigDecimal.ZERO;
        countData.put("currencyCustomerRealCashWithdrawalRealAmount", currencyCustomerRealCashWithdrawalRealAmount);
        //统计用户结余(钱包余额)
        BigDecimal currencyUserAmount = BigDecimal.ZERO;
        countData.put("currencyUserAmount", currencyUserAmount);
        //游戏投注总数量
        Integer currencyBetNum = 0;
        countData.put("currencyBetNum", currencyBetNum);
        //游戏投注金额(不分输赢)
        BigDecimal currencyBetAmount = BigDecimal.ZERO;
        countData.put("currencyBetAmount", currencyBetAmount);
        //结算金额
        BigDecimal currencySettleAmount = BigDecimal.ZERO;
        countData.put("currencySettleAmount", currencySettleAmount);
        //有效投注金额(分输赢)
        BigDecimal currencyEfficientBetAmount = BigDecimal.ZERO;
        countData.put("currencyEfficientBetAmount", currencyEfficientBetAmount);
        //输赢金额(返奖额减去投注额)-当日
        BigDecimal currencyWinLoseAmount = BigDecimal.ZERO;
        countData.put("currencyWinLoseAmount", currencyWinLoseAmount);
        //待返水金额
        BigDecimal currencyWaitRebateAmount = BigDecimal.ZERO;
        countData.put("currencyWaitRebateAmount", currencyWaitRebateAmount);
        //待返佣金额-下级用户返水返给自己的金额
        BigDecimal currencyWaitReturnCommissionAmount = BigDecimal.ZERO;
        countData.put("currencyWaitReturnCommissionAmount", currencyWaitReturnCommissionAmount);
        //已返水金额
        BigDecimal currencyAlreadyRebateAmount = BigDecimal.ZERO;
        countData.put("currencyAlreadyRebateAmount", currencyAlreadyRebateAmount);
        //已返佣金额
        BigDecimal currencyAlreadyReturnCommissionAmount = BigDecimal.ZERO;
        countData.put("currencyAlreadyReturnCommissionAmount", currencyAlreadyReturnCommissionAmount);
        //转盘活动奖励金额
        BigDecimal currencyPlayWheelTermAward = BigDecimal.ZERO;
        countData.put("currencyPlayWheelTermAward", currencyPlayWheelTermAward);
        //参入转盘活动次数
        Integer currencyPlayWheelTermNum = 0;
        countData.put("currencyPlayWheelTermNum", currencyPlayWheelTermNum);
        //统计 用户活动奖励领取奖励-彩金金额
        BigDecimal currencyActivityAwardBonusAmount = BigDecimal.ZERO;
        countData.put("currencyActivityAwardBonusAmount", currencyActivityAwardBonusAmount);
        //统计 用户活动奖励领取次数-奖励彩金
        int currencyReceiveBonusNum = 0;
        countData.put("currencyReceiveBonusNum", currencyReceiveBonusNum);
        //统计 用户活动奖励领取奖励-转盘总次数
        Integer currencyActivityAwardTotalNum = 0;
        countData.put("currencyActivityAwardTotalNum", currencyActivityAwardTotalNum);
        //统计 用户活动奖励领取次数-奖励转盘次数
        Integer currencyReceiveTurntableNum = 0;
        countData.put("currencyReceiveTurntableNum", currencyReceiveTurntableNum);
        //统计 用户收入
        BigDecimal currencyUserEarnings = BigDecimal.ZERO;
        countData.put("currencyUserEarnings", currencyUserEarnings);
        //统计 用户支出
        BigDecimal currencyUserExpenses = BigDecimal.ZERO;
        countData.put("currencyUserExpenses", currencyUserExpenses);
        //统计 客损
        BigDecimal currencyCustomerLossAmount = BigDecimal.ZERO;
        countData.put("currencyCustomerLossAmount", currencyCustomerLossAmount);

        //区块链直冲数量
        int currencyBlockchainRechargeNum = 0;
        countData.put("currencyBlockchainRechargeNum", currencyBlockchainRechargeNum);
        //区块链直冲金额
        BigDecimal currencyBlockchainRechargeAmount = BigDecimal.ZERO;
        countData.put("currencyBlockchainRechargeAmount", currencyBlockchainRechargeAmount);
        //MPAY充值充值数量
        int currencyMpayRechargeNum = 0;
        countData.put("currencyMpayRechargeNum", currencyMpayRechargeNum);
        //MPAY充值金额
        BigDecimal currencyMpayRechargeAmount = BigDecimal.ZERO;
        countData.put("currencyMpayRechargeAmount", currencyMpayRechargeAmount);

        /** pix充值金额 */
        BigDecimal currencyPixpayRechargeAmount = BigDecimal.ZERO;
        countData.put("currencyPixpayRechargeAmount", currencyPixpayRechargeAmount);
        /** pix充值数量 */
        int currencyPixpayRechargeNum = 0;
        countData.put("currencyPixpayRechargeNum", currencyPixpayRechargeNum);

        BigDecimal currencyCodeAmount = BigDecimal.ZERO;
        countData.put("currencyCodeAmount", currencyCodeAmount);

    }

    /**
     * 初始化
     *
     * @param day
     * @param channelId
     * @param currency
     * @return
     */
    private ChannelCountOrder initChannelCountOrder(String day, Long channelId, Currency currency) {
        ChannelCountOrder channelCountOrder = new ChannelCountOrder();
        channelCountOrder.setDayStr(day);
        channelCountOrder.setCurrencyId(currency.getId());
        channelCountOrder.setItemId(currency.getItemId());
        channelCountOrder.setChainTag(currency.getChainTag());
        channelCountOrder.setChannelId(channelId);
        //统计充值数据
        channelCountOrder.setRechargeNum(0);
        channelCountOrder.setRechargeAmount(BigDecimal.ZERO);
        //统计提现数据
        channelCountOrder.setWithdrawNum(0);
        channelCountOrder.setWithdrawAmount(BigDecimal.ZERO);
        channelCountOrder.setFee(BigDecimal.ZERO);
        channelCountOrder.setRealAmount(BigDecimal.ZERO);
        //游戏投注总数量
        channelCountOrder.setBetNum(0);
        //游戏投注总金额(不分输赢)
        channelCountOrder.setBetAmount(BigDecimal.ZERO);
        //结算总金额
        channelCountOrder.setSettleAmount(BigDecimal.ZERO);
        //有效投注总金额
        channelCountOrder.setEfficientBetAmount(BigDecimal.ZERO);
        //输赢总金额
        channelCountOrder.setWinLoseAmount(BigDecimal.ZERO);
        //待返水总金额
        channelCountOrder.setWaitRebateAmount(BigDecimal.ZERO);
        //待返佣总金额-下级用户返水返给自己的金额
        channelCountOrder.setWaitReturnCommissionAmount(BigDecimal.ZERO);
        //已返水总金额
        channelCountOrder.setAlreadyRebateAmount(BigDecimal.ZERO);
        //已返佣总金额
        channelCountOrder.setAlreadyReturnCommissionAmount(BigDecimal.ZERO);
        //转盘活动奖励总金额
        channelCountOrder.setPlayWheelTermAward(BigDecimal.ZERO);
        //参入转盘活动总次数
        channelCountOrder.setPlayWheelTermNum(0);
        //彩金金额
        channelCountOrder.setBonusAmount(BigDecimal.ZERO);
        //pix彩金金额
        channelCountOrder.setPixBonusAmount(BigDecimal.ZERO);
        //后台客服手动上分金额
        channelCountOrder.setCustomerRechargeAmount(BigDecimal.ZERO);
        //后台客服手动上分彩金金额
        channelCountOrder.setCustomerBonusAmount(BigDecimal.ZERO);
        //后台客服手动上分订单数量
        channelCountOrder.setCustomerRechargeNum(0);
        //后台客服手动下分金额-下分类型是真实提现
        channelCountOrder.setCustomerRealCashWithdrawalAmount(BigDecimal.ZERO);
        //后台客服手动下分订单数量-下分类型是真实提现
        channelCountOrder.setCustomerRealCashWithdrawalNum(0);
        //后台客服手动下分实际金额-下分类型是真实提现
        channelCountOrder.setCustomerRealCashWithdrawalRealAmount(BigDecimal.ZERO);
        //后台客服手动下分金额-下分类型是扣除积分
        channelCountOrder.setCustomerPointsDeductedAmount(BigDecimal.ZERO);
        //后台客服手动下分订单数量-下分类型是扣除积分
        channelCountOrder.setCustomerPointsDeductedNum(0);
        //法币提现订单数量
        channelCountOrder.setLawWithdrawNum(0);
        //法币提现金额
        channelCountOrder.setLawWithdrawAmount(BigDecimal.ZERO);
        //用户活动奖励领取奖励-彩金金额
        channelCountOrder.setActivityAwardBonusAmount(BigDecimal.ZERO);
        //用户活动奖励领取次数-奖励彩金
        channelCountOrder.setReceiveBonusNum(0);
        //用户活动奖励领取奖励-转盘总次数
        channelCountOrder.setActivityAwardTotalNum(0);
        //用户活动奖励领取次数-奖励转盘次数
        channelCountOrder.setReceiveTurntableNum(0);
        //用户收入
        channelCountOrder.setUserEarnings(BigDecimal.ZERO);
        //用户支出
        channelCountOrder.setUserExpenses(BigDecimal.ZERO);
        //客损[计算公式 等于 输赢 加 所有送的金额 减 手动下分(扣除积分)]
        channelCountOrder.setCustomerLossAmount(BigDecimal.ZERO);
        //统计用户结余(钱包余额)
        channelCountOrder.setUserAmount(BigDecimal.ZERO);

        //区块链直冲数量
        channelCountOrder.setBlockchainRechargeNum(0);
        //区块链直冲金额
        channelCountOrder.setBlockchainRechargeAmount(BigDecimal.ZERO);
        //MPAY充值充值数量
        channelCountOrder.setMpayRechargeNum(0);
        //MPAY充值金额
        channelCountOrder.setMpayRechargeAmount(BigDecimal.ZERO);
        /** pix充值金额 */
        channelCountOrder.setPixpayRechargeAmount(BigDecimal.ZERO);
        /** pix充值数量 */
        channelCountOrder.setPixpayRechargeNum(0);

        //3.新增  人数
        //统计用户新增
        channelCountOrder.setUserNum(0);
        channelCountOrder.setFirstAmount(BigDecimal.ZERO);
        channelCountOrder.setFirstNum(0);
        //2.二存以上  第二次充值以上的 金额 或 数量 当日
        //统计第二次(包含)存款 以上
        channelCountOrder.setSecondNum(0);
        channelCountOrder.setSecondAmount(BigDecimal.ZERO);
        channelCountOrder.setThreeRemain(BigDecimal.ZERO);
        channelCountOrder.setThreeRemainNum(0);
        channelCountOrder.setSevenRemain(BigDecimal.ZERO);
        channelCountOrder.setSevenRemainNum(0);
        channelCountOrder.setCodeAmount(BigDecimal.ZERO);
        return channelCountOrder;
    }

    /**
     * 处理用户每日统计数据
     *
     * @param day
     * @param user
     * @param currency
     * @param startTime
     * @param endTime
     * @param countData
     */
    private void handleUserCountOrderData(
            String day, TUser user, Currency currency
            , String startTime, String endTime, Map<String, Object> countData
            , Map<Long, ChannelCountOrder> channelCountOrderMap
    ) {
        Long userId = user.getUserId();

        UserCountOrder userCountOrder = new UserCountOrder();
        userCountOrder.setDayStr(day);
        userCountOrder.setCurrencyId(currency.getId());
        userCountOrder.setItemId(currency.getItemId());
        userCountOrder.setChainTag(currency.getChainTag());
        userCountOrder.setUserId(userId);
        userCountOrder.setSuperUserId(user.getSuperUserId());
        userCountOrder.setSuperUserTgId(user.getSuperUserTgId());
        Long channelId = user.getChannelId();
        userCountOrder.setChannelId(channelId);

        ChannelCountOrder channelCountOrder = channelCountOrderMap.get(channelId);
        if (Objects.isNull(channelCountOrder)) {
            channelCountOrder = this.initChannelCountOrder(day, channelId, currency);
        }

        LambdaQueryWrapper<OrderAmount> orderAmountQuery = Wrappers.lambdaQuery(OrderAmount.class);
        orderAmountQuery.between(OrderAmount::getPayTime, startTime, endTime);
        orderAmountQuery.eq(OrderAmount::getCurrencyId, currency.getId());
        orderAmountQuery.eq(OrderAmount::getOrderStatus, 1);
        orderAmountQuery.eq(OrderAmount::getUserId, userId);
        List<OrderAmount> orderAmountList = orderAmountService.list(orderAmountQuery);

        List<OrderAmount> blockchainOrderAmountList =
                orderAmountList.stream().filter(it -> new Integer("0").equals(it.getType())).collect(Collectors.toList());

        //区块链直冲数量
        int blockchainRechargeNum = blockchainOrderAmountList.size();
        userCountOrder.setBlockchainRechargeNum(blockchainRechargeNum);
        channelCountOrder.setBlockchainRechargeNum(channelCountOrder.getBlockchainRechargeNum() + blockchainRechargeNum);
        int currencyBlockchainRechargeNum = Integer.parseInt(countData.get("currencyBlockchainRechargeNum").toString());
        currencyBlockchainRechargeNum += blockchainRechargeNum;
        countData.put("currencyBlockchainRechargeNum", currencyBlockchainRechargeNum);

        //区块链直冲金额
        BigDecimal blockchainRechargeAmount = blockchainOrderAmountList.stream()
                .map(OrderAmount::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setBlockchainRechargeAmount(blockchainRechargeAmount);
        channelCountOrder.setBlockchainRechargeAmount(channelCountOrder.getBlockchainRechargeAmount().add(blockchainRechargeAmount));
        BigDecimal currencyBlockchainRechargeAmount = new BigDecimal(countData.get("currencyBlockchainRechargeAmount").toString());
        currencyBlockchainRechargeAmount = currencyBlockchainRechargeAmount.add(blockchainRechargeAmount);
        countData.put("currencyBlockchainRechargeAmount", currencyBlockchainRechargeAmount);

        List<OrderAmount> mpayOrderAmountList =
                orderAmountList.stream().filter(it -> new Integer("1").equals(it.getType())).collect(Collectors.toList());

        //MPAY充值充值数量
        int mpayRechargeNum = mpayOrderAmountList.size();
        userCountOrder.setMpayRechargeNum(mpayRechargeNum);
        channelCountOrder.setMpayRechargeNum(channelCountOrder.getMpayRechargeNum() + mpayRechargeNum);
        int currencyMpayRechargeNum = Integer.parseInt(countData.get("currencyMpayRechargeNum").toString());
        currencyMpayRechargeNum += mpayRechargeNum;
        countData.put("currencyMpayRechargeNum", currencyMpayRechargeNum);

        //MPAY充值金额
        BigDecimal mpayRechargeAmount = mpayOrderAmountList.stream()
                .map(OrderAmount::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setMpayRechargeAmount(mpayRechargeAmount);
        channelCountOrder.setMpayRechargeAmount(channelCountOrder.getMpayRechargeAmount().add(mpayRechargeAmount));
        BigDecimal currencyMpayRechargeAmount = new BigDecimal(countData.get("currencyMpayRechargeAmount").toString());
        currencyMpayRechargeAmount = currencyMpayRechargeAmount.add(mpayRechargeAmount);
        countData.put("currencyMpayRechargeAmount", currencyMpayRechargeAmount);

        List<OrderAmount> pixpayOrderAmountList =
                orderAmountList.stream().filter(it -> new Integer("2").equals(it.getType())).collect(Collectors.toList());

        //pix充值数量
        int pixRechargeNum = pixpayOrderAmountList.size();
        userCountOrder.setPixpayRechargeNum(pixRechargeNum);
        channelCountOrder.setPixpayRechargeNum(channelCountOrder.getPixpayRechargeNum() + pixRechargeNum);
        int currencyPixpayRechargeNum = Integer.parseInt(countData.get("currencyPixpayRechargeNum").toString());
        currencyPixpayRechargeNum += pixRechargeNum;
        countData.put("currencyPixpayRechargeNum", currencyPixpayRechargeNum);

        //pix充值金额
        BigDecimal pixRechargeAmount = pixpayOrderAmountList.stream()
                .map(OrderAmount::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setPixpayRechargeAmount(pixRechargeAmount);
        channelCountOrder.setPixpayRechargeAmount(channelCountOrder.getPixpayRechargeAmount().add(pixRechargeAmount));
        BigDecimal currencyPixpayRechargeAmount = new BigDecimal(countData.get("currencyPixpayRechargeAmount").toString());
        currencyPixpayRechargeAmount = currencyPixpayRechargeAmount.add(pixRechargeAmount);
        countData.put("currencyPixpayRechargeAmount", currencyPixpayRechargeAmount);

        //用户充值订单数
        int rechargeNum = orderAmountList.size();
        userCountOrder.setRechargeNum(rechargeNum);
        channelCountOrder.setRechargeNum(channelCountOrder.getRechargeNum() + rechargeNum);
        int currencyRechargeNum = Integer.parseInt(countData.get("currencyRechargeNum").toString());
        currencyRechargeNum += rechargeNum;
        countData.put("currencyRechargeNum", currencyRechargeNum);

        //充值金额
        BigDecimal rechargeAmount = orderAmountList.stream()
                .map(OrderAmount::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setRechargeAmount(rechargeAmount);
        channelCountOrder.setRechargeAmount(channelCountOrder.getRechargeAmount().add(rechargeAmount));
        BigDecimal currencyRechargeAmount = new BigDecimal(countData.get("currencyRechargeAmount").toString());
        currencyRechargeAmount = currencyRechargeAmount.add(rechargeAmount);
        countData.put("currencyRechargeAmount", currencyRechargeAmount);

        //彩金金额 -> 彩金金额只有 mpay 充值才有
        BigDecimal bonusAmount = mpayOrderAmountList.stream()
                .map(OrderAmount::getBonusAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setBonusAmount(bonusAmount);
        channelCountOrder.setBonusAmount(channelCountOrder.getBonusAmount().add(bonusAmount));
        BigDecimal currencyBonusAmount = new BigDecimal(countData.get("currencyBonusAmount").toString());
        currencyBonusAmount = currencyBonusAmount.add(bonusAmount);
        countData.put("currencyBonusAmount", currencyBonusAmount);

        //pix彩金金额
        BigDecimal pixbonusAmount = pixpayOrderAmountList.stream()
                .map(OrderAmount::getBonusAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setPixBonusAmount(pixbonusAmount);
        channelCountOrder.setPixBonusAmount(channelCountOrder.getPixBonusAmount().add(pixbonusAmount));
        BigDecimal currencypixBonusAmount = new BigDecimal(countData.get("currencypixBonusAmount").toString());
        currencypixBonusAmount = currencypixBonusAmount.add(pixbonusAmount);
        countData.put("currencypixBonusAmount", currencypixBonusAmount);

        LambdaQueryWrapper<OrderPerson> orderPersonQuery = Wrappers.lambdaQuery(OrderPerson.class);
        orderPersonQuery.between(OrderPerson::getCreateTime, startTime, endTime);
        orderPersonQuery.eq(OrderPerson::getUserId, userId);
        orderPersonQuery.eq(OrderPerson::getCurrencyId, currency.getId());
        List<OrderPerson> orderPersonList = orderPersonService.list(orderPersonQuery);
        //orderType 类型(1 上分, 2 下分)
        List<OrderPerson> upOrderPersonList = orderPersonList.stream()
                .filter(it -> it.getOrderType() == 1).collect(Collectors.toList());

        //后台客服手动上分订单数量
        int customerRechargeNum =
                (int) upOrderPersonList.stream().filter(it -> it.getAmount().compareTo(BigDecimal.ZERO) > 0).count();
        userCountOrder.setCustomerRechargeNum(customerRechargeNum);
        channelCountOrder.setCustomerRechargeNum(channelCountOrder.getCustomerRechargeNum() + customerRechargeNum);
        int currencyCustomerRechargeNum = Integer.parseInt(countData.get("currencyCustomerRechargeNum").toString());
        currencyCustomerRechargeNum += customerRechargeNum;
        countData.put("currencyCustomerRechargeNum", currencyCustomerRechargeNum);

        //后台客服手动上分金额
        BigDecimal customerRechargeAmount = upOrderPersonList.stream()
                .map(OrderPerson::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setCustomerRechargeAmount(customerRechargeAmount);
        channelCountOrder.setCustomerRechargeAmount(channelCountOrder.getCustomerRechargeAmount().add(customerRechargeAmount));
        BigDecimal currencyCustomerRechargeAmount = new BigDecimal(countData.get("currencyCustomerRechargeAmount").toString());
        currencyCustomerRechargeAmount = currencyCustomerRechargeAmount.add(customerRechargeAmount);
        countData.put("currencyCustomerRechargeAmount", currencyCustomerRechargeAmount);

        //后台客服手动上分彩金金额
        BigDecimal customerBonusAmount = upOrderPersonList.stream()
                .map(OrderPerson::getBonusAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setCustomerBonusAmount(customerBonusAmount);
        channelCountOrder.setCustomerBonusAmount(channelCountOrder.getCustomerBonusAmount().add(customerBonusAmount));
        BigDecimal currencyCustomerBonusAmount = new BigDecimal(countData.get("currencyCustomerBonusAmount").toString());
        currencyCustomerBonusAmount = currencyCustomerBonusAmount.add(customerBonusAmount);
        countData.put("currencyCustomerBonusAmount", currencyCustomerBonusAmount);

        //统计提现数据
        Map<String, Object> withdrawStats = orderWithdrawMapper.countWithdraw(startTime, endTime, currency.getId(), userId, 4);

        //orderType 类型(1 上分, 2 下分) ; lowerSubtype 下分类型(1.真实提现;2.扣除积分)
        List<OrderPerson> realCashDownOrderPersonList = orderPersonList.stream()
                .filter(it -> it.getOrderType() == 2 && it.getLowerSubtype() == 1)
                .collect(Collectors.toList());

        //orderType 类型(1 上分, 2 下分) ; lowerSubtype 下分类型(1.真实提现;2.扣除积分)
        List<OrderPerson> pointsDeductedDownOrderPersonList = orderPersonList.stream()
                .filter(it -> it.getOrderType() == 2 && it.getLowerSubtype() == 2)
                .collect(Collectors.toList());

        //统计法币订单数
        LambdaQueryWrapper<OrderLawWithdraw> orderLawWithdrawQuery = Wrappers.lambdaQuery(OrderLawWithdraw.class);
        orderLawWithdrawQuery.between(OrderLawWithdraw::getCreateTime, startTime, endTime);
        orderLawWithdrawQuery.eq(OrderLawWithdraw::getUserId, userId);
        orderLawWithdrawQuery.eq(OrderLawWithdraw::getCurrencyId, currency.getId());
        List<OrderLawWithdraw> orderLawWithdrawList = orderLawWithdrawService.list(orderLawWithdrawQuery);

        //提现订单数
        int withdrawNum = ((Long) withdrawStats.get("withdrawCount")).intValue();
        userCountOrder.setWithdrawNum(withdrawNum);
        channelCountOrder.setWithdrawNum(channelCountOrder.getWithdrawNum() + withdrawNum);
        int currencyWithdrawNum = Integer.parseInt(countData.get("currencyWithdrawNum").toString());
        currencyWithdrawNum += withdrawNum;
        countData.put("currencyWithdrawNum", currencyWithdrawNum);

        //后台客服手动下分订单数量-下分类型是真实提现
        int customerRealCashWithdrawalNum = realCashDownOrderPersonList.size();
        userCountOrder.setCustomerRealCashWithdrawalNum(customerRealCashWithdrawalNum);
        channelCountOrder.setCustomerRealCashWithdrawalNum(channelCountOrder.getCustomerRealCashWithdrawalNum() + customerRealCashWithdrawalNum);
        int currencyCustomerRealCashWithdrawalNum = Integer.parseInt(countData.get("currencyCustomerRealCashWithdrawalNum").toString());
        currencyCustomerRealCashWithdrawalNum += customerRealCashWithdrawalNum;
        countData.put("currencyCustomerRealCashWithdrawalNum", currencyCustomerRealCashWithdrawalNum);

        //后台客服手动下分订单数量-下分类型是扣除积分
        int customerPointsDeductedNum = pointsDeductedDownOrderPersonList.size();
        userCountOrder.setCustomerPointsDeductedNum(customerPointsDeductedNum);
        channelCountOrder.setCustomerPointsDeductedNum(channelCountOrder.getCustomerPointsDeductedNum() + customerPointsDeductedNum);
        int currencyCustomerPointsDeductedNum = Integer.parseInt(countData.get("currencyCustomerPointsDeductedNum").toString());
        currencyCustomerPointsDeductedNum += customerPointsDeductedNum;
        countData.put("currencyCustomerPointsDeductedNum", currencyCustomerPointsDeductedNum);

        //法币提现订单数量
        int lawWithdrawNum = orderLawWithdrawList.size();
        userCountOrder.setLawWithdrawNum(lawWithdrawNum);
        channelCountOrder.setLawWithdrawNum(channelCountOrder.getLawWithdrawNum() + lawWithdrawNum);
        int currencyLawWithdrawNum = Integer.parseInt(countData.get("currencyLawWithdrawNum").toString());
        currencyLawWithdrawNum += lawWithdrawNum;
        countData.put("currencyLawWithdrawNum", currencyLawWithdrawNum);

        //提现金额
        BigDecimal withdrawAmount = new BigDecimal(withdrawStats.get("amount").toString());
        userCountOrder.setWithdrawAmount(withdrawAmount);
        channelCountOrder.setWithdrawAmount(channelCountOrder.getWithdrawAmount().add(withdrawAmount));
        BigDecimal currencyWithdrawAmount = new BigDecimal(countData.get("currencyWithdrawAmount").toString());
        currencyWithdrawAmount = currencyWithdrawAmount.add(withdrawAmount);
        countData.put("currencyWithdrawAmount", currencyWithdrawAmount);

        //后台客服手动下分金额-下分类型是真实提现
        BigDecimal customerRealCashWithdrawalAmount = realCashDownOrderPersonList.stream()
                .map(OrderPerson::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setCustomerRealCashWithdrawalAmount(customerRealCashWithdrawalAmount);
        channelCountOrder.setCustomerRealCashWithdrawalAmount(channelCountOrder.getCustomerRealCashWithdrawalAmount().add(customerRealCashWithdrawalAmount));
        BigDecimal currencyCustomerRealCashWithdrawalAmount = new BigDecimal(countData.get("currencyCustomerRealCashWithdrawalAmount").toString());
        currencyCustomerRealCashWithdrawalAmount = currencyCustomerRealCashWithdrawalAmount.add(customerRealCashWithdrawalAmount);
        countData.put("currencyCustomerRealCashWithdrawalAmount", currencyCustomerRealCashWithdrawalAmount);

        //法币提现金额
        BigDecimal currencyLawWithdrawAmounts = orderLawWithdrawList.stream()
                .map(OrderLawWithdraw::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setLawWithdrawAmount(currencyLawWithdrawAmounts);
        channelCountOrder.setLawWithdrawAmount(channelCountOrder.getLawWithdrawAmount().add(currencyLawWithdrawAmounts));
        BigDecimal currencyLawWithdrawAmount = new BigDecimal(countData.get("currencyLawWithdrawAmount").toString());
        currencyLawWithdrawAmount = currencyLawWithdrawAmount.add(currencyLawWithdrawAmounts);
        countData.put("currencyLawWithdrawAmount", currencyLawWithdrawAmount);

        //后台客服手动下分金额-下分类型是扣除积分
        BigDecimal customerPointsDeductedAmount = pointsDeductedDownOrderPersonList.stream()
                .map(OrderPerson::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setCustomerPointsDeductedAmount(customerPointsDeductedAmount);
        channelCountOrder.setCustomerPointsDeductedAmount(channelCountOrder.getCustomerPointsDeductedAmount().add(customerPointsDeductedAmount));
        BigDecimal currencyCustomerPointsDeductedAmount = new BigDecimal(countData.get("currencyCustomerPointsDeductedAmount").toString());
        currencyCustomerPointsDeductedAmount = currencyCustomerPointsDeductedAmount.add(customerPointsDeductedAmount);
        countData.put("currencyCustomerPointsDeductedAmount", currencyCustomerPointsDeductedAmount);

        //手续费
        BigDecimal fee = new BigDecimal(withdrawStats.get("fee").toString());
        userCountOrder.setFee(fee);
        channelCountOrder.setFee(channelCountOrder.getFee().add(fee));
        BigDecimal currencyFee = new BigDecimal(countData.get("currencyFee").toString());
        currencyFee = currencyFee.add(fee);
        countData.put("currencyFee", currencyFee);

        //实际到账金额
        BigDecimal realAmount = new BigDecimal(withdrawStats.get("realAmount").toString());
        userCountOrder.setRealAmount(realAmount);
        channelCountOrder.setRealAmount(channelCountOrder.getRealAmount().add(realAmount));
        BigDecimal currencyRealAmount = new BigDecimal(countData.get("currencyRealAmount").toString());
        currencyRealAmount = currencyRealAmount.add(realAmount);
        countData.put("currencyRealAmount", currencyRealAmount);

        //后台客服手动下分实际金额-下分类型是真实提现
        userCountOrder.setCustomerRealCashWithdrawalRealAmount(customerRealCashWithdrawalAmount);
        channelCountOrder.setCustomerRealCashWithdrawalRealAmount(channelCountOrder.getCustomerRealCashWithdrawalRealAmount().add(customerRealCashWithdrawalAmount));
        BigDecimal currencyCustomerRealCashWithdrawalRealAmount =
                new BigDecimal(countData.get("currencyCustomerRealCashWithdrawalRealAmount").toString());
        currencyCustomerRealCashWithdrawalRealAmount = currencyCustomerRealCashWithdrawalRealAmount.add(customerRealCashWithdrawalAmount);
        countData.put("currencyCustomerRealCashWithdrawalRealAmount", currencyCustomerRealCashWithdrawalRealAmount);

        //用户结余
        BigDecimal userAmount = userWalletMapper.countUserAmount(currency.getId(), userId);
        userCountOrder.setUserAmount(userAmount);
        channelCountOrder.setUserAmount(channelCountOrder.getUserAmount().add(userAmount));
        BigDecimal currencyUserAmount = new BigDecimal(countData.get("currencyUserAmount").toString());
        currencyUserAmount = currencyUserAmount.add(userAmount);
        countData.put("currencyUserAmount", currencyUserAmount);

        //今日投注数据
        LambdaQueryWrapper<OrderTerm> betOrderTermQuery = Wrappers.lambdaQuery(OrderTerm.class);
        betOrderTermQuery.between(OrderTerm::getCreateTime, startTime, endTime);  //此处使用的是创建时间
        betOrderTermQuery.eq(OrderTerm::getUserId, userId);
        betOrderTermQuery.eq(OrderTerm::getCurrencyId, currency.getId());
        List<OrderTerm> betOrderTermList = orderTermService.list(betOrderTermQuery);
        Integer betNum = betOrderTermList.size();
        //游戏投注数量(不分输赢)
        userCountOrder.setBetNum(betNum);
        channelCountOrder.setBetNum(channelCountOrder.getBetNum() + betNum);
        int currencyBetNum = Integer.parseInt(countData.get("currencyBetNum").toString());
        currencyBetNum += betNum;
        countData.put("currencyBetNum", currencyBetNum);

        BigDecimal betAmount = betOrderTermList.stream().map(OrderTerm::getBetAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        //用户游戏投注金额(不分输赢)-当日
        userCountOrder.setBetAmount(betAmount);
        channelCountOrder.setBetAmount(channelCountOrder.getBetAmount().add(betAmount));

        BigDecimal currencyBetAmount = new BigDecimal(countData.get("currencyBetAmount").toString());
        currencyBetAmount = currencyBetAmount.add(betAmount);
        countData.put("currencyBetAmount", currencyBetAmount);

        //今日结算投注数据
        LambdaQueryWrapper<OrderTerm> settleOrderTermQuery = Wrappers.lambdaQuery(OrderTerm.class);
        settleOrderTermQuery.between(OrderTerm::getSettleTime, startTime, endTime);  //此处使用的是结算时间
        settleOrderTermQuery.eq(OrderTerm::getUserId, userId);
        settleOrderTermQuery.eq(OrderTerm::getCurrencyId, currency.getId());
        List<OrderTerm> settleOrderTermList = orderTermService.list(settleOrderTermQuery);

        //  `win` decimal(16,4) DEFAULT NULL COMMENT '返奖(本金+奖金)',
        //结算金额(返奖金额)
        BigDecimal settleWinAmount = settleOrderTermList.stream()
                .map(OrderTerm::getWin)
                .filter(win -> Objects.nonNull(win))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setSettleAmount(settleWinAmount);
        channelCountOrder.setSettleAmount(channelCountOrder.getSettleAmount().add(settleWinAmount));

        BigDecimal currencySettleAmount = new BigDecimal(countData.get("currencySettleAmount").toString());
        currencySettleAmount = currencySettleAmount.add(settleWinAmount);
        countData.put("currencySettleAmount", currencySettleAmount);

        //有效投注金额(分输赢)-当日
        BigDecimal settleBetAmount = settleOrderTermList.stream().map(OrderTerm::getBetAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setEfficientBetAmount(settleBetAmount);
        channelCountOrder.setEfficientBetAmount(channelCountOrder.getEfficientBetAmount().add(settleBetAmount));
        BigDecimal currencyEfficientBetAmount = new BigDecimal(countData.get("currencyEfficientBetAmount").toString());
        currencyEfficientBetAmount = currencyEfficientBetAmount.add(settleBetAmount);
        countData.put("currencyEfficientBetAmount", currencyEfficientBetAmount);

        //输赢金额(返奖额减去投注额)-当日
        userCountOrder.setWinLoseAmount(settleWinAmount.subtract(settleBetAmount));
        channelCountOrder.setWinLoseAmount(channelCountOrder.getWinLoseAmount().add(settleWinAmount.subtract(settleBetAmount)));
        BigDecimal currencyWinLoseAmount = new BigDecimal(countData.get("currencyWinLoseAmount").toString());
        currencyWinLoseAmount = currencyWinLoseAmount.add(userCountOrder.getWinLoseAmount());
        countData.put("currencyWinLoseAmount", currencyWinLoseAmount);

        //`code_amount` decimal(16,4) DEFAULT NULL COMMENT '打码量(真实投注额)',
        //打码量  code_amount
        BigDecimal codeAmount = settleOrderTermList.stream()
                .map(OrderTerm::getCodeAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setCodeAmount(codeAmount);
        channelCountOrder.setCodeAmount(channelCountOrder.getCodeAmount().add(codeAmount));
        BigDecimal currencyCodeAmount = new BigDecimal(countData.get("currencyCodeAmount").toString());
        currencyCodeAmount = currencyCodeAmount.add(codeAmount);
        countData.put("currencyCodeAmount", currencyCodeAmount);

        //用户账变表
        LambdaQueryWrapper<UserExtChange> userExtChangeQuery = Wrappers.lambdaQuery(UserExtChange.class);
        userExtChangeQuery.between(UserExtChange::getCreateTime, startTime, endTime);
        userExtChangeQuery.eq(UserExtChange::getUserId, userId);
        //userExtChangeQuery.eq(UserExtChange::getCurrencyId, currency.getId());
        List<UserExtChange> userExtChangeList = userExtChangeService.list(userExtChangeQuery);

        //待返水金额 - 本人反水
        BigDecimal waitRebateAmount = userExtChangeList.stream()
                .filter(it -> Objects.equals(it.getExtType(), UserExtTypeCons.未领取反水))
                .filter(it -> Objects.equals(it.getOrderType(), BaseGameInfoCons.UserExtOrderType.注单))
                .filter(it -> Objects.equals(it.getType(), BaseGameInfoCons.UserExtChangeType.用户返水))
                .filter(it -> it.getAccountType() == AccountChangeTypeConstants.INCOME)
                .map(UserExtChange::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setWaitRebateAmount(waitRebateAmount);
        channelCountOrder.setWaitRebateAmount(channelCountOrder.getWaitRebateAmount().add(waitRebateAmount));
        BigDecimal currencyWaitRebateAmount = new BigDecimal(countData.get("currencyWaitRebateAmount").toString());
        currencyWaitRebateAmount = currencyWaitRebateAmount.add(waitRebateAmount);
        countData.put("currencyWaitRebateAmount", currencyWaitRebateAmount);

        //待返佣金额-下级用户返水返给自己的金额
        BigDecimal waitReturnCommissionAmount = userExtChangeList.stream()
                .filter(it -> Objects.equals(it.getExtType(), UserExtTypeCons.未领取返佣))
                .filter(it -> Objects.equals(it.getOrderType(), BaseGameInfoCons.UserExtOrderType.注单))
                .filter(it -> Objects.equals(it.getType(), BaseGameInfoCons.UserExtChangeType.上级返佣))
                .filter(it -> it.getAccountType() == AccountChangeTypeConstants.INCOME)
                .map(UserExtChange::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setWaitReturnCommissionAmount(waitReturnCommissionAmount);
        channelCountOrder.setWaitReturnCommissionAmount(channelCountOrder.getWaitReturnCommissionAmount().add(waitReturnCommissionAmount));
        BigDecimal currencyWaitReturnCommissionAmount = new BigDecimal(countData.get("currencyWaitReturnCommissionAmount").toString());
        currencyWaitReturnCommissionAmount = currencyWaitReturnCommissionAmount.add(waitReturnCommissionAmount);
        countData.put("currencyWaitReturnCommissionAmount", currencyWaitReturnCommissionAmount);

        //已返水金额
        BigDecimal alreadyRebateAmount = userExtChangeList.stream()
                .filter(it -> Objects.equals(it.getExtType(), UserExtTypeCons.未领取反水))
                .filter(it -> Objects.equals(it.getOrderType(), BaseGameInfoCons.UserExtOrderType.反水返佣领取订单))
                .filter(it -> Objects.equals(it.getType(), BaseGameInfoCons.UserExtChangeType.反水领取))
                .filter(it -> it.getAccountType() == AccountChangeTypeConstants.EXPENSE)
                .map(UserExtChange::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setAlreadyRebateAmount(alreadyRebateAmount);
        channelCountOrder.setAlreadyRebateAmount(channelCountOrder.getAlreadyRebateAmount().add(alreadyRebateAmount));
        BigDecimal currencyAlreadyRebateAmount = new BigDecimal(countData.get("currencyAlreadyRebateAmount").toString());
        currencyAlreadyRebateAmount = currencyAlreadyRebateAmount.add(alreadyRebateAmount);
        countData.put("currencyAlreadyRebateAmount", currencyAlreadyRebateAmount);

        //已返佣金额-下级用户返水返给自己的金额
        BigDecimal alreadyReturnCommissionAmount = userExtChangeList.stream()
                .filter(it -> Objects.equals(it.getExtType(), UserExtTypeCons.未领取返佣))
                .filter(it -> Objects.equals(it.getOrderType(), BaseGameInfoCons.UserExtOrderType.反水返佣领取订单))
                .filter(it -> Objects.equals(it.getType(), BaseGameInfoCons.UserExtChangeType.返佣领取))
                .filter(it -> it.getAccountType() == AccountChangeTypeConstants.EXPENSE)
                .map(UserExtChange::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setAlreadyReturnCommissionAmount(alreadyReturnCommissionAmount);
        channelCountOrder.setAlreadyReturnCommissionAmount(channelCountOrder.getAlreadyReturnCommissionAmount().add(alreadyReturnCommissionAmount));

        BigDecimal currencyAlreadyReturnCommissionAmount = new BigDecimal(countData.get("currencyAlreadyReturnCommissionAmount").toString());
        currencyAlreadyReturnCommissionAmount = currencyAlreadyReturnCommissionAmount.add(alreadyReturnCommissionAmount);
        countData.put("currencyAlreadyReturnCommissionAmount", currencyAlreadyReturnCommissionAmount);

        //转盘活动奖励金额
        LambdaQueryWrapper<PlayWheelTerm> termLambdaQueryWrapper = Wrappers.lambdaQuery(PlayWheelTerm.class);
        termLambdaQueryWrapper.between(PlayWheelTerm::getCreateTime, startTime, endTime);
        termLambdaQueryWrapper.eq(PlayWheelTerm::getUserId, userId);
        //termLambdaQueryWrapper.eq(PlayWheelTerm::getCurrencyId, currency.getId());
        List<PlayWheelTerm> playWheelTermList = playWheelTermService.list(termLambdaQueryWrapper);
        BigDecimal playWheelTermAward = playWheelTermList.stream()
                .map(PlayWheelTerm::getAward)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setPlayWheelTermAward(playWheelTermAward);
        channelCountOrder.setPlayWheelTermAward(channelCountOrder.getPlayWheelTermAward().add(playWheelTermAward));

        BigDecimal currencyPlayWheelTermAward = new BigDecimal(countData.get("currencyPlayWheelTermAward").toString());
        currencyPlayWheelTermAward = currencyPlayWheelTermAward.add(playWheelTermAward);
        countData.put("currencyPlayWheelTermAward", currencyPlayWheelTermAward);

        //参入转盘活动次数
        userCountOrder.setPlayWheelTermNum(playWheelTermList.size());
        channelCountOrder.setPlayWheelTermNum(channelCountOrder.getPlayWheelTermNum() + playWheelTermList.size());
        int currencyPlayWheelTermNum = Integer.parseInt(countData.get("currencyPlayWheelTermNum").toString());
        currencyPlayWheelTermNum += playWheelTermList.size();
        countData.put("currencyPlayWheelTermNum", currencyPlayWheelTermNum);

        LambdaQueryWrapper<ActivityAwardReceive> activityAwardReceiveQuery = Wrappers.lambdaQuery(ActivityAwardReceive.class);
        activityAwardReceiveQuery.between(ActivityAwardReceive::getCreateTime, startTime, endTime);
        activityAwardReceiveQuery.eq(ActivityAwardReceive::getUserId, userId);
        //activityAwardReceiveQuery.eq(ActivityAwardReceive::getCurrencyId, currency.getId());
        List<ActivityAwardReceive> activityAwardReceiveList = activityAwardReceiveService.list(activityAwardReceiveQuery);

        List<ActivityAwardReceive> typeIsOneList = activityAwardReceiveList.stream()
                //活动奖励类型(1 彩金-支持小数, 2 转盘次数-必须是整数)
                .filter(it -> it.getType() == 1).collect(Collectors.toList());
        //用户活动奖励领取奖励-彩金金额
        BigDecimal activityAwardBonusAmount = typeIsOneList.stream()
                .map(ActivityAwardReceive::getAward)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        userCountOrder.setActivityAwardBonusAmount(activityAwardBonusAmount);
        channelCountOrder.setActivityAwardBonusAmount(channelCountOrder.getActivityAwardBonusAmount().add(activityAwardBonusAmount));
        BigDecimal currencyActivityAwardBonusAmount = new BigDecimal(countData.get("currencyActivityAwardBonusAmount").toString());
        currencyActivityAwardBonusAmount = currencyActivityAwardBonusAmount.add(activityAwardBonusAmount);
        countData.put("currencyActivityAwardBonusAmount", currencyActivityAwardBonusAmount);

        //用户活动奖励领取次数-奖励彩金
        int receiveBonusNum = typeIsOneList.size();
        userCountOrder.setReceiveBonusNum(receiveBonusNum);
        channelCountOrder.setReceiveBonusNum(channelCountOrder.getReceiveBonusNum() + receiveBonusNum);
        int currencyReceiveBonusNum = Integer.parseInt(countData.get("currencyReceiveBonusNum").toString());
        currencyReceiveBonusNum += receiveBonusNum;
        countData.put("currencyReceiveBonusNum", currencyReceiveBonusNum);

        List<ActivityAwardReceive> typeIsTwoList = activityAwardReceiveList.stream()
                //活动奖励类型(1 彩金-支持小数, 2 转盘次数-必须是整数)
                .filter(it -> it.getType() == 2).collect(Collectors.toList());

        //用户活动奖励领取奖励-转盘总次数
        Integer activityAwardTotalNum = typeIsTwoList.stream()
                .map(ActivityAwardReceive::getAward)
                .reduce(BigDecimal.ZERO, BigDecimal::add).intValue();
        userCountOrder.setActivityAwardTotalNum(activityAwardTotalNum);
        channelCountOrder.setActivityAwardTotalNum(channelCountOrder.getActivityAwardTotalNum() + activityAwardTotalNum);
        int currencyActivityAwardTotalNum = Integer.parseInt(countData.get("currencyActivityAwardTotalNum").toString());
        currencyActivityAwardTotalNum += activityAwardTotalNum;
        countData.put("currencyActivityAwardTotalNum", currencyActivityAwardTotalNum);

        //用户活动奖励领取次数-奖励转盘次数
        Integer receiveTurntableNum = typeIsTwoList.size();
        userCountOrder.setReceiveTurntableNum(receiveTurntableNum);
        channelCountOrder.setReceiveTurntableNum(channelCountOrder.getReceiveTurntableNum() + receiveTurntableNum);

        int currencyReceiveTurntableNum = Integer.parseInt(countData.get("currencyReceiveTurntableNum").toString());
        currencyReceiveTurntableNum += receiveTurntableNum;
        countData.put("currencyReceiveTurntableNum", currencyReceiveTurntableNum);

        LambdaQueryWrapper<AmountChange> amountChangeQuery = Wrappers.lambdaQuery(AmountChange.class);
        amountChangeQuery.between(AmountChange::getCreateTime, startTime, endTime);
        amountChangeQuery.eq(AmountChange::getUserId, userId);
        amountChangeQuery.eq(AmountChange::getCurrencyId, currency.getId());
        List<AmountChange> list = amountChangeService.list(amountChangeQuery);

        // 收支类型(1 收入, 2 支出)
        BigDecimal userEarnings = list.stream().filter(it -> it.getAccountType() == 1)
                .map(AmountChange::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //用户收入
        userCountOrder.setUserEarnings(userEarnings);
        channelCountOrder.setUserEarnings(channelCountOrder.getUserEarnings().add(userEarnings));
        BigDecimal currencyUserEarnings = new BigDecimal(countData.get("currencyUserEarnings").toString());
        currencyUserEarnings = currencyUserEarnings.add(userEarnings);
        countData.put("currencyUserEarnings", currencyUserEarnings);

        // 收支类型(1 收入, 2 支出)
        BigDecimal userExpenses = list.stream().filter(it -> it.getAccountType() == 2)
                .map(AmountChange::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        //用户支出
        userCountOrder.setUserExpenses(userExpenses);
        channelCountOrder.setUserExpenses(channelCountOrder.getUserExpenses().add(userExpenses));

        BigDecimal currencyUserExpenses = new BigDecimal(countData.get("currencyUserExpenses").toString());
        currencyUserExpenses = currencyUserExpenses.add(userExpenses);
        countData.put("currencyUserExpenses", currencyUserExpenses);

        //计算 客损 = 输赢 + 已领取的返水 + 已领取的返佣  +  总彩金(=
        // t_count_order表中的 (bonusAmount(彩金金额) + customerBonusAmount(后台客服手动上分彩金金额)  + playWheelTermAward(转盘活动彩金)
        //         activityAwardBonusAmount(用户活动奖励领取奖励-彩金金额) - customerPointsDeductedAmount(后台客服手动下分金额-下分类型是扣除积分) ))
        BigDecimal totalBonusAmount = userCountOrder.getBonusAmount()
                .add(userCountOrder.getCustomerBonusAmount())
                .add(userCountOrder.getPlayWheelTermAward())
                .add(userCountOrder.getActivityAwardBonusAmount())
                .add(userCountOrder.getPixBonusAmount())
                .subtract(userCountOrder.getCustomerPointsDeductedAmount());
        BigDecimal customerLossAmount = userCountOrder.getWinLoseAmount()
                .add(totalBonusAmount)
                .add(userCountOrder.getAlreadyRebateAmount()) //已领取的返水
                .add(userCountOrder.getAlreadyReturnCommissionAmount()); //已领取的返佣

        userCountOrder.setCustomerLossAmount(customerLossAmount);
        channelCountOrder.setCustomerLossAmount(channelCountOrder.getCustomerLossAmount().add(customerLossAmount));

        BigDecimal currencyCustomerLossAmount = new BigDecimal(countData.get("currencyCustomerLossAmount").toString());
        currencyCustomerLossAmount = currencyCustomerLossAmount.add(customerLossAmount);
        countData.put("currencyCustomerLossAmount", currencyCustomerLossAmount);

        if (this.checkUserCountOrder(userCountOrder)) {

            userCountOrder.setCalculationsTime(new Date());

            LambdaUpdateWrapper<UserCountOrder> orderWrapper = Wrappers.lambdaUpdate(UserCountOrder.class);
            orderWrapper.eq(UserCountOrder::getDayStr, day);
            orderWrapper.eq(UserCountOrder::getCurrencyId, currency.getId());
            orderWrapper.eq(UserCountOrder::getUserId, userId);
            userCountOrderService.saveOrUpdate(userCountOrder, orderWrapper);
        }
        channelCountOrderMap.put(channelId, channelCountOrder);
    }

    /**
     * 检查 用户每日统计订单 是否需要保存
     *
     * @param userCountOrder
     * @return true:需要保存;false:不需要保存
     */
    private boolean checkUserCountOrder(UserCountOrder userCountOrder) {
        //充值订单数
        if (Objects.nonNull(userCountOrder.getRechargeNum()) && userCountOrder.getRechargeNum() > 0) {
            return true;
        }
        //后台客服手动上分订单数量
        if (Objects.nonNull(userCountOrder.getCustomerRechargeNum()) && userCountOrder.getCustomerRechargeNum() > 0) {
            return true;
        }
        //法币提现订单数
        if (Objects.nonNull(userCountOrder.getLawWithdrawNum()) && userCountOrder.getLawWithdrawNum() > 0) {
            return true;
        }
        //提现订单数
        if (Objects.nonNull(userCountOrder.getWithdrawNum()) && userCountOrder.getWithdrawNum() > 0) {
            return true;
        }
        //后台客服手动下分订单数量-下分类型是真实提现
        if (Objects.nonNull(userCountOrder.getCustomerRealCashWithdrawalNum()) && userCountOrder.getCustomerRealCashWithdrawalNum() > 0) {
            return true;
        }
        //后台客服手动下分订单数量-下分类型是扣除积分
        if (Objects.nonNull(userCountOrder.getCustomerPointsDeductedNum()) && userCountOrder.getCustomerPointsDeductedNum() > 0) {
            return true;
        }
        //游戏投注数量(不分输赢)
        if (Objects.nonNull(userCountOrder.getBetNum()) && userCountOrder.getBetNum() > 0) {
            return true;
        }
        //参入转盘活动次数
        if (Objects.nonNull(userCountOrder.getPlayWheelTermNum()) && userCountOrder.getPlayWheelTermNum() > 0) {
            return true;
        }
        //用户活动奖励领取次数-奖励彩金
        if (Objects.nonNull(userCountOrder.getReceiveBonusNum()) && userCountOrder.getReceiveBonusNum() > 0) {
            return true;
        }
        //用户活动奖励领取次数-奖励转盘次数
        if (Objects.nonNull(userCountOrder.getActivityAwardTotalNum()) && userCountOrder.getActivityAwardTotalNum() > 0) {
            return true;
        }
        //用户活动奖励领取次数-奖励转盘次数
        if (Objects.nonNull(userCountOrder.getReceiveTurntableNum()) && userCountOrder.getReceiveTurntableNum() > 0) {
            return true;
        }
        //充值金额-当日
        if (Objects.nonNull(userCountOrder.getRechargeAmount()) && userCountOrder.getRechargeAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //后台客服手动上分金额
        if (Objects.nonNull(userCountOrder.getCustomerRechargeAmount()) && userCountOrder.getCustomerRechargeAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //彩金金额
        if (Objects.nonNull(userCountOrder.getBonusAmount()) && userCountOrder.getBonusAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //后台客服手动上分彩金金额
        if (Objects.nonNull(userCountOrder.getCustomerBonusAmount()) && userCountOrder.getCustomerBonusAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //提现金额
        if (Objects.nonNull(userCountOrder.getWithdrawAmount()) && userCountOrder.getWithdrawAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //后台客服手动下分金额-下分类型是真实提现
        if (Objects.nonNull(userCountOrder.getCustomerRealCashWithdrawalAmount()) && userCountOrder.getCustomerRealCashWithdrawalAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //法币提现金额
        if (Objects.nonNull(userCountOrder.getLawWithdrawAmount()) && userCountOrder.getLawWithdrawAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //后台客服手动下分金额-下分类型是扣除积分
        if (Objects.nonNull(userCountOrder.getCustomerPointsDeductedAmount()) && userCountOrder.getCustomerPointsDeductedAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //手续费
        if (Objects.nonNull(userCountOrder.getFee()) && userCountOrder.getFee().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //实际到账金额
        if (Objects.nonNull(userCountOrder.getRealAmount()) && userCountOrder.getRealAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //后台客服手动下分实际金额-下分类型是真实提现
        if (Objects.nonNull(userCountOrder.getCustomerRealCashWithdrawalRealAmount()) && userCountOrder.getCustomerRealCashWithdrawalRealAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
//        if( Objects.nonNull(userCountOrder.getUserAmount()) && userCountOrder.getUserAmount().compareTo(BigDecimal.ZERO) > 0 ){
//            return true;
//        }
        //用户游戏投注金额
        if (Objects.nonNull(userCountOrder.getBetAmount()) && userCountOrder.getBetAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //结算金额(返奖金额)
        if (Objects.nonNull(userCountOrder.getSettleAmount()) && userCountOrder.getSettleAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //有效投注金额(分输赢)
        if (Objects.nonNull(userCountOrder.getEfficientBetAmount()) && userCountOrder.getEfficientBetAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //输赢金额(返奖额减去投注额)
        if (Objects.nonNull(userCountOrder.getWinLoseAmount()) && userCountOrder.getWinLoseAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //待返水金额
        if (Objects.nonNull(userCountOrder.getWaitRebateAmount()) && userCountOrder.getWaitRebateAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //返佣金额
        if (Objects.nonNull(userCountOrder.getWaitReturnCommissionAmount()) && userCountOrder.getWaitReturnCommissionAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //已返水金额
        if (Objects.nonNull(userCountOrder.getAlreadyRebateAmount()) && userCountOrder.getAlreadyRebateAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //已返佣金额
        if (Objects.nonNull(userCountOrder.getAlreadyReturnCommissionAmount()) && userCountOrder.getAlreadyReturnCommissionAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //转盘活动奖励金额
        if (Objects.nonNull(userCountOrder.getPlayWheelTermAward()) && userCountOrder.getPlayWheelTermAward().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //用户活动奖励领取奖励-彩金金额
        if (Objects.nonNull(userCountOrder.getActivityAwardBonusAmount()) && userCountOrder.getActivityAwardBonusAmount().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //用户收入
        if (Objects.nonNull(userCountOrder.getUserEarnings()) && userCountOrder.getUserEarnings().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //用户支出
        if (Objects.nonNull(userCountOrder.getUserExpenses()) && userCountOrder.getUserExpenses().compareTo(BigDecimal.ZERO) > 0) {
            return true;
        }
        //客损[计算公式 等于 输赢 加 所有送的金额 减 手动下分(扣除积分)]
        return Objects.nonNull(userCountOrder.getCustomerLossAmount()) && userCountOrder.getCustomerLossAmount().compareTo(BigDecimal.ZERO) > 0;
    }

    public Integer getShareholderActiveUserCount(Long shareholderId, String nowDay) {
        //获取当前渠道下的所有用户
        LambdaQueryWrapper<TUser> userQuery = Wrappers.lambdaQuery(TUser.class);
        if (Objects.nonNull(shareholderId)) {
            userQuery.eq(TUser::getShareholderId, shareholderId);
        }
        List<TUser> channelUserList = userService.list(userQuery);
        if (CollectionUtils.isEmpty(channelUserList)) {
            return 0;
        }
        List<Long> channelUserIdList = channelUserList.stream().map(TUser::getUserId).collect(Collectors.toList());

        //金额有变动的 -> 就是活跃的
        String startTime = nowDay + " 00:00:00";
        String endTime = nowDay + " 23:59:59";

        LambdaQueryWrapper<AmountChange> amountChangeQuery = Wrappers.lambdaQuery(AmountChange.class);
        amountChangeQuery.between(AmountChange::getCreateTime, startTime, endTime);
        amountChangeQuery.in(AmountChange::getUserId, channelUserIdList);
        List<AmountChange> amountChangeList = amountChangeService.list(amountChangeQuery);
        List<Long> activePeopleUserIds = amountChangeList.stream().map(AmountChange::getUserId).distinct().collect(Collectors.toList());
        log.info("股东活跃人数统计, shareholderId={}, nowDay={}, activePeopleUserIds.size={}", shareholderId, nowDay, activePeopleUserIds.size());
        return activePeopleUserIds.size();
    }
}
