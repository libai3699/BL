package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.DifferentialConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 级差配置Mapper接口
 *
 * @author axing
 * @date 2025-04-29
 */
public interface DifferentialConfigMapper extends BaseMapper<DifferentialConfig>
{
    /**
     * 查询级差配置
     *
     * @param id 级差配置ID
     * @return 级差配置
     */
    public DifferentialConfig selectDifferentialConfigById(Long id);

    /**
     * 查询级差配置列表
     *
     * @param differentialConfig 级差配置
     * @return 级差配置集合
     */
    public List<DifferentialConfig> selectDifferentialConfigList(DifferentialConfig differentialConfig);

    /**
     * 新增级差配置
     *
     * @param differentialConfig 级差配置
     * @return 结果
     */
    public int insertDifferentialConfig(DifferentialConfig differentialConfig);

    /**
     * 修改级差配置
     *
     * @param differentialConfig 级差配置
     * @return 结果
     */
    public int updateDifferentialConfig(DifferentialConfig differentialConfig);

    /**
     * 删除级差配置
     *
     * @param id 级差配置ID
     * @return 结果
     */
    public int deleteDifferentialConfigById(Long id);

    /**
     * 批量删除级差配置
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteDifferentialConfigByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
