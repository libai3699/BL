package com.gp.common.mybatisplus.mqService;

import com.gp.common.mybatisplus.config.RedisOnlineService;
import com.gp.common.mybatisplus.entity.ChannelCountOrder;
import com.gp.common.mybatisplus.entity.CountOrder;
import com.gp.common.mybatisplus.entity.Currency;
import com.gp.common.mybatisplus.entity.UserCountOrder;
import com.gp.common.mybatisplus.mapper.ChannelCountOrderMapper;
import com.gp.common.mybatisplus.mapper.CountOrderMapper;
import com.gp.common.mybatisplus.mapper.UserCountOrderMapper;
import com.gp.common.mybatisplus.mq.OrderEntity;
import com.gp.common.mybatisplus.service.ChannelCountOrderService;
import com.gp.common.mybatisplus.service.ConsumeOrderService;
import com.gp.common.mybatisplus.service.CountOrderService;
import com.gp.common.mybatisplus.service.UserCountOrderService;
import com.gp.common.mybatisplus.vo.ConsumOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class WheelMqService {

    @Autowired
    public UserCountOrderService userCountOrderService;

    @Autowired
    public ChannelCountOrderService channelCountOrderService;

    @Autowired
    public CountOrderService tCountOrderService;

    @Resource
    private UserCountOrderMapper userCountOrderMapper;

    @Resource
    private ChannelCountOrderMapper channelCountOrderMapper;

    @Resource
    private CountOrderMapper countOrderMapper;
    @Resource
    private ConsumeOrderService consumeOrderService;
    @Resource
    private RedisOnlineService redisOnlineService;

    /**
     *
     * @param orderEntity
     */
    public void computeWheelUserCountOrder(
            Currency currency, OrderEntity orderEntity
            , UserCountOrder userCountOrder, ConsumOrderVO amountVo
    ) {
        if (userCountOrder == null) {
            userCountOrder = new UserCountOrder();
            userCountOrder.setUserId(orderEntity.getUserId());//用户id
            userCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
            userCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
            userCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
            if(Objects.nonNull(currency)){
                userCountOrder.setItemId(currency.getItemId());
                userCountOrder.setChainTag(currency.getChainTag());
            }
            userCountOrder.setUserAmount(amountVo.getUserAmount());//用户结余
            userCountOrder.setPlayWheelTermAward(orderEntity.getBonusAmount());//转盘彩金
            userCountOrder.setPlayWheelTermNum(1);//参入转盘活动次
            userCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//收入
            userCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
            userCountOrder.setCalculationsTime(new Date());//操作时间
            userCountOrder.setCreateTime(new Date());//操作时间
            userCountOrder.setUpdateTime(new Date());//操作时间
            int result = userCountOrderMapper.saveUserCountOrderData(userCountOrder);
            log.info("uuid={}, 转盘奖励->添加用户数据: {} 结果: {}", orderEntity.getUuid(), userCountOrder.toString(), result);
        } else {
            UserCountOrder updateUserCountOrder = new UserCountOrder();
            updateUserCountOrder.setId(userCountOrder.getId());
            updateUserCountOrder.setUserAmount(amountVo.getUserAmount());//用户结余
            updateUserCountOrder.setPlayWheelTermAward(orderEntity.getBonusAmount());//转盘彩金
            updateUserCountOrder.setPlayWheelTermNum(1);//参入转盘活动次
            updateUserCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//收入
            updateUserCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
            updateUserCountOrder.setCalculationsTime(new Date());//操作时间
            updateUserCountOrder.setUpdateTime(new Date());//操作时间
            int result = userCountOrderMapper.updateUserCountOrderData(updateUserCountOrder);
            log.info("uuid={}, 转盘奖励->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateUserCountOrder.toString(), result);
        }
    }


    public void computeWheelChannelCountOrder(Currency currency, OrderEntity orderEntity, ChannelCountOrder channelCountOrder, ConsumOrderVO amountVo) {
        if (channelCountOrder == null) {
            channelCountOrder = new ChannelCountOrder();
            channelCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
            channelCountOrder.setShareholderId(consumeOrderService.setShareholderIdFromChannel(orderEntity));//股东id
            channelCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
            channelCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
            if(Objects.nonNull(currency)){
                channelCountOrder.setItemId(currency.getItemId());
                channelCountOrder.setChainTag(currency.getChainTag());
            }
            channelCountOrder.setCreateTime(new Date());
            channelCountOrder.setUpdateTime(new Date());
            channelCountOrder.setUserAmount(amountVo.getChannelAmount());//用户结余
            channelCountOrder.setPlayWheelTermAward(orderEntity.getBonusAmount());//转盘彩金
            channelCountOrder.setPlayWheelTermNum(1);//参入转盘活动次
            channelCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//收入
            channelCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
            channelCountOrder.setCalculationsTime(new Date());//操作时间
            int result = channelCountOrderMapper.insertChannelCountOrderDataV2(channelCountOrder);
            log.info("uuid={}, 转盘奖励->添加渠道数据: {} 结果: {}", orderEntity.getUuid(), channelCountOrder.toString(), result);
        } else {
            ChannelCountOrder updateChannelCountOrder = new ChannelCountOrder();
            updateChannelCountOrder.setId(channelCountOrder.getId());
            updateChannelCountOrder.setShareholderId(consumeOrderService.setShareholderIdFromChannel(orderEntity));//股东id
            updateChannelCountOrder.setUserAmount(amountVo.getChannelAmount());//用户结余
            updateChannelCountOrder.setPlayWheelTermAward(orderEntity.getBonusAmount());//转盘彩金
            updateChannelCountOrder.setPlayWheelTermNum(1);//参入转盘活动次
            updateChannelCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//收入
            updateChannelCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
            updateChannelCountOrder.setCalculationsTime(new Date());//操作时间
            updateChannelCountOrder.setUpdateTime(new Date());
            int result = channelCountOrderMapper.updateChannelCountOrderDataV2(updateChannelCountOrder);
            log.info("uuid={}, 转盘奖励->更新渠道数据: {} 更新结果: {}", orderEntity.getUuid(), updateChannelCountOrder.toString(), result);
        }
    }

    public void computeWheelCountOrder(Currency currency, OrderEntity orderEntity, CountOrder countOrder, ConsumOrderVO amountVo) {
        if (countOrder == null) {
            countOrder = new CountOrder();
            countOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
            countOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
            if (Objects.nonNull(currency)) {
                countOrder.setItemId(currency.getItemId());
                countOrder.setChainTag(currency.getChainTag());
            }
            countOrder.setCreateTime(new Date());
            countOrder.setUpdateTime(new Date());
            countOrder.setUserAmount(amountVo.getOrderAmount());//用户结余
            countOrder.setPlayWheelTermAward(orderEntity.getBonusAmount());//转盘彩金
            countOrder.setPlayWheelTermNum(1);//参入转盘活动次
            countOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//收入
            countOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
            countOrder.setCalculationsTime(new Date());//操作时间
            countOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
            int result = countOrderMapper.insertCountOrderDataV2(countOrder);
            log.info("uuid={}, 转盘奖励->添加总览数据: {} 结果: {}", orderEntity.getUuid(), countOrder.toString(), result);
        } else {
            CountOrder updateCountOrder = new CountOrder();
            updateCountOrder.setId(countOrder.getId());
            updateCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
            updateCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
            updateCountOrder.setUserAmount(amountVo.getOrderAmount());//用户结余
            updateCountOrder.setPlayWheelTermAward(orderEntity.getBonusAmount());//转盘彩金
            updateCountOrder.setPlayWheelTermNum(1);//参入转盘活动次
            updateCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//收入
            updateCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
            updateCountOrder.setCalculationsTime(new Date());//操作时间
            updateCountOrder.setUpdateTime(new Date());
            updateCountOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
            int result = countOrderMapper.updateCountOrderDataV2(updateCountOrder);
            log.info("uuid={}, 转盘奖励->更新总览数据: {} 更新结果: {}", orderEntity.getUuid(), updateCountOrder.toString(), result);
        }
    }




}

