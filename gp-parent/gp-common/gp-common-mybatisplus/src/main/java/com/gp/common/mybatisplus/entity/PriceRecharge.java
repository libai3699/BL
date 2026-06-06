package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.common.mybatisplus.base.BaseEntity;
import com.gp.common.base.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 充值列对象 t_price_recharge
 *
 * @author axing
 * @date 2024-05-14
 */
@ApiModel("充值列")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_price_recharge")
public class PriceRecharge extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:PriceRecharge";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Long id;

    /**
     * 价格
     */
    @ApiModelProperty("价格")
    @TableField("price")
    @Excel(name = "价格")
    private BigDecimal price;

    /**
     * 彩金
     */
    @ApiModelProperty("彩金")
    @TableField("bonus")
    @Excel(name = "彩金")
    private BigDecimal bonus;

    /**
     * 商户表ID
     */
    @ApiModelProperty("商户表ID")
    @TableField("merchant_pay_id")
    private Long merchantPayId;

    /**
     * 是否展示:0不展示;1展示;
     */
    @ApiModelProperty("是否展示:0不展示;1展示;")
    @TableField("is_show")
    @Excel(name = "是否展示:0不展示;1展示;")
    private Integer isShow;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    @TableField("sort")
    @Excel(name = "排序")
    private Integer sort;

    /**
     * 签名时间
     */
    @ApiModelProperty("签名时间")
    @TableField("sign_time")
    @Excel(name = "签名时间")
    private Long signTime;

    /**
     * 签名
     */
    @ApiModelProperty("签名")
    @TableField("sign")
    @Excel(name = "签名")
    private String sign;

    /**
     * 创建人
     */
    @ApiModelProperty("创建人")
    @TableField("create_by")
    @Excel(name = "创建人")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "创建时间")
    private Date createTime;
    @ApiModelProperty("创建时间数组")
    @TableField(exist = false)
    private String[] createTimes;

    /**
     * 删除标志(0删除  1存在)
     */
    @ApiModelProperty("删除标志(0删除  1存在)")
    @TableField("has_del")
    @Excel(name = "删除标志(0删除  1存在)")
    private Integer hasDel;

    @ApiModelProperty("签名是否正确")
    @TableField(exist = false)
    private Boolean flag;

    /**
     * 商户表-商户名称
     */
    @ApiModelProperty("商户名称")
    @TableField(exist = false)
    @Excel(name = "商户名称")
    private String name;

    /**
     * 商户表-商户头像
     */
    @ApiModelProperty("商户头像")
    @TableField(exist = false)
    private String avatar;

    /**
     * 商户表-与U的汇率
     */
    @ApiModelProperty("与U的汇率")
    @TableField(exist = false)
    private BigDecimal rate;

    /**
     * 商户表-币种
     */
    @ApiModelProperty("币种")
    @TableField(exist = false)
    private String currency;
}
