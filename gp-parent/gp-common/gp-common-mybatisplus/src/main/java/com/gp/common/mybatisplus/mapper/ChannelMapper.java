package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.Channel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 渠道Mapper接口
 *
 * @author axing
 * @date 2024-05-08
 */
public interface ChannelMapper extends BaseMapper<Channel>
{
    /**
     * 查询渠道
     *
     * @param id 渠道ID
     * @return 渠道
     */
    public Channel selectChannelById(Long id);

    /**
     * 查询渠道列表
     *
     * @param channel 渠道
     * @return 渠道集合
     */
    public List<Channel> selectChannelList(Channel channel);

    /**
     * 查询渠道列表数量
     *
     * @param channel 渠道
     * @return 渠道集合数量
     */
    public int selectChannelListCount(Channel channel);

    /**
     * 新增渠道
     *
     * @param channel 渠道
     * @return 结果
     */
    public int insertChannel(Channel channel);

    /**
     * 修改渠道
     *
     * @param channel 渠道
     * @return 结果
     */
    public int updateChannel(Channel channel);

    /**
     * 删除渠道
     *
     * @param id 渠道ID
     * @return 结果
     */
    public int deleteChannelById(Long id);

    /**
     * 批量删除渠道
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteChannelByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    public List<Channel> selectlistByChannelId(Long channelId);

    public int getChannelCodeCount(String channelCode);

    int getChannelCodeCountById(Channel param);

    String getChannelName(Long channelId);
}