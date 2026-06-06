package com.gp.feign.api;

import com.common.core.result.AjaxResult;
import com.gp.common.base.constant.ServiceNameConstants;
import com.gp.common.base.feign.productsys.SysMenu;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@FeignClient(contextId = "ProductSysMenuFeignService", path = "/feign/systemMenu", value = ServiceNameConstants.tg_ft_back_service)
public interface ProductSysMenuFeignService {

    /**
     * 获取菜单列表
     */
    @GetMapping("/list")
    AjaxResult list(
            @RequestParam(value = "menuId", required = false) Long menuId,
            @RequestParam(value = "menuName", required = false) String menuName,
            @RequestParam(value = "parentName", required = false) String parentName,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "orderNum", required = false) String orderNum,
            @RequestParam(value = "path", required = false) String path,
            @RequestParam(value = "component", required = false) String component,
            @RequestParam(value = "isFrame", required = false) String isFrame,
            @RequestParam(value = "menuType", required = false) String menuType,
            @RequestParam(value = "visible", required = false) String visible,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "perms", required = false) String perms,
            @RequestParam(value = "icon", required = false) String icon,
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "createBy", required = false) String createBy,
            @RequestParam(value = "updateBy", required = false) String updateBy,
            @RequestParam(value = "remark", required = false) String remark
    );

    /**
     * 根据菜单编号获取详细信息
     */
    @GetMapping(value = "/{menuId}")
    AjaxResult getInfo(@RequestParam(value = "menuId", required = false) Long menuId);

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    AjaxResult treeselect(
            @RequestParam(value = "menuId", required = false) Long menuId,
            @RequestParam(value = "menuName", required = false) String menuName,
            @RequestParam(value = "parentName", required = false) String parentName,
            @RequestParam(value = "parentId", required = false) Long parentId,
            @RequestParam(value = "orderNum", required = false) String orderNum,
            @RequestParam(value = "path", required = false) String path,
            @RequestParam(value = "component", required = false) String component,
            @RequestParam(value = "isFrame", required = false) String isFrame,
            @RequestParam(value = "menuType", required = false) String menuType,
            @RequestParam(value = "visible", required = false) String visible,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "perms", required = false) String perms,
            @RequestParam(value = "icon", required = false) String icon,
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "createBy", required = false) String createBy,
            @RequestParam(value = "updateBy", required = false) String updateBy,
            @RequestParam(value = "remark", required = false) String remark
    );

    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    AjaxResult roleMenuTreeselect(@PathVariable("roleId") Long roleId);

    @PostMapping("/add")
    AjaxResult add(@Validated @RequestBody SysMenu menu);

    @PostMapping("/edit")
    AjaxResult edit(@Validated @RequestBody SysMenu menu);

    @PostMapping("/delete/{menuId}")
    AjaxResult remove(@PathVariable("menuId") Long menuId);
}
