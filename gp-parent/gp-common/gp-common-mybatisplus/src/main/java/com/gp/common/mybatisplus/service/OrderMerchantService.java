package com.gp.common.mybatisplus.service;

import java.util.List;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.OrderMerchantMapper;
import com.gp.common.mybatisplus.entity.OrderMerchant;
import com.gp.common.mybatisplus.service.OrderMerchantService;

/**
 * 商户订单Service业务层处理
 *
 * @author axing
 * @date 2024-08-11
 */
@Service
public class OrderMerchantService extends ServiceImpl<OrderMerchantMapper, OrderMerchant>
{
    @Autowired
    private OrderMerchantMapper orderMerchantMapper;

    /**
     * 查询商户订单
     *
     * @param id 商户订单ID
     * @return 商户订单
     */

    public OrderMerchant selectOrderMerchantById(Long id)
    {
        return orderMerchantMapper.selectOrderMerchantById(id);
    }

    /**
     * 查询商户订单列表
     *
     * @param param 商户订单
     * @return 商户订单
     */

    public List<OrderMerchant> selectOrderMerchantList(OrderMerchant param)
    {
        return orderMerchantMapper.selectOrderMerchantList(param);
    }

    /**
     * 新增商户订单
     *
     * @param param 商户订单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertOrderMerchant(OrderMerchant param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改商户订单
     *
     * @param param 商户订单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOrderMerchant(OrderMerchant param)
    {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除商户订单
     *
     * @param ids 需要删除的商户订单ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrderMerchantByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除商户订单信息
     *
     * @param id 商户订单ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrderMerchantById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
