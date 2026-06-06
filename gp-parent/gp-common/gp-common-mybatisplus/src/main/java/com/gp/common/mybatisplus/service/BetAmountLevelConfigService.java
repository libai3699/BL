package com.gp.common.mybatisplus.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.BetAmountLevelConfigMapper;
import com.gp.common.mybatisplus.entity.BetAmountLevelConfig;
import com.gp.common.mybatisplus.service.BetAmountLevelConfigService;

/**
 * 下注金额和级别配置Service业务层处理
 *
 * @author axing
 * @date 2024-05-08
 */
@Service
public class BetAmountLevelConfigService extends ServiceImpl<BetAmountLevelConfigMapper, BetAmountLevelConfig>
{
    @Autowired
    private BetAmountLevelConfigMapper betAmountLevelConfigMapper;

    /**
     * 查询下注金额和级别配置
     *
     * @param id 下注金额和级别配置ID
     * @return 下注金额和级别配置
     */

    public BetAmountLevelConfig selectBetAmountLevelConfigById(Long id)
    {
        return betAmountLevelConfigMapper.selectBetAmountLevelConfigById(id);
    }

    /**
     * 查询下注金额和级别配置列表
     *
     * @param param 下注金额和级别配置
     * @return 下注金额和级别配置
     */

    public List<BetAmountLevelConfig> selectBetAmountLevelConfigList(BetAmountLevelConfig param)
    {
        return betAmountLevelConfigMapper.selectBetAmountLevelConfigList(param);
    }

    /**
     * 新增下注金额和级别配置
     *
     * @param param 下注金额和级别配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertBetAmountLevelConfig(BetAmountLevelConfig param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改下注金额和级别配置
     *
     * @param param 下注金额和级别配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateBetAmountLevelConfig(BetAmountLevelConfig param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除下注金额和级别配置
     *
     * @param ids 需要删除的下注金额和级别配置ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteBetAmountLevelConfigByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除下注金额和级别配置信息
     *
     * @param id 下注金额和级别配置ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteBetAmountLevelConfigById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }
    public BetAmountLevelConfig queryConfigByLevel(Integer level) {
        LambdaQueryWrapper<BetAmountLevelConfig> q = new LambdaQueryWrapper<>();
        q.eq(BetAmountLevelConfig::getLevelCode,level);
        q.last("limit 1");
        return betAmountLevelConfigMapper.selectOne(q);
    }

    public Integer queryMaxLevel() {
        LambdaQueryWrapper<BetAmountLevelConfig> q = new LambdaQueryWrapper<>();
        q.orderByDesc(BetAmountLevelConfig::getId);
        q.last("limit 1");
        return betAmountLevelConfigMapper.selectOne(q).getLevelCode();
    }

    public List<BetAmountLevelConfig> queryAllLevel() {
        LambdaQueryWrapper<BetAmountLevelConfig> q = new LambdaQueryWrapper<>();
        q.orderByAsc(BetAmountLevelConfig::getId);
        return betAmountLevelConfigMapper.selectList(q);
    }
    public List<BetAmountLevelConfig> queryAllLevelDesc() {
        LambdaQueryWrapper<BetAmountLevelConfig> q = new LambdaQueryWrapper<>();
        q.orderByDesc(BetAmountLevelConfig::getId);
        return betAmountLevelConfigMapper.selectList(q);
    }
}
