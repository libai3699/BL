package com.gp.common.mybatisplus.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.constant.CommonConstant;
import com.gp.common.base.constant.ActivityConstants;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.deal.DataProcessor;
import com.gp.common.mybatisplus.entity.Activity;
import com.gp.common.mybatisplus.entity.ActivityAward;
import com.gp.common.mybatisplus.entity.ActivityTask;
import com.gp.common.mybatisplus.mapper.ActivityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 活动Service业务层处理
 *
 * @author axing
 * @date 2024-05-12
 */
@Service
public class ActivityService extends ServiceImpl<ActivityMapper, Activity> {
    @Resource
    private ActivityMapper activityMapper;

    @Resource
    private ActivityTaskService activityTaskService;

    @Resource
    private ActivityAwardService activityAwardService;

    /**
     * 查询活动
     *
     * @param id 活动ID
     * @return 活动
     */

    public Activity selectActivityById(Long id) {
        return activityMapper.selectActivityById(id);
    }

    /**
     * 查询活动列表
     *
     * @param param 活动
     * @return 活动
     */

    public List<Activity> selectActivityList(Activity param) {
        return activityMapper.selectActivityList(param);
    }
    
    /**
     * 查询活动列表数量
     *
     * @param param 活动
     * @return 活动数量
     */
    public int selectActivityListCount(Activity param) {
        return activityMapper.selectActivityListCount(param);
    }

    /**
     * 新增活动
     *
     * @param param 活动
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertActivity(Activity param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改活动
     *
     * @param param 活动
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateActivity(Activity param) {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除活动
     *
     * @param ids 需要删除的活动ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteActivityByIds(Long[] ids) {
        //查询所有关联的活动任务
        LambdaQueryWrapper<ActivityTask> taskLambdaQueryWrapper = Wrappers.lambdaQuery(ActivityTask.class);
        taskLambdaQueryWrapper.in(ActivityTask::getActivityId, Arrays.asList(ids));
        List<ActivityTask> taskList = activityTaskService.list(taskLambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(taskList)) {
            List<Long> taskIdList = taskList.stream().map(ActivityTask::getId).collect(Collectors.toList());
            //删除关联的奖励
            LambdaQueryWrapper<ActivityAward> awardLambdaQueryWrapper = Wrappers.lambdaQuery(ActivityAward.class);
            awardLambdaQueryWrapper.in(ActivityAward::getActivityTaskId, taskIdList);
            activityAwardService.remove(awardLambdaQueryWrapper);
            //删除关联活动任务
            activityTaskService.removeByIds(taskIdList);
        }

        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除活动信息
     *
     * @param id 活动ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteActivityById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    /**
     * 查询所有活动
     *
     * @return
     */
    public List<Activity> queryAllActivity() {
        ArrayList<Integer> arr = CollUtil.newArrayList(
                ActivityConstants.BET
                ,ActivityConstants.RECHARGE
                ,ActivityConstants.SIGN_IN
                ,ActivityConstants.LOTTERY_RECHARGE
                ,ActivityConstants.LOTTERY_HELPMONEY
        );
        List<Activity> activities = getActivities(arr);
        activities.stream().forEach(e -> {
            DataProcessor.process(e);

        });
        return activities;
    }

    /**
     * 查询签到活动
     *
     * @return
     */
    public Activity queryActivityByType(Integer type) {
        ArrayList<Integer> arr = CollUtil.newArrayList(type);
        List<Activity> activities = getActivities(arr);
        activities.stream().forEach(e -> {
            DataProcessor.process(e);

        });
        if(CollectionUtils.isNotEmpty(activities)) {
            return activities.get(0);
        }
        return null;

    }//querySignActivity

    public List<Activity> querySpecialActivity() {
        ArrayList<Integer> arr = CollUtil.newArrayList(ActivityConstants.SPECIAL);
        List<Activity> activities = getActivities(arr);
        activities.stream().forEach(e -> {
            DataProcessor.process(e);

        });
        return activities;
    }

    public Activity queryChannelActivity() {
        ArrayList<Integer> arr = CollUtil.newArrayList(ActivityConstants.CHANNEL);
        LambdaQueryWrapper<Activity> q = new LambdaQueryWrapper<>();
        q.eq(Activity::getStatus, CommonConstant.OPEN);
        q.in(Activity::getType, arr);
        q.last("limit 1");
        q.orderByAsc(Activity::getSort);
        Activity activity = activityMapper.selectOne(q);
        return activity;
    }

    private List<Activity> getActivities(ArrayList<Integer> arr) {
        LambdaQueryWrapper<Activity> q = new LambdaQueryWrapper<>();
        q.eq(Activity::getStatus, CommonConstant.OPEN);
        q.in(Activity::getType, arr);
        q.orderByAsc(Activity::getSort);
        List<Activity> activities = activityMapper.selectList(q);
        return activities;
    }

    /**
     * @param
     * @param gameTypeCode
     * @return
     */
    public List<ActivityTask> queryHelpMoneyActivityTask(String gameTypeCode) {
        ArrayList<Integer> arr = CollUtil.newArrayList(ActivityConstants.HELPMONEY);
        LambdaQueryWrapper<Activity> q = new LambdaQueryWrapper<>();
        q.eq(Activity::getStatus, CommonConstant.OPEN);
        q.in(Activity::getGameTypeCode, gameTypeCode);
        q.in(Activity::getType, arr);
        q.last("limit 1");
        q.orderByAsc(Activity::getSort);
        Activity activity = activityMapper.selectOne(q);
        if (activity == null) {
            return CollUtil.newArrayList();
        }
        //根据活动去查询任务
        List<ActivityTask> activityTaskList = activityTaskService.queryActivityTaskVos(activity.getId());
        return activityTaskList;
    }

    public List<Activity> queryHomeShowActivity() {
        LambdaQueryWrapper<Activity> q = new LambdaQueryWrapper<>();
        q.eq(Activity::getStatus, CommonConstant.OPEN);
        q.eq(Activity::getActivityHomeShow, CommonConstant.OPEN);
        q.orderByAsc(Activity::getSort);
        List<Activity> activities = activityMapper.selectList(q);
        activities.stream().forEach(e -> {
            DataProcessor.process(e);

        });
        return activities;
    }

    public Activity queryHelpMoneyActivityCommon() {
        ArrayList<Integer> arr = CollUtil.newArrayList(ActivityConstants.HELPMONEY);
        LambdaQueryWrapper<Activity> q = new LambdaQueryWrapper<>();
        q.eq(Activity::getStatus, CommonConstant.OPEN);
        q.in(Activity::getGameTypeCode, 0);
        q.in(Activity::getType, arr);
        q.last("limit 1");
        q.orderByAsc(Activity::getSort);
        return activityMapper.selectOne(q);
    }

}
