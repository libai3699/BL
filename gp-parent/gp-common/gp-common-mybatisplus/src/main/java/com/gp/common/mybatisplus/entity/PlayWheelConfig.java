package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * 转盘游戏配置对象 t_play_wheel_config
 *
 * @author axing
 * @date 2024-05-31
 */
@ApiModel("转盘游戏配置")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_play_wheel_config")
public class PlayWheelConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:PlayWheelConfig";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Integer id;

    /**
     * 奖池编码
     */
    @ApiModelProperty("奖池编码")
    @TableField("prize_pool_code")
    @Excel(name = "奖池编码")
    private String prizePoolCode;

    /**
     * 编号
     */
    @ApiModelProperty("编号")
    @TableField("no")
    @Excel(name = "编号")
    private Integer no;

    /**
     * 提示语(中文)
     */
    @ApiModelProperty("提示语(中文)")
    @TableField("reminder_zh")
    @Excel(name = "提示语(中文)")
    private String reminderZh;

    /**
     * 提示语(英文)
     */
    @ApiModelProperty("提示语(英文)")
    @TableField("reminder_en")
    @Excel(name = "提示语(英文)")
    private String reminderEn;

    /**
     * 提示语(韩语)
     */
    @ApiModelProperty("提示语(韩语)")
    @TableField("reminder_ko")
    @Excel(name = "提示语(韩语)")
    private String reminderKo;

    /**
     * 提示语(葡萄牙)
     */
    @ApiModelProperty("提示语(葡萄牙)")
    @TableField("reminder_pt")
    @Excel(name = "提示语(葡萄牙)")
    private String reminderPt;

    /**
     * 提示语(越南语)
     */
    @ApiModelProperty("提示语(越南语)")
    @TableField("reminder_vi")
    @Excel(name = "提示语(越南语)")
    private String reminderVi;

    /**
     * 提示语(土耳其语)
     */
    @ApiModelProperty("提示语(土耳其语)")
    @TableField("reminder_tr")
    @Excel(name = "提示语(土耳其语)")
    private String reminderTr;

    /**
     * 提示语(繁体中文)
     */
    @ApiModelProperty("提示语(繁体中文)")
    @TableField("reminder_tw")
    @Excel(name = "提示语(繁体中文)")
    private String reminderTw;

    /**
     * 提示语(日语)
     */
    @ApiModelProperty("提示语(日语)")
    @TableField("reminder_ja")
    @Excel(name = "提示语(日语)")
    private String reminderJa;

    /**
     * 提示语(印度语)
     */
    @ApiModelProperty("提示语(印度语)")
    @TableField("reminder_hi")
    @Excel(name = "提示语(印度语)")
    private String reminderHi;

    /**
     * 提示语(泰语)
     */
    @ApiModelProperty("提示语(泰语)")
    @TableField("reminder_th")
    @Excel(name = "提示语(泰语)")
    private String reminderTh;

    /**
     * 提示语(俄语)
     */
    @ApiModelProperty("提示语(俄语)")
    @TableField("reminder_ru")
    @Excel(name = "提示语(俄语)")
    private String reminderRu;

    /**
     * 提示语(阿拉伯语)
     */
    @ApiModelProperty("提示语(阿拉伯语)")
    @TableField("reminder_ar")
    @Excel(name = "提示语(阿拉伯语)")
    private String reminderAr;

    /**
     * 奖励类型 0 转盘次数 1 奖金
     */
    @ApiModelProperty("奖励类型 0 转盘次数 1 奖金")
    @TableField("type")
    @Excel(name = "奖励类型 0 转盘次数 1 奖金")
    private Integer type;

    /**
     * 图片
     */
    @ApiModelProperty("图片")
    @TableField("pic")
    @Excel(name = "图片")
    private String pic;

    /**
     * 奖金
     */
    @ApiModelProperty("奖金")
    @TableField("award")
    @Excel(name = "奖金")
    private BigDecimal award;

    /**
     * 状态(0 关闭, 1 开启)
     */
    @ApiModelProperty("状态(0 关闭, 1 开启)")
    @TableField("status")
    @Excel(name = "状态(0 关闭, 1 开启)")
    private Integer status;

    /**
     * 默认权重
     */
    @ApiModelProperty("默认权重")
    @TableField("weight")
    @Excel(name = "默认权重")
    private Integer weight;

    /**
     * 二阶段权重
     */
    @ApiModelProperty("二阶段权重")
    @TableField("weight_second")
    @Excel(name = "二阶段权重")
    private Integer weightSecond;

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
    @ApiModelProperty("提示语(英文)")
    @TableField(exist = false)
    @Localized("reminder")
    private String reminder;

}
