package com.gp.common.mybatisplus.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.entity.UserChannel;
import com.gp.common.mybatisplus.mapper.UserMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.UserDefaultChannelMapper;
import com.gp.common.mybatisplus.entity.UserDefaultChannel;
import com.gp.common.mybatisplus.service.UserDefaultChannelService;

/**
 * 用户下级默认Service业务层处理
 *
 * @author axing
 * @date 2025-10-16
 */
@Service
public class UserDefaultChannelService extends ServiceImpl<UserDefaultChannelMapper, UserDefaultChannel> {
    @Autowired
    private UserDefaultChannelMapper userDefaultChannelMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UserChannelService userChannelService;

    /**
     * 查询用户下级默认
     *
     * @param id 用户下级默认ID
     * @return 用户下级默认
     */

    public UserDefaultChannel selectUserDefaultChannelById(Long id) {
        return userDefaultChannelMapper.selectUserDefaultChannelById(id);
    }

    /**
     * 查询用户下级默认列表
     *
     * @param param 用户下级默认
     * @return 用户下级默认
     */

    public List<UserDefaultChannel> selectUserDefaultChannelList(UserDefaultChannel param) {
        return userDefaultChannelMapper.selectUserDefaultChannelList(param);
    }

    /**
     * 新增用户下级默认
     *
     * @param param 用户下级默认
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertUserDefaultChannel(UserDefaultChannel param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改用户下级默认
     *
     * @param param 用户下级默认
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserDefaultChannel(UserDefaultChannel param) {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除用户下级默认
     *
     * @param ids 需要删除的用户下级默认ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserDefaultChannelByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除用户下级默认信息
     *
     * @param id 用户下级默认ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserDefaultChannelById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    public UserDefaultChannel queryByUserId(Long userId) {
        LambdaQueryWrapper<UserDefaultChannel> q = new LambdaQueryWrapper<>();
        q.eq(UserDefaultChannel::getUserId, userId);
        q.last("limit 1");
        return this.getOne(q);
    }

    public void setDefaultRebateConfig(TUser tUser, UserDefaultChannel finalUserDefaultChannel) {
        //查询他的下级
        Long userId = tUser.getUserId();
        Long channelId = tUser.getChannelId();
        List<TUser> tUsers = userService.queryLowUser(userId);
        for (TUser item : tUsers) {
            UserChannel userChannel = new UserChannel();
            userChannel.setUserId(item.getUserId());
            userChannel.setChannelId(channelId);
            userChannel.setPid(item.getSuperUserId());
            userChannel.setPPath(item.getPPath());
            userChannel.setSuperUserRebate1(finalUserDefaultChannel.getSuperUserRebate1());
            userChannel.setSuperUserRebate2(finalUserDefaultChannel.getSuperUserRebate2());
            userChannel.setSuperUserRebate3(finalUserDefaultChannel.getSuperUserRebate3());
            userChannel.setSuperUserRebate4(finalUserDefaultChannel.getSuperUserRebate4());
            userChannel.setSuperUserRebate5(finalUserDefaultChannel.getSuperUserRebate5());
            userChannel.setSuperUserRebate6(finalUserDefaultChannel.getSuperUserRebate6());
            userChannel.setSuperUserRebate7(finalUserDefaultChannel.getSuperUserRebate7());
            userChannel.setSuperUserRebate8(finalUserDefaultChannel.getSuperUserRebate8());
            userChannel.setSuperUserRebate9(finalUserDefaultChannel.getSuperUserRebate9());
            userChannel.setDividendStatus(finalUserDefaultChannel.getDividendStatus());
            userChannel.setDividendRebate(finalUserDefaultChannel.getDividendRebate());
            userChannelService.insertTUserChannelIfNotExist(userChannel);
        }
    }

    public void setDefaultRebateConfigItem(TUser tUser, UserDefaultChannel finalUserDefaultChannel) {
        //查询他的下级
        UserChannel userChannel = new UserChannel();
        userChannel.setUserId(tUser.getUserId());
        userChannel.setChannelId(tUser.getChannelId());
        userChannel.setPid(tUser.getSuperUserId());
        userChannel.setPPath(tUser.getPPath());
        userChannel.setSuperUserRebate1(finalUserDefaultChannel.getSuperUserRebate1());
        userChannel.setSuperUserRebate2(finalUserDefaultChannel.getSuperUserRebate2());
        userChannel.setSuperUserRebate3(finalUserDefaultChannel.getSuperUserRebate3());
        userChannel.setSuperUserRebate4(finalUserDefaultChannel.getSuperUserRebate4());
        userChannel.setSuperUserRebate5(finalUserDefaultChannel.getSuperUserRebate5());
        userChannel.setSuperUserRebate6(finalUserDefaultChannel.getSuperUserRebate6());
        userChannel.setSuperUserRebate7(finalUserDefaultChannel.getSuperUserRebate7());
        userChannel.setSuperUserRebate8(finalUserDefaultChannel.getSuperUserRebate8());
        userChannel.setSuperUserRebate9(finalUserDefaultChannel.getSuperUserRebate9());
        userChannel.setDividendStatus(finalUserDefaultChannel.getDividendStatus());
        userChannel.setDividendRebate(finalUserDefaultChannel.getDividendRebate());
        userChannelService.insertTUserChannelIfNotExist(userChannel);
    }
}

