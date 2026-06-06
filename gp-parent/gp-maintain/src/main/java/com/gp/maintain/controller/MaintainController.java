package com.gp.maintain.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.result.ApiResult;
import com.gp.common.base.utils.BeanUtils;
import com.gp.common.mybatisplus.entity.Maintain;
import com.gp.common.mybatisplus.service.MaintainService;
import com.gp.maintain.vo.MaintainVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(value = "维护管理", tags = "维护管理")
@Slf4j
@RestController
@RequestMapping("/maintain")
public class MaintainController {

    @Resource
    private MaintainService maintainService;
    @Resource
    private CecuUtil cecuUtil;

    @SneakyThrows
    @Cacheable(value = "checkMaintain", key = "#productCode + '-checkMaintain'")
    @GetMapping(value = "/checkMaintain")
    @ApiOperation(value = "维护查询", notes = "维护查询")
    public ApiResult<MaintainVO> checkMaintain(@RequestParam String productCode) {
        log.info("回调信息: {}", productCode);
//        CecuUtil.cleanDbInfo();
        cecuUtil.cutDbByName("yh_ft_ext");
        Maintain maintain = maintainService.getOne(Wrappers.lambdaQuery(Maintain.class).eq(Maintain::getProductCode, productCode));
        if (maintain == null) {
            return ApiResult.error("productCode does not exist");
        }else {
            return ApiResult.success(BeanUtils.copyPropertiesIgnoreNull(maintain, MaintainVO.class));
        }
    }


}

