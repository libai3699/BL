package com.gp.common.mybatisplus.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.common.datasource.param.DataSource;
import com.gp.common.mybatisplus.entity.AmountChange;
import com.gp.common.mybatisplus.entity.OrderAmountHeard;
import com.gp.common.mybatisplus.entity.OrderGameCareerHeard;
import com.gp.common.mybatisplus.entity.TUser;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户账变Mapper接口
 *
 * @author axing
 * @date 2023-12-27
 */
public interface AmountChangeMapper extends BaseMapper<AmountChange> {

    @DS(DataSource.doris)
    List<AmountChange> listOmitInfo(@Param("startTime") Date startTime,@Param("endTime") Date endTime,@Param("userId") Long userId,@Param("typeArr") List<Integer> typeArr,@Param("accountType") Integer accountType);

    /**
     * 查询用户账变
     *
     * @param id 用户账变ID
     * @return 用户账变
     */
    @DS(DataSource.doris)
    public AmountChange selectAmountChangeById(Long id);


    /**
     * 查询这个币种最后一条记录
     *
     * @return 结果
     */
    @DS(DataSource.doris)
    AmountChange getLastAmountChange(@Param("userId") Long userId, @Param("currencyId") Integer currencyId);

    @DS(DataSource.doris)
    List<AmountChange> queryNoSign(@Param("start") Integer start, @Param("limit") Integer limit);

    @DS(DataSource.doris)
    OrderAmountHeard queryAmountHeard(@Param("type") Integer type, @Param("userId") Long userId, @Param("startTime") Date startTime, @Param(
            "endTime") Date endTime);

    @DS(DataSource.doris)
    OrderGameCareerHeard queryOrderGameCareerHeard(@Param("userId") Long userId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @DS(DataSource.doris)
    List<AmountChange> selectAmountChangeListBychannelId(@Param("channelId") Long channelId, @Param("id") Integer id,
                                                         @Param("startTime") String startTime, @Param("endTime") String endTime);

    @DS(DataSource.doris)
    List<AmountChange> selectAmountChangeListByTime(@Param("channelId") Long channelId, @Param("startTime") String startTime,
                                                    @Param("endTime") String endTime);

    @DS(DataSource.doris)
    List<AmountChange> selectLastDayData(@Param("startTime") String startTime);

    /**
     * 查询用户账变列表
     *
     * @param tAmountChange 用户账变
     * @return 用户账变集合
     */
    @DS(DataSource.doris)
    public List<AmountChange> selectAmountChangeTodayList(AmountChange tAmountChange);


    @DS(DataSource.doris)
    Integer getActiveUserCountSql(@Param("startTime") String startTime, @Param("endTime") String endTime);

    @DS(DataSource.doris)
    long selectAmountChangeListByTimeSql(@Param("channelId") Long channelId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    @DS(DataSource.doris)
    Map<String, Object> amountChangeCountMap(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("userId") Long userId,
                                             @Param("currencyId") Integer currencyId);

    @DS(DataSource.doris)
    Map<String, Object> selectAmountChangeMapBychannelId(@Param("channelId") Long channelId, @Param("id") Integer id,
                                                         @Param("startTime") String startTime,
                                                         @Param("endTime") String endTime, @Param("accountType") Integer accountType, @Param(
            "orderType") Integer orderType, @Param("type") Integer type);

    @DS(DataSource.doris)
    int selectAmountChangeListByTimeCount(@Param("channelId") Long channelId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    @DS(DataSource.doris)
    Long selectAmountChangeTodayCount(AmountChange tAmountChange);

    @DS(DataSource.doris)
    long selectAmountShareholderByTimeSql(@Param("shareholderId") Long shareholderId, @Param("startTime") String startTime,
                                          @Param("endTime") String endTime);

    @DS(DataSource.doris)
    BigDecimal getRemainIntegral(@Param("endTime") Date endTime, @Param("userIds") List<Long> userIds, @Param("currencyId") Integer currencyId);

    @DS(DataSource.doris)
    Long getDorisMaxId();

    void delByMaxId(@Param("maxId") Long maxId);

    @DS(DataSource.doris)
    List<TUser> selectUserListByTime(@Param("startTime") String startTime, @Param("endTime") String endTime);
}
