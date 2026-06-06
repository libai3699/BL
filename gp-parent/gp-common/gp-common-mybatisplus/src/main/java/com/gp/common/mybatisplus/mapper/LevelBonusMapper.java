package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.LevelBonus;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 等级奖励Mapper接口
 *
 * @author axing
 * @date 2025-07-10
 */
public interface LevelBonusMapper extends BaseMapper<LevelBonus>
{
    /**
     * 查询等级奖励
     *
     * @param id 等级奖励ID
     * @return 等级奖励
     */
    public LevelBonus selectLevelBonusById(Long id);

    /**
     * 查询等级奖励列表
     *
     * @param levelBonus 等级奖励
     * @return 等级奖励集合
     */
    public List<LevelBonus> selectLevelBonusList(LevelBonus levelBonus);

    /**
     * 查询等级奖励数量
     *
     * @param levelBonus 等级奖励
     * @return 等级奖励数量
     */
    public long selectLevelBonusCount(LevelBonus levelBonus);

    /**
     * 新增等级奖励
     *
     * @param levelBonus 等级奖励
     * @return 结果
     */
    public int insertLevelBonus(LevelBonus levelBonus);

    /**
     * 修改等级奖励
     *
     * @param levelBonus 等级奖励
     * @return 结果
     */
    public int updateLevelBonus(LevelBonus levelBonus);

    /**
     * 删除等级奖励
     *
     * @param id 等级奖励ID
     * @return 结果
     */
    public int deleteLevelBonusById(Long id);

    /**
     * 批量删除等级奖励
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteLevelBonusByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    Map<String, Object> leveBonusMap(@Param("startTime")String startTime, @Param("endTime")String endTime, @Param("userId") Long userId);

    Map<String, Object> leveBonusMapBychannelId(@Param("channelId")Long channelId, @Param("startTime")String startTime, @Param("endTime")String endTime);

}
