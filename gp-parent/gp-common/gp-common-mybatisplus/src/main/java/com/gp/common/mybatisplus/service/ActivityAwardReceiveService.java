package com.gp.common.mybatisplus.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.util.RedisUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.ActivityConstants;
import com.gp.common.base.constant.ActivityTypeConstants;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.constant.UserExtTypeCons;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.Activity;
import com.gp.common.mybatisplus.entity.ActivityAwardReceive;
import com.gp.common.mybatisplus.entity.ActivityTask;
import com.gp.common.mybatisplus.entity.UserExt;
import com.gp.common.mybatisplus.mapper.ActivityAwardReceiveMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 活动奖励领取Service业务层处理
 *
 * @author axing
 * @date 2024-06-12
 */
@Service
@Slf4j
public class ActivityAwardReceiveService extends ServiceImpl<ActivityAwardReceiveMapper, ActivityAwardReceive> {

    @Autowired
    private ActivityAwardReceiveMapper activityAwardReceiveMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ConfigRiskService configRiskService;
    @Resource
    public UserExtService userExtService;

    // ===================== CRUD =====================

    public ActivityAwardReceive selectActivityAwardReceiveById(Long id) {
        return activityAwardReceiveMapper.selectActivityAwardReceiveById(id);
    }

    public List<ActivityAwardReceive> selectActivityAwardReceiveList(ActivityAwardReceive param) {
        return activityAwardReceiveMapper.selectActivityAwardReceiveList(param);
    }

    public int selectActivityAwardReceiveListCount(ActivityAwardReceive param) {
        return activityAwardReceiveMapper.selectActivityAwardReceiveListCount(param);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean insertActivityAwardReceive(ActivityAwardReceive param) {
        param.setCreateTime(DateUtils.getNowDate());
        return this.save(param);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean updateActivityAwardReceive(ActivityAwardReceive param) {
        return this.updateById(param);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteActivityAwardReceiveByIds(Long[] ids) {
        return this.removeByIds(Arrays.asList(ids));
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteActivityAwardReceiveById(Long id) {
        return this.removeById(id);
    }

    // ===================== 查询 =====================

    public List<ActivityAwardReceive> selectActivityAwardReceiveListBychannelId(Long channelId, String startTime, String endTime) {
        return activityAwardReceiveMapper.selectActivityAwardReceiveListBychannelId(channelId, startTime, endTime);
    }

    public Map<String, Object> selectActivityAwardReceiveMapBychannelId(Long channelId, int type, String startTime, String endTime) {
        return activityAwardReceiveMapper.selectActivityAwardReceiveMapBychannelId(channelId, type, startTime, endTime);
    }

    public Map<String, Object> playWheelTermCountMap(String startTime, String endTime, Long userId) {
        return activityAwardReceiveMapper.playWheelTermCountMap(startTime, endTime, userId);
    }

    public List<ActivityAwardReceive> queryReceiveListByDate(Long userId, Long activityId, int day) {
        LambdaQueryWrapper<ActivityAwardReceive> q = new LambdaQueryWrapper<>();
        q.eq(ActivityAwardReceive::getUserId, userId);
        q.eq(ActivityAwardReceive::getActivityId, activityId);
        Date startTimeByDate = DateUtils.getStartTimeByDate(DateUtils.addDateDays(new Date(), -30));
        q.ge(ActivityAwardReceive::getCreateTime, startTimeByDate);
        return activityAwardReceiveMapper.selectList(q);
    }

    /**
     * 查询用户在某时间段内是否已领取
     *
     * @return 领取次数（> 0 表示已领取）
     */
    public Integer isReceive(Long userId, Long taskId, Long activityId, Integer timeType) {
        Map<String, Date> timeRange = DateUtils.getTimeRangeByType(timeType);
        Date startTime = timeRange.get("startTime");
        Date endTime = timeRange.get("endTime");

        LambdaQueryWrapper<ActivityAwardReceive> q = new LambdaQueryWrapper<>();
        q.eq(ActivityAwardReceive::getUserId, userId);
        // 首冲/二冲/三冲按活动维度判断，其余按任务维度
        boolean isSpecialCharge = Objects.equals(timeType, ActivityTypeConstants.First)
                || Objects.equals(timeType, ActivityTypeConstants.Second)
                || Objects.equals(timeType, ActivityTypeConstants.three);
        if (isSpecialCharge) {
            q.eq(ActivityAwardReceive::getActivityId, activityId);
        } else {
            q.eq(ActivityAwardReceive::getActivityTaskId, taskId);
        }
        q.ge(startTime != null, ActivityAwardReceive::getCreateTime, startTime);
        q.le(endTime != null, ActivityAwardReceive::getCreateTime, endTime);
        return activityAwardReceiveMapper.selectCount(q);
    }

    // ===================== 领取状态判断 =====================

    /**
     * 查询单任务领取状态
     *
     * @param activityType 活动类型（2=充值, 3=打码量 等）
     * @param gameTypeCode 游戏类型，null 表示不限游戏类型
     * @param taskId       任务ID
     * @param activityId   活动ID
     * @param timeType     时间类型（1每日, 2永久, 3每周, 4每月, 5首冲, 6二冲, 7三冲）
     * @param taskAmount   任务门槛金额
     * @param userId       用户ID
     * @return 0=未领取（满足条件）, 1=已领取, 2=无法领取
     */
    public Integer queryReceiveType(Integer activityType, String gameTypeCode, Long taskId, Long activityId,
                                    Integer timeType, BigDecimal taskAmount, Long userId) {
        Map<String, Date> timeRange = DateUtils.getTimeRangeByType(timeType);
        Date startTime = timeRange.get("startTime");
        Date endTime = timeRange.get("endTime");

        // 首充 + 开启了"有打码量不允许领首充"开关时，直接拦截
        if (Objects.equals(activityType, ActivityConstants.RECHARGE)
                && Objects.equals(timeType, ActivityTypeConstants.First)
                && configRiskService.firstChargeNotBet()) {
            UserExt userExt = userExtService.queryUSerExt(userId, UserExtTypeCons.打码量);
            if (userExt != null && userExt.getAmount().compareTo(BigDecimal.ZERO) > 0) {
                return 2;
            }
        }

        boolean isSpecialCharge = Objects.equals(timeType, ActivityTypeConstants.First)
                || Objects.equals(timeType, ActivityTypeConstants.Second)
                || Objects.equals(timeType, ActivityTypeConstants.three);

        LambdaQueryWrapper<ActivityAwardReceive> q = buildReceiveQuery(userId, startTime, endTime);
        q.eq(ActivityAwardReceive::getActivityTaskId, taskId);

        if (isSpecialCharge) {
            // 已领取该任务
            if (activityAwardReceiveMapper.selectCount(q) > 0) {
                return 1;
            }
            // 同活动内其他任务已领取 → 无法领取
            LambdaQueryWrapper<ActivityAwardReceive> qActivity = buildReceiveQuery(userId, startTime, endTime);
            qActivity.eq(ActivityAwardReceive::getActivityId, activityId);
            if (activityAwardReceiveMapper.selectCount(qActivity) > 0) {
                return 2;
            }
        } else {
            if (activityAwardReceiveMapper.selectCount(q) > 0) {
                return 1;
            }
        }

        // 充值/打码量：校验 Redis 中金额是否达到门槛
        if (Objects.equals(activityType, ActivityConstants.RECHARGE)
                || Objects.equals(activityType, ActivityConstants.BET)) {
            BigDecimal current = dealAmount(activityType, gameTypeCode, timeType, userId);
            if (current.compareTo(taskAmount) >= 0) {
                return 0;
            }
        }

        return 2;
    }

    /**
     * 批量查询任务领取状态（用于红点接口性能优化）
     *
     * @param userId      用户ID
     * @param tasks       任务列表
     * @param activityMap 活动ID → Activity 映射
     * @return key: taskId_activityId_timeType, value: 0未领取 1已领取 2无法领取
     */
    public Map<String, Integer> batchQueryReceiveStatus(Long userId, List<ActivityTask> tasks, Map<Long, Activity> activityMap) {
        Map<String, Integer> resultMap = new HashMap<>();
        if (CollUtil.isEmpty(tasks)) {
            return resultMap;
        }

        // 1. 收集所有任务ID、活动ID、时间类型
        Set<Long> taskIds = new HashSet<>();
        Set<Long> activityIds = new HashSet<>();
        Set<Integer> timeTypes = new HashSet<>();
        for (ActivityTask task : tasks) {
            taskIds.add(task.getId());
            activityIds.add(task.getActivityId());
            timeTypes.add(task.getTimeType());
        }

        // 2. 按 timeType 计算时间范围
        Map<Integer, Map<String, Date>> timeRangeMap = new HashMap<>();
        for (Integer timeType : timeTypes) {
            timeRangeMap.put(timeType, DateUtils.getTimeRangeByType(timeType));
        }

        // 3. 一次性查询用户所有相关领取记录
        LambdaQueryWrapper<ActivityAwardReceive> receiveQuery = new LambdaQueryWrapper<>();
        receiveQuery.eq(ActivityAwardReceive::getUserId, userId);
        receiveQuery.and(w -> w.in(ActivityAwardReceive::getActivityTaskId, taskIds)
                .or()
                .in(ActivityAwardReceive::getActivityId, activityIds));
        List<ActivityAwardReceive> receiveList = activityAwardReceiveMapper.selectList(receiveQuery);

        Map<Long, List<ActivityAwardReceive>> receiveByTaskId = receiveList.stream()
                .filter(r -> r.getActivityTaskId() != null)
                .collect(Collectors.groupingBy(ActivityAwardReceive::getActivityTaskId));
        Map<Long, List<ActivityAwardReceive>> receiveByActivityId = receiveList.stream()
                .filter(r -> r.getActivityId() != null)
                .collect(Collectors.groupingBy(ActivityAwardReceive::getActivityId));

        // 4. 预查首充配置（仅存在首充类型任务时才查）
        Boolean firstChargeNotBet = null;
        BigDecimal userBetAmount = null;
        boolean hasFirstChargeTask = tasks.stream().anyMatch(t -> {
            Activity a = activityMap.get(t.getActivityId());
            return Objects.equals(t.getTimeType(), ActivityTypeConstants.First)
                    && a != null && Objects.equals(a.getType(), ActivityConstants.RECHARGE);
        });
        if (hasFirstChargeTask) {
            firstChargeNotBet = configRiskService.firstChargeNotBet();
            if (firstChargeNotBet) {
                UserExt userExt = userExtService.queryUSerExt(userId, UserExtTypeCons.打码量);
                userBetAmount = userExt != null ? userExt.getAmount() : BigDecimal.ZERO;
            }
        }

        // 5. 预取 Redis 金额（按 activityId_timeType 缓存，避免重复查询）
        Map<String, BigDecimal> redisAmountMap = new HashMap<>();
        for (ActivityTask task : tasks) {
            Activity activity = activityMap.get(task.getActivityId());
            if (activity == null) continue;
            Integer activityType = activity.getType();
            if (!Objects.equals(activityType, ActivityConstants.RECHARGE)
                    && !Objects.equals(activityType, ActivityConstants.BET)) continue;

            String mapKey = task.getActivityId() + "_" + task.getTimeType();
            if (!redisAmountMap.containsKey(mapKey)) {
                redisAmountMap.put(mapKey, dealAmount(activityType, activity.getGameTypeCode(), task.getTimeType(), userId));
            }
        }

        // 6. 逐任务计算领取状态
        for (ActivityTask task : tasks) {
            String cacheKey = task.getId() + "_" + task.getActivityId() + "_" + task.getTimeType();
            Integer timeType = task.getTimeType();
            Map<String, Date> timeRange = timeRangeMap.get(timeType);
            Date startTime = timeRange != null ? timeRange.get("startTime") : null;
            Date endTime = timeRange != null ? timeRange.get("endTime") : null;

            Activity activity = activityMap.get(task.getActivityId());
            Integer activityType = activity != null ? activity.getType() : null;

            // 首充 + 有打码量 → 无法领取
            if (Objects.equals(activityType, ActivityConstants.RECHARGE)
                    && Objects.equals(timeType, ActivityTypeConstants.First)
                    && Boolean.TRUE.equals(firstChargeNotBet)
                    && userBetAmount != null && userBetAmount.compareTo(BigDecimal.ZERO) > 0) {
                resultMap.put(cacheKey, 2);
                continue;
            }

            boolean isSpecialCharge = Objects.equals(timeType, ActivityTypeConstants.First)
                    || Objects.equals(timeType, ActivityTypeConstants.Second)
                    || Objects.equals(timeType, ActivityTypeConstants.three);

            // 已领取该任务
            long taskReceiveCount = receiveByTaskId.getOrDefault(task.getId(), Collections.emptyList())
                    .stream().filter(r -> isInTimeRange(r.getCreateTime(), startTime, endTime)).count();
            if (taskReceiveCount > 0) {
                resultMap.put(cacheKey, 1);
                continue;
            }

            // 首冲/二冲/三冲：同活动已有其他领取记录 → 无法领取
            if (isSpecialCharge) {
                long activityReceiveCount = receiveByActivityId.getOrDefault(task.getActivityId(), Collections.emptyList())
                        .stream().filter(r -> isInTimeRange(r.getCreateTime(), startTime, endTime)).count();
                if (activityReceiveCount > 0) {
                    resultMap.put(cacheKey, 2);
                    continue;
                }
            }

            // 充值/打码量：Redis 金额达到门槛 → 可领取
            if (Objects.equals(activityType, ActivityConstants.RECHARGE)
                    || Objects.equals(activityType, ActivityConstants.BET)) {
                BigDecimal redisAmount = redisAmountMap.getOrDefault(task.getActivityId() + "_" + timeType, BigDecimal.ZERO);
                if (redisAmount.compareTo(task.getTaskAmount()) >= 0) {
                    resultMap.put(cacheKey, 0);
                    continue;
                }
            }

            resultMap.put(cacheKey, 2);
        }

        return resultMap;
    }

    public Map<String, Integer> batchQueryReceiveStatus(Long userId, List<ActivityTask> tasks) {
        return batchQueryReceiveStatus(userId, tasks, Collections.emptyMap());
    }

    // ===================== Redis 金额 =====================

    /**
     * 获取活动对应的当前金额（充值额 / 打码量）
     * gameTypeCode 为 null 时自动汇总所有游戏类型
     */
    public BigDecimal getRedisKeyAmount(Activity activity, ActivityTask activityTask, Long userId) {
        return dealAmount(activity.getType(), activity.getGameTypeCode(), activityTask.getTimeType(), userId);
    }

    /**
     * 不限游戏类型时的便捷方法，等价于 dealAmount(type, null, timeType, userId)
     */
    public BigDecimal dealAmount(Integer type, Integer timeType, Long userId) {
        return dealAmount(type, null, timeType, userId);
    }

    /**
     * 查询充值/打码量金额
     * BET 类型 gameTypeCode 为 null/空/"0" 时，自动汇总所有游戏类型：
     *   - 有时间词的 timeType（TODAY/WEEK/MONTH/FIRST/SECOND/THREE）用 Redis pattern scan
     *   - FOREVER 因 key 无时间词无法安全区分，回退到遍历 1-9
     */
    public BigDecimal dealAmount(Integer type, String gameTypeCode, Integer timeType, Long userId) {
        if (Objects.equals(type, ActivityConstants.BET) && isAllGameTypes(gameTypeCode)) {
            List<String> allKeys = new ArrayList<>();
            for (String gameType : CollUtil.newArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")) {
                String redisKey = getRedisKey(gameType, type, timeType, userId);
                if (StrUtil.isNotEmpty(redisKey)) {
                    allKeys.add(redisKey);
                }
            }
            BigDecimal total = BigDecimal.ZERO;
            if (CollUtil.isNotEmpty(allKeys)) {
                List<String> values = redisUtil.strMGet(allKeys);
                for (String value : values) {
                    if (value != null) {
                        total = total.add(parseSafe(value));
                    }
                }
            }
            log.info("dealAmount total:{}", total);
            return total;
        }
        // BET 单个或逗号拼接多个游戏类型：按逗号拆分后累加
        if (Objects.equals(type, ActivityConstants.BET)) {
            BigDecimal total = BigDecimal.ZERO;
            for (String singleCode : gameTypeCode.split(",")) {
                String trimmed = singleCode.trim();
                if (StrUtil.isEmpty(trimmed)) continue;
                String redisKey = getRedisKey(trimmed, type, timeType, userId);
                if (StrUtil.isNotEmpty(redisKey)) {
                    Object o = redisUtil.get(redisKey);
                    if (o != null) {
                        total = total.add(parseSafe(o.toString()));
                    }
                }
            }
            log.info("dealAmount gameTypeCode:{} total:{}", gameTypeCode, total);
            return total;
        }
        String redisKey = getRedisKey(gameTypeCode, type, timeType, userId);
        if (StrUtil.isNotEmpty(redisKey)) {
            log.info("redisKey:{}", redisKey);
            Object o = redisUtil.get(redisKey);
            if (o != null) {
                log.info("剩余时长:{}", redisUtil.getExpire(redisKey));
                return parseSafe(o.toString());
            }
        }
        return BigDecimal.ZERO;
    }


    /**
     * 构建 Redis key（充值 / 打码量）
     */
    public String getRedisKey(String gameTypeCode, Integer activityType, Integer timeType, Long userId) {
        String dbCode = CecuUtil.getDbCode();
        if (Objects.equals(activityType, ActivityConstants.RECHARGE)) {
            if (Objects.equals(timeType, ActivityTypeConstants.DAY))     return StrUtil.format(RedisKey.USER_RECHARGE_TODAY,   dbCode, userId);
            if (Objects.equals(timeType, ActivityTypeConstants.FOREVER)) return StrUtil.format(RedisKey.USER_RECHARGE,          dbCode, userId);
            if (Objects.equals(timeType, ActivityTypeConstants.WEEK))    return StrUtil.format(RedisKey.USER_RECHARGE_WEEK,    dbCode, userId);
            if (Objects.equals(timeType, ActivityTypeConstants.MONTH))   return StrUtil.format(RedisKey.USER_RECHARGE_MONTH,   dbCode, userId);
            if (Objects.equals(timeType, ActivityTypeConstants.First))   return StrUtil.format(RedisKey.USER_RECHARGE_FIRST,   dbCode, userId);
            if (Objects.equals(timeType, ActivityTypeConstants.Second))  return StrUtil.format(RedisKey.USER_RECHARGE_SECOND,  dbCode, userId);
            if (Objects.equals(timeType, ActivityTypeConstants.three))   return StrUtil.format(RedisKey.USER_RECHARGE_THREE,   dbCode, userId);
        } else if (Objects.equals(activityType, ActivityConstants.BET)) {
            if (Objects.equals(timeType, ActivityTypeConstants.DAY))     return StrUtil.format(RedisKey.USER_BET_TODAY,   dbCode, gameTypeCode, userId);
            if (Objects.equals(timeType, ActivityTypeConstants.FOREVER)) return StrUtil.format(RedisKey.USER_BET,          dbCode, gameTypeCode, userId);
            if (Objects.equals(timeType, ActivityTypeConstants.WEEK))    return StrUtil.format(RedisKey.USER_BET_WEEK,    dbCode, gameTypeCode, userId);
            if (Objects.equals(timeType, ActivityTypeConstants.MONTH))   return StrUtil.format(RedisKey.USER_BET_MONTH,   dbCode, gameTypeCode, userId);
            if (Objects.equals(timeType, ActivityTypeConstants.First))   return StrUtil.format(RedisKey.USER_BET_FIRST,   dbCode, gameTypeCode, userId);
            if (Objects.equals(timeType, ActivityTypeConstants.Second))  return StrUtil.format(RedisKey.USER_BET_SECOND,  dbCode, gameTypeCode, userId);
            if (Objects.equals(timeType, ActivityTypeConstants.three))   return StrUtil.format(RedisKey.USER_BET_THREE,   dbCode, gameTypeCode, userId);
        }
        return "";
    }

    private BigDecimal parseSafe(String value) {
        if (StrUtil.isBlank(value)) return BigDecimal.ZERO;
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            log.warn("Redis中存储的金额格式异常，已忽略: {}", value);
            return BigDecimal.ZERO;
        }
    }

    // ===================== 私有工具 =====================

    /** gameTypeCode 为 null、空字符串、"0" 或逗号拼接中含 "0" 时，均表示不限游戏类型 */
    public static boolean isAllGameTypes(String gameTypeCode) {
        if (StrUtil.isEmpty(gameTypeCode)) return true;
        for (String code : gameTypeCode.split(",")) {
            if ("0".equals(code.trim())) return true;
        }
        return false;
    }

    private LambdaQueryWrapper<ActivityAwardReceive> buildReceiveQuery(Long userId, Date startTime, Date endTime) {
        LambdaQueryWrapper<ActivityAwardReceive> q = new LambdaQueryWrapper<>();
        q.eq(ActivityAwardReceive::getUserId, userId);
        q.ge(startTime != null, ActivityAwardReceive::getCreateTime, startTime);
        q.le(endTime != null, ActivityAwardReceive::getCreateTime, endTime);
        return q;
    }

    private boolean isInTimeRange(Date createTime, Date startTime, Date endTime) {
        if (createTime == null) return false;
        if (startTime != null && createTime.before(startTime)) return false;
        if (endTime != null && createTime.after(endTime)) return false;
        return true;
    }
}
