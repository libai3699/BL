package com.gp.common.mybatisplus.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@ApiModel(description = "彩票代理比例信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelLotteryConfigVo {


    /**
     * 返佣
     */
    private BigDecimal superUserRebate;



}
