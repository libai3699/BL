package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.ActivityTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 活动任务Mapper接口
 *
 * @author axing
 * @date 2024-05-12
 */
public interface ActivityTaskMapper extends BaseMapper<ActivityTask>
{
    /**
     * 查询活动任务
     *
     * @param id 活动任务ID
     * @return 活动任务
     */
    public ActivityTask selectActivityTaskById(Long id);

    /**
     * 查询活动任务列表
     *
     * @param activityTask 活动任务
     * @return 活动任务集合
     */
    public List<ActivityTask> selectActivityTaskList(ActivityTask activityTask);

    /**
     * 查询活动任务列表数量
     *
     * @param activityTask 活动任务
     * @return 活动任务集合数量
     */
    public int selectActivityTaskListCount(ActivityTask activityTask);

    /**
     * 新增活动任务
     *
     * @param activityTask 活动任务
     * @return 结果
     */
    public int insertActivityTask(ActivityTask activityTask);

    /**
     * 修改活动任务
     *
     * @param activityTask 活动任务
     * @return 结果
     */
    public int updateActivityTask(ActivityTask activityTask);

    /**
     * 删除活动任务
     *
     * @param id 活动任务ID
     * @return 结果
     */
    public int deleteActivityTaskById(Long id);

    /**
     * 批量删除活动任务
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteActivityTaskByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
