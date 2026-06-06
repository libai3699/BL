package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.common.base.constant.BaseGameInfoCons;
import com.gp.common.mybatisplus.base.BaseEntity;
import com.gp.common.base.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户账变对象 t_user_ext_change
 *
 * @author axing
 * @date 2024-05-16
 */
@ApiModel("用户账变")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_user_ext_change")
public class UserExtChange extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:UserExtChange";

    /**
     * 用户额外属性账变id
     */
    @ApiModelProperty("用户额外属性账变id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "用户额外属性账变id")
    private Long id;

    /**
     * 用户额外属性类型
     */
    @ApiModelProperty("用户额外属性类型")
    @TableField("ext_type")
    @Excel(name = "用户额外属性类型")
    private Integer extType;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    @TableField("user_id")
    @Excel(name = "用户ID")
    private Long userId;

    /**
     * tg用户ID
     */
    @ApiModelProperty("tg用户ID")
    @TableField("tg_user_id")
    @Excel(name = "tg用户ID")
    private Long tgUserId;

    /**
     * 用户上级ID
     */
    @ApiModelProperty("用户上级ID")
    @TableField("super_user_id")
    @Excel(name = "用户上级ID", cellType = Excel.ColumnType.NUMERIC)
    private Long superUserId;


    /**
     * 用户渠道ID
     */
    @ApiModelProperty("渠道ID")
    @TableField("channel_id")
    @Excel(name = "渠道ID", cellType = Excel.ColumnType.NUMERIC)
    private Long channelId;

    /**
     * 关联的订单号
     */
    @ApiModelProperty("关联的订单号")
    @TableField("order_no")
    @Excel(name = "关联的订单号")
    private String orderNo;

    /**
     * 订单类型(1=注单,2=反水返佣领取订单,3=转盘订单,4=活动订单,5=充值订单,6=人工上分下分订单,7=人工操作提现打码量订单,8=人工操作转盘次数订单,9=新人赠送,10=人工操作打码量订单,11=红包订单)
     * {@link BaseGameInfoCons.UserExtOrderType}
     */
    @ApiModelProperty("订单类型1=注单;2=反水返佣领取订单;3=轉盤訂單;4=活動訂單;5=充值訂單;6=人工上分下分訂單;7=人工操作提现打码量訂單;8=人工操作轉盤次數訂單;9=新人贈送;10=人工操作打碼量訂單;11=紅包訂單;12=級差返佣;13=代理工資領取")
    @TableField("order_type")
    @Excel(name = "订单类型", readConverterExp = "1=注单;2=反水返佣领取订单;3=轉盤訂單;4=活動訂單;5=充值訂單;6=人工上分下分訂單;7=人工操作提现打码量訂單;8=人工操作轉盤次數訂單;9=新人贈送;10=人工操作打碼量訂單;11=紅包訂單;12=級差返佣;13=代理工資領取,14=转账订单")
    private Integer orderType;

    /**
     * 收支类型(1 收入, 2 支出)
     */
    @ApiModelProperty("收支类型(1 收入, 2 支出)")
    @TableField("account_type")
    @Excel(name = "收支类型", readConverterExp = "1=收入,2=支出")
    private Integer accountType;

    /**
     * 帐变类型(1=用户返水,2=上级返佣,3=反水领取,4=返佣领取,5=转盘消耗,6=转盘增加,7=彩金增加,8=提现打码量增加,
     * 9=人工上分,10=人工下分,11=打码量增加,12=人工增加提现打码量,13=人工减少提现打码量,14=人工增加转盘次数,15=人工减少转盘次数,16=累计充值增加,17=人工增加打码量,18=人工减少打码量)
     * {@link BaseGameInfoCons.UserExtChangeType}
     */
    @ApiModelProperty("帐变类型(1=用户返水,2=上级返佣,3=反水领取,4=返佣领取,5=转盘消耗,6=转盘增加,7=彩金增加,8=提现打码量增加," +
            "9=人工上分,10=人工下分,11=打码量增加,12=人工增加提现打码量,13=人工减少提现打码量,14=人工增加转盘次数,15=人工减少转盘次数,16=累计充值增加,17=人工增加打码量,18=人工减少打码量19 代理工资)")
    @TableField("type")
    @Excel(name = "帐变类型", readConverterExp = "1=用户返水,2=上级返佣,3=反水领取,4=返佣领取,5=转盘消耗,6=转盘增加,7=彩金增加,8=提现打码量增加," +
            "9=人工上分,10=人工下分,11=打码量增加,12=人工增加提现打码量,13=人工减少提现打码量,14=人工增加转盘次数,15=人工减少转盘次数,16=累计充值增加," +
            "17=人工增加打码量,18=人工减少打码量19 代理工资")
    private Integer type;

    /**
     * 变更金额
     */
    @ApiModelProperty("变更金额")
    @TableField("amount")
    @Excel(name = "变更金额")
    private BigDecimal amount;

    /**
     * 变更前金额
     */
    @ApiModelProperty("变更前金额")
    @TableField("old_amount")
    @Excel(name = "变更前金额")
    private BigDecimal oldAmount;

    /**
     * 变更后金额
     */
    @ApiModelProperty("变更后金额")
    @TableField("new_amount")
    @Excel(name = "变更后金额")
    private BigDecimal newAmount;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    @TableField("remark")
    @Excel(name = "备注")
    private String remark;

    /**
     * 操作人
     */
    @ApiModelProperty("操作人")
    @TableField("operator")
    @Excel(name = "操作人")
    private String operator;

    /**
     * 签名时间
     */
    @ApiModelProperty("签名时间")
    @TableField("sign_time")
    @Excel(name = "签名时间")
    private Long signTime;

    /**
     * 签名
     */
    @ApiModelProperty("签名")
    @TableField("sign")
    @Excel(name = "签名")
    private String sign;

    /**
     * 变更时间
     */
    @ApiModelProperty("变更时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "变更时间")
    private Date createTime;
    @ApiModelProperty("变更时间数组")
    @TableField(exist = false)
    private String[] createTimes;

    @ApiModelProperty("是否是查询当天数据 0.当天 1.非当天")
    @TableField(exist = false)
    private Integer todayFag = 0;


    /**
     * 版本号es同步使用
     */
    @ApiModelProperty("版本号es同步使用")
    @TableField(exist = false)
    private Long updateVersion;
}
