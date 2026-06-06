package com.gp.common.base.feign.productsys;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author axing
 * @version 1.0
 * @date 2022/6/16 15:54
 */
@Data
public class RoleFiledParam implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 菜单页面key
     */
    private String key;
    /**
     * 菜单名称
     */
    private String menuName;


    /**
     * 字段集合
     */
    private List<FiledParam> filedParams;

    /**
     * 字段字符串
     */
    private String filed;



}
