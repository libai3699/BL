package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.*;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 每日用户订单统计Mapper接口
 *
 * @author axing
 * @date 2024-05-10
 */
public interface UserCountOrderMapper extends BaseMapper<UserCountOrder> {
    /**
     * 查询每日用户订单统计
     *
     * @param id 每日用户订单统计ID
     * @return 每日用户订单统计
     */
    public UserCountOrder selectUserCountOrderById(Long id);

    /**
     * 查询每日用户订单统计列表
     *
     * @param userCountOrder 每日用户订单统计
     * @return 每日用户订单统计集合
     */
    public List<UserCountOrder> selectUserCountOrderList(UserCountOrder userCountOrder);

    public List<UserCountOrder> topRankingList(UserCountOrder userCountOrder);

    List<UserCountOrder> betRankingList(UserCountOrder param);

    List<UserCountOrder> winRankingList(UserCountOrder param);

    List<UserCountOrder> agencyRankingListByChannel(@Param("param") UserCountOrder param, @Param("channelIds") List<Long> channelIds);

    /**
     * 新增每日用户订单统计
     *
     * @param userCountOrder 每日用户订单统计
     * @return 结果
     */
    public int insertUserCountOrder(UserCountOrder userCountOrder);

    /**
     * 修改每日用户订单统计
     *
     * @param userCountOrder 每日用户订单统计
     * @return 结果
     */
    public int updateUserCountOrder(UserCountOrder userCountOrder);

    /**
     * 删除每日用户订单统计
     *
     * @param id 每日用户订单统计ID
     * @return 结果
     */
    public int deleteUserCountOrderById(Long id);

    /**
     * 批量删除每日用户订单统计
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserCountOrderByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    OrderAmountHeard queryHeard(@Param("userId") Long userId, @Param("date") String date, @Param("startTime") Date startTime,
                                @Param("endTime") Date endTime);

    OrderBetHeard queryBetHeard(@Param("userId") Long userId, @Param("date") String date, @Param("startTime") Date startTime,
                                @Param("endTime") Date endTime);

    UserCountTotalAmount queryTotalNotDayAmount(@Param("userId") Long userId, @Param("date") String dayStr);

    UserCountTotalAmount queryTotalAmount(@Param("userId") Long userId);

    UserCountTotalAmount querySumAmountByUserIdAndTime(@Param("arr") List<Long> userIdLIst, @Param("startTime") Date startTime,
                                                       @Param("endTime") Date endTime);

    OrderGameCareerHeard queryOrderGameCareerHeard(@Param("userId") Long userId, @Param("date") String dateStr);

    int saveUserCountOrderWaitReturnCommissionAmountData(
            @Param("dayStr") String dayStr, @Param("userId") Long userId
            , @Param("currencyId") Integer currencyId, @Param("channelId") Long channelId
            , @Param("waitReturnCommissionAmount") BigDecimal waitReturnCommissionAmount
    );

    int updateUserCountOrderWaitReturnCommissionAmountData(@Param("id") Long id,
                                                           @Param("waitReturnCommissionAmount") BigDecimal waitReturnCommissionAmount);

    int saveUserCountOrderBySettle(UserCountOrder userCountOrder);

    int updateUserCountOrderBySettle(UserCountOrder userCountOrder);

    void updateDemo();

    int saveUserCountOrderData(UserCountOrder userCountOrder);

    int updateUserCountOrderData(UserCountOrder userCountOrder);

    UserCountOrder selectTeamReportCountList(
            @Param("start") String start, @Param("end") String end, @Param("userIdList") List<Long> userIdList
    );

    List<UserLowM> queryAgentSumAmountByUserNameAndTime(@Param("userName") String userName, @Param("userId") Long userId,
                                                        @Param("startTime") Date startTime, @Param("endTime") Date endTime,
                                                        @Param("sort") Integer sort);

    List<UserLowM> queryAgentSumAmountByUserNameAndTimeNewAddUser(@Param("userName") String userName, @Param("userId") Long userId,
                                                                  @Param("startTime") Date startTime, @Param("endTime") Date endTime,
                                                                  @Param("sort") Integer sort);

    List<UserLowM> queryChannelSumAmountByUserNameAndTime(@Param("userName") String userName, @Param("channelId") Long agentId,
                                                          @Param("startTime") Date startTime, @Param("endTime") Date endTime,
                                                          @Param("sort") Integer sort);

    List<UserLowM> queryChannelSumAmountByUserNameAndTimeNewAddUser(@Param("userName") String userName, @Param("channelId") Long agentId,
                                                                    @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param(
                                                                            "sort") Integer sort);

    List<UserLowM> queryAgentSumAmountByUserNameAndTimeByPid(@Param("userName") String userName, @Param("userId") Long userId,
                                                             @Param("startTime") Date startTime, @Param("endTime") Date endTime,
                                                             @Param("sort") Integer sort);

    List<UserLowM> queryAgentSumAmountByUserNameAndTimeByPidNewAddUser(@Param("userName") String userName, @Param("userId") Long userId,
                                                                       @Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param(
                                                                               "sort") Integer sort);

    List<UserCountOrder> getByUserIdlist(UserCountOrder userCountOrder);

    /**
     * 两阶段分页 Phase A：仅查当前页的 user_id 列表（不做 80+ 列 SUM 聚合，配合 PageHelper 自动 COUNT + LIMIT 使用）。
     * Phase B 再调用 {@link #getByUserIdlist(UserCountOrder)} 并把 userIds 设为本方法返回的 id 列表，做窄聚合。
     */
    List<Long> pageUserIdsForGroupList(UserCountOrder userCountOrder);

    UserCountOrder countSum(UserCountOrder param);

    BigDecimal querySumCustomerLossAmount(@Param("userId") Long userId);

    List<UserCountOrder> getMonthList(UserCountOrder param);

    UserCountOrder selectSumOneByMonthStr(UserCountOrder param);

    /**
     * 查询每日用户订单统计数据条数
     *
     * @param userCountOrder 每日用户订单统计
     * @return 结果
     */
    public int selectUserCountOrderCount(UserCountOrder userCountOrder);

    /**
     * 查询分组用户订单统计数据条数
     *
     * @param userCountOrder 每日用户订单统计
     * @return 结果
     */
    public int selectUserCountOrderGroupCount(UserCountOrder userCountOrder);

    List<Map<String, Object>> queryCustomerLossMap(@Param("userIds") List<Long> userIds);

    List<Map<String, Object>> queryUserCodeAmountTotal(@Param("userId") Long userId);

    /**
     * 统计校准 — 用差值增量修正 t_user_count_order
     * 不存在则插入，存在则累加差值
     */
    int calibrateUserCountOrder(UserCountOrder userCountOrder);

    List<UserCountOrder> batchGetLatestWaitAmounts(@Param("userIds") Set<Long> userIds, @Param("endTime") String endTime);

}
