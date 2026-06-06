package com.gp.common.mybatisplus.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.common.datasource.param.DataSource;
import com.gp.common.mybatisplus.entity.OrderRedEnvelopeReceive;
import com.gp.common.mybatisplus.param.OrderRedEnvelopeReceiveVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 红包接收Mapper接口
 *
 * @author axing
 * @date 2024-12-25
 */
public interface OrderRedEnvelopeReceiveMapper extends BaseMapper<OrderRedEnvelopeReceive>
{
    /**
     * 查询红包接收
     *
     * @param id 红包接收ID
     * @return 红包接收
     */

    public OrderRedEnvelopeReceive selectOrderRedEnvelopeReceiveById(Long id);

    /**
     * 查询红包接收列表
     *
     * @param orderRedEnvelopeReceive 红包接收
     * @return 红包接收集合
     */

    public List<OrderRedEnvelopeReceive> selectOrderRedEnvelopeReceiveList(OrderRedEnvelopeReceive orderRedEnvelopeReceive);

    /**
     * 新增红包接收
     *
     * @param orderRedEnvelopeReceive 红包接收
     * @return 结果
     */
    public int insertOrderRedEnvelopeReceive(OrderRedEnvelopeReceive orderRedEnvelopeReceive);

    /**
     * 修改红包接收
     *
     * @param orderRedEnvelopeReceive 红包接收
     * @return 结果
     */
    public int updateOrderRedEnvelopeReceive(OrderRedEnvelopeReceive orderRedEnvelopeReceive);

    /**
     * 删除红包接收
     *
     * @param id 红包接收ID
     * @return 结果
     */
    public int deleteOrderRedEnvelopeReceiveById(Long id);

    /**
     * 批量删除红包接收
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteOrderRedEnvelopeReceiveByIds(Long[] ids);


    List<OrderRedEnvelopeReceiveVo> listByOrder(@Param("orderNo") String orderNo, @Param("userId") Long userId);


    List<OrderRedEnvelopeReceiveVo> listByOrderWithOutUserId(String orderNo);


    Map<String, Object> orderRedEnvelopeReceiveCountMap(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("userId") Long userId, @Param("currencyId") Integer currencyId);


    Map<String, Object> orderRedEnvelopeSendsMap(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("currencyId") Integer currencyId, @Param("type") Integer type, @Param("channelId") Long channelId);


    Long selectOrderRedEnvelopeReceiveCount(OrderRedEnvelopeReceive param);
}
