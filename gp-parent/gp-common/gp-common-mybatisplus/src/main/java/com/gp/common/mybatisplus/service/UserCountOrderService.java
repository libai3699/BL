package com.gp.common.mybatisplus.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.util.StringUtils;
import com.gp.common.base.constant.UserCountOrderCons;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.*;
import com.gp.common.mybatisplus.entity.Currency;
import com.gp.common.mybatisplus.mapper.UserCountOrderMapper;
import com.gp.common.mybatisplus.vo.MyDividendsVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 每日用户订单统计Service业务层处理
 *
 * @author axing
 * @date 2024-05-10
 */
@Slf4j
@Service
public class UserCountOrderService extends ServiceImpl<UserCountOrderMapper, UserCountOrder> {
    @Autowired
    private UserCountOrderMapper userCountOrderMapper;

    @Resource
    private OrderTermService orderTermService;

    @Resource
    private CurrencyService currencyService;

    @Resource
    private ConfigRiskService configRiskService;

    @Resource
    private UserChannelService userChannelService;

    @Resource
    private UserService userService;

    /**
     * 查询每日用户订单统计
     *
     * @param id 每日用户订单统计ID
     * @return 每日用户订单统计
     */

    public UserCountOrder selectUserCountOrderById(Long id) {
        return userCountOrderMapper.selectUserCountOrderById(id);
    }

    /**
     * 查询每日用户订单统计数据条数
     *
     * @param param 每日用户订单统计
     * @return 每日用户订单统计数量
     */
    public int selectUserCountOrderCount(UserCountOrder param) {
        return userCountOrderMapper.selectUserCountOrderCount(param);
    }

    /**
     * 查询分组用户订单统计数据条数
     *
     * @param param 每日用户订单统计
     * @return 每日用户订单统计数量
     */
    public int selectUserCountOrderGroupCount(UserCountOrder param) {
        return userCountOrderMapper.selectUserCountOrderGroupCount(param);
    }

    /**
     * 查询每日用户订单统计列表
     *
     * @param param 每日用户订单统计
     * @return 每日用户订单统计
     */

    public List<UserCountOrder> selectUserCountOrderList(UserCountOrder param) {
        List<UserCountOrder> list = userCountOrderMapper.selectUserCountOrderList(param);
        if (list.size() == 0) {
            return list;
        }
        List<Currency> currencyList = currencyService.list();
        Map<Integer, Currency> currencyMap = currencyList.stream()
                .collect(Collectors.toMap(Currency::getId, Function.identity()));
        for (UserCountOrder it : list) {
            Currency currency = currencyMap.get(it.getCurrencyId());
            if (Objects.nonNull(currency)) {
                it.setCurrencyName(currency.getItemName() + "-" + currency.getChainTag());
            }
        }
        return list;
    }

    public List<UserCountOrder> getByUserIdlist(UserCountOrder param) {
        List<UserCountOrder> list = userCountOrderMapper.getByUserIdlist(param);
        if (list.isEmpty()) {
            return list;
        }
        List<Currency> currencyList = currencyService.list();
        Map<Integer, Currency> currencyMap = currencyList.stream()
                .collect(Collectors.toMap(Currency::getId, Function.identity()));
        for (UserCountOrder it : list) {
            Currency currency = currencyMap.get(it.getCurrencyId());
            if (Objects.nonNull(currency)) {
                it.setCurrencyName(currency.getItemName() + "-" + currency.getChainTag());
            }
        }
        return list;
    }

    /**
     * 两阶段分页 Phase A：仅取当前页的 user_id 列表，配合 PageHelper.startPage 使用（PageHelper 自动添加 COUNT + LIMIT）。
     * 返回值实际是 {@link com.github.pagehelper.Page}，调用方可强转后取 total。
     */
    public List<Long> pageUserIdsForGroupList(UserCountOrder param) {
        return userCountOrderMapper.pageUserIdsForGroupList(param);
    }

    public Map<Long, UserCountOrder> batchGetLatestWaitAmounts(Set<Long> userIds, String endTime) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<UserCountOrder> list = userCountOrderMapper.batchGetLatestWaitAmounts(userIds, endTime);
        return list.stream().collect(Collectors.toMap(UserCountOrder::getUserId, Function.identity(), (a, b) -> a));
    }


    /**
     * 新增每日用户订单统计
     *
     * @param param 每日用户订单统计
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertUserCountOrder(UserCountOrder param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改每日用户订单统计
     *
     * @param param 每日用户订单统计
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserCountOrder(UserCountOrder param) {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除每日用户订单统计
     *
     * @param ids 需要删除的每日用户订单统计ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserCountOrderByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除每日用户订单统计信息
     *
     * @param id 每日用户订单统计ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserCountOrderById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    /**
     * TODO 处理用户 投注额
     */
    public void handleUserCountOrder(
            String orderNo) {
        if (StringUtils.isEmpty(orderNo)) {
            return;
        }
        LambdaQueryWrapper<OrderTerm> orderTermQueryWrapper = Wrappers.lambdaQuery(OrderTerm.class);
        orderTermQueryWrapper.eq(OrderTerm::getOrderNo, orderNo);
        OrderTerm orderTerm = orderTermService.getOne(orderTermQueryWrapper);
        if (Objects.isNull(orderTerm)) {
            log.info("handleUserCountOrder, 订单号非法! orderNo={}", orderNo);
            return;
        }
        doHandelUserCountOrder(orderTerm);

    }

    /**
     * TODO 处理用户 投注额
     */
    public void handleUserCountOrder(
            OrderTerm orderTerm) {
        doHandelUserCountOrder(orderTerm);
    }

    private void doHandelUserCountOrder(OrderTerm orderTerm) {
        Date now = new Date();
        String nowDay = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, now);

        LambdaQueryWrapper<UserCountOrder> userCountOrderQueryWrapper = Wrappers.lambdaQuery(UserCountOrder.class);
        userCountOrderQueryWrapper.eq(UserCountOrder::getUserId, orderTerm.getUserId());
        userCountOrderQueryWrapper.eq(UserCountOrder::getCurrencyId, orderTerm.getCurrencyId());
        userCountOrderQueryWrapper.eq(UserCountOrder::getDayStr, nowDay);
        UserCountOrder userCountOrder = this.getOne(userCountOrderQueryWrapper);
        if (Objects.nonNull(userCountOrder)) {
            // 修改
            UserCountOrder update = new UserCountOrder();
            Integer betNum = userCountOrder.getBetNum();
            update.setBetAmount(userCountOrder.getBetAmount().add(orderTerm.getBetAmount()));
            update.setBetNum(betNum + 1);
            update.setId(userCountOrder.getId());
            this.updateUserCountOrder(update);
        } else {
            // 创建
            UserCountOrder insert = new UserCountOrder();
            insert.setUserId(orderTerm.getUserId());
            insert.setDayStr(nowDay);
            insert.setCurrencyId(orderTerm.getCurrencyId());
            insert.setItemId(orderTerm.getItemId());
            insert.setChainTag(orderTerm.getChainTag());
            insert.setBetAmount(orderTerm.getBetAmount());
            insert.setBetNum(1);
            this.insertUserCountOrder(insert);
        }
    }

    public OrderAmountHeard queryHeard(Long userId, String date, Date startTime, Date endTime) {
        OrderAmountHeard orderAmountHeard = userCountOrderMapper.queryHeard(userId, date, startTime, endTime);
        if (orderAmountHeard == null) {
            orderAmountHeard = new OrderAmountHeard();
        }
        return orderAmountHeard;
    }

    public OrderBetHeard queryBetHeard(Long userId, String date, Date startTime, Date endTime) {
        OrderBetHeard orderBetHeard = userCountOrderMapper.queryBetHeard(userId, date, startTime, endTime);
        if (orderBetHeard == null) {
            orderBetHeard = new OrderBetHeard();
        }
        return orderBetHeard;
    }

    public BigDecimal queryTotalNotDayAmount(Long userId, Integer type) {
        String date = DateUtils.getDate();
        UserCountTotalAmount userCountTotalAmount = userCountOrderMapper.queryTotalNotDayAmount(userId, date);
        if (Objects.equals(type, UserCountOrderCons.有效投注)) {
            return userCountTotalAmount.getEfficientBetAmount();
        }
        if (Objects.equals(type, UserCountOrderCons.总反水)) {
            return userCountTotalAmount.getAlreadyRebateAmount();
        }
        if (Objects.equals(type, UserCountOrderCons.总反拥)) {
            return userCountTotalAmount.getAlreadyReturnCommissionAmount();
        }
        if (Objects.equals(type, UserCountOrderCons.打码量)) {
            return userCountTotalAmount.getCodeAmount();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal queryTotalAmount(Long userId, Integer type) {
        UserCountTotalAmount userCountTotalAmount = userCountOrderMapper.queryTotalAmount(userId);
        if (Objects.equals(type, UserCountOrderCons.有效投注)) {
            return userCountTotalAmount.getEfficientBetAmount();
        }
        if (Objects.equals(type, UserCountOrderCons.总反水)) {
            return userCountTotalAmount.getAlreadyRebateAmount();
        }
        if (Objects.equals(type, UserCountOrderCons.总反拥)) {
            return userCountTotalAmount.getAlreadyReturnCommissionAmount();
        }
        if (Objects.equals(type, UserCountOrderCons.打码量)) {
            return userCountTotalAmount.getCodeAmount();
        }
        return BigDecimal.ZERO;
    }

    public UserCountTotalAmount querySumAmountByUserIdAndTime(List<Long> userIdLIst, Date startTime, Date endTime) {

        return userCountOrderMapper.querySumAmountByUserIdAndTime(userIdLIst, startTime, endTime);

    }

    public OrderGameCareerHeard queryOrderGameCareerHeard(Long userId, String dateStr) {
        return userCountOrderMapper.queryOrderGameCareerHeard(userId, dateStr);
    }

    public UserCountTotalAmount querySumAmountByUserIdIdAndTime(Long userId, Date startTime, Date endTime) {
        List<Long> arr = CollUtil.newArrayList(userId);
        return userCountOrderMapper.querySumAmountByUserIdAndTime(arr, startTime, endTime);
    }

    public UserCountOrder selectTeamReportCountList(String start, String end, List<Long> userIdList) {
        return userCountOrderMapper.selectTeamReportCountList(start, end, userIdList);

    }

    public List<UserLowM> queryAgentSumAmountByUserNameAndTime(String userName, Long userId, Date startTime,
            Date endTime, Integer sort) {
        return userCountOrderMapper.queryAgentSumAmountByUserNameAndTime(userName, userId, startTime, endTime, sort);
    }

    public List<UserLowM> queryAgentSumAmountByUserNameAndTimeNewAddUser(String userName, Long userId, Date startTime,
            Date endTime, Integer sort) {
        return userCountOrderMapper.queryAgentSumAmountByUserNameAndTimeNewAddUser(userName, userId, startTime, endTime,
                sort);
    }

    public List<UserLowM> queryAgentSumAmountByUserNameAndTimeByPid(String userName, Long userId, Date startTime,
            Date endTime, Integer sort) {
        return userCountOrderMapper.queryAgentSumAmountByUserNameAndTimeByPid(userName, userId, startTime, endTime,
                sort);
    }

    public List<UserLowM> queryAgentSumAmountByUserNameAndTimeByPidNewAddUser(String userName, Long userId,
            Date startTime, Date endTime,
            Integer sort) {
        return userCountOrderMapper.queryAgentSumAmountByUserNameAndTimeByPidNewAddUser(userName, userId, startTime,
                endTime, sort);
    }

    public List<UserLowM> queryChannelSumAmountByUserNameAndTime(String userName, Long agentId, Date startTime,
            Date endTime, Integer sort) {
        return userCountOrderMapper.queryChannelSumAmountByUserNameAndTime(userName, agentId, startTime, endTime, sort);
    }

    public List<UserLowM> queryChannelSumAmountByUserNameAndTimeNewAddUser(String userName, Long agentId,
            Date startTime, Date endTime,
            Integer sort) {
        return userCountOrderMapper.queryChannelSumAmountByUserNameAndTimeNewAddUser(userName, agentId, startTime,
                endTime, sort);
    }

    /**
     * 我的分红
     *
     * @param userId   用户ID
     * @param dateType 日期类型(1-本月 2-上月)
     * @return
     */
    public MyDividendsVO queryMyDividends(Long userId, Integer dateType) {
        MyDividendsVO result = new MyDividendsVO();
        // 获取所有下级和自己
        List<Long> userIdList = userService.getAllSubUserId(userId);
        // 处理日期
        TUser user = userService.getById(userId);
        Date endDate = new Date();
        Date startDate = DateUtil.beginOfMonth(endDate);
        if (dateType.equals(2)) {
            endDate = DateUtil.offsetMonth(endDate, -1);
            startDate = DateUtil.beginOfMonth(endDate);
            endDate = DateUtil.endOfMonth(endDate);
        }
        String startDateStr = DateUtil.format(startDate, "yyyy-MM-dd");
        String endDateStr = DateUtil.format(endDate, "yyyy-MM-dd");
        LambdaQueryWrapper<UserCountOrder> lambdaQuery = Wrappers.lambdaQuery(UserCountOrder.class);
        lambdaQuery.in(UserCountOrder::getUserId, userIdList)
                .between(UserCountOrder::getDayStr, startDateStr, endDateStr);
        List<UserCountOrder> list = userCountOrderMapper.selectList(lambdaQuery);

        // 获取彩票代理配置
        UserChannel userChannel = userChannelService.getOne(Wrappers.lambdaQuery(UserChannel.class)
                .eq(UserChannel::getUserId, userId)
                .eq(UserChannel::getChannelId, user.getChannelId())
                .orderByDesc(UserChannel::getCreateTime).last("limit 1"));
        if (null != userChannel) {
            result.setNegativeProfitRatio(userChannel.getDividendRebate());
        }
        // 获取各项费率配置
        BigDecimal feeRate = configRiskService.transactionFeeRate(); // 官方收取比例(游戏手续费比例)
        BigDecimal rechargeFeeRateVal = configRiskService.rechargeFeeRate(); // 充值手续费比例
        BigDecimal withdrawFeeRateVal = configRiskService.withdrawFeeRate(); // 提现手续费比例
        BigDecimal managementFeeRateVal = configRiskService.managementFeeRate(); // 管理费比例

        // 设置费率到返回结果
        result.setGameFeeRate(feeRate);
        result.setRechargeFeeRate(rechargeFeeRateVal);
        result.setWithdrawFeeRate(withdrawFeeRateVal);
        result.setManagementFeeRate(managementFeeRateVal);

        if (CollectionUtils.isNotEmpty(list)) {
            // +已返水总金额(alreadyRebateAmount)
            BigDecimal alreadyRebateAmount = sumBigDecimalField(list, UserCountOrder::getAlreadyRebateAmount);
            result.setAlreadyRebateAmount(alreadyRebateAmount);
            // + 外水金额(waitReturnCommissionAmount)
            BigDecimal waitReturnCommissionAmount = sumBigDecimalField(list,
                    UserCountOrder::getWaitReturnCommissionAmount);
            result.setWaitReturnCommissionAmount(waitReturnCommissionAmount);
            // 未领取反水 waitRebateAmount
            BigDecimal waitRebateAmount = sumBigDecimalField(list, UserCountOrder::getWaitRebateAmount);
            result.setWaitRebateAmount(waitRebateAmount);
            // + 直充彩金 (bonusAmount)
            BigDecimal bonusAmount = sumBigDecimalField(list, UserCountOrder::getBonusAmount);
            result.setBonusAmount(bonusAmount);

            // + 手动上分赠送彩金 (customerBonusAmount)
            BigDecimal customerBonusAmount = sumBigDecimalField(list, UserCountOrder::getCustomerBonusAmount);
            result.setCustomerBonusAmount(customerBonusAmount);

            // + 转盘彩金 (playWheelTermAward)
            BigDecimal playWheelTermAward = sumBigDecimalField(list, UserCountOrder::getPlayWheelTermAward);
            result.setPlayWheelTermAward(playWheelTermAward);

            // + 用户活动奖励领取奖励-彩金金额 (activityAwardBonusAmount)
            BigDecimal activityAwardBonusAmount = sumBigDecimalField(list, UserCountOrder::getActivityAwardBonusAmount);
            result.setActivityAwardBonusAmount(activityAwardBonusAmount);

            // + 总红包彩金 (totalPacketAmount)
            BigDecimal totalPacketAmount = sumBigDecimalField(list, UserCountOrder::getTotalPacketAmount);
            result.setTotalPacketAmount(totalPacketAmount);

            // + 转账彩金金额 (transferBonusAmount)
            BigDecimal transferBonusAmount = sumBigDecimalField(list, UserCountOrder::getTransferBonusAmount);
            result.setTransferBonusAmount(transferBonusAmount);

            // - 彩金扣款(customerPointsDeductedAmount)
            BigDecimal customerPointsDeductedAmount = sumBigDecimalField(list,
                    UserCountOrder::getCustomerPointsDeductedAmount);
            result.setCustomerPointsDeductedAmount(customerPointsDeductedAmount);
            // - 已返外水金额(alreadyReturnCommissionAmount)
            BigDecimal alreadyReturnCommissionAmount = sumBigDecimalField(list,
                    UserCountOrder::getAlreadyReturnCommissionAmount);
            result.setAlreadyReturnCommissionAmount(alreadyReturnCommissionAmount);

            // 总彩金
            result.setTotalBonusAmount(calculateTotalAmountForList(list));
            // 总充值
            BigDecimal totalRecharge = sumBigDecimalField(list, UserCountOrder::getRechargeAmount)
                    .add(sumBigDecimalField(list, UserCountOrder::getCustomerRechargeAmount));
            result.setTotalRecharge(totalRecharge);
            // 总提现
            BigDecimal totalWithdraw = sumBigDecimalField(list, UserCountOrder::getWithdrawAmount)
                    .add(sumBigDecimalField(list, UserCountOrder::getCustomerRealCashWithdrawalAmount))
                    .add(sumBigDecimalField(list, UserCountOrder::getLawWithdrawAmount));
            result.setTotalWithdraw(totalWithdraw);

            BigDecimal difference = totalRecharge.subtract(totalWithdraw);
            // 充提差额
            result.setDifference(difference);

            // 总余额
            BigDecimal totalBalance = sumBigDecimalField(list, UserCountOrder::getUserAmount);
            result.setTotalBalance(totalBalance);
            // 游戏输赢
            BigDecimal gameWin = sumBigDecimalField(list, UserCountOrder::getWinLoseAmount);
            result.setGameWin(gameWin);

            // === 新的费用明细计算 ===
            // 充值手续费 = 总充值 × 充值手续费比例
            BigDecimal rechargeFee = totalRecharge.multiply(rechargeFeeRateVal);
            result.setRechargeFee(rechargeFee);

            // 提现手续费 = 总提现 × 提现手续费比例
            BigDecimal withdrawFee = totalWithdraw.multiply(withdrawFeeRateVal);
            result.setWithdrawFee(withdrawFee);

            // 游戏手续费 = 游戏输赢 × 官方收取比例
            BigDecimal gameFee = gameWin.multiply(feeRate);
            result.setGameFee(gameFee);

            // 毛利润 = 总充值 - 充值手续费 - 总提现 - 提现手续费 - 用户余额 - 未领取返点 - 游戏手续费
            BigDecimal grossProfit = totalRecharge
                    .subtract(rechargeFee)
                    .subtract(totalWithdraw)
                    .subtract(withdrawFee)
                    .subtract(totalBalance)
                    .subtract(waitRebateAmount)
                    .subtract(gameFee);
            result.setGrossProfit(grossProfit);

            // 管理费 = 毛利润 × 管理费比例
            BigDecimal managementFee = grossProfit.multiply(managementFeeRateVal);
            result.setManagementFee(managementFee);

            // 团队盈利 = 毛利润 - 管理费
            BigDecimal teamProfitU = grossProfit.subtract(managementFee);
            result.setTeamProfitU(teamProfitU);

            // 我的分红 = 团队盈利 × 负盈利比例
            BigDecimal teamProfit = teamProfitU.multiply(result.getNegativeProfitRatio());
            result.setTeamProfit(teamProfit);
            result.setActualAmount(teamProfit.subtract(gameFee));

            // 打码量-根据list的更新时间获取最新一条数据
            UserCountOrder userCountOrder = list.stream()
                    .max(Comparator.comparing(UserCountOrder::getUpdateTime)).orElse(null);
            if (userCountOrder != null) {
                result.setCodeAmount(userCountOrder.getCodeAmount());
            }
        }
        return result;
    }

    private BigDecimal sumBigDecimalField(List<UserCountOrder> orders, Function<UserCountOrder, BigDecimal> getter) {
        return orders.stream()
                .map(getter)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 计算订单列表总金额
     *
     * @param list 订单列表
     * @return 总金额
     */
    public BigDecimal calculateTotalAmountForList(List<UserCountOrder> list) {
        return list.stream()
                .map(order -> order.getBonusAmount()
                        .add(order.getCustomerBonusAmount())
                        .add(order.getTotalPacketAmount())
                        .add(order.getPixBonusAmount())
                        .add(order.getPlayWheelTermAward())
                        .add(order.getYndBonusAmount())
                        .add(order.getUpayBonusAmount())
                        .add(order.getPay1818BonusAmount())
                        .add(order.getRupeeBonusAmount())
                        .add(order.getZlotyBonusAmount())
                        .add(order.getHwpayBonusAmount())
                        .add(order.getUBonusAmount())
                        .add(order.getActivityAwardBonusAmount()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .setScale(2, RoundingMode.DOWN);
    }

    public UserCountOrder countSum(UserCountOrder param) {
        return userCountOrderMapper.countSum(param);
    }

    public BigDecimal querySumCustomerLossAmount(Long userId) {
        return userCountOrderMapper.querySumCustomerLossAmount(userId);
    }

    public List<UserCountOrder> getMonthList(UserCountOrder param) {
        List<UserCountOrder> list = userCountOrderMapper.getMonthList(param);
        if (list.size() == 0) {
            return list;
        }
        List<Currency> currencyList = currencyService.list();
        Map<Integer, Currency> currencyMap = currencyList.stream()
                .collect(Collectors.toMap(Currency::getId, Function.identity()));
        for (UserCountOrder it : list) {
            Currency currency = currencyMap.get(it.getCurrencyId());
            if (Objects.nonNull(currency)) {
                it.setCurrencyName(currency.getItemName() + "-" + currency.getChainTag());
            }
        }
        return list;

    }

    public UserCountOrder selectSumOneByMonthStr(UserCountOrder param) {
        UserCountOrder userCountOrder = userCountOrderMapper.selectSumOneByMonthStr(param);
        Currency currency = currencyService.getById(userCountOrder.getCurrencyId());
        if (Objects.nonNull(currency)) {
            userCountOrder.setCurrencyName(currency.getItemName() + "-" + currency.getChainTag());
        }
        return userCountOrder;
    }

    public List<UserCountOrder> topRankingList(UserCountOrder param) {
        List<UserCountOrder> list = userCountOrderMapper.topRankingList(param);
        if (list.size() == 0) {
            return list;
        }
        return list;
    }

    public List<UserCountOrder> betRankingList(UserCountOrder param) {
        List<UserCountOrder> list = userCountOrderMapper.betRankingList(param);
        if (list.size() == 0) {
            return list;
        }
        return list;
    }

    public List<UserCountOrder> winRankingList(UserCountOrder param) {
        List<UserCountOrder> list = userCountOrderMapper.winRankingList(param);
        if (list.size() == 0) {
            return list;
        }
        return list;
    }

    public List<UserCountOrder> agencyRankingListByChannel(UserCountOrder param, List<Long> channelIds) {
        List<UserCountOrder> list = userCountOrderMapper.agencyRankingListByChannel(param, channelIds);
        if (list.size() == 0) {
            return list;
        }
        return list;
    }
    public Map<Long, BigDecimal> queryCustomerLossMap(List<Long> userIds) {
        List<Map<String, Object>> maps = userCountOrderMapper.queryCustomerLossMap(userIds);
        return maps.stream()
                .collect(Collectors.toMap(
                        m -> (Long) m.get("userId"),
                        m -> (BigDecimal) m.getOrDefault("customerLossAmount", BigDecimal.ZERO)
                ));
    }
}
