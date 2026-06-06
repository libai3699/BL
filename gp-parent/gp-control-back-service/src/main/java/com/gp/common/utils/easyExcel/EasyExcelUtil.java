package com.gp.common.utils.easyExcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * @ClassName EasyExcelUtil
 * @Description Excel工具类
 * @Author mazhitao
 * @Date 2020/3/30 21:43
 * @Version 1.0
 **/
public class EasyExcelUtil {

    /**
     * 读取Excel文件
     *
     * @param fileName
     * @param clazz
     * @return
     */
    public static List<?> readFile(String fileName, Class<?> clazz) {
        ExcelListener excelListener = new ExcelListener();
        EasyExcel.read(fileName, clazz, excelListener).sheet().doRead();
        List<Object> list = excelListener.getDatas();
        return list;
    }

    public static List<?> readFile(InputStream inputStream, Class<?> clazz) {
        ExcelListener excelListener = new ExcelListener();
        EasyExcel.read(inputStream, clazz, excelListener).sheet().doRead();
        List<Object> list = excelListener.getDatas();
        return list;
    }

    public static List<?> readFiles(InputStream inputStream, Class<?> clazz) {
        ExcelListener excelListener = new ExcelListener();
        ExcelReaderBuilder read = EasyExcel.read(inputStream, clazz, excelListener);
        read.doReadAllSync();
        List<Object> list = excelListener.getDatas();
        return list;
    }

    public static File assemblyFile(String filePath, String fileName) {
        //先判断文件夹是否存在，避免不存在时报错
        File filePathExist = new File(filePath);
        if (!filePathExist.exists()) {
            filePathExist.mkdirs();
        }
        //默认导出xlsx格式
        return new File(filePath + fileName + ".xlsx");
    }





}
