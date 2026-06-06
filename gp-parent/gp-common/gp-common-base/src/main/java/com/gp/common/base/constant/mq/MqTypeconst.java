package com.gp.common.base.constant.mq;

import java.io.Serializable;

/**
 * @author axing
 * @version 1.0
 * @date 2023/11/16/016 11:51
 */
public class MqTypeconst implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 发送消息机器人钱包 直接发送的
     */
    public static final String walletBotSendMessage = "walletBotSendMessage";
    /**
     * 发送消息机器人钱包 文本编辑的
     */
    public static final String walletBotEditSendText = "walletBotEditSendText";
    /**
     * 图片的
     */
    public static final String walletBotPhotoSendText = "walletBotPhotoSendText";
}
