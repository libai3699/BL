package com.gp.feign.api;

import com.common.core.result.AjaxResult;
import com.gp.common.base.constant.ServiceNameConstants;
import com.gp.common.base.feign.TableDataInfo;
import com.gp.common.mybatisplus.entity.GamePlate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "ProductGamePlateFeignService", path = "/feign/gameManagePlat", value = ServiceNameConstants.tg_ft_back_service)
public interface ProductGamePlateFeignService {

    @GetMapping("/getGameTypeAllList")
    AjaxResult getGameTypeAllList();

    @PostMapping("/list")
    TableDataInfo list(@RequestBody GamePlate param);

    @GetMapping(value = "/{id}")
    AjaxResult getInfo(@PathVariable("id") Long id);

    @PostMapping("/edit")
    AjaxResult edit(@RequestBody GamePlate paramp);

    @PostMapping("/delete/{ids}")
    AjaxResult remove(@PathVariable("ids") Long[] ids);
}
