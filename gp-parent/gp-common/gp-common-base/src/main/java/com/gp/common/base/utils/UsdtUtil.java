package com.gp.common.base.utils;

import cn.hutool.core.util.StrUtil;

import java.io.Serializable;

/**
 * @author axing
 * @version 1.0
 * @date 2023/11/27/027 17:49
 */
public class UsdtUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    public static boolean isUsdtAddr(String addr){
        return StrUtil.isNotBlank(addr) && StrUtil.startWith(addr, "T") && addr.length() == 34;
    }
}
