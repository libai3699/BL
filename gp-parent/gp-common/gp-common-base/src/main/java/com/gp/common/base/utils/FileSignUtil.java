package com.gp.common.base.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.core.lang.Console;
import lombok.SneakyThrows;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * @author axing
 * @version 1.0
 * @date 2023/11/9/009 23:03
 */
public class FileSignUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    @SneakyThrows
    public static String getFileSign(String path){
        return getFileSign(new File(path));
    }

    @SneakyThrows
    public static String getFileSign(File file){
        return getFileSign(new FileInputStream(file));
    }

    @SneakyThrows
    public static String getFileSign(FileInputStream fileInputStream){
        return DigestUtils.md5DigestAsHex(fileInputStream);
    }

    public static void main(String[] args) {
        File file = FileUtil.file("E:\\tmp\\out.txt");
        WatchMonitor watchMonitor = WatchMonitor.create(file, WatchMonitor.ENTRY_MODIFY);
        watchMonitor.setWatcher(new Watcher(){
            @Override
            public void onCreate(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("创建：{}-> {}", currentPath, obj);
            }

            @Override
            public void onModify(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("修改：{}-> {}", currentPath, obj);
            }

            @Override
            public void onDelete(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("删除：{}-> {}", currentPath, obj);
            }

            @Override
            public void onOverflow(WatchEvent<?> event, Path currentPath) {
                Object obj = event.context();
                Console.log("Overflow：{}-> {}", currentPath, obj);
            }
        });
        watchMonitor.start();
    }
}
