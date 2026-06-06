package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.Maintain;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author axing
 * @date 2024-11-13
 */
public interface MaintainMapper extends BaseMapper<Maintain>
{
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public Maintain selectMaintainById(Integer id);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param maintain 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<Maintain> selectMaintainList(Maintain maintain);

    /**
     * 新增【请填写功能名称】
     *
     * @param maintain 【请填写功能名称】
     * @return 结果
     */
    public int insertMaintain(Maintain maintain);

    /**
     * 修改【请填写功能名称】
     *
     * @param maintain 【请填写功能名称】
     * @return 结果
     */
    public int updateMaintain(Maintain maintain);

    /**
     * 删除【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteMaintainById(Integer id);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteMaintainByIds(Integer[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
