package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.ChannelCountOrder;
import com.gp.common.mybatisplus.entity.UserCountTotalAmount;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 渠道每日订单统计Mapper接口
 *
 * @author axing
 * @date 2024-05-24
 */
public interface ChannelCountOrderMapper extends BaseMapper<ChannelCountOrder> {
    /**
     * 查询渠道每日订单统计
     *
     * @param id 渠道每日订单统计ID
     * @return 渠道每日订单统计
     */
    public ChannelCountOrder selectChannelCountOrderById(Long id);

    /**
     * 查询渠道每日订单统计列表
     *
     * @param channelCountOrder 渠道每日订单统计
     * @return 渠道每日订单统计集合
     */
    public List<ChannelCountOrder> selectChannelCountOrderList(ChannelCountOrder channelCountOrder);

    /**
     * 新增渠道每日订单统计
     *
     * @param channelCountOrder 渠道每日订单统计
     * @return 结果
     */
    public int insertChannelCountOrder(ChannelCountOrder channelCountOrder);

    /**
     * 修改渠道每日订单统计
     *
     * @param channelCountOrder 渠道每日订单统计
     * @return 结果
     */
    public int updateChannelCountOrder(ChannelCountOrder channelCountOrder);

    /**
     * 删除渠道每日订单统计
     *
     * @param id 渠道每日订单统计ID
     * @return 结果
     */
    public int deleteChannelCountOrderById(Long id);

    /**
     * 批量删除渠道每日订单统计
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteChannelCountOrderByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    ChannelCountOrder countOrderSum(
            @Param("startTime") String startTime, @Param("endTime") String endTime
            , @Param("currencyId") Integer currencyId, @Param("channelId") Long channelId,
            @Param("channelIdList") List<Long> channelIdList);

    ChannelCountOrder countOrderSumShareholder(@Param("startTime") String startTime, @Param("endTime") String endTime
            , @Param("currencyId") Integer currencyId, @Param("shareholderId") Long shareholderId,
                                               @Param("shareholderIdList") List<Long> shareholderIdList);

    List<ChannelCountOrder> selectSumByMonth(
            @Param("startTime") String startTime, @Param("endTime") String endTime
            , @Param("currencyId") Integer currencyId, @Param("channelId") Long channelId,
            @Param("channelIdList") List<Long> channelIdList
    );

    List<ChannelCountOrder> selectSumByMonthShareholder(@Param("startTime") String startTime, @Param("endTime") String endTime
            , @Param("currencyId") Integer currencyId, @Param("shareholderId") Long shareholderId,@Param("shareholderIdList") List<Long> shareholderIdList);

    ChannelCountOrder selectSumOneByMonthStr(
            @Param("dayStr") String dayStr
            , @Param("currencyId") Integer currencyId
            , @Param("channelId") Long channelId
    );

    ChannelCountOrder selectSumOneByMonthShareholder(@Param("dayStr") String dayStr
            , @Param("currencyId") Integer currencyId
            , @Param("shareholderId") Long shareholderId);

    UserCountTotalAmount querySumAmountByUserIdAndTime(@Param("channelId") Long channelId, @Param("startTime") Date startTime,
                                                       @Param("endTime") Date endTime);

    UserCountTotalAmount querySumAmountByUserIdsAndTime(@Param("shareholderId") Long shareholderId, @Param("channelId") Long channelId, @Param(
            "startTime") Date startTime, @Param("endTime") Date endTime);

    ChannelCountOrder queryByChannelId(@Param("channelId") Long channelId);

    int saveChannelCountOrderBySettle(ChannelCountOrder channelCountOrder);

    int updateChannelCountOrderBySettle(ChannelCountOrder updateChannelCountOrder);

    int insertChannelCountOrderDataV2(ChannelCountOrder channelCountOrder);

    int updateChannelCountOrderDataV2(ChannelCountOrder channelCountOrder);

    List<ChannelCountOrder> getByDayChannelCount(@Param("startTime") String startTime, @Param("endTime") String endTime
            , @Param("currencyId") Integer currencyId, @Param("channelIdList") List<Long> channelIdList);

    ChannelCountOrder selectRebateAndCommissionSummary(@Param("endTime") String endTime, @Param("channelId") Long channelId,
                                                       @Param("shareholderId") Long shareholderId);

    List<ChannelCountOrder> getByDayShareholderCount(@Param("startTime") String startTime, @Param("endTime") String endTime
            , @Param("currencyId") Integer currencyId, @Param("shareholderIdList") List<Long> shareholderIdList);

    List<ChannelCountOrder> getByMonthChannelCount(@Param("startTime") String startTime, @Param("endTime") String endTime
            , @Param("currencyId") Integer currencyId, @Param("channelIdList") List<Long> channelIdList);

    List<ChannelCountOrder> getByMonthShareholderCount(@Param("startTime") String startTime, @Param("endTime") String endTime
            , @Param("currencyId") Integer currencyId, @Param("shareholderIdList") List<Long> shareholderIdList);

    ChannelCountOrder selectLastDayCountOrder(@Param("channelId")Long channelId, @Param("shareholderId") Long shareholderId, @Param("monthEndTime") String monthEndTime);

    ChannelCountOrder getUnclaimedRebateAndCommission
            (@Param("channelId")Long channelId,@Param("shareholderId") Long shareholderId,@Param("dayStr") String dayStr);

    ChannelCountOrder getUnclaimedRebateByYesterday
            (@Param("channelIdList") List<Long> channelIdList,@Param("shareholderIdList") List<Long> shareholderIdList, @Param("dayStr") String dayStr);

    /**
     * 统计渠道分组导出数据总量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param currencyId 币种ID
     * @param channelIdList 渠道ID列表
     * @return 数据总量
     */
    int countByDayChannelCount(@Param("startTime") String startTime, @Param("endTime") String endTime
            , @Param("currencyId") Integer currencyId, @Param("channelIdList") List<Long> channelIdList);

    /**
     * 统计股东分组导出数据总量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param currencyId 币种ID
     * @param shareholderIdList 股东ID列表
     * @return 数据总量
     */
    int countByDayShareholderCount(@Param("startTime") String startTime, @Param("endTime") String endTime
            , @Param("currencyId") Integer currencyId, @Param("shareholderIdList") List<Long> shareholderIdList);

    /**
     * 统计渠道分组月数据导出总量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param currencyId 币种ID
     * @param channelIdList 渠道ID列表
     * @return 数据总量
     */
    int countByMonthChannelCount(@Param("startTime") String startTime, @Param("endTime") String endTime
            , @Param("currencyId") Integer currencyId, @Param("channelIdList") List<Long> channelIdList);

    /**
     * 统计股东分组月数据导出总量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param currencyId 币种ID
     * @param shareholderIdList 股东ID列表
     * @return 数据总量
     */
    int countByMonthShareholderCount(@Param("startTime") String startTime, @Param("endTime") String endTime
            , @Param("currencyId") Integer currencyId, @Param("shareholderIdList") List<Long> shareholderIdList);

    int calibrateChannelCountOrder(ChannelCountOrder channelCountOrder);
}
