package com.gp.common.mybatisplus.mq;

import com.gp.common.mybatisplus.entity.OrderTerm;
import com.gp.common.mybatisplus.entity.TUser;
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
public class OtherDealOrder implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("订单信息")
    private OrderTerm orderTerm;

    @ApiModelProperty("user信息")
    private TUser user;
    @ApiModelProperty("打码量")
    private BigDecimal codeAmount;


    @ApiModelProperty("发送消息时间")
    private Date sendDateTime;
    @ApiModelProperty("产品id")
    private String productId;
}
