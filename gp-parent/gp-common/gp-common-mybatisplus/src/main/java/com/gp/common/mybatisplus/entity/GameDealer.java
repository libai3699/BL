package com.gp.common.mybatisplus.entity;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import com.gp.common.base.excel.annotation.Excel;
import com.gp.common.mybatisplus.base.BaseEntity;

/**
 * 游戏商对象 t_game_dealer
 *
 * @author axing
 * @date 2024-05-29
 */
@ApiModel("游戏商")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_game_dealer")
public class GameDealer extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:GameDealer";

    /** id */
    @ApiModelProperty("id")
    @TableId(value = "dealer_id", type = IdType.AUTO)
    @Excel(name = "id")
     private Long dealerId;

    /** 编号 */
    @ApiModelProperty("编号")
    @TableField("dealer_code")
    @Excel(name = "编号")
    private String dealerCode;

    /** 游戏代理商名称-中文 */
    @ApiModelProperty("游戏代理商名称")
    @TableField("dealer_name")
    @Excel(name = "游戏代理商名称")
    private String dealerName;

    /** 归属中文名 */
    @ApiModelProperty("归属")
    @TableField("category")
    @Excel(name = "归属")
    private String category;

    /** 简介 */
    @ApiModelProperty("简介")
    @TableField("dealer_info")
    @Excel(name = "简介")
    private String dealerInfo;

    /** 内容 */
    @ApiModelProperty("内容")
    @TableField("dealer_content")
    @Excel(name = "内容")
    private String dealerContent;

    /** 创建时间 */
    @ApiModelProperty("创建时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("create_time")
    @Excel(name = "创建时间")
    private Date createTime;
    @ApiModelProperty("创建时间数组")
    @TableField(exist = false)
    private String[] createTimes;

    /** 创建人 */
    @ApiModelProperty("创建人")
    @TableField("create_by")
    @Excel(name = "创建人")
    private String createBy;

    /** 修改时间 */
    @ApiModelProperty("修改时间")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("update_time")
    @Excel(name = "修改时间")
    private Date updateTime;
    @ApiModelProperty("修改时间数组")
    @TableField(exist = false)
    private String[] updateTimes;

    /** 修改人 */
    @ApiModelProperty("修改人")
    @TableField("update_by")
    @Excel(name = "修改人")
    private String updateBy;


}
