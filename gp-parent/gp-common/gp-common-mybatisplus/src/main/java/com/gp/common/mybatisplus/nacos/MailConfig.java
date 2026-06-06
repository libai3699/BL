package com.gp.common.mybatisplus.nacos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailConfig {
    private String emailAppKey;
    private String emailTemplateId;
    private String emailAlias;
}
