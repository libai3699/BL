package com.gp.common.mybatisplus.pay.mpay;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Base {
    private static final long serialVersionUID = 1L;


    @ApiModelProperty("状态码")
    private Integer code;
    @ApiModelProperty("返回内容")
    private String msg;
}
