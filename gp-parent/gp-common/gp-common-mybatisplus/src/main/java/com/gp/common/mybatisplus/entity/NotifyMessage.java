package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.common.mybatisplus.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@ApiModel(description = "通知消息表")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "t_notify_message")
public class NotifyMessage extends BaseEntity {
    /** id */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 语言 */
    @ApiModelProperty("语言")
    @TableField("lan_key")
    private String lanKey;

    /** 标题 */
    @ApiModelProperty("标题")
    @TableField("title")
    private String title;

    /** 文件列表 */
    @ApiModelProperty("文件列表")
    @TableField(exist = false)
    private List<String> files;

    /** 内容 */
    @ApiModelProperty("内容")
    @TableField("content")
    private String content;

    /** 备注 */
    @ApiModelProperty("备注")
    @TableField("remark")
    private String remark;

    /** 创建时间 */
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    private Date createTime;
    @ApiModelProperty("创建时间数组")
    @TableField(exist = false)
    private String[] createTimes;

    /** 创建人 */
    @ApiModelProperty("创建人")
    @TableField("create_by")
    private String createBy;


}
