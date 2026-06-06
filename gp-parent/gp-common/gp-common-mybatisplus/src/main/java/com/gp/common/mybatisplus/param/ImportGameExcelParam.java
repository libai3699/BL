package com.gp.common.mybatisplus.param;

import com.gp.common.base.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("导入游戏列表参数")
@Data
public class ImportGameExcelParam implements Serializable {
    /**
     * id
     */
    @ApiModelProperty("id")
    @Excel(name = "id")
    private Long id;


    /**
     * 游戏厂商ID
     */
    @ApiModelProperty("游戏厂商ID")
    @Excel(name = "游戏厂商ID")
    private Long gamePlateId;

    /**
     * 游戏类型名称(中文)
     */
    @ApiModelProperty("游戏类型名称(中文)")
    @Excel(name = "游戏类型名称(中文)")
    private String typeNameZh;

    /**
     * 游戏编码
     */
    @ApiModelProperty("游戏编码")
    @Excel(name = "游戏编码")
    private String gameCode;

    /**
     * 游戏编码2(扩展的游戏编码)
     */
    @ApiModelProperty("游戏编码2(扩展的游戏编码)")
    @Excel(name = "二级游戏编码")
    private String gameCode2;

    /**
     * 游戏名称-中文
     */
    @ApiModelProperty("游戏名称-中文")
    @Excel(name = "游戏名称-中文")
    private String gameNameZh;

    /**
     * 游戏简介-中文
     */
    @ApiModelProperty("游戏简介-中文")
    @Excel(name = "游戏简介-中文")
    private String gameInfoZh;

    /**
     * 游戏名称-繁体中文
     */
    @ApiModelProperty("游戏名称-繁体中文")
    @Excel(name = "游戏名称-繁体中文")
    private String gameNameTw;

    /**
     * 游戏简介-繁体中文
     */
    @ApiModelProperty("游戏简介-繁体中文")
    @Excel(name = "游戏简介-繁体中文")
    private String gameInfoTw;

    /**
     * 游戏名称-英语
     */
    @ApiModelProperty("游戏名称-英文")
    @Excel(name = "游戏名称-英文")
    private String gameNameEn;

    /**
     * 游戏简介-英文
     */
    @ApiModelProperty("游戏简介-英文")
    @Excel(name = "游戏简介-英文")
    private String gameInfoEn;

    /**
     * 游戏名称-韩语
     */
    @ApiModelProperty("游戏名称-韩语")
    @Excel(name = "游戏名称-韩语")
    private String gameNameKo;

    /**
     * 游戏简介-韩语
     */
    @ApiModelProperty("游戏简介-韩语")
    @Excel(name = "游戏简介-韩语")
    private String gameInfoKo;

    /**
     * 游戏名称-葡萄牙
     */
    @ApiModelProperty("游戏名称-葡萄牙")
    @Excel(name = "游戏名称-葡萄牙")
    private String gameNamePt;

    /**
     * 游戏简介-葡萄牙
     */
    @ApiModelProperty("游戏简介-葡萄牙")
    @Excel(name = "游戏简介-葡萄牙")
    private String gameInfoPt;

    /**
     * 游戏名称-越南语
     */
    @ApiModelProperty("游戏名称-越南语")
    @Excel(name = "游戏名称-越南语")
    private String gameNameVi;

    /**
     * 游戏简介-越南语
     */
    @ApiModelProperty("游戏简介-越南语")
    @Excel(name = "游戏简介-越南语")
    private String gameInfoVi;

    /**
     * 游戏名称-土耳其语
     */
    @ApiModelProperty("游戏名称-土耳其语")
    @Excel(name = "游戏名称-土耳其语")
    private String gameNameTr;

    /**
     * 游戏简介-土耳其语
     */
    @ApiModelProperty("游戏简介-土耳其语")
    @Excel(name = "游戏简介-土耳其语")
    private String gameInfoTr;

    /**
     * 游戏名称-日语
     */
    @ApiModelProperty("游戏名称-日语")
    @Excel(name = "游戏名称-日语")
    private String gameNameJa;

    /**
     * 游戏简介-日语
     */
    @ApiModelProperty("游戏简介-日语")
    @Excel(name = "游戏简介-日语")
    private String gameInfoJa;

    /**
     * 游戏名称-印度语
     */
    @ApiModelProperty("游戏名称-印度语")
    @Excel(name = "游戏名称-印度语")
    private String gameNameHi;

    /**
     * 游戏简介-印度语
     */
    @ApiModelProperty("游戏简介-印度语")
    @Excel(name = "游戏简介-印度语")
    private String gameInfoHi;

    /**
     * 游戏名称-泰语
     */
    @ApiModelProperty("游戏名称-泰语")
    @Excel(name = "游戏名称-泰语")
    private String gameNameTh;

    /**
     * 游戏简介-泰语
     */
    @ApiModelProperty("游戏简介-泰语")
    @Excel(name = "游戏简介-泰语")
    private String gameInfoTh;

    /**
     * 游戏名称-俄语
     */
    @ApiModelProperty("游戏名称-俄语")
    @Excel(name = "游戏名称-俄语")
    private String gameNameRu;

    /**
     * 游戏简介-俄语
     */
    @ApiModelProperty("游戏简介-俄语")
    @Excel(name = "游戏简介-俄语")
    private String gameInfoRu;

    /**
     * 游戏名称-阿拉伯语
     */
    @ApiModelProperty("游戏名称-阿拉伯语")
    @Excel(name = "游戏名称-阿拉伯语")
    private String gameNameAr;

    /**
     * 游戏简介-阿拉伯语
     */
    @ApiModelProperty("游戏简介-阿拉伯语")
    @Excel(name = "游戏简介-阿拉伯语")
    private String gameInfoAr;

    /**
     * 安卓是否打开h5
     */
    @ApiModelProperty("安卓是否打开h5(0-否、1-是)")
    @Excel(name = "安卓是否打开h5(0-否、1-是)")
    private Integer androidIsOpenH5;

    /**
     * ios是否打开h5
     */
    @ApiModelProperty("ios是否打开h5(0-否、1-是)")
    @Excel(name = "ios是否打开h5(0-否、1-是)")
    private Integer iosIsOpenH5;

    /**
     * pc是否打开h5
     */
    @ApiModelProperty("pc是否打开h5(0-否、1-是)")
    @Excel(name = "pc是否打开h5(0-否、1-是)")
    private Integer pcIsOpenH5;

    /**
     * mac是否打开h5
     */
    @ApiModelProperty("mac是否打开h5(0-否、1-是)")
    @Excel(name = "mac是否打开h5(0-否、1-是)")
    private Integer macIsOpenH5;

    /**
     * 是否热门 1热门
     */
    @ApiModelProperty("是否热门(0-非热门、1-热门)")
    @Excel(name = "是否热门((0-非热门、1-热门)）")
    private Integer isHot;

    /**
     * 状态(0 关闭, 1 开启)
     */
    @ApiModelProperty("状态(0 关闭, 1 开启)")
    @Excel(name = "状态(0 关闭, 1 开启)")
    private Integer status;

    /**
     * 是否新游戏
     */
    @ApiModelProperty("是否新游戏")
    @Excel(name = "是否新游戏(0 不是 1是)")
    private Integer isNewGame;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    @Excel(name = "排序")
    private Integer sort = 0;

    /**
     * 热门排序
     */
    @ApiModelProperty("热门排序")
    @Excel(name = "热门排序")
    private Integer hotSort;

    /**
     * 厂商排序
     */
    @ApiModelProperty("厂商排序")
    @Excel(name = "厂商排序")
    private Integer plateSort;
}
