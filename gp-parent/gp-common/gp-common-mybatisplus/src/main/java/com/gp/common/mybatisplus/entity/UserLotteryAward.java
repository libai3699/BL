package com.gp.common.mybatisplus.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gp.common.mybatisplus.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import com.gp.common.base.excel.annotation.Excel;

/**
 * 用户彩票特殊活动记录对象 t_user_lottery_award
 *
 * @author axing
 * @date 2025-09-17
 */
@ApiModel("用户彩票特殊活动记录")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_lottery_award")
public class UserLotteryAward extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:UserLotteryAward";

    /** 主键id */
    @ApiModelProperty("主键id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键id")
     private Long id;

    /** 用户id */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /** 渠道id,没有则为0 */
    @ApiModelProperty("渠道id,没有则为0")
    @TableField("channel_id")
    @Excel(name = "渠道id,没有则为0")
    private Long channelId;

    /** 0充值1亏损 */
    @ApiModelProperty("0充值1亏损")
    @TableField("type")
    @Excel(name = "0充值1亏损")
    private Integer type;

    /** 订单号 */
    @ApiModelProperty("订单号")
    @TableField("order_no")
    @Excel(name = "订单号")
    private String orderNo;

    /** 金额 */
    @ApiModelProperty("金额")
    @TableField("award")
    @Excel(name = "金额")
    private BigDecimal award;

    /** 比例 */
    @ApiModelProperty("比例")
    @TableField("ratio")
    @Excel(name = "比例")
    private BigDecimal ratio;

    /** 来源于 */
    @ApiModelProperty("来源于")
    @TableField("from_user_id")
    @Excel(name = "来源于")
    private Long fromUserId;

    /** 来源金额 */
    @ApiModelProperty("来源金额")
    @TableField("from_amount")
    @Excel(name = "来源金额")
    private BigDecimal fromAmount;

    /** 创建时间 */
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "创建时间")
    private Date createTime;
    @ApiModelProperty("创建时间数组")
    @TableField(exist = false)
    private String[] createTimes;

    /** 创建人 */
    @ApiModelProperty("创建人")
    @TableField("create_by")
    @Excel(name = "创建人")
    private String createBy;

    /** 更新时间 */
    @ApiModelProperty("更新时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    @Excel(name = "更新时间")
    private Date updateTime;
    @ApiModelProperty("更新时间数组")
    @TableField(exist = false)
    private String[] updateTimes;

    /** 更新人 */
    @ApiModelProperty("更新人")
    @TableField("update_by")
    @Excel(name = "更新人")
    private String updateBy;


}
