package com.gp.verification.config;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.generator.ImageCaptchaGenerator;
import cloud.tianai.captcha.generator.ImageCaptchaGeneratorProvider;
import cloud.tianai.captcha.generator.ImageTransform;
import cloud.tianai.captcha.generator.common.model.dto.GenerateParam;
import cloud.tianai.captcha.generator.common.model.dto.ParamKeyEnum;
import cloud.tianai.captcha.generator.impl.StandardWordClickImageCaptchaGenerator;
import cloud.tianai.captcha.interceptor.CaptchaInterceptor;
import cloud.tianai.captcha.resource.ImageCaptchaResourceManager;
import cloud.tianai.captcha.resource.common.model.dto.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 文字点选验证码生成器：替换默认中文字符源为 A-Z 26 个英文字母
 * <p>
 * tianai-captcha 1.5.x 的 {@code SpringMultiImageCaptchaGenerator.doInit()} 会扫描 Spring 容器中
 * 所有 {@link ImageCaptchaGeneratorProvider} 类型的 Bean，并按 {@link ImageCaptchaGeneratorProvider#getType()}
 * 注册到对应类型，覆盖内置的 SLIDER/ROTATE/CONCAT/WORD_IMAGE_CLICK 默认实现。
 */
@Configuration
public class CaptchaConfig {

    private static final char[] ALPHABET = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    @Bean
    public ImageCaptchaGeneratorProvider alphabetWordClickProvider() {
        return new ImageCaptchaGeneratorProvider() {
            @Override
            public ImageCaptchaGenerator get(ImageCaptchaResourceManager resourceManager,
                                             ImageTransform imageTransform,
                                             CaptchaInterceptor interceptor) {
                return new AlphabetWordClickCaptchaGenerator(resourceManager, imageTransform, interceptor);
            }

            @Override
            public String getType() {
                return CaptchaTypeConstant.WORD_IMAGE_CLICK;
            }
        };
    }

    /**
     * 自定义文字点选生成器：从 A-Z 中无重复随机抽取 (checkClickCount + interferenceCount) 个字母
     */
    public static class AlphabetWordClickCaptchaGenerator extends StandardWordClickImageCaptchaGenerator {

        public AlphabetWordClickCaptchaGenerator(ImageCaptchaResourceManager resourceManager,
                                                  ImageTransform imageTransform,
                                                  CaptchaInterceptor interceptor) {
            super(resourceManager, imageTransform, interceptor);
        }

        @Override
        protected List<Resource> randomGetClickImgTips(GenerateParam param) {
            Integer checkCount = param.getOrDefault(ParamKeyEnum.CLICK_CHECK_CLICK_COUNT, getCheckClickCount());
            Integer interfereCount = param.getOrDefault(ParamKeyEnum.CLICK_INTERFERENCE_COUNT, getInterferenceCount());
            int tipSize = checkCount + interfereCount;
            if (tipSize > ALPHABET.length) {
                throw new IllegalStateException("字母不够用：tipSize=" + tipSize + " > 26");
            }
            Set<Character> picked = new LinkedHashSet<>(tipSize);
            ThreadLocalRandom rnd = ThreadLocalRandom.current();
            while (picked.size() < tipSize) {
                picked.add(ALPHABET[rnd.nextInt(ALPHABET.length)]);
            }
            List<Resource> tips = new ArrayList<>(tipSize);
            for (Character c : picked) {
                // type=null + data=字母 表示这是一个文字资源（非图片）
                tips.add(new Resource(null, String.valueOf(c)));
            }
            return tips;
        }
    }
}
