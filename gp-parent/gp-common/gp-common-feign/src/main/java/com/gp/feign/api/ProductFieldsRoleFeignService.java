package com.gp.feign.api;

import com.common.core.result.AjaxResult;
import com.gp.common.base.constant.ServiceNameConstants;
import com.gp.common.base.feign.TableDataInfo;
import com.gp.common.base.feign.productsys.RoleFiledQueryParam;
import com.gp.common.mybatisplus.entity.FieldsRole;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(contextId = "ProductFieldsRoleFeignService", path = "/feign/projectRoleFields", value =
        ServiceNameConstants.tg_ft_back_service)
public interface ProductFieldsRoleFeignService {

    /**
     * 查询角色字段列表
     */
    @PostMapping("/getRoleFieldsList")
    public TableDataInfo getRoleFieldsList(@RequestBody FieldsRole param);

    /**
     * 导出角色字段列表
     */
    @PostMapping("/export")
    public AjaxResult export(@RequestBody FieldsRole param);

    /**
     * 获取角色字段详细信息
     */

    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id);

    /**
     * 新增角色字段
     */
    @PostMapping("/addFieldsRole")
    public AjaxResult addFieldsRole(@RequestBody FieldsRole param);

    /**
     * 修改角色字段
     */
    @PostMapping("/editFieldsRole")
    public AjaxResult editFieldsRole(@RequestBody FieldsRole param);

    /**
     * 删除角色字段
     */
    @PostMapping("/delete/{ids}")
    public AjaxResult remove(@PathVariable("ids") Long[] ids);

    /**
     * 查询角色字段控制列表
     */
    @PostMapping("/list")
    public AjaxResult list(@RequestBody FieldsRole tRoleFields);

    /**
     * 修改角色字段控制
     */
    @ApiOperation(value = "修改角色字段控制", notes = "备注: 无")
    @PostMapping("/edit")
    public AjaxResult edit(@RequestBody RoleFiledQueryParam param);

}
