package com.gp.common.mybatisplus.service;

import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.PlayPrizePoolMapper;
import com.gp.common.mybatisplus.entity.PlayPrizePool;
import com.gp.common.mybatisplus.service.PlayPrizePoolService;

/**
 * 游戏奖池Service业务层处理
 *
 * @author axing
 * @date 2024-05-12
 */
@Service
public class PlayPrizePoolService extends ServiceImpl<PlayPrizePoolMapper, PlayPrizePool>
{
    @Autowired
    private PlayPrizePoolMapper playPrizePoolMapper;

    public static final String wheelPrizePool = "wheel_prize_pool";

    public PlayPrizePool getWheelPrizePool(){
        return getWheelPrizePool(wheelPrizePool);
    }

    public PlayPrizePool getWheelPrizePool(String code){
        return this.getOne(Wrappers.lambdaQuery(PlayPrizePool.class).eq(PlayPrizePool::getPrizePoolCode, code));
    }

    public int addPrizePool(Integer id, BigDecimal award) {
        return this.baseMapper.addPrizePool(id, award);
    }

    /**
     * 查询游戏奖池
     *
     * @param id 游戏奖池ID
     * @return 游戏奖池
     */

    public PlayPrizePool selectPlayPrizePoolById(Integer id)
    {
        return playPrizePoolMapper.selectPlayPrizePoolById(id);
    }

    /**
     * 查询游戏奖池列表
     *
     * @param param 游戏奖池
     * @return 游戏奖池
     */

    public List<PlayPrizePool> selectPlayPrizePoolList(PlayPrizePool param)
    {
        return playPrizePoolMapper.selectPlayPrizePoolList(param);
    }

    /**
     * 新增游戏奖池
     *
     * @param param 游戏奖池
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertPlayPrizePool(PlayPrizePool param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改游戏奖池
     *
     * @param param 游戏奖池
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePlayPrizePool(PlayPrizePool param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除游戏奖池
     *
     * @param ids 需要删除的游戏奖池ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePlayPrizePoolByIds(Integer[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除游戏奖池信息
     *
     * @param id 游戏奖池ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePlayPrizePoolById(Integer id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
