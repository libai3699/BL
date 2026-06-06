package com.gp.common.mybatisplus.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 仅修改用户商户标签（t_user_merchant_tag 每用户一行存逗号串；并刷新用户主表 update_time）
 */
@Data
public class UserMerchantTagCodesUpdateParam {

    @ApiModelProperty("用户id")
    @NotNull(message = "用户id不能为空")
    private Long userId;

    /**
     * 逗号分隔；不传、null 或空字符串表示清空数据库中的标签列
     */
    @ApiModelProperty("商户标签，逗号分隔；清空可不传或传空字符串")
    private String merchantTagCodes;
}
