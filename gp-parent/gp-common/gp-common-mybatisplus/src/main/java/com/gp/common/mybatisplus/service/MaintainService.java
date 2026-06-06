package com.gp.common.mybatisplus.service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.MaintainMapper;
import com.gp.common.mybatisplus.entity.Maintain;
import com.gp.common.mybatisplus.service.MaintainService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author axing
 * @date 2024-11-13
 */
@Service
public class MaintainService extends ServiceImpl<MaintainMapper, Maintain>
{
    @Autowired
    private MaintainMapper maintainMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */

    public Maintain selectMaintainById(Integer id)
    {
        return maintainMapper.selectMaintainById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param param 【请填写功能名称】
     * @return 【请填写功能名称】
     */

    public List<Maintain> selectMaintainList(Maintain param)
    {
        return maintainMapper.selectMaintainList(param);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param param 【请填写功能名称】
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertMaintain(Maintain param)
    {
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param param 【请填写功能名称】
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateMaintain(Maintain param)
    {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteMaintainByIds(Integer[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteMaintainById(Integer id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
