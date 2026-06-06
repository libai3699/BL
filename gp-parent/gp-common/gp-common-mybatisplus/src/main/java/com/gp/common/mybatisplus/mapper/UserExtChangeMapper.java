package com.gp.common.mybatisplus.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.common.datasource.param.DataSource;
import com.gp.common.mybatisplus.entity.UserExtChange;
import com.gp.common.mybatisplus.vo.HomeAlreadyRebateAmountExportVO;
import com.gp.common.mybatisplus.vo.HomeAlreadyReturnCommissionAmountExportVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户账变Mapper接口
 *
 * @author axing
 * @date 2024-05-16
 */
public interface UserExtChangeMapper extends BaseMapper<UserExtChange> {
    /**
     * 查询用户账变
     *
     * @param id 用户账变ID
     * @return 用户账变
     */
    @DS(DataSource.doris)
    public UserExtChange selectUserExtChangeById(Long id);





    @DS(DataSource.doris)
    BigDecimal queryTotalMoney(@Param("userId") Long userId, @Param("type") Integer type, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

    @DS(DataSource.doris)
    List<UserExtChange> selectUserExtChangeListByUser(@Param("channelId") Long channelId, @Param("startTime") String startTime, @Param("endTime") String endTime);

    @DS(DataSource.doris)
    List<UserExtChange> selectLastDayData(@Param("startTime") String startTime);



    /**
     * 查询用户账变列表
     *
     * @param userExtChange 用户账变
     * @return 用户账变集合
     */
    @DS(DataSource.doris)
    public List<UserExtChange> selectUserExtChangeTodayList(UserExtChange userExtChange);



    @DS(DataSource.doris)
    Map<String, Object> userExtChangeCountMap(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("userId") Long userId);
    @DS(DataSource.doris)
    Map<String, Object> userExtChangeChannelCountMap(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("channelId") Long channelId);

    @DS(DataSource.doris)
    List<HomeAlreadyRebateAmountExportVO> queryAlreadyRebateAmountDetailByUser(@Param("startTime") String startTime, @Param("endTime") String endTime);

    @DS(DataSource.doris)
    List<HomeAlreadyReturnCommissionAmountExportVO> queryAlreadyReturnCommissionAmountDetailByUser(@Param("startTime") String startTime, @Param("endTime") String endTime);

    @DS(DataSource.doris)
    Long selectUserExtChangeTodayCount(UserExtChange param);



    @DS(DataSource.doris)
    Long getDorisMaxId();

    void delByMaxId(@Param("maxId") Long maxId);
}

