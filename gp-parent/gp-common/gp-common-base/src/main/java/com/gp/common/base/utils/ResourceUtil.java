package com.gp.common.base.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import lombok.SneakyThrows;

import java.io.File;
import java.io.Serializable;

/**
 * @author axing
 * @version 1.0
 * @date 2023/11/9/009 23:03
 */
public class ResourceUtil implements Serializable {

    private static final long serialVersionUID = 1L;


    @SneakyThrows
    public static File getFileFromResource(String filePath){
        File file = FileUtil.file(filePath);
        if (file.exists()) {
            return file;
        }else {
            FileUtil.writeFromStream(new ClassPathResource(filePath).getStream(), file);
            return file;
        }
    }


}
