package com.gp.common.mybatisplus.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.log.ErrorTelegramUtil;
import com.common.core.result.AjaxResult;
import com.common.core.util.RedisLockUtil;
import com.common.core.util.RedisUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.AccountChangeTypeConstants;
import com.gp.common.base.constant.BaseGameInfoCons;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.utils.BeanCopyUtil;
import com.gp.common.base.utils.BigDecimalUtils;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.mybatisplus.configuration.BotConfiguration;
import com.gp.common.mybatisplus.configuration.ReportTelegramUtil;
import com.gp.common.mybatisplus.deal.DataProcessor;
import com.gp.common.mybatisplus.dto.EsUserAmountSummary;
import com.gp.common.mybatisplus.entity.OrderBetHeard;
import com.gp.common.mybatisplus.entity.OrderTerm;
import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.function.AmountConsumer;
import com.gp.common.mybatisplus.manage.UserAmountChangeManage;
import com.gp.common.mybatisplus.mapper.OrderTermMapper;
import com.gp.common.mybatisplus.param.UserWalletDto;
import com.gp.common.mybatisplus.vo.UserGameBetStatVO;
//import com.gp.common.mybatisplus.vo.UserGameBetStatVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户投注Service业务层处理
 *
 * @author axing
 * @date 2024-05-09
 */
@Service
@Slf4j
public class OrderTermService extends ServiceImpl<OrderTermMapper, OrderTerm> {

    @Autowired
    private OrderTermMapper orderTermMapper;

    @Resource
    private ConfigAmountService configAmountService;

    @Resource
    private UserService userService;

    @Resource
    private RedisLockUtil redisLockUtil;

    @Resource
    private UserAmountChangeManage userAmountChangeManage;

    @Resource
    private ErrorTelegramUtil errorTelegramUtil;
    @Resource
    private CecuUtil cecuUtil;
//    @Resource
//    private EsOrderTermService esOrderTermService;
    @Resource
    private LanguageService languageService;
//    @Resource
//    private ConfigRiskService configRiskService;
    @Resource
    private RedisUtil redisUtil;
//    @Resource
//    private RabbitMqTemplate rabbitMqTemplate;
//    @Resource
//    private ThreadPoolTaskExecutor esThreadPoolTaskExecutor;

//    @Override
//    public boolean save(OrderTerm orderTerm) {
//        if (orderTerm.getCreateTime() == null) {
//            orderTerm.setCreateTime(new Date());
//        }
//        if (orderTerm.getOrderType() == null) {
//            //默认是主注单
//            orderTerm.setOrderType(0);
//        }
//        // 先调用父类的方法保存到数据库
//        boolean result = super.save(orderTerm);
//        // 保存成功后将数据同步到 Elasticsearch
//        if (result) {
//            //发送到mq,异步处理
////            esOrderTermService.save(BeanCopyUtil.copyProperties(orderTerm, EsOrderTermEntity.class), CecuUtil.getDbCode());
//            String dbCode = CecuUtil.getDbCode();
//            orderTerm.setUpdateVersion(EsVersionUtil.generateVersion(1));
//            esThreadPoolTaskExecutor.execute(() -> {
//                //执行下注统计
//                rabbitMqTemplate.sendMq(MqEnum.orderEsMq.getExchange(), MqEnum.orderEsMq.getKey(), MessageBody.builder()
//                        .data(orderTerm)
//                        .productId(dbCode)
//                        .build());
//            });
//        }
//        return result;
//    }

//    public boolean saveAll(List<OrderTerm> orderTerms) {
//        List<OrderTerm> orderTermArrayList = CollUtil.newArrayList();
//
//        for (OrderTerm orderTerm : orderTerms) {
//            if (orderTerm.getCreateTime() == null) {
//                orderTerm.setCreateTime(new Date());
//            }
//            if (orderTerm.getOrderType() == null) {
//                //默认是主注单
//                orderTerm.setOrderType(0);
//            }
//            orderTerm.setUpdateVersion(EsVersionUtil.generateVersion(1));
//            orderTermArrayList.add(orderTerm);
//            // 先调用父类的方法保存到数据库
//
//        }
//        boolean result = super.saveBatch(orderTermArrayList);
        // 保存成功后将数据同步到 Elasticsearch
//        if (result) {
////            esOrderTermService.saveAll(BeanCopyUtil.copyToList(orderTermArrayList, EsOrderTermEntity.class), CecuUtil.getDbCode());
//            String dbCode = CecuUtil.getDbCode();
//            //执行下注统计
//            esThreadPoolTaskExecutor.execute(() -> {
//                rabbitMqTemplate.sendMq(MqEnum.orderListEsMq.getExchange(), MqEnum.orderListEsMq.getKey(), MessageBody.builder()
//                        .data(orderTermArrayList)
//                        .productId(dbCode)
//                        .build());
//            });
//        }
//        return result;
//    }

//    @Override
//    public boolean updateById(OrderTerm orderTerm) {
//        orderTerm.setUpdateTime(new Date());
//        // 先调用父类的方法保存到数据库
//        boolean result = super.updateById(orderTerm);
//        // 保存成功后将数据同步到 Elasticsearch
//        if (result) {
////            esOrderTermService.updateById(BeanCopyUtil.copyProperties(orderTerm, EsOrderTermEntity.class), CecuUtil.getDbCode());
//            orderTerm.setUpdateVersion(EsVersionUtil.generateVersion(2));
//            String dbCode = CecuUtil.getDbCode();
//            //执行下注统计
//            esThreadPoolTaskExecutor.execute(() -> {
//                rabbitMqTemplate.sendMq(MqEnum.orderEsMq.getExchange(), MqEnum.orderEsMq.getKey(), MessageBody.builder()
//                        .data(orderTerm)
//                        .productId(dbCode)
//                        .build());
//            });
//        }
//        return result;
//    }

//    public boolean updateByIdBatch(List<OrderTerm> orderTerms) {
//        ArrayList<OrderTerm> arr = CollUtil.newArrayList();
//
//        for (OrderTerm term : orderTerms) {
//            term.setUpdateTime(new Date());
//            term.setUpdateVersion(EsVersionUtil.generateVersion(2));
//            arr.add(term);
//        }
//
//        // 先调用父类的方法保存到数据库
//        boolean result = super.updateBatchById(arr);
//        // 保存成功后将数据同步到 Elasticsearch
//        if (result) {
////            esOrderTermService.updateAll(BeanCopyUtil.copyToList(arr, EsOrderTermEntity.class), CecuUtil.getDbCode());
//
//            String dbCode = CecuUtil.getDbCode();
//            //执行下注统计
//            esThreadPoolTaskExecutor.execute(() -> {
//                rabbitMqTemplate.sendMq(MqEnum.orderListEsMq.getExchange(), MqEnum.orderListEsMq.getKey(), MessageBody.builder()
//                        .data(orderTerms)
//                        .productId(dbCode)
//                        .build());
//            });
//        }
//        return result;
//    }

//    public boolean updateBatchById(Collection<OrderTerm> orderTerms) {
//        for (OrderTerm orderTerm : orderTerms) {
//            orderTerm.setUpdateTime(new Date());
//            orderTerm.setUpdateVersion(EsVersionUtil.generateVersion(2));
//        }
//        // 先调用父类的方法保存到数据库
//        boolean result = super.updateBatchById(orderTerms);
//        // 保存成功后将数据同步到 Elasticsearch
//        if (result) {
////            esOrderTermService.updateAll(BeanCopyUtil.copyToList(orderTerms, EsOrderTermEntity.class), CecuUtil.getDbCode());
//
//            String dbCode = CecuUtil.getDbCode();
//            //执行下注统计
//            esThreadPoolTaskExecutor.execute(() -> {
//                rabbitMqTemplate.sendMq(MqEnum.orderListEsMq.getExchange(), MqEnum.orderListEsMq.getKey(), MessageBody.builder()
//                        .data(orderTerms)
//                        .productId(dbCode)
//                        .build());
//            });
//        }
//        return result;
//    }

    /**
     * 获取投注总金额
     *
     * @param param
     * @return
     */
    public Map<String, Object> getTodayTotalBetAmount(OrderTerm param) {
        OrderTerm params = verificationTimes(param);
        log.info("时间 : {} ", param.getCreateTimes());
        return orderTermMapper.getTodayTotalBetAmount(params);
    }

    private OrderTerm verificationTime(OrderTerm param) {
        String[] createTimes = param.getCreateTimes();
        if (createTimes == null || createTimes.length < 2) {
            createTimes = new String[2];
            // 获取当前时间
            Date sTime = new Date();
//            Date eTime = DateUtil.offsetDay(sTime, -7);
            String startTime = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, sTime) + " 00:00:00";
            String endTime = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, sTime) + " 23:59:59";
            createTimes[0] = startTime;
            createTimes[1] = endTime;
            param.setCreateTimes(createTimes);
        }
        return param;
    }

    private OrderTerm verificationTimes(OrderTerm param) {
        String[] createTimes = param.getCreateTimes();
        String[] settleTimes = param.getSettleTimes();
        if ((createTimes == null || createTimes.length < 2) && (settleTimes == null || settleTimes.length < 2)) {
            createTimes = new String[2];
            // 获取当前时间
            Date sTime = new Date();
            String startTime = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, sTime) + " 00:00:00";
            String endTime = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, sTime) + " 23:59:59";
            createTimes[0] = startTime;
            createTimes[1] = endTime;
            param.setCreateTimes(createTimes);
        }
        return param;
    }

    /**
     * 获取已经投注结算的总金额
     *
     * @param param
     * @return
     */
    public BigDecimal getTodaySettleBetAmount(OrderTerm param) {
        OrderTerm params = verificationTimes(param);
        return orderTermMapper.getTodaySettleBetAmount(params);
    }

    /**
     * 查询用户投注
     *
     * @param id 用户投注ID
     * @return 用户投注
     */
    public OrderTerm selectOrderTermById(Long id) {
        return orderTermMapper.selectOrderTermById(id);
    }

    /**
     * 查询用户投注
     *
     * @param orderNo 订单号
     * @return 用户投注
     */
    public OrderTerm selectOrderTermByOrderNo(String orderNo) {
        return orderTermMapper.selectOrderTermByOrderNo(orderNo);
    }

    public OrderBetHeard queryBetAmountHeard(Integer gameType, Integer OrderStatus, Long userId, Date startTime, Date endTime) {
        OrderBetHeard orderBetHeard = orderTermMapper.queryBetAmountHeard(gameType, OrderStatus, userId, startTime, endTime);
        if (orderBetHeard == null) {
            orderBetHeard = new OrderBetHeard();
        }
        return orderBetHeard;
    }

    public OrderBetHeard queryBetAmountHeardByTypeCodes(List<Integer> gameTypeCodes, Integer orderStatus, Long userId, Date startTime, Date endTime) {
        OrderBetHeard result = orderTermMapper.queryBetAmountHeardByTypeCodes(gameTypeCodes, orderStatus, userId, startTime, endTime);
        return result != null ? result : new OrderBetHeard();
    }

    public OrderBetHeard queryMyRebateHeard(Long userId, Date startTime, Date endTime) {
        OrderBetHeard orderBetHeard = orderTermMapper.queryMyRebateHeard(userId, startTime, endTime);
        if (orderBetHeard == null) {
            orderBetHeard = new OrderBetHeard();
        }
        return orderBetHeard;
    }

    public void reportWinInfo(Integer time) {
        String dbCode = CecuUtil.getDbCode();
        String defaultLanKey = languageService.getDefaultLanguageWin();
        MessagesUtils.setLang(defaultLanKey);
        //查询近 3分钟的中奖记录大于 设置的值
        Map<Long, TUser> map = map = null;
        StringBuilder s = new StringBuilder();
        ReportTelegramUtil reportTelegramUtil = BotConfiguration.getReportBot(dbCode);
        //取出 arr
        //看看长度吧如果大于10 一次发10条
        String riskListKey = StrUtil.format(RedisKey.winReportListKey, dbCode);
        List<OrderTerm> arr = CollUtil.newArrayList();
        //看看长度吧如果大于10 一次发10条
        long length = redisUtil.lGetListSize(riskListKey);
        if (length < 10) {
            List<Object> objects = redisUtil.lPopMultiple(riskListKey, length);
            arr = objects.stream().map(e -> BeanCopyUtil.copyProperties(e, OrderTerm.class)).collect(Collectors.toList());
        } else {
            List<Object> objects = redisUtil.lPopMultiple(riskListKey, 10l);
            arr = objects.stream().map(e -> BeanCopyUtil.copyProperties(e, OrderTerm.class)).collect(Collectors.toList());
        }
        log.info("发送中奖播报信息, 产品id: {}, size: {}", dbCode, arr.size());
        if (CollectionUtils.isNotEmpty(arr)) {
            //开始拼接中奖信息
            List<Long> userIds = arr.stream()
                    .map(OrderTerm::getUserId)
                    .collect(Collectors.toList());
            List<TUser> userList = userService.list(new LambdaQueryWrapper<TUser>().in(TUser::getUserId, userIds));
            map = userList.stream()
                    .collect(Collectors.toMap(TUser::getUserId, Function.identity()));
            //然后去拼接
            Map<Long, TUser> finalMap = map;
            BigDecimal multipleBig = configAmountService.reportAmountBS();
            arr.stream().forEach(e -> {
                DataProcessor.process(e);
                TUser user = finalMap.get(e.getUserId());
                if (user != null) {

                    //三倍以上播报
                    BigDecimal bet = e.getBetAmount();
                    BigDecimal win = e.getWin();
                    if (bet.compareTo(BigDecimal.ZERO) == 0) {
                        e.setBetAmount(BigDecimal.ONE);
                    }
                    if (win.compareTo(BigDecimal.ZERO) == 0) {
                        e.setWin(BigDecimal.ONE);
                    }
                    //判断一这个使用的是金额还是倍数
                    Boolean b = configAmountService.reportType();
                    String msg = reportTelegramUtil.desensitizeWithLastChar(user);
                    if (b) {
                        BigDecimal reportAmount = configAmountService.reportAmount();
                        if (win.compareTo(reportAmount) >= 0) {
                            s.append(StrUtil.format(MessagesUtils.get("bot.report.win"), msg, e.getGameName(), BigDecimalUtils.trim(e.getWin()), MessagesUtils.get("bot.money.unit"))).append("\n");
                        }
                    } else {
                        if (e.getWin().compareTo(e.getBetAmount().multiply(multipleBig)) >= 0) {
                            //看看多少倍
                            String multiple = calculateOdds(e);
                            s.append(StrUtil.format(MessagesUtils.get("bot.report.winMultiple"), msg, e.getGameName(), multiple)).append("\n");
                        }
                    }
                }

            });
        }
        String sendS = s.toString();
        //商户设置的默认语言

        if (StrUtil.isNotEmpty(sendS)) {
            reportTelegramUtil.sendWinMsg(sendS, defaultLanKey);
        }

    }

    public static String calculateOdds(OrderTerm e) {
        if (e == null) return "0";

        BigDecimal win = e.getWin();
        BigDecimal bet = e.getBetAmount();

        if (win == null || bet == null || bet.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }

        return BigDecimalUtils.trim(win.divide(bet, 3, RoundingMode.DOWN));
    }

    public AjaxResult cancel(Long orderId, String operator) {
        //判断id是否为null
        OrderTerm orderTerm = this.getById(orderId);
        //注单不存在,或者已结算的话不能再处理
        if (orderTerm == null || orderTerm.getSettleStatus().equals(BaseGameInfoCons.TermSettleStatus.已结算)) {
            return AjaxResult.error("注单不存在或者已结算 !");
        }
        //查询该笔注单
        BigDecimal betAmount = orderTerm.getBetAmount();
        Integer accountType = AccountChangeTypeConstants.INCOME;
        //将注单设置取消
        AmountConsumer consumer = (oldUser, newUser) -> {
            orderTerm.setWin(betAmount);
            orderTerm.setWinLoss(BigDecimal.ZERO);
            orderTerm.setCodeAmount(BigDecimal.ZERO);
            orderTerm.setOrderStatus(BaseGameInfoCons.TermOrderStatus.注单取消);
            orderTerm.setSettleStatus(BaseGameInfoCons.TermSettleStatus.已结算);
            orderTerm.setSettleTime(new Date());
            this.updateById(orderTerm);
        };
        //变动游戏方指定的金额
        UserWalletDto newUserWalletDto = userAmountChangeManage.changeSpribeGameBalance(
                orderTerm.getUserId(), CurrencyService.usdtCurrency,
                accountType, betAmount, orderTerm.getOrderNo(), "后台操作-注单取消", operator, null, null,
                consumer
        );
        return AjaxResult.success();

        //通知用户
//        String title = new StringBuilder()
//                .append(EmojiCons.对号).append(" ").append(MessagesUtils.get("bot.notity.ZDQXTX")).toString(); //TODO
//
//        String content = new StringBuilder()
//                .append(MessagesUtils.get("bot.notity.ZDQXCG"))  //TODO
//                .append(" ")
//                .append(BigDecimalUtils.trim(orderTerm.getBetAmount()))
//                .append("\n")
//                .append(MessagesUtils.get("bot.notity.TZDH"))
//                .append(" ")
//                .append(orderTerm.getOrderNo())
//                .append("\n")
//                .append(MessagesUtils.get("bot.notity.DQYE"))
//                .append(" ")
//                .append(BigDecimalUtils.trim(userWalletDto.getNewWallet().getAmount()))
//                .append(" ")
//                .append(userWalletDto.getNewWallet().getCurrency())
//                .toString();

//        SendNotifyVo sendNotifyVo = new SendNotifyVo();
//        sendNotifyVo.setUserTgId(tUser.getUserTgId());
//        sendNotifyVo.setTitle(title);
//        sendNotifyVo.setContent(content);
//        sendNotifyVo.setLanKey(tUser.getLanKey());

    }

    public AjaxResult lose(Long orderId, String operator) {
        OrderTerm orderTerm = this.getById(orderId);
        if (BaseGameInfoCons.TermSettleStatus.已结算.equals(orderTerm.getSettleStatus())) {
            return AjaxResult.error("订单已结算无法修改");
        }
        //体育的返奖是包含投注额的,如果上游没有加投注额,需要自己加上
        orderTerm.setWin(BigDecimal.ZERO);
        BigDecimal winLose = orderTerm.getWin().subtract(orderTerm.getBetAmount());
        orderTerm.setWinLoss(winLose);
        orderTerm.setCodeAmount(winLose.abs());
        orderTerm.setOrderStatus(BaseGameInfoCons.TermOrderStatus.输);
        //输赢相同,是和, 派彩金额大于投注金额是赢, 派彩金额小于投注金额是输;
        orderTerm.setSettleStatus(BaseGameInfoCons.TermSettleStatus.已结算);
        orderTerm.setSettleTime(new Date());
        orderTerm.setUpdateBy(operator);
        this.updateById(orderTerm);
        return AjaxResult.success();
    }

    public AjaxResult win(Long orderId, String operator, BigDecimal win) {
        OrderTerm orderTerm = this.getById(orderId);
        if (BaseGameInfoCons.TermSettleStatus.已结算.equals(orderTerm.getSettleStatus())) {
            return AjaxResult.error("订单已结算无法修改");
        }
        //查询该笔注单
        Integer accountType = AccountChangeTypeConstants.INCOME;
        //将注单设置取消
        AmountConsumer consumer = (oldUser, newUser) -> {
            //体育的返奖是包含投注额的,如果上游没有加投注额,需要自己加上
            orderTerm.setWin(win);
            BigDecimal winLose = orderTerm.getWin().subtract(orderTerm.getBetAmount());
            orderTerm.setWinLoss(winLose);
            orderTerm.setCodeAmount(winLose.abs());
            orderTerm.setOrderStatus(BaseGameInfoCons.TermOrderStatus.赢);
            //输赢相同,是和, 派彩金额大于投注金额是赢, 派彩金额小于投注金额是输;
            orderTerm.setSettleStatus(BaseGameInfoCons.TermSettleStatus.已结算);
            orderTerm.setSettleTime(new Date());
            orderTerm.setUpdateBy(operator);
            this.updateById(orderTerm);
        };
        //变动游戏方指定的金额
        UserWalletDto newUserWalletDto = userAmountChangeManage.changeSpribeGameBalance(
                orderTerm.getUserId(), CurrencyService.usdtCurrency,
                accountType, win, orderTerm.getOrderNo(), "后台操作-注单为赢", operator, null, null,
                consumer
        );

        return AjaxResult.success();
    }

    public List<OrderTerm> selectOrderTermListByChannelId(Long channelId, String threeDayEndTimeStr, String startTime, String endTime) {
        return orderTermMapper.selectOrderTermListByChannelId(channelId, threeDayEndTimeStr, startTime, endTime);
    }

    public List<OrderTerm> selectOrderTermListByChannelIds(String threeDayEndTime, String startTime, String endTime) {
        return orderTermMapper.selectOrderTermListByChannelIds(threeDayEndTime, startTime, endTime);
    }

    public Map<String, Object> fetchOrderTermsBycreatTime(String startTime, String endTime, String plateCode, Integer currencyId) {
        return orderTermMapper.fetchOrderTermsBycreatTime(startTime, endTime, plateCode, currencyId);
    }

    public Map<String, Object> fetchOrderTermsBysettleTime(String startTime, String endTime, String plateCode, Integer currencyId) {
        return orderTermMapper.fetchOrderTermsBysettleTime(startTime, endTime, plateCode, currencyId);
    }

    /**
     * 查询用户投注列表
     *
     * @param param 用户投注
     * @return 用户投注
     */

    public List<OrderTerm> selectOrderTermTodayList(OrderTerm param) {
        return orderTermMapper.selectOrderTermTodayList(param);
    }


    /**
     * 获取没有投注结算的总金额
     *
     * @param param
     * @return
     */
    public BigDecimal getTodayUnsettleBetAmount(OrderTerm param) {
        OrderTerm params = verificationTimes(param);
        return orderTermMapper.getTodayUnsettleBetAmount(params);
    }

    /**
     * 获取没有投注结算的总数
     *
     * @param param
     * @return
     */
    public Integer getTodayUnsettleBetNum(OrderTerm param) {
        OrderTerm params = verificationTime(param);
        return orderTermMapper.getTodayUnsettleBetNum(params);
    }

    /**
     * Es获取没有投注结算的总数
     *
     * @param
     * @return
     */
//    public Map<String, BigDecimal> getTodayUnsettleBetNumByEs(Date startTime, Date endTime) {
//
//        SearchEsTermChange searchEsTermChange = SearchEsTermChange.builder()
//                .startTime(startTime).endTime(endTime)
//                .settleStatus(0)
//                .productId(CecuUtil.getDbCode())
//                .build();
//
//        Map<String, BigDecimal> stringObjectMap = esOrderTermService.orderStatSearch(searchEsTermChange);
//        return null;
//    }

    public List<OrderTerm> getCombinedOrderTermData(List<Long> channelUserIdList, String startTime, String endTime, Long channelId) {
        return orderTermMapper.customQuery(channelUserIdList, startTime, endTime, channelId);
    }

    public Map<String, Object> orderTermCountMap(String startTime, String endTime, Integer currencyId, Long userId) {
        return orderTermMapper.orderTermCountMap(startTime, endTime, currencyId, userId);
    }

    public Map<String, Object> betOrderTermMap(String startTime, String endTime, Integer id, Long channelId) {
        return orderTermMapper.betOrderTermMap(startTime, endTime, id, channelId);
    }

    public Map<String, Object> settleOrderTermMap(String startTime, String endTime, Integer id, Long channelId) {
        return orderTermMapper.settleOrderTermMap(startTime, endTime, id, channelId);
    }

    public OrderTerm queryYetNotRebateOrderTerm(String id) {
        LambdaQueryWrapper<OrderTerm> q = new LambdaQueryWrapper<>();
        q.eq(OrderTerm::getId, id);
        //已结算反水未处理
        //`rebate_status`  反水处理状态(0 未处理, 1 已处理)',
        q.eq(OrderTerm::getRebateStatus, 0);

        //`settle_status` '结算状态(0 未结算, 1 已结算)',
        q.eq(OrderTerm::getSettleStatus, 1);
        return orderTermMapper.selectOne(q);

    }

    public List<OrderTerm> queryYetNotRebateOrderTerms() {
        LambdaQueryWrapper<OrderTerm> q = new LambdaQueryWrapper<>();
        //`order_type`  '订单类型(1 单一注单, 1 复合注单, 2 复合子注单)',
        q.in(OrderTerm::getOrderType, 0, 2);
        //`rebate_status`  反水处理状态(0 未处理, 1 已处理)',
        //order_type
        Date startTimeByDate = DateUtils.getStartTimeByDate(new Date());
        q.le(OrderTerm::getCreateTime, startTimeByDate);
        //分佣为执行
        q.eq(OrderTerm::getRebateStatus, 0);
        //已经结算
        q.eq(OrderTerm::getSettleStatus, 1);
        return orderTermMapper.selectList(q);

    }

    public Long selectOrderTermTodayCount(OrderTerm param) {
        return orderTermMapper.selectOrderTermTodayCount(param);
    }



//    public ESPage<EsOrderTermEntity> listEs(OrderTerm param) {
//        PageDomain pageDomain = TableSupportUtil.buildPageRequest();
//        int pageNum = pageDomain.getPageNum();
//        int pageSize = pageDomain.getPageSize();
//
//        SearchEsTermChange searchEsTermChange = SearchEsTermChange.builder()
//                .plateNameZh(emptyToNull(param.getPlateNameZh()))
//                .gameCode(emptyToNull(param.getGameCode()))
//                .gameNameZh(emptyToNull(param.getGameNameZh()))
//                .gameTypeCode(emptyToNull(param.getGameTypeCode()))
//                .orderNo(emptyToNull(param.getOrderNo()))
//                .upOrderNo(emptyToNull(param.getUpOrderNo()))
//                .upPreOrderNo(emptyToNull(param.getUpPreOrderNo()))
//                .channelId(validLong(param.getChannelId()))
//                .currencyId(validInteger(param.getCurrencyId()))
//                .userId(validLong(param.getUserId()))
//                .tgUserId(validLong(param.getTgUserId()))
//                .orderStatus(validInteger(param.getOrderStatus()))
//                .orderType(validInteger(param.getOrderType()))
//                .superUserId(validLong(param.getSuperUserId()))
//                .superUserTgId(validLong(param.getSuperUserTgId()))
//                .createTimes(validArray(param.getCreateTimes()))
//                .settleTimes(validArray(param.getSettleTimes()))
//                .page(pageNum)
//                .size(pageSize)
//                .sortField("createTime")
//                .sortOrder(SortOrder.DESC)
//                .productId(CecuUtil.getDbCode())
//                .build();
//        log.info("searchEsTermChange:{}", JSONObject.toJSONString(searchEsTermChange));
//        ESPage<EsOrderTermEntity> search = esOrderTermService.searchList(searchEsTermChange);
//        return search;
//    }

    private String emptyToNull(String value) {
        return (value == null || value.trim().isEmpty()) ? null : value;
    }

    private Integer validInteger(Integer value) {
        return (value == null) ? null : value;
    }

    private String[] validArray(String[] array) {
        return (array == null || array.length == 0) ? null : array;
    }

    private Long validLong(Long value) {
        return (value == null) ? null : value;
    }

//    public String exportEs(OrderTerm param, String filePrefix) {
//        SearchEsTermChange searchEsTermChange = SearchEsTermChange.builder()
//                .plateNameZh(emptyToNull(param.getPlateNameZh()))
//                .gameCode(emptyToNull(param.getGameCode()))
//                .gameNameZh(emptyToNull(param.getGameNameZh()))
//                .gameTypeCode(emptyToNull(param.getGameTypeCode()))
//                .orderNo(emptyToNull(param.getOrderNo()))
//                .upOrderNo(emptyToNull(param.getUpOrderNo()))
//                .upPreOrderNo(emptyToNull(param.getUpPreOrderNo()))
//                .channelId(validLong(param.getChannelId()))
//                .currencyId(validInteger(param.getCurrencyId()))
//                .userId(validLong(param.getUserId()))
//                .tgUserId(validLong(param.getTgUserId()))
//                .orderStatus(validInteger(param.getOrderStatus()))
//                .orderType(validInteger(param.getOrderType()))
//                .superUserId(validLong(param.getSuperUserId()))
//                .superUserTgId(validLong(param.getSuperUserTgId()))
//                .createTimes(validArray(param.getCreateTimes()))
//                .settleTimes(validArray(param.getSettleTimes()))
//                .sortField("createTime")
//                .sortOrder(SortOrder.DESC)
//                .productId(CecuUtil.getDbCode())
//                .build();
//        log.info("searchEsTermChange:{}", JSONObject.toJSONString(searchEsTermChange));
//        return esOrderTermService.exportEs(searchEsTermChange, filePrefix);
//    }

//    public long countEs(OrderTerm param) {
//        SearchEsTermChange searchEsTermChange = SearchEsTermChange.builder()
//                .plateNameZh(emptyToNull(param.getPlateNameZh()))
//                .gameCode(emptyToNull(param.getGameCode()))
//                .gameNameZh(emptyToNull(param.getGameNameZh()))
//                .gameTypeCode(emptyToNull(param.getGameTypeCode()))
//                .orderNo(emptyToNull(param.getOrderNo()))
//                .upOrderNo(emptyToNull(param.getUpOrderNo()))
//                .upPreOrderNo(emptyToNull(param.getUpPreOrderNo()))
//                .channelId(validLong(param.getChannelId()))
//                .currencyId(validInteger(param.getCurrencyId()))
//                .userId(validLong(param.getUserId()))
//                .tgUserId(validLong(param.getTgUserId()))
//                .orderStatus(validInteger(param.getOrderStatus()))
//                .orderType(validInteger(param.getOrderType()))
//                .superUserId(validLong(param.getSuperUserId()))
//                .superUserTgId(validLong(param.getSuperUserTgId()))
//                .createTimes(validArray(param.getCreateTimes()))
//                .settleTimes(validArray(param.getSettleTimes()))
//                .sortField("createTime")
//                .sortOrder(SortOrder.DESC)
//                .productId(CecuUtil.getDbCode())
//                .build();
//        log.info("searchEsTermChange:{}", JSONObject.toJSONString(searchEsTermChange));
//        return esOrderTermService.countEs(searchEsTermChange);
//
//    }

//    public Map<String, BigDecimal> sumsEs(OrderTerm param, String... FieldNames) {
//        SearchEsTermChange.SearchEsTermChangeBuilder builder = SearchEsTermChange.builder()
//                .plateNameZh(emptyToNull(param.getPlateNameZh()))
//                .gameCode(emptyToNull(param.getGameCode()))
//                .gameNameZh(emptyToNull(param.getGameNameZh()))
//                .gameTypeCode(emptyToNull(param.getGameTypeCode()))
//                .orderNo(emptyToNull(param.getOrderNo()))
//                .upOrderNo(emptyToNull(param.getUpOrderNo()))
//                .upPreOrderNo(emptyToNull(param.getUpPreOrderNo()))
//                .channelId(validLong(param.getChannelId()))
//                .currencyId(validInteger(param.getCurrencyId()))
//                .userId(validLong(param.getUserId()))
//                .tgUserId(validLong(param.getTgUserId()))
//                .orderStatus(validInteger(param.getOrderStatus()))
//                .orderStatusList(Arrays.asList(0, 1, 2, 3))
//                .superUserId(validLong(param.getSuperUserId()))
//                .superUserTgId(validLong(param.getSuperUserTgId()))
//                .createTimes(validArray(param.getCreateTimes()))
//                .settleTimes(validArray(param.getSettleTimes()))
//                .settleStatus(validInteger(param.getSettleStatus()))
//                .sortField("createTime")
//                .sortOrder(SortOrder.DESC)
//                .productId(CecuUtil.getDbCode());
//        if (null != param.getOrderType()) {
//            builder.orderType(validInteger(param.getOrderType()));
//        } else {
//            builder.orderTypes(Arrays.asList(0, 1));
//        }
//        SearchEsTermChange searchEsTermChange = builder.build();
//        log.info("searchEsTermChange:{}", JSONObject.toJSONString(searchEsTermChange));
//        return esOrderTermService.sumFieldsEs(searchEsTermChange, FieldNames);
//    }

//    public BigDecimal sumEs(OrderTerm param, String FieldNames) {
//        SearchEsTermChange.SearchEsTermChangeBuilder builder = SearchEsTermChange.builder()
//                .plateNameZh(emptyToNull(param.getPlateNameZh()))
//                .gameCode(emptyToNull(param.getGameCode()))
//                .gameNameZh(emptyToNull(param.getGameNameZh()))
//                .gameTypeCode(emptyToNull(param.getGameTypeCode()))
//                .orderNo(emptyToNull(param.getOrderNo()))
//                .upOrderNo(emptyToNull(param.getUpOrderNo()))
//                .upPreOrderNo(emptyToNull(param.getUpPreOrderNo()))
//                .channelId(validLong(param.getChannelId()))
//                .currencyId(validInteger(param.getCurrencyId()))
//                .userId(validLong(param.getUserId()))
//                .tgUserId(validLong(param.getTgUserId()))
//                .orderStatus(validInteger(param.getOrderStatus()))
//                .orderStatusList(Arrays.asList(0, 1, 2, 3))
//                .superUserId(validLong(param.getSuperUserId()))
//                .superUserTgId(validLong(param.getSuperUserTgId()))
//                .createTimes(validArray(param.getCreateTimes()))
//                .settleTimes(validArray(param.getSettleTimes()))
//                .settleStatus(validInteger(param.getSettleStatus()))
//                .sortField("createTime")
//                .sortOrder(SortOrder.DESC)
//                .productId(CecuUtil.getDbCode());
//        if (null != param.getOrderType()) {
//            builder.orderType(validInteger(param.getOrderType()));
//        } else {
//            builder.orderTypes(Arrays.asList(0, 1));
//        }
//        SearchEsTermChange searchEsTermChange = builder.build();
//        log.info("searchEsTermChange:{}", JSONObject.toJSONString(searchEsTermChange));
//        return esOrderTermService.sumFieldEs(searchEsTermChange, FieldNames);
//    }

    public OrderBetHeard queryTodayBetAmount(Long userId, String gameType) {
        HashMap<String, Date> mapToday = DateUtils.changeTimeSection(1);
        Date endTime = mapToday.get("endTime");
        Date startTime = mapToday.get("startTime");

        // null / 空 / "0" / 逗号中含"0" → 不限游戏类型
        if (ActivityAwardReceiveService.isAllGameTypes(gameType)) {
            return this.queryBetAmountHeard(null, null, userId, startTime, endTime);
        }

        // 逗号拼接多个游戏类型：收集 code 列表，一次 IN 查询
        List<Integer> codes = new java.util.ArrayList<>();
        for (String part : gameType.split(",")) {
            String trimmed = part.trim();
            if (StrUtil.isNotEmpty(trimmed)) {
                codes.add(Integer.parseInt(trimmed));
            }
        }
        return this.queryBetAmountHeardByTypeCodes(codes, null, userId, startTime, endTime);
    }



//    public ESPage<EsUserAmountSummary> getDetailedSummary(OrderTerm param) {
//        PageDomain pageDomain = TableSupportUtil.buildPageRequest();
//        int pageNum = pageDomain.getPageNum();
//        int pageSize = pageDomain.getPageSize();
//        SearchEsTermChange.SearchEsTermChangeBuilder builder = SearchEsTermChange.builder()
//                .plateCode(emptyToNull(param.getPlateCode()))
//                .userId(param.getUserId())
//                .gameTypeCode(emptyToNull(param.getTypeCode()))
//                .createTimes(validArray(param.getCreateTimes()))
//                .settleTimes(validArray(param.getSettleTimes()))
//                .page(pageNum)
//                .size(pageSize)
//                .productId(CecuUtil.getDbCode());
//        if (null != param.getOrderType()) {
//            builder.orderType(validInteger(param.getOrderType()));
//        } else {
//            builder.orderTypes(Arrays.asList(0, 1));
//        }
//        return esOrderTermService.getDetailedSummaryWithAggPage(builder.build());
//    }

//    public List<EsUserAmountSummary> getDetailedSummaryList(OrderTerm param) {
//
//        SearchEsTermChange.SearchEsTermChangeBuilder builder = SearchEsTermChange.builder()
//                .plateCode(emptyToNull(param.getPlateCode()))
//                .userId(param.getUserId())
//                .gameTypeCode(emptyToNull(param.getTypeCode()))
//                .createTimes(validArray(param.getCreateTimes()))
//                .settleTimes(validArray(param.getSettleTimes()))
//                .productId(CecuUtil.getDbCode());
//        if (null != param.getOrderType()) {
//            builder.orderType(validInteger(param.getOrderType()));
//        } else {
//            builder.orderTypes(Arrays.asList(0, 1));
//        }
//
//        return esOrderTermService.getDetailedSummary(builder.build());
//    }

    public List<EsUserAmountSummary> getDetailedSummaryByUserId(OrderTerm param) {
        return orderTermMapper.getDetailedSummaryByUserId(param);
    }

    public long getDetailedSummaryByUserIdCount(OrderTerm param) {
        return orderTermMapper.getDetailedSummaryByUserIdCount(param);
    }

    public List<EsUserAmountSummary> getDetailedSummaryByUserIds(OrderTerm param) {
        return orderTermMapper.getDetailedSummaryByUserIds(param);
    }

    /**
     * 获取用户对应的输赢
     */
    public List<EsUserAmountSummary> getWinLossList(OrderTerm param) {
        return orderTermMapper.getWinLossList(param);
    }

    public List<UserGameBetStatVO> selectGameByUserId(OrderTerm param) {
        return orderTermMapper.selectGameByUserId(param);
    }

    public List<UserGameBetStatVO> selectGameDealerByUserId(OrderTerm param) {
        return orderTermMapper.selectGameDealerByUserId(param);
    }

    public List<UserGameBetStatVO> selectGameTypeByUserId(OrderTerm param) {
        return orderTermMapper.selectGameTypeByUserId(param);
    }

//    public List<UserGameBetStatVO> selectGameByUserId(OrderTerm param) {
//        return orderTermMapper.selectGameByUserId(param);
//    }
//
//    public List<UserGameBetStatVO> selectGameDealerByUserId(OrderTerm param) {
//        return orderTermMapper.selectGameDealerByUserId(param);
//    }
//
//    public List<UserGameBetStatVO> selectGameTypeByUserId(OrderTerm param) {
//        return orderTermMapper.selectGameTypeByUserId(param);
//    }
}
