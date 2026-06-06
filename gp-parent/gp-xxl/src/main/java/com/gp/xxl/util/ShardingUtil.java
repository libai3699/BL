package com.gp.xxl.util;

import com.xxl.job.core.context.XxlJobHelper;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * xxl-job 集合数据分片处理工具类
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
public class ShardingUtil {

    /**
     * 构建函数
     */
    private ShardingUtil() {
    }

    public static <T> List<T> handleData(List<T> list) {
        List<T> handleList = new ArrayList<T>();
        if (CollectionUtils.isEmpty(list)) {
            return handleList;
        }
        for (int i = 0; i < list.size(); i++) {
            if (isHandle(i)) {
                handleList.add(list.get(i));
            }
        }
        return handleList;
    }

    /**
     * 根据分片情况和数据判断是否处理
     *
     * @param order 当前数据顺序
     * @return 是否执行
     */
    private static boolean isHandle(int order) {
        int index = XxlJobHelper.getShardIndex();
        int total = XxlJobHelper.getShardTotal();
        if (total <= 1) {
            return true;
        }
        order += 1;
        if (order % total == index) {
            return true;
        } else {
            return false;
        }
    }
}
