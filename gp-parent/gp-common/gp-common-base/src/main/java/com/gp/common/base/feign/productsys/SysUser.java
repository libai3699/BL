package com.gp.common.base.feign.productsys;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gp.common.base.excel.annotation.Excel;
import com.gp.common.base.feign.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * 用户对象 sys_user
 *
 * @author ruoyi
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SysUser extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户账号
     */
    @Excel(name = "用户账号")
    private String userName;

    /**
     * 用户昵称
     */
    @Excel(name = "用户昵称")
    private String nickName;

    /**
     * 语言
     */
    @Excel(name = "语言")
    private String language;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户权限
     */
    @Excel(name = "角色")
    private String roleNames;

    /**
     * 产品名称
     */
    @Excel(name = "商户名称")
    private String product;

    /**
     * 产品编码
     */
    @Excel(name = "商户编码")
    private Integer code;
    /**
     * 手机号码
     */
    private String phonenumber;

    /**
     * 用户性别
     */
    private String sex;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 密码
     */
    private String password;

    /**
     * 谷歌登录码
     */
    @Excel(name = "谷歌登录码")
    private String googleAuthSecret;

    /**
     * 谷歌支付码
     */
    @Excel(name = "谷歌支付码")
    private String googleAmountSecret;

    /**
     * 验证码
     */
    private Integer googleCode;

    /**
     * 盐加密
     */
    private String salt;

    /**
     * 帐号状态（0正常 1停用）
     */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    private String delFlag;

    /**
     * 最后登陆IP
     */
    private String loginIp;

    /**
     * 最后登陆时间
     */
    private Date loginDate;

    /**
     * 部门对象
     */
    private SysDept dept;

    /**
     * 角色对象
     */
    private List<SysRole> roles;

    /**
     * 角色组
     */
    private Long[] roleIds;

    /**
     * 岗位组
     */
    private Long[] postIds;

    @ApiModelProperty("结束时间")
    @JsonIgnore
    private String endTime;

}
