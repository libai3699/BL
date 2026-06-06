package com.gp.common.mybatisplus.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.gp.common.base.excel.annotation.Excel;
import com.gp.common.mybatisplus.base.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 客服对象 t_community
 *
 * @author axing
 * @date 2024-03-13
 */
@ApiModel("客服")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_community")
public class Community extends BaseEntity {
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:Community";

    /**
     * id
     */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Integer id;

    /**
     * 地址
     */
    @ApiModelProperty("地址")
    @TableField("url")
    @Excel(name = "地址")
    private String url;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    @TableField("nickname")
    @Excel(name = "名称")
    private String nickname;

    /**
     * 类型(1 群组, 2 频道)
     */
    @ApiModelProperty("类型(1 群组, 2 频道)")
    @TableField("type")
    @Excel(name = "类型", readConverterExp = "1=群组,2=频道")
    private Integer type;

    /**
     * 语言表示
     */
    @ApiModelProperty("语言表示")
    @TableField("lan_key")
    @Excel(name = "语言")
    private String lanKey;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    @ApiModelProperty("创建时间数组")
    @TableField(exist = false)
    private String[] createTimes;

    /**
     * 文件地址
     */
    @ApiModelProperty("文件地址")
    @TableField(exist = false)
    private List<String> fileUrls;

    /**
     * 消息
     */
    @ApiModelProperty("消息")
    @TableField(exist = false)
    private String message;

}
