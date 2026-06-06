package com.gp.common.mybatisplus.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgencyDto implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "代理id")
    private Long agentId;

    @ApiModelProperty(value = "0:全部 1:今天")
    private Integer type;
    @ApiModelProperty(value = "用户名或者用户id 这个条件只有搜索下级用户的时候有用")
    private String userName;

    @ApiModelProperty(value = "查询时间", example = "2025-01-02",  dataType = "string")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date queryTime;

    /**
     * 用户id
     */
    @ApiModelProperty("用户id")
    private Long userId;

}
