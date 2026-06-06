package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.Channel;
import com.gp.common.mybatisplus.entity.OrderAmount;
import com.gp.common.mybatisplus.entity.OrderPerson;
import com.gp.common.mybatisplus.entity.Shareholder;
import com.gp.common.mybatisplus.mapper.OrderPersonMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 人工上下分订单Service业务层处理
 *
 * @author axing
 * @date 2024-05-18
 */
@Service
public class OrderPersonService extends ServiceImpl<OrderPersonMapper, OrderPerson> {
    @Autowired
    private OrderPersonMapper orderPersonMapper;

    @Resource
    private ChannelService channelService;

    @Resource
    private ShareholderService shareholderService;

    /**
     * 查询人工上下分订单
     *
     * @param id 人工上下分订单ID
     * @return 人工上下分订单
     */

    public OrderPerson selectOrderPersonById(Long id) {
        return orderPersonMapper.selectOrderPersonById(id);
    }

    /**
     * 查询人工上下分订单列表
     *
     * @param param 人工上下分订单
     * @return 人工上下分订单
     */

    public List<OrderPerson> selectOrderPersonList(OrderPerson param) {
        List<OrderPerson> orderPersonList = orderPersonMapper.selectOrderPersonList(param);
        if (CollectionUtils.isNotEmpty(orderPersonList)) {
            //渠道处理
            Set<Long> chainIdSet =
                    orderPersonList.stream().map(OrderPerson::getChannelId).filter(channelId -> channelId != null && channelId != 0).collect(Collectors.toSet());
            Map<Long, Channel> channelMap;
            if (!chainIdSet.isEmpty()) {
                LambdaQueryWrapper<Channel> channelLambda = Wrappers.lambdaQuery(Channel.class);
                channelLambda.in(Channel::getId, chainIdSet);
                channelMap = channelService.list(channelLambda).stream()
                        .collect(Collectors.toMap(Channel::getId, channel -> channel, (existing, replacement) -> existing));
            } else {
                channelMap = new HashMap<>();
            }
            boolean channelEmpty = MapUtils.isNotEmpty(channelMap);
            //股东处理
            Set<Long> shareholderIdSet =
                    orderPersonList.stream().map(OrderPerson::getShareholderId).filter(shareholderId -> shareholderId != null && shareholderId != 0).collect(Collectors.toSet());
            Map<Long, Shareholder> shareholderMap;
            if (!shareholderIdSet.isEmpty()) {
                LambdaQueryWrapper<Shareholder> shareholderLambda = Wrappers.lambdaQuery(Shareholder.class);
                shareholderLambda.in(Shareholder::getId, shareholderIdSet);
                shareholderMap = shareholderService.list(shareholderLambda).stream()
                        .collect(Collectors.toMap(Shareholder::getId, shareholder -> shareholder, (existing, replacement) -> existing));
            } else {
                shareholderMap = new HashMap<>();
            }
            boolean shareholderEmpty = MapUtils.isNotEmpty(shareholderMap);
            orderPersonList.forEach(orderPerson -> {
                //渠道名称
                if (channelEmpty && null != orderPerson.getChannelId() && channelMap.containsKey(orderPerson.getChannelId())) {
                    orderPerson.setChannelName(channelMap.get(orderPerson.getChannelId()).getChannelName());
                }
                //股东名称
                if (shareholderEmpty && orderPerson.getShareholderId() != null && shareholderMap.containsKey(orderPerson.getShareholderId())) {
                    orderPerson.setShareholderName(shareholderMap.get(orderPerson.getShareholderId()).getName());
                }
            });
        }

        return orderPersonList;
    }

    public Map<String, Object> getTodayTotalBetAmount(OrderPerson param) {
        return orderPersonMapper.getTodayTotalBetAmount(param);
    }
    /**
     * 查询人工上下分订单数量
     *
     * @param param 人工上下分订单
     * @return 人工上下分订单数量
     */
    public long selectOrderPersonCount(OrderPerson param) {
        return orderPersonMapper.selectOrderPersonCount(param);
    }

    /**
     * 新增人工上下分订单
     *
     * @param param 人工上下分订单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertOrderPerson(OrderPerson param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改人工上下分订单
     *
     * @param param 人工上下分订单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOrderPerson(OrderPerson param) {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除人工上下分订单
     *
     * @param ids 需要删除的人工上下分订单ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrderPersonByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除人工上下分订单信息
     *
     * @param id 人工上下分订单ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrderPersonById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    public List<OrderPerson> selectOrderPersonListBrUser(Long channelId, Integer id, String startTime, String endTime) {
        return orderPersonMapper.selectOrderPersonListBrUser(channelId, id, startTime, endTime);
    }

    public Map<String, Object> selectOrderPersonMap1BrUser(Long channelId, Integer id, String startTime, String endTime) {
        return orderPersonMapper.selectOrderPersonMap1BrUser(channelId, id, startTime, endTime);
    }

    public Map<String, Object> selectOrderPersonMap2BrUser(Long channelId, Integer type, Integer id, String startTime, String endTime) {
        return orderPersonMapper.selectOrderPersonMap2BrUser(channelId, type, id, startTime, endTime);
    }

    public List<OrderPerson> selectOrderPersonListBrchannelId(Long channelId, Integer currencyId, String startTime, String endTime) {
        return orderPersonMapper.selectOrderPersonListBrchannelId(channelId, currencyId, startTime, endTime);
    }

    public List<OrderPerson> selectOrderPersonListBrType(Long channelId, Integer currencyId, String startTime, String endTime) {
        return orderPersonMapper.selectOrderPersonListBrType(channelId, currencyId, startTime, endTime);
    }

    /**
     * 人工上下分订单sql统计
     *
     * @param startTime  创建时间-开始
     * @param endTime    创建时间-结束
     * @param currencyId 币种ID
     * @param userId     用户ID
     */
    public Map<String, Object> orderPersonCountMap(String startTime, String endTime, Integer currencyId, Long userId) {
        return orderPersonMapper.orderPersonCountMap(startTime, endTime, currencyId, userId);
    }


    public Map<Long, BigDecimal> queryWithdrawMap(List<Long> userIds){
        if(CollectionUtils.isEmpty(userIds)){
            return Collections.emptyMap();
        }
        List<Map<String,Object>> list = orderPersonMapper.queryWithdrawMap(userIds);
        return list.stream().collect(Collectors.toMap(
                m -> (Long) m.get("userId"),
                m -> (BigDecimal) m.getOrDefault("totalWithdrawAmount", BigDecimal.ZERO)
        ));
    }


}
