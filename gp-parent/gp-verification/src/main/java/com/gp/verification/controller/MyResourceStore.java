package com.gp.verification.controller;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import cloud.tianai.captcha.resource.impl.LocalMemoryResourceStore;
import cloud.tianai.captcha.resource.impl.provider.ClassPathResourceProvider;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 文字点选验证码资源注册：注册 11 张背景图供 A-Z 文字点选随机使用
 * <p>
 * tianai-captcha 1.5.x 的 ResourceStore 由 starter 自动装配为 {@link LocalMemoryResourceStore}，
 * 本类用 @Component 替换默认 Bean（由 starter 的 @ConditionalOnMissingBean 决定生效）。
 */
@Component
public class MyResourceStore extends LocalMemoryResourceStore {

    @PostConstruct
    public void registerResources() {
        // 文字点选 (WORD_IMAGE_CLICK) 背景 —— 11 张图随机选
        addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource(ClassPathResourceProvider.NAME, "bgimages/48.jpg", "default"));
        addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource(ClassPathResourceProvider.NAME, "bgimages/1.jpg", "default"));
        addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource(ClassPathResourceProvider.NAME, "bgimages/2.jpg", "default"));
        addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource(ClassPathResourceProvider.NAME, "bgimages/3.jpg", "default"));
        addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource(ClassPathResourceProvider.NAME, "bgimages/4.jpg", "default"));
        addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource(ClassPathResourceProvider.NAME, "bgimages/5.jpg", "default"));
        addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource(ClassPathResourceProvider.NAME, "bgimages/6.jpg", "default"));
        addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource(ClassPathResourceProvider.NAME, "bgimages/7.jpg", "default"));
        addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource(ClassPathResourceProvider.NAME, "bgimages/8.jpg", "default"));
        addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource(ClassPathResourceProvider.NAME, "bgimages/9.jpg", "default"));
        addResource(CaptchaTypeConstant.WORD_IMAGE_CLICK, new Resource(ClassPathResourceProvider.NAME, "bgimages/10.jpg", "default"));
    }
}
