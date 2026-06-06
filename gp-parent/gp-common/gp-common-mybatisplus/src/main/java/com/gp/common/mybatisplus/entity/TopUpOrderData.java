package com.gp.common.mybatisplus.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 充值订单数据
 */
@Data
@Builder
public class TopUpOrderData {

    @ApiModelProperty("订单id")
    private Long orderId;

    /** 用户id */
    @ApiModelProperty("用户id")
    private Long userId;

    /** 金额 */
    @ApiModelProperty("金额")
    private BigDecimal amount;


    @ApiModelProperty("支付时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    @ApiModelProperty("订单类型 1:用户自动充值;2:手动上分")
    private Integer orderType;


}

