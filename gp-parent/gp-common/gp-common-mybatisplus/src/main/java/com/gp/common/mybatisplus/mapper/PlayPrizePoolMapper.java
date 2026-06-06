package com.gp.common.mybatisplus.mapper;

import java.math.BigDecimal;
import java.util.List;
import com.gp.common.mybatisplus.entity.PlayPrizePool;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 游戏奖池Mapper接口
 *
 * @author axing
 * @date 2024-05-12
 */
public interface PlayPrizePoolMapper extends BaseMapper<PlayPrizePool>
{

    int addPrizePool(@Param("id") Integer id, @Param("award") BigDecimal award);

    /**
     * 查询游戏奖池
     *
     * @param id 游戏奖池ID
     * @return 游戏奖池
     */
    public PlayPrizePool selectPlayPrizePoolById(Integer id);

    /**
     * 查询游戏奖池列表
     *
     * @param playPrizePool 游戏奖池
     * @return 游戏奖池集合
     */
    public List<PlayPrizePool> selectPlayPrizePoolList(PlayPrizePool playPrizePool);

    /**
     * 新增游戏奖池
     *
     * @param playPrizePool 游戏奖池
     * @return 结果
     */
    public int insertPlayPrizePool(PlayPrizePool playPrizePool);

    /**
     * 修改游戏奖池
     *
     * @param playPrizePool 游戏奖池
     * @return 结果
     */
    public int updatePlayPrizePool(PlayPrizePool playPrizePool);

    /**
     * 删除游戏奖池
     *
     * @param id 游戏奖池ID
     * @return 结果
     */
    public int deletePlayPrizePoolById(Integer id);

    /**
     * 批量删除游戏奖池
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deletePlayPrizePoolByIds(Integer[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
