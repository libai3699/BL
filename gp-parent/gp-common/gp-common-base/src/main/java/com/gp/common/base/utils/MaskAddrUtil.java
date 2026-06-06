package com.gp.common.base.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import cn.hutool.extra.qrcode.QrConfig;
import cn.hutool.http.HttpUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.File;
import java.io.Serializable;

/**
 * @author axing
 * @version 1.0
 * @date 2021/8/17 17:35
 */
@Slf4j
@Data
public class MaskAddrUtil implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * @deprecated 使用 {@link #getFile(Long, Integer, String, Integer, String)}，
     * 由调用方注入 FtLogoProp 后传入 imgUrl。
     * 老方法的硬编码 switch 在多服务器场景下会取错 logo。
     */
    @Deprecated
    public static File getFile(Long userId, Integer currencyId, String addr, Integer dbCodeInt) {
        return getFile(userId, currencyId, addr, dbCodeInt, changeDbCode(dbCodeInt));
    }

    public static File getFile(Long userId, Integer currencyId, String addr, Integer dbCodeInt, String imgUrl) {
        File file = FileUtil.file("static/recharge/" + userId + "-" + currencyId + "-" + dbCodeInt + ".png");
        //不存在就生成用户的充值码
        if (!file.exists()) {
            QrConfig qrConfig = new QrConfig();
            qrConfig.setHeight(390);
            qrConfig.setWidth(390);
            qrConfig.setMargin(2);
            qrConfig.setBackColor(Color.WHITE);
            qrConfig.setImg(resolveLogo(imgUrl, dbCodeInt));
            QrCodeUtil.generate(addr, qrConfig, file);
        }
        return file;
    }

    /**
     * 解析 logo 来源：
     *  - http(s) URL：下载到 static/logo-cache/{dbCode}-{hash}.png 缓存复用
     *  - 本地 classpath 路径：走 ResourceUtil 原逻辑
     *  - 空：兜底 static/ft.png
     */
    private static File resolveLogo(String imgUrl, Integer dbCodeInt) {
        if (StrUtil.isBlank(imgUrl)) {
            return ResourceUtil.getFileFromResource("static/ft.png");
        }
        if (StrUtil.startWithIgnoreCase(imgUrl, "http")) {
            String name = dbCodeInt + "-" + Integer.toHexString(imgUrl.hashCode()) + ".png";
            File cached = FileUtil.file("static/logo-cache/" + name);
            if (!cached.exists()) {
                try {
                    FileUtil.mkParentDirs(cached);
                    HttpUtil.downloadFile(imgUrl, cached);
                } catch (Exception e) {
                    log.error("下载二维码 logo 失败，回退到默认图。url={}", imgUrl, e);
                    return ResourceUtil.getFileFromResource("static/ft.png");
                }
            }
            return cached;
        }
        return ResourceUtil.getFileFromResource(imgUrl);
    }

    private static String changeDbCode(Integer dbCodeInt) {
        String imgUrl = "static/ft.png";
        switch (dbCodeInt) {
            case 1:
                imgUrl = "static/jincai.png";
                return imgUrl;
            case 2:
                imgUrl = "static/feiyun.png";
                return imgUrl;
            case 3:
                imgUrl = "static/idg.png";
                return imgUrl;
            case 4:
                imgUrl = "static/bnbet.png";
                return imgUrl;
            case 5:
                imgUrl = "static/pgsj.png";
                return imgUrl;
            case 6:
                imgUrl = "static/xinbi.png";
                return imgUrl;
            case 7:
                imgUrl = "static/v8.png";
                return imgUrl;
            case 8:
                imgUrl = "static/hongniu.png";
                return imgUrl;
            case 9:
                imgUrl = "static/hlw.png";
                return imgUrl;
            case 10:
                imgUrl = "static/jvxing.png";
                return imgUrl;
            case 11:
                imgUrl = "static/yl8.png";
                return imgUrl;
            case 12:
                imgUrl = "static/pgylc.png";
                return imgUrl;
            case 13:
                imgUrl = "static/yongli.png";
                return imgUrl;
            case 14:
                imgUrl = "static/wanli.png";
                return imgUrl;
            default:
                return imgUrl; // 或者抛出一个异常，具体取决于业务需求
        }


    }
}
