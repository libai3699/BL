package com.common.core.serial;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.filter.Filter;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

/**
 * Redis使用FastJson序列化
 *
 * @author xunman
 */
public class FastJson2JsonRedisSerializer<T> implements RedisSerializer<T> {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    static final Filter autoTypeFilter = JSONReader.autoTypeFilter(
            // 按需加上需要支持自动类型的类名前缀，范围越小越安全
            "com.gp.framework.security",
            "com.gp.bot",
            "com.gp",
            "java.math.BigDecimal"  // 支持BigDecimal类型（红包金额必需）
    );
    private Class<T> clazz;

    public FastJson2JsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        // 基础类型直接存纯字符串，不做 JSON 序列化
        if (t instanceof String || t instanceof Number
                || t instanceof Boolean || t instanceof Character) {
            return t.toString().getBytes(DEFAULT_CHARSET);
        }
        return JSON.toJSONBytes(t, JSONWriter.Feature.WriteClassName);
    }


    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);
        // 非合法 JSON（基础类型存的纯字符串），直接返回原始字符串，不走 FastJSON
        if (!JSON.isValid(str)) {
            return (T) str;
        }
        try {
            return JSON.parseObject(str, clazz, autoTypeFilter);
        } catch (Exception e) {
            return (T) str;
        }
    }


}
