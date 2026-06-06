package com.gp.common.mybatisplus.entity;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import com.gp.common.base.excel.annotation.Excel;
import com.gp.common.mybatisplus.base.BaseEntity;

/**
 * 客服对象 t_customer
 *
 * @author axing
 * @date 2024-07-15
 */
@ApiModel("客服")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_customer")
public class Customer extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:Customer";

    /** id */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
     private Integer id;

    /** 用户名 */
    @ApiModelProperty("用户名/手机号/外链")
    @TableField("username")
    @Excel(name = "用户名/手机号/外链")
    private String username;

    /** 名称 */
    @ApiModelProperty("名称")
    @TableField("nickname")
    private String nickname;

    /** 客服头像 */
    @ApiModelProperty("客服头像")
    @TableField("avatar")
    private String avatar;

    /** 客服地址类型(0 飞机, 1 whatapps, 2 外链) */
    @ApiModelProperty("客服地址类型(0 飞机, 1 whatapps, 2 外链)")
    @TableField("link_type")
    private Integer linkType;

    /** 客服类型(1 财务客服, 2 反馈客服) */
    @ApiModelProperty("客服类型(1 财务客服, 2 反馈客服 3代理客服)")
    @TableField("type")
    private Integer type;

    /** 语言的key */
    @ApiModelProperty("语言的key")
    @TableField("lan_key")
    @Excel(name = "语言的key")
    private String lanKey;

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


}
