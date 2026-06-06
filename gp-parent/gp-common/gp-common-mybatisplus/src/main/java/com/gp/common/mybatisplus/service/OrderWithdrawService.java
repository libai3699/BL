package com.gp.common.mybatisplus.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.log.TelegramUtil;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.Channel;
import com.gp.common.mybatisplus.entity.OrderWithdraw;
import com.gp.common.mybatisplus.mapper.OrderWithdrawMapper;
import com.gp.common.mybatisplus.param.MerchantPayParam;
import com.gp.common.mybatisplus.vo.MerchantPayVo;
import com.gp.common.mybatisplus.until.OrderWithdrawSign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderWithdrawService extends ServiceImpl<OrderWithdrawMapper, OrderWithdraw> {
    @Autowired
    private OrderWithdrawMapper tOrderWithdrawMapper;
    @Autowired
    private OrderWithdrawSign orderWithdrawSign;
    @Resource
    private TelegramUtil telegramUtil;
    @Resource
    private ChannelService channelService;
    /**
     * 查询提现订单
     *
     * @param id 提现订单ID
     * @return 提现订单
     */

    public OrderWithdraw selectOrderWithdrawById(Long id) {
        return tOrderWithdrawMapper.selectOrderWithdrawById(id);
    }

    /**
     * 查询提现订单列表
     *
     * @param orderWithdraw 提现订单
     * @return 提现订单
     */

    public List<OrderWithdraw> selectOrderWithdrawList(OrderWithdraw orderWithdraw) {

        List<OrderWithdraw> orderWithdraws = tOrderWithdrawMapper.selectOrderWithdrawList(orderWithdraw);
        if (CollectionUtils.isNotEmpty(orderWithdraws)) {
            //渠道处理
            Set<Long> chainIdSet =
                    orderWithdraws.stream().map(OrderWithdraw::getChannelId).filter(channelId -> channelId != null && channelId != 0).collect(Collectors.toSet());
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

            orderWithdraws.forEach(orderAmount -> {
                //渠道名称
                if (channelEmpty && null != orderAmount.getChannelId() && channelMap.containsKey(orderAmount.getChannelId())) {
                    orderAmount.setChannelName(channelMap.get(orderAmount.getChannelId()).getChannelName());
                }

            });
        }
        return orderWithdraws;
    }


    /**
     * 查询提现订单数量
     *
     * @param orderWithdraw 提现订单
     * @return 提现订单数量
     */
    public long selectOrderWithdrawCount(OrderWithdraw orderWithdraw) {
        return tOrderWithdrawMapper.selectOrderWithdrawCount(orderWithdraw);
    }

    /**
     * 新增提现订单
     *
     * @param orderWithdraw 提现订单
     * @return 结果
     */

    public Boolean insertOrderWithdraw(OrderWithdraw orderWithdraw) {
        orderWithdraw.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(orderWithdraw);
        return result;
    }

    /**
     * 修改提现订单
     *
     * @param orderWithdraw 提现订单
     * @return 结果
     */

    public Boolean updateOrderWithdraw(OrderWithdraw orderWithdraw) {
        boolean result = this.updateById(orderWithdraw);
        return result;
    }

    /**
     * 批量删除提现订单
     *
     * @param ids 需要删除的提现订单ID
     * @return 结果
     */

    public Boolean deleteOrderWithdrawByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除提现订单信息
     *
     * @param id 提现订单ID
     * @return 结果
     */

    public Boolean deleteOrderWithdrawById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    private List<OrderWithdraw> queryNoSign(Integer start, Integer limit) {
        return this.tOrderWithdrawMapper.queryNoSign(start, limit);
    }

    public void dealNoSign() {
        log.info("开始-OrderTransfer,{}", new Date());
        //一次查多少条
        Integer start = 0;
        //一次查多少条
        Integer limit = 1000;
        List<OrderWithdraw> contents = this.queryNoSign(start, limit);
        dealSignContent(contents, start, limit);

        log.info("结束-OrderTransfer,{}", new Date());
    }

    private void dealSignContent(List<OrderWithdraw> contents, Integer start, Integer limit) {
        List<List<OrderWithdraw>> split = CollUtil.split(contents, 100);
        for (List<OrderWithdraw> amountChangeList : split) {
            List<OrderWithdraw> collect = amountChangeList.stream().map(e -> {
                orderWithdrawSign.dealSign(e);
                return e;
            }).collect(Collectors.toList());
            this.updateBatchById(collect);
        }
        //结束之后取给他 start 加 size
        start = start + contents.size();
        log.info("执行中: {} -- 执行的start !", start);

        contents = this.queryNoSign(start, limit);
        if (CollectionUtil.isEmpty(contents)) {
            return;  // 列表为空则直接返回，避免索引越界异常
        }
        log.info("执行中: {} -- 最后一条 !", contents.get(contents.size() - 1));
        dealSignContent(contents, start, limit);
    }

    public List<OrderWithdraw> selectOrderWithdrawListBrchannelId(Long channelId, Integer currencyId, String startTime, String endTime) {
        return tOrderWithdrawMapper.selectOrderWithdrawListBrchannelId(channelId, currencyId, startTime, endTime);
    }

    public Map<String, Object> summaryCount(OrderWithdraw tOrderWithdraw) {
        return tOrderWithdrawMapper.summaryCount(tOrderWithdraw);
    }

    public List<MerchantPayVo> summary(MerchantPayParam param) {
        return tOrderWithdrawMapper.summary(param);
    }

    public List<MerchantPayVo> payChannelList(MerchantPayParam param) {
        return tOrderWithdrawMapper.payChannelList(param);
    }

    public List<MerchantPayVo> payTypeWithdrawOne(MerchantPayParam param) {
        return tOrderWithdrawMapper.payTypeWithdrawOne(param);
    }

    public List<MerchantPayVo> payTypeWithdrawTwo(MerchantPayParam param) {
        return tOrderWithdrawMapper.payTypeWithdrawTwo(param);
    }
}
