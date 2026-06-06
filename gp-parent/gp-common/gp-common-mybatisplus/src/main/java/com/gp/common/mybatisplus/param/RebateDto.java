package com.gp.common.mybatisplus.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author axing
 * @version 1.0
 * @date 2024/1/8/008 20:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RebateDto implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 上游用户的返佣比例
     */
    private BigDecimal totalAllocationRebate;

    /**
     * 用户自己的返佣比例
     */
    private BigDecimal userRebate;
    /**
     * 上游用户的返佣比例
     */
    private BigDecimal superUserRebate;

}
