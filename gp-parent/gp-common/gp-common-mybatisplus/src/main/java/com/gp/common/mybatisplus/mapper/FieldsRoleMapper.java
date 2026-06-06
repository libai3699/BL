package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.FieldsRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 角色字段Mapper接口
 *
 * @author axing
 * @date 2024-05-13
 */
public interface FieldsRoleMapper extends BaseMapper<FieldsRole>
{
    /**
     * 查询角色字段
     *
     * @param id 角色字段ID
     * @return 角色字段
     */
    public FieldsRole selectFieldsRoleById(Long id);

    /**
     * 查询角色字段列表
     *
     * @param fieldsRole 角色字段
     * @return 角色字段集合
     */
    public List<FieldsRole> selectFieldsRoleList(FieldsRole fieldsRole);

    /**
     * 新增角色字段
     *
     * @param fieldsRole 角色字段
     * @return 结果
     */
    public int insertFieldsRole(FieldsRole fieldsRole);

    /**
     * 修改角色字段
     *
     * @param fieldsRole 角色字段
     * @return 结果
     */
    public int updateFieldsRole(FieldsRole fieldsRole);

    /**
     * 删除角色字段
     *
     * @param id 角色字段ID
     * @return 结果
     */
    public int deleteFieldsRoleById(Long id);

    /**
     * 批量删除角色字段
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteFieldsRoleByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
