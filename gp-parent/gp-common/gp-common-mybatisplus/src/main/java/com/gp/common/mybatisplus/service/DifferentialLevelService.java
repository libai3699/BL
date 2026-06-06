package com.gp.common.mybatisplus.service;

import java.util.List;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.DifferentialLevelMapper;
import com.gp.common.mybatisplus.entity.DifferentialLevel;
import com.gp.common.mybatisplus.service.DifferentialLevelService;

/**
 * 级差层级Service业务层处理
 *
 * @author axing
 * @date 2025-04-29
 */
@Service
public class DifferentialLevelService extends ServiceImpl<DifferentialLevelMapper, DifferentialLevel>
{
    @Autowired
    private DifferentialLevelMapper differentialLevelMapper;

    /**
     * 查询级差层级
     *
     * @param id 级差层级ID
     * @return 级差层级
     */

    public DifferentialLevel selectDifferentialLevelById(Long id)
    {
        return differentialLevelMapper.selectDifferentialLevelById(id);
    }

    /**
     * 查询级差层级列表
     *
     * @param param 级差层级
     * @return 级差层级
     */

    public List<DifferentialLevel> selectDifferentialLevelList(DifferentialLevel param)
    {
        return differentialLevelMapper.selectDifferentialLevelList(param);
    }

    /**
     * 新增级差层级
     *
     * @param param 级差层级
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertDifferentialLevel(DifferentialLevel param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改级差层级
     *
     * @param param 级差层级
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDifferentialLevel(DifferentialLevel param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除级差层级
     *
     * @param ids 需要删除的级差层级ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDifferentialLevelByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除级差层级信息
     *
     * @param id 级差层级ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDifferentialLevelById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
