package com.gp.common.base.bot.notify;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author axing
 * @version 1.0
 * @date 2023/11/20/020 16:55
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class NotifyBaseUser extends NotifyBase {

    /**
     * 消息内容
     */
    private String msg;
    /**
     * 聊天框id
     */
    private Long chatId;

    private Object extParam1;
}
