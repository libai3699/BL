package com.common.mybatisplus.utils;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.ColumnCache;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.core.toolkit.support.SerializedLambda;
import com.common.mybatisplus.domain.PageDTO;
import com.common.mybatisplus.domain.PageDomain;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gp.common.base.utils.BeanCopyUtil;
import org.apache.ibatis.reflection.property.PropertyNamer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 分页工具类
 *
 * @author axing
 * @version 1.0
 * @date 2023/11/1/001 12:54
 */
public class PageUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String emptyStr = " ";

    /**
     * 设置请求分页数据
     */
    public static void startPage() {
        PageDomain pageDomain = TableSupportUtil.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum() == null ? 1 : pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        //设置条数限制不能超过50,防止mysql大批量查询崩溃
        if (pageSize == null || pageSize > 50) {
            pageSize = 50;
        }
        String orderByStr = "";
        if (StrUtil.isNotBlank(pageDomain.getOrderByColumn())) {
            if (StrUtil.isBlank(pageDomain.getIsAsc())) {
                orderByStr = new StringBuilder(pageDomain.getOrderByColumn()).append(emptyStr).append(PageDomain.asc).toString();
            } else {
                orderByStr = new StringBuilder(pageDomain.getOrderByColumn()).append(emptyStr).append(pageDomain.getIsAsc()).toString();
            }
        }
        //拼接排序, 防sql注入
        String orderBy = SqlUtil.escapeOrderBySql(StrUtil.toUnderlineCase(orderByStr));
        PageHelper.startPage(pageNum, pageSize, orderBy);

    }

    /**
     * @param func
     * @param isAsc true正序,false倒序
     * @param <T>
     */
    public static <T> void startPage(SFunction<T, ?> func, boolean isAsc) {
        PageDomain pageDomain = TableSupportUtil.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum() == null ? 1 : pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        //设置条数限制不能超过50,防止mysql大批量查询崩溃
        if (pageSize == null || pageSize > 100) {
            pageSize = 100;
        }

        String orderByStr = "";
        if (func != null) {
            orderByStr = getColumn(func);
        }
        if (StrUtil.isNotBlank(orderByStr)) {
            if (isAsc) {
                orderByStr = new StringBuilder(orderByStr).append(emptyStr).append(PageDomain.asc).toString();
            } else {
                orderByStr = new StringBuilder(orderByStr).append(emptyStr).append(PageDomain.desc).toString();
            }
        }
        PageHelper.startPage(pageNum, pageSize, orderByStr);

    }

    public static <T> void startPage(Integer pageNum, Integer pageSize, SFunction<T, ?> func, boolean isAsc) {
        if (pageNum != null && pageSize != null) {
            String orderByStr = "";
            if (func != null) {
                orderByStr = getColumn(func);
            }
            if (StrUtil.isNotBlank(orderByStr)) {
                if (isAsc) {
                    orderByStr = new StringBuilder(orderByStr).append(emptyStr).append(PageDomain.asc).toString();
                } else {
                    orderByStr = new StringBuilder(orderByStr).append(emptyStr).append(PageDomain.desc).toString();
                }
            }
            PageHelper.startPage(pageNum, pageSize, orderByStr);
        }
    }

    public static <T> void startPage(Integer pageNum, Integer pageSize) {
        if (pageNum != null && pageSize != null) {
            PageHelper.startPage(pageNum, pageSize);
        }
    }

    /**
     * 获取对应的字段名
     *
     * @param func 数据
     * @param <T>
     * @return
     */
    public static <T> String getColumn(SFunction<T, ?> func) {
        SerializedLambda lambda = LambdaUtils.resolve(func);
        String fieldName = PropertyNamer.methodToProperty(lambda.getImplMethodName());
        Map<String, ColumnCache> columnMap = LambdaUtils.getColumnMap(lambda.getInstantiatedType());
        ColumnCache columnCache = columnMap.get(LambdaUtils.formatKey(fieldName));
        return columnCache.getColumn();
    }

    public static <T> PageDTO<T> buildPageDto(List<T> list) {
        PageInfo<T> pageInfo = new PageInfo<>(list);
        PageDTO<T> tPageDTO = new PageDTO<>();
        tPageDTO.setTotal(pageInfo.getTotal());
        tPageDTO.setSize(pageInfo.getSize());
        tPageDTO.setCurrent(pageInfo.getPageNum());
        tPageDTO.setPages(pageInfo.getPages());
        tPageDTO.setHasPreviousPage(pageInfo.isHasPreviousPage());
        tPageDTO.setHasNextPage(pageInfo.isHasNextPage());
        tPageDTO.setRecords(list);
        return tPageDTO;
    }

    public static <T> PageDTO<T> buildPageDto(List list, Class<T> clazz) {
        PageInfo pageInfo = new PageInfo<>(list);
        PageDTO<T> tPageDTO = new PageDTO<>();
        tPageDTO.setTotal(pageInfo.getTotal());
        tPageDTO.setSize(pageInfo.getSize());
        tPageDTO.setCurrent(pageInfo.getPageNum());
        tPageDTO.setPages(pageInfo.getPages());
        tPageDTO.setHasPreviousPage(pageInfo.isHasPreviousPage());
        tPageDTO.setHasNextPage(pageInfo.isHasNextPage());
        tPageDTO.setRecords(BeanCopyUtil.copyToList(list, clazz));
        return tPageDTO;
    }

    public static <T> PageDTO<T> buildPageDto(PageInfo pageInfo, Class<T> clazz) {
        PageDTO<T> tPageDTO = new PageDTO<>();
        tPageDTO.setTotal(pageInfo.getTotal());
        tPageDTO.setSize(pageInfo.getSize());
        tPageDTO.setCurrent(pageInfo.getPageNum());
        tPageDTO.setPages(pageInfo.getPages());
        tPageDTO.setHasPreviousPage(pageInfo.isHasPreviousPage());
        tPageDTO.setHasNextPage(pageInfo.isHasNextPage());
        tPageDTO.setRecords(BeanCopyUtil.copyToList(pageInfo.getList(), clazz));
        return tPageDTO;
    }

    /**
     * 对List进行分页处理
     *
     * @param list 原始数据列表
     * @param <T>  数据类型
     * @return 分页后的数据
     */
    public static <T> PageDTO<T> getPageFromList(List<T> list) {
        PageDomain pageDomain = TableSupportUtil.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();

        // 设置条数限制不能超过50,防止mysql大批量查询崩溃
        if (pageSize == null || pageSize > 50) {
            pageSize = 50;
        }

        if (pageNum == null || pageNum <= 0) {
            pageNum = 1;
        }

        if (pageSize <= 0) {
            pageSize = 10;
        }

        int total = list.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);

        List<T> records = new ArrayList<>();
        if (fromIndex < total) {
            records = list.subList(fromIndex, toIndex);
        }

        PageDTO<T> pageDTO = new PageDTO<>();
        pageDTO.setRecords(records);
        pageDTO.setTotal((long) total);
        pageDTO.setSize(pageSize);
        pageDTO.setCurrent(pageNum);
        pageDTO.setPages((total + pageSize - 1) / pageSize);
        pageDTO.setHasPreviousPage(pageNum > 1);
        pageDTO.setHasNextPage(pageNum < pageDTO.getPages());
        return pageDTO;
    }

}
