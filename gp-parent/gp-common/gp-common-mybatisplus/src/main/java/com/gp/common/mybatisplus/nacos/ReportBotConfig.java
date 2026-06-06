package com.gp.common.mybatisplus.nacos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportBotConfig {
    private String botUsername;
    private String botToken;
    private String chatId;


}
