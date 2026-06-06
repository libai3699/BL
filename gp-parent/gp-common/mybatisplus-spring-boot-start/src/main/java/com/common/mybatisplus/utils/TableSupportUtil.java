package com.common.mybatisplus.utils;


import com.common.mybatisplus.domain.PageDomain;
import com.gp.common.base.utils.RequestUtil;

/**
 * 表格数据处理
 *
 * @author ruoyi
 */
public class TableSupportUtil
{
    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 封装分页对象
     */
    public static PageDomain getPageDomain()
    {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(RequestUtil.getParamInt(PAGE_NUM)==null?1:RequestUtil.getParamInt(PAGE_NUM));
        pageDomain.setPageSize(RequestUtil.getParamInt(PAGE_SIZE)==null?10:RequestUtil.getParamInt(PAGE_SIZE));
        pageDomain.setOrderByColumn(RequestUtil.getParamStr(ORDER_BY_COLUMN));
        pageDomain.setIsAsc(RequestUtil.getParamStr(IS_ASC));
        return pageDomain;
    }

    public static PageDomain buildPageRequest()
    {
        return getPageDomain();
    }


}
