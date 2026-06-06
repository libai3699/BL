package com.gp.common.base.excel.poi;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.gp.common.base.excel.annotation.Excel;
import lombok.SneakyThrows;
import org.slf4j.MDC;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.apache.commons.io.FileUtils.deleteDirectory;

public class CsvZipExport2Util<T> {

    private final Class<T> clazz;
    private final String filePrefix;
    private final String baseDir;
    private final String zipName;
    private final String zipPath;
    private final Field[] fields;
    private final List<File> csvFiles = new ArrayList<>();

    public CsvZipExport2Util(Class<T> clazz, String filePrefix) {
        this.clazz = clazz;
        this.filePrefix = filePrefix;
        this.baseDir = "uploadPath/download/" + UUID.randomUUID() + "/";
        this.zipName = filePrefix + "_" + UUID.randomUUID() + ".zip";
        this.zipPath = "uploadPath/download/" + zipName;
        new File(baseDir).mkdirs();
        this.fields = getExportFields();
    }

    @SneakyThrows
    public void appendCsvPart(List<T> list, int partIndex) {
        if (list == null || list.isEmpty()) return;

        String fileName = filePrefix + "_part" + partIndex + ".csv";
        File csv = new File(baseDir + fileName);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csv), StandardCharsets.UTF_8))) {
            writer.write(getHeader(fields));
            writer.newLine();
            for (T item : list) {
                writer.write(getRow(fields, item));
                writer.newLine();
            }
        }
        csvFiles.add(csv);
    }

    @SneakyThrows
    public String finalizeZip() {
        if (csvFiles.isEmpty()) throw new RuntimeException("没有可压缩的CSV文件");

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath))) {
            for (File csv : csvFiles) {
                try (FileInputStream fis = new FileInputStream(csv)) {
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
        deleteDirectory(new File(baseDir));
        MDC.put("downUrl", zipPath);
        return zipName;
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
                if (StrUtil.isNotEmpty(excel.readConverterExp())) {
                    strVal = convertByExp(val.toString(), excel.readConverterExp());
                } else if (val instanceof Date && StrUtil.isNotEmpty(excel.dateFormat())) {
                    strVal = DateUtil.format((Date) val, excel.dateFormat());
                } else if (val instanceof BigDecimal) {
                    DecimalFormat df = new DecimalFormat("0.##########");
                    strVal = df.format(val);
                } else if (val instanceof Long || val instanceof Integer) {
                    strVal = val.toString();
                } else {
                    strVal = Convert.toStr(val, "");
                }
            }

            // Long/Integer 直接写，其他加双引号包裹并处理转义
            if (val instanceof Long || val instanceof Integer) {
                values.add(strVal);
            } else {
                values.add('"' + strVal.replace("\"", "\"\"") + '"');
            }
        }
        return String.join(",", values);
    }

    public static String convertByExp(String propertyValue, String converterExp) {
        try {
            String[] convertSource = converterExp.split(",");
            for (String item : convertSource) {
                String[] itemArray = item.split("=");
                if (itemArray[0].equals(propertyValue)) {
                    return itemArray[1];
                }
            }
        } catch (Exception ignored) {}
        return propertyValue;
    }

    public static String getDownUrl() {
        return MDC.get("downUrl");
    }
}
