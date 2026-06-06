package com.gp.common.base.utils;

import cn.hutool.core.util.ZipUtil;
import lombok.SneakyThrows;
import org.springframework.util.Base64Utils;

import java.io.IOException;

/**
 * Java 字符串压缩工具
 *
 * @author Logan
 * @version 1.0.0
 */
public class StringCompressUtils {

    /**
     * 使用zip进行解缩
     *
     * @param str 压缩后的文本
     * @return 解压后的字符串
     * @throws IOException 有异常时抛出，由调用者捕获处理
     */
    public static final String zip(String str) {
        return Base64Utils.encodeToString(ZipUtil.gzip(str, "utf-8"));
    }

    /**
     * 使用zip进行解压
     *
     * @param str 压缩后的文本
     * @return 解压后的字符串
     * @throws IOException 有异常时抛出，由调用者捕获处理
     */
    @SneakyThrows
    public static final String unzip(String str) {
        return ZipUtil.unGzip(Base64Utils.decodeFromString(str), "utf-8");
    }
}
