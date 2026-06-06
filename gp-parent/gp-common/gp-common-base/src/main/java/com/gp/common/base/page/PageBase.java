package com.gp.common.base.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author axing
 * @version 1.0
 * @date 2023/10/16/016 16:18
 */
@Accessors(chain = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 当前记录起始索引 */
    private Integer pg = 1;
    /** 每页显示记录数 */
    private Integer sz = 12;

}
