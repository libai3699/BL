package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.FieldsQuery;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 用户绑定字段Mapper接口
 *
 * @author axing
 * @date 2024-05-13
 */
public interface FieldsQueryMapper extends BaseMapper<FieldsQuery>
{
    /**
     * 查询用户绑定字段
     *
     * @param id 用户绑定字段ID
     * @return 用户绑定字段
     */
    public FieldsQuery selectFieldsQueryById(Long id);

    /**
     * 查询用户绑定字段列表
     *
     * @param fieldsQuery 用户绑定字段
     * @return 用户绑定字段集合
     */
    public List<FieldsQuery> selectFieldsQueryList(FieldsQuery fieldsQuery);

    /**
     * 新增用户绑定字段
     *
     * @param fieldsQuery 用户绑定字段
     * @return 结果
     */
    public int insertFieldsQuery(FieldsQuery fieldsQuery);

    /**
     * 修改用户绑定字段
     *
     * @param fieldsQuery 用户绑定字段
     * @return 结果
     */
    public int updateFieldsQuery(FieldsQuery fieldsQuery);

    /**
     * 删除用户绑定字段
     *
     * @param id 用户绑定字段ID
     * @return 结果
     */
    public int deleteFieldsQueryById(Long id);

    /**
     * 批量删除用户绑定字段
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteFieldsQueryByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
