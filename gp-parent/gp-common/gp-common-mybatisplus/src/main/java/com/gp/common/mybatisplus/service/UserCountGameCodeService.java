package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.GameBetAmountTotalAmount;
import com.gp.common.mybatisplus.entity.UserCountGameCode;
import com.gp.common.mybatisplus.mapper.UserCountGameCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 用户游戏类型打码量统计Service业务层处理
 *
 * @author axing
 * @date 2025-06-23
 */
@Service
public class UserCountGameCodeService extends ServiceImpl<UserCountGameCodeMapper, UserCountGameCode>
{
    @Autowired
    private UserCountGameCodeMapper userCountGameCodeMapper;

    /**
     * 查询用户游戏类型打码量统计
     *
     * @param id 用户游戏类型打码量统计ID
     * @return 用户游戏类型打码量统计
     */

    public UserCountGameCode selectUserCountGameCodeById(Long id)
    {
        return userCountGameCodeMapper.selectUserCountGameCodeById(id);
    }

    /**
     * 查询用户游戏类型打码量统计列表
     *
     * @param param 用户游戏类型打码量统计
     * @return 用户游戏类型打码量统计
     */

    public List<UserCountGameCode> selectUserCountGameCodeList(UserCountGameCode param)
    {
        return userCountGameCodeMapper.selectUserCountGameCodeList(param);
    }

    /**
     * 新增用户游戏类型打码量统计
     *
     * @param param 用户游戏类型打码量统计
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertUserCountGameCode(UserCountGameCode param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改用户游戏类型打码量统计
     *
     * @param param 用户游戏类型打码量统计
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserCountGameCode(UserCountGameCode param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除用户游戏类型打码量统计
     *
     * @param ids 需要删除的用户游戏类型打码量统计ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserCountGameCodeByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除用户游戏类型打码量统计信息
     *
     * @param id 用户游戏类型打码量统计ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserCountGameCodeById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }

    public GameBetAmountTotalAmount querySumAmountByUserIdAndTime(List<Long> userIds, Date startTime, Date endTime) {
        return userCountGameCodeMapper.querySumAmountByUserIdAndTime(userIds,startTime,endTime);

    }

    public List<UserCountGameCode> queryAllUserCountGameCode(Date date) {
        LambdaQueryWrapper<UserCountGameCode> q = new LambdaQueryWrapper<>();
        Date startTimeByDate = DateUtils.getStartTimeByDate(date);
        Date endTimeByDate = DateUtils.getEndTimeByDate(date);
        // 获取昨天的日期范围
        // 查询创建时间是昨天的记录
        q.between(UserCountGameCode::getCreateTime, startTimeByDate, endTimeByDate);

        return this.list(q);
    }
}
