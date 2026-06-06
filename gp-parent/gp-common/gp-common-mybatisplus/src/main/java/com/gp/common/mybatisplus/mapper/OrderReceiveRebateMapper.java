package com.gp.common.mybatisplus.mapper;

import java.math.BigDecimal;
import java.util.List;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.common.datasource.param.DataSource;
import com.gp.common.mybatisplus.entity.OrderReceiveRebate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 领取返水返佣记录Mapper接口
 *
 * @author axing
 * @date 2024-05-17
 */
public interface OrderReceiveRebateMapper extends BaseMapper<OrderReceiveRebate>
{
    /**
     * 查询领取返水返佣记录
     *
     * @param id 领取返水返佣记录ID
     * @return 领取返水返佣记录
     */

    public OrderReceiveRebate selectOrderReceiveRebateById(Long id);

    /**
     * 查询领取返水返佣记录列表
     *
     * @param orderReceiveRebate 领取返水返佣记录
     * @return 领取返水返佣记录集合
     */

    public List<OrderReceiveRebate> selectOrderReceiveRebateList(OrderReceiveRebate orderReceiveRebate);

    /**
     * 查询领取返水返佣记录列表数量
     *
     * @param orderReceiveRebate 领取返水返佣记录
     * @return 领取返水返佣记录集合数量
     */

    public int selectOrderReceiveRebateListCount(OrderReceiveRebate orderReceiveRebate);

    /**
     * 新增领取返水返佣记录
     *
     * @param orderReceiveRebate 领取返水返佣记录
     * @return 结果
     */
    public int insertOrderReceiveRebate(OrderReceiveRebate orderReceiveRebate);

    /**
     * 修改领取返水返佣记录
     *
     * @param orderReceiveRebate 领取返水返佣记录
     * @return 结果
     */
    public int updateOrderReceiveRebate(OrderReceiveRebate orderReceiveRebate);

    /**
     * 删除领取返水返佣记录
     *
     * @param id 领取返水返佣记录ID
     * @return 结果
     */
    public int deleteOrderReceiveRebateById(Long id);

    /**
     * 批量删除领取返水返佣记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteOrderReceiveRebateByIds(Long[] ids);

    /**
     * 当日渠道下用户领取的代理工资（t_order_receive_rebate.record_type = 3）
     */
    BigDecimal sumClaimedAgentSalaryByChannel(@Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("channelId") Long channelId,
            @Param("currencyId") Integer currencyId);

    /**
     * 当日全站（不按渠道/用户）领取的代理工资
     */
    BigDecimal sumClaimedAgentSalaryByDayCurrency(@Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("currencyId") Integer currencyId);

    /**
     * 当日某用户领取的代理工资
     */
    BigDecimal sumClaimedAgentSalaryByUser(@Param("startTime") String startTime,
            @Param("endTime") String endTime,
            @Param("userId") Long userId,
            @Param("currencyId") Integer currencyId);

}