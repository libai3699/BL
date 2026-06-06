package com.gp.common.base.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * @author axing
 * @version 1.0
 * @date 2023/11/24/024 21:08
 */
public class UrlFileUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    public static List<File> downFile(String dir, List<String> fileUrls) {
        List<File> files = CollUtil.newArrayList();
        for (String fileUrl : fileUrls) {
            downFile(dir, fileUrl);
        }
        return files;
    }


    public static File downFile(String dir, String fileUrl) {
        File downFile = HttpUtil.downloadFileFromUrl(fileUrl, FileUtil.mkdir(dir));
        return FileUtil.rename(downFile, IdUtil.fastSimpleUUID() + "." + FileUtil.extName(downFile), true);
    }


    public static File downFile2(String dir, String fileUrl) {
        return HttpUtil.downloadFileFromUrl(fileUrl, FileUtil.mkdir(dir));
    }
}
