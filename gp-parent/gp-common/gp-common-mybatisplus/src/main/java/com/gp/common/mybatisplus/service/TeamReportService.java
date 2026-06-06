package com.gp.common.mybatisplus.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.common.core.result.AjaxResult;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.mybatisplus.entity.DailyActiveUser;
import com.gp.common.mybatisplus.entity.GameBetAmountTotalAmount;
import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.entity.UserCountOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TeamReportService {

    private static final Logger log = LoggerFactory.getLogger(TeamReportService.class);
    @Resource
    private UserService userService;

    @Resource
    private UserCountOrderService userCountOrderService;

    @Resource
    private DailyActiveUserService dailyActiveUserService;

    @Resource
    private UserCountGameCodeService userCountGameCodeService;

    public AjaxResult teamList(TUser param) {
        Map<String, Object> result = new HashMap<>();
//        CecuUtil.cleanDbInfo();
        // 获取当前用户
        if (Objects.isNull(param.getUserId())) {
            return AjaxResult.error(com.common.core.util.StringUtils.format(MessagesUtils.get("common.param.is.null"), "id"));
        }
        ArrayList<TUser> tUserList = CollectionUtil.newArrayList();
        collectAllSubUsers(param.getUserId(), tUserList);
        if (CollUtil.isEmpty(tUserList)) {
            return AjaxResult.success(result);
        }
        log.info("用户信息：{}", tUserList);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today;
        if (Objects.isNull(param.getSearchTimes())) {
            today = LocalDate.now();
        } else {
            today = param.getSearchTimes();
        }

        String start = today.format(formatter) + " 00:00:00";
        String end = today.format(formatter) + " 23:59:59";

        List<Long> userIdList = tUserList.stream().map(TUser::getUserId).collect(Collectors.toList());
        result.put("totalNumberOfPeople", userIdList.size());
        //有效人数
        Date from = Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Integer byActiveDateNum = dailyActiveUserService.queryAvailableNumByTime(param.getUserId(), from);
        result.put("numberOfPeopleInCharge", byActiveDateNum);
        // 获取当天数据
        UserCountOrder todayCount = userCountOrderService.selectTeamReportCountList(start, end, userIdList);
        if (todayCount != null) {
            //总存款:totalRechargeAmount
            todayCount.setTotalRechargeAmount(
                    (Objects.nonNull(todayCount.getRechargeAmount()) ? todayCount.getRechargeAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(todayCount.getCustomerRechargeAmount()) ? todayCount.getCustomerRechargeAmount() : BigDecimal.ZERO)
            );
            //总提款:totalWithdrawalAmount
            todayCount.setTotalWithdrawalAmount(
                    (Objects.nonNull(todayCount.getWithdrawAmount()) ? todayCount.getWithdrawAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(todayCount.getCustomerRealCashWithdrawalAmount()) ? todayCount.getCustomerRealCashWithdrawalAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(todayCount.getLawWithdrawAmount()) ? todayCount.getLawWithdrawAmount() : BigDecimal.ZERO));
            //总反水:alreadyRebateAmount
            todayCount.setAlreadyRebateAmount(Objects.nonNull(todayCount.getAlreadyRebateAmount()) ? todayCount.getAlreadyRebateAmount() : BigDecimal.ZERO);

            //总彩金:totalBonusAmount
            todayCount.setTotalBonusAmount(
                    (Objects.nonNull(todayCount.getBonusAmount()) ? todayCount.getBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(todayCount.getPlayWheelTermAward()) ? todayCount.getPlayWheelTermAward() : BigDecimal.ZERO)
                            .add(Objects.nonNull(todayCount.getActivityAwardBonusAmount()) ? todayCount.getActivityAwardBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(todayCount.getCustomerBonusAmount()) ? todayCount.getCustomerBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(todayCount.getPixBonusAmount()) ? todayCount.getPixBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(todayCount.getYndBonusAmount()) ? todayCount.getYndBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(todayCount.getUpayBonusAmount()) ? todayCount.getUpayBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(todayCount.getPay1818BonusAmount()) ? todayCount.getPay1818BonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(todayCount.getTransferBonusAmount()) ? todayCount.getTransferBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(todayCount.getTotalPacketAmount()) ? todayCount.getTotalPacketAmount() : BigDecimal.ZERO));

            //总盈亏:customerLossAmount
            todayCount.setCustomerLossAmount(Objects.nonNull(todayCount.getCustomerLossAmount()) ? todayCount.getCustomerLossAmount() : BigDecimal.ZERO);
            LambdaQueryWrapper<DailyActiveUser> queryWrapper1 = Wrappers.lambdaQuery(DailyActiveUser.class);
            queryWrapper1.in(DailyActiveUser::getUserId, userIdList);
            queryWrapper1.eq(DailyActiveUser::getActiveDate, DateUtils.formatDate(from));
            queryWrapper1.eq(DailyActiveUser::getIsNew, 1);
            int count = dailyActiveUserService.count(queryWrapper1);
            todayCount.setCountNew(count);
            //弹珠打码量
            GameBetAmountTotalAmount gameBetAmountTotalAmount = userCountGameCodeService.querySumAmountByUserIdAndTime(userIdList, DateUtils.formatDate(start), DateUtils.formatDate(end));
            todayCount.setDZBetAmount(gameBetAmountTotalAmount.getGameTypeCode9());

        } else {
            todayCount = new UserCountOrder();
            todayCount.setCountNew(0);
        }
        result.put("today", todayCount);

//        // 获取7天数据
//        UserCountOrder sevenCount = userCountOrderService.selectTeamReportCountList(sevenStart, end, userIdList);
//        if (sevenCount != null) {
//            sevenCount.setTotalRechargeAmount(
//                    sevenCount.getRechargeAmount().add(sevenCount.getCustomerRechargeAmount())
//            );
//            sevenCount.setTotalWithdrawalAmount(
//                    sevenCount.getWithdrawAmount()
//                            .add(sevenCount.getCustomerRealCashWithdrawalAmount())
//                            .add(sevenCount.getLawWithdrawAmount())
//            );
//        } else {
//            sevenCount = new UserCountOrder();
//        }
//        result.put("seven", sevenCount);

        // 获取30天数据
        String[] monthStartAndEnd = DateUtils.getMonthStartAndEnd(today);
        UserCountOrder thirtyCount = userCountOrderService.selectTeamReportCountList(monthStartAndEnd[0], monthStartAndEnd[1], userIdList);
        if (thirtyCount != null) {
            //总存款:totalRechargeAmount
            thirtyCount.setTotalRechargeAmount(
                    (Objects.nonNull(thirtyCount.getRechargeAmount()) ? thirtyCount.getRechargeAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getCustomerRechargeAmount()) ? thirtyCount.getCustomerRechargeAmount() : BigDecimal.ZERO)
            );
            //总提款:totalWithdrawalAmount
            thirtyCount.setTotalWithdrawalAmount(
                    (Objects.nonNull(thirtyCount.getWithdrawAmount()) ? thirtyCount.getWithdrawAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getCustomerRealCashWithdrawalAmount()) ? thirtyCount.getCustomerRealCashWithdrawalAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getLawWithdrawAmount()) ? thirtyCount.getLawWithdrawAmount() : BigDecimal.ZERO));
            //总反水:alreadyRebateAmount
            thirtyCount.setAlreadyRebateAmount(Objects.nonNull(thirtyCount.getAlreadyRebateAmount()) ? thirtyCount.getAlreadyRebateAmount() : BigDecimal.ZERO);

            //总彩金:totalBonusAmount
            thirtyCount.setTotalBonusAmount(
                    (Objects.nonNull(thirtyCount.getBonusAmount()) ? thirtyCount.getBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getPlayWheelTermAward()) ? thirtyCount.getPlayWheelTermAward() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getActivityAwardBonusAmount()) ? thirtyCount.getActivityAwardBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getCustomerBonusAmount()) ? thirtyCount.getCustomerBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getPixBonusAmount()) ? thirtyCount.getPixBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getYndBonusAmount()) ? thirtyCount.getYndBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getUpayBonusAmount()) ? thirtyCount.getUpayBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getPay1818BonusAmount()) ? thirtyCount.getPay1818BonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getTransferBonusAmount()) ? thirtyCount.getTransferBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getTotalPacketAmount()) ? thirtyCount.getTotalPacketAmount() : BigDecimal.ZERO));

            //总盈亏:customerLossAmount
            thirtyCount.setCustomerLossAmount(Objects.nonNull(thirtyCount.getCustomerLossAmount()) ? thirtyCount.getCustomerLossAmount() : BigDecimal.ZERO);
            //弹珠打码量
            GameBetAmountTotalAmount gameBetAmountTotalAmount = userCountGameCodeService.querySumAmountByUserIdAndTime(
                    userIdList, DateUtils.formatDate(monthStartAndEnd[0] + " 00:00:00"), DateUtils.formatDate(monthStartAndEnd[1] + " 23:59:59"));
            thirtyCount.setDZBetAmount(gameBetAmountTotalAmount.getGameTypeCode9());
        } else {
            thirtyCount = new UserCountOrder();
        }
        result.put("thirty", thirtyCount);

        return AjaxResult.success(result);
    }

    public AjaxResult individualList(TUser param) {
//        CecuUtil.cleanDbInfo();
        // 获取当前用户
        if (Objects.isNull(param.getUserId())) {
            return AjaxResult.error(com.common.core.util.StringUtils.format(MessagesUtils.get("common.param.is.null"), "id"));
        }

        // 用户信息
        TUser tUser = userService.getById(param.getUserId());
        if (Objects.isNull(tUser)) {
            return AjaxResult.error("用户不存在");
        }

        Long userId = tUser.getUserId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        String start = today.format(formatter) + " 00:00:00";
        String end = today.format(formatter) + " 23:59:59";
        String sevenStart = today.minusDays(6).format(formatter) + " 00:00:00";
        String thirtyStart = today.minusDays(29).format(formatter) + " 00:00:00";

        List<Long> userIdList = new ArrayList<>();
        userIdList.add(userId);
        log.info("今天:{},今天结束,七天开始:{},30天开始:{},用户:{}", start, end, sevenStart, thirtyStart, userIdList);
        Map<String, Object> result = new HashMap<>();

        // 获取当天数据
        UserCountOrder todayCount = userCountOrderService.selectTeamReportCountList(start, end, userIdList);
        log.info("当天数据:{}", todayCount);
        if (todayCount != null) {
            todayCount.setTotalRechargeAmount(
                    todayCount.getRechargeAmount().add(todayCount.getCustomerRechargeAmount())
            );
            todayCount.setTotalWithdrawalAmount(
                    todayCount.getWithdrawAmount()
                            .add(todayCount.getCustomerRealCashWithdrawalAmount())
                            .add(todayCount.getLawWithdrawAmount())
            );
        } else {
            todayCount = new UserCountOrder();
        }
        result.put("today", todayCount);

        // 获取7天数据
        UserCountOrder sevenCount = userCountOrderService.selectTeamReportCountList(sevenStart, end, userIdList);
        log.info("7天数据:{}", sevenCount);
        if (sevenCount != null) {
            sevenCount.setTotalRechargeAmount(
                    sevenCount.getRechargeAmount().add(sevenCount.getCustomerRechargeAmount())
            );
            sevenCount.setTotalWithdrawalAmount(
                    sevenCount.getWithdrawAmount()
                            .add(sevenCount.getCustomerRealCashWithdrawalAmount())
                            .add(sevenCount.getLawWithdrawAmount())
            );
        } else {
            sevenCount = new UserCountOrder();
        }
        result.put("seven", sevenCount);

        // 获取30天数据
        UserCountOrder thirtyCount = userCountOrderService.selectTeamReportCountList(thirtyStart, end, userIdList);
        log.info("30天数据:{}", thirtyCount);
        if (thirtyCount != null) {
            thirtyCount.setTotalRechargeAmount(
                    thirtyCount.getRechargeAmount().add(thirtyCount.getCustomerRechargeAmount())
            );
            thirtyCount.setTotalWithdrawalAmount(
                    thirtyCount.getWithdrawAmount()
                            .add(thirtyCount.getCustomerRealCashWithdrawalAmount())
                            .add(thirtyCount.getLawWithdrawAmount())
            );
        } else {
            thirtyCount = new UserCountOrder();
        }
        result.put("thirty", thirtyCount);

        return AjaxResult.success(result);
    }

    public AjaxResult teamIntervalList(TUser param) {
        Map<String, Object> result = new HashMap<>();
        // 获取当前用户
        if (Objects.isNull(param.getUserId())) {
            return AjaxResult.error(com.common.core.util.StringUtils.format(MessagesUtils.get("common.param.is.null"), "id"));
        }
        ArrayList<TUser> tUserList = CollectionUtil.newArrayList();
        collectAllSubUsers(param.getUserId(), tUserList);
        if (CollUtil.isEmpty(tUserList)) {
            return AjaxResult.success(result);
        }
        if (null == param.getSearchTimesArr() || param.getSearchTimesArr().length < 2) {
            return AjaxResult.error(com.common.core.util.StringUtils.format(MessagesUtils.get("common.param.is.null"), "searchTimesArr"));
        }

        String[] searchTimesArr = param.getSearchTimesArr();
        String start = searchTimesArr[0] + " 00:00:00";
        String end = searchTimesArr[1] + " 23:59:59";

        List<Long> userIdList = tUserList.stream().map(TUser::getUserId).collect(Collectors.toList());
        result.put("totalNumberOfPeople", userIdList.size());
        UserCountOrder thirtyCount = userCountOrderService.selectTeamReportCountList(start, end, userIdList);
        if (thirtyCount != null) {
            //总存款:totalRechargeAmount
            thirtyCount.setTotalRechargeAmount(
                    (Objects.nonNull(thirtyCount.getRechargeAmount()) ? thirtyCount.getRechargeAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getCustomerRechargeAmount()) ? thirtyCount.getCustomerRechargeAmount() : BigDecimal.ZERO)
            );
            //总提款:totalWithdrawalAmount
            thirtyCount.setTotalWithdrawalAmount(
                    (Objects.nonNull(thirtyCount.getWithdrawAmount()) ? thirtyCount.getWithdrawAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getCustomerRealCashWithdrawalAmount()) ? thirtyCount.getCustomerRealCashWithdrawalAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getLawWithdrawAmount()) ? thirtyCount.getLawWithdrawAmount() : BigDecimal.ZERO));
            //总反水:alreadyRebateAmount
            thirtyCount.setAlreadyRebateAmount(Objects.nonNull(thirtyCount.getAlreadyRebateAmount()) ? thirtyCount.getAlreadyRebateAmount() : BigDecimal.ZERO);

            //总彩金:totalBonusAmount
            thirtyCount.setTotalBonusAmount(
                    (Objects.nonNull(thirtyCount.getBonusAmount()) ? thirtyCount.getBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getPlayWheelTermAward()) ? thirtyCount.getPlayWheelTermAward() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getActivityAwardBonusAmount()) ? thirtyCount.getActivityAwardBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getCustomerBonusAmount()) ? thirtyCount.getCustomerBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getPixBonusAmount()) ? thirtyCount.getPixBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getYndBonusAmount()) ? thirtyCount.getYndBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getUpayBonusAmount()) ? thirtyCount.getUpayBonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getPay1818BonusAmount()) ? thirtyCount.getPay1818BonusAmount() : BigDecimal.ZERO)
                            .add(Objects.nonNull(thirtyCount.getTotalPacketAmount()) ? thirtyCount.getTotalPacketAmount() : BigDecimal.ZERO));

            //总盈亏:customerLossAmount
            thirtyCount.setCustomerLossAmount(Objects.nonNull(thirtyCount.getCustomerLossAmount()) ? thirtyCount.getCustomerLossAmount() : BigDecimal.ZERO);

            //弹珠打码量
            GameBetAmountTotalAmount gameBetAmountTotalAmount = userCountGameCodeService.querySumAmountByUserIdAndTime(userIdList, DateUtils.formatDate(start), DateUtils.formatDate(end));
            thirtyCount.setDZBetAmount(gameBetAmountTotalAmount.getGameTypeCode9());

        } else {
            thirtyCount = new UserCountOrder();
        }
        result.put("interval", thirtyCount);

        return AjaxResult.success(result);
    }

    /**
     * 递归查询所有下级用户
     *
     * @param userId 当前要查询的上级用户ID
     * @return 所有下级用户的列表
     */
    public void collectAllSubUsers(Long userId, List<TUser> result) {
        List<TUser> userList = userService.queryAllTermUser(userId,null,null);
        TUser user = userService.getById(userId);
        result.add(user);
        result.addAll(userList);
    }
}
