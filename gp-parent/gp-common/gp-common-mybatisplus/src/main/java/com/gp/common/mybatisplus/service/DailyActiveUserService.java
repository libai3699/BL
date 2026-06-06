package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.DailyActiveUser;
import com.gp.common.mybatisplus.entity.DifferentialConfig;
import com.gp.common.mybatisplus.mapper.DailyActiveUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 每日活跃用户Service业务层处理
 *
 * @author axing
 * @date 2025-04-29
 */
@Slf4j
@Service
public class DailyActiveUserService extends ServiceImpl<DailyActiveUserMapper, DailyActiveUser> {
    @Resource
    private DailyActiveUserMapper dailyActiveUserMapper;
    @Resource
    private DifferentialConfigService differentialConfigService;

    /**
     * 查询每日活跃用户
     *
     * @param id 每日活跃用户ID
     * @return 每日活跃用户
     */

    public DailyActiveUser selectDailyActiveUserById(Long id) {
        return dailyActiveUserMapper.selectDailyActiveUserById(id);
    }

    /**
     * 查询每日活跃用户列表
     *
     * @param param 每日活跃用户
     * @return 每日活跃用户
     */

    public List<DailyActiveUser> selectDailyActiveUserList(DailyActiveUser param) {
        return dailyActiveUserMapper.selectDailyActiveUserList(param);
    }





    /**
     * 新增/更新 每日活跃用户打码量
     *
     * @param params 每日活跃用户
     */
    public void insertDailyActiveUser(DailyActiveUser param) {
        this.baseMapper.insertOrUpdateData(param);
//        log.info("新增/更新 每日活跃用户打码量:{}", param);
//        //判读是否已存在
//        LambdaQueryWrapper<DailyActiveUser> queryWrapper = Wrappers.lambdaQuery(DailyActiveUser.class);
//        queryWrapper.eq(DailyActiveUser::getActiveDate, DateUtils.formatDate(param.getActiveDate())).eq(DailyActiveUser::getUserId, param.getUserId());
//        if (null != param.getParentUserId()) {
//                queryWrapper.eq(DailyActiveUser::getParentUserId, param.getParentUserId());
//            }
//            DailyActiveUser dailyActiveUser = this.getOne(queryWrapper);
//            if (null == dailyActiveUser) {
//                param.setCreateTime(DateUtils.getNowDate());
//                this.save(param);
//            } else {
//                LambdaUpdateWrapper<DailyActiveUser> updateWrapper = Wrappers.lambdaUpdate(DailyActiveUser.class);
//                updateWrapper.eq(DailyActiveUser::getId, dailyActiveUser.getId())
//                        .set(DailyActiveUser::getUpdateTime, DateUtils.getNowDate())
//                        .setSql("turnover_amount = turnover_amount  +" + param.getTurnoverAmount());
//                this.update(new DailyActiveUser(), updateWrapper);
//            }
    }

    /**
     * 修改每日活跃用户
     *
     * @param param 每日活跃用户
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDailyActiveUser(DailyActiveUser param) {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除每日活跃用户
     *
     * @param ids 需要删除的每日活跃用户ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDailyActiveUserByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除每日活跃用户信息
     *
     * @param id 每日活跃用户ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDailyActiveUserById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    /**
     * 查询某天的可有效人数
     *
     * @param userId
     * @param activeDate
     * @return
     */
    public Integer queryAvailableNumByTime(Long userId, Date activeDate) {
        LambdaQueryWrapper<DailyActiveUser> q = new LambdaQueryWrapper<>();
        DifferentialConfig differentialConfig = differentialConfigService.getOne(
                Wrappers.lambdaQuery(DifferentialConfig.class).orderByDesc(DifferentialConfig::getUpdateTime).last("limit 1"));
        BigDecimal turnoverThreshold = new BigDecimal("300");
        if (differentialConfig != null) {
            turnoverThreshold = differentialConfig.getTurnoverThreshold();
        }
        q.ge(DailyActiveUser::getTurnoverAmount, turnoverThreshold);
        q.eq(DailyActiveUser::getActiveDate, DateUtils.formatDate(activeDate));
        q.last("and ( find_in_set(" + userId + ",p_path) or user_id = " + userId + " )");
        return this.count(q);
    }

    /**
     * 获取指定日期所有用户的打码量
     */
    public Map<Long, BigDecimal> queryAllUserTurnover(Date activeDate, List<Long> userIds) {
        LambdaQueryWrapper<DailyActiveUser> q = new LambdaQueryWrapper<>();
        q.eq(DailyActiveUser::getActiveDate, DateUtils.formatDate(activeDate)).in(DailyActiveUser::getUserId, userIds);
        List<DailyActiveUser> list = this.list(q);
        Map<Long, BigDecimal> result = new HashMap<>(list.size());
        if (CollectionUtils.isNotEmpty(list)) {
            for (DailyActiveUser dailyActiveUser : list) {
                result.put(dailyActiveUser.getUserId(), dailyActiveUser.getTurnoverAmount());
            }
        }
        return result;
    }

}
