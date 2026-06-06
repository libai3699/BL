package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.Fields;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 所有字段Mapper接口
 *
 * @author axing
 * @date 2024-05-13
 */
public interface FieldsMapper extends BaseMapper<Fields>
{
    /**
     * 查询所有字段
     *
     * @param id 所有字段ID
     * @return 所有字段
     */
    public Fields selectFieldsById(Long id);

    /**
     * 查询所有字段列表
     *
     * @param fields 所有字段
     * @return 所有字段集合
     */
    public List<Fields> selectFieldsList(Fields fields);

    /**
     * 新增所有字段
     *
     * @param fields 所有字段
     * @return 结果
     */
    public int insertFields(Fields fields);

    /**
     * 修改所有字段
     *
     * @param fields 所有字段
     * @return 结果
     */
    public int updateFields(Fields fields);

    /**
     * 删除所有字段
     *
     * @param id 所有字段ID
     * @return 结果
     */
    public int deleteFieldsById(Long id);

    /**
     * 批量删除所有字段
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteFieldsByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
