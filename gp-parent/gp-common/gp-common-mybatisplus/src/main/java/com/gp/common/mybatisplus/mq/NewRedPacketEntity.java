package com.gp.common.mybatisplus.mq;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author scent
 */
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewRedPacketEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("tg用户Id")
    private Long tgUserId;

    @ApiModelProperty("发送消息时间")
    private Date sendDateTime;
    @ApiModelProperty("产品id")
    private String productId;
    @ApiModelProperty("lankey")
    private String lankey;
}
