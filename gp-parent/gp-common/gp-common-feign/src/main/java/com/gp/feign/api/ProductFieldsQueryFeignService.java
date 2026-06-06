package com.gp.feign.api;

import com.common.core.result.AjaxResult;
import com.gp.common.base.constant.ServiceNameConstants;
import com.gp.common.base.feign.TableDataInfo;
import com.gp.common.mybatisplus.entity.FieldsQuery;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(contextId = "ProductFieldsQueryFeignService", path = "/feign/projectFields", value = ServiceNameConstants.tg_ft_back_service)
public interface ProductFieldsQueryFeignService {

    /**
     * 获取用户绑定字段详细信息
     */
    @GetMapping(value = "/getInfo/{id}")
    AjaxResult getInfo(@PathVariable("id") Long id);

    /**
     * 新增用户绑定字段
     */
    @PostMapping("/addFieldsQuery")
    AjaxResult addFieldsQuery(@RequestBody FieldsQuery param);

    /**
     * 修改用户绑定字段
     */
    @PostMapping("/editFieldsQuery")
    AjaxResult editFieldsQuery(@RequestBody FieldsQuery param);

    /**
     * 删除用户绑定字段
     */
    @PostMapping("/delete/{ids}")
    AjaxResult remove(@PathVariable("ids") Long[] ids);

    /**
     * 查询用户绑定字段列表
     */
    @PostMapping("/list")
    TableDataInfo list(@RequestBody FieldsQuery param);

    /**
     * 导出用户绑定字段列表
     */

    @PostMapping("/export")
    AjaxResult export(@RequestBody FieldsQuery param);

    /**
     * 根据key获取查询字段控制详细信息
     */
    @PostMapping(value = "/get")
    AjaxResult getInfo(@RequestBody FieldsQuery queryFields);

    /**
     * 修改查询字段控制  创建时间,创建人,修改时间,修改人,放最后
     */
    @PostMapping("/edit")
    AjaxResult edit(@RequestBody FieldsQuery param);

}
