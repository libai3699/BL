package com.gp.common.mybatisplus.configuration;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.common.core.log.TelegramUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.utils.AdminUtil;
import com.gp.common.base.utils.BigDecimalUtils;
import com.gp.common.base.utils.FiletypeUtil;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.mybatisplus.entity.AttachButton;
import com.gp.common.mybatisplus.entity.ConfigRisk;
import com.gp.common.mybatisplus.entity.Customer;
import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.nacos.BotConfig;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import com.gp.common.mybatisplus.service.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaAudio;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 播报机器人
 *
 * @author admin
 * @date 2021/08/06
 */
@RefreshScope
@Slf4j
public class ReportTelegramUtil extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    private final String chatId;
    @Resource
    public BotItemService botItemService;

    @Resource
    public MybatisBotService mybatisBotService;
    @Resource
    public ConfigRiskService configRiskService;
    @Resource
    public AttachButtonService attachButtonService;
    @Resource
    public ConfigAmountService configAmountService;

    public ReportTelegramUtil(String botUsername, String botToken, String chatId) {
        this.botUsername = botUsername;
        this.botToken = botToken;
        this.chatId = chatId;

    }

    @Resource
    private MNacosParam mNacosParam;
    @Resource
    private CustomerService customerService;

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

    /**
     * @param user
     * @param money
     * @param type  0 提现 1 充值
     */
    //发送充值消息
    public void sendRechargeMsg(TUser user, BigDecimal money, Integer type) {

        sendRechargeItem(user, money, type, null);
        ConfigRisk configRisk = configRiskService.getConfigOneUrlMaybeNul(ConfigRiskService.notifyBot);
        if (configRisk == null) {
            return;
        }
        String configValue = configRisk.getConfigValue();
        if (StrUtil.isEmpty(configValue)) {
            return;
        }
        String[] split = configValue.split(",");
        for (String s : split) {
            try {
                sendRechargeItem(user, money, type, s);
            } catch (Exception e) {
                log.error("发送消息出错", e);
            }
        }
        //查询下那些群组需要同步发送
    }

    private void sendRechargeItem(TUser user, BigDecimal money, Integer type, String chatUsername) {
        if (money.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        MessagesUtils.setLang(user.getLanKey());
        String userName = desensitizeWithLastChar(user);
        String url = "";
        try {
            if (type == 1) {
                url = mybatisBotService.getMybatisBotConfig().getRechargeUrl();
            }
            if (type == 0) {
                url = mybatisBotService.getMybatisBotConfig().getWithdrawUrl();
            }
        } catch (Exception e) {
            log.info("发送消息出错", e);
        }

        String photo = AdminUtil.perfectAdmin(mNacosParam.getFileAdmin(), url);
        String caption = "";
        String moneyUnit = MessagesUtils.get("bot.money.unit");
        if (type == 1) {
            String rechargeMsg = MessagesUtils.get("bot.report.recharge");
            caption = StrUtil.format(rechargeMsg, userName, BigDecimalUtils.trim(money), moneyUnit);
        }
        if (type == 0) {
            String rechargeMsg = MessagesUtils.get("bot.report.withdraw");
            caption = StrUtil.format(rechargeMsg, userName, BigDecimalUtils.trim(money), moneyUnit);
        }

        try {
            if (FiletypeUtil.isGIf(photo)) {
                SendAnimation sendAnimation = new SendAnimation();
                sendAnimation.setChatId(chatUsername == null ? chatId : chatUsername);
                sendAnimation.setAnimation(new InputFile(photo));
                sendAnimation.setCaption(caption);
                sendAnimation.setParseMode(ParseMode.HTML);
                String language = user.getLanKey();
                this.dealMsg(sendAnimation, language);
            } else if (FiletypeUtil.isVideo(photo)) {
                SendVideo sendVideo = new SendVideo();
                sendVideo.setChatId(chatUsername == null ? chatId : chatUsername);
                sendVideo.setVideo(new InputFile(photo));
                sendVideo.setCaption(caption);
                sendVideo.setParseMode(ParseMode.HTML);
                String language = user.getLanKey();
                this.dealMsg(sendVideo, language);
            } else {
                SendPhoto message = new SendPhoto();
                message.setChatId(chatUsername == null ? chatId : chatUsername);
                message.setPhoto(new InputFile(photo));
                message.setCaption(caption);
                message.setParseMode(ParseMode.HTML);
                String language = user.getLanKey();
                this.dealMsg(message, language);
            }
        } catch (Exception e) {
            log.error("发送消息失败", e);
        }
    }

    //发送充值消息
    public void sendWinMsg(String mes, String defaultLanKey) {
        try {
            sendWinMsgItem(mes, null, defaultLanKey);
        } catch (Exception e) {
            log.warn("发送播报失败, 产品id: {}", CecuUtil.getDbCode());
        }
        ConfigRisk configRisk = configRiskService.getConfigOneUrlMaybeNul(ConfigRiskService.notifyBot);
        if (configRisk == null) {
            return;
        }
        String configValue = configRisk.getConfigValue();
        if (StrUtil.isEmpty(configValue)) {
            return;
        }
        String[] split = configValue.split(",");
        for (String s : split) {
            try {
                sendWinMsgItem(mes, s, defaultLanKey);
            } catch (Exception e) {
                log.error("发送消息出错", e);
            }

        }
    }

    private void sendWinMsgItem(String mes, String chatUsername, String defaultLanKey) {
        MessagesUtils.setLang(StrUtil.isBlank(defaultLanKey) ? Locale.CHINA.toString() : defaultLanKey);
        String photo = AdminUtil.perfectAdmin(mNacosParam.getFileAdmin(), mybatisBotService.getMybatisBotConfig().getWinUrl());
        String language = LocaleContextHolder.getLocale().toString();

        try {
            if (FiletypeUtil.isGIf(photo)) {
                SendAnimation sendAnimation = new SendAnimation();
                sendAnimation.setChatId(chatUsername == null ? chatId : chatUsername);
                sendAnimation.setAnimation(new InputFile(photo));
                sendAnimation.setCaption(mes);
                sendAnimation.setParseMode(ParseMode.HTML);
                this.dealMsg(sendAnimation, language);
            } else if (FiletypeUtil.isVideo(photo)) {
                SendVideo sendVideo = new SendVideo();
                sendVideo.setChatId(chatUsername == null ? chatId : chatUsername);
                sendVideo.setVideo(new InputFile(photo));
                sendVideo.setCaption(mes);
                sendVideo.setParseMode(ParseMode.HTML);
                this.dealMsg(sendVideo, language);
            } else {
                SendPhoto message = new SendPhoto();
                message.setChatId(chatUsername == null ? chatId : chatUsername);
                message.setPhoto(new InputFile(photo));
                message.setCaption(mes);
                message.setParseMode(ParseMode.HTML);
                this.dealMsg(message, language);
            }
        } catch (Exception e) {
            log.error("发送消息失败", e);
        }
    }

    public void dealMsg(Object message, String language) {
        BotConfig botConfig = botItemService.getNowBot();
        InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        // 创建一个按钮 - 进入游戏
        InlineKeyboardButton joinButton = new InlineKeyboardButton();
        joinButton.setText(MessagesUtils.get("bot.report.KSYX"));
        joinButton.setUrl(TelegramUtil.tgAdmin + botConfig.getGameBotUsername()); // 设置按钮的回调数据
        List<InlineKeyboardButton> one = CollUtil.newArrayList(joinButton);
        keyboard.add(one);
        //这里查询下之前表里有没有
        //没有的话 再去补这个些按钮
        //查询对于语言的按钮
        List<AttachButton> attachButtons = attachButtonService.queryAttachButton(language);
        if (CollUtil.isEmpty(attachButtons)) {
            addKFButton(language, keyboard);
        } else {
            doAttachButton(attachButtons, keyboard);
        }

        replyMarkup.setKeyboard(keyboard);

        try {
            if (message instanceof SendPhoto) {
                ((SendPhoto) message).setReplyMarkup(replyMarkup);
                execute((SendPhoto) message);
            } else if (message instanceof SendAnimation) {
                ((SendAnimation) message).setReplyMarkup(replyMarkup);
                execute((SendAnimation) message);
            } else if (message instanceof SendVideo) {
                ((SendVideo) message).setReplyMarkup(replyMarkup);
                execute((SendVideo) message);
            }
        } catch (TelegramApiException e) {
            log.error("发送消息失败", e);
        }
    }

    private void addKFButton(String language, List<List<InlineKeyboardButton>> keyboard) {
        // 创建按钮 - 专属财务和唯一客服
        List<InlineKeyboardButton> two = new ArrayList<>();
        // 专属财务按钮
        InlineKeyboardButton financeButton = new InlineKeyboardButton();
        financeButton.setText(MessagesUtils.get("bot.report.GFKF"));
        Customer customerCW = customerService.queryCustomer(1, language);
        if (customerCW != null) {
            String url = dealCustomer(customerCW);
            financeButton.setUrl(url); // 设置按钮的回调数据
            two.add(financeButton);
        }

        // 成为代理
        InlineKeyboardButton customerButton = new InlineKeyboardButton();
        customerButton.setText(MessagesUtils.get("bot.report.CWDL"));
        Customer customerFK = customerService.queryCustomer(3, language);
        if (customerFK != null) {
            String url = dealCustomer(customerFK);
            customerButton.setUrl(url); // 设置按钮的回调数据
            two.add(customerButton);
        }
        if (!two.isEmpty()) {
            keyboard.add(two); // 可以添加更多按钮，形成多列
        }
    }

    private void doAttachButton(List<AttachButton> attachButtons, List<List<InlineKeyboardButton>> keyboard) {
        int size = attachButtons.size();
        // 计算可以组成完整对的数量
        int pairs = size / 2;

        // 处理完整的对
        for (int i = 0; i < pairs; i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(InlineKeyboardButton.builder()
                    .text(attachButtons.get(i * 2).getName())
                    .url(attachButtons.get(i * 2).getUrl())
                    .build());
            row.add(InlineKeyboardButton.builder()
                    .text(attachButtons.get(i * 2 + 1).getName())
                    .url(attachButtons.get(i * 2 + 1).getUrl())
                    .build());
            keyboard.add(row);
        }

        // 如果还有最后一个按钮（奇数个按钮的情况）
        if (size % 2 != 0) {
            List<InlineKeyboardButton> lastRow = new ArrayList<>();
            lastRow.add(InlineKeyboardButton.builder()
                    .text(attachButtons.get(size - 1).getName())
                    .url(attachButtons.get(size - 1).getUrl())
                    .build());
            keyboard.add(lastRow);
        }
    }

    private static String dealCustomer(Customer customerCW) {
        String url = "";
        if (customerCW.getLinkType() == 1) {
            // WhatsApp类型：显示手机号，可复制
            url = customerCW.getUsername().contains("https") ? customerCW.getUsername() : TelegramUtil.whatsapp + customerCW.getUsername();

        } else if (customerCW.getLinkType() == 0) {

            url = customerCW.getUsername().contains("https") ? customerCW.getUsername() : TelegramUtil.tgAdmin + customerCW.getUsername();
        } else {
            // 外链类型：显示链接，点击打开
            url = customerCW.getUsername();
        }
        return url;
    }

    public String desensitizeWithLastChar(TUser user) {
        String originalStr = user.getUserTgName();
        String userId = user.getUserId() + "";

        Boolean reportTM = configAmountService.reportTM();
        if (reportTM) {
            if (userId.length() <= 2) {
                return "**";
            }
            int mid = userId.length() / 2;
            return StrUtil.repeat('*', mid - 1)
                    + userId.substring(mid - 1, mid + 1)
                    + StrUtil.repeat('*', userId.length() - (mid + 1));
        }
        if (originalStr == null) {
            return null; // 如果原始字符串为null，直接返回null
        }

        // 计算需要脱敏的星号数量，最多脱敏到倒数第二个字符之前
        int maskLength = Math.max(0, originalStr.length() - 2); // 保留最后两个字符

        // 使用StringBuilder构造脱敏后的字符串
        StringBuilder maskedStrBuilder = new StringBuilder();

        // 添加星号
        for (int i = 0; i < maskLength; i++) {
            maskedStrBuilder.append("**");
        }
        if (maskLength == 0) {
            maskedStrBuilder.append("**");
        }

        // 追加最后两个字符
        if (originalStr.length() > 2) {
            maskedStrBuilder.append(originalStr.substring(originalStr.length() - 2));
        } else {
            maskedStrBuilder.append(originalStr);
        }

        return maskedStrBuilder.toString();
    }

    @SneakyThrows
    public Message sendMedia(List<File> files, String content, String chatIdStr, List<List<InlineKeyboardButton>> keyboard) {
        Message message = new Message();
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setChatId(chatIdStr);
        List<InputMedia> inputMedias = CollUtil.newArrayList();
        //多个文件变成媒体集
        if (files.size() > 1) {
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                if (FiletypeUtil.isPic(file)) {
                    InputMediaPhoto inputMedia = new InputMediaPhoto();
                    inputMedia.setMedia(file, file.getName());
                    if (i == 0) {
                        inputMedia.setCaption(content);
                        inputMedia.setParseMode(ParseMode.HTML);
                    }
                    inputMedias.add(inputMedia);
                } else if (FiletypeUtil.isVideo(file)) {
                    InputMediaVideo inputMedia = new InputMediaVideo();
                    inputMedia.setMedia(file, file.getName());
                    if (i == 0) {
                        inputMedia.setCaption(content);
                        inputMedia.setParseMode(ParseMode.HTML);
                    }
                    inputMedias.add(inputMedia);
                } else if (FiletypeUtil.isAudio(file)) {
                    InputMediaAudio inputMedia = new InputMediaAudio();
                    inputMedia.setMedia(file, file.getName());
                    if (i == 0) {
                        inputMedia.setCaption(content);
                        inputMedia.setParseMode(ParseMode.HTML);
                    }
                    inputMedias.add(inputMedia);
                }
            }

            sendMediaGroup.setMedias(inputMedias);
            List<Message> messages = execute(sendMediaGroup);
            if (CollUtil.isNotEmpty(messages) && messages.size() > 0) {
                message = messages.get(0);
            }
        } else if (files.size() == 1) {
            //单个文件就是
            File file = files.get(0);
            if (FiletypeUtil.isPic(file)) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(chatIdStr);
                sendPhoto.setPhoto(new InputFile(file));
                sendPhoto.setCaption(content);
                sendPhoto.setParseMode(ParseMode.HTML);
                //按钮处理
                if (keyboard != null && !keyboard.isEmpty()) {
                    InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
                    replyMarkup.setKeyboard(keyboard);
                    sendPhoto.setReplyMarkup(replyMarkup);
                }
                message = execute(sendPhoto);
            } else if (FiletypeUtil.isVideo(file)) {
                SendVideo sendVideo = new SendVideo();
                sendVideo.setChatId(chatIdStr);
                sendVideo.setVideo(new InputFile(file));
                sendVideo.setCaption(content);
                sendVideo.setParseMode(ParseMode.HTML);
                //按钮处理
                if (keyboard != null && !keyboard.isEmpty()) {
                    InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
                    replyMarkup.setKeyboard(keyboard);
                    sendVideo.setReplyMarkup(replyMarkup);
                }
                message = execute(sendVideo);
            } else if (FiletypeUtil.isAudio(file)) {
                SendAudio sendAudio = new SendAudio();
                sendAudio.setChatId(chatIdStr);
                sendAudio.setAudio(new InputFile(file));
                sendAudio.setCaption(content);
                sendAudio.setParseMode(ParseMode.HTML);
                //按钮处理
                if (keyboard != null && !keyboard.isEmpty()) {
                    InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
                    replyMarkup.setKeyboard(keyboard);
                    sendAudio.setReplyMarkup(replyMarkup);
                }
                message = execute(sendAudio);
            }
        } else {
            SendMessage sendDocument = new SendMessage();
            sendDocument.setChatId(chatIdStr);
            sendDocument.setText(content);
            sendDocument.setParseMode(ParseMode.HTML);
            //按钮处理
            if (keyboard != null && !keyboard.isEmpty()) {
                InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
                replyMarkup.setKeyboard(keyboard);
                sendDocument.setReplyMarkup(replyMarkup);
            }
            message = execute(sendDocument);

        }
        return message;
    }

}
