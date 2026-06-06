package com.gp.common.mybatisplus.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@ApiModel("批量查询商户余额参数")
@Data
public class MerchantPayBatchQueryParam {

    /**
     * 商户ID列表
     */
    @ApiModelProperty("商户ID列表")
    @NotNull(message = "商户ID列表不能为空")
    @Size(min = 1, message = "商户ID列表不能为空")
    private List<Long> ids;
}
