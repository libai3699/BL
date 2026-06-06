package com.gp.feign.api;

import com.common.core.result.AjaxResult;
import com.gp.common.base.constant.ServiceNameConstants;
import com.gp.common.base.feign.TableDataInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@FeignClient(contextId = "ProductSysOperlogFeignService", path = "/feign/monitorOperlog", value = ServiceNameConstants.tg_ft_back_service)
public interface ProductSysOperlogFeignService {

    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(value = "operId", required = false) Long operId,
                              @RequestParam(value = "title", required = false) String title,
                              @RequestParam(value = "businessType", required = false) Integer businessType,
                              @RequestParam(value = "businessTypes", required = false) Integer[] businessTypes,
                              @RequestParam(value = "method", required = false) String method,
                              @RequestParam(value = "requestMethod", required = false) String requestMethod,
                              @RequestParam(value = "operatorType", required = false) Integer operatorType,
                              @RequestParam(value = "operName", required = false) String operName,
                              @RequestParam(value = "deptName", required = false) String deptName,
                              @RequestParam(value = "operUrl", required = false) String operUrl,
                              @RequestParam(value = "operIp", required = false) String operIp,
                              @RequestParam(value = "operLocation", required = false) String operLocation,
                              @RequestParam(value = "operParam", required = false) String operParam,
                              @RequestParam(value = "jsonResult", required = false) String jsonResult,
                              @RequestParam(value = "status", required = false) Integer status,
                              @RequestParam(value = "errorMsg", required = false) String errorMsg,
                              @RequestParam(value = "operTime", required = false) Date operTime,
                              @RequestParam(value = "beginTime", required = false) String beginTime,
                              @RequestParam(value = "endTime", required = false) String endTime,
                              @RequestParam(value = "createBy", required = false) String createBy,
                              @RequestParam(value = "createTime", required = false) Date createTime,
                              @RequestParam(value = "updateBy", required = false) String updateBy,
                              @RequestParam(value = "updateTime", required = false) Date updateTime,
                              @RequestParam(value = "remark", required = false) String remark
    );

    @GetMapping("/export")
    public AjaxResult export(@RequestParam(value = "operId", required = false) Long operId,
                             @RequestParam(value = "title", required = false) String title,
                             @RequestParam(value = "businessType", required = false) Integer businessType,
                             @RequestParam(value = "businessTypes", required = false) Integer[] businessTypes,
                             @RequestParam(value = "method", required = false) String method,
                             @RequestParam(value = "requestMethod", required = false) String requestMethod,
                             @RequestParam(value = "operatorType", required = false) Integer operatorType,
                             @RequestParam(value = "operName", required = false) String operName,
                             @RequestParam(value = "deptName", required = false) String deptName,
                             @RequestParam(value = "operUrl", required = false) String operUrl,
                             @RequestParam(value = "operIp", required = false) String operIp,
                             @RequestParam(value = "operLocation", required = false) String operLocation,
                             @RequestParam(value = "operParam", required = false) String operParam,
                             @RequestParam(value = "jsonResult", required = false) String jsonResult,
                             @RequestParam(value = "status", required = false) Integer status,
                             @RequestParam(value = "errorMsg", required = false) String errorMsg,
                             @RequestParam(value = "operTime", required = false) Date operTime,
                             @RequestParam(value = "beginTime", required = false) String beginTime,
                             @RequestParam(value = "endTime", required = false) String endTime,
                             @RequestParam(value = "createBy", required = false) String createBy,
                             @RequestParam(value = "createTime", required = false) Date createTime,
                             @RequestParam(value = "updateBy", required = false) String updateBy,
                             @RequestParam(value = "updateTime", required = false) Date updateTime,
                             @RequestParam(value = "remark", required = false) String remark
    );
}
