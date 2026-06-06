//package com.gp.common.mybatisplus.service;
//
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.gp.common.base.utils.DateUtils;
//import com.gp.common.mybatisplus.entity.OrderTerm;
//import com.gp.common.mybatisplus.entity.OrderTermAll;
//import com.gp.common.mybatisplus.mapper.OrderTermAllMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
///**
// * 用户投注Service业务层处理
// *
// * @author axing
// * @date 2024-10-19
// */
//@Service
//public class OrderTermAllService extends ServiceImpl<OrderTermAllMapper, OrderTermAll>
//{
//    @Autowired
//    private OrderTermAllMapper orderTermAllMapper;
//
//    /**
//     * 查询用户投注
//     *
//     * @param id 用户投注ID
//     * @return 用户投注
//     */
//
//    public OrderTermAll selectOrderTermAllById(Long id)
//    {
//        return orderTermAllMapper.selectOrderTermAllById(id);
//    }
//
//    /**
//     * 查询用户投注列表
//     *
//     * @param param 用户投注
//     * @return 用户投注
//     */
//
//    public List<OrderTermAll> selectOrderTermAllList(OrderTermAll param)
//    {
//        return orderTermAllMapper.selectOrderTermAllList(param);
//    }
//
//    /**
//     * 新增用户投注
//     *
//     * @param param 用户投注
//     * @return 结果
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public Boolean insertOrderTermAll(OrderTermAll param)
//    {
//        param.setCreateTime(DateUtils.getNowDate());
//        boolean result = this.save(param);
//        return result;
//    }
//
//    /**
//     * 修改用户投注
//     *
//     * @param param 用户投注
//     * @return 结果
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public Boolean updateOrderTermAll(OrderTermAll param)
//    {
//        boolean result = this.updateById(param);
//        return result;
//    }
//
//    /**
//     * 批量删除用户投注
//     *
//     * @param ids 需要删除的用户投注ID
//     * @return 结果
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public Boolean deleteOrderTermAllByIds(Long[] ids)
//    {
//        boolean result = this.removeByIds(Arrays.asList(ids));
//        return result;
//    }
//
//    /**
//     * 删除用户投注信息
//     *
//     * @param id 用户投注ID
//     * @return 结果
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public Boolean deleteOrderTermAllById(Long id)
//    {
//        boolean result = this.removeById(id);
//        return result;
//    }
//
//    public List<OrderTermAll> selectOrderTermListByChannelIds(String threeDayEndTime, String startTime, String endTime) {
//        return orderTermAllMapper.selectOrderTermListByChannelIds( threeDayEndTime,startTime,endTime);
//    }
//
//    public List<OrderTermAll> selectOrderTermListByChannelId(Long channelId, String threeDayEndTimeStr, String startTime, String endTime) {
//        return orderTermAllMapper.selectOrderTermListByChannelId( channelId,threeDayEndTimeStr,startTime,endTime);
//    }
//
//    public Map<String, Object> betOrderTermMap(String startTime, String endTime, Integer id, Long channelId) {
//        return orderTermAllMapper.betOrderTermMap( startTime,endTime,id,channelId);
//    }
//
//    public Map<String, Object> settleOrderTermMap(String startTime, String endTime, Integer id, Long channelId) {
//        return orderTermAllMapper.settleOrderTermMap( startTime,endTime,id,channelId);
//    }
//
//    public Map<String, BigDecimal> getTotalBetAmount(OrderTerm param) {
//        return orderTermAllMapper.getTotalBetAmount(param);
//    }
//
//    public Map<String, Object> fetchOrderTermsBycreatTime(String startTime, String endTime, String plateCode, Integer currencyId) {
//        return orderTermAllMapper.fetchOrderTermsBycreatTime(startTime,endTime,plateCode,currencyId);
//    }
//
//    public Map<String, Object> fetchOrderTermsBysettleTime(String startTime, String endTime, String plateCode, Integer currencyId) {
//        return orderTermAllMapper.fetchOrderTermsBysettleTime(startTime,endTime,plateCode,currencyId);
//    }
//}
