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
public class RoleFiledQueryParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String createBy;

    /**
     * 角色id
     */
    private Long roleId;
    /**
     * 字段集合
     */
    private List<RoleFiledParam> roleFiledParams;

}
