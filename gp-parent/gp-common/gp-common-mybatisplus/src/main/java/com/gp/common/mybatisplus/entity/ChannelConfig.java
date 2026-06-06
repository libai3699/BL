package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.*;
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
 * 渠道配置对象 t_channel_config
 *
 * @author axing
 * @date 2025-05-23
 */
@ApiModel("渠道配置")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_channel_config")
public class ChannelConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:ChannelConfig";

    /**
     * 渠道配置id
     */
    @ApiModelProperty("渠道配置id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "渠道配置id")
    private Long id;

    /**
     * 渠道id
     */
    @ApiModelProperty("渠道id")
    @TableField("channel_id")
    @Excel(name = "渠道id")
    private Long channelId;

    /**
     * 渠道绑定用户id
     */
    @ApiModelProperty("渠道绑定用户id")
    @TableField("user_id")
    @Excel(name = "渠道绑定用户id")
    private Long userId;

    /**
     * 游戏类型
     */
    @ApiModelProperty("游戏类型")
    @TableField("game_type")
    @Excel(name = "游戏类型")
    private Integer gameType;
    /**
     * 总分配额度(VIP1)
     */
    @ApiModelProperty("总分配额度(VIP1)")
    @TableField(value = "total_allocation_v1", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "总分配额度(VIP1)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalAllocationV1;

    /**
     * 总分配额度(VIP2)
     */
    @ApiModelProperty("总分配额度(VIP2)")
    @TableField(value = "total_allocation_v2", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "总分配额度(VIP2)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalAllocationV2;

    /**
     * 总分配额度(VIP3)
     */
    @ApiModelProperty("总分配额度(VIP3)")
    @TableField(value = "total_allocation_v3", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "总分配额度(VIP3)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalAllocationV3;

    /**
     * 总分配额度(VIP4)
     */
    @ApiModelProperty("总分配额度(VIP4)")
    @TableField(value = "total_allocation_v4", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "总分配额度(VIP4)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalAllocationV4;

    /**
     * 总分配额度(VIP5)
     */
    @ApiModelProperty("总分配额度(VIP5)")
    @TableField(value = "total_allocation_v5", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "总分配额度(VIP5)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalAllocationV5;

    /** 总分配额度(VIP6) */
    @ApiModelProperty("总分配额度(VIP6)")
    @TableField(value = "total_allocation_v6", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "总分配额度(VIP6)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalAllocationV6;

    /** 总分配额度(VIP7) */
    @ApiModelProperty("总分配额度(VIP7)")
    @TableField(value = "total_allocation_v7", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "总分配额度(VIP7)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalAllocationV7;

    /** 总分配额度(VIP8) */
    @ApiModelProperty("总分配额度(VIP8)")
    @TableField(value = "total_allocation_v8", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "总分配额度(VIP8)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalAllocationV8;

    /** 总分配额度(VIP9) */
    @ApiModelProperty("总分配额度(VIP9)")
    @TableField(value = "total_allocation_v9", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "总分配额度(VIP9)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalAllocationV9;

    /** 总分配额度(VIP10) */
    @ApiModelProperty("总分配额度(VIP10)")
    @TableField(value = "total_allocation_v10", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "总分配额度(VIP10)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalAllocationV10;

    /**
     * 用户反水(1级)
     */
    @ApiModelProperty("用户反水(1级)")
    @TableField(value = "user_rebate_v1", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户反水(VIP1)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal userRebateV1;

    /**
     * 用户反水(2级)
     */
    @ApiModelProperty("用户反水(2级)")
    @TableField(value = "user_rebate_v2", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户反水(VIP2)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal userRebateV2;

    /**
     * 用户反水(3级)
     */
    @ApiModelProperty("用户反水(3级)")
    @TableField(value = "user_rebate_v3", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户反水(VIP3)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal userRebateV3;

    /**
     * 用户反水(4级)
     */
    @ApiModelProperty("用户反水(4级)")
    @TableField(value = "user_rebate_v4", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户反水(VIP4)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal userRebateV4;

    /**
     * 用户反水(5级)
     */
    @ApiModelProperty("用户反水(5级)")
    @TableField(value = "user_rebate_v5", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户反水(VIP5)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal userRebateV5;

    /** 用户反水(6级) */
    @ApiModelProperty("用户反水(6级)")
    @TableField(value = "user_rebate_v6", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户反水(6级)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal userRebateV6;

    /** 用户反水(7级) */
    @ApiModelProperty("用户反水(7级)")
    @TableField(value = "user_rebate_v7", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户反水(7级)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal userRebateV7;

    /** 用户反水(8级) */
    @ApiModelProperty("用户反水(8级)")
    @TableField(value = "user_rebate_v8", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户反水(8级)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal userRebateV8;

    /** 用户反水(9级) */
    @ApiModelProperty("用户反水(9级)")
    @TableField(value = "user_rebate_v9", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户反水(9级)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal userRebateV9;

    /** 用户反水(10级) */
    @ApiModelProperty("用户反水(10级)")
    @TableField(value = "user_rebate_v10", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户反水(10级)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal userRebateV10;

    /**
     * 用户上级外水(1级)
     */
    @ApiModelProperty("用户上级外水(1级)")
    @TableField(value = "super_user_rebate_v1", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户上级外水(VIP1)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal superUserRebateV1;

    /**
     * 用户上级外水(2级)
     */
    @ApiModelProperty("用户上级外水(2级)")
    @TableField(value = "super_user_rebate_v2", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户上级外水(VIP2)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal superUserRebateV2;

    /**
     * 用户上级外水(3级)
     */
    @ApiModelProperty("用户上级外水(3级)")
    @TableField(value = "super_user_rebate_v3", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户上级外水(VIP3)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal superUserRebateV3;

    /**
     * 用户上级外水(4级)
     */
    @ApiModelProperty("用户上级外水(4级)")
    @TableField(value = "super_user_rebate_v4", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户上级外水(VIP4)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal superUserRebateV4;

    /**
     * 用户上级外水(5级)
     */
    @ApiModelProperty("用户上级外水(5级)")
    @TableField(value = "super_user_rebate_v5", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户上级外水(VIP5)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal superUserRebateV5;

    /** 用户上级外水(6级) */
    @ApiModelProperty("用户上级外水(6级)")
    @TableField(value ="super_user_rebate_v6", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户上级外水(6级)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal superUserRebateV6;

    /** 用户上级外水(7级) */
    @ApiModelProperty("用户上级外水(7级)")
    @TableField(value ="super_user_rebate_v7", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户上级外水(7级)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal superUserRebateV7;

    /** 用户上级外水(8级) */
    @ApiModelProperty("用户上级外水(8级)")
    @TableField(value ="super_user_rebate_v8", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户上级外水(8级)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal superUserRebateV8;

    /** 用户上级外水(9级) */
    @ApiModelProperty("用户上级外水(9级)")
    @TableField(value ="super_user_rebate_v9", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户上级外水(9级)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal superUserRebateV9;

    /** 用户上级外水(10级) */
    @ApiModelProperty("用户上级外水(10级)")
    @TableField(value ="super_user_rebate_v10", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "用户上级外水(10级)", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal superUserRebateV10;
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
     * 修改人
     */
    @ApiModelProperty("修改人")
    @TableField("update_by")
    @Excel(name = "修改人")
    private String updateBy;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    @Excel(name = "修改时间")
    private Date updateTime;
    @ApiModelProperty("修改时间数组")
    @TableField(exist = false)
    private String[] updateTimes;

}
