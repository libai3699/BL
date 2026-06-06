package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.ChannelConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 渠道配置Mapper接口
 *
 * @author axing
 * @date 2025-05-23
 */
public interface ChannelConfigMapper extends BaseMapper<ChannelConfig>
{
    /**
     * 查询渠道配置
     *
     * @param id 渠道配置ID
     * @return 渠道配置
     */
    public ChannelConfig selectChannelConfigById(Long id);

    /**
     * 查询渠道配置列表
     *
     * @param channelConfig 渠道配置
     * @return 渠道配置集合
     */
    public List<ChannelConfig> selectChannelConfigList(ChannelConfig channelConfig);

    /**
     * 查询渠道配置列表数量
     *
     * @param channelConfig 渠道配置
     * @return 渠道配置集合数量
     */
    public int selectChannelConfigListCount(ChannelConfig channelConfig);

    /**
     * 新增渠道配置
     *
     * @param channelConfig 渠道配置
     * @return 结果
     */
    public int insertChannelConfig(ChannelConfig channelConfig);

    /**
     * 修改渠道配置
     *
     * @param channelConfig 渠道配置
     * @return 结果
     */
    public int updateChannelConfig(ChannelConfig channelConfig);

    /**
     * 删除渠道配置
     *
     * @param id 渠道配置ID
     * @return 结果
     */
    public int deleteChannelConfigById(Long id);

    /**
     * 批量删除渠道配置
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteChannelConfigByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}