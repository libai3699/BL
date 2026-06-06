package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.ActivityAward;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 活动奖励Mapper接口
 *
 * @author axing
 * @date 2024-05-12
 */
public interface ActivityAwardMapper extends BaseMapper<ActivityAward>
{
    /**
     * 查询活动奖励
     *
     * @param id 活动奖励ID
     * @return 活动奖励
     */
    public ActivityAward selectActivityAwardById(Long id);

    /**
     * 查询活动奖励列表
     *
     * @param activityAward 活动奖励
     * @return 活动奖励集合
     */
    public List<ActivityAward> selectActivityAwardList(ActivityAward activityAward);

    /**
     * 查询活动奖励列表数量
     *
     * @param activityAward 活动奖励
     * @return 活动奖励集合数量
     */
    public int selectActivityAwardListCount(ActivityAward activityAward);

    /**
     * 新增活动奖励
     *
     * @param activityAward 活动奖励
     * @return 结果
     */
    public int insertActivityAward(ActivityAward activityAward);

    /**
     * 修改活动奖励
     *
     * @param activityAward 活动奖励
     * @return 结果
     */
    public int updateActivityAward(ActivityAward activityAward);

    /**
     * 删除活动奖励
     *
     * @param id 活动奖励ID
     * @return 结果
     */
    public int deleteActivityAwardById(Long id);

    /**
     * 批量删除活动奖励
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteActivityAwardByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
