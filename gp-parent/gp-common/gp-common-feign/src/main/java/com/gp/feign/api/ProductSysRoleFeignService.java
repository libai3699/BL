package com.gp.feign.api;

import com.common.core.result.AjaxResult;
import com.gp.common.base.constant.ServiceNameConstants;
import com.gp.common.base.feign.TableDataInfo;
import com.gp.common.base.feign.productsys.SysRole;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@FeignClient(contextId = "ProductSysRoleFeignService", path = "/feign/systemRole", value = ServiceNameConstants.tg_ft_back_service)
public interface ProductSysRoleFeignService {

    @PostMapping("/getAllRoleList")
    AjaxResult getAllRoleList();

    @GetMapping("/list")
    TableDataInfo list(
            @RequestParam(value = "roleId", required = false) Long roleId,
            @RequestParam(value = "code", required = false) Integer code,
            @RequestParam(value = "product", required = false) String product,
            @RequestParam(value = "roleName", required = false) String roleName,
            @RequestParam(value = "roleKey", required = false) String roleKey,
            @RequestParam(value = "roleSort", required = false) String roleSort,
            @RequestParam(value = "dataScope", required = false) String dataScope,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "delFlag", required = false) String delFlag,
            @RequestParam(value = "flag", required = false) Boolean flag,
            @RequestParam(value = "menuIds", required = false) Long[] menuIds,
            @RequestParam(value = "deptIds", required = false) Long[] deptIds,
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "createBy", required = false) String createBy,
            @RequestParam(value = "updateBy", required = false) String updateBy,
            @RequestParam(value = "remark", required = false) String remark
    );

    @GetMapping("/export")
    AjaxResult export(@RequestParam(value = "roleId", required = false) Long roleId,
                      @RequestParam(value = "code", required = false) Integer code,
                      @RequestParam(value = "product", required = false) String product,
                      @RequestParam(value = "roleName", required = false) String roleName,
                      @RequestParam(value = "roleKey", required = false) String roleKey,
                      @RequestParam(value = "roleSort", required = false) String roleSort,
                      @RequestParam(value = "dataScope", required = false) String dataScope,
                      @RequestParam(value = "status", required = false) String status,
                      @RequestParam(value = "delFlag", required = false) String delFlag,
                      @RequestParam(value = "flag", required = false) Boolean flag,
                      @RequestParam(value = "menuIds", required = false) Long[] menuIds,
                      @RequestParam(value = "deptIds", required = false) Long[] deptIds,
                      @RequestParam(value = "beginTime", required = false) String beginTime,
                      @RequestParam(value = "endTime", required = false) String endTime,
                      @RequestParam(value = "createBy", required = false) String createBy,
                      @RequestParam(value = "updateBy", required = false) String updateBy,
                      @RequestParam(value = "remark", required = false) String remark
    );

    @GetMapping(value = "/{roleId}")
    AjaxResult getInfo(@PathVariable("roleId") Long roleId);

    @PostMapping("/add")
    AjaxResult add(@Validated @RequestBody SysRole role);

    @PostMapping("/edit")
    AjaxResult edit(@Validated @RequestBody SysRole role);

    @PostMapping("/dataScope")
    AjaxResult dataScope(@RequestBody SysRole role);

    @PostMapping("/changeStatus")
    AjaxResult changeStatus(@RequestBody SysRole role);

    @PostMapping("/delete/{roleIds}")
    AjaxResult remove(@PathVariable("roleIds") Long[] roleIds);

    @GetMapping("/optionselect")
    AjaxResult optionselect();
}
