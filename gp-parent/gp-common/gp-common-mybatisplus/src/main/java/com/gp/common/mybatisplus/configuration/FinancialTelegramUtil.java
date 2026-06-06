package com.gp.common.mybatisplus.configuration;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import com.common.core.enums.AmountChangeTypeEnum;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.BotUrlFormat;
import com.gp.common.base.constant.UserExtTypeCons;
import com.gp.common.base.utils.BigDecimalUtils;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.Channel;
import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.nacos.BotConfig;
import com.gp.common.mybatisplus.nacos.FinanceBotConfig;
import com.gp.common.mybatisplus.nacos.ReportBotConfig;
import com.gp.common.mybatisplus.service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 电报机器人工具bean
 *
 * @author admin
 * @date 2021/08/06
 */
@Slf4j
public class FinancialTelegramUtil extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    private final String chatId;

    @Resource
    private UserWalletService userWalletService;
    @Resource
    private UserExtService userExtService;
    @Resource
    private UserService userService;
    @Resource
    public BotItemService botItemService;
    @Resource
    public MybatisBotService mybatisBotService;
    @Resource
    public ChannelService channelService;

    public FinancialTelegramUtil(String botUsername, String botToken, String chatId) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.chatId = chatId;
    }

    @Override
    public String getBotUsername() {
        // 填写username
        return botUsername;
    }

    @Override
    public String getBotToken() {
        // 填写token
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Robot回复的内容");
            System.out.println("Robot回复的内容");
            try {
                execute(message);
            } catch (TelegramApiException e) {
                log.error("Robot消息异常", e);
            }
        }
    }


    public void sendMessage(String tex) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(tex);
        message.disableWebPagePreview(); // 可选，禁用网页预览，根据需要调整
        message.setParseMode(ParseMode.HTML);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("消费失败", e);
        }
    }


    public void sendTXFWMsg(Long userId, BigDecimal amount, String orderNo, String operator) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            log.info("发送财务信息出错", e);
        }

        BigDecimal CZMoney = BigDecimal.ZERO;
        BigDecimal TXdMoney = BigDecimal.ZERO;
        TUser user = null;

        try {
            String operatorStr = null;
            user = userService.getById(userId);
            if (StrUtil.isNotEmpty(operator)) {
                operatorStr = new StringBuilder()
                        .append("操作人: ").append(operator).append("\n").toString();
            }
            String userName = HtmlUtil.cleanHtmlTag(BotUrlFormat.getUserName(user.getUserTgUsername(), user.getUserTgName(), user.getUserId()));
            String msg = new StringBuilder()
                    .append("#提现通知").append("\n\n")
                    .append("订单号: ").append("<code>" + orderNo + "</code>").append("\n")
                    .append("用户名: ").append("<code>" + userName + "</code>").append("\n")
                    .append("用户id: ").append("<code>" + user.getUserId() + "</code>").append("\n")
                    .append(StrUtil.isEmpty(operatorStr) ? "" : operatorStr)
                    .append("用户飞机id: ").append("<code>" + user.getUserTgId() + "</code>").append("\n")
                    .append("金额: ").append(BigDecimalUtils.trim(amount)).append("\n")
                    .append("余额: ").append(BigDecimalUtils.trim(userWalletService.getUsdtAmount(user, CurrencyService.usdtCurrency).add(CZMoney))).append("\n")
                    .append("总充值: ").append(BigDecimalUtils.trim(userExtService.queryUSerExt(userId, UserExtTypeCons.累计充值).getAmount().add(CZMoney))).append("\n")
                    .append("总提现: ").append(BigDecimalUtils.trim(userService.queryWithdrawAll(userId).add(TXdMoney))).append("\n")
                    .append("北京时间: ").append(DateUtils.formatDateToStr(new Date(), DateUtils.YYYY_MM_DD_HH_MM_SS)).append("\n")
                    .toString();

            FinancialTelegramUtil financialBot = BotConfiguration.getFinancialBot(CecuUtil.getDbCode());
            financialBot.sendMessage(msg);
        } catch (Exception e) {
            log.info("发送财务信息出错", e);
        }
        try {
            ReportTelegramUtil reportTelegramUtil = BotConfiguration.getReportBot(CecuUtil.getDbCode());
            reportTelegramUtil.sendRechargeMsg(user, amount, 0);
        } catch (Exception e) {
            log.info("发送播报出错", e);
        }
    }

    public void sendCZFWMsg(Long userId, BigDecimal amount, BigDecimal bonus, BigDecimal userMoney, AmountChangeTypeEnum amountChangeTypeEnum, String orderNo, String merchantCode, String operator, String remark, String extParam1) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (Exception e) {
            log.info("发送财务信息出错", e);
        }

        BigDecimal CZMoney = BigDecimal.ZERO;
        BigDecimal TXdMoney = BigDecimal.ZERO;
        String title = "";
        String method = merchantCode;
        Integer type = 1;
        String operatorStr = null;

        if (Objects.equals(amountChangeTypeEnum, AmountChangeTypeEnum.personAmountUp)) {
            title = "#人工上分通知";
            method = "后台";
            if (StrUtil.isNotEmpty(operator)) {
                operatorStr = new StringBuilder()
                        .append("操作人: ").append(operator).append("\n").toString();
            }
            //CZMoney = CZMoney.add(amount);
        }
        if (Objects.equals(amountChangeTypeEnum, AmountChangeTypeEnum.personAmountDown)) {
            title = "#人工下分通知";
            if ("2".equals(extParam1)) {
                title = "#扣除彩金通知";
            }
            method = "后台";
            type = 0;
            if (StrUtil.isNotEmpty(operator)) {
                operatorStr = new StringBuilder()
                        .append("操作人: ").append(operator).append("\n").toString();
            }
            //TXdMoney = TXdMoney.add(amount);
        }
        if (Objects.equals(amountChangeTypeEnum, AmountChangeTypeEnum.userRecharge)) {
            title = "#用户充值";
            //CZMoney = CZMoney.add(amount);
        }
        FinancialTelegramUtil financialBot = BotConfiguration.getFinancialBot(CecuUtil.getDbCode());
        TUser user = userService.getById(userId);
        Long channelId = user.getChannelId();
        Channel channel = null;
        if (channelId != null && channelId != 0) {
            channel = channelService.getById(channelId);
        }
        String userName = HtmlUtil.cleanHtmlTag(BotUrlFormat.getUserName(user.getUserTgUsername(), user.getUserTgName(), user.getUserId()));
        StringBuilder msg = new StringBuilder()
                .append("<b>" + title + "</b>").append("\n\n")
                .append("订单号: ").append("<code>" + orderNo + "</code>").append("\n")
                .append("充值方式: ").append("<code>" + method + "</code>").append("\n")
                .append(StrUtil.isEmpty(operatorStr) ? "" : operatorStr)
                .append("用户名: ").append("<code>" + userName + "</code>").append("\n")
                .append("用户id: ").append("<code>" + user.getUserId() + "</code>").append("\n")
                .append("用户飞机id: ").append("<code>" + user.getUserTgId() + "</code>").append("\n");
        if(channel != null){
            // 假设您有 channelName 和 channelCode 变量
            String channelName = channel.getChannelName(); // 获取渠道名称
            String channelCode = channel.getChannelCode(); // 获取渠道编码
            if (!StrUtil.isEmpty(channelName) && !StrUtil.isEmpty(channelCode)) {
                msg.append("渠道名称: ").append("<code>").append(channelName).append("</code>").append("\n")
                        .append("渠道编码: ").append("<code>").append(channelCode).append("</code>").append("\n");
            }
        }

        Long superUserId = user.getSuperUserId();
        if (superUserId != null && superUserId != 0) {
            TUser superUser = userService.getById(superUserId);
            if (superUser != null) {
                String userNamePre = HtmlUtil.cleanHtmlTag(BotUrlFormat.getUserName(superUser.getUserTgUsername(), superUser.getUserTgName(), superUser.getUserId()));
                msg.append("上级用户名: ").append("<code>" + userNamePre + "</code>").append("\n")
                        .append("上级id: ").append("<code>" + superUser.getUserId() + "</code>").append("\n")
                        .append("上级飞机id: ").append("<code>" + superUser.getUserTgId() + "</code>").append("\n");
            }
        }

       String finalMsg = msg
                .append("金额: ").append(BigDecimalUtils.trim(amount)).append("\n")
                .append("彩金: ").append(BigDecimalUtils.trim(bonus)).append("\n")
                .append("余额: ").append(BigDecimalUtils.trim(userMoney)).append("\n")
                .append("总充值: ").append(BigDecimalUtils.trim(userExtService.queryUSerExt(userId, UserExtTypeCons.累计充值).getAmount().add(CZMoney))).append("\n")
                .append("总提现: ").append(BigDecimalUtils.trim(userService.queryWithdrawAll(userId).add(TXdMoney))).append("\n")
                .append("北京时间: ").append(DateUtils.formatDateToStr(new Date(), DateUtils.YYYY_MM_DD_HH_MM_SS)).append("\n")
                .append(StrUtil.isEmpty(remark) ? "" : "备注: ").append(StrUtil.isEmpty(remark) ? "" : remark).append(StrUtil.isEmpty(remark) ? "" : "\n")
                .append("通知人: ").append("@" + mybatisBotService.getMybatisBotConfig().getSendRen()).append("\n")
                .toString();
        financialBot.sendMessage(finalMsg);
        try {
            if (Objects.equals(amountChangeTypeEnum, AmountChangeTypeEnum.personAmountDown)) {
                if ("2".equals(extParam1)) {
                    return;
                }
            }
            ReportTelegramUtil reportTelegramUtil = BotConfiguration.getReportBot(CecuUtil.getDbCode());
            reportTelegramUtil.sendRechargeMsg(user, amount, type);
        } catch (Exception e) {
            log.info("发送播报出错", e);
        }


    }

}
