package com.gp.common.mybatisplus.vo;

import com.gp.common.base.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel("首页待返水导出")
public class HomeWaitRebateAmountExportVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    @Excel(name = "用户ID", cellType = Excel.ColumnType.NUMERIC)
    private Long userId;

    @ApiModelProperty("飞机名称")
    @Excel(name = "飞机名称")
    private String userTgName;

    @ApiModelProperty("待返水金额")
    @Excel(name = "待返水金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal waitRebateAmount;
}
