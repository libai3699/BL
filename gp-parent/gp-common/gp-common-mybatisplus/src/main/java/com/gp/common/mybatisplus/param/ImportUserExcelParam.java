package com.gp.common.mybatisplus.param;

import com.gp.common.base.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@ApiModel("导入用户列表参数")
@Data
public class ImportUserExcelParam implements Serializable {


    /**
     * 飞机名
     */
    @ApiModelProperty("账号或者飞机id")
    @Excel(name = "账号")
    private String username;

    /**
     * 飞机名
     */
    @ApiModelProperty("飞机名称")
    @Excel(name = "飞机名")
    private String userTgName;

    /**
     * 提款密码
     */
    @ApiModelProperty("支付密码(6位数字)")
    @Excel(name = "提款密码")
    private String payPassword;


    /**
     * password
     */
    @ApiModelProperty("密码")
    @Excel(name = "password")
    private String password;

    /**
     * 余额
     */
    @ApiModelProperty("余额")
    @Excel(name = "余额")
    private BigDecimal balance;

    /**
     * 上级
     */
    @ApiModelProperty(value = "上级")
    @Excel(name = "上级")
    private String superUserEmail;


}
