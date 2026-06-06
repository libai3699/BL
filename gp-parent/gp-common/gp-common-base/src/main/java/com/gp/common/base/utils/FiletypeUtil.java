package com.gp.common.base.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author axing
 * @version 1.0
 * @date 2023/11/4/004 11:24
 */
public class FiletypeUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    public static String createFileNameByOld(String oldUrl) {
        return IdUtil.fastSimpleUUID() + "." + FileUtil.extName(oldUrl);
    }

    public static String createFileNameByExtName(String extName) {
        return IdUtil.fastSimpleUUID() + "." + extName;
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
    public static boolean isVideo(File file) {
        String reg = "(mp4|flv|avi|mov|rm|rmvb|wmv|hevc)";
        Pattern pattern = Pattern.compile(reg);
        return pattern.matcher(FileUtil.extName(file).toLowerCase()).find();
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
    public static boolean isVideo(String file) {
        String reg = "(mp4|flv|avi|mov|rm|rmvb|wmv|hevc)";
        Pattern pattern = Pattern.compile(reg);
        return pattern.matcher(FileUtil.extName(file).toLowerCase()).find();
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
    public static boolean isPic(File file) {
        String reg = "(png|jpg|jpeg|svg|webp|gif|bmp)";
        Pattern pattern = Pattern.compile(reg);
        return pattern.matcher(FileUtil.extName(file).toLowerCase()).find();
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
    public static boolean isAudio(File file) {
        String reg = "(mp3|wav|wma|aac)";
        Pattern pattern = Pattern.compile(reg);
        return pattern.matcher(FileUtil.extName(file).toLowerCase()).find();
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
    public static boolean isGIf(File file) {
        String reg = "(gif)";
        Pattern pattern = Pattern.compile(reg);
        return pattern.matcher(FileUtil.extName(file).toLowerCase()).find();
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
    public static boolean isGIf(String file) {
        String reg = "(gif)";
        Pattern pattern = Pattern.compile(reg);
        return pattern.matcher(FileUtil.extName(file).toLowerCase()).find();
    }
    /**
     * 读取网络中的图片
     *
     * @param urls 文件链接
     * @return
     */
    public static List<File> URLToFile(List<String> urls) {
        List<File> files = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(urls)) {
            for (String url : urls) {
                String fileName = IdUtil.getSnowflake().nextIdStr() + "." + FileUtil.extName(url);
                File file1 = new File(fileName);
                try {
                    URL url1 = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                    // 添加请求头
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                    connection.setRequestProperty("Referer", "https://example.com");
                    connection.setRequestMethod("GET");
                    FileUtils.copyToFile(connection.getInputStream(), file1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                files.add(file1);
            }
        }

        return files;
    }

    public static InputStream URLToInputStream(String url) {
        if (StringUtils.isNotBlank(url)) {
            try {
                URL url1 = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) url1.openConnection();
                // 添加请求头
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.setRequestProperty("Referer", "https://example.com");
                connection.setRequestMethod("GET");
                return connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
