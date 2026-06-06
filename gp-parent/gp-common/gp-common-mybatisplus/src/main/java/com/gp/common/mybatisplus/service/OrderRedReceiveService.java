package com.gp.common.mybatisplus.service;

import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

import com.gp.common.mybatisplus.mapper.OrderRedReceiveMapper;
import com.gp.common.mybatisplus.entity.OrderRedReceive;
import com.gp.common.mybatisplus.service.OrderRedReceiveService;

/**
 * 红包雨和私人红包记录Service业务层处理
 *
 * @author axing
 * @date 2025-07-24
 */
@Service
public class OrderRedReceiveService extends ServiceImpl<OrderRedReceiveMapper, OrderRedReceive> {
    @Autowired
    private OrderRedReceiveMapper orderRedReceiveMapper;

    /**
     * 查询红包雨和私人红包记录
     *
     * @param id 红包雨和私人红包记录ID
     * @return 红包雨和私人红包记录
     */

    public OrderRedReceive selectOrderRedReceiveById(Long id) {
        return orderRedReceiveMapper.selectOrderRedReceiveById(id);
    }

    /**
     * 查询红包雨和私人红包记录列表
     *
     * @param param 红包雨和私人红包记录
     * @return 红包雨和私人红包记录
     */

    public List<OrderRedReceive> selectOrderRedReceiveList(OrderRedReceive param) {
        return orderRedReceiveMapper.selectOrderRedReceiveList(param);
    }

    /**
     * 查询红包雨和私人红包记录列表数量
     *
     * @param param 红包雨和私人红包记录
     * @return 红包雨和私人红包记录数量
     */
    public int selectOrderRedReceiveListCount(OrderRedReceive param) {
        return orderRedReceiveMapper.selectOrderRedReceiveListCount(param);
    }

    /**
     * 新增红包雨和私人红包记录
     *
     * @param param 红包雨和私人红包记录
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertOrderRedReceive(OrderRedReceive param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改红包雨和私人红包记录
     *
     * @param param 红包雨和私人红包记录
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOrderRedReceive(OrderRedReceive param) {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除红包雨和私人红包记录
     *
     * @param ids 需要删除的红包雨和私人红包记录ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrderRedReceiveByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除红包雨和私人红包记录信息
     *
     * @param id 红包雨和私人红包记录ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrderRedReceiveById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    public OrderRedReceive queryRedPacketRain(Long redRainId, Long userId, Integer type) {
        //看看有没有领取过
        LambdaQueryWrapper<OrderRedReceive> q = new LambdaQueryWrapper<>();
        q.eq(OrderRedReceive::getUserId, userId);
        q.eq(OrderRedReceive::getLinkId, redRainId);
        q.eq(OrderRedReceive::getType, type);
        q.last("limit 1");
        return this.getOne(q);
    }

    public BigDecimal queryTotalMoney(Long userId, Integer type) {
        LambdaQueryWrapper<OrderRedReceive> q = new LambdaQueryWrapper<>();
        q.eq(OrderRedReceive::getUserId, userId);
        q.eq(OrderRedReceive::getType, type);
        List<OrderRedReceive> list = this.list(q);
        BigDecimal totalMoney = BigDecimal.ZERO;
        for (OrderRedReceive orderRedReceive : list) {
            totalMoney = totalMoney.add(orderRedReceive.getAmount());
        }
        return totalMoney;
    }

    public Map<String, Object> redEnvelopeRainCountMap(String startTime, String endTime, Long userId, Integer id) {
        return orderRedReceiveMapper.redEnvelopeRainCountMap(startTime, endTime, userId, id);
    }

    public Map<String, Object> redEnvelopeRainChanneCountMap(String startTime, String endTime, Integer id, Integer type,  Long channelId) {
        return orderRedReceiveMapper.redEnvelopeRainChanneCountMap(startTime, endTime, id, type,channelId);
    }
}
