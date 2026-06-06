package com.common.core.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Administrator
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailSendDto {
    /**
     * 收件人
     */
    private String to;
    /**
     * 主题
     */
    private String subject;

    /**
     * 内容
     */
    private String content;

}
