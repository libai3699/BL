package com.gp.framework.web.controller;

import com.common.core.constant.HttpStatus;
import com.common.core.result.AjaxResult;
import com.common.core.util.StringUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gp.common.base.constant.CommonConstants;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.utils.sql.SqlUtil;
import com.gp.framework.web.page.PageDomain;
import com.gp.framework.web.page.TableDataInfo;
import com.gp.framework.web.page.TableSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * web层通用数据处理
 *
 * @author ruoyi
 */
public class BaseController
{
    protected final Logger logger = LoggerFactory.getLogger(BaseController.class);

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport()
        {
            @Override
            public void setAsText(String text)
            {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize))
        {
            String orderByColumn = pageDomain.getOrderByColumn();

            if ( !isValidOrderByColumn(orderByColumn) && StringUtils.isNotEmpty(orderByColumn)) {
                // 如果 orderByColumn 不在指定范围内，设置为默认排序字段 "id"
                pageDomain.setOrderByColumn( "id");
            }
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            if(CommonConstants.PROJECT_SORT.equals(orderBy)){
                orderBy = CommonConstants.SORT;
            }
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    protected void startUserListPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize))
        {
            String orderByColumn = pageDomain.getOrderByColumn();
            if ( StringUtils.isNotEmpty(orderByColumn) ) {
                pageDomain.setOrderByColumn( "a.user_id");
            }
            String isAsc = pageDomain.getIsAsc();
            if ( StringUtils.isNotEmpty(isAsc) ) {
                pageDomain.setIsAsc( "desc");
            }
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            PageHelper.startPage(pageNum, pageSize, orderBy);
        }
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected TableDataInfo getDataTable(List<?> list)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg(MessagesUtils.get("query.success"));
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected TableDataInfo getDataTableALl(List<?> list,Long total)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setMsg(MessagesUtils.get("query.success"));
        rspData.setRows(list);
        rspData.setTotal(total);
        return rspData;
    }

    /**
     * 响应返回结果
     *
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 响应返回结果
     *
     * @param result 结果
     * @return 操作结果
     */
    protected AjaxResult toAjax(Boolean result)
    {
        return result ? AjaxResult.success() : AjaxResult.error();
    }


    // 验证 orderByColumn 是否合法
    private boolean isValidOrderByColumn(String orderByColumn) {
        List<String> allowedColumns = Arrays.asList(
                "id","table_id","column_id","config_id","dept_id","dict_code","dict_id","job_id","job_log_id","info_id"
                ,"menu_id","notice_id","oper_id","post_id","role_id","user_id","dealer_id"
                ,"tableId","columnId","configId","deptId","dictCode","dictId","jobId","jobLogId","infoId"
                ,"menuId","noticeId","operId","postId","roleId","userId","dealerId"
                , "sort", "create_time", "createTime"
                , "dealerId","merchantId","realAmount","realLawAmount"
                , "amount","oldAmount","newAmount","bonusAmount","lawAmount","totalAmount","fee"
                ,"channelRebate", "betAmount", "win", "winLoss", "codeAmount", "rebate", "superRebate"
                , "waitRebateAmount", "waitReturnCommissionAmount", "alreadyRebateAmount", "alreadyReturnCommissionAmount"
        );
        return allowedColumns.contains(orderByColumn);
    }

}
