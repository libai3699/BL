package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.BetAmountLevelConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 下注金额和级别配置Mapper接口
 *
 * @author axing
 * @date 2024-05-08
 */
public interface BetAmountLevelConfigMapper extends BaseMapper<BetAmountLevelConfig>
{
    /**
     * 查询下注金额和级别配置
     *
     * @param id 下注金额和级别配置ID
     * @return 下注金额和级别配置
     */
    public BetAmountLevelConfig selectBetAmountLevelConfigById(Long id);

    /**
     * 查询下注金额和级别配置列表
     *
     * @param betAmountLevelConfig 下注金额和级别配置
     * @return 下注金额和级别配置集合
     */
    public List<BetAmountLevelConfig> selectBetAmountLevelConfigList(BetAmountLevelConfig betAmountLevelConfig);

    /**
     * 新增下注金额和级别配置
     *
     * @param betAmountLevelConfig 下注金额和级别配置
     * @return 结果
     */
    public int insertBetAmountLevelConfig(BetAmountLevelConfig betAmountLevelConfig);

    /**
     * 修改下注金额和级别配置
     *
     * @param betAmountLevelConfig 下注金额和级别配置
     * @return 结果
     */
    public int updateBetAmountLevelConfig(BetAmountLevelConfig betAmountLevelConfig);

    /**
     * 删除下注金额和级别配置
     *
     * @param id 下注金额和级别配置ID
     * @return 结果
     */
    public int deleteBetAmountLevelConfigById(Long id);

    /**
     * 批量删除下注金额和级别配置
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBetAmountLevelConfigByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
