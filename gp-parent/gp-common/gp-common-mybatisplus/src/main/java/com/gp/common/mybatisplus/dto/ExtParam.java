package com.gp.common.mybatisplus.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author axing
 * @version 1.0
 * @date 2023/11/22/022 12:11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtParam implements Serializable {

    private static final long serialVersionUID = 1L;
    @ApiModelProperty("传入的key")
    private String key;
    @ApiModelProperty("页面显示的名称")
    private String label;
    @ApiModelProperty("type 默认是 text")
    private String type = "text";
    @ApiModelProperty("是否必填 0 否, 1 是")
    private Integer required;

    @ApiModelProperty("type 默认是 text")
    private List<ArrMap> arr;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArrMap {
        @ApiModelProperty("label")
        private String label;
        @ApiModelProperty("value")
        private String value;
        @ApiModelProperty("开始金额")
        private BigDecimal startMoney;
        @ApiModelProperty("结束金额")
        private BigDecimal endMoney;

        @ApiModelProperty("1 跳转 0 不跳")
        private Integer jump;
        @ApiModelProperty("最低可用等级，0表示所有等级可用")
        private Integer minLevel;
        @ApiModelProperty("该选项的子字段配置")
        private List<ExtParam> subFields;
    }
}
