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
public class HelpMoneyEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty("用户Id")
    private Long userId;
    @ApiModelProperty("tg用户Id")
    private Long tgUserId;

    @ApiModelProperty("日期")
    private String dayStr;

    @ApiModelProperty("游戏类型")
    private String gameTypeCode;
    @ApiModelProperty("投注金额")
    private BigDecimal betMoney =BigDecimal.ZERO;

    @ApiModelProperty("中奖金额金额")
    private BigDecimal winMoney=BigDecimal.ZERO;
    @ApiModelProperty("彩金金额")
    private BigDecimal bonusMoney=BigDecimal.ZERO;
    @ApiModelProperty("计算金额 这里不做传递只是为了去计算签名")
    private BigDecimal calMoney=BigDecimal.ZERO;
    @ApiModelProperty("比例 这里不做传递只是为了去计算签名")
    private BigDecimal calRatio=BigDecimal.ZERO;
    @ApiModelProperty("最终领取金额 这里不做传递只是为了去计算签名")
    private BigDecimal receiveMoney=BigDecimal.ZERO;
    @ApiModelProperty("是否领取 这里不做传递只是为了去计算签名")
    private Integer isReceive= 0;
    @ApiModelProperty("产品id")
    private String productId;

    @ApiModelProperty("signTime")
    private Long signTime =System.currentTimeMillis();
    @ApiModelProperty("signSecretKey")
    private String signSecretKey;
    @ApiModelProperty("发送消息时间")
    private Date sendDateTime;
}
