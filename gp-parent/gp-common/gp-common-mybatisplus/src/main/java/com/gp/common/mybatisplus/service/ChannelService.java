package com.gp.common.mybatisplus.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.Channel;
import com.gp.common.mybatisplus.entity.ChannelConfig;
import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.mapper.ChannelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 渠道Service业务层处理
 *
 * @author axing
 * @date 2024-05-08
 */
@Service
@Slf4j
public class ChannelService extends ServiceImpl<ChannelMapper, Channel> {
    @Resource
    private ChannelMapper channelMapper;

    @Resource
    private ChannelConfigService channelConfigService;

    @Resource
    private UserService userService;

    @Resource
    public ConfigRiskService configRiskService;

    /**
     * 查询渠道
     *
     * @param id 渠道ID
     * @return 渠道
     */

    public Channel selectChannelById(Long id) {
        return channelMapper.selectChannelById(id);
    }

    /**
     * 查询渠道列表
     *
     * @param param 渠道
     * @return 渠道
     */

    public List<Channel> selectChannelList(Channel param) {
        return channelMapper.selectChannelList(param);
    }

    /**
     * 查询渠道列表数量
     *
     * @param param 渠道
     * @return 渠道数量
     */
    public int selectChannelListCount(Channel param) {
        return channelMapper.selectChannelListCount(param);
    }

    /**
     * 新增渠道
     *
     * @param param 渠道
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertChannel(Channel param, String operateName) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        //修改用户渠道, 用户成为最高级源头, 他和他的下级要修改渠道id, 他的下级要根据pid,修改ppath
        LambdaUpdateWrapper<TUser> updateWrapper = Wrappers.lambdaUpdate(TUser.class);
        updateWrapper.eq(TUser::getUserId, param.getUserId())
                .set(TUser::getChannelId, param.getId())
                .set(TUser::getPPath, null)
                .set(TUser::getSuperUserId, null)
                .set(TUser::getSuperUserTgId, null)
        ;
        userService.update(updateWrapper);

        //还要修改用户的下级用户列表,遍历更新下级所有用户
        for (TUser tUser : userService.list(Wrappers.lambdaQuery(TUser.class).eq(TUser::getSuperUserId, param.getUserId()))) {
            userService.setUserChannel(tUser, userService.getById(param.getUserId()), operateName);
        }
        return result;
    }

    public Boolean getChannelCodeCount(String channelCode) {
        int channelCodeCount = channelMapper.getChannelCodeCount(channelCode);
        if (channelCodeCount == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 修改渠道
     *
     * @param param 渠道
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateChannel(Channel param) {
        param.setUpdateTime(DateUtils.getNowDate());
        Channel byId = this.getById(param.getId());
        //彩票模式不修改用户
        Boolean lotteryModel = configRiskService.isLotteryModel();
        if (lotteryModel) {
            param.setUserId(byId.getUserId());
            param.setUserTgId(byId.getUserTgId());
            param.setUserTgName(byId.getUserTgName());
            param.setUserTgUsername(byId.getUserTgUsername());
            return this.updateById(param);
        }
        //修改用户渠道
        if (!byId.getUserId().equals(param.getUserId())) {
            LambdaUpdateWrapper<TUser> updateWrapper = Wrappers.lambdaUpdate(TUser.class);
            updateWrapper.eq(TUser::getUserId, byId.getUserId())
                    .set(TUser::getShareholderId, 0)
                    .set(TUser::getChannelId, 0);
            userService.update(updateWrapper);
            //还要修改渠道配置绑定的userid
            channelConfigService.update(Wrappers.lambdaUpdate(ChannelConfig.class).set(ChannelConfig::getUserId, param.getUserId()).eq(ChannelConfig::getChannelId, byId.getId()));
        }
        LambdaUpdateWrapper<TUser> updateWrapper = Wrappers.lambdaUpdate(TUser.class);
        updateWrapper.eq(TUser::getUserId, param.getUserId()).set(TUser::getChannelId, param.getId())
                .set(TUser::getShareholderId, null == param.getShareholderId() ? 0 : param.getShareholderId())
                .set(TUser::getPPath, null)
                .set(TUser::getSuperUserId, null)
                .set(TUser::getSuperUserTgId, null);
        userService.update(updateWrapper);
        return this.updateById(param);
    }

    /**
     * 批量删除渠道
     *
     * @param ids 需要删除的渠道ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteChannelByIds(Long[] ids) {
        List<Channel> channelList = this.listByIds(Arrays.asList(ids));
        channelList.forEach(x -> {
            LambdaUpdateWrapper<TUser> updateWrapper = Wrappers.lambdaUpdate(TUser.class);
            updateWrapper.eq(TUser::getUserId, x.getUserId()).set(TUser::getChannelId, 0);
            userService.update(updateWrapper);
        });

        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除渠道信息
     *
     * @param id 渠道ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteChannelById(Long id) {
        Channel byId = this.getById(id);
        LambdaUpdateWrapper<TUser> updateWrapper = Wrappers.lambdaUpdate(TUser.class);
        updateWrapper.eq(TUser::getUserId, byId.getUserId()).set(TUser::getChannelId, 0);
        userService.update(updateWrapper);
        boolean result = this.removeById(id);
        return result;
    }

    public List<Channel> queryChannelByUserTgId(Long chatId) {
        LambdaQueryWrapper<Channel> q = new LambdaQueryWrapper<>();
        q.eq(Channel::getUserTgId, chatId);
//        q.last("limit 1");
        return channelMapper.selectList(q);
    }

    public List<Channel> queryChannelByUserId(Long userId) {
        LambdaQueryWrapper<Channel> q = new LambdaQueryWrapper<>();
        q.eq(Channel::getUserId, userId);
//        q.last("limit 1");
        return channelMapper.selectList(q);
    }

    public List<Channel> queryChannelByShareholderId(Long shareholderId, String keyword) {
        LambdaQueryWrapper<Channel> q = new LambdaQueryWrapper<>();
        q.eq(Channel::getShareholderId, shareholderId);
        q.like(StrUtil.isNotEmpty(keyword), Channel::getChannelName, keyword);
//        q.last("limit 1");
        return channelMapper.selectList(q);
    }

    public Boolean getChannelCodeCountById(Channel param) {
        int channelCodeCount = channelMapper.getChannelCodeCountById(param);
        if (channelCodeCount == 0) {
            return true;
        } else {
            return false;
        }
    }

    public String getChannelName(Long channelId) {
        return channelMapper.getChannelName(channelId);
    }

    public List<Channel> queryAllChannel() {
        LambdaQueryWrapper<Channel> q = new LambdaQueryWrapper<>();
        return channelMapper.selectList(q);
    }

    /**
     * 根据渠道编码查询渠道
     *
     * @param channelCode 渠道编码
     * @return 渠道对象，如果不存在则返回null
     */
    public Channel getByChannelCode(String channelCode) {
        LambdaQueryWrapper<Channel> q = new LambdaQueryWrapper<>();
        q.eq(Channel::getChannelCode, channelCode);
        return this.getOne(q);
    }
}
