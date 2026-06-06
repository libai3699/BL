package com.gp.common.mybatisplus.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.common.datasource.param.DataSource;
import com.gp.common.mybatisplus.dto.EsUserAmountSummary;
import com.gp.common.mybatisplus.entity.OrderBetHeard;
import com.gp.common.mybatisplus.entity.OrderTerm;
import com.gp.common.mybatisplus.vo.UserGameBetStatVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户投注Mapper接口
 *
 * @author axing
 * @date 2024-05-09
 */
public interface OrderTermMapper extends BaseMapper<OrderTerm> {
    /**
     * 查询用户投注
     *
     * @param id 用户投注ID
     * @return 用户投注
     */
    @DS(DataSource.doris)
    public OrderTerm selectOrderTermById(Long id);

    /**
     * 查询用户投注
     *
     * @param orderNo 订单号
     * @return 用户投注
     */
    @DS(DataSource.doris)
    public OrderTerm selectOrderTermByOrderNo(String orderNo);

    /**
     * 获取投注总金额
     *
     * @param params
     * @return
     */
    @DS(DataSource.doris)
    Map<String, Object> getTodayTotalBetAmount(OrderTerm params);


    /**
     * 获取已经投注结算的总金额
     *
     * @param params
     * @return
     */
    @DS(DataSource.doris)
    BigDecimal getTodaySettleBetAmount(OrderTerm params);

    @DS(DataSource.doris)
    OrderBetHeard queryBetAmountHeard(@Param("gameType") Integer gameType, @Param("orderStatus") Integer orderStatus, @Param("userId") Long userId,
                                      @Param("startTime") Date startTime, @Param("endTime") Date endTime);
    @DS(DataSource.doris)
    OrderBetHeard queryBetAmountHeardByTypeCodes(@Param("gameTypeCodes") List<Integer> gameTypeCodes, @Param("orderStatus") Integer orderStatus,
                                                 @Param("userId") Long userId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @DS(DataSource.doris)
    OrderBetHeard queryMyRebateHeard(@Param("userId") Long userId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @DS(DataSource.doris)
    List<OrderTerm> queryOrderTermByTime(@Param("endTime") Date endTime, @Param("moreBig") BigDecimal moreBig);

    @DS(DataSource.doris)
    Map<String, Object> selectUnsettledBetsOrderTermList(@Param("startTime") String startTime);

    @DS(DataSource.doris)
    List<OrderTerm> selectOrderTermListByChannelId(@Param("channelId") Long channelId, @Param("threeDayEndTimeStr") String threeDayEndTime, @Param(
            "startTime") String startTime,
                                                   @Param("endTime") String endTime);

    @DS(DataSource.doris)
    List<OrderTerm> selectOrderTermListByChannelIds(@Param("threeDayEndTime") String threeDayEndTime, @Param("startTime") String startTime, @Param(
            "endTime") String endTime);

    @DS(DataSource.doris)
    List<OrderTerm> selectLastDayData(@Param("startTime") String startTime, @Param("settleTime") String settleTime);


    /**
     * 查询用户投注列表
     *
     * @param orderTerm 用户投注
     * @return 用户投注集合
     */
    @DS(DataSource.doris)
    public List<OrderTerm> selectOrderTermTodayList(OrderTerm orderTerm);



    /**
     * 获取没有投注结算的总金额
     *
     * @param param
     * @return
     */
    @DS(DataSource.doris)
    BigDecimal getTodayUnsettleBetAmount(OrderTerm param);



    /**
     * 获取当日未结算注单数量
     *
     * @param params
     * @return
     */
    @DS(DataSource.doris)
    Integer getTodayUnsettleBetNum(OrderTerm params);

    @DS(DataSource.doris)
    List<OrderTerm> customQuery(@Param("channelUserIdList") List<Long> channelUserIdList, @Param("startTime") String startTime,
                                @Param("endTime") String endTime, @Param("channelId") Long channelId);

    @DS(DataSource.doris)
    Map<String, Object> orderTermCountMap(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                          @Param("currencyId") Integer currencyId, @Param("userId") Long userId);

    @DS(DataSource.doris)
    Map<String, Object> orderTermSettleTimeCountMap(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                                    @Param("currencyId") Integer currencyId, @Param("userId") Long userId);

    @DS(DataSource.doris)
    List<Map<String, Object>> gameTypeCodeMapCountMap(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                                      @Param("currencyId") Integer currencyId, @Param("userId") Long userId);

    @DS(DataSource.doris)
    Map<String, Object> betOrderTermMap(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("id") Integer id, @Param(
            "channelId") Long channelId);

    @DS(DataSource.doris)
    Map<String, Object> settleOrderTermMap(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("id") Integer id, @Param(
            "channelId") Long channelId);

    @DS(DataSource.doris)
    List<Map<String, Object>> gameTypeCodeChanneCountMap(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("currencyId"
    ) Integer currencyId,
                                                         @Param("channelId") Long channelId);

    @DS(DataSource.doris)
    Map<String, Object> gameTypeByCreateTimeCountMap(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                                     @Param("currencyId") Integer currencyId,
                                                     @Param("typeCode") String typeCode);

    @DS(DataSource.doris)
    Map<String, Object> gameTypeBySettleTimeCountMap(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                                     @Param("currencyId") Integer currencyId,
                                                     @Param("typeCode") String typeCode);

    @DS(DataSource.doris)
    Long selectOrderTermTodayCount(OrderTerm param);



    @DS(DataSource.doris)
    Map<String, Object> fetchOrderTermsBycreatTime(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                                   @Param("plateCode") String plateCode, @Param("currencyId") Integer currencyId);

    @DS(DataSource.doris)
    Map<String, Object> fetchOrderTermsBysettleTime(@Param("startTime") String startTime, @Param("endTime") String endTime,
                                                    @Param("plateCode") String plateCode, @Param("currencyId") Integer currencyId);

    @DS(DataSource.doris)
    List<EsUserAmountSummary> getDetailedSummaryByUserId(OrderTerm param);

    @DS(DataSource.doris)
    Map<String, Object> selectUnsettledBetsByUserList(@Param("channelUserList") List<Long> channelUserList);

    @DS(DataSource.doris)
    Map<String, Object> selectUnsettledBetsByChannelId(@Param("channelId") Long channelId);

    @DS(DataSource.doris)
    Long getDorisMaxId();

//    删除体育的注单
    void delByMaxId(Long maxId);
//    删除电子的注单
    void delByMaxId2(Long maxId);

    @DS(DataSource.doris)
    Long getDetailedSummaryByUserIdCount(OrderTerm param);
    @DS(DataSource.doris)
    List<EsUserAmountSummary> getDetailedSummaryByUserIds(OrderTerm param);

    @DS(DataSource.doris)
    List<EsUserAmountSummary> getWinLossList(OrderTerm param);
    @DS(DataSource.doris)
    List<UserGameBetStatVO> selectGameByUserId(OrderTerm param);
    @DS(DataSource.doris)
    List<UserGameBetStatVO> selectGameDealerByUserId(OrderTerm param);

    @DS(DataSource.doris)
    List<OrderTerm> queryBetRecord(@Param("startTime") Date startTime,@Param("endTime") Date endTime,@Param("userId") Long userId,@Param("gameTypeCodeArr") List<Integer> gameTypeCodeArr,@Param("orderStatus") Integer orderStatus);
    @DS(DataSource.doris)
    List<UserGameBetStatVO> selectGameTypeByUserId(OrderTerm param);
}
