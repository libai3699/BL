package com.gp.common.mybatisplus.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * @author axing
 * @version 1.0
 * @date 2023/11/22/022 12:11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgencyBetDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "查询用户id")
    @NotNull(message = "查询userId 不能为空")
    private Long queryUserId;


    @ApiModelProperty(value = "查询时间")
    @NotNull(message = "查询时间 不能为空")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Date queryTime;
}
