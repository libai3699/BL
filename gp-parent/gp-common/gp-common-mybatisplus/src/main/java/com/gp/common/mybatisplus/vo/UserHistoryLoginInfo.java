package com.gp.common.mybatisplus.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel("用户历史登录信息")
public class UserHistoryLoginInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("飞机ID")
    private Long userTgId;

    @ApiModelProperty("飞机名称")
    private String userTgName;

    @ApiModelProperty("飞机用户名")
    private String userTgUsername;

    @ApiModelProperty("IP列表(去重)")
    private List<String> ipList;

    @ApiModelProperty("IP列表字符串")
    private String ipListStr;
}
