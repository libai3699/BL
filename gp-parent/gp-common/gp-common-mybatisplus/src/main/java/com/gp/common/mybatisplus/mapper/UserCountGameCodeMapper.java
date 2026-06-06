package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.GameBetAmountTotalAmount;
import com.gp.common.mybatisplus.entity.UserCountGameCode;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 用户游戏类型打码量统计Mapper接口
 *
 * @author axing
 * @date 2025-06-23
 */
public interface UserCountGameCodeMapper extends BaseMapper<UserCountGameCode>
{
    /**
     * 查询用户游戏类型打码量统计
     *
     * @param id 用户游戏类型打码量统计ID
     * @return 用户游戏类型打码量统计
     */
    public UserCountGameCode selectUserCountGameCodeById(Long id);

    /**
     * 查询用户游戏类型打码量统计列表
     *
     * @param userCountGameCode 用户游戏类型打码量统计
     * @return 用户游戏类型打码量统计集合
     */
    public List<UserCountGameCode> selectUserCountGameCodeList(UserCountGameCode userCountGameCode);

    /**
     * 新增用户游戏类型打码量统计
     *
     * @param userCountGameCode 用户游戏类型打码量统计
     * @return 结果
     */
    public int insertUserCountGameCode(UserCountGameCode userCountGameCode);

    /**
     * 修改用户游戏类型打码量统计
     *
     * @param userCountGameCode 用户游戏类型打码量统计
     * @return 结果
     */
    public int updateUserCountGameCode(UserCountGameCode userCountGameCode);

    /**
     * 删除用户游戏类型打码量统计
     *
     * @param id 用户游戏类型打码量统计ID
     * @return 结果
     */
    public int deleteUserCountGameCodeById(Long id);

    /**
     * 批量删除用户游戏类型打码量统计
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserCountGameCodeByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();


    GameBetAmountTotalAmount querySumAmountByUserIdAndTime(@Param("arr")List<Long> userIdLIst, @Param("startTime") Date startTime, @Param("endTime")Date endTime);

    int saveUserCountGameCodeBySettle(UserCountGameCode userCountGameCode);

    int updateUserCountGameCodeBySettle(UserCountGameCode userCountGameCode);

    int calibrateUserCountGameCode(UserCountGameCode userCountGameCode);
}
