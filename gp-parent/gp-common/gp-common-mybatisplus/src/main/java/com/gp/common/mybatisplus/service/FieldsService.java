package com.gp.common.mybatisplus.service;

import java.util.List;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.FieldsMapper;
import com.gp.common.mybatisplus.entity.Fields;
import com.gp.common.mybatisplus.service.FieldsService;

/**
 * 所有字段Service业务层处理
 *
 * @author axing
 * @date 2024-05-13
 */
@Service
public class FieldsService extends ServiceImpl<FieldsMapper, Fields>
{
    @Autowired
    private FieldsMapper fieldsMapper;

    /**
     * 查询所有字段
     *
     * @param id 所有字段ID
     * @return 所有字段
     */

    public Fields selectFieldsById(Long id)
    {
        return fieldsMapper.selectFieldsById(id);
    }

    /**
     * 查询所有字段列表
     *
     * @param param 所有字段
     * @return 所有字段
     */

    public List<Fields> selectFieldsList(Fields param)
    {
        return fieldsMapper.selectFieldsList(param);
    }

    /**
     * 新增所有字段
     *
     * @param param 所有字段
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertFields(Fields param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改所有字段
     *
     * @param param 所有字段
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateFields(Fields param)
    {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除所有字段
     *
     * @param ids 需要删除的所有字段ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteFieldsByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除所有字段信息
     *
     * @param id 所有字段ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteFieldsById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
