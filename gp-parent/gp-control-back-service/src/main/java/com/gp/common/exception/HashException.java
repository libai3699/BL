package com.gp.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author axing
 * @version 1.0
 * @date 2022/5/18 14:54
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HashException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 异常状态码
     */
    private String isWin;
    /**
     * 扣款赔率
     */
    private BigDecimal winOdds;
    /**
     * 扣款赔率
     */
    private BigDecimal betAmount;
    /**
     * 投注内容
     */
    private String betContent;
    /**
     * 结果内容
     */
    private String resultContent;


}
