package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.DifferentialConfig;
import com.gp.common.mybatisplus.mapper.DifferentialConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 级差配置Service业务层处理
 *
 * @author axing
 * @date 2025-04-29
 */
@Service
public class DifferentialConfigService extends ServiceImpl<DifferentialConfigMapper, DifferentialConfig> {
    @Autowired
    private DifferentialConfigMapper differentialConfigMapper;

    /**
     * 查询级差配置
     *
     * @param id 级差配置ID
     * @return 级差配置
     */

    public DifferentialConfig selectDifferentialConfigById(Long id) {
        return differentialConfigMapper.selectDifferentialConfigById(id);
    }

    /**
     * 查询级差配置列表
     *
     * @param param 级差配置
     * @return 级差配置
     */

    public List<DifferentialConfig> selectDifferentialConfigList(DifferentialConfig param) {
        return differentialConfigMapper.selectDifferentialConfigList(param);
    }

    /**
     * 新增级差配置
     *
     * @param param 级差配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertDifferentialConfig(DifferentialConfig param) {
        Date nowDate = DateUtils.getNowDate();
        param.setCreateTime(nowDate);
        param.setUpdateTime(nowDate);
        return this.save(param);
    }

    /**
     * 修改级差配置
     *
     * @param param 级差配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDifferentialConfig(DifferentialConfig param) {
        param.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(param);
    }

    /**
     * 批量删除级差配置
     *
     * @param ids 需要删除的级差配置ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDifferentialConfigByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除级差配置信息
     *
     * @param id 级差配置ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDifferentialConfigById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }
}
