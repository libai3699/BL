package com.gp.common.mybatisplus.mapper;

import java.util.List;
import java.util.Map;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.common.datasource.param.DataSource;
import com.gp.common.mybatisplus.entity.OrderRedReceive;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 红包雨和私人红包记录Mapper接口
 *
 * @author axing
 * @date 2025-07-24
 */
public interface OrderRedReceiveMapper extends BaseMapper<OrderRedReceive>
{
    /**
     * 查询红包雨和私人红包记录
     *
     * @param id 红包雨和私人红包记录ID
     * @return 红包雨和私人红包记录
     */
    public OrderRedReceive selectOrderRedReceiveById(Long id);

    /**
     * 查询红包雨和私人红包记录列表
     *
     * @param orderRedReceive 红包雨和私人红包记录
     * @return 红包雨和私人红包记录集合
     */
    public List<OrderRedReceive> selectOrderRedReceiveList(OrderRedReceive orderRedReceive);

    /**
     * 查询红包雨和私人红包记录列表数量
     *
     * @param orderRedReceive 红包雨和私人红包记录
     * @return 红包雨和私人红包记录集合数量
     */
    public int selectOrderRedReceiveListCount(OrderRedReceive orderRedReceive);

    /**
     * 新增红包雨和私人红包记录
     *
     * @param orderRedReceive 红包雨和私人红包记录
     * @return 结果
     */
    public int insertOrderRedReceive(OrderRedReceive orderRedReceive);

    /**
     * 修改红包雨和私人红包记录
     *
     * @param orderRedReceive 红包雨和私人红包记录
     * @return 结果
     */
    public int updateOrderRedReceive(OrderRedReceive orderRedReceive);

    /**
     * 删除红包雨和私人红包记录
     *
     * @param id 红包雨和私人红包记录ID
     * @return 结果
     */
    public int deleteOrderRedReceiveById(Long id);

    /**
     * 批量删除红包雨和私人红包记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteOrderRedReceiveByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();


    Map<String, Object> redEnvelopeRainCountMap(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                                @Param("userId") Long userId, @Param("currencyId") Integer currencyId);


    Map<String, Object> redEnvelopeRainChanneCountMap(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                                       @Param("currencyId") Integer currencyId,@Param("type") Integer type,@Param("channelId") Long channelId );
}
