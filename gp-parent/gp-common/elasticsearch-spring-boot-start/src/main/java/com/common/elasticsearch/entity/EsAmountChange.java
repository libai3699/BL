package com.common.elasticsearch.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EsAmountChange extends ESBaseEntity {
    @Id
    @Field(type = FieldType.Long)
    private Long id;
    /** 用户ID */
    @Field(type = FieldType.Long)
    private Long userId;

    /** tg用户ID */
    @Field(type = FieldType.Long)
    private Long tgUserId;

    /**
     * 用户上级ID
     */
    @Field(type = FieldType.Long)
    private Long superUserId;

    /**
     * 用户渠道ID
     */
    @Field(type = FieldType.Long)
    private Long channelId;

    /** 用户名称 */
    @Field(type = FieldType.Keyword)
    private String username;

    /**
     * 关联的订单号
     */
    @Field(type = FieldType.Keyword)
    private String icon;

    /**
     * currencyId
     */
    @Field(type = FieldType.Integer)
    private Integer currencyId;

    /**
     * 币种
     */
    @Field(type = FieldType.Keyword)
    private String currencyName;

    /**
     * 币种ID
     */
    @Field(type = FieldType.Integer)
    private Integer itemId;

    /**
     * 链名称
     */
    @Field(type = FieldType.Keyword)
    private String chainTag;

    /**
     * 币加连
     */
    @Field(type = FieldType.Keyword)
    private String itemName;


    /** 关联的订单号 */
    @Field(type = FieldType.Keyword)
    private String orderNo;

    @Field(type = FieldType.Integer)
    private Integer orderType;

    /** 地址类型(1 内部地址, 2 外部地址) */
    @Field(type = FieldType.Integer)
    private Integer exchangeType;

    /** 收支类型(1 收入, 2 支出) */
    @Field(type = FieldType.Integer)
    private Integer accountType;

    @Field(type = FieldType.Integer)
    private Integer type;


    /** 变更金额 */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100000000)
    private BigDecimal amount;

    /** 变更前金额 */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100000000)
    private BigDecimal oldAmount;

    /** 变更后金额 */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100000000)
    private BigDecimal newAmount;

    @Field(type = FieldType.Long)
    private Long signTime;

    @Field(type = FieldType.Keyword)
    private String sign;

    /** 备注 */
    @Field(type = FieldType.Keyword)
    private String remark;

    /** 操作人 */
    @Field(type = FieldType.Keyword)
    private String operator;

    /**
     * 版本号es同步使用
     */
    @ApiModelProperty("版本号es同步使用")
    @Transient   // Spring Data 标记，不会写入 ES
    private Long updateVersion;

    /** 变更时间 */
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Date createTime;

}
