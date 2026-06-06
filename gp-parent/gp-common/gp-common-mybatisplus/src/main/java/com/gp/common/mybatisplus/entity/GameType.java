package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.common.mybatisplus.aspect.Localized;
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
 * 游戏类型对象 t_game_type
 *
 * @author axing
 * @date 2024-05-08
 */
@ApiModel("游戏类型")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_game_type")
public class GameType extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:GameType";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id", cellType = Excel.ColumnType.NUMERIC)
    private Long id;

    /**
     * 游戏编码
     */
    @ApiModelProperty("游戏编码")
    @TableField("type_code")
    @Excel(name = "游戏编码")
    private String typeCode;

    /**
     * 游戏类型名称(中文)
     */
    @ApiModelProperty("游戏类型名称(中文)")
    @TableField("type_name_zh")
    @Excel(name = "游戏类型名称(中文)")
    private String typeNameZh;

    /**
     * 游戏类型名称(英文)
     */
    @ApiModelProperty("游戏类型名称(英文)")
    @TableField("type_name_en")
    @Excel(name = "游戏类型名称(英文)")
    private String typeNameEn;

    /**
     * 游戏类型名称(韩语)
     */
    @ApiModelProperty("游戏类型名称(韩语)")
    @TableField("type_name_ko")
    @Excel(name = "游戏类型名称(韩语)")
    private String typeNameKo;

    /**
     * 游戏类型名称(葡萄牙)
     */
    @ApiModelProperty("游戏类型名称(葡萄牙)")
    @TableField("type_name_pt")
    @Excel(name = "游戏类型名称(葡萄牙)")
    private String typeNamePt;

    /**
     * 游戏类型名称(越南语)
     */
    @ApiModelProperty("游戏类型名称(越南语)")
    @TableField("type_name_vi")
    @Excel(name = "游戏类型名称(越南语)")
    private String typeNameVi;
    /**
     * 游戏类型名称(土耳其语)
     */
    @ApiModelProperty("游戏类型名称(土耳其语)")
    @TableField("type_name_tr")
    @Excel(name = "游戏类型名称(土耳其语)")
    private String typeNameTr;

    /**
     * 游戏类型名称-繁体中文
     */
    @ApiModelProperty("游戏类型名称-繁体中文")
    @TableField("type_name_tw")
    @Excel(name = "游戏类型名称-繁体中文")
    private String typeNameTw;

    /**
     * 游戏类型名称-日语
     */
    @ApiModelProperty("游戏类型名称-日语")
    @TableField("type_name_ja")
    @Excel(name = "游戏类型名称-日语")
    private String typeNameJa;

    /**
     * 游戏类型名称-印度语
     */
    @ApiModelProperty("游戏类型名称-印度语")
    @TableField("type_name_hi")
    @Excel(name = "游戏类型名称-印度语")
    private String typeNameHi;

    /**
     * 游戏类型名称(泰语)
     */
    @ApiModelProperty("游戏类型名称(泰语)")
    @TableField("type_name_th")
    @Excel(name = "游戏类型名称(泰语)")
    private String typeNameTh;

    /**
     * 游戏类型名称(俄语)
     */
    @ApiModelProperty("游戏类型名称(俄语)")
    @TableField("type_name_ru")
    @Excel(name = "游戏类型名称(俄语)")
    private String typeNameRu;


    /**
     * 游戏类型名称(阿拉伯语)
     */
    @ApiModelProperty("游戏类型名称(阿拉伯语)")
    @TableField("type_name_ar")
    @Excel(name = "游戏类型名称(阿拉伯语)")
    private String typeNameAr;

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
     * 上级用户反水(1级)
     */
    @ApiModelProperty("上级用户反水(1级)")
    @TableField(value = "super_user_rebate_v1", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "上级用户反水(VIP1)", cellType = Excel.ColumnType.NUMERIC)
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
     * 状态(0 关闭, 1 开启)
     */
    @ApiModelProperty("状态(0 关闭, 1 开启)")
    @TableField("status")
    @Excel(name = "状态(0 关闭, 1 开启)", readConverterExp = "0=关闭,1=开启")
    private Integer status;

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

    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    @TableField("update_by")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty("签名是否正确")
    @TableField(exist = false)
    private Boolean flag;

    @Localized("typeName")
    @TableField(exist = false)
    public String typeName;

}
