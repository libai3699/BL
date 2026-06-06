package com.gp.common.mybatisplus.mq;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 统计校准MQ实体 — 携带差值，直接加减 t_user_count_order
 */
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalibrationStatsEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户Id")
    private Long userId;

    @ApiModelProperty("渠道id")
    private Long channelId;

    @ApiModelProperty("币种")
    private Integer currencyId;

    @ApiModelProperty("日期 yyyy-MM-dd")
    private String dayStr;

    @ApiModelProperty("产品编码(数据源)")
    private String productId;

    @ApiModelProperty("关联订单号(可选，用于审计)")
    private String orderNo;

    @ApiModelProperty("差值-有效投注额")
    private BigDecimal deltaBetAmount;

    @ApiModelProperty("差值-结算额(派彩)")
    private BigDecimal deltaSettleAmount;

    @ApiModelProperty("差值-输赢")
    private BigDecimal deltaWinLoss;

    @ApiModelProperty("差值-打码量")
    private BigDecimal deltaCodeAmount;

    @ApiModelProperty("游戏类型编码(1-9)")
    private String gameTypeCode;

    @ApiModelProperty("校准原因")
    private String reason;

    @ApiModelProperty("发送时间")
    private Date sendDateTime;
}
