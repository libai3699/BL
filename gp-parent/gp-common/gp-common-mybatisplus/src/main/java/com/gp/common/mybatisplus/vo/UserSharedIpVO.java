package com.gp.common.mybatisplus.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel("用户共用历史IP列表")
public class UserSharedIpVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户id")
    private Long userId;

    @ApiModelProperty("飞机id")
    private Long userTgId;

    @ApiModelProperty("飞机名称")
    private String userTgName;

    @ApiModelProperty("飞机用户名")
    private String userTgUsername;

    @ApiModelProperty("当前用户ip")
    private String currentIp;

    @ApiModelProperty("当前ip地址")
    private String currentIpAddr;

    @ApiModelProperty("命中的历史登录次数")
    private Long loginCount;

    @ApiModelProperty("最近命中登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastLoginTime;
}
