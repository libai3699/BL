package com.gp.maintain.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.fastjson.JSON;
import com.gp.maintain.domain.GamePic;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ExcelReadExample {
    public static void main(String[] args) {
        // 指定Excel文件路径
        String filePath = "D:\\wallet\\game.xlsx";
        String gamePlate = "hacksaw-ops";
        String fileDirs = "D:\\wallet\\Hacksaw-F";
        //竖图 中文
//        String picName = "-zh-S.png";
        //竖图 英文
//        String picName = "-us-S.png";
        //方图 中文
        String picName = "-zh-F.png";

        // 创建ExcelReader对象，读取Excel文件
        ExcelReader reader = ExcelUtil.getReader(filePath);
        List<GamePic> readAll = reader.readAll(GamePic.class);
        List<GamePic> filteredUsers = readAll.stream()
                .filter(gamePic -> gamePic.getPlateCode().equals(gamePlate)) // 过滤条件
                .collect(Collectors.toList()); // 收集为列表
        // 遍历打印
        for (GamePic row : filteredUsers) {
            System.out.println(JSON.toJSONString(row));
        }
        updateFileName(fileDirs, filteredUsers, picName);
        // 关闭资源
        reader.close();
    }

    public static void updateFileName(String fileDirs, List<GamePic> gamePics, String picName) {
        // 指定文件夹路径
        String folderPath = fileDirs; // 替换为目标文件夹路径

        // 获取文件夹对象
        File folder = new File(folderPath);

        // 检查文件夹是否存在
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("指定路径不是文件夹或文件夹不存在！");
            return;
        }

        // 获取文件夹中的所有文件
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("文件夹为空！");
            return;
        }

        // 遍历文件夹中的文件并修改名称
        int count = 1; // 用于生成新文件名的计数器
        for (File file : files) {
            if (file.isFile()) { // 仅处理文件，忽略子文件夹
                // 获取文件扩展名
                String extName = FileUtil.extName(file);
                String name = FileUtil.getName(file).replace( "." + extName, "");
                // 通过流过滤 status == 1，并找到第一个
                Optional<GamePic> result = gamePics.stream()
                        .filter(gamePic -> getSimilarityPercentage(gamePic.getGameNameZh().toLowerCase(), name.toLowerCase()) > 0.8) // 过滤条件
                        .findFirst(); // 获取第一个符合条件的
                // 构建新文件名，例如 "renamed_1.jpg"
                if (result.isPresent()) {

                    String newFileName = result.get().getId() + picName + "." + extName;
                    // 修改文件名
                    File newFile = FileUtil.rename(file, newFileName, true);
                    System.out.println("文件重命名成功：" + file.getName() + " -> " + newFile.getName());
                }

                count++; // 计数器递增
            }
        }
    }


    /**
     * 获取两个字符串的相似度百分比
     *
     * @param str1 第一个字符串
     * @param str2 第二个字符串
     * @return 相似度百分比（0.0 ~ 100.0）
     */
    public static double getSimilarityPercentage(String str1, String str2) {
        double similarity = StrUtil.similar(str1, str2);
        return similarity;
    }



}
