package com.gp.verification.controller;


import cloud.tianai.captcha.application.ImageCaptchaApplication;
import cloud.tianai.captcha.application.vo.CaptchaResponse;
import cloud.tianai.captcha.application.vo.ImageCaptchaVO;
import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.common.core.util.RedisUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.verification.param.ImageClass;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;


/**
 * @author axing
 * @version 1.0
 * @date 2022/9/21 15:09
 */
@Slf4j
@RequestMapping("/verification")
@RestController
@Api(value = "文字点选验证码", tags = "文字点选验证码")
public class VerificationController {

    @Resource
    private ImageCaptchaApplication application;

    @Resource
    private RedisUtil redisUtil;

    @PostMapping(value = "/getVerificationCode")
    @ApiOperation(value = "获取文字点选验证码")
    @SentinelResource(
            value = "/verification/getVerificationCode",
            blockHandler = "getVerificationCodeHandler"
    )
    public CaptchaResponse<ImageCaptchaVO> getVerificationCode(@RequestAttribute(value = "URL", required = false) String url) {
        return application.generateCaptcha(CaptchaTypeConstant.WORD_IMAGE_CLICK);
    }

    public CaptchaResponse<ImageCaptchaVO> getVerificationCodeHandler(String url, BlockException ex) {
        log.warn("限流触发：getVerificationCode，URL：{}", url);
        return null;
    }


    @PostMapping("/verificationCode")
    @ResponseBody
    @ApiOperation(value = "校验文字点选验证码")
    @SentinelResource(
            value = "/verification/verificationCode",
            blockHandler = "checkCaptchaHandler"
    )
    public ApiResponse<?> checkCaptchaHandler(@RequestAttribute(value = "URL", required = false) String url,
                                              @RequestBody Data data,
                                              HttpServletRequest request) {
        String account = data.getAccount();
        ImageClass imageClass = data.getData();
        if (imageClass != null) {
            ImageCaptchaTrack imageCaptchaTrack = new ImageCaptchaTrack();
            imageCaptchaTrack.setBgImageHeight(imageClass.getBgImageHeight());
            imageCaptchaTrack.setBgImageWidth(imageClass.getBgImageWidth());
            imageCaptchaTrack.setTemplateImageWidth(imageClass.getTemplateImageWidth());
            imageCaptchaTrack.setTemplateImageHeight(imageClass.getTemplateImageHeight());
            imageCaptchaTrack.setStartTime(imageClass.getStartTime());
            imageCaptchaTrack.setStopTime(imageClass.getStopTime());
            imageCaptchaTrack.setTrackList(imageClass.getTrackList());
            imageCaptchaTrack.setData(imageClass.getData());
            ApiResponse<?> response = application.matching(data.getId(), imageCaptchaTrack);
            String key = StrUtil.format(RedisKey.captcha_user_fail_count, CecuUtil.getDbCode(), account);
            if (response.isSuccess()) {
                redisUtil.del(key);
                return ApiResponse.ofSuccess(Collections.singletonMap("id", data.getId()));
            }
        }
        return ApiResponse.ofError(MessagesUtils.get("bot.verification.YZSB"));
    }


    public ApiResponse<?> checkCaptchaHandler(String url, Data data, HttpServletRequest request, BlockException ex) {
        log.warn("限流触发：checkCaptcha，URL：{}", url);
        return ApiResponse.ofError(MessagesUtils.get("system.busy"));
    }

    @lombok.Data
    public static class Data {
        private String id;
        private ImageClass data;
        private String account;
    }

}
