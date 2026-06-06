//package com.gp.common.mybatisplus.service;
//
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.gp.common.base.utils.DateUtils;
//import com.gp.common.mybatisplus.entity.AmountChangeAll;
//import com.gp.common.mybatisplus.mapper.AmountChangeAllMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//
///**
// * 用户账变Service业务层处理
// *
// * @author axing
// * @date 2024-10-19
// */
//@Service
//public class AmountChangeAllService extends ServiceImpl<AmountChangeAllMapper, AmountChangeAll>
//{
//    @Autowired
//    private AmountChangeAllMapper amountChangeAllMapper;
//
//    /**
//     * 查询用户账变
//     *
//     * @param id 用户账变ID
//     * @return 用户账变
//     */
//
//    public AmountChangeAll selectAmountChangeAllById(Long id)
//    {
//        return amountChangeAllMapper.selectAmountChangeAllById(id);
//    }
//
//    /**
//     * 查询用户账变列表
//     *
//     * @param param 用户账变
//     * @return 用户账变
//     */
//
//    public List<AmountChangeAll> selectAmountChangeAllList(AmountChangeAll param)
//    {
//        return amountChangeAllMapper.selectAmountChangeAllList(param);
//    }
//
//    /**
//     * 新增用户账变
//     *
//     * @param param 用户账变
//     * @return 结果
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public Boolean insertAmountChangeAll(AmountChangeAll param)
//    {
//        param.setCreateTime(DateUtils.getNowDate());
//        boolean result = this.save(param);
//        return result;
//    }
//
//    /**
//     * 修改用户账变
//     *
//     * @param param 用户账变
//     * @return 结果
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public Boolean updateAmountChangeAll(AmountChangeAll param)
//    {
//        boolean result = this.updateById(param);
//        return result;
//    }
//
//    /**
//     * 批量删除用户账变
//     *
//     * @param ids 需要删除的用户账变ID
//     * @return 结果
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public Boolean deleteAmountChangeAllByIds(Long[] ids)
//    {
//        boolean result = this.removeByIds(Arrays.asList(ids));
//        return result;
//    }
//
//    /**
//     * 删除用户账变信息
//     *
//     * @param id 用户账变ID
//     * @return 结果
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public Boolean deleteAmountChangeAllById(Long id)
//    {
//        boolean result = this.removeById(id);
//        return result;
//    }
//
//    public List<AmountChangeAll> selectAmountChangeListBychannelId(Long channelId, Integer id, String startTime, String endTime) {
//        return amountChangeAllMapper.selectAmountChangeListBychannelId(channelId,id,startTime,endTime);
//    }
//
//
//    public List<AmountChangeAll> selectAmountChangeAllByTime(Long channelId, String startTime, String endTime) {
//        return amountChangeAllMapper.selectAmountChangeAllByTime(channelId,startTime,endTime);
//    }
//
//    public long selectAmountChangeAllByTimeSql(Long channelId, String startTime, String endTime) {
//        return amountChangeAllMapper.selectAmountChangeAllByTimeSql(channelId,startTime,endTime);
//    }
//
//    public Map<String, Object> amountChangeAllCountMap(String startTime, String endTime, Long userId, Integer currencyId) {
//        return baseMapper.amountChangeAllCountMap(startTime,endTime,userId,currencyId);
//    }
//
//
//    public Map<String, Object> selectAmountChangeMapBychannelId(Long channelId, Integer id, String startTime, String endTime, Integer accountType, Integer orderType, Integer type) {
//        return amountChangeAllMapper.selectAmountChangeMapBychannelId( channelId, id, startTime, endTime, accountType, orderType, type);
//    }
//
//    public int selectAmountChangeAllByTimeCount(Long channelId, String startTime, String endTime) {
//        return baseMapper.selectAmountChangeAllByTimeCount(channelId,startTime,endTime);
//    }
//
//    public Integer getActiveUserCountSql(String startTime, String endTime) {
//        return amountChangeAllMapper.getActiveUserCountSql(startTime, endTime);
//    }
//}
