package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.PlayWheelTerm;
import com.gp.common.mybatisplus.mapper.PlayWheelTermMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 转盘活动订单Service业务层处理
 *
 * @author axing
 * @date 2024-05-12
 */
@Service
public class PlayWheelTermService extends ServiceImpl<PlayWheelTermMapper, PlayWheelTerm>
{
    @Autowired
    private PlayWheelTermMapper playWheelTermMapper;

    /**
     * 查询转盘活动订单
     *
     * @param id 转盘活动订单ID
     * @return 转盘活动订单
     */

    public PlayWheelTerm selectPlayWheelTermById(Integer id)
    {
        return playWheelTermMapper.selectPlayWheelTermById(id);
    }

    /**
     * 查询转盘活动订单列表
     *
     * @param param 转盘活动订单
     * @return 转盘活动订单
     */

    public List<PlayWheelTerm> selectPlayWheelTermList(PlayWheelTerm param)
    {
        return playWheelTermMapper.selectPlayWheelTermList(param);
    }

    /**
     * 查询转盘活动订单数量
     *
     * @param param 转盘活动订单
     * @return 转盘活动订单数量
     */
    public Long selectPlayWheelTermCount(PlayWheelTerm param)
    {
        return playWheelTermMapper.selectPlayWheelTermCount(param);
    }

    /**
     * 新增转盘活动订单
     *
     * @param param 转盘活动订单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertPlayWheelTerm(PlayWheelTerm param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改转盘活动订单
     *
     * @param param 转盘活动订单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePlayWheelTerm(PlayWheelTerm param)
    {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除转盘活动订单
     *
     * @param ids 需要删除的转盘活动订单ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePlayWheelTermByIds(Integer[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除转盘活动订单信息
     *
     * @param id 转盘活动订单ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePlayWheelTermById(Integer id)
    {
        boolean result = this.removeById(id);
        return result;
    }

    public List<PlayWheelTerm> selectPlayWheelTermListByUser(Long channelId,  String startTime, String endTime) {
        return playWheelTermMapper.selectPlayWheelTermListByUser(channelId,startTime,endTime);
    }

    public Map<String, Object> playWheelTermCountMap(String startTime, String endTime, Long userId) {
        return playWheelTermMapper.playWheelTermCountMap(startTime,endTime,userId);
    }

    public Map<String, Object> selectPlayWheelTermMapByUser(Long channelId, String startTime, String endTime) {
        return playWheelTermMapper.selectPlayWheelTermMapByUser(channelId,startTime,endTime);
    }
}