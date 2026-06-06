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
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

import static com.gp.common.base.excel.annotation.Excel.Type.IMPORT;

/**
 * 游戏厂商对象 t_game_plate
 *
 * @author axing
 * @date 2024-07-16
 */
@EqualsAndHashCode(callSuper = true)
@ApiModel("游戏厂商")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_game_plate")
public class GamePlate extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:GamePlate";

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
     * 游戏厂商编码
     */
    @ApiModelProperty("游戏厂商编码")
    @TableField("plate_code")
    @Excel(name = "游戏厂商编码")
    private String plateCode;

    /**
     * 归属中文名
     */
    @ApiModelProperty("归属")
    @TableField("category")
    @Excel(name = "归属")
    private String category;

    /**
     * 归属图标
     */
    @ApiModelProperty("归属图标")
    @TableField("icon")
    @Excel(name = "归属图标")
    private String icon;

    /**
     * 归属黑夜图标
     */
    @ApiModelProperty("归属黑夜图标")
    @TableField("black_icon")
    @Excel(name = "归属黑夜图标")
    private String blackIcon;

    /**
     * 场馆图
     */
    @ApiModelProperty("场馆图")
    @TableField("btc_icon")
    @Excel(name = "场馆图")
    private String btcIcon;

    /**
     * 封面图
     */
    @ApiModelProperty("封面图")
    @TableField("cover_icon")
    @Excel(name = "封面图")
    private String coverIcon;

    /**
     * 默认游戏id
     */
    @ApiModelProperty("默认游戏id")
    @TableField(value = "default_game_id", fill = FieldFill.INSERT_UPDATE)
    @Excel(name = "默认游戏id")
    private Long defaultGameId;

    /**
     * 游戏code
     */
    @ApiModelProperty("游戏code")
    @TableField("game_type_code")
    @Excel(name = "游戏code")
    private String gameTypeCode;

    /**
     * 游戏方的费率
     */
    @ApiModelProperty("游戏方的费率")
    @TableField("game_rate")
    @Excel(name = "游戏方的费率", type = IMPORT)
    private BigDecimal gameRate;

    /**
     * 包网方的费率
     */
    @ApiModelProperty("包网方的费率")
    @TableField("game_rate_merchant")
    @Excel(name = "包网方的费率", type = IMPORT)
    private BigDecimal gameRateMerchant;

    /**
     * 币种
     */
    @ApiModelProperty("币种")
    @TableField("currency")
    @Excel(name = "币种")
    private String currency;

    /**
     * 支持杀率(0 不支持, 1 支持)
     */
    @ApiModelProperty("支持杀率(0 不支持, 1 支持)")
    @TableField("rtp_type")
    @Excel(name = "支持杀率(0 不支持, 1 支持)")
    private Integer rtpType;

    /**
     * 钱包类型(1 单一钱包, 2 转账钱包)
     */
    @ApiModelProperty("钱包类型(1 单一钱包, 2 转账钱包)")
    @TableField("wallet_type")
    @Excel(name = "钱包类型(1 单一钱包, 2 转账钱包)")
    private Integer walletType;

    /**
     * 白名单
     */
    @ApiModelProperty("白名单")
    @TableField("white_ip")
    @Excel(name = "白名单")
    private String whiteIp;

    /**
     * 商户账号
     */
    @ApiModelProperty("商户账号")
    @TableField("account")
    @Excel(name = "商户账号", type = IMPORT)
    private String account;

    /**
     * 商户token
     */
    @ApiModelProperty("商户token")
    @TableField("token")
    @Excel(name = "商户token", type = IMPORT)
    private String token;

    /**
     * 商户秘钥
     */
    @ApiModelProperty("商户秘钥")
    @TableField("secret")
    @Excel(name = "商户秘钥")
    private String secret;

    /**
     * 扩展参数1
     */
    @ApiModelProperty("扩展参数1")
    @TableField("param_1")
    @Excel(name = "扩展参数1")
    private String param1;

    /**
     * 扩展参数2
     */
    @ApiModelProperty("扩展参数2")
    @TableField("param_2")
    @Excel(name = "扩展参数2")
    private String param2;

    /**
     * 扩展参数3
     */
    @ApiModelProperty("扩展参数3")
    @TableField("param_3")
    @Excel(name = "扩展参数3")
    private String param3;

    /**
     * 游戏域名
     */
    @ApiModelProperty("游戏域名")
    @TableField("game_domain")
    @Excel(name = "游戏域名")
    private String gameDomain;

    /**
     * 状态(0 关闭, 1 开启)
     */
    @ApiModelProperty("状态(0 关闭, 1 开启)")
    @TableField("status")
    @Excel(name = "状态(0 关闭, 1 开启)")
    private Integer status;

    /** 是否展示 0 不展示 1展示 */
    @ApiModelProperty("是否展示 0 不展示 1展示")
    @TableField("is_show")
    @Excel(name = "是否展示 0 不展示 1展示")
    private Integer isShow;
    /**
     * 是否刷新获取链接
     */
    @ApiModelProperty("是否刷新获取链接")
    @TableField("is_refresh_url")
    @Excel(name = "是否刷新获取链接")
    private Integer isRefreshUrl;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    @TableField("sort")
    @Excel(name = "排序")
    private Integer sort;

    /**
     * 是否维护
     */
    @ApiModelProperty("是否维护")
    @TableField("is_maint")
    @Excel(name = "是否维护 (0 否, 1 是)")
    private Integer isMaint;

    /**
     * 是否支持试玩
     */
    @ApiModelProperty("是否支持试玩")
    @TableField("is_demo")
    @Excel(name = "是否支持试玩(0 否,1 是)")
    private Integer isDemo;

    /**
     * 运营区域
     */
    @ApiModelProperty("运营区域")
    @TableField("operating_area")
    @Excel(name = "运营区域")
    private String operatingArea;


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

    @ApiModelProperty("签名是否正确")
    @TableField(exist = false)
    private Boolean flag;

    @Localized("plateName")
    @TableField(exist = false)
    public String plateName;

    @ApiModelProperty("杀率")
    @TableField(exist = false)
    private String rtp;

}
