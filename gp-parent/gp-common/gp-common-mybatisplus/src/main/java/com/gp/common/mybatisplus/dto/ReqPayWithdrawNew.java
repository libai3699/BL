package com.gp.common.mybatisplus.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReqPayWithdrawNew {
	@ApiModelProperty( value = "支付类型编码")
	private String    payTypeCode;
	@ApiModelProperty( value = "提现金额" )
	private BigDecimal money = BigDecimal.ZERO;

    @ApiModelProperty("支付密码(6位数字)")
    private String payPassword;

    @ApiModelProperty("USDT地址")
    private String address;


    @ApiModelProperty( hidden = true )
	private String     orderNo;

    @ApiModelProperty( hidden = true )
	private Integer     blockId;

    @ApiModelProperty( hidden = true )
	private String blockAddr;

	@ApiModelProperty( hidden = true )
	private String     realIp;
	// 失败原因
	@ApiModelProperty( hidden = true )
	private String     failReason;
	@ApiModelProperty( hidden = true )
	private BigDecimal successRate;
	@ApiModelProperty( hidden = true )
	private String name;

}
