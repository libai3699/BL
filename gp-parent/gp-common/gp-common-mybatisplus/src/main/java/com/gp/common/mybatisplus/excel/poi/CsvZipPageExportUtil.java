/**
 * CsvZipExportUtil - 支持将数据分页导出为多个 CSV 文件（每1万个为1份），压缩成 ZIP 文件下载。
 * 使用 CsvBatchSupplier<T> 接口实现分页流式处理，适合大数据量（如百万级）场景。
 */

package com.gp.common.mybatisplus.excel.poi;

import cn.hutool.core.convert.Convert;
import com.common.core.config.RuoYiConfig;
import com.common.core.result.AjaxResult;
import com.gp.common.base.excel.annotation.Excel;
import org.slf4j.MDC;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CsvZipPageExportUtil<T> {

    private static final int BATCH_SIZE = 10000;
    private final Class<T> clazz;

    public CsvZipPageExportUtil(Class<T> clazz) {
        this.clazz = clazz;
    }

    public interface CsvBatchSupplier<T> {
        List<T> fetch(int pageIndex, int pageSize);
    }

    public AjaxResult exportCsvZip(String filePrefix, CsvBatchSupplier<T> supplier) {
        try {
            // 创建CSV临时目录
            String baseDir = RuoYiConfig.getDownloadPath() + UUID.randomUUID() + "/";
            File dir = new File(baseDir);
            if (!dir.exists()) dir.mkdirs();

            List<File> csvFiles = new ArrayList<>();
            Field[] fields = getExportFields();
            int pageIndex = 0;
            int totalExported = 0;

            while (true) {
                List<T> pageData = supplier.fetch(pageIndex++, BATCH_SIZE);
                if (pageData == null || pageData.isEmpty()) break;

                String fileName = filePrefix + "_part" + pageIndex + ".csv";
                File csv = new File(baseDir + fileName);
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csv), StandardCharsets.UTF_8))) {
                    // 写表头
                    writer.write(getHeader(fields));
                    writer.newLine();
                    for (T item : pageData) {
                        writer.write(getRow(fields, item));
                        writer.newLine();
                    }
                }
                totalExported += pageData.size();
                csvFiles.add(csv);
                if (pageData.size() < BATCH_SIZE) break; // 最后一页
            }

            if (csvFiles.isEmpty()) return AjaxResult.error("无数据导出");

            // 压缩为 ZIP
            String zipName = filePrefix + ".zip";
            String zipPath = RuoYiConfig.getDownloadPath() + zipName;
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath))) {
                for (File csv : csvFiles) {
                    try (FileInputStream fis = new FileInputStream(csv)) {
                        zos.putNextEntry(new ZipEntry(csv.getName()));
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = fis.read(buffer)) != -1) {
                            zos.write(buffer, 0, len);
                        }
                        zos.closeEntry();
                    }
                }
            }

            MDC.put("downUrl", zipPath);
            return AjaxResult.success("成功导出 " + totalExported + " 条记录", zipName);
        } catch (Exception e) {
            return AjaxResult.error("导出CSV失败: " + e.getMessage());
        }
    }

    private Field[] getExportFields() {
        List<Field> list = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Excel.class)) {
                field.setAccessible(true);
                list.add(field);
            }
        }
        return list.toArray(new Field[0]);
    }

    private String getHeader(Field[] fields) {
        List<String> headers = new ArrayList<>();
        for (Field f : fields) {
            Excel excel = f.getAnnotation(Excel.class);
            headers.add('"' + excel.name() + '"');
        }
        return String.join(",", headers);
    }

    private String getRow(Field[] fields, T item) throws IllegalAccessException {
        List<String> values = new ArrayList<>();
        for (Field f : fields) {
            Object val = f.get(item);
            values.add('"' + Convert.toStr(val, "") + '"');
        }
        return String.join(",", values);
    }

    public static String getDownUrl() {
        return MDC.get("downUrl");
    }
}
