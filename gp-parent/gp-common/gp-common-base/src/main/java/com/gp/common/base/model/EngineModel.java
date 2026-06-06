package com.gp.common.base.model;

import lombok.Data;

/**
 * 执行链路基本参数
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Data
public class EngineModel {
    /**
     * 接口类型
     */
    private String type;
    /**
     * 结果类型
     */
    private Class clazz;
    /**
     * API地址
     */
    private String api;
    /**
     * 描述信息
     */
    private String desc;
    /**
     * Bean名称
     */
    private String bean;
    /**
     * 扩展字段
     */
    private Object extend;

    /**
     * 请求方法
     */
    private String httpMethod;
}
