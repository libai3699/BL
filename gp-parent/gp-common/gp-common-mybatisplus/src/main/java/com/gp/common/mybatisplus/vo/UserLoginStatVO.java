package com.gp.common.mybatisplus.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录统计VO
 *
 * @author axing
 * @date 2026-04-10
 */
@ApiModel("用户登录统计VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginStatVO {
    
    /**
     * IP地址或设备类型
     */
    @ApiModelProperty("IP地址或设备类型")
    private String groupKey;
    
    /**
     * 用户数量
     */
    @ApiModelProperty("用户数量")
    private Integer userCount;
    
    /**
     * 所有用户ID，逗号分隔
     */
    @ApiModelProperty("所有用户ID，逗号分隔")
    private String userIds;
    
    /**
     * 所有用户姓名，逗号分隔
     */
    @ApiModelProperty("所有用户姓名，逗号分隔")
    private String userNames;

    /**
     * 设备类型(1-androidTg,2-iosTg,3-pcTg, 4-macTg, 5-pc , 6-h5)
     */
    @ApiModelProperty("设备类型(1-androidTg,2-iosTg,3-pcTg, 4-macTg, 5-pc , 6-h5),逗号分隔")
    private String devices;
}
