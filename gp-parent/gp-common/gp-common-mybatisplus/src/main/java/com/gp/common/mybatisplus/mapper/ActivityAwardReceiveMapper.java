package com.gp.common.mybatisplus.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.common.datasource.param.DataSource;
import com.gp.common.mybatisplus.entity.ActivityAwardReceive;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 活动奖励领取Mapper接口
 *
 * @author axing
 * @date 2024-06-12
 */
public interface ActivityAwardReceiveMapper extends BaseMapper<ActivityAwardReceive>
{
    /**
     * 查询活动奖励领取
     *
     * @param id 活动奖励领取ID
     * @return 活动奖励领取
     */

    public ActivityAwardReceive selectActivityAwardReceiveById(Long id);

    /**
     * 查询活动奖励领取列表
     *
     * @param activityAwardReceive 活动奖励领取
     * @return 活动奖励领取集合
     */

    public List<ActivityAwardReceive> selectActivityAwardReceiveList(ActivityAwardReceive activityAwardReceive);

    /**
     * 查询活动奖励领取列表数量
     *
     * @param activityAwardReceive 活动奖励领取
     * @return 活动奖励领取集合数量
     */

    public int selectActivityAwardReceiveListCount(ActivityAwardReceive activityAwardReceive);

    /**
     * 新增活动奖励领取
     *
     * @param activityAwardReceive 活动奖励领取
     * @return 结果
     */
    public int insertActivityAwardReceive(ActivityAwardReceive activityAwardReceive);

    /**
     * 修改活动奖励领取
     *
     * @param activityAwardReceive 活动奖励领取
     * @return 结果
     */
    public int updateActivityAwardReceive(ActivityAwardReceive activityAwardReceive);

    /**
     * 删除活动奖励领取
     *
     * @param id 活动奖励领取ID
     * @return 结果
     */
    public int deleteActivityAwardReceiveById(Long id);

    /**
     * 批量删除活动奖励领取
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteActivityAwardReceiveByIds(Long[] ids);


    List<ActivityAwardReceive> selectActivityAwardReceiveListBychannelId(@Param("channelId") Long channelId, @Param("startTime") String startTime, @Param("endTime") String endTime );


    Map<String, Object> selectActivityAwardReceiveMapBychannelId(@Param("channelId") Long channelId, @Param("type") Integer type, @Param("startTime") String startTime, @Param("endTime") String endTime );


    Map<String, Object> playWheelTermCountMap(@Param("startTime")String startTime, @Param("endTime")String endTime,@Param("userId") Long userId);
}
