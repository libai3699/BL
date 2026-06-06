package com.gp.common.mybatisplus.until;

import cn.hutool.core.collection.CollUtil;
import com.gp.common.mybatisplus.entity.ConfigAmount;
import com.gp.common.mybatisplus.service.ConfigAmountService;
import com.gp.common.mybatisplus.service.ConfigRiskService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;

/**
 * redis工具类
 *
 * @author axing
 * @date 2021/08/03
 */

@Data
@Slf4j
@Component
public class CheckYZMUtil {
    @Resource
    private ConfigRiskService configRiskService;

    public Boolean checkYZM(String code) {
        return configRiskService.noCheckYZM();
    }

    public Boolean checkUp(String code) {
        return configRiskService.checkUp();
    }

    public static final List<String> NoCheckYZMList = CollUtil.newArrayList("8","9","10");
    public static final List<String> NoCheckUpList = CollUtil.newArrayList("8","10");
}
