package com.gp.common.mybatisplus.entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.common.base.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
    * 用户表
    */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLowM {

    @ApiModelProperty("头像")
    private String userAvatar;

    /** 飞机名称 */
    @ApiModelProperty("飞机名称")
    private String userTgName;

    /** 飞机用户名 */
    @ApiModelProperty("飞机用户名")
    private String userTgUsername;
    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("飞机id")
    private Long userTgId;

    @ApiModelProperty("上级用户id")
    private Long superUserId;


    /** 创建时间 */
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


    @ApiModelProperty("充值")
    private BigDecimal rechargeAmount;
    @ApiModelProperty("提款")
    private BigDecimal withdrawAmount;
    @ApiModelProperty("投注")
    private BigDecimal betAmount;
    @ApiModelProperty("打码量")
    private BigDecimal codeAmount;
    @ApiModelProperty("输赢")
    private BigDecimal winLoss;
}
