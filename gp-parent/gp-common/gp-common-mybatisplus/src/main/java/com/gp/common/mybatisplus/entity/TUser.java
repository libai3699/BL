package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.common.mybatisplus.base.BaseEntity;
import com.gp.common.base.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * 用户表
 */
@ApiModel(description = "用户表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_user")
public class TUser extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:TUser";

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    @TableId(value = "user_id", type = IdType.AUTO)
    @Excel(name = "用户id")
    private Long userId;

    /**
     * 飞机id
     */
    @ApiModelProperty("飞机id")
    @TableField("user_tg_id")
    @Excel(name = "飞机id")
    private Long userTgId;

    /**
     * 新用户id
     */
    @ApiModelProperty("用户id")
    @TableField(exist = false)
    private Long newUserId;
    /**
     * 新飞机id
     */
    @ApiModelProperty("新飞机id")
    @TableField(exist = false)
    private Long newTgId;
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
     * 上级用户id
     */
    @ApiModelProperty("上级用户id")
    @TableField("super_user_id")
    @Excel(name = "上级用户id")
    private Long superUserId;

    /**
     * 上级飞机id
     */
    @ApiModelProperty("上级飞机id")
    @TableField("super_user_tg_id")
    @Excel(name = "上级飞机id")
    private Long superUserTgId;
    /**
     * 上级 ppth
     */
    @ApiModelProperty("上级列表")
    @TableField("p_path")
    @Excel(name = "上级列表")
    private String pPath;

    /**
     * 上级用户名称
     */
    @ApiModelProperty("上级用户名称")
    @TableField(exist = false)
    @Excel(name = "上级用户名称")
    private String username;

    /**
     * 上级飞机用户昵称
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "上级飞机用户昵称")
    @Excel(name = "上级飞机用户昵称")
    private String superUserTgName;

    /**
     * 上级飞机用户名
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "上级飞机用户名")
    @Excel(name = "上级飞机用户名")
    private String superUserTgUsername;

    /**
     * /** 用户头像
     */
    @ApiModelProperty("用户头像")
    @TableField("user_avatar")
    @Excel(name = "用户头像")
    private String userAvatar;

    /**
     * 用户类型
     */
    @ApiModelProperty("用户类型")
    @TableField("user_type")
    private Integer userType;

    /**
     * 用户级别
     */
    @ApiModelProperty("用户级别")
    @TableField("level")
    @Excel(name = "用户级别", cellType = Excel.ColumnType.NUMERIC)
    private Integer level;

    /**
     * 盐
     */
    @ApiModelProperty("盐")
    @TableField("salt")
    private String salt;

    /**
     * 支付密码(6位数字)
     */
    @ApiModelProperty("支付密码(6位数字)")
    @TableField("pay_password")
    private String payPassword;

    /**
     * 谷歌秘钥
     */
    @ApiModelProperty("谷歌秘钥")
    @TableField("google_key")
    private String googleKey;

    /**
     * 语言
     */
    @ApiModelProperty("语言")
    @TableField("lan_key")
    @Excel(name = "语言")
    private String lanKey;

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    @TableField("phone")
    @Excel(name = "手机号")
    private String phone;

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    @TableField("email")
    @Excel(name = "邮箱")
    private String email;

    /**
     * $column.columnComment
     */
    @ApiModelProperty("密码t")
    @TableField("password")
    private String password;

    /**
     * 来源(0是飞机,1是H5)
     */
    @ApiModelProperty("来源(0是飞机,1是H5)")
    @TableField("source")
    @Excel(name = "来源", readConverterExp = "0=飞机,1=H5")
    private Integer source;

    /**
     * 提现冻结状态（0.未冻结 1.冻结）
     */
    @ApiModelProperty("提现冻结状态（0.未冻结 1.冻结）")
    @TableField("freeze_status")
    @Excel(name = "提现冻结状态", readConverterExp = "0=未冻结,1=冻结")
    private Integer freezeStatus;
    /**
     * 操作冻结状态（0.未冻结 1.冻结）
     */
    @ApiModelProperty("操作冻结状态（0.未冻结 1.冻结）")
    @TableField("handle_freeze_status")
    @Excel(name = "操作冻结状态", readConverterExp = "0=未冻结,1=冻结")
    private Integer handleFreezeStatus;
    /**
     * 用户ip
     */
    @ApiModelProperty("用户ip")
    @TableField("ip")
    @Excel(name = "用户ip")
    private String ip;

    /**
     * ip地址
     */
    @ApiModelProperty("ip地址")
    @TableField("ip_addr")
    @Excel(name = "ip地址")
    private String ipAddr;

    @ApiModelProperty("签名时间")
    @TableField("sign_time")
    private Long signTime;

    @ApiModelProperty("签名")
    @TableField("sign")
    private String sign;

    /**
     * 登录时间
     */
    @ApiModelProperty("登录时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("login_time")
    @Excel(name = "登录时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;

    @ApiModelProperty("登录时间数组")
    @TableField(exist = false)
    private String[] loginTimes;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
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
    @Excel(name = "修改时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
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
     * 渠道id,没有则为0
     */
    @ApiModelProperty("渠道id,没有则为0")
    @TableField("channel_id")
    @Excel(name = "渠道id,没有则为0")
    private Long channelId;

    /**
     * 股东ID
     */
    @ApiModelProperty("股东ID")
    @TableField("shareholder_id")
    @Excel(name = "股东ID")
    private Long shareholderId;

    /**
     * 股东名称
     */
    @ApiModelProperty("股东名称")
    @TableField(exist = false)
    @Excel(name = "股东名称")
    private String shareholderName;

    /**
     * 渠道名称
     */
    @ApiModelProperty("渠道名称")
    @TableField(exist = false)
    @Excel(name = "渠道名称")
    private String channelName;

    /**
     * 渠道编码
     */
    @ApiModelProperty("渠道编码")
    @TableField(exist = false)
    @Excel(name = "渠道编码")
    private String channelCode;

    /**
     * 用户额外数据
     */
    @ApiModelProperty("用户额外数据")
    @TableField(exist = false)
    private List<UserExt> userExtList;

    /**
     * 关注等级 0-10级
     */
    @ApiModelProperty("关注状态:0-10")
    @TableField("follow_status")
    @Excel(name = "关注等级", readConverterExp = "0=0级,1=1级,2=2级,3=3级,4=4级,5=5级,6=6级,7=7级,8=8级,9=9级,10=10级")
    private Integer followStatus;

    /**
     * 是否设置过杀率:1是;0否
     */
    @ApiModelProperty("是否设置过杀率:1是;0否")
    @TableField("has_set_rtp")
    @Excel(name = "是否设置过杀率", readConverterExp = "1=是,0=否")
    private Integer hasSetRtp;

    /**
     * 机器人状态(-1 未知异常, 0 正常, 1 tg账号被删除, 2 bot被用户拉黑)
     */
    @ApiModelProperty("机器人状态(-1 未知异常, 0 正常, 1 tg账号被删除, 2 bot被用户拉黑)")
    @TableField("bot_status")
    @Excel(name = "机器人状态", readConverterExp = "-1=未知异常,0=正常,1=tg账号被删除,2=bot被用户拉黑")
    private Integer botStatus;

    /**
     * 商户标签（来自 t_user_merchant_tag.merchant_tag_codes；不入库 t_user）
     */
    @ApiModelProperty("商户标签，逗号分隔（查询时由标签表拼接）")
    @TableField(exist = false)
    @Excel(name = "商户标签")
    private String merchantTagCodes;

    /**
     * 打码量
     */
    @ApiModelProperty("打码量")
    @TableField(exist = false)
    @Excel(name = "打码量", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal cumulativeBetVolume;

    /**
     * 彩金
     */
    @ApiModelProperty("彩金")
    @TableField(exist = false)
    @Excel(name = "彩金", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal bonusAmount;

    /**
     * 累计充值
     */
    @ApiModelProperty("累计充值")
    @TableField(exist = false)
    @Excel(name = "累计充值", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal cumulativeRecharge;

    /**
     * 用户余额
     */
    @ApiModelProperty("用户余额")
    @TableField(exist = false)
    @Excel(name = "用户余额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal usdtBalanceAmount;

    /**
     * 客损[计算公式 等于 输赢 加 所有送的金额 减 手动下分(扣除积分)]
     */
    @ApiModelProperty("客损[计算公式 等于 输赢 加 所有送的金额 减 手动下分(扣除积分)]")
    @TableField(exist = false)
    @Excel(name = "客损", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal customerLossAmount;

    /**
     * 累计提现金额
     */
    @ApiModelProperty("累计提现金额")
    @TableField(exist = false)
    @Excel(name = "累计提现金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal totalWithdrawAmount;

    /**
     * 用户的茶品code
     */
    @ApiModelProperty("用户的茶品code")
    @TableField(exist = false)
    private String productId;
    /**
     * 0不是新人 1 是新人
     */
    @ApiModelProperty("0不是新人 1 是新人")
    @TableField("is_new")
    private Integer isNew;

    /**
     * 搜索日期
     */
    @ApiModelProperty("搜索日期")
    @TableField(exist = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate searchTimes;

    /**
     * 搜索日期区间(yyyy-MM-dd)
     */
    @ApiModelProperty("搜索日期区间(yyyy-MM-dd)")
    @TableField(exist = false)
    private String[] searchTimesArr;

    /**
     * 未完成打码量
     */
    @ApiModelProperty("未完成打码量")
    @TableField(exist = false)
    private BigDecimal unfinishedWagerAmount;

    /**
     * 用户ID集合
     */
    @ApiModelProperty("用户ID集合")
    @TableField(exist = false)
    private List<Long> userIdList;

    /**
     * 关联web账号
     */
    @ApiModelProperty("关联web账号")
    @TableField(exist = false)
    @Excel(name = "关联web账号")
    private String webAccount;

    /**
     * 浏览器指纹-设备码
     */
    @ApiModelProperty("浏览器指纹-设备码")
    @TableField(exist = false)
    private String fingerprint;

    /**
     * 累计已领取反水金额:
     */
    @ApiModelProperty("累计已领取反水金额:")
    @TableField(exist = false)
    private BigDecimal alreadyRebateAmount;

    /**
     * 累计已领取外水金额:
     */
    @ApiModelProperty("累计已领取外水金额:")
    @TableField(exist = false)
    private BigDecimal alreadyReturnCommissionAmount;

    /**
     * 充提差 (累加充值金额-累计提现金额)
     */
    @ApiModelProperty("充提差")
    @TableField(exist = false)
    private BigDecimal difference;
}
