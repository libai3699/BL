package com.gp.common.mybatisplus.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.constant.CommonConstant;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.Activity;
import com.gp.common.mybatisplus.entity.ActivityAward;
import com.gp.common.mybatisplus.entity.ActivityTask;
import com.gp.common.mybatisplus.mapper.ActivityTaskMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 活动任务Service业务层处理
 *
 * @author axing
 * @date 2024-05-12
 */
@Service
@Slf4j
public class ActivityTaskService extends ServiceImpl<ActivityTaskMapper, ActivityTask> {
    @Resource
    private ActivityTaskMapper activityTaskMapper;
    @Resource
    private ActivityService activityService;

    @Resource
    private ActivityAwardService activityAwardService;
    /**
     * 查询活动任务
     *
     * @param id 活动任务ID
     * @return 活动任务
     */

    public ActivityTask selectActivityTaskById(Long id) {
        return activityTaskMapper.selectActivityTaskById(id);
    }

    /**
     * 查询活动任务列表
     *
     * @param param 活动任务
     * @return 活动任务
     */

    public List<ActivityTask> selectActivityTaskList(ActivityTask param) {
        return activityTaskMapper.selectActivityTaskList(param);
    }

    /**
     * 查询活动任务列表数量
     *
     * @param param 活动任务
     * @return 活动任务数量
     */
    public int selectActivityTaskListCount(ActivityTask param) {
        return activityTaskMapper.selectActivityTaskListCount(param);
    }

    /**
     * 新增活动任务
     *
     * @param param 活动任务
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertActivityTask(ActivityTask param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改活动任务
     *
     * @param param 活动任务
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateActivityTask(ActivityTask param) {
        boolean result = this.updateById(param);
        return result;
    }


    /**
     * 批量删除活动任务
     *
     * @param ids 需要删除的活动任务ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteActivityTaskByIds(Long[] ids) {
        //删除关联的奖励
        LambdaQueryWrapper<ActivityAward> awardLambdaQueryWrapper = Wrappers.lambdaQuery(ActivityAward.class);
        awardLambdaQueryWrapper.in(ActivityAward::getActivityTaskId, Arrays.asList(ids));
        activityAwardService.remove(awardLambdaQueryWrapper);
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除活动任务信息
     *
     * @param id 活动任务ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteActivityTaskById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    public List<ActivityTask> queryActivityTaskVos(Long activityId) {
        LambdaQueryWrapper<ActivityTask> q = new LambdaQueryWrapper<>();
        q.eq(ActivityTask::getActivityId, activityId);
        q.eq(ActivityTask::getStatus, CommonConstant.OPEN);
        q.orderByAsc(ActivityTask::getSort);
        List<ActivityTask> activities = activityTaskMapper.selectList(q);

        return activities;
    }

    public ActivityTask querySpecialActivityTask(Long activityId) {
        LambdaQueryWrapper<ActivityTask> q = new LambdaQueryWrapper<>();
        q.eq(ActivityTask::getActivityId, activityId);
        q.eq(ActivityTask::getStatus, CommonConstant.OPEN);
        q.orderByAsc(ActivityTask::getSort);
        q.last("limit 1");
        ActivityTask activityTask = activityTaskMapper.selectOne(q);

        return activityTask;
    }


//    private void dealTaskName( List<ActivityTask> arr,Activity activity) {
//        for (int i = 0; i < arr.size(); i++) {
//            ActivityTask item = arr.get(i);
//            item.setTaskName(activity.getActivityName());
//            Integer type = item.getType();
//            Integer timeType = item.getTimeType();
//            String msg ="";
//            //先看任务名称
//            item.setTaskName(item.getTaskName()+"-"+i);
//            if(Objects.equals(type, ActivityConstants.SIGN_IN)){
//                if (Objects.equals(timeType, ActivityTypeConstants.DAY)) {
//                    msg = StrUtil.format(MessagesUtils.get("bot.activity.DZQD"));
//                } else {
//                    msg = StrUtil.format(MessagesUtils.get("bot.activity.LJQD"), item.getTaskAmount());
//                }
//            }else if(Objects.equals(type, ActivityConstants.RECHARGE)){
//                if (Objects.equals(timeType, ActivityTypeConstants.DAY)) {
//                    msg = StrUtil.format(MessagesUtils.get("bot.activity.DZLJCZ"), item.getTaskAmount());
//                } else {
//                    msg = StrUtil.format(MessagesUtils.get("bot.activity.LJCZ"), item.getTaskAmount());
//                }
//            }else   if(Objects.equals(type, ActivityConstants.BET)) {
//                if (Objects.equals(timeType, ActivityTypeConstants.DAY)) {
//                    msg = StrUtil.format(MessagesUtils.get("bot.activity.DZYXTZ"), item.getTaskAmount());
//                } else {
//                    msg = StrUtil.format(MessagesUtils.get("bot.activity.YXTZ"), item.getTaskAmount());
//                }
//            }
//            item.setTaskExplain(msg);
//        }
//
//    }

    public List<ActivityTask> queryTask(Integer type) {
        LambdaQueryWrapper<ActivityTask> q = new LambdaQueryWrapper<>();
        q.eq(ActivityTask::getType, type);
        q.eq(ActivityTask::getStatus, CommonConstant.OPEN);
        List<ActivityTask> activityTaskList = activityTaskMapper.selectList(q);
        //把这个list拿到活动id
        List<Long> collect = activityTaskList.stream().map(item -> {
            return item.getActivityId();
        }).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(collect)) {
            return CollUtil.newArrayList();
        }
        LambdaQueryWrapper<Activity> activityQ = new LambdaQueryWrapper<>();
        activityQ.in(Activity::getId, collect);
        activityQ.eq(Activity::getStatus, CommonConstant.OPEN);
        //然后根据id 去查询 活动
        List<Activity> activities = activityService.list(activityQ);
        Map<Long, Activity> map = activities.stream().collect(Collectors.toMap(Activity::getId, Function.identity()));
        //activityTaskList过滤出map 中有值的 就证明活动开启了
        return activityTaskList.stream().filter(item -> {
            return map.get(item.getActivityId()) != null;
        }).collect(Collectors.toList());
    }
}
