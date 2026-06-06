package com.gp.common.mybatisplus.service;

import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.mapper.UserExtChangeMapper;
import com.gp.common.mybatisplus.mapper.UserExtMapper;
import com.gp.common.mybatisplus.vo.HomeAlreadyRebateAmountExportVO;
import com.gp.common.mybatisplus.vo.HomeAlreadyReturnCommissionAmountExportVO;
import com.gp.common.mybatisplus.vo.HomeWaitRebateAmountExportVO;
import com.gp.common.mybatisplus.vo.HomeWaitReturnCommissionAmountExportVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 首页实时详情：待返水/待返佣、已返水/已返佣分条 SQL，列表配合 PageHelper 排序分页；导出走全量无分页。
 */
@Service
public class HomeRealtimeStatService {

    @Resource
    private UserExtMapper userExtMapper;
    @Resource
    private UserExtChangeMapper userExtChangeMapper;

    /** 待返水（type=4），由调用方在查询前执行 {@code PageHelper.startPage(..., orderBy)} */
    public List<HomeWaitRebateAmountExportVO> queryTodayWaitRebateAmountDetail() {
        return userExtMapper.queryWaitRebateAmountDetailByUser();
    }

    /** 待返佣（type=5），由调用方在查询前执行 {@code PageHelper.startPage(..., orderBy)} */
    public List<HomeWaitReturnCommissionAmountExportVO> queryTodayWaitReturnCommissionAmountDetail() {
        return userExtMapper.queryWaitReturnCommissionAmountDetailByUser();
    }

    /** 已返水：同 {@link UserExtChangeMapper#userExtChangeCountMap} 中已返水条件，按自然日，由调用方在查询前执行 {@code PageHelper.startPage} */
    public List<HomeAlreadyRebateAmountExportVO> queryTodayAlreadyRebateAmountDetail() {
        String day = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, new Date());
        String startTime = day + " 00:00:00";
        String endTime = day + " 23:59:59";
        return userExtChangeMapper.queryAlreadyRebateAmountDetailByUser(startTime, endTime);
    }

    /** 已返佣：同 userExtChangeCountMap 中已返佣条件，按自然日 */
    public List<HomeAlreadyReturnCommissionAmountExportVO> queryTodayAlreadyReturnCommissionAmountDetail() {
        String day = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, new Date());
        String startTime = day + " 00:00:00";
        String endTime = day + " 23:59:59";
        return userExtChangeMapper.queryAlreadyReturnCommissionAmountDetailByUser(startTime, endTime);
    }
}
