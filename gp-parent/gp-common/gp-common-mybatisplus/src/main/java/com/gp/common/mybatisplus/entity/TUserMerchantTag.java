package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户商户标签扩展表：每个用户一行，{@code merchant_tag_codes} 与前端一致为英文逗号分隔。
 */
@ApiModel(description = "用户商户标签")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_user_merchant_tag")
public class TUserMerchantTag {

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private Long id;

    @TableField("user_id")
    @ApiModelProperty("用户ID")
    private Long userId;

    @TableField("merchant_tag_codes")
    @ApiModelProperty("商户标签，英文逗号分隔")
    private String merchantTagCodes;

    @TableField("create_time")
    private Date createTime;

    @TableField("update_time")
    private Date updateTime;

    @TableField("update_by")
    private String updateBy;
}
