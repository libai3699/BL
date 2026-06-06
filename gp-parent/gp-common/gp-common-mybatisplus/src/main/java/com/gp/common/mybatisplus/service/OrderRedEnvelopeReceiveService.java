package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.OrderRedEnvelopeReceive;
import com.gp.common.mybatisplus.mapper.OrderRedEnvelopeReceiveMapper;
import com.gp.common.mybatisplus.param.OrderRedEnvelopeReceiveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 红包接收Service业务层处理
 *
 * @author axing
 * @date 2024-12-25
 */
@Service
public class OrderRedEnvelopeReceiveService extends ServiceImpl<OrderRedEnvelopeReceiveMapper, OrderRedEnvelopeReceive> {
    @Autowired
    private OrderRedEnvelopeReceiveMapper orderRedEnvelopeReceiveMapper;

    /**
     * 查询红包接收
     *
     * @param id 红包接收ID
     * @return 红包接收
     */

    public OrderRedEnvelopeReceive selectOrderRedEnvelopeReceiveById(Long id) {
        return orderRedEnvelopeReceiveMapper.selectOrderRedEnvelopeReceiveById(id);
    }

    /**
     * 查询红包接收列表
     *
     * @param param 红包接收
     * @return 红包接收
     */

    public List<OrderRedEnvelopeReceive> selectOrderRedEnvelopeReceiveList(OrderRedEnvelopeReceive param) {
        return orderRedEnvelopeReceiveMapper.selectOrderRedEnvelopeReceiveList(param);
    }

    /**
     * 新增红包接收
     *
     * @param param 红包接收
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertOrderRedEnvelopeReceive(OrderRedEnvelopeReceive param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改红包接收
     *
     * @param param 红包接收
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOrderRedEnvelopeReceive(OrderRedEnvelopeReceive param) {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除红包接收
     *
     * @param ids 需要删除的红包接收ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrderRedEnvelopeReceiveByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除红包接收信息
     *
     * @param id 红包接收ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrderRedEnvelopeReceiveById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    public List<OrderRedEnvelopeReceiveVo> listByOrder(String orderNo, Long userId) {
        return this.baseMapper.listByOrder(orderNo, userId);
    }
    public List<OrderRedEnvelopeReceiveVo> listByOrderWithOutUserId(String orderNo) {
        return this.baseMapper.listByOrderWithOutUserId(orderNo);
    }
    public Integer queryIsReceive(String orderNo, Long userId) {
        LambdaQueryWrapper<OrderRedEnvelopeReceive> q = new LambdaQueryWrapper<>();
        q.eq(OrderRedEnvelopeReceive::getLinkOrderNo, orderNo).eq(OrderRedEnvelopeReceive::getUserId, userId);
        return this.baseMapper.selectCount(q);
    }

    public Map<String, Object> orderRedEnvelopeSendsMap(String startTime, String endTime, Integer currencyId, Integer type, Long channelId) {
        return this.baseMapper.orderRedEnvelopeSendsMap(startTime, endTime, currencyId, type,channelId);
    }

    public Map<String, Object> orderRedEnvelopeReceiveCountMap(String startTime, String endTime, Long userId, Integer currencyId) {
        return baseMapper.orderRedEnvelopeReceiveCountMap(startTime, endTime, userId, currencyId);
    }

    public Long selectOrderRedEnvelopeReceiveCount(OrderRedEnvelopeReceive param) {
        return baseMapper.selectOrderRedEnvelopeReceiveCount(param);
    }
}
