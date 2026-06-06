package com.gp.common.mybatisplus.configuration;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.alibaba.fastjson2.JSONObject;
import com.gp.common.base.exception.MyPayException;
import com.gp.common.base.utils.FiletypeUtil;
import com.gp.common.mybatisplus.service.BotItemService;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
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
public class RiskTelegramUtil extends TelegramLongPollingBot {
    private final String botUsername;
    private final String botToken;
    private final String chatId;
    @Resource
    public BotItemService botItemService;


    public RiskTelegramUtil(String botUsername, String botToken, String chatId ) {
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
        StringBuilder stringBuilder = new StringBuilder();
        if(e instanceof MyPayException){
            MyPayException   payException  =  (MyPayException)e;
            String msg = payException.getMsg();
            String msgReal = payException.getMsgReal();
            stringBuilder.append(msg).append("----").append(msgReal);
        }
        //发送到飞机,特殊关键的一样
        String requestURI = request.getRequestURI();
//        String param = MDC.get("params");
        String traceId = MDC.get("traceId");
        e.printStackTrace();
        log.error("请求地址'{}',发生系统异常: {}", requestURI, e.getMessage());

        StringBuilder sb = stringBuilder
                .append("------------------------------异常信息------------------------------").append("\n")
                .append("创建时间: ").append(DateUtil.formatDateTime(new Date())).append("\n")
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
    public void dealErrorMsg(String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append(msg);
        CompletableFuture.runAsync(() -> this.sendMessage(sb.toString()));
    }
}
