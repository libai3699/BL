package com.gp.common.mybatisplus.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.mybatisplus.deal.DataProcessor;
import com.gp.common.mybatisplus.entity.*;
import com.gp.common.mybatisplus.manage.UserExtChangeManage;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.ActivityAwardMapper;

import javax.annotation.Resource;

/**
 * 活动奖励Service业务层处理
 *
 * @author axing
 * @date 2024-05-12
 */
@Service
public class ActivityAwardService extends ServiceImpl<ActivityAwardMapper, ActivityAward>
{
    @Resource
    private ActivityAwardMapper activityAwardMapper;
    @Resource
    private ActivityTaskService activityTaskService;
    @Resource
    private SignInService signInService;

    @Resource
    private CurrencyService currencyService;
    @Resource
    private ActivityAwardReceiveService activityAwardReceiveService;
    @Resource
    private UserExtChangeManage userExtChangeManage;
    /**
     * 查询活动奖励
     *
     * @param id 活动奖励ID
     * @return 活动奖励
     */

    public ActivityAward selectActivityAwardById(Long id)
    {
        return activityAwardMapper.selectActivityAwardById(id);
    }

    /**
     * 查询活动奖励列表
     *
     * @param param 活动奖励
     * @return 活动奖励
     */

    public List<ActivityAward> selectActivityAwardList(ActivityAward param)
    {
        return activityAwardMapper.selectActivityAwardList(param);
    }

    /**
     * 查询活动奖励列表数量
     *
     * @param param 活动奖励
     * @return 活动奖励数量
     */
    public int selectActivityAwardListCount(ActivityAward param) {
        return activityAwardMapper.selectActivityAwardListCount(param);
    }

    /**
     * 新增活动奖励
     *
     * @param param 活动奖励
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertActivityAward(ActivityAward param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改活动奖励
     *
     * @param param 活动奖励
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateActivityAward(ActivityAward param)
    {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除活动奖励
     *
     * @param ids 需要删除的活动奖励ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteActivityAwardByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除活动奖励信息
     *
     * @param id 活动奖励ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteActivityAwardById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }

    public List<ActivityAward> queryActivityTaskAward(Long taskId) {
        LambdaQueryWrapper<ActivityAward> q = new LambdaQueryWrapper<>();
        q.eq(ActivityAward::getActivityTaskId, taskId);
        List<ActivityAward> activityAwards = activityAwardMapper.selectList(q);
        activityAwards.stream().forEach(e->{
            DataProcessor.process(e);
        });
        return activityAwards;
    }


    public ActivityAward querySpecialActivityAward(Long taskId) {
        LambdaQueryWrapper<ActivityAward> q = new LambdaQueryWrapper<>();
        q.eq(ActivityAward::getActivityTaskId, taskId);
        q.last("limit 1");
        ActivityAward activityAward = activityAwardMapper.selectOne(q);
        return activityAward;
    }
}
