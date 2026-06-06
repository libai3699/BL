package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.PlayWheelTerm;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 转盘活动订单Mapper接口
 *
 * @author axing
 * @date 2024-05-12
 */
public interface PlayWheelTermMapper extends BaseMapper<PlayWheelTerm> {
    /**
     * 查询转盘活动订单
     *
     * @param id 转盘活动订单ID
     * @return 转盘活动订单
     */
    public PlayWheelTerm selectPlayWheelTermById(Integer id);

    /**
     * 查询转盘活动订单列表
     *
     * @param playWheelTerm 转盘活动订单
     * @return 转盘活动订单集合
     */
    public List<PlayWheelTerm> selectPlayWheelTermList(PlayWheelTerm playWheelTerm);

    /**
     * 查询转盘活动订单数量
     *
     * @param playWheelTerm 转盘活动订单
     * @return 转盘活动订单数量
     */
    public Long selectPlayWheelTermCount(PlayWheelTerm playWheelTerm);

    /**
     * 新增转盘活动订单
     *
     * @param playWheelTerm 转盘活动订单
     * @return 结果
     */
    public int insertPlayWheelTerm(PlayWheelTerm playWheelTerm);

    /**
     * 修改转盘活动订单
     *
     * @param playWheelTerm 转盘活动订单
     * @return 结果
     */
    public int updatePlayWheelTerm(PlayWheelTerm playWheelTerm);

    /**
     * 删除转盘活动订单
     *
     * @param id 转盘活动订单ID
     * @return 结果
     */
    public int deletePlayWheelTermById(Integer id);

    /**
     * 批量删除转盘活动订单
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deletePlayWheelTermByIds(Integer[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    List<PlayWheelTerm> selectPlayWheelTermListByUser(@Param("channelId") Long channelId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    Map<String, Object> playWheelTermCountMap(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("userId") Long userId);

    Map<String, Object> selectPlayWheelTermMapByUser(@Param("channelId") Long channelId,@Param("startTime") String startTime, @Param("endTime") String endTime );
}