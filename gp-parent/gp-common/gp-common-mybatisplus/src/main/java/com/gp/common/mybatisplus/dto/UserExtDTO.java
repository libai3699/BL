package com.gp.common.mybatisplus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserExtDTO {
    private Long userId;

    /** 奖金 */
    private BigDecimal bonusAmount = BigDecimal.ZERO;

    /** 累计投注 */
    private BigDecimal cumulativeBetVolume = BigDecimal.ZERO;

    /** 累计充值 */
    private BigDecimal cumulativeRecharge = BigDecimal.ZERO;

    /** 未完成投注 */
    private BigDecimal unfinishedWagerAmount = BigDecimal.ZERO;
}