package com.common.elasticsearch.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EsUserExtChange extends ESBaseEntity {

    @Id
    @Field(type = FieldType.Long)
    private Long id;

    /**
     * 用户额外属性类型
     */
    @Field(type = FieldType.Integer)
    private Integer extType;

    /**
     * 用户ID
     */
    @Field(type = FieldType.Long)
    private Long userId;

    /**
     * tg用户ID
     */
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

    /**
     * 关联的订单号
     */
    @Field(type = FieldType.Keyword)
    private String orderNo;

    /**
     * 订单类型(1=注单,2=反水返佣领取订单,3=转盘订单,4=活动订单,5=充值订单,6=人工上分下分订单,7=人工操作提现打码量订单,8=人工操作转盘次数订单,9=新人赠送,10=人工操作打码量订单,11=红包订单)
     * {@link BaseGameInfoCons.UserExtOrderType}
     */
    @Field(type = FieldType.Integer)
    private Integer orderType;

    /**
     * 收支类型(1 收入, 2 支出)
     */
    @Field(type = FieldType.Integer)
    private Integer accountType;

    /**
     * 帐变类型(1=用户返水,2=上级返佣,3=反水领取,4=返佣领取,5=转盘消耗,6=转盘增加,7=彩金增加,8=提现打码量增加,
     * 9=人工上分,10=人工下分,11=打码量增加,12=人工增加提现打码量,13=人工减少提现打码量,14=人工增加转盘次数,15=人工减少转盘次数,16=累计充值增加,17=人工增加打码量,18=人工减少打码量)
     * {@link BaseGameInfoCons.UserExtChangeType}
     */
    @Field(type = FieldType.Integer)
    private Integer type;

    /**
     * 变更金额
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100000000)
    private BigDecimal amount;

    /**
     * 变更前金额
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100000000)
    private BigDecimal oldAmount;

    /**
     * 变更后金额
     */
    @Field(type = FieldType.Scaled_Float, scalingFactor = 100000000)
    private BigDecimal newAmount;

    /**
     * 备注
     */
    @Field(type = FieldType.Keyword)
    private String remark;

    /**
     * 操作人
     */
    @Field(type = FieldType.Keyword)
    private String operator;

    /**
     * 签名时间
     */
    @Field(type = FieldType.Long)
    private Long signTime;

    /**
     * 签名
     */
    @Field(type = FieldType.Keyword)
    private String sign;

    /**
     * 变更时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    private Date createTime;

    /**
     * 版本号es同步使用
     */
    @ApiModelProperty("版本号es同步使用")
    @Transient   // Spring Data 标记，不会写入 ES
    private Long updateVersion;
}
