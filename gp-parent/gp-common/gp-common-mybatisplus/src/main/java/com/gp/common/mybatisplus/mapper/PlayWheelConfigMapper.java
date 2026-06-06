package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.PlayWheelConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 转盘游戏配置Mapper接口
 *
 * @author axing
 * @date 2024-05-12
 */
public interface PlayWheelConfigMapper extends BaseMapper<PlayWheelConfig>
{
    /**
     * 查询转盘游戏配置
     *
     * @param id 转盘游戏配置ID
     * @return 转盘游戏配置
     */
    public PlayWheelConfig selectPlayWheelConfigById(Integer id);

    /**
     * 查询转盘游戏配置列表
     *
     * @param playWheelConfig 转盘游戏配置
     * @return 转盘游戏配置集合
     */
    public List<PlayWheelConfig> selectPlayWheelConfigList(PlayWheelConfig playWheelConfig);

    /**
     * 新增转盘游戏配置
     *
     * @param playWheelConfig 转盘游戏配置
     * @return 结果
     */
    public int insertPlayWheelConfig(PlayWheelConfig playWheelConfig);

    /**
     * 修改转盘游戏配置
     *
     * @param playWheelConfig 转盘游戏配置
     * @return 结果
     */
    public int updatePlayWheelConfig(PlayWheelConfig playWheelConfig);

    /**
     * 删除转盘游戏配置
     *
     * @param id 转盘游戏配置ID
     * @return 结果
     */
    public int deletePlayWheelConfigById(Integer id);

    /**
     * 批量删除转盘游戏配置
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deletePlayWheelConfigByIds(Integer[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
