package com.gp.common.base.feign;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity基类
 *
 * @author ruoyi
 */
@Data
@ApiModel("基础实体类")
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 搜索值
     */
    @ApiModelProperty("搜索值")
    private String searchValue;

    /**
     * 创建者
     */
    @ApiModelProperty("创建者")
    private String createBy;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新者
     */

    @ApiModelProperty("更新者")
    private String updateBy;

    /**
     * 更新时间
     */

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 备注
     */

    @ApiModelProperty("备注")
    private String remark;

    /**
     * 数据权限
     */

    @ApiModelProperty("数据权限")
    private String dataScope;

    /**
     * 开始时间
     */

    @ApiModelProperty("开始时间")
    @JsonIgnore
    private String beginTime;

    /**
     * 请求参数
     */

    @ApiModelProperty("请求参数")
    private Map<String, Object> params;

    public Map<String, Object> getParams() {
        if (params == null) {
            params = new HashMap<>();
        }
        return params;
    }

}
