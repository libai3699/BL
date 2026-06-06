package com.gp.common.mybatisplus.until;

import com.gp.common.mybatisplus.entity.UserCountTotalAmount;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BonusUtil {
    /**
     * 赠送总彩金：各类彩金字段相加（不含彩金扣款；扣款见 customerPointsDeductedAmount 等字段）。
     */
    public static BigDecimal getTotalBonusAmountValue(UserCountTotalAmount count) {
        return count.getBonusAmount()
                .add(count.getCustomerBonusAmount())
                .add(count.getPixBonusAward())
                .add(count.getYndBonusAward())
                .add(count.getUpayBonusAward())
                .add(count.getPlayWheelTermAward())
                .add(count.getPay1818BonusAmount())
                .add(count.getActivityAwardBonusAmount())
                .add(count.getTotalPacketAmount())
                .add(count.getRupeeBonusAmount())
                .add(count.getZlotyBonusAmount())
                .add(count.getHwpayBonusAmount())
                .add(count.getUBonusAmount())
                .add(count.getTransferBonusAmount())
                .setScale(2, RoundingMode.DOWN);
    }
}
