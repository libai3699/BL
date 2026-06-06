package com.common.mybatisplus.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页数据
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageDomain
{

    /** 倒序 */
    public static final String desc = "desc";
    /** 正序 */
    public static final String asc = "asc";

    /** 当前记录起始索引 */
    private Integer pageNum;
    /** 每页显示记录数 */
    private Integer pageSize;
    /** 排序列 */
    private String orderByColumn;
    /** 排序的方向 "desc" 或者 "asc". */
    private String isAsc;
}
