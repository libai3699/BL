package com.gp.common.mybatisplus.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class QueryUserParam implements Serializable {

    @ApiModelProperty("第几页")
    private Integer pageNum;

    @ApiModelProperty("每页显示多少条数据")
    private Integer pageSize;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private Long userId;

    /**
     * 飞机名称
     */
    @ApiModelProperty("飞机名称")
    private String userTgName;

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("ip")
    private String ip;
    /**
     * 飞机用户名
     */
    @ApiModelProperty("飞机用户名")
    private String userTgUsername;

    /**
     * 飞机id
     */
    @ApiModelProperty("飞机id")
    private Long userTgId;

    /**
     * 语言
     */
    @ApiModelProperty("语言")
    private String lanKey;

    /**
     * 关注状态:0无;1关注;2特别关注
     */
    @ApiModelProperty("关注状态:0无;1关注;2特别关注")
    private Integer followStatus;

    /**
     * 是否设置过杀率:1是;0否
     */
    @ApiModelProperty("是否设置过杀率:1是;0否")
    private Integer hasSetRtp;

    /**
     * 提现冻结状态（0.未冻结 1.冻结）
     */
    @ApiModelProperty("提现冻结状态（0.未冻结 1.冻结）")
    private Integer freezeStatus;

    @ApiModelProperty("用户冻结状态（0.未冻结 1.冻结）")
    private Integer handleFreezeStatus;

    @ApiModelProperty("登录时间数组")
    private String[] loginTimes;

    @ApiModelProperty("创建时间数组")
    private String[] createTimes;

    /**
     * 用户级别
     */
    @ApiModelProperty("用户级别")
    private Integer level;

    /**
     * 渠道id,没有则为0
     */
    @ApiModelProperty("渠道id,没有则为0")
    private Long channelId;

    /**
     * 上级用户id
     */
    @ApiModelProperty("上级用户id")
    private Long superUserId;

    /**
     * 上级飞机id
     */
    @ApiModelProperty("上级飞机id")
    private Long superUserTgId;

    /**
     * 最小打码量
     */
    @ApiModelProperty("最小打码量")
    private BigDecimal minCumulativeBetVolume;

    /**
     * 最大打码量
     */
    @ApiModelProperty("最大打码量")
    private BigDecimal maxCumulativeBetVolume;

    /**
     * 最小彩金
     */
    @ApiModelProperty("最小彩金")
    private BigDecimal minBonusAmount;

    /**
     * 最大彩金
     */
    @ApiModelProperty("最大彩金")
    private BigDecimal maxBonusAmount;

    /**
     * 最小累计充值
     */
    @ApiModelProperty("最小累计充值")
    private BigDecimal minCumulativeRecharge;

    /**
     * 最大累计充值
     */
    @ApiModelProperty("最大累计充值")
    private BigDecimal maxCumulativeRecharge;

    /**
     * 最小用户余额
     */
    @ApiModelProperty("最小用户余额")
    private BigDecimal minUsdtBalanceAmount;

    /**
     * 最大用户余额
     */
    @ApiModelProperty("最大用户余额")
    private BigDecimal maxUsdtBalanceAmount;

    @ApiModelProperty("排序字段, cumulativeBetVolume(累计打码量),bonusAmount(彩金),cumulativeRecharge(累计充值),usdtBalanceAmount(余额)")
    private String orderByColumn;

    @ApiModelProperty("排序方式, asc(升序),desc(降序)")
    private String isAsc;

    /**
     * 来源(0是飞机,1是H5)
     */
    @ApiModelProperty("来源(0是飞机,1是H5)")
    private Integer source;

    /**
     * 机器人状态(-1 未知异常, 0 正常, 1 tg账号被删除, 2 bot被用户拉黑)
     */
    @ApiModelProperty("机器人状态(-1 未知异常, 0 正常, 1 tg账号被删除, 2 bot被用户拉黑)")
    private Integer botStatus;

    /**
     * 浏览器指纹-设备码
     */
    @ApiModelProperty("浏览器指纹-设备码")
    private String fingerprint;
}
