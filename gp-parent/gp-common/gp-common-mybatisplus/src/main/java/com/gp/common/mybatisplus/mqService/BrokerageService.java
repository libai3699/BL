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
public class BrokerageService {

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
     * 领取返佣
     * 处理 UserCountOrder 数据
     */
    public void computeBrokerageUserCountOrder(
            Currency currency, OrderEntity orderEntity
            , UserCountOrder userCountOrder, ConsumOrderVO amount
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
            userCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日(加上反水)
            userCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getMoney()));//用户收入+领取的反水
            userCountOrder.setCustomerLossAmount(orderEntity.getMoney());
            userCountOrder.setCalculationsTime(new Date());//操作时间
            userCountOrder.setCreateTime(new Date());
            userCountOrder.setUpdateTime(new Date());
//            userCountOrder.setAlreadyReturnCommissionAmount(orderEntity.getMoney());
            int result = userCountOrderMapper.saveUserCountOrderData(userCountOrder);
            log.info("uuid={}, 领取用户反拥->添加用户数据: {} 结果: {}", orderEntity.getUuid(), userCountOrder.toString(), result);
        } else {
            UserCountOrder updateUserCountOrder = new UserCountOrder();
            updateUserCountOrder.setId(userCountOrder.getId());
            updateUserCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
            updateUserCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//用户收入
            updateUserCountOrder.setCustomerLossAmount(orderEntity.getMoney());
            updateUserCountOrder.setCalculationsTime(new Date());//操作时间
//            updateUserCountOrder.setAlreadyReturnCommissionAmount(orderEntity.getMoney());
            updateUserCountOrder.setUpdateTime(new Date());
            int result = userCountOrderMapper.updateUserCountOrderData(updateUserCountOrder);
            log.info("uuid={}, 领取用户反拥->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateUserCountOrder.toString(), result);
        }
    }

    /**
     * 领取返佣
     * 处理 ChannelCountOrder 数据
     */
    public void computeBrokerageChannelCountOrder(
            Currency currency, OrderEntity orderEntity
            , ChannelCountOrder channelCountOrder, ConsumOrderVO amount
    ) {
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
            channelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日(加上反水)
            channelCountOrder.setUserEarnings(orderEntity.getMoney());//用户收入+领取的反水
            channelCountOrder.setCustomerLossAmount(orderEntity.getMoney());
            channelCountOrder.setCalculationsTime(new Date());//操作时间
//            channelCountOrder.setAlreadyReturnCommissionAmount(orderEntity.getMoney());
            int result = channelCountOrderMapper.insertChannelCountOrderDataV2(channelCountOrder);
            log.info("uuid={}, 领取用户反拥->添加渠道数据: {} 结果: {}", orderEntity.getUuid(), channelCountOrder.toString(), result);
        } else {
            ChannelCountOrder updateChannelCountOrder = new ChannelCountOrder();
            updateChannelCountOrder.setId(channelCountOrder.getId());
            updateChannelCountOrder.setShareholderId(consumeOrderService.setShareholderIdFromChannel(orderEntity));//股东id
            updateChannelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
            updateChannelCountOrder.setUserEarnings(orderEntity.getMoney());//用户收入
            updateChannelCountOrder.setCustomerLossAmount(orderEntity.getMoney());
            updateChannelCountOrder.setCalculationsTime(new Date());//操作时间
//            updateChannelCountOrder.setAlreadyReturnCommissionAmount(orderEntity.getMoney());
            updateChannelCountOrder.setUpdateTime(new Date());
            int result = channelCountOrderMapper.updateChannelCountOrderDataV2(updateChannelCountOrder);
            log.info("uuid={}, 领取用户反拥->更新渠道数据: {} 更新结果: {}", orderEntity.getUuid(), updateChannelCountOrder.toString(), result);
        }
    }

    /**
     * 领取返佣
     * 处理 CountOrder 数据
     */
    public void computeBrokerageCountOrder(Currency currency, OrderEntity orderEntity, CountOrder countOrder, ConsumOrderVO amount) {
        if (countOrder == null) {
            countOrder = new CountOrder();
            countOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
            countOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
            if(Objects.nonNull(currency)){
                countOrder.setItemId(currency.getItemId());
                countOrder.setChainTag(currency.getChainTag());
            }
            countOrder.setCreateTime(new Date());
            countOrder.setUpdateTime(new Date());
            countOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日(加上反水)
            countOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getMoney()));//用户收入+领取的反水
            countOrder.setCustomerLossAmount(orderEntity.getMoney());
            countOrder.setCalculationsTime(new Date());//操作时间
//            countOrder.setAlreadyReturnCommissionAmount(orderEntity.getMoney());
            countOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
            int result = countOrderMapper.insertCountOrderDataV2(countOrder);
            log.info("uuid={}, 领取用户反拥->添加总览数据: {} 结果: {}", orderEntity.getUuid(), countOrder.toString(), result);
        } else {
            CountOrder updateCountOrder = new CountOrder();
            updateCountOrder.setId(countOrder.getId());
            updateCountOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
            updateCountOrder.setUserEarnings(orderEntity.getMoney());//用户收入
            updateCountOrder.setCustomerLossAmount(orderEntity.getMoney());
            updateCountOrder.setCalculationsTime(new Date());//操作时间
//            updateCountOrder.setAlreadyReturnCommissionAmount(orderEntity.getMoney());
            updateCountOrder.setUpdateTime(new Date());
            updateCountOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
            int result = countOrderMapper.updateCountOrderDataV2(updateCountOrder);
            log.info("uuid={}, 领取用户反拥->更新总览数据: {} 更新结果: {}", orderEntity.getUuid(), updateCountOrder.toString(), result);
        }
    }
}

