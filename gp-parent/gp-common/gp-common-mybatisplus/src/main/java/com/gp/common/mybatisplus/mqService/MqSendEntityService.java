package com.gp.common.mybatisplus.mqService;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.common.rabbitmq.model.MessageBody;
import com.common.rabbitmq.service.RabbitMqTemplate;
import com.gp.common.base.constant.mq.MqEnum;
import com.gp.common.mybatisplus.mq.*;
import com.gp.common.mybatisplus.service.LanguageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
@Slf4j
public class MqSendEntityService {
    @Resource
    private LanguageService languageService;
    public void  sendOrderEntity(OrderEntity orderEntity){
        try{
            orderEntity.setSendDateTime(new Date());
            log.info("发送消息是{}",JSON.toJSONString(orderEntity));
            SpringUtil.getBean(RabbitMqTemplate.class).sendMq(MqEnum.orderTaskMq.getExchange(), MqEnum.orderTaskMq.getKey(), MessageBody.builder()
                    .data(JSON.toJSON(orderEntity)).build());
        }catch (Exception e){
            log.info("发送消息异常");
        }

    }
    public void sendHelpMoneyEntity(HelpMoneyEntity helpMoneyEntity){
        try{
            helpMoneyEntity.setSendDateTime(new Date());
            log.info("发送消息是{}",JSON.toJSONString(helpMoneyEntity));
            SpringUtil.getBean(RabbitMqTemplate.class).sendMq(MqEnum.helpMoneyMq.getExchange(), MqEnum.helpMoneyMq.getKey(), MessageBody.builder()
                    .data(JSON.toJSON(helpMoneyEntity)).build());
        }catch (Exception e){
            log.info("发送消息异常");
        }
    }

    public void sendNewRedPacket(Long tgUserId,String dbCode,String lanKey) {
        try{
            if(StrUtil.isEmpty(lanKey)){
                lanKey = languageService.getDefaultLanguage();
            }
            NewRedPacketEntity newRedPacketEntity = new NewRedPacketEntity();
            newRedPacketEntity.setSendDateTime(new Date());
            newRedPacketEntity.setTgUserId(tgUserId);
            newRedPacketEntity.setProductId(dbCode);
            newRedPacketEntity.setLankey(lanKey);
            SpringUtil.getBean(RabbitMqTemplate.class).sendMq(MqEnum.newRedPacketMq.getExchange(), MqEnum.newRedPacketMq.getKey(), MessageBody.builder()
                    .data(JSON.toJSON(newRedPacketEntity)).build());
        }catch (Exception e){
            log.info("发送消息异常");
        }
    }

    public void sendOtherDeal(OtherDealOrder otherDealOrder){
        try{
            SpringUtil.getBean(RabbitMqTemplate.class).sendMq(MqEnum.otherDealMq.getExchange(), MqEnum.otherDealMq.getKey(), MessageBody.builder()
                    .data(JSON.toJSON(otherDealOrder)).build());
        }catch (Exception e){
            log.info("发送消息异常");
        }
    }
    public void sendRebateConsume(RebateDealEntity rebateDealEntity,MqEnum mqEnum){
        try{
            SpringUtil.getBean(RabbitMqTemplate.class).sendMq(mqEnum.getExchange(), mqEnum.getKey(), MessageBody.builder()
                    .data(JSON.toJSON(rebateDealEntity)).build());
        }catch (Exception e){
            log.info("发送消息异常");
        }
    }
    public void sendRechargeConsume(RechargeEntity rechargeEntity,MqEnum mqEnum){
        try{
            SpringUtil.getBean(RabbitMqTemplate.class).sendMq(mqEnum.getExchange(), mqEnum.getKey(), MessageBody.builder()
                    .data(JSON.toJSON(rechargeEntity)).build());
        }catch (Exception e){
            log.info("发送消息异常");
        }
    }

    /**
     * 发送统计校准MQ（投注额/派彩额/输赢/打码量 差值修正）
     */
    public void sendCalibrationStats(CalibrationStatsEntity entity) {
        try {
            entity.setSendDateTime(new Date());
            log.info("发送统计校准消息: {}", JSON.toJSONString(entity));
            SpringUtil.getBean(RabbitMqTemplate.class).sendMq(
                    MqEnum.calibrationStatsMq.getExchange(),
                    MqEnum.calibrationStatsMq.getKey(),
                    MessageBody.builder().data(JSON.toJSON(entity)).productId(entity.getProductId()).build());
        } catch (Exception e) {
            log.error("发送统计校准消息异常: {}", e.getMessage());
        }
    }
}

