package com.common.elasticsearch.rule;

import cn.hutool.core.util.StrUtil;

/**
 * 索引规则类
 * @author axing
 * @version 1.0
 * @date 2023/10/13/013 12:26
 */
public class ESIndexRule {

    /**
     * 用户账变
     */
    public final static String amountChange = "{}_amount_change";

    /**
     * 项目模块
     */
    public final static String extChange = "{}_ext_change";

    /**
     * 项目模块
     */
    public final static String orderTerm = "{}_order_term";


    /**
     * 内容索引
     *
     * @return
     */
    public static String amountChangeIndex(String appId) {
        return StrUtil.format(amountChange,appId);
    }
    /**
     * 内容索引
     *
     * @return
     */
    public static String extChangeIndex(String appId) {
        return StrUtil.format(extChange,appId);
    }
    /**
     * 内容索引
     *
     * @return
     */
    public static String orderTermIndex(String appId) {
        return StrUtil.format(orderTerm,appId);
    }


}
