package com.gp.feign.api;

import com.common.core.result.AjaxResult;
import com.gp.common.base.constant.ServiceNameConstants;
import com.gp.common.base.feign.TableDataInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@FeignClient(contextId = "ProductSysOperlogFeignService", path = "/feign/monitorLogininfor", value =
        ServiceNameConstants.tg_ft_back_service)
public interface ProductSysLogininforFeignService {

    @GetMapping("/list")
    public TableDataInfo list(
            @RequestParam(value = "infoId", required = false) Long infoId,
            @RequestParam(value = "product", required = false) String product,
            @RequestParam(value = "code", required = false) Integer code,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "ipaddr", required = false) String ipaddr,
            @RequestParam(value = "loginLocation", required = false) String loginLocation,
            @RequestParam(value = "browser", required = false) String browser,
            @RequestParam(value = "os", required = false) String os,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "msg", required = false) String msg,
            @RequestParam(value = "loginTime", required = false) Date loginTime,
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "createBy", required = false) String createBy,
            @RequestParam(value = "createTime", required = false) Date createTime,
            @RequestParam(value = "updateBy", required = false) String updateBy,
            @RequestParam(value = "updateTime", required = false) Date updateTime,
            @RequestParam(value = "remark", required = false) String remark
    );

    @GetMapping("/export")
    AjaxResult export(@RequestParam(value = "infoId", required = false) Long infoId,
                      @RequestParam(value = "product", required = false) String product,
                      @RequestParam(value = "code", required = false) Integer code,
                      @RequestParam(value = "userName", required = false) String userName,
                      @RequestParam(value = "ipaddr", required = false) String ipaddr,
                      @RequestParam(value = "loginLocation", required = false) String loginLocation,
                      @RequestParam(value = "browser", required = false) String browser,
                      @RequestParam(value = "os", required = false) String os,
                      @RequestParam(value = "status", required = false) String status,
                      @RequestParam(value = "msg", required = false) String msg,
                      @RequestParam(value = "loginTime", required = false) Date loginTime,
                      @RequestParam(value = "beginTime", required = false) String beginTime,
                      @RequestParam(value = "endTime", required = false) String endTime,
                      @RequestParam(value = "createBy", required = false) String createBy,
                      @RequestParam(value = "createTime", required = false) Date createTime,
                      @RequestParam(value = "updateBy", required = false) String updateBy,
                      @RequestParam(value = "updateTime", required = false) Date updateTime,
                      @RequestParam(value = "remark", required = false) String remark
    );
}
