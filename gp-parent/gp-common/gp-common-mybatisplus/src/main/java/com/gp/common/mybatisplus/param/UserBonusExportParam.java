package com.gp.common.mybatisplus.param;

import com.gp.common.base.excel.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 用户彩金导出模板
 * 用于批量导入/导出用户赠送彩金
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBonusExportParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @Excel(name = "用户ID")
    private Long userId;

    /**
     * 彩金金额
     */
    @Excel(name = "彩金金额")
    private BigDecimal bonusAmount;
}
