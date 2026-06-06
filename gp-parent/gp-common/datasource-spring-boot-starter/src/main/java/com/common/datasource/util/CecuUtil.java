package com.common.datasource.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.common.datasource.param.CecuProp;
import com.gp.common.base.utils.LogUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 切库工具类
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Slf4j
@Data
public class CecuUtil {

    /**
     * 库名key
     */
    public static String DB_NAME_KEY = "db_name_key";

    /**
     * 库的编码
     */
    public static String DB_CODE = "db_code";


    private CecuProp cecuProp;

    /**
     * 请求头部token切换数据库
     */
    public static String cutDb(String tableName) {
        try {
            String dbName = getDbName();
            if (StringUtils.isNotBlank(dbName)) {
                log.info("切库SQL拦截 - 库名: {}", dbName);
                tableName = StringUtils.join(dbName, ".", tableName);
            }
        } catch (Exception e) {
            LogUtil.error(e, "切库异常");
            e.printStackTrace();
        }
        return tableName;
    }

    /**
     * 获取切库名
     * 优先 手动切库
     * 然后自动切库
     *
     * @return
     */
    public static String getDbName() {


        String dbName = CecuThreadLocal.getProperty(DB_NAME_KEY);

//        if (StringUtils.isBlank(dbName)) {
//            LogUtil.info("使用默认库", dbName);
//            dbName = DEFAULT_DBNAME;
//        }
        return dbName;
    }

    /**
     * 获取切库名
     * 优先 手动切库
     * 然后自动切库
     *
     * @return
     */
    public static String getDbCode() {
        return CecuThreadLocal.getProperty(DB_CODE);
    }

    /**
     * 获取切库名
     * 优先 手动切库
     * 然后自动切库
     *
     * @return
     */
    public static Integer getDbCodeInt() {
        String dbCode = CecuThreadLocal.getProperty(DB_CODE);
        if (StrUtil.isBlank(dbCode)) {
            return null;
        } else {
            return Integer.valueOf(dbCode);
        }
    }


    /**
     * 手动按数据库名切库
     *
     * @param dbName
     */
    public void cutDbByName(String dbName) {
        try {
            if (StringUtils.isNotBlank(dbName)) {
                Map<String, String> newEntries = MapUtil.newHashMap(2);
                newEntries.put(CecuUtil.DB_NAME_KEY, dbName);

                String[] split = dbName.split("_");
                String code = split[split.length - 1];
                if (NumberUtil.isNumber(code)) {
                    newEntries.put(CecuUtil.DB_CODE, code);
                }
                CecuThreadLocal.addProperty(newEntries);
            }
        } catch (Exception e) {
            LogUtil.error(e, "数据库切库异常");
            e.printStackTrace();
        }
    }

    /**
     * 手动按数据库CODE码 切库
     *
     * @param code
     */
    public void cutDbByCode(String code) {
        if (StrUtil.isBlank(code)) {
            return;
        }
        try {
            String dbName = getDbNameFromList(code);
            if (StringUtils.isNotBlank(dbName)) {
                Map<String, String> newEntries = MapUtil.newHashMap(2);
                newEntries.put(CecuUtil.DB_NAME_KEY, dbName);
                newEntries.put(CecuUtil.DB_CODE, code);
                CecuThreadLocal.addProperty(newEntries);
            }
        } catch (Exception e) {
            LogUtil.error(e, "数据库切库异常");
            e.printStackTrace();
        }
    }


    /**
     * 获取数据名
     *
     * @param code
     * @return
     */
    public String getDbNameFromList(String code) {
        if (StrUtil.isBlank(code)) {
            return "";
        }
        if (CollectionUtils.isEmpty(cecuProp.getDbList())) {
            return "";
        }

        for (String dbNameStr : cecuProp.getDbList()) {
            if (StringUtils.isBlank(dbNameStr)) {
                continue;
            }
            String[] split = dbNameStr.split("\\|");
            if (code.equals(split[0])) {
                return split[1];
            }
        }
        return "";
    }

    /**
     * 手动清理切库信息
     */
    public static void cleanDbInfo() {
        CecuThreadLocal.clear();
    }

    /**
     * 手动清理切库信息
     */
    public static void debug() {
        CecuThreadLocal.debug();
    }

    /**
     * 获取产品id列表
     *
     * @return
     */
    public List<String> getAppList() {
        List<String> list = new ArrayList<>();
        if (CollectionUtils.isEmpty(cecuProp.getAppList())) {
            return list;
        }
       list=cecuProp.getAppList();
        return list;
    }

    /**
     * 按数据库CODE码获取产品名称
     *
     * @param code
     */
    public String getAppNameByCode(String code) {
        if (StrUtil.isBlank(code)) {
            return "";
        }
        try {
            List<String> appNameList = cecuProp.getAppNameList();
            for (String appName : appNameList) {
                String[] split = appName.split("\\|");
                if (code.equals(split[0])) {
                    return split[1];
                }
            }

        } catch (Exception e) {
            LogUtil.error(e, "获取商户名异常");
            e.printStackTrace();
        }
        return "";
    }

}
