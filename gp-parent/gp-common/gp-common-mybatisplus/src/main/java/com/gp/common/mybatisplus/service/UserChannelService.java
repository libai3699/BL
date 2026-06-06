package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.Channel;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.mybatisplus.mapper.UserChannelMapper;
import com.gp.common.mybatisplus.entity.UserChannel;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserChannelService extends ServiceImpl<UserChannelMapper, UserChannel> {
    @Autowired
    private UserChannelMapper userChannelMapper;
    @Autowired
    private ChannelService channelService;

    public UserChannel getChannelLotteryConfig(Long userId,Long channelId) {
        LambdaQueryWrapper<UserChannel> q = new LambdaQueryWrapper<>();
        q.eq(UserChannel::getUserId, userId);
        q.eq(UserChannel::getChannelId, channelId);
        return userChannelMapper.selectOne(q);
    }
    public List<UserChannel> getChannelLotteryConfigArr(List<String> arr,Long channelId) {
        LambdaQueryWrapper<UserChannel> q = new LambdaQueryWrapper<>();
        q.in(UserChannel::getUserId, arr);
        q.eq(UserChannel::getChannelId, channelId);
        return userChannelMapper.selectList(q);
    }




    public BigDecimal dealUserChannelRatio(UserChannel userChannel, String typeCode) {
            Integer gameType = Integer.parseInt(typeCode);
            if (gameType == 1) {
                return userChannel.getSuperUserRebate1();
            } else if (gameType == 2) {
                return userChannel.getSuperUserRebate2();
            } else if (gameType == 3) {
                return userChannel.getSuperUserRebate3();
            } else if (gameType == 4) {
                return userChannel.getSuperUserRebate4();
            } else if (gameType == 5) {
                return userChannel.getSuperUserRebate5();
            } else if (gameType == 6) {
                return userChannel.getSuperUserRebate6();
            } else if (gameType == 7) {
                return userChannel.getSuperUserRebate7();
            } else if (gameType == 8) {
                return userChannel.getSuperUserRebate8();
            } else if (gameType == 9) {
                return userChannel.getSuperUserRebate9();
            } else {
                return BigDecimal.ZERO;
            }
    }


    /**
     * 查询渠道配置
     *
     * @param param 渠道配置ID 用户ID
     * @return 渠道配置
     */

    public UserChannel selectTUserChannelById(UserChannel param)
    {
        return userChannelMapper.selectTUserChannelById(param);
    }
    /**
     * 修改渠道配置
     *
     * @param param 渠道配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTUserChannel(UserChannel param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 新增渠道配置
     *
     * @param param 渠道配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertTUserChannel(UserChannel param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }


    /**
     * 新增渠道配置（有则不管，无则新增） - SQL形式
     * 使用 INSERT IGNORE 语法，性能更好，不需要先查询
     *
     * @param param 渠道配置
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertTUserChannelIfNotExist(UserChannel param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        int rows = userChannelMapper.insertIgnore(param);
    }
    // (0 关闭, 1 开启)
    public Byte queryChannelShowLottery(Long channelId,String pPath,Long userId) {
        //查询渠道
        if(null==channelId||0==channelId){
            return  0;
        }
        Channel byId = channelService.getById(channelId);
        if(byId==null){
            return 0;
        }
        Long channelUserId = byId.getUserId();
        String subPath = "";
        //如果ppath 为空 则证明他是顶级渠道
        if(pPath==null){
            UserChannel userChannel = this.getChannelLotteryConfig(byId.getUserId(),channelId);
            if(userChannel==null){
                return 0;
            }
            return userChannel.getDividendStatus();
        }

        int startIndex = pPath.indexOf(channelUserId.toString());
        if (startIndex != -1) {
            subPath = pPath.substring(startIndex);
        }else {
            return 0;
        }
        List<String> arr = new ArrayList<>(Arrays.asList(subPath.split(",")));
        arr.add(userId+"");  // ✅ 可以正常添加
        List<UserChannel> userChannelList = this.getChannelLotteryConfigArr(arr,channelId);
        if(userChannelList == null || userChannelList.isEmpty()){
            return 0;
        }
        // 有一个没开启就返回关闭
        for(UserChannel userChannel : userChannelList){
            if(userChannel.getDividendStatus() == null || userChannel.getDividendStatus() != 1){
                return 0;
            }
        }
        return 1;
    }

    public static void main(String[] args) {
        String pPath = "1,2,3,4,5";
        Long channelUserId = 2l;
        String subPath = "";
        int startIndex = pPath.indexOf(channelUserId.toString());
        if (startIndex != -1) {
            subPath = pPath.substring(startIndex);
        }else {
            System.out.println(111);
        }
        List<String> arr = Arrays.asList(subPath.split(","));
        System.out.println(arr);
    }
}
