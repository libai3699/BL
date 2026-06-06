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
 * 活动对象 t_activity
 *
 * @author axing
 * @date 2024-08-13
 */
@ApiModel("活动")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_activity")
public class Activity extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:Activity";

    /**
     * 活动id
     */
    @ApiModelProperty("活动id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "活动id", cellType = Excel.ColumnType.NUMERIC)
    private Long id;

    /**
     * 活动类型(1 签到活动, 2 充值活动, 3 打码量活动 4 特殊活动 5 救援金  6转盘活动  )’,
     */
    @ApiModelProperty("活动类型(1 签到活动, 2 充值活动, 3 打码量活动 4 特殊活动 5渠道活动 6 救援金  7转盘活动 8 反水 9返佣 )’,")
    @TableField("type")
    @Excel(name = "活动类型", readConverterExp = "1=签到活动,2=充值活动,3=打码量活动,4=特殊活动,5=渠道活动,6=救援金,7=转盘活动,8=反水,9=返佣 ")
    private Integer type;

    /**
     * 活动编号
     */
    @ApiModelProperty("活动编号")
    @TableField("activity_code")
    @Excel(name = "活动编号")
    private String activityCode;

    /**
     * 活动名称-中文
     */
    @ApiModelProperty("活动名称-中文")
    @TableField("activity_name_zh")
    @Excel(name = "活动名称-中文")
    private String activityNameZh;

    /**
     * 活动名称-英语
     */
    @ApiModelProperty("活动名称-英语")
    @TableField("activity_name_en")
    @Excel(name = "活动名称-英语")
    private String activityNameEn;

    /**
     * 活动名称-韩语
     */
    @ApiModelProperty("活动名称-韩语")
    @TableField("activity_name_ko")
    @Excel(name = "活动名称-韩语")
    private String activityNameKo;

    /**
     * 活动名称-葡萄牙
     */
    @ApiModelProperty("活动名称-葡萄牙")
    @TableField("activity_name_pt")
    @Excel(name = "活动名称-葡萄牙")
    private String activityNamePt;

    /**
     * 活动名称越南语
     */
    @ApiModelProperty("活动名称越南语")
    @TableField("activity_name_vi")
    @Excel(name = "活动名称越南语")
    private String activityNameVi;

    /**
     * 活动名称土耳其语
     */
    @ApiModelProperty("活动名称土耳其语")
    @TableField("activity_name_tr")
    @Excel(name = "活动名称土耳其语")
    private String activityNameTr;

    /**
     * 活动名称-繁体中文
     */
    @ApiModelProperty("活动名称-繁体中文")
    @TableField("activity_name_tw")
    @Excel(name = "活动名称-繁体中文")
    private String activityNameTw;

    /**
     * 活动名称-日语
     */
    @ApiModelProperty("活动名称-日语")
    @TableField("activity_name_ja")
    @Excel(name = "活动名称-日语")
    private String activityNameJa;

    /**
     * 活动名称-印度语
     */
    @ApiModelProperty("活动名称-印度语")
    @TableField("activity_name_hi")
    @Excel(name = "活动名称-印度语")
    private String activityNameHi;

    /**
     * 活动名称(泰语)
     */
    @ApiModelProperty("活动名称(泰语)")
    @TableField("activity_name_th")
    @Excel(name = "活动名称(泰语)")
    private String activityNameTh;

    /**
     * 活动名称(俄语)
     */
    @ApiModelProperty("活动名称(俄语)")
    @TableField("activity_name_ru")
    @Excel(name = "活动名称(俄语)")
    private String activityNameRu;

    /**
     * 活动名称(阿拉伯语)
     */
    @ApiModelProperty("活动名称(阿拉伯语)")
    @TableField("activity_name_ar")
    @Excel(name = "活动名称(阿拉伯语)")
    private String activityNameAr;

    /**
     * 活动封面
     */
    @ApiModelProperty("活动封面")
    @TableField("activity_pic_zh")
    @Excel(name = "活动封面")
    private String activityPicZh;

//    /** 活动封面-黑夜 */
//    @ApiModelProperty("活动封面-黑夜")
//    @TableField("activity_pic_black_zh")
//    @Excel(name = "活动封面-黑夜")
//    private String activityPicBlackZh;

    /**
     * 活动封面
     */
    @ApiModelProperty("活动封面")
    @TableField("activity_pic_en")
    @Excel(name = "活动封面")
    private String activityPicEn;

//    /** 活动封面-黑夜 */
//    @ApiModelProperty("活动封面-黑夜")
//    @TableField("activity_pic_black_en")
//    @Excel(name = "活动封面-黑夜")
//    private String activityPicBlackEn;

    /**
     * 活动封面(韩语)
     */
    @ApiModelProperty("活动封面(韩语)")
    @TableField("activity_pic_ko")
    @Excel(name = "活动封面(韩语)")
    private String activityPicKo;
//
//    /** 活动封面-黑夜(韩语) */
//    @ApiModelProperty("活动封面-黑夜(韩语)")
//    @TableField("activity_pic_black_ko")
//    @Excel(name = "活动封面-黑夜(韩语)")
//    private String activityPicBlackKo;

    /**
     * 活动封面(葡萄牙)
     */
    @ApiModelProperty("活动封面(葡萄牙)")
    @TableField("activity_pic_pt")
    @Excel(name = "活动封面(葡萄牙)")
    private String activityPicPt;

//    /** 活动封面-黑夜(葡萄牙) */
//    @ApiModelProperty("活动封面-黑夜(葡萄牙)")
//    @TableField("activity_pic_black_pt")
//    @Excel(name = "活动封面-黑夜(葡萄牙)")
//    private String activityPicBlackPt;

    /**
     * 活动封面(越南语)
     */
    @ApiModelProperty("活动封面(越南语)")
    @TableField("activity_pic_vi")
    @Excel(name = "活动封面(越南语)")
    private String activityPicVi;
    /**
     * 活动封面(土耳其语)
     */
    @ApiModelProperty("活动封面(土耳其语)")
    @TableField("activity_pic_tr")
    @Excel(name = "活动封面(土耳其语)")
    private String activityPicTr;

    /**
     * 活动封面(繁体中文)
     */
    @ApiModelProperty("活动封面(繁体中文)")
    @TableField("activity_pic_tw")
    @Excel(name = "活动封面(繁体中文)")
    private String activityPicTw;

    /**
     * 活动封面(日语)
     */
    @ApiModelProperty("活动封面(日语)")
    @TableField("activity_pic_ja")
    @Excel(name = "活动封面(日语)")
    private String activityPicJa;

    /**
     * 活动封面(印度语)
     */
    @ApiModelProperty("活动封面(印度语)")
    @TableField("activity_pic_hi")
    @Excel(name = "活动封面(印度语)")
    private String activityPicHi;

    /**
     * 活动封面(泰语)
     */
    @ApiModelProperty("活动封面(泰语)")
    @TableField("activity_pic_th")
    @Excel(name = "活动封面(泰语)")
    private String activityPicTh;

    /**
     * 活动封面(俄语)
     */
    @ApiModelProperty("活动封面(俄语)")
    @TableField("activity_pic_ru")
    @Excel(name = "活动封面(俄语)")
    private String activityPicRu;

    /**
     * 活动封面(阿拉伯语)
     */
    @ApiModelProperty("活动封面(阿拉伯语)")
    @TableField("activity_pic_ar")
    @Excel(name = "活动封面(阿拉伯语)")
    private String activityPicAr;

//    /** 活动封面-黑夜(越南语) */
//    @ApiModelProperty("活动封面-黑夜(越南语)")
//    @TableField("activity_pic_black_vi")
//    @Excel(name = "活动封面-黑夜(越南语)")
//    private String activityPicBlackVi;
//    /** 活动封面-黑夜(土耳其语) */
//    @ApiModelProperty("活动封面-黑夜(土耳其语)")
//    @TableField("activity_pic_black_tr")
//    @Excel(name = "活动封面-黑夜(土耳其语)")
//    private String activityPicBlackTr;

    /**
     * 活动小图
     */
    @ApiModelProperty("活动小图")
    @TableField("activity_small_pic")
    @Excel(name = "活动小图")
    private String activitySmallPic;

    /**
     * 活动简介-中文
     */
    @ApiModelProperty("活动简介-中文")
    @TableField("activity_info_zh")
    @Excel(name = "活动简介-中文")
    private String activityInfoZh;

    /**
     * 活动简介-英文
     */
    @ApiModelProperty("活动简介-英文")
    @TableField("activity_info_en")
    @Excel(name = "活动简介-英文")
    private String activityInfoEn;

    /**
     * 活动简介-韩语
     */
    @ApiModelProperty("活动简介-韩语")
    @TableField("activity_info_ko")
    @Excel(name = "活动简介-韩语")
    private String activityInfoKo;

    /**
     * 活动简介-葡萄牙
     */
    @ApiModelProperty("活动简介-葡萄牙")
    @TableField("activity_info_pt")
    @Excel(name = "活动简介-葡萄牙")
    private String activityInfoPt;

    /**
     * 活动简介-越南语
     */
    @ApiModelProperty("活动简介-越南语")
    @TableField("activity_info_vi")
    @Excel(name = "活动简介-越南语")
    private String activityInfoVi;
    /**
     * 活动简介-土耳其语
     */
    @ApiModelProperty("活动简介-土耳其语")
    @TableField("activity_info_tr")
    @Excel(name = "活动简介-土耳其语")
    private String activityInfoTr;

    /**
     * 活动简介-繁体中文
     */
    @ApiModelProperty("活动简介-繁体中文")
    @TableField("activity_info_tw")
    @Excel(name = "活动简介-繁体中文")
    private String activityInfoTw;

    /**
     * 活动简介-日语
     */
    @ApiModelProperty("活动简介-日语")
    @TableField("activity_info_ja")
    @Excel(name = "活动简介-日语")
    private String activityInfoJa;

    /**
     * 活动简介-印度语
     */
    @ApiModelProperty("活动简介-印度语")
    @TableField("activity_info_hi")
    @Excel(name = "活动简介-印度语")
    private String activityInfoHi;

    /**
     * 活动简介(泰语)
     */
    @ApiModelProperty("活动简介(泰语)")
    @TableField("activity_info_th")
    @Excel(name = "活动简介(泰语)")
    private String activityInfoTh;

    /**
     * 活动简介(俄语)
     */
    @ApiModelProperty("活动简介(俄语)")
    @TableField("activity_info_ru")
    @Excel(name = "活动简介(俄语)")
    private String activityInfoRu;

    /**
     * 活动简介(阿拉伯语)
     */
    @ApiModelProperty("活动简介(阿拉伯语)")
    @TableField("activity_info_ar")
    @Excel(name = "活动简介(阿拉伯语)")
    private String activityInfoAr;

    /**
     * 活动内容-中文
     */
    @ApiModelProperty("活动内容-中文")
    @TableField("activity_content_zh")
    @Excel(name = "活动内容-中文")
    private String activityContentZh;

    /**
     * 活动内容-英文
     */
    @ApiModelProperty("活动内容-英文")
    @TableField("activity_content_en")
    @Excel(name = "活动内容-英文")
    private String activityContentEn;

    /**
     * 活动内容-韩语
     */
    @ApiModelProperty("活动内容-韩语")
    @TableField("activity_content_ko")
    @Excel(name = "活动内容-韩语")
    private String activityContentKo;

    /**
     * 活动内容-葡萄牙
     */
    @ApiModelProperty("活动内容-葡萄牙")
    @TableField("activity_content_pt")
    @Excel(name = "活动内容-葡萄牙")
    private String activityContentPt;

    /**
     * 活动内容-越南语
     */
    @ApiModelProperty("活动内容-越南语")
    @TableField("activity_content_vi")
    @Excel(name = "活动内容-越南语")
    private String activityContentVi;
    /**
     * 活动内容-越南语
     */
    @ApiModelProperty("活动内容-土耳其语")
    @TableField("activity_content_tr")
    @Excel(name = "活动内容-土耳其语")
    private String activityContentTr;

    /**
     * 活动内容-繁体中文
     */
    @ApiModelProperty("活动内容-繁体中文")
    @TableField("activity_content_tw")
    @Excel(name = "活动内容-繁体中文")
    private String activityContentTw;

    /**
     * 活动内容-日语
     */
    @ApiModelProperty("活动内容-日语")
    @TableField("activity_content_ja")
    @Excel(name = "活动内容-日语")
    private String activityContentJa;

    /**
     * 活动内容-印度语
     */
    @ApiModelProperty("活动内容-印度语")
    @TableField("activity_content_hi")
    @Excel(name = "活动内容-印度语")
    private String activityContentHi;

    /**
     * 活动内容(泰语)
     */
    @ApiModelProperty("活动内容(泰语)")
    @TableField("activity_content_th")
    @Excel(name = "活动内容(泰语)")
    private String activityContentTh;

    /**
     * 活动内容(俄语)
     */
    @ApiModelProperty("活动内容(俄语)")
    @TableField("activity_content_ru")
    @Excel(name = "活动内容(俄语)")
    private String activityContentRu;

    /**
     * 活动内容(阿拉伯语)
     */
    @ApiModelProperty("活动内容(阿拉伯语)")
    @TableField("activity_content_ar")
    @Excel(name = "活动内容(阿拉伯语)")
    private String activityContentAr;

    /**
     * 针对的活动类型 0 全部 其他的按照查出来的
     */
    @ApiModelProperty("针对的活动类型 0 全部 其他的按照查出来的")
    @TableField("game_type_code")
    @Excel(name = "针对的活动类型 0 全部 其他的按照查出来的")
    private String gameTypeCode;

    /**
     * 活动展示id
     */
    @ApiModelProperty("活动总览")
    @TableField("activity_show_id")
    @Excel(name = "活动展示id")
    private Long activityShowId;

    /**
     * 活动是否展示 0 不展示 1展示
     */
    @ApiModelProperty("活动是否展示 0 不展示 1展示")
    @TableField("activity_is_show")
    @Excel(name = "活动是否展示 0 不展示 1展示")
    private Integer activityIsShow;

    /**
     * 活动是否首页展示
     */
    @ApiModelProperty("活动是否首页展示")
    @TableField("activity_home_show")
    @Excel(name = "活动是否首页展示")
    private Integer activityHomeShow;

    /**
     * 渠道信息
     */
    @ApiModelProperty("渠道信息(多个以‘,’分隔)")
    @TableField("channel")
    @Excel(name = "渠道信息")
    private String channel;

    /**
     * 状态(0 关闭, 1 开启)
     */
    @ApiModelProperty("状态(0 关闭, 1 开启)")
    @TableField("status")
    @Excel(name = "状态", readConverterExp = "1=关闭,2=开启")
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

    /**
     * 活动名称
     */
    @TableField(exist = false)
    @Localized("activityName")
    private String activityName;
    /** 活动封面 */

    /**
     * 活动简介
     */
    @TableField(exist = false)
    @Localized("activityInfo")
    private String activityInfo;

    /**
     * 活动简介
     */
    @TableField(exist = false)
    @Localized("activityContent")
    private String activityContent;

    /**
     * 活动内容里的图
     */
    @TableField(exist = false)
    @Localized("activityContentPic")
    private String activityContentPic;

    /**
     * 活动封面
     */
    @TableField(exist = false)
    @Localized("activityPic")
    private String activityPic;

}
