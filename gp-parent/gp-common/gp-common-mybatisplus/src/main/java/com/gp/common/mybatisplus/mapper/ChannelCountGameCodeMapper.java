package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.ChannelCountGameCode;
import com.gp.common.mybatisplus.entity.GameBetAmountTotalAmount;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;

/**
 * 渠道游戏类型打码量统计
Mapper接口
 *
 * @author axing
 * @date 2025-06-23
 */
public interface ChannelCountGameCodeMapper extends BaseMapper<ChannelCountGameCode>
{
    /**
     * 查询渠道游戏类型打码量统计

     *
     * @param id 渠道游戏类型打码量统计
ID
     * @return 渠道游戏类型打码量统计

     */
    public ChannelCountGameCode selectChannelCountGameCodeById(Long id);

    /**
     * 查询渠道游戏类型打码量统计
列表
     *
     * @param channelCountGameCode 渠道游戏类型打码量统计

     * @return 渠道游戏类型打码量统计
集合
     */
    public List<ChannelCountGameCode> selectChannelCountGameCodeList(ChannelCountGameCode channelCountGameCode);

    /**
     * 新增渠道游戏类型打码量统计

     *
     * @param channelCountGameCode 渠道游戏类型打码量统计

     * @return 结果
     */
    public int insertChannelCountGameCode(ChannelCountGameCode channelCountGameCode);

    /**
     * 修改渠道游戏类型打码量统计

     *
     * @param channelCountGameCode 渠道游戏类型打码量统计

     * @return 结果
     */
    public int updateChannelCountGameCode(ChannelCountGameCode channelCountGameCode);

    /**
     * 删除渠道游戏类型打码量统计

     *
     * @param id 渠道游戏类型打码量统计
ID
     * @return 结果
     */
    public int deleteChannelCountGameCodeById(Long id);

    /**
     * 批量删除渠道游戏类型打码量统计

     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteChannelCountGameCodeByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    GameBetAmountTotalAmount querySumAmountByUserIdAndTime(@Param("channelId") Long channelId, @Param("startTime") Date startTime,@Param("endTime") Date endTime);

    int savechannelCountGameCodeBySettle(ChannelCountGameCode channelCountGameCode);
    int updatechannelCountGameCodeBySettle(ChannelCountGameCode channelCountGameCode);

    GameBetAmountTotalAmount querySumAmountByUserIdsAndTime(@Param("arr") List<Long> channelIds, @Param("startTime") Date startTime,@Param("endTime") Date endTime);

    int calibrateChannelCountGameCode(ChannelCountGameCode channelCountGameCode);
}
