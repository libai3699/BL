package com.gp.common.mybatisplus.vo;

import com.gp.common.base.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel("下载模版")
@Data
public class PersonExportVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @ApiModelProperty("userId")
    @Excel(name = "用户ID")
    private Long userId;

}
