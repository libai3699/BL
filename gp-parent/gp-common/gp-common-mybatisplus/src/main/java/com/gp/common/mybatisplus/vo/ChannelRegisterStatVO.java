package com.gp.common.mybatisplus.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "代理排行渠道")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelRegisterStatVO {
    private Long channelId;
    /** 该渠道当日注册人数 */
    private Long registerNum;
}