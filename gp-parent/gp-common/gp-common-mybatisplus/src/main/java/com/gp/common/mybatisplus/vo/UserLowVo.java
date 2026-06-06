package com.gp.common.mybatisplus.vo;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    * 用户表
    */
@ApiModel(description="下级列表")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLowVo {

    @ApiModelProperty("头像")
    private String userAvatar;

    /** 飞机名称 */
    @ApiModelProperty("飞机名称")
    private String userTgName;

    /** 飞机用户名 */
    @ApiModelProperty("飞机用户名")
    private String userTgUsername;
    @ApiModelProperty("用户id")
    @Excel(name = "用户id")
    private Long userId;
    @ApiModelProperty("飞机id")
    @TableField("user_tg_id")
    @Excel(name = "飞机id")
    private Long userTgId;


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
    @ApiModelProperty("输赢")
    private BigDecimal winLoss;
}
