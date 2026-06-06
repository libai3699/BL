package com.gp.common.mybatisplus.mq;

import com.gp.common.mybatisplus.entity.OrderTerm;
import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.param.ChangeExtValueVo;
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
public class RebateDealEntity implements Serializable {
    @ApiModelProperty("内容")
    ChangeExtValueVo changeExtValueVo;
    @ApiModelProperty("发送消息时间")
    private Date sendDateTime;
    @ApiModelProperty("产品id")
    private String productId;

    @ApiModelProperty("订单信息")
    private OrderTerm orderTerm;

    @ApiModelProperty("user信息")
    private TUser user;
    @ApiModelProperty("打码量")
    private BigDecimal codeAmount;



}
