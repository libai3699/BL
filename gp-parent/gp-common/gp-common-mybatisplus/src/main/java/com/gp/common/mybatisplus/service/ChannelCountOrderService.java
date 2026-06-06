package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.ChannelCountOrder;
import com.gp.common.mybatisplus.entity.UserCountTotalAmount;
import com.gp.common.mybatisplus.mapper.ChannelCountOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 渠道每日订单统计Service业务层处理
 *
 * @author axing
 * @date 2024-05-24
 */
@Service
public class ChannelCountOrderService extends ServiceImpl<ChannelCountOrderMapper, ChannelCountOrder> {
    @Autowired
    private ChannelCountOrderMapper channelCountOrderMapper;

    /**
     * 查询渠道每日订单统计
     *
     * @param id 渠道每日订单统计ID
     * @return 渠道每日订单统计
     */

    public ChannelCountOrder selectChannelCountOrderById(Long id) {
        return channelCountOrderMapper.selectChannelCountOrderById(id);
    }

    /**
     * 查询渠道每日订单统计列表
     *
     * @param param 渠道每日订单统计
     * @return 渠道每日订单统计
     */

    public List<ChannelCountOrder> selectChannelCountOrderList(ChannelCountOrder param) {
        return channelCountOrderMapper.selectChannelCountOrderList(param);
    }

    /**
     * 新增渠道每日订单统计
     *
     * @param param 渠道每日订单统计
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertChannelCountOrder(ChannelCountOrder param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改渠道每日订单统计
     *
     * @param param 渠道每日订单统计
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateChannelCountOrder(ChannelCountOrder param) {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除渠道每日订单统计
     *
     * @param ids 需要删除的渠道每日订单统计ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteChannelCountOrderByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除渠道每日订单统计信息
     *
     * @param id 渠道每日订单统计ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteChannelCountOrderById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    public UserCountTotalAmount querySumAmountByChannelIdAndTime(Long channelId, Date startTime, Date endTime) {
        return channelCountOrderMapper.querySumAmountByUserIdAndTime(channelId, startTime, endTime);
    }

    public UserCountTotalAmount querySumAmountByChannelIdsAndTime(Long shareholderId, Long channelId, Date startTime, Date endTime) {
        return channelCountOrderMapper.querySumAmountByUserIdsAndTime(shareholderId, channelId, startTime, endTime);
    }

    public ChannelCountOrder queryByChannelId(Long channelId) {
        return channelCountOrderMapper.queryByChannelId(channelId);
    }

    public List<ChannelCountOrder> getByDayChannelCount(String startTime, String endTime, Integer currencyId, List<Long> channelIdList) {
        return channelCountOrderMapper.getByDayChannelCount(startTime, endTime, currencyId, channelIdList);
    }

    /**
     * 查询返水和返佣汇总数据
     *
     * @param endTime       结束时间
     * @param channelId     渠道ID
     * @param shareholderId 股东ID
     * @return 汇总结果
     */
    public ChannelCountOrder selectRebateAndCommissionSummary(String endTime, Long channelId, Long shareholderId) {
        return channelCountOrderMapper.selectRebateAndCommissionSummary(endTime, channelId, shareholderId);
    }

    /**
     * 获取股东分组数据
     *
     * @param startTime         开始时间
     * @param endTime           结束时间
     * @param currencyId        币种ID
     * @param shareholderIdList 股东ID列表
     * @return
     */
    public List<ChannelCountOrder> getByDayShareholderCount(String startTime, String endTime, Integer currencyId, List<Long> shareholderIdList) {
        return channelCountOrderMapper.getByDayShareholderCount(startTime, endTime, currencyId, shareholderIdList);
    }

    public List<ChannelCountOrder> getByMonthChannelCount(String startTime, String endTime, Integer currencyId, List<Long> channelIdList) {
        return channelCountOrderMapper.getByMonthChannelCount(startTime, endTime, currencyId, channelIdList);
    }

    public List<ChannelCountOrder> getByMonthShareholderCount(String startTime, String endTime, Integer currencyId, List<Long> shareholderIdList) {

        return channelCountOrderMapper.getByMonthShareholderCount(startTime, endTime, currencyId, shareholderIdList);
    }

    public ChannelCountOrder getUnclaimedRebateAndCommission(Long channelId, Long shareholderId, String dayStr) {
        return channelCountOrderMapper.getUnclaimedRebateAndCommission(channelId,shareholderId, dayStr);
    }

    public ChannelCountOrder getUnclaimedRebateByYesterday(List<Long> channelIdList,List<Long> shareholderIdList, String dayStr) {
        return  channelCountOrderMapper.getUnclaimedRebateByYesterday(channelIdList,shareholderIdList, dayStr);
    }
    
    /**
     * 统计渠道分组导出数据总量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param currencyId 币种ID
     * @param channelIdList 渠道ID列表
     * @return 数据总量
     */
    public int countByDayChannelCount(String startTime, String endTime, Integer currencyId, List<Long> channelIdList) {
        return channelCountOrderMapper.countByDayChannelCount(startTime, endTime, currencyId, channelIdList);
    }
    
    /**
     * 统计股东分组导出数据总量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param currencyId 币种ID
     * @param shareholderIdList 股东ID列表
     * @return 数据总量
     */
    public int countByDayShareholderCount(String startTime, String endTime, Integer currencyId, List<Long> shareholderIdList) {
        return channelCountOrderMapper.countByDayShareholderCount(startTime, endTime, currencyId, shareholderIdList);
    }
    
    /**
     * 统计渠道分组月数据导出总量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param currencyId 币种ID
     * @param channelIdList 渠道ID列表
     * @return 数据总量
     */
    public int countByMonthChannelCount(String startTime, String endTime, Integer currencyId, List<Long> channelIdList) {
        return channelCountOrderMapper.countByMonthChannelCount(startTime, endTime, currencyId, channelIdList);
    }
    
    /**
     * 统计股东分组月数据导出总量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param currencyId 币种ID
     * @param shareholderIdList 股东ID列表
     * @return 数据总量
     */
    public int countByMonthShareholderCount(String startTime, String endTime, Integer currencyId, List<Long> shareholderIdList) {
        return channelCountOrderMapper.countByMonthShareholderCount(startTime, endTime, currencyId, shareholderIdList);
    }
}
