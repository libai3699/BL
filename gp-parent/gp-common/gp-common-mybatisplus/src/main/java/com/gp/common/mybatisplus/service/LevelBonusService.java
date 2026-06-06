package com.gp.common.mybatisplus.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.LevelBonus;
import com.gp.common.mybatisplus.mapper.LevelBonusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 等级奖励Service业务层处理
 *
 * @author axing
 * @date 2025-07-10
 */
@Service
public class LevelBonusService extends ServiceImpl<LevelBonusMapper, LevelBonus> {
    @Autowired
    private LevelBonusMapper levelBonusMapper;

    /**
     * 查询等级奖励
     *
     * @param id 等级奖励ID
     * @return 等级奖励
     */

    public LevelBonus selectLevelBonusById(Long id) {
        return levelBonusMapper.selectLevelBonusById(id);
    }

    /**
     * 查询等级奖励列表
     *
     * @param param 等级奖励
     * @return 等级奖励
     */

    public List<LevelBonus> selectLevelBonusList(LevelBonus param) {
        return levelBonusMapper.selectLevelBonusList(param);
    }

    /**
     * 查询等级奖励数量
     *
     * @param param 等级奖励
     * @return 等级奖励数量
     */
    public long selectLevelBonusCount(LevelBonus param) {
        return levelBonusMapper.selectLevelBonusCount(param);
    }

    /**
     * 新增等级奖励
     *
     * @param param 等级奖励
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertLevelBonus(LevelBonus param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改等级奖励
     *
     * @param param 等级奖励
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateLevelBonus(LevelBonus param) {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除等级奖励
     *
     * @param ids 需要删除的等级奖励ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteLevelBonusByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除等级奖励信息
     *
     * @param id 等级奖励ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteLevelBonusById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    /**
     *
     * @param userId
     * @param type
     * @param level
     * @param one 0 查询 1 领取
     * @return
     */
    public List<LevelBonus> queryLevelBonus(Long userId, Integer type, Integer level,Integer one) {
        if (type == 0) {
            LambdaQueryWrapper<LevelBonus> q = new LambdaQueryWrapper<>();
            q.eq(LevelBonus::getUserId, userId).eq(LevelBonus::getType, type);
            if(one==0){
                q.le(LevelBonus::getLevel, level);
            }else {
                q.eq(LevelBonus::getLevel, level);
            }

            return levelBonusMapper.selectList(q);
        }
        //1 周奖励 2 月奖励
        Map<String, Date> timeRangeByType = null;
        if (type == 1) {
            timeRangeByType = DateUtils.getTimeRangeByType(3);
        }
        if (type == 2) {
            timeRangeByType = DateUtils.getTimeRangeByType(4);
        }
        if (type == 1 || type == 2) {
            Date startTime = timeRangeByType.get("startTime");
            Date endTime = timeRangeByType.get("endTime");
            LambdaQueryWrapper<LevelBonus> q = new LambdaQueryWrapper<>();
            q.eq(LevelBonus::getUserId, userId).eq(LevelBonus::getType, type)
                   // .eq(LevelBonus::getLevel, level)
                    .between(LevelBonus::getCreateTime, startTime, endTime);
            List<LevelBonus> levelBonuses = levelBonusMapper.selectList(q);
            return levelBonuses;
        }
        return CollUtil.newArrayList();
    }


    public Map<String, Object> leveBonusMap(String startTime, String endTime, Long userId) {
        return levelBonusMapper.leveBonusMap(startTime, endTime, userId);
    }

    public Map<String, Object> leveBonusMapBychannelId(Long channelId, String startTime, String endTime) {
        return levelBonusMapper.leveBonusMapBychannelId(channelId,startTime, endTime);
    }


}
