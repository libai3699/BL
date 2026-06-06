package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.DifferentialLevel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 级差层级Mapper接口
 *
 * @author axing
 * @date 2025-04-29
 */
public interface DifferentialLevelMapper extends BaseMapper<DifferentialLevel>
{
    /**
     * 查询级差层级
     *
     * @param id 级差层级ID
     * @return 级差层级
     */
    public DifferentialLevel selectDifferentialLevelById(Long id);

    /**
     * 查询级差层级列表
     *
     * @param differentialLevel 级差层级
     * @return 级差层级集合
     */
    public List<DifferentialLevel> selectDifferentialLevelList(DifferentialLevel differentialLevel);

    /**
     * 新增级差层级
     *
     * @param differentialLevel 级差层级
     * @return 结果
     */
    public int insertDifferentialLevel(DifferentialLevel differentialLevel);

    /**
     * 修改级差层级
     *
     * @param differentialLevel 级差层级
     * @return 结果
     */
    public int updateDifferentialLevel(DifferentialLevel differentialLevel);

    /**
     * 删除级差层级
     *
     * @param id 级差层级ID
     * @return 结果
     */
    public int deleteDifferentialLevelById(Long id);

    /**
     * 批量删除级差层级
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteDifferentialLevelByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
