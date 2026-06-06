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
 * 推荐购买网站对象 t_recommend_coin
 *
 * @author axing
 * @date 2024-06-12
 */
@ApiModel("推荐购买网站")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_recommend_coin")
public class RecommendCoin extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:RecommendCoin";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Long id;

    /**
     * 名称(中文)
     */
    @ApiModelProperty("名称(中文)")
    @TableField("name_zh")
    @Excel(name = "名称(中文)")
    private String nameZh;

    /**
     * 名称(英文)
     */
    @ApiModelProperty("名称(英文)")
    @TableField("name_en")
    @Excel(name = "名称(英文)")
    private String nameEn;

    /**
     * 名称(韩语)
     */
    @ApiModelProperty("名称(韩语)")
    @TableField("name_ko")
    @Excel(name = "名称(韩语)")
    private String nameKo;

    /**
     * 名称(葡萄牙)
     */
    @ApiModelProperty("名称(葡萄牙)")
    @TableField("name_pt")
    @Excel(name = "名称(葡萄牙)")
    private String namePt;

    /**
     * 名称(越南语)
     */
    @ApiModelProperty("名称(越南语)")
    @TableField("name_vi")
    @Excel(name = "名称(越南语)")
    private String nameVi;

    /**
     * 名称(越南语)
     */
    @ApiModelProperty("名称(土耳其语)")
    @TableField("name_tr")
    @Excel(name = "名称(土耳其语)")
    private String nameTr;

    /**
     * 名称-繁体中文
     */
    @ApiModelProperty("名称-繁体中文")
    @TableField("name_tw")
    @Excel(name = "名称-繁体中文")
    private String nameTw;

    /**
     * 名称-日语
     */
    @ApiModelProperty("名称-日语")
    @TableField("name_ja")
    @Excel(name = "名称-日语")
    private String nameJa;

    /**
     * 名称-印度语
     */
    @ApiModelProperty("名称-印度语")
    @TableField("name_hi")
    @Excel(name = "名称-印度语")
    private String nameHi;

    /**
     * 名称(泰语)
     */
    @ApiModelProperty("名称(泰语)")
    @TableField("name_th")
    @Excel(name = "名称(泰语)")
    private String nameTh;

    /**
     * 名称(俄语)
     */
    @ApiModelProperty("名称(俄语)")
    @TableField("name_ru")
    @Excel(name = "名称(俄语)")
    private String nameRu;

    /**
     * 名称(阿拉伯语)
     */
    @ApiModelProperty("名称(阿拉伯语)")
    @TableField("name_ar")
    @Excel(name = "名称(阿拉伯语)")
    private String nameAr;



    /**
     * 图标
     */
    @ApiModelProperty("图标")
    @TableField("icon")
    @Excel(name = "图标")
    private String icon;

    /**
     * 黑底图标
     */
    @ApiModelProperty("黑底图标")
    @TableField("black_icon")
    @Excel(name = "黑底图标")
    private String blackIcon;

    /**
     * 中文网网址
     */
    @ApiModelProperty("中文网网址")
    @TableField("website_url_zh")
    @Excel(name = "中文网网址")
    private String websiteUrlZh;

    /**
     * 英文网址
     */
    @ApiModelProperty("英文网址")
    @TableField("website_url_en")
    @Excel(name = "英文网址")
    private String websiteUrlEn;

    /**
     * 网址韩语
     */
    @ApiModelProperty("网址韩语")
    @TableField("website_url_ko")
    @Excel(name = "网址韩语")
    private String websiteUrlKo;

    /**
     * 网址西班牙语
     */
    @ApiModelProperty("网址西班牙语")
    @TableField("website_url_pt")
    @Excel(name = "网址西班牙语")
    private String websiteUrlPt;

    /**
     * 网址越南语
     */
    @ApiModelProperty("网址越南语")
    @TableField("website_url_vi")
    @Excel(name = "网址越南语")
    private String websiteUrlVi;
    /**
     * 网址土耳其语
     */
    @ApiModelProperty("网址土耳其语")
    @TableField("website_url_tr")
    @Excel(name = "网址土耳其语")
    private String websiteUrlTr;

    /**
     * 中文网址-繁体中文
     */
    @ApiModelProperty("中文网址-繁体中文")
    @TableField("website_url_tw")
    @Excel(name = "中文网址-繁体中文")
    private String websiteUrlTw;

    /**
     * 网址-日语
     */
    @ApiModelProperty("网址-日语")
    @TableField("website_url_ja")
    @Excel(name = "网址-日语")
    private String websiteUrlJa;

    /**
     * 网址-印度语
     */
    @ApiModelProperty("网址-印度语")
    @TableField("website_url_hi")
    @Excel(name = "网址-印度语")
    private String websiteUrlHi;

    /**
     * 网址(泰语)
     */
    @ApiModelProperty("网址(泰语)")
    @TableField("website_url_th")
    @Excel(name = "网址(泰语)")
    private String websiteUrlTh;

    /**
     * 网址(俄语)
     */
    @ApiModelProperty("网址(俄语)")
    @TableField("website_url_ru")
    @Excel(name = "网址(俄语)")
    private String websiteUrlRu;


    /**
     * 网址(阿拉伯语)
     */
    @ApiModelProperty("网址(阿拉伯语)")
    @TableField("website_url_ar")
    @Excel(name = "网址(阿拉伯语)")
    private String websiteUrlAr;

    /**
     * 帮助中文网网址
     */
    @ApiModelProperty("帮助中文网网址")
    @TableField("help_url_zh")
    @Excel(name = "帮助中文网网址")
    private String helpUrlZh;

    /**
     * 帮助英文网址
     */
    @ApiModelProperty("帮助英文网址")
    @TableField("help_url_en")
    @Excel(name = "帮助英文网址")
    private String helpUrlEn;

    /**
     * 帮助网址韩语
     */
    @ApiModelProperty("帮助网址韩语")
    @TableField("help_url_ko")
    @Excel(name = "帮助网址韩语")
    private String helpUrlKo;

    /**
     * 帮助网址葡萄牙
     */
    @ApiModelProperty("帮助网址葡萄牙")
    @TableField("help_url_pt")
    @Excel(name = "帮助网址葡萄牙")
    private String helpUrlPt;

    /**
     * 帮助网址越南语
     */
    @ApiModelProperty("帮助网址越南语")
    @TableField("help_url_vi")
    @Excel(name = "帮助网址越南语")
    private String helpUrlVi;
    /**
     * 帮助网址土耳其语
     */
    @ApiModelProperty("帮助网址土耳其语")
    @TableField("help_url_tr")
    @Excel(name = "帮助网址土耳其语")
    private String helpUrlTr;

    /**
     * 帮助网址-繁体中文
     */
    @ApiModelProperty("帮助网址-繁体中文")
    @TableField("help_url_tw")
    @Excel(name = "帮助网址-繁体中文")
    private String helpUrlTw;

    /**
     * 帮助网址-日语
     */
    @ApiModelProperty("帮助网址-日语")
    @TableField("help_url_ja")
    @Excel(name = "帮助网址-日语")
    private String helpUrlJa;

    /**
     * 帮助网址-印度语
     */
    @ApiModelProperty("帮助网址-印度语")
    @TableField("help_url_hi")
    @Excel(name = "帮助网址-印度语")
    private String helpUrlHi;

    /**
     * 帮助网址(泰语)
     */
    @ApiModelProperty("帮助网址(泰语)")
    @TableField("help_url_th")
    @Excel(name = "帮助网址(泰语)")
    private String helpUrlTh;

    /**
     * 帮助网址(俄语)
     */
    @ApiModelProperty("帮助网址(俄语)")
    @TableField("help_url_ru")
    @Excel(name = "帮助网址(俄语)")
    private String helpUrlRu;

    /**
     * 帮助网址(阿拉伯语)
     */
    @ApiModelProperty("帮助网址(阿拉伯语)")
    @TableField("help_url_ar")
    @Excel(name = "帮助网址(阿拉伯语)")
    private String helpUrlAr;

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

    @Localized("name")
    @TableField(exist = false)
    public String name;
    @Localized("websiteUrl")
    @TableField(exist = false)
    public String websiteUrl;
    @Localized("helpUrl")
    @TableField(exist = false)
    public String helpUrl;

}
