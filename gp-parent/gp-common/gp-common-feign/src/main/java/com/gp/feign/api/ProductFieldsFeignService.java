package com.gp.feign.api;

import com.common.core.result.AjaxResult;
import com.gp.common.base.constant.ServiceNameConstants;
import com.gp.common.base.feign.TableDataInfo;
import com.gp.common.mybatisplus.entity.Fields;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@FeignClient(contextId = "ProductFieldsFeignService", path = "/feign/projectMenuFields", value = ServiceNameConstants.tg_ft_back_service)
public interface ProductFieldsFeignService {

    @PostMapping("/list")
    TableDataInfo list(@RequestBody Fields param);

    @PostMapping("/export")
    AjaxResult export(@RequestBody Fields param);

    @GetMapping(value = "/getInfo/{id}")
    AjaxResult getInfo(@RequestParam(value = "id", required = false) Long id);

    @PostMapping("/addFields")
    AjaxResult addFields(@RequestBody Fields param);

    @PostMapping("/editFields")
    AjaxResult editFields(@RequestBody Fields param);

    @PostMapping("/delete/{ids}")
    AjaxResult remove(@PathVariable("ids") Long[] ids);

    @PostMapping(value = "/get")
    AjaxResult getInfo(@RequestBody Fields tField);

    @PostMapping("/edit")
    AjaxResult edit(@RequestBody Fields tFields);

    @PostMapping("/add")
    AjaxResult add(@RequestBody Fields tFields);

}
