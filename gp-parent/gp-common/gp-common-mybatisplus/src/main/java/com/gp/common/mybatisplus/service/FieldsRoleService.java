package com.gp.common.mybatisplus.service;

import java.util.List;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.FieldsRoleMapper;
import com.gp.common.mybatisplus.entity.FieldsRole;
import com.gp.common.mybatisplus.service.FieldsRoleService;

/**
 * 角色字段Service业务层处理
 *
 * @author axing
 * @date 2024-05-13
 */
@Service
public class FieldsRoleService extends ServiceImpl<FieldsRoleMapper, FieldsRole>
{
    @Autowired
    private FieldsRoleMapper fieldsRoleMapper;

    /**
     * 查询角色字段
     *
     * @param id 角色字段ID
     * @return 角色字段
     */

    public FieldsRole selectFieldsRoleById(Long id)
    {
        return fieldsRoleMapper.selectFieldsRoleById(id);
    }

    /**
     * 查询角色字段列表
     *
     * @param param 角色字段
     * @return 角色字段
     */

    public List<FieldsRole> selectFieldsRoleList(FieldsRole param)
    {
        return fieldsRoleMapper.selectFieldsRoleList(param);
    }

    /**
     * 新增角色字段
     *
     * @param param 角色字段
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertFieldsRole(FieldsRole param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改角色字段
     *
     * @param param 角色字段
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateFieldsRole(FieldsRole param)
    {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除角色字段
     *
     * @param ids 需要删除的角色字段ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteFieldsRoleByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除角色字段信息
     *
     * @param id 角色字段ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteFieldsRoleById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
