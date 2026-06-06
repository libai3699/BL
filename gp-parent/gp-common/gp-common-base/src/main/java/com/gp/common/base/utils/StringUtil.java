package com.gp.common.base.utils;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author axing
 * @version 1.0
 * @date 2021/8/27 16:36
 */
@Data
public class StringUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 校验Url
     *
     * @param set 无权限的字段集合
     * @param url  访问的url
     * @return
     */
    public static boolean checkUrl(Set<String> set, String url) {
        if (set.contains(url)) {
            return true;
        }
        return false;
    }
    public static boolean checkUrl(List<String> set, String url) {
        for (String service : set) {
            if (StringUtils.isNotEmpty(service) && url.indexOf(service)>=0) {
                return true;
            }
        }
        return false;
    }


    /**
     * 检查服务
     *
     * @param Services 服务列表
     * @param path     路径
     * @return boolean
     */
    public static boolean checkService(List<String> Services, String path) {
        // 校验放行的服务
        for (String service : Services) {
            if (path.indexOf(service)==0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 截取字符串后几位字符
     * @param str
     * @param num
     * @return
     */
    public static String substrSuffix(String str,int num){
        return str.substring(str.length()-num);
    }

    /**
     * 字段转驼峰
     * @param camelCase
     * @return
     */
    public static String camelToSnake(String camelCase) {
        // 检查输入是否为空
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        StringBuilder snakeCase = new StringBuilder();
        // 遍历每个字符
        for (char c : camelCase.toCharArray()) {
            // 如果字符是大写字母
            if (Character.isUpperCase(c)) {
                // 如果不是第一个字符，则在前面加下划线
                if (snakeCase.length() > 0) {
                    snakeCase.append('_');
                }
                // 将字符转为小写并添加到结果中
                snakeCase.append(Character.toLowerCase(c));
            } else {
                // 直接添加小写字符
                snakeCase.append(c);
            }
        }
        return snakeCase.toString();
    }
}
