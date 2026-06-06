package com.gp.common.mybatisplus.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.common.datasource.param.DataSource;
import com.gp.common.mybatisplus.entity.UserLotteryAward;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户彩票特殊活动记录Mapper接口
 *
 * @author axing
 * @date 2025-09-16
 */
public interface UserLotteryAwardMapper extends BaseMapper<UserLotteryAward> {
    /**
     * 查询用户彩票特殊活动记录
     *
     * @param id 用户彩票特殊活动记录ID
     * @return 用户彩票特殊活动记录
     */
    public UserLotteryAward selectUserLotteryAwardById(Long id);

    /**
     * 查询用户彩票特殊活动记录列表
     *
     * @param userLotteryAward 用户彩票特殊活动记录
     * @return 用户彩票特殊活动记录集合
     */
    public List<UserLotteryAward> selectUserLotteryAwardList(UserLotteryAward userLotteryAward);

    /**
     * 新增用户彩票特殊活动记录
     *
     * @param userLotteryAward 用户彩票特殊活动记录
     * @return 结果
     */
    public int insertUserLotteryAward(UserLotteryAward userLotteryAward);

    /**
     * 修改用户彩票特殊活动记录
     *
     * @param userLotteryAward 用户彩票特殊活动记录
     * @return 结果
     */
    public int updateUserLotteryAward(UserLotteryAward userLotteryAward);

    /**
     * 删除用户彩票特殊活动记录
     *
     * @param id 用户彩票特殊活动记录ID
     * @return 结果
     */
    public int deleteUserLotteryAwardById(Long id);

    /**
     * 批量删除用户彩票特殊活动记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserLotteryAwardByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();


    BigDecimal selectFromAmountByCreateTime(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("userId") Long userId,
                                            @Param("channelId") Long channelId);
}
