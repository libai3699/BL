package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.Activity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 活动Mapper接口
 *
 * @author axing
 * @date 2024-05-12
 */
public interface ActivityMapper extends BaseMapper<Activity>
{
    /**
     * 查询活动
     *
     * @param id 活动ID
     * @return 活动
     */
    public Activity selectActivityById(Long id);

    /**
     * 查询活动列表
     *
     * @param activity 活动
     * @return 活动集合
     */
    public List<Activity> selectActivityList(Activity activity);

    /**
     * 查询活动列表数量
     *
     * @param activity 活动
     * @return 活动集合数量
     */
    public int selectActivityListCount(Activity activity);

    /**
     * 新增活动
     *
     * @param activity 活动
     * @return 结果
     */
    public int insertActivity(Activity activity);

    /**
     * 修改活动
     *
     * @param activity 活动
     * @return 结果
     */
    public int updateActivity(Activity activity);

    /**
     * 删除活动
     *
     * @param id 活动ID
     * @return 结果
     */
    public int deleteActivityById(Long id);

    /**
     * 批量删除活动
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteActivityByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}