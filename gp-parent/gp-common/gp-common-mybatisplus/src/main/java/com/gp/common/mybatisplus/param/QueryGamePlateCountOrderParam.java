package com.gp.common.mybatisplus.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QueryGamePlateCountOrderParam implements Serializable {

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 币种id*
     */
    private Integer currencyId;

    /**
     * 游戏厂商编码
     */
    private String plateCode;

    /**
     * 游戏厂商编码List
     */
    private List<String> plateCodeList;

    /**
     * 日查询 - 日期(年月日)  2024-05-31
     * 月查询 - 日期(年月)  2024-05
     */
    private String dayStr;

}
