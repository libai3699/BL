package com.gp.common.mybatisplus.dto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgencyDetailsDto implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "userId")
    @NotNull(message = "userId cannot be null")
    private Long userId;


}
