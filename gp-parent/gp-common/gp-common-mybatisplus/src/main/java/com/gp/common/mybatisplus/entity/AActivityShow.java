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

import java.util.Date;

/**
 * 活动总揽对象 t_a_activity_show
 *
 * @author axing
 * @date 2024-08-08
 */
@ApiModel("活动总揽")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_a_activity_show")
public class AActivityShow extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:AActivityShow";

    /**
     * 活动id
     */
    @ApiModelProperty("活动id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "活动id")
    private Long id;

    /**
     * 标题-中文
     */
    @ApiModelProperty("标题-中文")
    @TableField("title_zh")
    @Excel(name = "标题-中文")
    private String titleZh;

    /**
     * 标题-英语
     */
    @ApiModelProperty("标题-英语")
    @TableField("title_en")
    @Excel(name = "标题-英语")
    private String titleEn;

    /**
     * 标题-韩语
     */
    @ApiModelProperty("标题-韩语")
    @TableField("title_ko")
    @Excel(name = "标题-韩语")
    private String titleKo;

    /**
     * 标题-葡萄牙
     */
    @ApiModelProperty("标题-葡萄牙")
    @TableField("title_pt")
    @Excel(name = "标题-葡萄牙")
    private String titlePt;

    /**
     * 标题-越南语
     */
    @ApiModelProperty("标题-越南语")
    @TableField("title_vi")
    @Excel(name = "标题-越南语")
    private String titleVi;

    /**
     * 标题-越南语
     */
    @ApiModelProperty("标题-土耳其语")
    @TableField("title_tr")
    @Excel(name = "标题-土耳其语")
    private String titleTr;

    /**
     * 标题-繁体中文
     */
    @ApiModelProperty("标题-繁体中文")
    @TableField("title_tw")
    @Excel(name = "标题-繁体中文")
    private String titleTw;

    /**
     * 标题-日语
     */
    @ApiModelProperty("标题-日语")
    @TableField("title_ja")
    @Excel(name = "标题-日语")
    private String titleJa;

    /**
     * 标题-印度语
     */
    @ApiModelProperty("标题-印度语")
    @TableField("title_hi")
    @Excel(name = "标题-印度语")
    private String titleHi;

    /**
     * 标题(泰语)
     */
    @ApiModelProperty("标题(泰语)")
    @TableField("title_th")
    @Excel(name = "标题(泰语)")
    private String titleTh;

    /**
     * 标题(俄语)
     */
    @ApiModelProperty("标题(俄语)")
    @TableField("title_ru")
    @Excel(name = "标题(俄语)")
    private String titleRu;

    /**
     * 标题(阿拉伯语)
     */
    @ApiModelProperty("标题(阿拉伯语)")
    @TableField("title_ar")
    @Excel(name = "标题(阿拉伯语)")
    private String titleAr;

    /**
     * 状态(0 关闭, 1 开启)
     */
    @ApiModelProperty("状态(0 关闭, 1 开启)")
    @TableField("status")
    @Excel(name = "状态(0 关闭, 1 开启)")
    private Integer status;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    @TableField("sort")
    @Excel(name = "排序")
    private Integer sort;

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

    @TableField(exist = false)
    @Localized("title")
    private String title;
}
