package com.gp.common.mybatisplus.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.gp.common.mybatisplus.entity.ChannelCountOrder;
import com.gp.common.mybatisplus.entity.CountOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 每日订单统计Mapper接口
 *
 * @author axing
 * @date 2024-05-09
 */
public interface CountOrderMapper extends BaseMapper<CountOrder>
{
    /**
     * 查询每日订单统计
     *
     * @param id 每日订单统计ID
     * @return 每日订单统计
     */
    public CountOrder selectCountOrderById(Long id);

    /**
     * 查询每日订单统计列表
     *
     * @param countOrder 每日订单统计
     * @return 每日订单统计集合
     */
    public List<CountOrder> selectCountOrderList(CountOrder countOrder);

    /**
     * 新增每日订单统计
     *
     * @param countOrder 每日订单统计
     * @return 结果
     */
    public int insertCountOrder(CountOrder countOrder);

    /**
     * 修改每日订单统计
     *
     * @param countOrder 每日订单统计
     * @return 结果
     */
    public int updateCountOrder(CountOrder countOrder);

    /**
     * 删除每日订单统计
     *
     * @param id 每日订单统计ID
     * @return 结果
     */
    public int deleteCountOrderById(Long id);

    /**
     * 批量删除每日订单统计
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteCountOrderByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();


    CountOrder countOrder(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("currencyId") Integer currencyId);

    CountOrder countOrderAll(@Param("currencyId") Integer currencyId);

    CountOrder countOrderByDay(@Param("day") String startTime,@Param("currencyId") Integer currencyId);

    CountOrder countOrderSum(@Param("startTime") String startTime, @Param("endTime") String endTime,@Param("currencyId") Integer currencyId);

    List<CountOrder> selectSumByMonth(@Param("startTime") String startTime, @Param("endTime") String endTime,@Param("currencyId") Integer currencyId);

    CountOrder selectSumOneByMonthStr(@Param("dayStr") String dayStr,@Param("currencyId") Integer currencyId);

    Integer countOrderByDayNum(@Param("day") String startTim);


    int saveCountOrderBySettle(CountOrder countOrder);


    int updateCountOrderBySettle(CountOrder udpateCountOrder);

    int insertCountOrderDataV2(CountOrder countOrder);

    int updateCountOrderDataV2(CountOrder countOrder);


    CountOrder selectLastDayCountOrder(
            @Param("startTime") String startTime,@Param("endTime")  String endTime
            ,@Param("currencyId")  Integer currencyId,@Param("monthEndTime")  String monthEndTime
    );

    Map<String, BigDecimal> getTotalBetAmount();

    CountOrder getUnclaimedRebateAndCommission(@Param("dayStr") String dayStr);

    CountOrder getUnclaimedRebateByYesterday(@Param("dayStr")String dayStr);

    int calibrateCountOrder(CountOrder countOrder);
}
