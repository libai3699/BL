package com.gp.feign.util;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.ttl.TtlRunnable;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.UserMessageConstant;
import com.gp.common.base.feign.SendNotifyVo;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.mybatisplus.deal.DataProcessor;
import com.gp.common.mybatisplus.entity.PlayWheelConfig;
import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.entity.UserMessage;
import com.gp.common.mybatisplus.service.BotItemService;
import com.gp.common.mybatisplus.service.UserMessageService;
import com.gp.common.mybatisplus.service.UserService;
import com.gp.feign.api.BotService;
import com.gp.feign.dto.RedEnvelope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
public class MessageSend {

    @Resource
    private UserService botConfig;
    @Resource
    private BotItemService botItemService;
    @Resource
    private CecuUtil cecuUtil;

    /**
     * 发送红包消息 
     *
     * @return 结果
     */
    public void sendRedEnvelopeMsg(RedEnvelope redEnvelope) {
        SpringUtil.getBean(BotService.class).sendRedEnvelope(redEnvelope);
    }


    public void sendMsgAsyButton(TUser user, String title, String msg, Integer messageConstantOrderType, String linkOrderNo, Integer type) {
        sendMsg(user, title, msg, messageConstantOrderType, linkOrderNo,type);
    }
    /**
     * 发送用户消息(异步)
     *
     * @param user
     * @param title
     * @param msg
     * @param messageConstantOrderType
     * @param linkOrderNo
     * @return 结果
     */
    public void sendMsgAsy(TUser user, String title, String msg, Integer messageConstantOrderType, String linkOrderNo) {
        sendMsg(user, title, msg, messageConstantOrderType, linkOrderNo,null);
    }

    private void sendMsg(TUser user, String title, String msg, Integer messageConstantOrderType, String linkOrderNo,Integer type) {
        String dbCode = CecuUtil.getDbCode();
        CompletableFuture.runAsync(TtlRunnable.get(() -> {
            cecuUtil.cutDbByCode(dbCode);
            UserMessage userMessage = new UserMessage();
            userMessage.setUserId(user.getUserId());
            userMessage.setUserTgId(user.getUserTgId());
            userMessage.setOrderType(messageConstantOrderType);
            userMessage.setLinkOrderNo(linkOrderNo);
            userMessage.setTitle(title);
            userMessage.setContent(msg);
            String originalMsg = msg; // 保持原始消息不变
            MessagesUtils.setLang(user.getLanKey());
            //如果转盘奖励的话需要特殊处理
            if (messageConstantOrderType ==  UserMessageConstant.WHEEL_AWARD) {
                try {
                    PlayWheelConfig playWheelConfig = JSON.parseObject(msg, PlayWheelConfig.class);
                    DataProcessor.process(playWheelConfig);
                    originalMsg = playWheelConfig.getReminder(); // 使用处理后的提醒信息
                }catch (Exception e){
                    originalMsg = "恭喜获得转盘奖励" ;
                }
            }

            SpringUtil.getBean(UserMessageService.class).save(userMessage);
            if (user.getUserTgId() == null) {
                return;
            }

            SpringUtil.getBean(BotService.class).sendNotifyAsy(SendNotifyVo.builder().userTgId(user.getUserTgId()).userAvatar(user.getUserAvatar()).title(dealMessagesMsg(title)).content(dealMessagesMsg(originalMsg)).isMPayWithdraw(type).build());
        }));
    }

    public void sendMsgAsyNoMessage(TUser user, String title, String msg, String photo) {
        if (user.getUserTgId() == null) {
            return;
        }
        String dbCode = CecuUtil.getDbCode();
        CompletableFuture.runAsync(TtlRunnable.get(() -> {
            cecuUtil.cutDbByCode(dbCode);
            SpringUtil.getBean(BotService.class).sendNotifyAsy(SendNotifyVo.builder().userTgId(user.getUserTgId()).userAvatar(photo).title(title).content(msg).build());
        }));
    }

    public static String dealMessagesMsg(String messageTemplate){
        // 正则表达式匹配 <...> 和 非 <...> 部分
        StringBuilder combinedMsgBuilder = new StringBuilder();

        // 使用正则表达式匹配所有 <...> 的部分
        String regex = "<(.*?)>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(messageTemplate);

        int lastEnd = 0; // 记录最后一个匹配结束的位置

        while (matcher.find()) {
            // 处理匹配到的文本前的部分
            String beforeText = messageTemplate.substring(lastEnd, matcher.start());
            combinedMsgBuilder.append(beforeText); // 添加匹配前文本

            // 获取匹配的键值
            String key = matcher.group(1); // 提取 <...> 中的内容

            // 通过 MessagesUtils.get() 获取国际化文本
            String internationalizedText = MessagesUtils.get(key);
            combinedMsgBuilder.append(internationalizedText); // 添加国际化文本

            lastEnd = matcher.end(); // 更新 lastEnd
        }

        // 处理文本最后可能有剩余部分
        if (lastEnd < messageTemplate.length()) {
            combinedMsgBuilder.append(messageTemplate.substring(lastEnd));
        }

        // 返回最终拼接后的字符串
        return combinedMsgBuilder.toString();
    }

}
