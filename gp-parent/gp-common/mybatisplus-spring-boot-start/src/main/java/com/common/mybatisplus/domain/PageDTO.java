package com.common.mybatisplus.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author axing
 * @version 1.0
 * @date 2021/10/16 19:31
 */
@Data
public class PageDTO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总计
     */
    private Long total;
    /**
     * 大小
     */
    private Integer size;
    /**
     * 当前的
     */
    private Integer current;
    /**
     * 总页数
     */
    private Integer pages;
    /**
     * 总条数
     */
    private List<T> records;
    /**
     * 是否有上一页
     */
    private Boolean hasPreviousPage;
    /**
     * 是否有下一页
     */
    private Boolean hasNextPage;




}
