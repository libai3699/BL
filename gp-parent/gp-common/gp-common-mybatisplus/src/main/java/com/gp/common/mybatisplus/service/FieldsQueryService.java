package com.gp.common.mybatisplus.service;

import java.util.List;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.FieldsQueryMapper;
import com.gp.common.mybatisplus.entity.FieldsQuery;
import com.gp.common.mybatisplus.service.FieldsQueryService;

/**
 * 用户绑定字段Service业务层处理
 *
 * @author axing
 * @date 2024-05-13
 */
@Service
public class FieldsQueryService extends ServiceImpl<FieldsQueryMapper, FieldsQuery>
{
    @Autowired
    private FieldsQueryMapper fieldsQueryMapper;

    /**
     * 查询用户绑定字段
     *
     * @param id 用户绑定字段ID
     * @return 用户绑定字段
     */

    public FieldsQuery selectFieldsQueryById(Long id)
    {
        return fieldsQueryMapper.selectFieldsQueryById(id);
    }

    /**
     * 查询用户绑定字段列表
     *
     * @param param 用户绑定字段
     * @return 用户绑定字段
     */

    public List<FieldsQuery> selectFieldsQueryList(FieldsQuery param)
    {
        return fieldsQueryMapper.selectFieldsQueryList(param);
    }

    /**
     * 新增用户绑定字段
     *
     * @param param 用户绑定字段
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertFieldsQuery(FieldsQuery param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改用户绑定字段
     *
     * @param param 用户绑定字段
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateFieldsQuery(FieldsQuery param)
    {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除用户绑定字段
     *
     * @param ids 需要删除的用户绑定字段ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteFieldsQueryByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除用户绑定字段信息
     *
     * @param id 用户绑定字段ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteFieldsQueryById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
