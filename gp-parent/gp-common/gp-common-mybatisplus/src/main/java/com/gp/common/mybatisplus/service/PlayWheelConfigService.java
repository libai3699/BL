package com.gp.common.mybatisplus.service;

import java.util.List;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.PlayWheelConfigMapper;
import com.gp.common.mybatisplus.entity.PlayWheelConfig;
import com.gp.common.mybatisplus.service.PlayWheelConfigService;

/**
 * 转盘游戏配置Service业务层处理
 *
 * @author axing
 * @date 2024-05-12
 */
@Service
public class PlayWheelConfigService extends ServiceImpl<PlayWheelConfigMapper, PlayWheelConfig>
{
    @Autowired
    private PlayWheelConfigMapper playWheelConfigMapper;

    /**
     * 查询转盘游戏配置
     *
     * @param id 转盘游戏配置ID
     * @return 转盘游戏配置
     */

    public PlayWheelConfig selectPlayWheelConfigById(Integer id)
    {
        return playWheelConfigMapper.selectPlayWheelConfigById(id);
    }

    /**
     * 查询转盘游戏配置列表
     *
     * @param param 转盘游戏配置
     * @return 转盘游戏配置
     */

    public List<PlayWheelConfig> selectPlayWheelConfigList(PlayWheelConfig param)
    {
        return playWheelConfigMapper.selectPlayWheelConfigList(param);
    }

    /**
     * 新增转盘游戏配置
     *
     * @param param 转盘游戏配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertPlayWheelConfig(PlayWheelConfig param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改转盘游戏配置
     *
     * @param param 转盘游戏配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePlayWheelConfig(PlayWheelConfig param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除转盘游戏配置
     *
     * @param ids 需要删除的转盘游戏配置ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePlayWheelConfigByIds(Integer[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除转盘游戏配置信息
     *
     * @param id 转盘游戏配置ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePlayWheelConfigById(Integer id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
