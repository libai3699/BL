package com.gp.common.mybatisplus.param;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author axing
 * @version 1.0
 * @date 2024/1/11/011 17:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRedEnvelopeReceiveVo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 币种ID
     */
    @ApiModelProperty(value = "币种id")
    private Integer currencyId;
    /**
     * 币种ID
     */
    @ApiModelProperty(value = "币种ID")
    private Integer itemId;

    /**
     * 链名称
     */
    @ApiModelProperty(value = "链名称")
    private String chainTag;
    /**
     * 币加连
     */
    @ApiModelProperty(value = "币名称")
    private String itemName;
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private Long userId;

    /**
     * 用户tgID
     */
    @ApiModelProperty(value = "用户tgID")
    private Long userTgId;

    /**
     * 飞机名称
     */
    @ApiModelProperty(value="飞机名称")
    private String userTgName;

    /**
     * 飞机用户名
     */
    @ApiModelProperty(value="飞机用户名")
    private String userTgUsername;


    /** 用户头像 */
    @ApiModelProperty("用户头像")
    private String userAvatar;

    /**
     * 金额
     */
    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    /**
     * 接收时间
     */
    @ApiModelProperty(value = "接收时间")
    private Date createTime;
}
