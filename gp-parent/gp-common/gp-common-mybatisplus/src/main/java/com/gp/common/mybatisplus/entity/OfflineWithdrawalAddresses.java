package com.gp.common.mybatisplus.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.common.mybatisplus.aspect.Localized;
import com.gp.common.mybatisplus.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import com.gp.common.base.excel.annotation.Excel;

/**
 * 线下提现相关信息对象 t_offline_withdrawal_addresses
 *
 * @author axing
 * @date 2025-11-13
 */
@ApiModel("线下提现相关信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_offline_withdrawal_addresses")
public class OfflineWithdrawalAddresses extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:OfflineWithdrawalAddresses";

    /** 自增主键 */
    @ApiModelProperty("自增主键")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "自增主键")
     private Integer id;

    /** 店名 */
    @ApiModelProperty("店名")
    @TableField("name")
    @Excel(name = "店名")
    private String name;

    /** 营业时间 9:00-22:00 */
    @ApiModelProperty("营业时间 9:00-22:00")
    @TableField("open_time_section")
    @Excel(name = "营业时间 9:00-22:00")
    private String openTimeSection;

    /** 提现地址 */
    @ApiModelProperty("提现地址")
    @TableField("address")
    @Excel(name = "提现地址")
    private String address;

    /** 国家名称-中文 */
    @ApiModelProperty("国家名称-中文")
    @TableField("country_zh")
    @Excel(name = "国家名称-中文")
    private String countryZh;

    /** 国家名称-英文 */
    @ApiModelProperty("国家名称-英文")
    @TableField("country_en")
    @Excel(name = "国家名称-英文")
    private String countryEn;

    /** 国家名称-韩语 */
    @ApiModelProperty("国家名称-韩语")
    @TableField("country_ko")
    @Excel(name = "国家名称-韩语")
    private String countryKo;

    /** 国家名称-葡萄牙语 */
    @ApiModelProperty("国家名称-葡萄牙语")
    @TableField("country_pt")
    @Excel(name = "国家名称-葡萄牙语")
    private String countryPt;

    /** 国家名称-越南语 */
    @ApiModelProperty("国家名称-越南语")
    @TableField("country_vi")
    @Excel(name = "国家名称-越南语")
    private String countryVi;

    /** 国家名称-土耳其语 */
    @ApiModelProperty("国家名称-土耳其语")
    @TableField("country_tr")
    @Excel(name = "国家名称-土耳其语")
    private String countryTr;

    /** 国家名称-繁体中文 */
    @ApiModelProperty("国家名称-繁体中文")
    @TableField("country_tw")
    @Excel(name = "国家名称-繁体中文")
    private String countryTw;

    /** 国家名称-日语 */
    @ApiModelProperty("国家名称-日语")
    @TableField("country_ja")
    @Excel(name = "国家名称-日语")
    private String countryJa;

    /** 国家名称-印度语 */
    @ApiModelProperty("国家名称-印度语")
    @TableField("country_hi")
    @Excel(name = "国家名称-印度语")
    private String countryHi;

    /** 国家名称-泰语 */
    @ApiModelProperty("国家名称-泰语")
    @TableField("country_th")
    @Excel(name = "国家名称-泰语")
    private String countryTh;

    /** 国家名称-俄语 */
    @ApiModelProperty("国家名称-俄语")
    @TableField("country_ru")
    @Excel(name = "国家名称-俄语")
    private String countryRu;

    /** 国家名称-阿拉伯语 */
    @ApiModelProperty("国家名称-阿拉伯语")
    @TableField("country_ar")
    @Excel(name = "国家名称-阿拉伯语")
    private String countryAr;

    /** 纬度 */
    @ApiModelProperty("纬度")
    @TableField("latitude")
    @Excel(name = "纬度")
    private BigDecimal latitude;

    /** 经度 */
    @ApiModelProperty("经度")
    @TableField("longitude")
    @Excel(name = "经度")
    private BigDecimal longitude;

    /** 电话号码 */
    @ApiModelProperty("电话号码")
    @TableField("phone_number")
    @Excel(name = "电话号码")
    private String phoneNumber;

    /** 最低提现金额 */
    @ApiModelProperty("最低提现金额")
    @TableField("minimum_amount")
    @Excel(name = "最低提现金额")
    private BigDecimal minimumAmount;

    /** 状态 (0: 营业, 1: 停止营业) */
    @ApiModelProperty("状态 (0: 营业, 1: 停止营业)")
    @TableField("status")
    @Excel(name = "状态 (0: 营业, 1: 停止营业)")
    private Integer status;

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

    /** 创建人 */
    @ApiModelProperty("创建人")
    @TableField("create_by")
    @Excel(name = "创建人")
    private String createBy;

    /** 国家名称-俄语 */
    @TableField(exist = false)
    @Localized("country")
    private String country;


}
