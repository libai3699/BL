package com.gp.common.mybatisplus.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import com.gp.common.base.excel.annotation.Excel;
import com.gp.common.mybatisplus.base.BaseEntity;

import javax.validation.constraints.NotBlank;

/**
 * 商户对象 t_merchant
 *
 * @author axing
 * @date 2024-08-09
 */
@ApiModel("商户")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_merchant")
public class Merchant extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:Merchant";

    /**
     * 商户id
     */
    @ApiModelProperty("商户id")
    @TableId(value = "merchant_id", type = IdType.AUTO)
    @Excel(name = "商户id")
    private Long merchantId;

    /**
     * 商户名称
     */
    @ApiModelProperty("商户名称")
    @TableField("merchant_name")
    @Excel(name = "商户名称")
    @NotBlank(message = "商户名称不能为空")
    private String merchantName;

    /**
     * 商户编码
     */
    @ApiModelProperty("商户编码")
    @TableField("code")
    @Excel(name = "商户编码")
    @NotBlank(message = "商户编码不能为空")
    private String code;

    /**
     * 积分(usdt)
     */
    @ApiModelProperty("积分(usdt)")
    @TableField("amount")
    @Excel(name = "积分(usdt)")
    private BigDecimal amount;

    /**
     * logo
     */
    @ApiModelProperty("logo")
    @TableField("logo")
    @Excel(name = "logo")
    private String logo;

    /**
     * 体育播报图片
     */
    @ApiModelProperty("体育播报图片")
    @TableField("soccer_report")
    @Excel(name = "体育播报图片")
    private String soccerReport;

    /**
     * 转盘图片
     */
    @ApiModelProperty("转盘图片")
    @TableField("wheel_pic")
    @Excel(name = "转盘图片")
    private String wheelPic;

    /**
     * 活动通知图片
     */
    @ApiModelProperty("活动通知图片")
    @TableField("activity_pic")
    @Excel(name = "活动通知图片")
    private String activityPic;

    /**
     * 充值奖励图
     */
    @ApiModelProperty("充值奖励图")
    @TableField("recharge_pic")
    @Excel(name = "充值奖励图")
    private String rechargePic;

    /**
     * 提现通知图
     */
    @ApiModelProperty("提现通知图")
    @TableField("withdraw_pic")
    @Excel(name = "提现通知图")
    private String withdrawPic;

    /**
     * 手动下分图
     */
    @ApiModelProperty("手动下分图")
    @TableField("down_money_pic")
    @Excel(name = "手动下分图")
    private String downMoneyPic;

    /**
     * 升级vip2图
     */
    @ApiModelProperty("升级vip2图")
    @TableField("level_up_pic_v2")
    @Excel(name = "升级vip2图")
    private String levelUpPicV2;

    /**
     * 升级vip3图
     */
    @ApiModelProperty("升级vip3图")
    @TableField("level_up_pic_v3")
    @Excel(name = "升级vip3图")
    private String levelUpPicV3;

    /**
     * 升级vip4图
     */
    @ApiModelProperty("升级vip4图")
    @TableField("level_up_pic_v4")
    @Excel(name = "升级vip4图")
    private String levelUpPicV4;

    /**
     * 升级vip5图
     */
    @ApiModelProperty("升级vip5图")
    @TableField("level_up_pic_v5")
    @Excel(name = "升级vip5图")
    private String levelUpPicV5;

    /** 升级vip6图 */
    @ApiModelProperty("升级vip6图")
    @TableField("level_up_pic_v6")
    @Excel(name = "升级vip6图")
    private String levelUpPicV6;

    /** 升级vip7图 */
    @ApiModelProperty("升级vip7图")
    @TableField("level_up_pic_v7")
    @Excel(name = "升级vip7图")
    private String levelUpPicV7;

    /** 升级vip8图 */
    @ApiModelProperty("升级vip8图")
    @TableField("level_up_pic_v8")
    @Excel(name = "升级vip8图")
    private String levelUpPicV8;

    /** 升级vip9图 */
    @ApiModelProperty("升级vip9图")
    @TableField("level_up_pic_v9")
    @Excel(name = "升级vip9图")
    private String levelUpPicV9;

    /** 升级vip10图 */
    @ApiModelProperty("升级vip10图")
    @TableField("level_up_pic_v10")
    @Excel(name = "升级vip10图")
    private String levelUpPicV10;
    /** 升级vip10图 */
    @ApiModelProperty("startBot图")
    @TableField("start_logo")
    @Excel(name = "startBot图")
    private String startLogo;
    /**
     * 欧洲杯奖金渠道 用,个隔开
     */
    @ApiModelProperty("欧洲杯奖金渠道 用,个隔开")
    @TableField("european_channel_id")
    @Excel(name = "欧洲杯奖金渠道 用,个隔开")
    private String europeanChannelId;

    /**
     * 维护特殊账号
     */
    @ApiModelProperty("维护特殊账号")
    @TableField("special_user_id")
    @Excel(name = "维护特殊账号")
    private String specialUserId;

    /**
     * mapy商户红包失败领取通知
     */
    @ApiModelProperty("mapy商户红包失败领取通知")
    @TableField("mpay_red_fail_msg")
    @Excel(name = "mapy商户红包失败领取通知")
    private String mpayRedFailMsg;

    /**
     * 播报群中奖图片
     */
    @ApiModelProperty("播报群中奖图片")
    @TableField("win_url")
    @Excel(name = "播报群中奖图片")
    private String winUrl;

    /**
     * 播报群充值图片
     */
    @ApiModelProperty("播报群充值图片")
    @TableField("recharge_url")
    @Excel(name = "播报群充值图片")
    private String rechargeUrl;

    /**
     * 播报群提现图片
     */
    @ApiModelProperty("播报群提现图片")
    @TableField("withdraw_url")
    @Excel(name = "播报群提现图片")
    private String withdrawUrl;
    /**
     * 播报群提现图片
     */
    @ApiModelProperty("红包url")
    @TableField("red_url")
    @Excel(name = "红包url")
    private String redUrl;

    /**
     * 风控群@的人
     */
    @ApiModelProperty("风控群@的人")
    @TableField("send_risk_ren")
    @Excel(name = "风控群@的人")
    private String sendRiskRen;

    /**
     * 财务群@的人
     */
    @ApiModelProperty("财务群@的人　")
    @TableField("send_ren")
    @Excel(name = "财务群@的人　")
    private String sendRen;

    /**
     * email的key
     */
    @ApiModelProperty("email的key")
    @TableField("email_app_key")
    @Excel(name = "email的key")
    private String emailAppKey;

    /**
     * eamil的模版id
     */
    @ApiModelProperty("eamil的模版id")
    @TableField("email_template_id")
    @Excel(name = "eamil的模版id")
    private String emailTemplateId;

    /**
     * Email 标题
     */
    @ApiModelProperty("Email 标题")
    @TableField("email_alias")
    @Excel(name = "Email 标题")
    private String emailAlias;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField("remark")
    @Excel(name = "备注")
    private String remark;

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

    @ApiModelProperty("签名是否正确")
    @TableField(exist = false)
    private Boolean flag;

    /**
     * 平台说明
     */
    @ApiModelProperty("平台说明")
    @TableField("platform_description")
    @Excel(name = "平台说明")
    private String platformDescription;

    /**
     * 邀请说明
     */
    @ApiModelProperty("邀请说明")
    @TableField("invite_description")
    @Excel(name = "邀请说明")
    private String inviteDescription;
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
     * 创建人
     */
    @ApiModelProperty("创建人")
    @TableField("create_by")
    @Excel(name = "创建人")
    private String createBy;

    /**
     * 状态:0禁用;1启用
     */
    @ApiModelProperty("状态:0禁用;1启用")
    @TableField("status")
    @Excel(name = "状态:0禁用;1启用")
    private Integer status;

    /**
     * 商户域名
     */
    @ApiModelProperty("商户域名")
    @TableField("domain_name")
    @Excel(name = "商户域名")
    @NotBlank(message = "商户域名不能为空")
    private String domainName;

    /**
     * 提现音频
     */
    @ApiModelProperty("提现音频")
    @TableField("audio_withdraw")
    private String audioWithdraw;

    /**
     * 音频提示2
     */
    @ApiModelProperty("法币提现音频")
    @TableField("audio_law")
    private String audioLaw;

}
