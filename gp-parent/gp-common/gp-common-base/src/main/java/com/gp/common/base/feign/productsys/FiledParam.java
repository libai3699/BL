package com.gp.common.base.feign.productsys;

import lombok.Data;

import java.io.Serializable;

/**
 * @author axing
 * @version 1.0
 * @date 2022/6/16 15:54
 */
@Data
public class FiledParam implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 字段
     */
    private String filed;

    /**
     * 字段名称
     */
    private String filedName;

    /**
     * 字段英文名称
     */
    private String filedEnName;

    /**
     * 是否展示
     */
    private boolean show;

    /**
     * 字段排序
     */
    private Integer filedSort;


}
