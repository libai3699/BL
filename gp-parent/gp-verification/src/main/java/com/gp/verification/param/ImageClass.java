package com.gp.verification.param;

import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 验证码校验请求体（对齐 tianai-captcha 1.5.x ImageCaptchaTrack 字段命名）
 */
@Data
public class ImageClass {
    /** 背景图宽度 */
    private Integer bgImageWidth;
    /** 背景图高度 */
    private Integer bgImageHeight;
    /** 模板图（点选提示图）宽度 */
    private Integer templateImageWidth;
    /** 模板图（点选提示图）高度 */
    private Integer templateImageHeight;
    /** 开始时间 */
    private Date startTime;
    /** 结束时间 */
    private Date stopTime;
    /** 点击轨迹 */
    private List<ImageCaptchaTrack.Track> trackList;
    /** 扩展数据 */
    private Object data;
}
