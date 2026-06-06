package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.UserLotteryAward;
import com.gp.common.mybatisplus.mapper.UserLotteryAwardMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 用户彩票特殊活动记录Service业务层处理
 *
 * @author axing
 * @date 2025-09-16
 */
@Service
public class UserLotteryAwardService extends ServiceImpl<UserLotteryAwardMapper, UserLotteryAward> {
    @Resource
    private UserLotteryAwardMapper userLotteryAwardMapper;

    /**
     * 查询用户彩票特殊活动记录
     *
     * @param id 用户彩票特殊活动记录ID
     * @return 用户彩票特殊活动记录
     */

    public UserLotteryAward selectUserLotteryAwardById(Long id) {
        return userLotteryAwardMapper.selectUserLotteryAwardById(id);
    }

    /**
     * 查询用户彩票特殊活动记录列表
     *
     * @param param 用户彩票特殊活动记录
     * @return 用户彩票特殊活动记录
     */

    public List<UserLotteryAward> selectUserLotteryAwardList(UserLotteryAward param) {
        return userLotteryAwardMapper.selectUserLotteryAwardList(param);
    }

    /**
     * 新增用户彩票特殊活动记录
     *
     * @param param 用户彩票特殊活动记录
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertUserLotteryAward(UserLotteryAward param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改用户彩票特殊活动记录
     *
     * @param param 用户彩票特殊活动记录
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserLotteryAward(UserLotteryAward param) {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除用户彩票特殊活动记录
     *
     * @param ids 需要删除的用户彩票特殊活动记录ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserLotteryAwardByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除用户彩票特殊活动记录信息
     *
     * @param id 用户彩票特殊活动记录ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserLotteryAwardById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    /**
     * 查询指定创建时间范围内的总来源金额
     */
    public BigDecimal selectFromAmountByCreateTime(String startTime, String endTime, Long userId,Long channelId) {
        return userLotteryAwardMapper.selectFromAmountByCreateTime(startTime, endTime, userId, channelId);
    }
}
