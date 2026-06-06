package com.gp.common.mybatisplus.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.common.datasource.param.DataSource;
import com.gp.common.mybatisplus.entity.OrderLawWithdraw;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 法币提现订单Mapper接口
 *
 * @author axing
 * @date 2024-06-29
 */
public interface OrderLawWithdrawMapper extends BaseMapper<OrderLawWithdraw> {
    /**
     * 查询法币提现订单
     *
     * @param id 法币提现订单ID
     * @return 法币提现订单
     */

    public OrderLawWithdraw selectOrderLawWithdrawById(Long id);

    /**
     * 查询法币提现订单列表
     *
     * @param orderLawWithdraw 法币提现订单
     * @return 法币提现订单集合
     */

    public List<OrderLawWithdraw> selectOrderLawWithdrawList(OrderLawWithdraw orderLawWithdraw);

    /**
     * 与列表同筛选条件全量汇总（订单数、订单金额、手续费、到账USDT、到账法币）
     */
    Map<String, Object> getTodayTotalBetAmount(OrderLawWithdraw orderLawWithdraw);

    /**
     * 新增法币提现订单
     *
     * @param orderLawWithdraw 法币提现订单
     * @return 结果
     */
    public int insertOrderLawWithdraw(OrderLawWithdraw orderLawWithdraw);

    /**
     * 修改法币提现订单
     *
     * @param orderLawWithdraw 法币提现订单
     * @return 结果
     */
    public int updateOrderLawWithdraw(OrderLawWithdraw orderLawWithdraw);

    /**
     * 删除法币提现订单
     *
     * @param id 法币提现订单ID
     * @return 结果
     */
    public int deleteOrderLawWithdrawById(Long id);

    /**
     * 批量删除法币提现订单
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteOrderLawWithdrawByIds(Long[] ids);



    List<OrderLawWithdraw> selectOrderLawWithdrawListByUser(@Param("channelId") Long channelId, @Param("id") Integer id, @Param("startTime") String startTime, @Param("endTime") String endTime);


    List<OrderLawWithdraw> selectOrderLawWithdrawListBychannelId(@Param("channelId") Long channelId, @Param("currencyId") Integer currencyId, @Param("startTime") String startTime,
                                                                 @Param("endTime") String endTime);

    Map<String, Object> selectOrderLawWithdrawMapByUser(@Param("channelId") Long channelId, @Param("id") Integer id, @Param("startTime") String startTime, @Param("endTime") String endTime );


    Map<String, Object> orderLawWithdrawCountMap(@Param("startTime") String startTime, @Param("endTime") String endTime
            , @Param("currencyId") Integer currencyId, @Param("userId") Long userId, @Param("orderStatus") Integer orderStatus);


}

