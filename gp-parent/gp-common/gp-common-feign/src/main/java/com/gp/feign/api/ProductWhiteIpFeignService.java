package com.gp.feign.api;

import com.common.core.result.AjaxResult;
import com.gp.common.base.constant.ServiceNameConstants;
import com.gp.common.base.feign.TableDataInfo;
import com.gp.common.mybatisplus.entity.WhiteIp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(contextId = "ProductWhiteIpFeignService", path = "/feign/projectIp", value = ServiceNameConstants.tg_ft_back_service)
public interface ProductWhiteIpFeignService {

    /**
     * 查询ip白名单列表
     */
    @PostMapping("/list")
    TableDataInfo list(@RequestBody WhiteIp tWhiteIp);

    /**
     * 导出ip白名单列表
     */
    @PostMapping("/export")
    AjaxResult export(@RequestBody WhiteIp tWhiteIp);

    /**
     * 获取ip白名单详细信息
     */
    @GetMapping(value = "/{id}")
    AjaxResult getInfo(@PathVariable("id") Long id);

    /**
     * 新增ip白名单
     */
    @PostMapping("/add")
    AjaxResult add(@RequestBody WhiteIp tWhiteIp);

    /**
     * 修改ip白名单
     */
    @PostMapping("/edit")
    AjaxResult edit(@RequestBody WhiteIp tWhiteIp);

    /**
     * 删除ip白名单
     */
    @PostMapping("/delete/{ids}")
    AjaxResult remove(@PathVariable("ids") Long[] ids);
}
