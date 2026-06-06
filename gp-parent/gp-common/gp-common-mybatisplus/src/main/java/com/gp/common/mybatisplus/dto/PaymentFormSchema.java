package com.gp.common.mybatisplus.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentFormSchema {
    @ApiModelProperty("参数列表")
    private List<FormField> fields;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FormField {
        @ApiModelProperty("标签key")
        private String key;
        @ApiModelProperty("标签")
        private String label;
        @ApiModelProperty("类型 text, number")
        private String type;
        @ApiModelProperty("是否必填")
        private Boolean required;
        @ApiModelProperty("最大长度")
        private Integer maxLength;
        @ApiModelProperty("校验格式")
        private String pattern;
    }

    public static void validateChannelData(
            PaymentFormSchema schema,
            Map<String, Object> channelData
    ) {
        for (FormField field : schema.getFields()) {

            Object value = channelData.get(field.getKey());

            if (Boolean.TRUE.equals(field.getRequired())) {
                if (value == null || value.toString().trim().isEmpty()) {
                    throw new IllegalArgumentException(field.getLabel() + "不能为空");
                }
            }

            if (value == null) {
                continue;
            }

            String str = value.toString();

            // 长度校验
            if (field.getMaxLength() != null &&
                    str.length() > field.getMaxLength()) {
                throw new IllegalArgumentException(
                        field.getLabel() + "长度不能超过" + field.getMaxLength()
                );
            }

            // 正则校验(select类型的pattern是选项数据,不做正则校验)
            if (!"select".equalsIgnoreCase(field.getType())
                    && StringUtils.isNotBlank(field.getPattern())
                    && !str.matches(field.getPattern())) {
                throw new IllegalArgumentException(
                        field.getLabel() + "格式不正确"
                );
            }
        }
    }


}
