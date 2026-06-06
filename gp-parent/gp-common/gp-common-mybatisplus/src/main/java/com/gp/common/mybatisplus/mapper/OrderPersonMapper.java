package com.gp.common.mybatisplus.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.common.datasource.param.DataSource;
import com.gp.common.mybatisplus.entity.OrderPerson;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 人工上下分订单Mapper接口
 *
 * @author axing
 * @date 2024-05-18
 */
public interface OrderPersonMapper extends BaseMapper<OrderPerson> {
    /**
     * 查询人工上下分订单
     *
     * @param id 人工上下分订单ID
     * @return 人工上下分订单
     */

    public OrderPerson selectOrderPersonById(Long id);

    /**
     * 查询人工上下分订单列表
     *
     * @param orderPerson 人工上下分订单
     * @return 人工上下分订单集合
     */

    public List<OrderPerson> selectOrderPersonList(OrderPerson orderPerson);

    /**
     * 查询人工上下分订单数量
     *
     * @param orderPerson 人工上下分订单
     * @return 人工上下分订单数量
     */

    public long selectOrderPersonCount(OrderPerson orderPerson);

    /**
     * 新增人工上下分订单
     *
     * @param orderPerson 人工上下分订单
     * @return 结果
     */
    public int insertOrderPerson(OrderPerson orderPerson);

    /**
     * 修改人工上下分订单
     *
     * @param orderPerson 人工上下分订单
     * @return 结果
     */
    public int updateOrderPerson(OrderPerson orderPerson);

    /**
     * 删除人工上下分订单
     *
     * @param id 人工上下分订单ID
     * @return 结果
     */
    public int deleteOrderPersonById(Long id);

    /**
     * 批量删除人工上下分订单
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteOrderPersonByIds(Long[] ids);



    List<OrderPerson> selectOrderPersonListBrUser(@Param("channelId") Long channelId, @Param("id") Integer id, @Param("startTime") String startTime, @Param("endTime") String endTime);


    List<OrderPerson> selectOrderPersonListBrchannelId(@Param("channelId") Long channelId, @Param("id") Integer id, @Param("startTime") String startTime, @Param("endTime") String endTime);


    List<OrderPerson> selectOrderPersonListBrType(@Param("channelId") Long channelId, @Param("id") Integer id, @Param("startTime") String startTime, @Param("endTime") String endTime);


    Map<String, Object> orderPersonCountMap(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("currencyId") Integer currencyId, @Param("userId") Long userId);


    Map<String, Object> selectOrderPersonMap1BrUser(@Param("channelId") Long channelId, @Param("id") Integer id, @Param("startTime") String startTime, @Param("endTime") String endTime );


    Map<String, Object> selectOrderPersonMap2BrUser(@Param("channelId") Long channelId, @Param("type") Integer type,@Param("id") Integer id, @Param("startTime") String startTime, @Param("endTime") String endTime );

    List<Map<String,Object>> queryWithdrawMap(@Param("userIds") List<Long> userIds);

    Map<String, Object> getTodayTotalBetAmount(OrderPerson param);
}