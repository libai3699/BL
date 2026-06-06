package com.gp.common.utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import lombok.Data;

/**
 * @author axing
 * @version 1.0
 * @date 2022/1/30 14:37
 */
@Data
public class DecimalFormaterUtil implements Serializable {

    private static final long serialVersionUID = 1L;
    public static BigDecimal ONE_WAN = new BigDecimal( "10000" );

    public static void main(BigDecimal value) {
        if ( value.compareTo( ONE_WAN ) < 0 ) {
            System.out.println("不足1万");
        }
        // 转换为万元（除以10000）
        BigDecimal decimal = value.divide( ONE_WAN, 2, RoundingMode.DOWN );
        // 保留两位小数
        DecimalFormat formater = new DecimalFormat( "0.00" );
        // 向下取整
        formater.setRoundingMode( RoundingMode.DOWN );
        // 格式化完成之后得出结果
        String rs = formater.format( decimal );
        System.out.println(rs.concat("万+"));
    }

    /**
     * @desc 1.0~1之间的BigDecimal小数，格式化后失去前面的0,则前面直接加上0。
     * 2.传入的参数等于0，则直接返回字符串"0.00"
     * 3.大于1的小数，直接格式化返回字符串
     * @param obj传入的小数
     * @return
     */
    public static String formatToNumber(BigDecimal obj) {
        return obj.setScale(2, BigDecimal.ROUND_FLOOR).toString();
    }

    public static void main(String[] args) {
        System.out.println(new BigDecimal(-999.554444).setScale(2, BigDecimal.ROUND_FLOOR).toString());
    }
}
