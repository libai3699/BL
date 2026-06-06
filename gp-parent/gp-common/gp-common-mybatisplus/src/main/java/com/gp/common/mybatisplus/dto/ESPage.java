package com.gp.common.mybatisplus.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 分页定义
 * @author axing
 * @version 1.0
 * @date 2023/10/13/013 12:26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ESPage<T> implements Serializable {
    private static final Long serialVersionUID = 1L;
    private int current;
    private int size;
    private long total; //总条数
    private int pages; //总页数
    private List<T> data;
}
