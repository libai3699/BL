package com.gp.common.mybatisplus.mq;

import com.gp.common.base.utils.DateUtils;
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
public class TermEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("订单号")
    private String orderNo;



    @ApiModelProperty("用户")
    private TUser tUser;


    @ApiModelProperty("金额")
    private BigDecimal money;


    @ApiModelProperty("类型 0 下注 1 结算 2 取消")
    private Integer type;

    @ApiModelProperty("日志id")
    private String uuid;

    @ApiModelProperty("发送消息时间")
    private Date sendDateTime;


}
