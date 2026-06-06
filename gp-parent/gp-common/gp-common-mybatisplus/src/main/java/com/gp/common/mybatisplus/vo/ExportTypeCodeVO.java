package com.gp.common.mybatisplus.vo;

import com.gp.common.base.excel.annotation.Excel;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class ExportTypeCodeVO {

    /**
     * 用户ID
     */
    @Excel(name = "用户ID", cellType = Excel.ColumnType.NUMERIC)
    private Long userId;

    /***
     * 飞机名称
     */
    @Excel(name = "飞机名称")
    private String userTgName;

    /**
     * 游戏类型
     */
    @Excel(name = "游戏类型")
    private String gameTypeCode;

    /**
     * 投注金额
     */
    @Excel(name = "投注金额", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal betAmount;

    /**
     * 盈亏
     */
    @Excel(name = "盈亏", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal winLoss;

    /**
     * 投注笔数
     */
    @Excel(name = "投注笔数", cellType = Excel.ColumnType.NUMERIC)
    private Integer betNum;

    /**
     * 打码量
     */
    @Excel(name = "打码量", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal codeAmount;

    /**
     * 派彩
     */
    @Excel(name = "派彩", cellType = Excel.ColumnType.NUMERIC)
    private BigDecimal win;
}
