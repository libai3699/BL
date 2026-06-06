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
 * 游戏收藏对象 t_game_like
 *
 * @author axing
 * @date 2024-05-09
 */
@ApiModel("游戏收藏")
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_game_like")
public class GameLike extends BaseEntity
{
    private static final long serialVersionUID = 1L;
    public static final String REDIS_KEY = "msg:GameLike";

    /** id */
    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
     private Long id;

    /** 用户id */
    @ApiModelProperty("用户id")
    @TableField("user_id")
    @Excel(name = "用户id")
    private Long userId;

    /** 游戏id */
    @ApiModelProperty("游戏id")
    @TableField("game_id")
    @Excel(name = "游戏id")
    private Long gameId;

    /** 删除标志(0删除  1存在) */
    @ApiModelProperty("删除标志(0删除  1存在)")
    @TableField("has_del")
    @Excel(name = "删除标志(0删除  1存在)")
    private Integer hasDel;

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


}
