package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.OrderRedEnvelopeTemplate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 红包模版Mapper接口
 *
 * @author axing
 * @date 2024-12-25
 */
public interface OrderRedEnvelopeTemplateMapper extends BaseMapper<OrderRedEnvelopeTemplate>
{
    /**
     * 查询红包模版
     *
     * @param id 红包模版ID
     * @return 红包模版
     */
    public OrderRedEnvelopeTemplate selectOrderRedEnvelopeTemplateById(Long id);

    /**
     * 查询红包模版列表
     *
     * @param orderRedEnvelopeTemplate 红包模版
     * @return 红包模版集合
     */
    public List<OrderRedEnvelopeTemplate> selectOrderRedEnvelopeTemplateList(OrderRedEnvelopeTemplate orderRedEnvelopeTemplate);

    /**
     * 新增红包模版
     *
     * @param orderRedEnvelopeTemplate 红包模版
     * @return 结果
     */
    public int insertOrderRedEnvelopeTemplate(OrderRedEnvelopeTemplate orderRedEnvelopeTemplate);

    /**
     * 修改红包模版
     *
     * @param orderRedEnvelopeTemplate 红包模版
     * @return 结果
     */
    public int updateOrderRedEnvelopeTemplate(OrderRedEnvelopeTemplate orderRedEnvelopeTemplate);

    /**
     * 删除红包模版
     *
     * @param id 红包模版ID
     * @return 结果
     */
    public int deleteOrderRedEnvelopeTemplateById(Long id);

    /**
     * 批量删除红包模版
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteOrderRedEnvelopeTemplateByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
