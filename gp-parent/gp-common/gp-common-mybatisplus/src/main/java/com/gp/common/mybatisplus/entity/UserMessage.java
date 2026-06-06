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

import java.util.Date;

/**
 * 用户消息表
 */
@ApiModel(description = "用户消息表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_user_message")
public class UserMessage extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:TUserMessage";

    /** id */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Long id;

    /** 用户id */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /** 用户飞机id */
    @ApiModelProperty("用户飞机id")
    @TableField("user_tg_id")
    @Excel(name = "用户飞机id")
    private Long userTgId;

    /** 关联订单号 */
    @ApiModelProperty("关联订单号")
    @TableField("link_order_no")
    @Excel(name = "关联订单号")
    private String linkOrderNo;

    /** 订单类型(1 人工上下分订单, 2 充值订单, 3 提现订单, 4 转账订单, 5 红包发送订单, 6 红包接收订单) */
    @ApiModelProperty("订单类型")
    @TableField("order_type")
    @Excel(name = "订单类型")
    private Integer orderType;


    /** 用户名称 */
    @ApiModelProperty("用户名称")
    @TableField(exist = false)
    @Excel(name = "用户名称")
    private String username;



    /** 标题 */
    @ApiModelProperty("标题")
    @TableField("title")
    @Excel(name = "标题")
    private String title;

    /** 内容 */
    @ApiModelProperty("内容")
    @TableField("content")
    @Excel(name = "内容")
    private String content;

    /** 读取状态(0 未读, 1 已读) */
    @ApiModelProperty("读取状态(0 未读, 1 已读)")
    @TableField("read_status")
    @Excel(name = "读取状态(0未读,1已读)")
    private Integer readStatus;

    /** 读取时间 */
    @ApiModelProperty("读取时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("read_time")
    @Excel(name = "读取时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;

    @ApiModelProperty("读取时间数组")
    @TableField(exist = false)
    private String[] readTimes;

    /** 创建时间 */
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("创建时间数组")
    @TableField(exist = false)
    private String[] createTimes;

}
