package com.gp.common.mybatisplus.mq;

import com.gp.common.base.utils.DateUtils;
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
public class RechargeEntity implements Serializable {

    private static final long serialVersionUID = 1L;



    @ApiModelProperty("来源充值金额")
    private BigDecimal fromAmount;
    @ApiModelProperty("来源人")
    private Long fromUserId;
    @ApiModelProperty("0充值1亏损")
    private Integer type;

    @ApiModelProperty("发送消息时间")
    private Date sendDateTime;
    @ApiModelProperty("产品id")
    private String productId;

}
