package com.gp.common.mybatisplus.service;

import java.util.List;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.RecommendCoinMapper;
import com.gp.common.mybatisplus.entity.RecommendCoin;
import com.gp.common.mybatisplus.service.RecommendCoinService;

/**
 * 推荐购买网站Service业务层处理
 *
 * @author axing
 * @date 2024-05-23
 */
@Service
public class RecommendCoinService extends ServiceImpl<RecommendCoinMapper, RecommendCoin>
{
    @Autowired
    private RecommendCoinMapper recommendCoinMapper;

    /**
     * 查询推荐购买网站
     *
     * @param id 推荐购买网站ID
     * @return 推荐购买网站
     */

    public RecommendCoin selectRecommendCoinById(Long id)
    {
        return recommendCoinMapper.selectRecommendCoinById(id);
    }

    /**
     * 查询推荐购买网站列表
     *
     * @param param 推荐购买网站
     * @return 推荐购买网站
     */

    public List<RecommendCoin> selectRecommendCoinList(RecommendCoin param)
    {
        return recommendCoinMapper.selectRecommendCoinList(param);
    }

    /**
     * 新增推荐购买网站
     *
     * @param param 推荐购买网站
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertRecommendCoin(RecommendCoin param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改推荐购买网站
     *
     * @param param 推荐购买网站
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateRecommendCoin(RecommendCoin param)
    {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除推荐购买网站
     *
     * @param ids 需要删除的推荐购买网站ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteRecommendCoinByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除推荐购买网站信息
     *
     * @param id 推荐购买网站ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteRecommendCoinById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
