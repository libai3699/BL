package com.gp.common.mybatisplus.merchantpay.base;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.common.core.util.RedisUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.RedisKey;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

public class JumpUtil {


    //"/redirectBlack/{product}/{item}/{chain}/{addr}"
    // 生成对应的跳转地址
    public static String doBlackJumpUrl(String domain, BigDecimal amount, String item, String chain, String addr) {
        String amountStr = amount
                .setScale(2, RoundingMode.DOWN)  // 向下取2位
                .stripTrailingZeros()            // 去掉多余0
                .toPlainString();
        return domain + StrUtil.format("/pay/pay/redirectBlack/{}/{}/{}/{}", amountStr, item, chain, addr);
    }

    public String doJumpUrl(String domain, JSONObject params, String payUrl, String orderNO) {
        StringBuilder sb = new StringBuilder("<form id='Form1' name='Form1' method='post' action='"
                + payUrl + "'>");

        params.forEach((k, v) -> sb.append("<input type='hidden' name='").append(k).append("' value='")
                .append(v).append("'>"));

        sb.append("<script>var form = document.getElementById('Form1');form.submit();</script>");
        // 参数存入redis中,之后用户点击跳转
        String key = StrUtil.format(RedisKey.payUrl, CecuUtil.getDbCode(), orderNO);
        SpringUtil.getBean(RedisUtil.class).set(key, sb.toString(), 10, TimeUnit.MINUTES);
        // 生成对应的跳转地址
        return domain + StrUtil.format("/pay/pay/redirect/{}/{}", CecuUtil.getDbCode(), orderNO);
    }

}
