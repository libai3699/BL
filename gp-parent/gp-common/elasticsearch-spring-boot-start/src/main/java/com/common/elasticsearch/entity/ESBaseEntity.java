package com.common.elasticsearch.entity;

import com.gp.common.base.utils.LogUtil;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * es基础bean封装
 * @author axing
 * @version 1.0
 * @date 2023/10/13/013 12:26
 */
@Data
public class ESBaseEntity implements Serializable {
    private static final Long serialVersionUID = 1L;

    //数据的ID,存的时候不用传,修改删除的时候要传
//    @Transient   // Spring Data 标记，不会写入 ES
    private Long esId;
    /**
     * 临时使用ID
     */
    @Transient   // Spring Data 标记，不会写入 ES
    private String logId;

    /**
     * 临时使用
     */
    @Transient   // Spring Data 标记，不会写入 ES
    private String lastDate;

    /**
     * MDC
     */
    @Transient   // Spring Data 标记，不会写入 ES
    private String mdc= LogUtil.getMDC();

    /**
     * 临时使用
     */
    @Transient   // Spring Data 标记，不会写入 ES
    private String extend;

    /**
     * 临时使用
     */
    @Transient   // Spring Data 标记，不会写入 ES
    private Long version;

    @Field(store = true, type = FieldType.Long)
    private Long lastTime=System.currentTimeMillis();
}
