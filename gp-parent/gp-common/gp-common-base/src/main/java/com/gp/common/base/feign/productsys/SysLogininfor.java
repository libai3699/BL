package com.gp.common.base.feign.productsys;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gp.common.base.excel.annotation.Excel;
import com.gp.common.base.feign.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;

/**
 * 系统访问记录表 sys_logininfor
 *
 * @author ruoyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SysLogininfor extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Excel(name = "访问编号", cellType = Excel.ColumnType.NUMERIC)
    private Long infoId;

    /**
     * 产品名称
     */
    @Excel(name = "商品名称")
    private String product;

    /**
     * 产品编码
     */
    @Excel(name = "商户编码", cellType = Excel.ColumnType.NUMERIC)
    private Integer code;

    /**
     * 用户账号
     */
    @Excel(name = "用户名称")
    private String userName;

    /**
     * 登录IP地址
     */
    @Excel(name = "登录地址")
    private String ipaddr;

    /**
     * 登录地点
     */
    @Excel(name = "登录地点")
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @Excel(name = "浏览器")
    private String browser;

    /**
     * 操作系统
     */
    @Excel(name = "操作系统")
    private String os;

    /**
     * 登录状态 0成功 1失败
     */
    @Excel(name = "状态", readConverterExp = "0=成功,1=失败")
    private String status;

    /**
     * 提示消息
     */
    @Excel(name = "操作消息")
    private String msg;

    /**
     * 访问时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "登录日期", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date loginTime;
    @ApiModelProperty("结束时间")
    @JsonIgnore
    private String endTime;

}
