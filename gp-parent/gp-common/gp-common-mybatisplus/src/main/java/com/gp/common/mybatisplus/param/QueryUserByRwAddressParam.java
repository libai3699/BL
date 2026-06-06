package com.gp.common.mybatisplus.param;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 按冲提地址反查用户（独立弹窗接口，不参与用户列表条件）。
 */
@Data
public class QueryUserByRwAddressParam implements Serializable {

    @ApiModelProperty(value = "充值/提现侧链上或账户地址，精确匹配", required = true)
    @JsonAlias({"address", "rw_address"})
    private String rwAddress;
}
