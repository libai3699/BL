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
public class OrderEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("订单号")
    private String orderNo;

    @ApiModelProperty("用户Id")
    private Long userId;

    @ApiModelProperty("渠道id,没有则为0")
    private Long channelId = 0L;

    @ApiModelProperty("币种")
    private Integer currencyId = 6;

    @ApiModelProperty("金额")
    private BigDecimal money =BigDecimal.ZERO;

    @ApiModelProperty("彩金金额")
    private BigDecimal bonusAmount=BigDecimal.ZERO;

    @ApiModelProperty("奖励转盘次数")
    private Integer wheelNum = 0;
    @ApiModelProperty("创建时间")
    private Date creatTime = new Date();

    @ApiModelProperty("事件类型: " +
            "  1,用户自动充值" +
            "  2,后台手动上分 " +
            "  3,用户自动提现," +
            "  4.后台手动下分(人工下分)\n" +
            "  5.用户下注\n" +
            "  6.用户下注结算\n" +
            "  7.用户注册\n" +
            "  8.用户领取返水\n" +
            "  9.用户领取返佣\n" +
            "  10.用户活动奖励相关\n" +
            "  11.转盘活动")
    private String type;

    @ApiModelProperty("当前时间 ")
    private String nowDay = DateUtils.getDate();

    /** 新增用户 */
    @ApiModelProperty("新增用户")
    private Integer userNum;

    @ApiModelProperty("日志id")
    private String uuid;

    @ApiModelProperty("发送消息时间")
    private Date sendDateTime;
    @ApiModelProperty("产品id")
    private String productId;

}
