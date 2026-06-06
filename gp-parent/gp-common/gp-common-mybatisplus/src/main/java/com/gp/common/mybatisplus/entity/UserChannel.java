package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 渠道配置表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_user_channel")
public class UserChannel {
    /**
     * 渠道配置id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 渠道id
     */
    @TableField(value = "channel_id")
    private Long channelId;

    /**
     * 上级用户id
     */
    @TableField(value = "pid")
    private Long pid;

    /**
     * 上级用户id列表
     */
    @TableField(value = "p_path")
    private String pPath;

    /**
     * 上级返佣(电子)
     */
    @TableField(value = "super_user_rebate_1")
    private BigDecimal superUserRebate1;

    /**
     * 上级返佣(体育)
     */
    @TableField(value = "super_user_rebate_2")
    private BigDecimal superUserRebate2;

    /**
     * 上级返佣(视讯)
     */
    @TableField(value = "super_user_rebate_3")
    private BigDecimal superUserRebate3;

    /**
     * 上级返佣(彩票)
     */
    @TableField(value = "super_user_rebate_4")
    private BigDecimal superUserRebate4;

    /**
     * 上级返佣(棋牌)
     */
    @TableField(value = "super_user_rebate_5")
    private BigDecimal superUserRebate5;

    /**
     * 上级返佣(区块链)
     */
    @TableField(value = "super_user_rebate_6")
    private BigDecimal superUserRebate6;

    /**
     * 上级返佣(捕鱼)
     */
    @TableField(value = "super_user_rebate_7")
    private BigDecimal superUserRebate7;

    /**
     * 上级返佣(宾果)
     */
    @TableField(value = "super_user_rebate_8")
    private BigDecimal superUserRebate8;

    /**
     * 上级返佣(弹珠)
     */
    @TableField(value = "super_user_rebate_9")
    private BigDecimal superUserRebate9;

    /**
     * 分红开关(0 关闭, 1 开启)
     */
    @TableField(value = "dividend_status")
    private Byte dividendStatus;

    /**
     * 分红比例
     */
    @TableField(value = "dividend_rebate")
    private BigDecimal dividendRebate;

    /**
     * 创建人
     */
    @TableField(value = "create_by")
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 修改人
     */
    @TableField(value = "update_by")
    private String updateBy;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private Date updateTime;
}