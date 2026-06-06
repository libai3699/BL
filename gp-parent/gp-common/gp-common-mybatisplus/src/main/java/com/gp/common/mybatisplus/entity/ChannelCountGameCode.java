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
import java.util.Date;

/**
 * 渠道游戏类型打码量统计
 * 对象 t_channel_count_game_code
 *
 * @author axing
 * @date 2025-06-23
 */
@ApiModel("渠道游戏类型打码量统计 ")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_channel_count_game_code")
public class ChannelCountGameCode extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:ChannelCountGameCode";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Long id;

    /**
     * 渠道id,没有则为0
     */
    @ApiModelProperty("渠道id,没有则为0")
    @TableField("channel_id")
    @Excel(name = "渠道id,没有则为0")
    private Long channelId;

    /**
     * 日期(年月日)
     */
    @ApiModelProperty("日期(年月日)")
    @TableField("day_str")
    @Excel(name = "日期(年月日)")
    private String dayStr;

    /**
     * 电子打码量
     */
    @ApiModelProperty("电子打码量")
    @TableField("game_type_code1")
    @Excel(name = "电子打码量")
    private BigDecimal gameTypeCode1 = BigDecimal.ZERO;

    /**
     * 体育打码量
     */
    @ApiModelProperty("体育打码量")
    @TableField("game_type_code2")
    @Excel(name = "体育打码量")
    private BigDecimal gameTypeCode2 = BigDecimal.ZERO;

    /**
     * 视讯打码量
     */
    @ApiModelProperty("视讯打码量")
    @TableField("game_type_code3")
    @Excel(name = "视讯打码量")
    private BigDecimal gameTypeCode3 = BigDecimal.ZERO;

    /**
     * 彩票打码量
     */
    @ApiModelProperty("彩票打码量")
    @TableField("game_type_code4")
    @Excel(name = "彩票打码量")
    private BigDecimal gameTypeCode4 = BigDecimal.ZERO;

    /**
     * 棋牌打码量
     */
    @ApiModelProperty("棋牌打码量")
    @TableField("game_type_code5")
    @Excel(name = "棋牌打码量")
    private BigDecimal gameTypeCode5 = BigDecimal.ZERO;

    /**
     * 区块链打码量
     */
    @ApiModelProperty("区块链打码量")
    @TableField("game_type_code6")
    @Excel(name = "区块链打码量")
    private BigDecimal gameTypeCode6 = BigDecimal.ZERO;

    /**
     * 捕鱼打码量
     */
    @ApiModelProperty("捕鱼打码量")
    @TableField("game_type_code7")
    @Excel(name = "捕鱼打码量")
    private BigDecimal gameTypeCode7 = BigDecimal.ZERO;

    /**
     * 宾果打码量
     */
    @ApiModelProperty("宾果打码量")
    @TableField("game_type_code8")
    @Excel(name = "宾果打码量")
    private BigDecimal gameTypeCode8 = BigDecimal.ZERO;

    /**
     * 弹珠打码量
     */
    @ApiModelProperty("弹珠打码量")
    @TableField("game_type_code9")
    @Excel(name = "弹珠打码量")
    private BigDecimal gameTypeCode9 = BigDecimal.ZERO;

    // ==================== 投注额 ====================

    @ApiModelProperty("电子投注额")
    @TableField("game_type_bet1")
    private BigDecimal gameTypeBet1 = BigDecimal.ZERO;

    @ApiModelProperty("体育投注额")
    @TableField("game_type_bet2")
    private BigDecimal gameTypeBet2 = BigDecimal.ZERO;

    @ApiModelProperty("视讯投注额")
    @TableField("game_type_bet3")
    private BigDecimal gameTypeBet3 = BigDecimal.ZERO;

    @ApiModelProperty("彩票投注额")
    @TableField("game_type_bet4")
    private BigDecimal gameTypeBet4 = BigDecimal.ZERO;

    @ApiModelProperty("棋牌投注额")
    @TableField("game_type_bet5")
    private BigDecimal gameTypeBet5 = BigDecimal.ZERO;

    @ApiModelProperty("区块链投注额")
    @TableField("game_type_bet6")
    private BigDecimal gameTypeBet6 = BigDecimal.ZERO;

    @ApiModelProperty("捕鱼投注额")
    @TableField("game_type_bet7")
    private BigDecimal gameTypeBet7 = BigDecimal.ZERO;

    @ApiModelProperty("宾果投注额")
    @TableField("game_type_bet8")
    private BigDecimal gameTypeBet8 = BigDecimal.ZERO;

    @ApiModelProperty("弹珠投注额")
    @TableField("game_type_bet9")
    private BigDecimal gameTypeBet9 = BigDecimal.ZERO;

    // ==================== 派彩额 ====================

    @ApiModelProperty("电子派彩额")
    @TableField("game_type_settle1")
    private BigDecimal gameTypeSettle1 = BigDecimal.ZERO;

    @ApiModelProperty("体育派彩额")
    @TableField("game_type_settle2")
    private BigDecimal gameTypeSettle2 = BigDecimal.ZERO;

    @ApiModelProperty("视讯派彩额")
    @TableField("game_type_settle3")
    private BigDecimal gameTypeSettle3 = BigDecimal.ZERO;

    @ApiModelProperty("彩票派彩额")
    @TableField("game_type_settle4")
    private BigDecimal gameTypeSettle4 = BigDecimal.ZERO;

    @ApiModelProperty("棋牌派彩额")
    @TableField("game_type_settle5")
    private BigDecimal gameTypeSettle5 = BigDecimal.ZERO;

    @ApiModelProperty("区块链派彩额")
    @TableField("game_type_settle6")
    private BigDecimal gameTypeSettle6 = BigDecimal.ZERO;

    @ApiModelProperty("捕鱼派彩额")
    @TableField("game_type_settle7")
    private BigDecimal gameTypeSettle7 = BigDecimal.ZERO;

    @ApiModelProperty("宾果派彩额")
    @TableField("game_type_settle8")
    private BigDecimal gameTypeSettle8 = BigDecimal.ZERO;

    @ApiModelProperty("弹珠派彩额")
    @TableField("game_type_settle9")
    private BigDecimal gameTypeSettle9 = BigDecimal.ZERO;

    // ==================== 游戏输赢 ====================

    @ApiModelProperty("电子游戏输赢")
    @TableField("game_type_win_loss1")
    private BigDecimal gameTypeWinLoss1 = BigDecimal.ZERO;

    @ApiModelProperty("体育游戏输赢")
    @TableField("game_type_win_loss2")
    private BigDecimal gameTypeWinLoss2 = BigDecimal.ZERO;

    @ApiModelProperty("视讯游戏输赢")
    @TableField("game_type_win_loss3")
    private BigDecimal gameTypeWinLoss3 = BigDecimal.ZERO;

    @ApiModelProperty("彩票游戏输赢")
    @TableField("game_type_win_loss4")
    private BigDecimal gameTypeWinLoss4 = BigDecimal.ZERO;

    @ApiModelProperty("棋牌游戏输赢")
    @TableField("game_type_win_loss5")
    private BigDecimal gameTypeWinLoss5 = BigDecimal.ZERO;

    @ApiModelProperty("区块链游戏输赢")
    @TableField("game_type_win_loss6")
    private BigDecimal gameTypeWinLoss6 = BigDecimal.ZERO;

    @ApiModelProperty("捕鱼游戏输赢")
    @TableField("game_type_win_loss7")
    private BigDecimal gameTypeWinLoss7 = BigDecimal.ZERO;

    @ApiModelProperty("宾果游戏输赢")
    @TableField("game_type_win_loss8")
    private BigDecimal gameTypeWinLoss8 = BigDecimal.ZERO;

    @ApiModelProperty("弹珠游戏输赢")
    @TableField("game_type_win_loss9")
    private BigDecimal gameTypeWinLoss9 = BigDecimal.ZERO;

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
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    @Excel(name = "更新时间")
    private Date updateTime;
    @ApiModelProperty("更新时间数组")
    @TableField(exist = false)
    private String[] updateTimes;

}
