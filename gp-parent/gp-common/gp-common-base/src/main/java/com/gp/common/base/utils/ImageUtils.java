package com.gp.common.base.utils;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.regex.Pattern;

/**
 * @author axing
 * @version 1.0
 * @date 2022/11/5 21:26
 */
public class ImageUtils {


    /**
     * 图片校验（上传单个图片）
     *
     * @param file
     * @return void
     * @Description
     * @author fuxshen
     * @date 2022-04-29 08:58:17
     **/
    public static void checkImage(MultipartFile file) {

        if (ObjectUtils.isEmpty(file) || file.getSize() <= 0) {
            Assert.isFalse(true,"上传内容为空");
        }

        if (!imageCheck(file)) {
            Assert.isFalse(true,"请上传图片格式为【jpg、png、tiff、webp、heif、gif、bmp】");
        }
    }

    /**
     * 图片和视频校验(上传多个)
     *
     * @param file
     * @return void
     * @Description
     * @author fuxshen
     * @date 2022-04-29 14:52:12
     **/
    public static void checkHomeImage(MultipartFile[] file) {
        if (ObjectUtils.isEmpty(file)) {

            Assert.isFalse(true,"上传内容为空");
        }
        for (MultipartFile multipartFile : file) {

            if (imageCheck(multipartFile)){
                if (!imageCheck(multipartFile)) {
                    Assert.isFalse(true,"请上传图片格式为【jpg、png、tiff、webp、heif、gif、bmp】");
                }
            }

            if (videoCheck(multipartFile)){
                if (!videoCheck(multipartFile)) {
                    Assert.isFalse(true,"请上传正确视频格式为【mp4、flv、avi、mov、rm、rmvb、wmv、hevc】");
                }
            }


        }
    }

    /**
     * 校验视频的正则
     *
     * @param file
     * @return boolean
     * @Description
     * @author fuxshen
     * @date 2022-04-29 15:08:06
     **/
    public static boolean videoCheck(MultipartFile file) {
        String reg = "(mp4|flv|avi|mov|rm|rmvb|wmv|hevc)";
        Pattern pattern = Pattern.compile(reg);
        boolean flag = pattern.matcher(file.getOriginalFilename().toLowerCase()).find();
        return flag;
    }

    public static boolean isImage(String filename) {
        String ext = StringUtils.getFilenameExtension(filename);
        if (ext == null) return false;

        ext = ext.toLowerCase();

        return ext.equals("jpg")
                || ext.equals("jpeg")
                || ext.equals("png")
                || ext.equals("gif")
                || ext.equals("bmp")
                || ext.equals("webp");
    }
    /**
     * 校验文件的正则
     *
     * @param file
     * @return boolean
     * @Description
     * @author fuxshen
     * @date 2022-04-29 15:08:06
     **/
    public static boolean fileCheck(MultipartFile file) {
        String reg = "(png|jpg|jpeg|gif|webp|mov|mp4|mp3|avi)";
        Pattern pattern = Pattern.compile(reg);
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isBlank(originalFilename)) {
            Assert.isFalse(true,"上传文件没有文件名 !");
        }
        boolean flag = pattern.matcher(originalFilename.toLowerCase()).find();
        return flag;
    }

    /**
     * 校验文件的正则
     *
     * @param fileName
     * @return boolean
     * @Description
     * @author fuxshen
     * @date 2022-04-29 15:08:06
     **/
    public static boolean fileCheck(String fileName) {
        String reg = "(png|jpg|jpeg|gif|webp|mov|mp4|mp3|xlsx)";
        Pattern pattern = Pattern.compile(reg);
        if (StrUtil.isBlank(fileName)) {
            Assert.isFalse(true,"上传文件没有文件名 !");
        }
        boolean flag = pattern.matcher(fileName.toLowerCase()).find();
        return flag;
    }

    /**
     * 图片校验正则
     *
     * @param file
     * @return boolean
     * @Description
     * @author fuxshen
     * @date 2022-04-29 15:25:31
     **/
    public static boolean imageCheck(MultipartFile file) {
        String reg = "(jpg|jpeg|png|tiff|webp|heif|gif|bmp)";
        Pattern pattern = Pattern.compile(reg);
        boolean flag = pattern.matcher(file.getOriginalFilename().toLowerCase()).find();
        return flag;
    }

    /**
     * 判断文件大小
     *
     * @param len 文件长度
     * @param size 限制大小
     * @param unit  限制单位（B,K,M,G）
     * @return boolean
     * @Description
     * @author fuxshen
     * @date 2022-10-10 11:47:36
     **/
    public static boolean checkFileSize(Long len, int size, String unit) {
        double fileSize = 0;
        if ("B".equals(unit.toUpperCase())) {
            fileSize = (double) len;
        } else if ("K".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1024;
        } else if ("M".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1048576;
        } else if ("G".equals(unit.toUpperCase())) {
            fileSize = (double) len / 1073741824;
        }
        if (fileSize > size) {
            return false;
        }
        return true;
    }
}
