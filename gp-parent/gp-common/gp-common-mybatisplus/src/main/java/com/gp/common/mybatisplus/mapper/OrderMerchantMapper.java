package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.OrderMerchant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 商户订单Mapper接口
 *
 * @author axing
 * @date 2024-08-11
 */
public interface OrderMerchantMapper extends BaseMapper<OrderMerchant>
{
    /**
     * 查询商户订单
     *
     * @param id 商户订单ID
     * @return 商户订单
     */
    public OrderMerchant selectOrderMerchantById(Long id);

    /**
     * 查询商户订单列表
     *
     * @param orderMerchant 商户订单
     * @return 商户订单集合
     */
    public List<OrderMerchant> selectOrderMerchantList(OrderMerchant orderMerchant);

    /**
     * 新增商户订单
     *
     * @param orderMerchant 商户订单
     * @return 结果
     */
    public int insertOrderMerchant(OrderMerchant orderMerchant);

    /**
     * 修改商户订单
     *
     * @param orderMerchant 商户订单
     * @return 结果
     */
    public int updateOrderMerchant(OrderMerchant orderMerchant);

    /**
     * 删除商户订单
     *
     * @param id 商户订单ID
     * @return 结果
     */
    public int deleteOrderMerchantById(Long id);

    /**
     * 批量删除商户订单
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteOrderMerchantByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
