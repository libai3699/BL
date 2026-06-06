package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.RecommendCoin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 推荐购买网站Mapper接口
 *
 * @author axing
 * @date 2024-05-23
 */
public interface RecommendCoinMapper extends BaseMapper<RecommendCoin>
{
    /**
     * 查询推荐购买网站
     *
     * @param id 推荐购买网站ID
     * @return 推荐购买网站
     */
    public RecommendCoin selectRecommendCoinById(Long id);

    /**
     * 查询推荐购买网站列表
     *
     * @param recommendCoin 推荐购买网站
     * @return 推荐购买网站集合
     */
    public List<RecommendCoin> selectRecommendCoinList(RecommendCoin recommendCoin);

    /**
     * 新增推荐购买网站
     *
     * @param recommendCoin 推荐购买网站
     * @return 结果
     */
    public int insertRecommendCoin(RecommendCoin recommendCoin);

    /**
     * 修改推荐购买网站
     *
     * @param recommendCoin 推荐购买网站
     * @return 结果
     */
    public int updateRecommendCoin(RecommendCoin recommendCoin);

    /**
     * 删除推荐购买网站
     *
     * @param id 推荐购买网站ID
     * @return 结果
     */
    public int deleteRecommendCoinById(Long id);

    /**
     * 批量删除推荐购买网站
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteRecommendCoinByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
