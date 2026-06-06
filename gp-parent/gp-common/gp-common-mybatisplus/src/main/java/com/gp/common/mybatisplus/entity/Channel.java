package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.common.base.excel.annotation.Excel;
import com.gp.common.mybatisplus.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 渠道对象 t_channel
 *
 * @author axing
 * @date 2025-06-03
 */
@ApiModel("渠道")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_channel")
public class Channel extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:Channel";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id", cellType = Excel.ColumnType.NUMERIC)
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id", cellType = Excel.ColumnType.NUMERIC)
    private Long userId;

    /**
     * 飞机id
     */
    @ApiModelProperty("飞机id")
    @TableField("user_tg_id")
    @Excel(name = "飞机id", cellType = Excel.ColumnType.NUMERIC)
    private Long userTgId;

    /**
     * 飞机名称
     */
    @ApiModelProperty("飞机名称")
    @TableField("user_tg_name")
    @Excel(name = "飞机名称")
    private String userTgName;

    /**
     * 飞机用户名
     */
    @ApiModelProperty("飞机用户名")
    @TableField("user_tg_username")
    @Excel(name = "飞机用户名")
    private String userTgUsername;

    /**
     * 股东id
     */
    @ApiModelProperty("股东id")
    @TableField(value = "shareholder_id", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "股东id")
    private Long shareholderId;

    /**
     * 渠道名称
     */
    @ApiModelProperty("渠道名称")
    @TableField("channel_name")
    @Excel(name = "渠道名称")
    private String channelName;

    /**
     * 渠道编码
     */
    @ApiModelProperty("渠道编码")
    @TableField("channel_code")
    @Excel(name = "渠道编码")
    private String channelCode;

    /**
     * 0 不显示 1显示
     */
    @ApiModelProperty("是否允许分配比例")
    @TableField(value = "is_proportion", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "是否允许分配比例 0不允许 1允许")
    private Integer isProportion;
    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    @Excel(name = "更新时间")
    private Date updateTime;
    @ApiModelProperty("更新时间数组")
    @TableField(exist = false)
    private String[] updateTimes;

    /**
     * 修改人
     */
    @ApiModelProperty("修改人")
    @TableField("update_by")
    @Excel(name = "修改人")
    private String updateBy;

    /**
     * 0手动结算1自动结算
     */
    @ApiModelProperty("0手动结算1自动结算")
    @TableField(value = "is_auto", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "0全部手动结算 1全部自动结算 2 渠道工资手动结算 其他自动结算")
    private Integer isAuto;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField("remark")
    @Excel(name = "备注")
    private String remark;

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
     * 创建人
     */
    @ApiModelProperty("创建人")
    @TableField("create_by")
    @Excel(name = "创建人")
    private String createBy;

    /**
     * 渠道新会员赠送彩金金额
     */
    @ApiModelProperty("渠道新会员赠送彩金金额")
    @TableField("giveaway_amount")
    @Excel(name = "渠道新会员赠送彩金金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal giveawayAmount;

    /**
     * 打码量倍数
     */
    @ApiModelProperty("打码量倍数")
    @TableField("coding_volume_multiple")
    @Excel(name = "打码量倍数", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal codingVolumeMultiple;

    /**
     * 发送消息的语言
     */
    @ApiModelProperty("发送消息的语言")
    @TableField("lan_key")
    @Excel(name = "发送消息的语言")
    private String lanKey;

    /**
     * 落地页url
     */
    @ApiModelProperty("落地页url")
    @TableField(exist = false)
    private String landingPageUrl;

    /**
     * 网页推广url
     */
    @ApiModelProperty("网页推广")
    @TableField(exist = false)
    private String promotionPageUrl;

    /**
     * 飞投推广url
     */
    @ApiModelProperty("飞投机器人推广url")
    @TableField(exist = false)
    private String ftAddressUrl;

    /**
     * 飞机APP推广url
     */
    @ApiModelProperty("飞机APP推广url")
    @TableField(exist = false)
    private String appPromotionUrl;

    /**
     * 股东IDList
     */
    @ApiModelProperty("股东IDList")
    @TableField(exist = false)
    private List<Long> shareholderIdList;
}
