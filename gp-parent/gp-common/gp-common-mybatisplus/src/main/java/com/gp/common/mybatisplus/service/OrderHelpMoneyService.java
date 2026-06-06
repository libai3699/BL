package com.gp.common.mybatisplus.service;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.constant.MqEventTypeConstants;
import com.common.core.constant.OrderConstant;
import com.common.core.constant.UserLotteryConstant;
import com.common.core.enums.AmountChangeTypeEnum;
import com.common.core.enums.ComputeWithdrawBetEnum;
import com.common.core.enums.OrderTypeEnum;
import com.common.core.util.StringUtils;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.*;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.base.utils.SnowIdUtil;
import com.gp.common.mybatisplus.entity.*;
import com.gp.common.mybatisplus.function.ExtConsumer;
import com.gp.common.mybatisplus.manage.UserExtChangeManage;
import com.gp.common.mybatisplus.mapper.OrderHelpMoneyMapper;
import com.gp.common.mybatisplus.mq.HelpMoneyEntity;
import com.gp.common.mybatisplus.mq.OrderEntity;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import com.gp.common.mybatisplus.mqService.MqSendEntityService;
import com.gp.common.mybatisplus.param.ChangeExtValueVo;
import com.gp.common.mybatisplus.until.HelpMoneySecretSign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户救济金申领Service业务层处理
 *
 * @author axing
 * @date 2024-08-30
 */
@Service
@Slf4j
public class OrderHelpMoneyService extends ServiceImpl<OrderHelpMoneyMapper, OrderHelpMoney> {
    @Autowired
    private OrderHelpMoneyMapper orderHelpMoneyMapper;
    @Autowired
    private ActivityAwardService activityAwardService;
    @Autowired
    private HelpMoneySecretSign helpMoneySecretSign;
    @Autowired
    private ActivityService activityService;
    @Resource
    private CecuUtil cecuUtil;
    @Resource
    private ConfigRiskService configRiskService;
    @Resource
    private UserService userService;
    @Resource
    private ComputeWithdrawBetService computeWithdrawBetService;
    @Resource
    private UserExtChangeManage userExtChangeManage;
    @Resource
    private UserLotteryAwardService userLotteryAwardService;
    @Resource
    private MqSendEntityService mqSendEntityService;
    @Resource
    private UserCountGameCodeService userCountGameCodeService;
    @Resource
    private MNacosParam mNacosParam;
    /**
     * 查询用户救济金申领
     *
     * @param id 用户救济金申领ID
     * @return 用户救济金申领
     */

    public OrderHelpMoney selectOrderHelpMoneyById(Integer id) {
        return orderHelpMoneyMapper.selectOrderHelpMoneyById(id);
    }

    /**
     * 查询用户救济金申领列表
     *
     * @param param 用户救济金申领
     * @return 用户救济金申领
     */

    public List<OrderHelpMoney> selectOrderHelpMoneyList(OrderHelpMoney param) {
        return orderHelpMoneyMapper.selectOrderHelpMoneyList(param);
    }

    /**
     * 新增用户救济金申领
     *
     * @param param 用户救济金申领
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertOrderHelpMoney(OrderHelpMoney param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改用户救济金申领
     *
     * @param param 用户救济金申领
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOrderHelpMoney(OrderHelpMoney param) {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除用户救济金申领
     *
     * @param ids 需要删除的用户救济金申领ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrderHelpMoneyByIds(Integer[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除用户救济金申领信息
     *
     * @param id 用户救济金申领ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrderHelpMoneyById(Integer id) {
        boolean result = this.removeById(id);
        return result;
    }

    public void insertHelpMoney(HelpMoneyEntity helpMoneyEntity) {
        this.orderHelpMoneyMapper.insertHelpMoney(helpMoneyEntity);
    }

    /**
     * 查询用户是否领取过救济金 0未领取 1 已领取 2无法领取 (专给救济金用的)
     * private Integer receiveType =2;
     *
     * @ApiModelProperty("昨日亏损") private BigDecimal yesterdayLoss = BigDecimal.ZERO;
     * @ApiModelProperty("今日救济金") private BigDecimal todayHelpMoney = BigDecimal.ZERO;
     */
    public HashMap<String, Object> queryHelpMoneyIsReceive(Long userId, String gameTypeCode, String yesterday, List<ActivityTask> activityTaskList) {
        //如果说没有计算的话 重新计算下  然后返回

        HashMap<String, Object> map = new HashMap<>();

        // t_user_count_game_code 每天凌晨1点才跑完昨天的数据，1点前没有数据不允许查询
        Calendar cal = Calendar.getInstance();
        if (cal.get(Calendar.HOUR_OF_DAY) < 1) {
            map.put("receiveType", 2);
            map.put("yesterdayLoss", BigDecimal.ZERO);
            map.put("todayHelpMoney", BigDecimal.ZERO);
            return map;
        }

        if(gameTypeCode.equals("0")){
            gameTypeCode = "-1";
        }
        // 查询昨日救济金记录（按 dayStr 过滤即可，不再限制 create_time）
        LambdaQueryWrapper<OrderHelpMoney> eq = new LambdaQueryWrapper<OrderHelpMoney>()
                .eq(OrderHelpMoney::getUserId, userId)
                .eq(OrderHelpMoney::getGameTypeCode, gameTypeCode)
                .eq(OrderHelpMoney::getDayStr, yesterday)
                .orderByDesc(OrderHelpMoney::getId)
                .last("LIMIT 1");
        //查询下彩金金额
        LambdaQueryWrapper<OrderHelpMoney> bonusEq = new LambdaQueryWrapper<OrderHelpMoney>()
                .eq(OrderHelpMoney::getUserId, userId)
                .eq(OrderHelpMoney::getGameTypeCode, "0")
                .eq(OrderHelpMoney::getDayStr, yesterday)
                .orderByDesc(OrderHelpMoney::getId)
                .last("LIMIT 1");
        OrderHelpMoney orderHelpMoney = this.orderHelpMoneyMapper.selectOne(eq);
        if (orderHelpMoney == null) {
            // 没有记录，从 t_user_count_game_code 实时补数
            orderHelpMoney = buildOrderHelpMoneyFromGameCode(userId, gameTypeCode, yesterday);
            if (orderHelpMoney == null) {
                map.put("receiveType", 2);
                map.put("yesterdayLoss", BigDecimal.ZERO);
                map.put("todayHelpMoney", BigDecimal.ZERO);
                return map;
            }
        } else if (orderHelpMoney.getCalMoney().compareTo(BigDecimal.ZERO) == 0 && orderHelpMoney.getAwardId() == null && orderHelpMoney.getIsReceive() == 0) {
            // 记录存在但未计算过，可能是旧 MQ 写入的不完整数据（只有半天），
            // 从 t_user_count_game_code 刷新 bet/win 确保数据完整
            OrderHelpMoney refreshed = buildOrderHelpMoneyFromGameCode(userId, gameTypeCode, yesterday);
            if (refreshed != null) {
                orderHelpMoney = refreshed;
            }
        }
        OrderHelpMoney orderHelpMoneyBonus = this.orderHelpMoneyMapper.selectOne(bonusEq);
        BigDecimal bonusMoney = orderHelpMoneyBonus==null?BigDecimal.ZERO:orderHelpMoneyBonus.getBonusMoney();

        if (orderHelpMoney.getIsReceive() == 1) {
            map.put("receiveType", 1);
            map.put("yesterdayLoss", orderHelpMoney.getCalMoney());
            map.put("todayHelpMoney", orderHelpMoney.getReceiveMoney());
            return map;
        }
        if (orderHelpMoney.getCalMoney().compareTo(BigDecimal.ZERO) == 0&& orderHelpMoney.getAwardId()==null) {
            HashMap<String, BigDecimal> mapCal = calculateHelpMoney(orderHelpMoney,bonusMoney, activityTaskList);
            map.put("receiveType",  mapCal.get("receiveMoney").compareTo(BigDecimal.ZERO) > 0? 0 : 2);
            map.put("yesterdayLoss", mapCal.get("calMoney"));
            map.put("todayHelpMoney", mapCal.get("receiveMoney"));
            return map;
        } else {
            map.put("receiveType", orderHelpMoney.getReceiveMoney().compareTo(BigDecimal.ZERO) > 0? 0 : 2);
            map.put("yesterdayLoss", orderHelpMoney.getCalMoney());
            map.put("todayHelpMoney", orderHelpMoney.getReceiveMoney());
        }
        return map;
        //剩下就是未领取的了
        //证明是没有经过定时并且可以适当触发下结算

    }

    /**
     * 这个吧活动任务传过来 去比较下 他属于哪一个档位然后去计算金额
     *
     * @param orderHelpMoney
     * @param activityTaskList
     * @return
     */
    private HashMap<String, BigDecimal> calculateHelpMoney(OrderHelpMoney orderHelpMoney, BigDecimal bonusMoney, List<ActivityTask> activityTaskList) {
        HashMap<String, BigDecimal> map = new HashMap<>();
        log.info("开始计算救济金{}", JSON.toJSONString(orderHelpMoney));
        //看看他满足那个档位
        //TODO 这里需要调用结算接口 然后更新数据库 现在看下 这个数据 看看 计算下金额
        BigDecimal betMoney = orderHelpMoney.getBetMoney();
        BigDecimal winMoney = orderHelpMoney.getWinMoney();
        //这个就是亏损金额
            BigDecimal calMoney = betMoney.subtract(winMoney).subtract(bonusMoney);
        if (calMoney.compareTo(BigDecimal.ZERO) <= 0) {
            map.put("receiveMoney", BigDecimal.ZERO);
            map.put("calMoney", BigDecimal.ZERO);
            map.put("calRatio", BigDecimal.ZERO);
            orderHelpMoney.setCalMoney(calMoney);
            orderHelpMoney.setCalRatio(BigDecimal.ZERO);
            orderHelpMoney.setAwardId(0l);
            orderHelpMoney.setReceiveMoney(BigDecimal.ZERO);
            helpMoneySecretSign.dealSign(orderHelpMoney);
            this.orderHelpMoneyMapper.updateById(orderHelpMoney);
        } else {
            //这里需要计算下  计算比例
            Optional<ActivityTask> lastGreaterThan = findMaxLessThanTarget(activityTaskList, calMoney);
            log.info("activityTaskList{}", JSON.toJSONString(activityTaskList));
            log.info("获取到的信息为{}", lastGreaterThan.isPresent());
            if (lastGreaterThan.isPresent()) {
                //这里就是找到了  然后计算下  然后更新数据库
                ActivityTask activityTask = lastGreaterThan.get();
                //找到奖励 按照金额去计算
                List<ActivityAward> arr = activityAwardService.queryActivityTaskAward(activityTask.getId());
                //如果说投注钱大于奖励的金额
                if (CollUtil.isEmpty(arr)) {
                    map.put("receiveMoney", BigDecimal.ZERO);
                    map.put("calMoney", calMoney);
                    map.put("calRatio", BigDecimal.ZERO);
                    orderHelpMoney.setCalMoney(calMoney);
                    orderHelpMoney.setCalRatio(BigDecimal.ZERO);
                    orderHelpMoney.setAwardId(0l);
                    orderHelpMoney.setReceiveMoney(BigDecimal.ZERO);
                    helpMoneySecretSign.dealSign(orderHelpMoney);
                    this.orderHelpMoneyMapper.updateById(orderHelpMoney);
                    return map;
                }

                ActivityAward activityAward = arr.get(0);
                Integer isFixed = activityAward.getIsFixed();
                BigDecimal calRatio = BigDecimal.ONE;
                BigDecimal receiveMoney = calMoney;
                //看看类型 是固定还是按照比例
                if (isFixed == 1) {
                    //这里就是固定金额
                    calRatio = activityAward.getRatio();
                    receiveMoney = calRatio.multiply(calMoney).setScale(6, BigDecimal.ROUND_DOWN);
                    //看下这个金额是否大于 封顶金额
                    BigDecimal amountCap = activityAward.getAmountCap();
                    if(amountCap!= null && amountCap.compareTo(BigDecimal.ZERO) > 0 && receiveMoney.compareTo(amountCap) > 0){
                        receiveMoney = amountCap;
                    }
                }else {
                    receiveMoney = activityAward.getAward();
                }
                //这里就是计算比例
                //这里就是计算金额
                //这里就是更新数据库
                orderHelpMoney.setCalMoney(calMoney);
                orderHelpMoney.setCalRatio(calRatio);
                orderHelpMoney.setAwardId(activityAward.getId());
                orderHelpMoney.setReceiveMoney(receiveMoney);
                helpMoneySecretSign.dealSign(orderHelpMoney);
                this.orderHelpMoneyMapper.updateById(orderHelpMoney);
                map.put("receiveMoney", receiveMoney);
                map.put("calMoney", calMoney);
                map.put("calRatio", calRatio);
            } else {
                //不存在的话页给他设置下
                orderHelpMoney.setCalMoney(calMoney);
                orderHelpMoney.setCalRatio(BigDecimal.ZERO);
                orderHelpMoney.setAwardId(0l);
                orderHelpMoney.setReceiveMoney(BigDecimal.ZERO);
                helpMoneySecretSign.dealSign(orderHelpMoney);
                this.orderHelpMoneyMapper.updateById(orderHelpMoney);
                map.put("receiveMoney", BigDecimal.ZERO);
                map.put("calMoney", calMoney);
                map.put("calRatio", BigDecimal.ZERO);
            }
        }
        return map;
    }

    public static void main(String[] args) {
      String str ="0.02:3,0.01:3,0.01:3,0.01:3";
        String[] split = str.split(",");
        for (String s : split) {
            String[] split1 = s.split(":");
            BigDecimal bigDecimal = new BigDecimal(split1[0]);
            BigDecimal decimal = new BigDecimal(split1[1]);
            System.out.println(bigDecimal);
            System.out.println(decimal);
            int i = Integer.parseInt(split1[1]);
        }

    }

    public static Optional<ActivityTask> findMaxLessThanTarget(List<ActivityTask> list, BigDecimal target) {
        if (list.isEmpty()) {
            return Optional.empty(); // 列表为空
        }

        // 对列表进行排序（默认升序）
        List<ActivityTask> sortedList = new ArrayList<>(list);
        sortedList.sort((a, b) -> a.getTaskAmount().compareTo(b.getTaskAmount()));

        // 初始化一个布尔值，用于标记是否找到了大于等于目标值的元素
        boolean foundGreaterOrEqual = false;
        ActivityTask result = null;

        for (int i = sortedList.size() - 1; i >= 0; i--) {
            ActivityTask current = sortedList.get(i);
            if (current.getTaskAmount().compareTo(target) < 0) {
                result = current;
                break;
            }
        }

        return Optional.ofNullable(result);
    }

public OrderHelpMoney queryOrderHelpMoney(Long userId, String gameTypeCode, String dayStr) {
        LambdaQueryWrapper eq = new LambdaQueryWrapper<OrderHelpMoney>()
                .eq(OrderHelpMoney::getUserId, userId).eq(OrderHelpMoney::getGameTypeCode, gameTypeCode).eq(OrderHelpMoney::getDayStr, dayStr).eq(OrderHelpMoney::getIsReceive, 0);
        return this.orderHelpMoneyMapper.selectOne(eq);
    }

    public void computerHelpMoney() {
        String yesterday = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, DateUtils.addDateDays(new Date(), -1));
        //查询今昨日的数据
        List<OrderHelpMoney> list = this.orderHelpMoneyMapper.selectList(new LambdaQueryWrapper<OrderHelpMoney>()
                .eq(OrderHelpMoney::getIsReceive, 0).ne(OrderHelpMoney::getGameTypeCode, "0").eq(OrderHelpMoney::getDayStr, yesterday));
        //查询下彩金金额

        //这里没次都查任务列表有点不合适干脆给他根据 gameTypeCOde 进行分组
        list.stream().collect(Collectors.groupingBy(OrderHelpMoney::getGameTypeCode)).forEach((gameTypeCode, orderHelpMoneyList) -> {
            //这里就是根据每个游戏类型去计算
            //先查出所有的任务列表
            //查询游戏类型对应的任务列表 和比例
            //这里如果救济中游戏类型是-1 的 改成通用
            if(gameTypeCode.equals("-1")){
                gameTypeCode = "0";
            }
            List<ActivityTask> activityTaskList = activityService.queryHelpMoneyActivityTask(gameTypeCode);
            for (OrderHelpMoney orderHelpMoney : orderHelpMoneyList) {
                log.info("开始计算游戏类型为{}的救济金", gameTypeCode);
                //这里就是根据每个游戏类型去计算
                //先查出所有的任务列表
                BigDecimal winMoney = orderHelpMoney.getWinMoney();
                BigDecimal betMoney = orderHelpMoney.getBetMoney();
                LambdaQueryWrapper<OrderHelpMoney> bonusEq = new LambdaQueryWrapper<OrderHelpMoney>().eq(OrderHelpMoney::getUserId, orderHelpMoney.getUserId()).eq(OrderHelpMoney::getGameTypeCode, "0").eq(OrderHelpMoney::getDayStr, yesterday);
                OrderHelpMoney orderHelpMoneyBonus = this.orderHelpMoneyMapper.selectOne(bonusEq);
                BigDecimal bonusMoney = orderHelpMoneyBonus==null?BigDecimal.ZERO:orderHelpMoneyBonus.getBonusMoney();
                BigDecimal calMoney = betMoney.subtract(winMoney).subtract(bonusMoney);
                if (calMoney.compareTo(BigDecimal.ZERO) < 0) {
                    //calMoney  然后更新数据库
                    orderHelpMoney.setCalMoney(calMoney);
                    orderHelpMoney.setCalRatio(BigDecimal.ZERO);
                    orderHelpMoney.setAwardId(0l);
                    orderHelpMoney.setReceiveMoney(BigDecimal.ZERO);
                    helpMoneySecretSign.dealSign(orderHelpMoney);
                    this.orderHelpMoneyMapper.updateById(orderHelpMoney);
                    continue;
                }
                Optional<ActivityTask> lastGreaterThan = findMaxLessThanTarget(activityTaskList, calMoney);
                if (lastGreaterThan.isPresent()) {
                    //这里就是找到了  然后计算下  然后更新数据库
                    ActivityTask activityTask = lastGreaterThan.get();
                    //找到奖励 按照金额去计算
                    List<ActivityAward> arr = activityAwardService.queryActivityTaskAward(activityTask.getId());
                    if (CollUtil.isEmpty(arr)) {
                        return;
                    }
                    ActivityAward activityAward = arr.get(0);
                    Integer isFixed = activityAward.getIsFixed();
                    BigDecimal calRatio = BigDecimal.ZERO;
                    BigDecimal receiveMoney;
                    //看看类型 是固定还是按照比例
                    if (isFixed == 1) {
                        //这里就是固定金额
                        calRatio = activityAward.getRatio();
                        receiveMoney = calRatio.multiply(calMoney).setScale(6, BigDecimal.ROUND_DOWN);
                        //看下这个金额是否大于 封顶金额
                        BigDecimal amountCap = activityAward.getAmountCap();
                        if(amountCap!= null && amountCap.compareTo(BigDecimal.ZERO) > 0 && receiveMoney.compareTo(amountCap) > 0){
                            receiveMoney = amountCap;
                        }
                    }else {
                        receiveMoney = activityAward.getAward();
                    }
                    //这里就是计算比例
                    //这里就是计算金额
                    //这里就是更新数据库
                    orderHelpMoney.setCalMoney(calMoney);
                    orderHelpMoney.setCalRatio(calRatio);
                    orderHelpMoney.setAwardId(activityAward.getId());
                    orderHelpMoney.setReceiveMoney(receiveMoney);
                    helpMoneySecretSign.dealSign(orderHelpMoney);
                    this.orderHelpMoneyMapper.updateById(orderHelpMoney);

                }
            }

        });

    }

    private HelpMoneyEntity buildHelpMoneyEntity(TUser user, String dayStr, String gameTypeCode,
                                                 BigDecimal betMoney, BigDecimal winMoney) {
        return HelpMoneyEntity.builder()
                .userId(user.getUserId())
                .tgUserId(user.getUserTgId())
                .dayStr(dayStr)
                .gameTypeCode(gameTypeCode)
                .betMoney(safeAmount(betMoney))
                .winMoney(safeAmount(winMoney))
                .bonusMoney(BigDecimal.ZERO)
                .calMoney(BigDecimal.ZERO)
                .calRatio(BigDecimal.ZERO)
                .receiveMoney(BigDecimal.ZERO)
                .isReceive(0)
                .signTime(System.currentTimeMillis())
                .signSecretKey(mNacosParam.getSignSecretKey())
                .build();
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    /**
     * 查不到 t_order_help_money 记录时，实时从 t_user_count_game_code 补数并写入
     * 返回新写入的 OrderHelpMoney，若用户当天无投注数据则返回 null
     *
     * gameTypeCode 支持以下格式：
     *   "-1"      — 通用救济金，汇总所有游戏类型
     *   "1"       — 单个游戏类型
     *   "1,3,5"   — 多个游戏类型，bet/win 累加
     */
    private OrderHelpMoney buildOrderHelpMoneyFromGameCode(Long userId, String gameTypeCode, String dayStr) {
        LambdaQueryWrapper<UserCountGameCode> q = new LambdaQueryWrapper<UserCountGameCode>()
                .eq(UserCountGameCode::getUserId, userId)
                .eq(UserCountGameCode::getDayStr, dayStr);
        UserCountGameCode stat = userCountGameCodeService.getOne(q);
        if (stat == null) {
            return null;
        }
        TUser user = userService.getById(userId);
        if (user == null) {
            return null;
        }

        BigDecimal betMoney;
        BigDecimal winMoney;

        if ("-1".equals(gameTypeCode)) {
            // 通用救济金：汇总所有游戏类型
            betMoney = safeAmount(stat.getGameTypeBet1()).add(safeAmount(stat.getGameTypeBet2()))
                    .add(safeAmount(stat.getGameTypeBet3())).add(safeAmount(stat.getGameTypeBet4()))
                    .add(safeAmount(stat.getGameTypeBet5())).add(safeAmount(stat.getGameTypeBet6()))
                    .add(safeAmount(stat.getGameTypeBet7())).add(safeAmount(stat.getGameTypeBet8()))
                    .add(safeAmount(stat.getGameTypeBet9()));
            winMoney = safeAmount(stat.getGameTypeSettle1()).add(safeAmount(stat.getGameTypeSettle2()))
                    .add(safeAmount(stat.getGameTypeSettle3())).add(safeAmount(stat.getGameTypeSettle4()))
                    .add(safeAmount(stat.getGameTypeSettle5())).add(safeAmount(stat.getGameTypeSettle6()))
                    .add(safeAmount(stat.getGameTypeSettle7())).add(safeAmount(stat.getGameTypeSettle8()))
                    .add(safeAmount(stat.getGameTypeSettle9()));
        } else if (gameTypeCode.contains(",")) {
            // 多游戏类型：按逗号分割后累加各自的 bet/win
            betMoney = BigDecimal.ZERO;
            winMoney = BigDecimal.ZERO;
            String[] codes = gameTypeCode.split(",");
            for (String code : codes) {
                String trimmed = code.trim();
                betMoney = betMoney.add(getGameTypeBetByCode(stat, trimmed));
                winMoney = winMoney.add(getGameTypeSettleByCode(stat, trimmed));
            }
        } else {
            // 单个游戏类型
            betMoney = getGameTypeBetByCode(stat, gameTypeCode);
            winMoney = getGameTypeSettleByCode(stat, gameTypeCode);
        }

        if (betMoney.compareTo(BigDecimal.ZERO) == 0 && winMoney.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        HelpMoneyEntity helpMoneyEntity = buildHelpMoneyEntity(user, dayStr, gameTypeCode, betMoney, winMoney);
        try {
            orderHelpMoneyMapper.upsertOrderHelpMoneyByCount(helpMoneyEntity);
        } catch (Exception e) {
            log.warn("实时补写救济金注单数据失败 userId={} day={} gameTypeCode={}", userId, dayStr, gameTypeCode, e);
        }

        LambdaQueryWrapper<OrderHelpMoney> reQuery = new LambdaQueryWrapper<OrderHelpMoney>()
                .eq(OrderHelpMoney::getUserId, userId)
                .eq(OrderHelpMoney::getGameTypeCode, gameTypeCode)
                .eq(OrderHelpMoney::getDayStr, dayStr);
        return this.orderHelpMoneyMapper.selectOne(reQuery);
    }

    private BigDecimal getGameTypeBetByCode(UserCountGameCode stat, String gameTypeCode) {
        switch (gameTypeCode) {
            case "1": return safeAmount(stat.getGameTypeBet1());
            case "2": return safeAmount(stat.getGameTypeBet2());
            case "3": return safeAmount(stat.getGameTypeBet3());
            case "4": return safeAmount(stat.getGameTypeBet4());
            case "5": return safeAmount(stat.getGameTypeBet5());
            case "6": return safeAmount(stat.getGameTypeBet6());
            case "7": return safeAmount(stat.getGameTypeBet7());
            case "8": return safeAmount(stat.getGameTypeBet8());
            case "9": return safeAmount(stat.getGameTypeBet9());
            default:  return BigDecimal.ZERO;
        }
    }

    private BigDecimal getGameTypeSettleByCode(UserCountGameCode stat, String gameTypeCode) {
        switch (gameTypeCode) {
            case "1": return safeAmount(stat.getGameTypeSettle1());
            case "2": return safeAmount(stat.getGameTypeSettle2());
            case "3": return safeAmount(stat.getGameTypeSettle3());
            case "4": return safeAmount(stat.getGameTypeSettle4());
            case "5": return safeAmount(stat.getGameTypeSettle5());
            case "6": return safeAmount(stat.getGameTypeSettle6());
            case "7": return safeAmount(stat.getGameTypeSettle7());
            case "8": return safeAmount(stat.getGameTypeSettle8());
            case "9": return safeAmount(stat.getGameTypeSettle9());
            default:  return BigDecimal.ZERO;
        }
    }

    public void lotteryLoss(String product, String day) {
        List<String> dbNameList = cecuUtil.getCecuProp().getAppList();
        if (StringUtils.isNotEmpty(product)) {
            dbNameList = CollUtil.newArrayList(product);
        }

        log.info("lotteryLoss传递进来的day:{},product:{}", day, product);
        for (String productCode : dbNameList) {
            try {
                cecuUtil.cutDbByCode(productCode);
                Activity activity = activityService.queryActivityByType(ActivityConstants.LOTTERY_HELPMONEY);
                if(activity==null||activity.getStatus()==0){
                    log.info("没有开启彩票救济金活动");
                    continue;
                }
                //往下走
                Date yesterdayDate = DateUtils.addDateDays(new Date(), -1);
                if (StringUtils.isNotEmpty(day)) {
                    yesterdayDate = DateUtils.parseToDate(day, DateUtils.YYYY_MM_DD);
                }
                String yesterday = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, yesterdayDate);
                List<OrderHelpMoneySum> orderHelpMoneySums = orderHelpMoneyMapper.querySumByDate(yesterday);
                String ratios = configRiskService.lotteryRechargeRatio();
                if(ratios==null){
                    log.warn("产品{}的彩票充值比例配置为空，跳过处理", productCode);
                    continue;
                }
                log.info("orderHelpMoneySums.size:{}",orderHelpMoneySums.size());
                for (OrderHelpMoneySum orderHelpMoneySum : orderHelpMoneySums) {
                    try {
                        // 抽成独立方法处理单个用户
                        processUserLotteryLoss(orderHelpMoneySum, ratios, productCode);
                    } catch (Exception e) {
                        log.error("处理用户{}的彩票救济金时发生异常，产品: {}, 错误: {}", 
                                orderHelpMoneySum.getUserId(), productCode, e.getMessage(), e);
                        // 继续处理下一个用户
                    }
                }
            } catch (Exception e) {
                log.error("处理产品{}的彩票救济金时发生异常，错误: {}", productCode, e.getMessage(), e);
                // 继续处理下一个产品
            }
        }
    }

    /**
     * 处理单个用户的彩票救济金
     */
    private void processUserLotteryLoss(OrderHelpMoneySum orderHelpMoneySum, String ratios, String productCode) {
        Long fromUserId = orderHelpMoneySum.getUserId();
        BigDecimal fromAmount = orderHelpMoneySum.getTotalBonusMoney().add(orderHelpMoneySum.getTotalWinMoney()).subtract(orderHelpMoneySum.getTotalBetMoney());
        log.info("开始计算用户{}的彩票救济金fromAmount{}", fromUserId, fromAmount);
        
        if(fromAmount.compareTo(BigDecimal.ZERO)>=0){
            return; // 用户没有亏损，不需要救济金
        }
        
        fromAmount = fromAmount.abs();
        String[] split = ratios.split(",");
        TUser fromUser = userService.getById(fromUserId);
        if (fromUser == null) {
            log.warn("用户{}不存在，跳过处理", fromUserId);
            return;
        }
        
        for (String s : split) {
            try {
                // 处理单个比例配置，并返回更新后的用户信息
                fromUser = processSingleRatioConfig(s, fromUser, fromAmount, fromUserId, productCode);
                if (fromUser == null) {
                    // 如果返回null，说明上级用户不存在，结束当前用户的所有比例配置处理
                    break;
                }
            } catch (Exception e) {
                log.error("处理用户{}的比例配置{}时发生异常，产品: {}, 错误: {}", 
                        fromUserId, s, productCode, e.getMessage(), e);
                // 继续处理下一个比例配置
            }
        }
    }

    /**
     * 处理单个比例配置的救济金发放
     * @return 返回上级用户信息，如果上级用户不存在则返回null
     */
    private TUser processSingleRatioConfig(String s, TUser fromUser, BigDecimal fromAmount, Long fromUserId, String productCode) {
        try {
            //一级一级的往上查配置几个查几个
            Long toUserId = fromUser.getSuperUserId();
            Long toChannelId = fromUser.getChannelId();
            if(toUserId==null||toUserId==0){
                return null; // 没有上级用户，返回null
            }
            
            String[] arrOne = s.split(":");
            if (arrOne.length < 2) {
                log.warn("比例配置格式错误: {}, 跳过处理", s);
                return null;
            }
            
            BigDecimal withdrawBonusRatio = BigDecimal.TEN;
            String ratioStr = arrOne[0];
            withdrawBonusRatio = new BigDecimal(arrOne[1]);
            BigDecimal ratio = new BigDecimal(ratioStr);
            
            TUser toUser = userService.getById(toUserId);
            if(toUser==null){
                log.warn("上级用户{}不存在，跳过处理", toUserId);
                return null; // 上级用户不存在，返回null
            }
            
            String userTgName = toUser.getUserTgName();
            Long userTgId = toUser.getUserTgId();
            BigDecimal award = fromAmount.multiply(ratio).setScale(2, BigDecimal.ROUND_DOWN);
            if(award.compareTo(BigDecimal.ZERO)<=0){
                return toUser; // 奖励金额小于等于0，但继续处理上级用户
            }
            
            // 创建救济金记录
            UserLotteryAward userLotteryAward = new UserLotteryAward();
            String orderNo = SnowIdUtil.getId(OrderConstant.lotteryAward);
            BigDecimal finalFromAmount = fromAmount;
            ExtConsumer extConsumer = () -> {
                userLotteryAward.setUserId(toUserId);
                userLotteryAward.setChannelId(toChannelId);
                userLotteryAward.setType(UserLotteryConstant.recharge);
                userLotteryAward.setOrderNo(orderNo);
                userLotteryAward.setAward(award);
                userLotteryAward.setRatio(ratio);
                userLotteryAward.setFromUserId(fromUserId);
                userLotteryAward.setFromAmount(finalFromAmount);
                userLotteryAwardService.save(userLotteryAward);
            };
            
            //todo 增加统计数据 t_count_order/t_user_count_order/t_channel_count_order
            String dbCode = CecuUtil.getDbCode();
            userService.judgeUserScore(award, dbCode);
            
            // 生成领取记录
            List<ChangeExtValueVo> arr = CollUtil.newArrayList();
            ChangeExtValueVo changeExtValueVoBonus = new ChangeExtValueVo();
            changeExtValueVoBonus.setUserId(toUserId);
            changeExtValueVoBonus.setExtType(UserExtTypeCons.彩金);
            changeExtValueVoBonus.setUpdateValue(award);
            changeExtValueVoBonus.setOrderNo(orderNo);
            changeExtValueVoBonus.setOrderType(BaseGameInfoCons.UserExtOrderType.活动订单);
            changeExtValueVoBonus.setAccountType(AccountChangeTypeConstants.INCOME);
            changeExtValueVoBonus.setChangeType(BaseGameInfoCons.UserExtChangeType.彩金增加);
            changeExtValueVoBonus.setOperator(userTgName);
            
            //彩金细分
            ChangeExtValueVo changeExtValueVoBonusSmall = new ChangeExtValueVo();
            changeExtValueVoBonusSmall.setUserId(toUserId);
            changeExtValueVoBonusSmall.setExtType(UserExtTypeCons.活动彩金);
            changeExtValueVoBonusSmall.setUpdateValue(award);
            changeExtValueVoBonusSmall.setOrderNo(orderNo);
            changeExtValueVoBonusSmall.setOrderType(BaseGameInfoCons.UserExtOrderType.活动订单);
            changeExtValueVoBonusSmall.setAccountType(AccountChangeTypeConstants.INCOME);
            changeExtValueVoBonusSmall.setChangeType(BaseGameInfoCons.UserExtChangeType.彩金增加);
            changeExtValueVoBonusSmall.setOperator(userTgName);
            arr.add(changeExtValueVoBonusSmall);
            arr.add(changeExtValueVoBonus);
            
            ChangeExtValueVo changeExtValueVoStatement = new ChangeExtValueVo();
            changeExtValueVoStatement.setUserId(toUserId);
            changeExtValueVoStatement.setExtType(UserExtTypeCons.提现打码量);
            changeExtValueVoStatement.setUpdateValue(computeWithdrawBetService.computeRedPacketService(award, withdrawBonusRatio));
            changeExtValueVoStatement.setOrderNo(orderNo);
            changeExtValueVoStatement.setOrderType(BaseGameInfoCons.UserExtOrderType.活动订单);
            changeExtValueVoStatement.setAccountType(AccountChangeTypeConstants.INCOME);
            changeExtValueVoStatement.setChangeType(BaseGameInfoCons.UserExtChangeType.提现打码量增加);
            changeExtValueVoStatement.setOperator(userTgName);
            arr.add(changeExtValueVoStatement);
            
            userExtChangeManage.changeExtOrAmount(arr, extConsumer, AmountChangeTypeEnum.taskAward, OrderTypeEnum.lossAward, true, 1, award);
            
            //发送商户积分扣减
            userService.sendJudgeUserScoreMq(award, dbCode, MerchantChangeTypeConstants.ACTIVITY_REWARD, MerchantOrderTypeConstant.ACTIVITY_REWARD, "活动奖励");
            
            mqSendEntityService.sendOrderEntity(OrderEntity.builder().userId(toUserId).channelId(toChannelId)
                    .type(MqEventTypeConstants.LOTTERY_AWARD).bonusAmount(award).orderNo(orderNo)
                    .productId(dbCode)
                    .build());

            //发送救济金mq计算赠送金额
            mqSendEntityService.sendHelpMoneyEntity(HelpMoneyEntity.builder().userId(toUserId).tgUserId(userTgId)
                    .betMoney(BigDecimal.ZERO).winMoney(BigDecimal.ZERO).bonusMoney(award).gameTypeCode("0").dayStr(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, new Date()))
                    .productId(dbCode)
                    .build());
            
            // 返回上级用户，用于下一次循环处理
            return toUser;
            
        } catch (Exception e) {
            log.error("处理比例配置{}时发生异常，用户: {}, 产品: {}, 错误: {}", 
                    s, fromUserId, productCode, e.getMessage(), e);
            return null; // 发生异常时返回null，结束当前用户的比例配置处理
        }
    }


}
