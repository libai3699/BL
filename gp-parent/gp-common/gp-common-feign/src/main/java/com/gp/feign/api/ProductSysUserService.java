package com.gp.feign.api;

import com.common.core.result.AjaxResult;
import com.gp.common.base.constant.ServiceNameConstants;
import com.gp.common.base.feign.TableDataInfo;
import com.gp.common.base.feign.productsys.SysUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: c
 * @Date: 2025/8/6 下午 03:38
 **/
@FeignClient(contextId = "ProductSysUserService", path = "/feign/systemUser", value = ServiceNameConstants.tg_ft_back_service)
public interface ProductSysUserService {

    @PostMapping("/deleteLoginFailKey/{userId}")
    AjaxResult deleteLoginFailKey(@PathVariable(value = "userId") Long userId);

    @GetMapping("/list")
    TableDataInfo list(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "deptId", required = false) Long deptId,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "nickName", required = false) String nickName,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "roleNames", required = false) String roleNames,
            @RequestParam(value = "product", required = false) String product,
            @RequestParam(value = "code", required = false) Integer code,
            @RequestParam(value = "phonenumber", required = false) String phonenumber,
            @RequestParam(value = "sex", required = false) String sex,
            @RequestParam(value = "avatar", required = false) String avatar,
            @RequestParam(value = "googleAuthSecret", required = false) String googleAuthSecret,
            @RequestParam(value = "googleAmountSecret", required = false) String googleAmountSecret,
            @RequestParam(value = "googleCode", required = false) Integer googleCode,
            @RequestParam(value = "salt", required = false) String salt,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "delFlag", required = false) String delFlag,
            @RequestParam(value = "loginIp", required = false) String loginIp,
            @RequestParam(value = "loginDate", required = false) String loginDate,
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime);

    @GetMapping("/listAll")
    AjaxResult listAll(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "deptId", required = false) Long deptId,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "nickName", required = false) String nickName,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "roleNames", required = false) String roleNames,
            @RequestParam(value = "product", required = false) String product,
            @RequestParam(value = "code", required = false) Integer code,
            @RequestParam(value = "phonenumber", required = false) String phonenumber,
            @RequestParam(value = "sex", required = false) String sex,
            @RequestParam(value = "avatar", required = false) String avatar,
            @RequestParam(value = "googleAuthSecret", required = false) String googleAuthSecret,
            @RequestParam(value = "googleAmountSecret", required = false) String googleAmountSecret,
            @RequestParam(value = "googleCode", required = false) Integer googleCode,
            @RequestParam(value = "salt", required = false) String salt,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "delFlag", required = false) String delFlag,
            @RequestParam(value = "loginIp", required = false) String loginIp,
            @RequestParam(value = "loginDate", required = false) String loginDate,
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime);

    /**
     * 获取谷歌验证码二维码
     */
    @GetMapping("getGoogleAuth")
    AjaxResult getGoogleAuth(@RequestParam(value = "name", required = false) String name);

    @PostMapping("/bindGoogleAuth")
    AjaxResult bindGoogleAuth(@RequestBody SysUser user);

    @PostMapping("/toBindGoogleAuth")
    AjaxResult toBindGoogleAuth(@RequestBody SysUser user);

    @PostMapping("/delGoogleAuth")
    AjaxResult delGoogleAuth(@RequestBody SysUser user);

    @PostMapping("/bindGoogleAmount")
    AjaxResult bindGoogleAmount(@RequestBody SysUser user);

    @PostMapping("/delGoogleAmount")
    AjaxResult delGoogleAmount(@RequestBody SysUser user);

    @GetMapping("/export")
    AjaxResult export(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "deptId", required = false) Long deptId,
            @RequestParam(value = "userName", required = false) String userName,
            @RequestParam(value = "nickName", required = false) String nickName,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "roleNames", required = false) String roleNames,
            @RequestParam(value = "product", required = false) String product,
            @RequestParam(value = "code", required = false) Integer code,
            @RequestParam(value = "phonenumber", required = false) String phonenumber,
            @RequestParam(value = "sex", required = false) String sex,
            @RequestParam(value = "avatar", required = false) String avatar,
            @RequestParam(value = "googleAuthSecret", required = false) String googleAuthSecret,
            @RequestParam(value = "googleAmountSecret", required = false) String googleAmountSecret,
            @RequestParam(value = "googleCode", required = false) Integer googleCode,
            @RequestParam(value = "salt", required = false) String salt,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "delFlag", required = false) String delFlag,
            @RequestParam(value = "loginIp", required = false) String loginIp,
            @RequestParam(value = "loginDate", required = false) String loginDate,
            @RequestParam(value = "beginTime", required = false) String beginTime,
            @RequestParam(value = "endTime", required = false) String endTime);

    @GetMapping(value = {"/{userId}"})
    AjaxResult getInfo(@PathVariable(value = "userId", required = false) Long userId);

    @PostMapping("/add")
    AjaxResult add(@Validated @RequestBody SysUser user);

    @PostMapping("/edit")
    AjaxResult edit(@Validated @RequestBody SysUser user);

    @PostMapping("/resetPwd")
    AjaxResult resetPwd(@RequestBody SysUser user);

    @PostMapping("/changeStatus")
    AjaxResult changeStatus(@RequestBody SysUser user);
}
