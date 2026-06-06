package com.gp.common.mybatisplus.service;

import java.util.Date;
import java.util.List;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.mybatisplus.entity.GameBetAmountTotalAmount;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.ChannelCountGameCodeMapper;
import com.gp.common.mybatisplus.entity.ChannelCountGameCode;
import com.gp.common.mybatisplus.service.ChannelCountGameCodeService;

/**
 * 渠道游戏类型打码量统计
Service业务层处理
 *
 * @author axing
 * @date 2025-06-23
 */
@Service
public class ChannelCountGameCodeService extends ServiceImpl<ChannelCountGameCodeMapper, ChannelCountGameCode>
{
    @Autowired
    private ChannelCountGameCodeMapper channelCountGameCodeMapper;

    /**
     * 查询渠道游戏类型打码量统计

     *
     * @param id 渠道游戏类型打码量统计
ID
     * @return 渠道游戏类型打码量统计

     */

    public ChannelCountGameCode selectChannelCountGameCodeById(Long id)
    {
        return channelCountGameCodeMapper.selectChannelCountGameCodeById(id);
    }

    /**
     * 查询渠道游戏类型打码量统计
列表
     *
     * @param param 渠道游戏类型打码量统计

     * @return 渠道游戏类型打码量统计

     */

    public List<ChannelCountGameCode> selectChannelCountGameCodeList(ChannelCountGameCode param)
    {
        return channelCountGameCodeMapper.selectChannelCountGameCodeList(param);
    }

    /**
     * 新增渠道游戏类型打码量统计

     *
     * @param param 渠道游戏类型打码量统计

     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertChannelCountGameCode(ChannelCountGameCode param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改渠道游戏类型打码量统计

     *
     * @param param 渠道游戏类型打码量统计

     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateChannelCountGameCode(ChannelCountGameCode param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除渠道游戏类型打码量统计

     *
     * @param ids 需要删除的渠道游戏类型打码量统计
ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteChannelCountGameCodeByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除渠道游戏类型打码量统计
信息
     *
     * @param id 渠道游戏类型打码量统计
ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteChannelCountGameCodeById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }

    public GameBetAmountTotalAmount querySumAmountByUserIdAndTime(Long channelId, Date startTime, Date endTime) {
        return channelCountGameCodeMapper.querySumAmountByUserIdAndTime(channelId,startTime,endTime);
    }

    public GameBetAmountTotalAmount querySumAmountByUserIdsAndTime(List<Long> channelIds, Date startTime, Date endTime) {
        return channelCountGameCodeMapper.querySumAmountByUserIdsAndTime(channelIds,startTime,endTime);
    }
}
