package com.common.core.log;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson2.JSONObject;
import com.gp.common.base.utils.FiletypeUtil;
import com.gp.common.base.utils.IpUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 电报机器人工具bean
 *
 * @author admin
 * @date 2021/08/06
 */
@RefreshScope
@Slf4j
@Component
public class TelegramUtil extends TelegramLongPollingBot {
    @Value("${sendBot.botUsername}")
    private String botUsername;
    @Value("${sendBot.botToken}")
    private String botToken;
    @Value("${sendBot.chatId}")
    private String chatId;
    public static final String tgAdmin = "https://t.me/";
    public static final String whatsapp = "https://wa.me/";
    public static final String agencyPage = "/#/pages/agent/index";
    public static final String indexPage = "/#/pages/home/index";
    public static final String channelIdCommand = "channelId";

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


    @SneakyThrows
    public Message sendMedia(List<File> files, String content, String chatIdStr) {
        if (StringUtils.isNotEmpty(chatId)) {
            chatId = chatIdStr;
        }
        Message message = new Message();
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setChatId(chatId);
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
               message=messages.get(0);
            }
        } else if (files.size() == 1) {
            //单个文件就是
            File file = files.get(0);
            if (FiletypeUtil.isPic(file)) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setChatId(chatId);
                sendPhoto.setPhoto(new InputFile(file));
                sendPhoto.setCaption(content);
                sendPhoto.setParseMode(ParseMode.HTML);
                message =execute(sendPhoto);
            } else if (FiletypeUtil.isVideo(file)) {
                SendVideo sendVideo = new SendVideo();
                sendVideo.setChatId(chatId);
                sendVideo.setVideo(new InputFile(file));
                sendVideo.setCaption(content);
                sendVideo.setParseMode(ParseMode.HTML);
                message =execute(sendVideo);
            } else if (FiletypeUtil.isAudio(file)) {
                SendAudio sendAudio = new SendAudio();
                sendAudio.setChatId(chatId);
                sendAudio.setAudio(new InputFile(file));
                sendAudio.setCaption(content);
                sendAudio.setParseMode(ParseMode.HTML);
                 message = execute(sendAudio);

            }
        } else {
            SendMessage sendDocument = new SendMessage();
            sendDocument.setChatId(chatId);
            sendDocument.setText(content);
            sendDocument.setParseMode(ParseMode.HTML);
            message= execute(sendDocument);



        }
       return message;
    }

    public void sendMessage(String tex) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(tex);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("消费失败", e);
        }
    }


    public void sendMessage(SendMessage message) {
        if (message.getChatId() == null) {
            message.setChatId(chatId);
        }
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("消费失败", e);
        }
    }

    /**
     * 发送照片 不能发动图
     */
    public void sendPhoto(String path) {
        SendPhoto message = new SendPhoto();
        message.setChatId(chatId);
        message.setPhoto(new InputFile(new File(path)));
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error("发送照片失败", e);
        }
    }


    /**
     * 发送照片 不能发动图
     */
    public void sendPhoto2(SendPhoto sendPhoto) {
        sendPhoto.setChatId(chatId);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            log.error("发送照片失败", e);
        }
    }


    /**
     * 发送视频
     *
     * @param path 视频路径
     */
    public void sendVideo(String path) {
        SendVideo message = new SendVideo();
        message.setChatId(chatId);
        message.setVideo(new InputFile(new File(path)));
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("发送视频失败", e);
        }
    }

    /**
     * 发送文档 文件 动图
     *
     * @param path 文档路径
     */
    public void sendDocument(String path) {
        SendDocument message = new SendDocument();
        message.setChatId(chatId);
        message.setDocument(new InputFile(new File(path)));
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("发送文档", e);
        }
    }

    public void sendByChatId(String tex, String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(tex);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("操作异常", e);
        }
    }


    public void dealMsg(Exception e, HttpServletRequest request) {
        //发送到飞机,特殊关键的一样
        String requestURI = request.getRequestURI();
        String param = MDC.get("params");
        String traceId = MDC.get("traceId");
        e.printStackTrace();
        log.error("请求地址'{}',发生系统异常: {}", requestURI, e.getMessage());
        StringBuilder stringBuilder = new StringBuilder();
        String product = ServletUtil.getHeader(request, "product", Charset.defaultCharset());
        StringBuilder sb = stringBuilder
                .append("------------------------------异常信息------------------------------").append("\n")
                .append("创建时间: ").append(DateUtil.formatDateTime(new Date())).append("\n")
                .append("产品编码: ").append(product).append("\n")
                .append("请求地址: ").append(request.getRequestURL()).append("\n");
        StackTraceElement[] stacks = e.getStackTrace();
        if (stacks != null && stacks.length > 0) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            sb
                    .append("class: ").append(stacks[0].getClassName()).append("\n")
                    .append("method: ").append(stacks[0].getMethodName()).append("\n")
                    .append("line: ").append(stacks[0].getLineNumber()).append("\n")
//                    .append("param: ").append(param).append("\n")
                    .append("traceId: ").append(traceId).append("\n")
                    .append("message: ").append(e.toString()).append("\n")
                    .append("headers: ").append(JSONObject.toJSONString(ServletUtil.getHeaderMap(request))).append("\n");
            log.error("异常信息-stacktrace:{}", sw);
        }

        CompletableFuture.runAsync(() -> this.sendMessage(sb.toString()));
    }


    public void dealUrlErrorMsg(String message, String plateNameZh, String gameNameZh, HttpServletRequest request) {
        //发送到飞机,特殊关键的一样
        StringBuilder stringBuilder = new StringBuilder();
        String product = ServletUtil.getHeader(request, "product", Charset.defaultCharset());
        StringBuilder sb = stringBuilder
                .append("------------------------------游戏链接异常信息------------------------------").append("\n")
                .append("创建时间: ").append(DateUtil.formatDateTime(new Date())).append("\n")
                .append("产品编码: ").append(product).append("\n")
                .append("请求地址: ").append(request.getRequestURL()).append("\n")
                .append("游戏厂商: ").append(plateNameZh).append("\n")
                .append("游戏名称: ").append(gameNameZh).append("\n")
                .append("异常信息: ").append(message).append("\n");
        CompletableFuture.runAsync(() -> this.sendMessage(sb.toString()));
    }
    public void dealWarnMsg(String sb) {
        //发送到飞机,特殊关键的一样


        CompletableFuture.runAsync(() -> this.sendMessage(sb.toString()));
    }
    public void dealG(String text) {
        //发送到飞机,特殊关键的一样
        String realIpAddr = IpUtil.getRealIpAddr();
        StringBuilder stringBuilder = new StringBuilder();
        String sb = stringBuilder
                .append("-----各单位注意这个人是挂----").append("\n")
                .append("----------UserId :-----------").append(text).append("\n")
                .append("----------请求ip :-----------").append(realIpAddr).toString();

        CompletableFuture.runAsync(() -> this.sendMessage(sb));
    }
    public void dealConfigKey(String text) {
        //发送到飞机,特殊关键的一样
        StringBuilder stringBuilder = new StringBuilder();
        String sb = stringBuilder
                .append("---各单位注意配置已被修改---").append("\n")
                .append("----------配置key :-----------").append(text).toString();

        CompletableFuture.runAsync(() -> this.sendMessage(sb));
    }
}
