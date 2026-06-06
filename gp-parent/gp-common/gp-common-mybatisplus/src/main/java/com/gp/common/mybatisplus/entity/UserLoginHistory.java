package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.common.base.excel.annotation.Excel;
import com.gp.common.mybatisplus.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 用户登录历史对象 t_user_login_history
 *
 * @author axing
 * @date 2025-01-22
 */
@ApiModel("用户登录历史")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_login_history")
public class UserLoginHistory extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:UserLoginHistory";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    @TableField("user_id")
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

    /**
     * (0注册1登录)
     */
    @ApiModelProperty("(0注册1登录)")
    @TableField("type")
    @Excel(name = "(0注册1登录)")
    private Integer type;

    /**
     * 设备类型(1-androidTg,2-iosTg,3-pcTg, 4-macTg, 5-pc , 6-h5)
     */
    @ApiModelProperty("设备类型(1-androidTg,2-iosTg,3-pcTg, 4-macTg, 5-pc , 6-h5)")
    @TableField("device")
    @Excel(name = "设备类型(1-androidTg,2-iosTg,3-pcTg, 4-macTg, 5-pc , 6-h5)")
    private Integer device;

    /**
     * 设备型号
     */
    @ApiModelProperty("设备型号")
    @TableField("device_model")
    @Excel(name = "设备型号")
    private String deviceModel;

    /**
     * 浏览器指纹-设备码
     */
    @ApiModelProperty("浏览器指纹-设备码")
    @TableField("fingerprint")
    @Excel(name = "设备码")
    private String fingerprint;

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

}
