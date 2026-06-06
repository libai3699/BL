/**
 * CsvZipExportUtil - 替代原 ExcelUtil 的工具类，支持将数据导出为多个 CSV 文件（每1万个为1份），压缩成 ZIP 文件下载。
 */

package com.gp.common.mybatisplus.excel.poi;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.common.core.config.RuoYiConfig;
import com.common.core.constant.S3DirConstant;
import com.common.core.result.AjaxResult;
import com.common.core.util.AmazonService;
import com.common.core.util.StringUtils;
import com.gp.common.base.excel.annotation.Excel;
import org.slf4j.MDC;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.apache.commons.io.FileUtils.deleteDirectory;

public class CsvZipExportUtil<T> {

    private AmazonService amazonService;

    private String fileAdmin;

    public CsvZipExportUtil(AmazonService amazonService, String fileAdmin, Class<T> clazz) {
        this.amazonService = amazonService;
        this.fileAdmin = fileAdmin;
        this.clazz = clazz;
    }

    public void setAmazonService(AmazonService amazonService) {
        this.amazonService = amazonService;
    }

    public void setFileAdmin(String fileAdmin) {
        this.fileAdmin = fileAdmin;
    }

    private static final int BATCH_SIZE = 10000;
    private final Class<T> clazz;

    public CsvZipExportUtil(Class<T> clazz) {
        this.clazz = clazz;
    }

    public AjaxResult exportCsvZip(List<T> list, String filePrefix) {
        try {
            if (list == null || list.isEmpty()) {
                return AjaxResult.error("导出数据为空");
            }

            // 创建CSV临时目录
            String baseDir = RuoYiConfig.getDownloadPath() + UUID.randomUUID() + "/";
            File dir = new File(baseDir);
            if (!dir.exists()) dir.mkdirs();

            List<File> csvFiles = new ArrayList<>();
            int total = list.size();
            int parts = (int) Math.ceil(total / (double) BATCH_SIZE);
            Field[] fields = getExportFields();

            for (int i = 0; i < parts; i++) {
                int start = i * BATCH_SIZE;
                int end = Math.min((i + 1) * BATCH_SIZE, total);
                List<T> subList = list.subList(start, end);

                String fileName = filePrefix + "_part" + (i + 1) + ".csv";
                File csv = new File(baseDir + fileName);
                try (BufferedWriter writer =
                             new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(csv.toPath()),
                                     StandardCharsets.UTF_8))) {
                    // 添加UTF-8 BOM确保Excel能正确识别编码
                    writer.write("\uFEFF");
                    // 写表头
                    writer.write(getHeader(fields));
                    writer.newLine();
                    for (T item : subList) {
                        writer.write(getRow(fields, item));
                        writer.newLine();
                    }
                }
                csvFiles.add(csv);
            }

            // 压缩为 ZIP
            String zipName = filePrefix + "_" + UUID.randomUUID() + ".zip";
            String zipPath = RuoYiConfig.getDownloadPath() + zipName;

            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(Paths.get(zipPath)),
                    StandardCharsets.UTF_8)) {
                for (File csv : csvFiles) {
                    try (FileInputStream fis = new FileInputStream(csv)) {
                        // 加入相对路径（让 zip 里保留结构）
                        String relativePath = csv.getAbsolutePath().replace(baseDir, "");
                        zos.putNextEntry(new ZipEntry(relativePath));
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = fis.read(buffer)) != -1) {
                            zos.write(buffer, 0, len);
                        }
                        zos.closeEntry();
                    }
                }
            }

            // 删除 CSV 临时文件和目录
            deleteDirectory(new File(baseDir));
            // 设置下载路径
//            MDC.put("downUrl", zipPath);
            //上传s3
            File file = new File(zipPath);
            try (InputStream in = Files.newInputStream(file.toPath())) {
                String path = amazonService.uploadFileTos3bucket(zipName, in, S3DirConstant.zipDir);
                // 删除zip文件
                boolean delete = file.delete();
                in.close();
                return AjaxResult.successMsg(fileAdmin + path);
            } catch (IOException e) {
                return AjaxResult.error("文件操作失败: " + e.getMessage());
            }
        } catch (Exception e) {
            return AjaxResult.error("导出CSV失败: " + e.getMessage());
        }
    }

    public String exportCsvZipPath(List<T> list, String filePrefix) {
        try {
            if (list == null || list.isEmpty()) {
                return null;
            }

            // 创建CSV临时目录
            String baseDir = RuoYiConfig.getDownloadPath() + UUID.randomUUID() + "/";
            File dir = new File(baseDir);
            if (!dir.exists()) dir.mkdirs();

            List<File> csvFiles = new ArrayList<>();
            int total = list.size();
            int parts = (int) Math.ceil(total / (double) BATCH_SIZE);
            Field[] fields = getExportFields();

            for (int i = 0; i < parts; i++) {
                int start = i * BATCH_SIZE;
                int end = Math.min((i + 1) * BATCH_SIZE, total);
                List<T> subList = list.subList(start, end);

                String fileName = filePrefix + "_part" + (i + 1) + ".csv";
                File csv = new File(baseDir + fileName);
                try (BufferedWriter writer =
                             new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(csv.toPath()),
                                     StandardCharsets.UTF_8))) {
                    // 添加UTF-8 BOM确保Excel能正确识别编码
                    writer.write("\uFEFF");
                    // 写表头
                    writer.write(getHeader(fields));
                    writer.newLine();
                    for (T item : subList) {
                        writer.write(getRow(fields, item));
                        writer.newLine();
                    }
                }
                csvFiles.add(csv);
            }

            // 压缩为 ZIP
            String zipName = filePrefix + "_" + UUID.randomUUID() + ".zip";
            String zipPath = RuoYiConfig.getDownloadPath() + zipName;

            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(Paths.get(zipPath)),
                    StandardCharsets.UTF_8)) {
                for (File csv : csvFiles) {
                    try (FileInputStream fis = new FileInputStream(csv)) {
                        // 加入相对路径（让 zip 里保留结构）
                        String relativePath = csv.getAbsolutePath().replace(baseDir, "");
                        zos.putNextEntry(new ZipEntry(relativePath));
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = fis.read(buffer)) != -1) {
                            zos.write(buffer, 0, len);
                        }
                        zos.closeEntry();
                    }
                }
            }

            // 删除 CSV 临时文件和目录
            deleteDirectory(new File(baseDir));
            // 设置下载路径
//            MDC.put("downUrl", zipPath);
            //上传s3
            File file = new File(zipPath);
            try (InputStream in = Files.newInputStream(file.toPath())) {
                String path = amazonService.uploadFileTos3bucket(zipName, in, S3DirConstant.zipDir);
                // 删除zip文件
                boolean delete = file.delete();
                in.close();
                return fileAdmin + path;
            } catch (IOException e) {
                throw new RuntimeException("文件操作失败: " + e.getMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("导出CSV失败: " + e.getMessage());
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
            Excel excel = f.getAnnotation(Excel.class);
            Object val = f.get(item);

            String strVal = "";

            if (val != null) {
                // 1. 优先处理映射表达式
                if (StringUtils.isNotEmpty(excel.readConverterExp())) {
                    strVal = convertByExp(val.toString(), excel.readConverterExp());
                }
                // 2. 日期格式
                else if (val instanceof Date && StringUtils.isNotEmpty(excel.dateFormat())) {
                    strVal = DateUtil.format((Date) val, excel.dateFormat());
                }
                // 3. BigDecimal 精度处理
                else if (val instanceof BigDecimal) {
                    DecimalFormat df = new DecimalFormat("0.##########");
                    strVal = df.format(val);
                }
                // 4. 整型处理（不加引号）
                else if (val instanceof Long || val instanceof Integer) {
                    strVal = val.toString();
                }
                // 5. 默认字符串处理（加引号防止逗号破坏格式）
                else {
                    strVal = Convert.toStr(val, "");
                }
            }

            // 判断是否需要加引号（只有整型不加）
            if (val instanceof Long || val instanceof Integer) {
                values.add(strVal);
            } else {
                values.add('"' + strVal.replace("\"", "\"\"") + '"'); // CSV规范：转义引号
            }
        }
        return String.join(",", values);
    }

    /**
     * 解析导出值 0=男,1=女,2=未知 → 0 -> 男
     */
    public static String convertByExp(String propertyValue, String converterExp) {
        try {
            String[] convertSource = converterExp.split(",");
            for (String item : convertSource) {
                String[] itemArray = item.split("=");
                if (itemArray[0].equals(propertyValue)) {
                    return itemArray[1];
                }
            }
        } catch (Exception ignored) {
        }
        return propertyValue;
    }

    public static String getDownUrl() {
        return MDC.get("downUrl");
    }
}
