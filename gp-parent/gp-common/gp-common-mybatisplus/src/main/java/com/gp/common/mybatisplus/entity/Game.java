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
 * 游戏对象 t_game
 *
 * @author axing
 * @date 2024-08-13
 */
@ApiModel("游戏")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_game")
public class Game extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:Game";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Long id;

    /**
     * 游戏代理商名称-中文
     */
    @ApiModelProperty("游戏代理商名称")
    @TableField("dealer_name")
    @Excel(name = "游戏代理商名称")
    private String dealerName;

    /**
     * 游戏代理商编码
     */
    @ApiModelProperty("游戏代理商编码")
    @TableField("dealer_code")
    @Excel(name = "游戏代理商编码")
    private String dealerCode;

    /**
     * 游戏厂商名称-中文
     */
    @ApiModelProperty("游戏厂商名称-中文")
    @TableField("plate_name_zh")
    @Excel(name = "游戏厂商名称-中文")
    private String plateNameZh;

    /**
     * 游戏厂商名称-英文
     */
    @ApiModelProperty("游戏厂商名称-英文")
    @TableField("plate_name_en")
    @Excel(name = "游戏厂商名称-英文")
    private String plateNameEn;

    /**
     * 游戏厂商名称-韩语
     */
    @ApiModelProperty("游戏厂商名称-韩语")
    @TableField("plate_name_ko")
    @Excel(name = "游戏厂商名称-韩语")
    private String plateNameKo;

    /**
     * 游戏厂商名称-葡萄牙
     */
    @ApiModelProperty("游戏厂商名称-葡萄牙")
    @TableField("plate_name_pt")
    @Excel(name = "游戏厂商名称-葡萄牙")
    private String plateNamePt;

    /**
     * 游戏厂商名称-越南语
     */
    @ApiModelProperty("游戏厂商名称-越南语")
    @TableField("plate_name_vi")
    @Excel(name = "游戏厂商名称-越南语")
    private String plateNameVi;

    /**
     * 游戏厂商名称-土耳其语
     */
    @ApiModelProperty("游戏厂商名称-土耳其语")
    @TableField("plate_name_tr")
    @Excel(name = "游戏厂商名称-土耳其语")
    private String plateNameTr;

    /**
     * 游戏厂商名称-繁体中文
     */
    @ApiModelProperty("游戏厂商名称-繁体中文")
    @TableField("plate_name_tw")
    @Excel(name = "游戏厂商名称-繁体中文")
    private String plateNameTw;

    /**
     * 游戏厂商名称-日语
     */
    @ApiModelProperty("游戏厂商名称-日语")
    @TableField("plate_name_ja")
    @Excel(name = "游戏厂商名称-日语")
    private String plateNameJa;

    /**
     * 游戏厂商名称-印度语
     */
    @ApiModelProperty("游戏厂商名称-印度语")
    @TableField("plate_name_hi")
    @Excel(name = "游戏厂商名称-印度语")
    private String plateNameHi;

    /**
     * 游戏厂商名称-泰语
     */
    @ApiModelProperty("游戏厂商名称-泰语")
    @TableField("plate_name_th")
    @Excel(name = "游戏厂商名称-泰语")
    private String plateNameTh;


    /**
     * 游戏厂商名称-俄语
     */
    @ApiModelProperty("游戏厂商名称-俄语")
    @TableField("plate_name_ru")
    @Excel(name = "游戏厂商名称-俄语")
    private String plateNameRu;

    /**
     * 游戏厂商名称-阿拉伯语
     */
    @ApiModelProperty("游戏厂商名称-阿拉伯语")
    @TableField("plate_name_ar")
    @Excel(name = "游戏厂商名称-阿拉伯语")
    private String plateNameAr;

    /**
     * 游戏厂商名称编码
     */
    @ApiModelProperty("游戏厂商名称编码")
    @TableField("plate_code")
    @Excel(name = "游戏厂商名称编码")
    private String plateCode;

    /**
     * 游戏名称-中文
     */
    @ApiModelProperty("游戏名称-中文")
    @TableField("game_name_zh")
    @Excel(name = "游戏名称-中文")
    private String gameNameZh;

    /**
     * 游戏名称-英语
     */
    @ApiModelProperty("游戏名称-英语")
    @TableField("game_name_en")
    @Excel(name = "游戏名称-英语")
    private String gameNameEn;

    /**
     * 游戏名称-韩语
     */
    @ApiModelProperty("游戏名称-韩语")
    @TableField("game_name_ko")
    @Excel(name = "游戏名称-韩语")
    private String gameNameKo;

    /**
     * 游戏名称-葡萄牙
     */
    @ApiModelProperty("游戏名称-葡萄牙")
    @TableField("game_name_pt")
    @Excel(name = "游戏名称-葡萄牙")
    private String gameNamePt;

    /**
     * 游戏名称-越南语
     */
    @ApiModelProperty("游戏名称-越南语")
    @TableField("game_name_vi")
    @Excel(name = "游戏名称-越南语")
    private String gameNameVi;
    /**
     * 游戏名称-土耳其语
     */
    @ApiModelProperty("游戏名称-土耳其语")
    @TableField("game_name_tr")
    @Excel(name = "游戏名称-土耳其语")
    private String gameNameTr;

    /**
     * 游戏名称-繁体中文
     */
    @ApiModelProperty("游戏名称-繁体中文")
    @TableField("game_name_tw")
    @Excel(name = "游戏名称-繁体中文")
    private String gameNameTw;

    /**
     * 游戏名称-日语
     */
    @ApiModelProperty("游戏名称-日语")
    @TableField("game_name_ja")
    @Excel(name = "游戏名称-日语")
    private String gameNameJa;

    /**
     * 游戏名称-印度语
     */
    @ApiModelProperty("游戏名称-印度语")
    @TableField("game_name_hi")
    @Excel(name = "游戏名称-印度语")
    private String gameNameHi;

    /**
     * 游戏名称-泰语
     */
    @ApiModelProperty("游戏名称-泰语")
    @TableField("game_name_th")
    @Excel(name = "游戏名称-泰语")
    private String gameNameTh;

    /**
     * 游戏名称-俄语
     */
    @ApiModelProperty("游戏名称-俄语")
    @TableField("game_name_ru")
    @Excel(name = "游戏名称-俄语")
    private String gameNameRu;

    /**
     * 游戏名称-阿拉伯语
     */
    @ApiModelProperty("游戏名称-阿拉伯语")
    @TableField("game_name_ar")
    @Excel(name = "游戏名称-阿拉伯语")
    private String gameNameAr;

    /**
     * 游戏编码
     */
    @ApiModelProperty("游戏编码")
    @TableField("game_code")
    @Excel(name = "游戏编码")
    private String gameCode;

    /**
     * 游戏编码2(扩展的游戏编码)
     */
    @ApiModelProperty("游戏编码2(扩展的游戏编码)")
    @TableField("game_code2")
    @Excel(name = "游戏编码2(扩展的游戏编码)")
    private String gameCode2;

    /**
     * 游戏封面(英文)
     */
    @ApiModelProperty("游戏方图(英文)")
    @TableField("game_pic_en")
    @Excel(name = "游戏方图")
    private String gamePicEn;

    /** 游戏封面 */
//    @ApiModelProperty("游戏封面")
//    @TableField("game_pic_black")
//    @Excel(name = "游戏封面")
//    private String gamePicBlack;

    /**
     * 游戏竖图(中文)
     */
    @ApiModelProperty("游戏竖图(中文)")
    @TableField("game_pic_vertical_zh")
    @Excel(name = "游戏竖图(中文)")
    private String gamePicVerticalZh;

    /**
     * 游戏竖图(英文)
     */
    @ApiModelProperty("游戏竖图(英文)")
    @TableField("game_pic_vertical_en")
    @Excel(name = "游戏竖图(英文)")
    private String gamePicVerticalEn;

    /**
     * 游戏竖图(韩语)
     */
    @ApiModelProperty("游戏竖图(韩语)")
    @TableField("game_pic_vertical_ko")
    @Excel(name = "游戏竖图(韩语)")
    private String gamePicVerticalKo;

    /**
     * 游戏竖图(巴西)
     */
    @ApiModelProperty("游戏竖图(巴西)")
    @TableField("game_pic_vertical_br")
    @Excel(name = "游戏竖图(巴西)")
    private String gamePicVerticalBr;

    /**
     * 游戏竖图(越南)
     */
    @ApiModelProperty("游戏竖图(越南)")
    @TableField("game_pic_vertical_vi")
    @Excel(name = "游戏竖图(越南)")
    private String gamePicVerticalVi;

    /**
     * 游戏竖图(中文繁体)
     */
    @ApiModelProperty("游戏竖图(中文繁体)")
    @TableField("game_pic_vertical_tw")
    @Excel(name = "游戏竖图(中文繁体)")
    private String gamePicVerticalTw;

    /**
     * 游戏竖图(日语)
     */
    @ApiModelProperty("游戏竖图(日语)")
    @TableField("game_pic_vertical_ja")
    @Excel(name = "游戏竖图(日语)")
    private String gamePicVerticalJa;

    /**
     * 游戏竖图(印度语)
     */
    @ApiModelProperty("游戏竖图(印度语)")
    @TableField("game_pic_vertical_hi")
    @Excel(name = "游戏竖图(印度语)")
    private String gamePicVerticalHi;

    /**
     * 游戏竖图(泰语)
     */
    @ApiModelProperty("游戏竖图(泰语)")
    @TableField("game_pic_vertical_th")
    @Excel(name = "游戏竖图(泰语)")
    private String gamePicVerticalTh;

    /**
     * 游戏竖图(俄语)
     */
    @ApiModelProperty("游戏竖图(俄语)")
    @TableField("game_pic_vertical_ru")
    @Excel(name = "游戏竖图(俄语)")
    private String gamePicVerticalRu;

    /**
     * 游戏竖图(阿拉伯语)
     */
    @ApiModelProperty("游戏竖图(阿拉伯语)")
    @TableField("game_pic_vertical_ar")
    @Excel(name = "游戏竖图(阿拉伯语)")
    private String gamePicVerticalAr;

    /**
     * 在线人数
     */
    @ApiModelProperty("在线人数")
    @TableField("online_section")
    @Excel(name = "在线人数(区间)")
    private String onlineSection;

    /**
     * 游戏简介-中文
     */
    @ApiModelProperty("游戏简介-中文")
    @TableField("game_info_zh")
    @Excel(name = "游戏简介-中文")
    private String gameInfoZh;

    /**
     * 游戏简介-英文
     */
    @ApiModelProperty("游戏简介-英文")
    @TableField("game_info_en")
    @Excel(name = "游戏简介-英文")
    private String gameInfoEn;

    /**
     * 游戏简介-韩语
     */
    @ApiModelProperty("游戏简介-韩语")
    @TableField("game_info_ko")
    @Excel(name = "游戏简介-韩语")
    private String gameInfoKo;

    /**
     * 游戏简介-葡萄牙
     */
    @ApiModelProperty("游戏简介-葡萄牙")
    @TableField("game_info_pt")
    @Excel(name = "游戏简介-葡萄牙")
    private String gameInfoPt;

    /**
     * 游戏简介-越南语
     */
    @ApiModelProperty("游戏简介-越南语")
    @TableField("game_info_vi")
    @Excel(name = "游戏简介-越南语")
    private String gameInfoVi;
    /**
     * 游戏简介-土耳其语
     */
    @ApiModelProperty("游戏简介-土耳其语")
    @TableField("game_info_tr")
    @Excel(name = "游戏简介-土耳其语")
    private String gameInfoTr;

    /**
     * 游戏简介-繁体中文
     */
    @ApiModelProperty("游戏简介-繁体中文")
    @TableField("game_info_tw")
    @Excel(name = "游戏简介-繁体中文")
    private String gameInfoTw;

    /**
     * 游戏简介-日语
     */
    @ApiModelProperty("游戏简介-日语")
    @TableField("game_info_ja")
    @Excel(name = "游戏简介-日语")
    private String gameInfoJa;

    /**
     * 游戏简介-印度语
     */
    @ApiModelProperty("游戏简介-印度语")
    @TableField("game_info_hi")
    @Excel(name = "游戏简介-印度语")
    private String gameInfoHi;

    /**
     * 游戏简介-泰语
     */
    @ApiModelProperty("游戏简介-泰语")
    @TableField("game_info_th")
    @Excel(name = "游戏简介-泰语")
    private String gameInfoTh;

    /**
     * 游戏简介-俄语
     */
    @ApiModelProperty("游戏简介-俄语")
    @TableField("game_info_ru")
    @Excel(name = "游戏简介-俄语")
    private String gameInfoRu;

    /**
     * 游戏简介-阿拉伯语
     */
    @ApiModelProperty("游戏简介-阿拉伯语")
    @TableField("game_info_ar")
    @Excel(name = "游戏简介-阿拉伯语")
    private String gameInfoAr;

    /**
     * 游戏类型
     */
    @ApiModelProperty("游戏类型")
    @TableField("game_type_code")
    @Excel(name = "游戏类型")
    private String gameTypeCode;

    /**
     * 归属中文名
     */
    @ApiModelProperty("归属")
    @TableField("category")
    @Excel(name = "归属")
    private String category;

    /**
     * 是否热门 1热门
     */
    @ApiModelProperty("是否热门 1热门")
    @TableField("is_hot")
    @Excel(name = "是否热门 1热门")
    private Integer isHot;

    /**
     * 状态(0 关闭, 1 开启)
     */
    @ApiModelProperty("状态(0 关闭, 1 开启)")
    @TableField("status")
    @Excel(name = "状态(0 关闭, 1 开启)")
    private Integer status;

    /**
     * 安卓是否打开h5
     */
    @ApiModelProperty("安卓是否打开h5")
    @TableField("android_is_open_h5")
    @Excel(name = "安卓是否打开h5")
    private Integer androidIsOpenH5;

    /**
     * ios是否打开h5
     */
    @ApiModelProperty("ios是否打开h5")
    @TableField("ios_is_open_h5")
    @Excel(name = "ios是否打开h5")
    private Integer iosIsOpenH5;

    /**
     * pc是否打开h5
     */
    @ApiModelProperty("pc是否打开h5")
    @TableField("pc_is_open_h5")
    @Excel(name = "pc是否打开h5")
    private Integer pcIsOpenH5;

    /**
     * mac是否打开h5
     */
    @ApiModelProperty("mac是否打开h5")
    @TableField("mac_is_open_h5")
    @Excel(name = "mac是否打开h5")
    private Integer macIsOpenH5;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    @TableField("sort")
    @Excel(name = "排序")
    private Integer sort;

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
     * 修改人
     */
    @ApiModelProperty("修改人")
    @TableField("update_by")
    @Excel(name = "修改人")
    private String updateBy;

    @Localized("gameName")
    @TableField(exist = false)
    public String gameName;
    @Localized("gameInfo")
    @TableField(exist = false)
    public String gameInfo;

    @Localized("gamePic")
    @TableField(exist = false)
    public String gamePic;

    @Localized("gamePicVertical")
    @TableField(exist = false)
    public String gamePicVertical;

    /**
     * 是否刷新获取链接
     */
    @ApiModelProperty("是否刷新获取链接 0 不刷新 1刷新")
    @TableField(exist = false)
    private Integer isRefreshUrl = 0;

    /**
     * 热门排序
     */
    @ApiModelProperty("热门排序")
    @TableField("hot_sort")
    @Excel(name = "热门排序")
    private Integer hotSort;

    /**
     * 厂商排序
     */
    @ApiModelProperty("厂商排序")
    @TableField("plate_sort")
    @Excel(name = "厂商排序")
    private Integer plateSort;

    /**
     * 是否新游戏
     */
    @ApiModelProperty("是否新游戏")
    @TableField("is_new_game")
    @Excel(name = "是否新游戏(0 不是 1是)")
    private Integer isNewGame;

    /**
     * 游戏厂商ID
     */
    @ApiModelProperty("游戏厂商ID")
    @TableField(exist = false)
    private Long gamePlateId;
}
