package com.gp.common.mybatisplus.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.common.mybatisplus.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import com.gp.common.base.excel.annotation.Excel;

/**
 * 用户下级默认对象 t_user_default_channel
 *
 * @author axing
 * @date 2025-10-16
 */
@ApiModel("用户下级默认")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_default_channel")
public class UserDefaultChannel extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:UserDefaultChannel";

    /** 下级默认渠道配置id */
    @ApiModelProperty("下级默认渠道配置id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "下级默认渠道配置id")
     private Long id;

    /** 用户id */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /** 上级用户id */
    @ApiModelProperty("上级用户id")
    @TableField("pid")
    @Excel(name = "上级用户id")
    private Long pid;

    /** 上级用户id列表 */
    @ApiModelProperty("上级用户id列表")
    @TableField("p_path")
    @Excel(name = "上级用户id列表")
    private String pPath;

    /** 上级返佣(电子) */
    @ApiModelProperty("上级返佣(电子)")
    @TableField("super_user_rebate_1")
    @Excel(name = "上级返佣(电子)")
    private BigDecimal superUserRebate1;

    /** 上级返佣(体育) */
    @ApiModelProperty("上级返佣(体育)")
    @TableField("super_user_rebate_2")
    @Excel(name = "上级返佣(体育)")
    private BigDecimal superUserRebate2;

    /** 上级返佣(视讯) */
    @ApiModelProperty("上级返佣(视讯)")
    @TableField("super_user_rebate_3")
    @Excel(name = "上级返佣(视讯)")
    private BigDecimal superUserRebate3;

    /** 上级返佣(彩票) */
    @ApiModelProperty("上级返佣(彩票)")
    @TableField("super_user_rebate_4")
    @Excel(name = "上级返佣(彩票)")
    private BigDecimal superUserRebate4;

    /** 上级返佣(棋牌) */
    @ApiModelProperty("上级返佣(棋牌)")
    @TableField("super_user_rebate_5")
    @Excel(name = "上级返佣(棋牌)")
    private BigDecimal superUserRebate5;

    /** 上级返佣(区块链) */
    @ApiModelProperty("上级返佣(区块链)")
    @TableField("super_user_rebate_6")
    @Excel(name = "上级返佣(区块链)")
    private BigDecimal superUserRebate6;

    /** 上级返佣(捕鱼) */
    @ApiModelProperty("上级返佣(捕鱼)")
    @TableField("super_user_rebate_7")
    @Excel(name = "上级返佣(捕鱼)")
    private BigDecimal superUserRebate7;

    /** 上级返佣(宾果) */
    @ApiModelProperty("上级返佣(宾果)")
    @TableField("super_user_rebate_8")
    @Excel(name = "上级返佣(宾果)")
    private BigDecimal superUserRebate8;

    /** 上级返佣(弹珠) */
    @ApiModelProperty("上级返佣(弹珠)")
    @TableField("super_user_rebate_9")
    @Excel(name = "上级返佣(弹珠)")
    private BigDecimal superUserRebate9;

    /** 分红开关(0 关闭, 1 开启) */
    @ApiModelProperty("分红开关(0 关闭, 1 开启)")
    @TableField("dividend_status")
    @Excel(name = "分红开关(0 关闭, 1 开启)")
    private Byte dividendStatus;

    /** 分红比例 */
    @ApiModelProperty("分红比例")
    @TableField("dividend_rebate")
    @Excel(name = "分红比例")
    private BigDecimal dividendRebate;

    /** 创建人 */
    @ApiModelProperty("创建人")
    @TableField("create_by")
    @Excel(name = "创建人")
    private String createBy;

    /** 创建时间 */
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "创建时间")
    private Date createTime;
    @ApiModelProperty("创建时间数组")
    @TableField(exist = false)
    private String[] createTimes;

    /** 修改人 */
    @ApiModelProperty("修改人")
    @TableField("update_by")
    @Excel(name = "修改人")
    private String updateBy;

    /** 修改时间 */
    @ApiModelProperty("修改时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    @Excel(name = "修改时间")
    private Date updateTime;
    @ApiModelProperty("修改时间数组")
    @TableField(exist = false)
    private String[] updateTimes;


}
