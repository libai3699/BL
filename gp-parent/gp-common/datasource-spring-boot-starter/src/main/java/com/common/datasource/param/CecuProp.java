package com.common.datasource.param;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.util.List;

/**
 * 服务参数
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 10:54
 */
@RefreshScope
@Data
@ConfigurationProperties(prefix = "dynamic")
public class CecuProp {

    /**
     * 切库开关
     */
    private boolean cecuSwitch = false;

    /**
     * 切库字段(请求头)
     */
    private String cecuField = "cecu";

    /**
     * 切库列表
     */
    private List<String> dbList = CollUtil.newArrayList();

    /**
     * 切库Id列表
     */
    private List<String> appList = CollUtil.newArrayList();

    /**
     * 默认库字段
     */
    private String defaultDBName = "";

    /**
     * 产品名称列表
     */
    private List<String> appNameList = CollUtil.newArrayList();


}
