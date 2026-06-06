package com.gp.common.mybatisplus.mapper;

import java.util.List;

import com.gp.common.mybatisplus.entity.Game;
import com.gp.common.mybatisplus.entity.GameLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 游戏收藏Mapper接口
 *
 * @author axing
 * @date 2024-05-09
 */
public interface GameLikeMapper extends BaseMapper<GameLike>
{
    /**
     * 查询游戏收藏
     *
     * @param id 游戏收藏ID
     * @return 游戏收藏
     */
    public GameLike selectGameLikeById(Long id);

    /**
     * 查询游戏收藏列表
     *
     * @param gameLike 游戏收藏
     * @return 游戏收藏集合
     */
    public List<GameLike> selectGameLikeList(GameLike gameLike);

    /**
     * 新增游戏收藏
     *
     * @param gameLike 游戏收藏
     * @return 结果
     */
    public int insertGameLike(GameLike gameLike);

    /**
     * 修改游戏收藏
     *
     * @param gameLike 游戏收藏
     * @return 结果
     */
    public int updateGameLike(GameLike gameLike);

    /**
     * 删除游戏收藏
     *
     * @param id 游戏收藏ID
     * @return 结果
     */
    public int deleteGameLikeById(Long id);

    /**
     * 批量删除游戏收藏
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteGameLikeByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();
    /**
     * 查询个人收藏的游戏
     *
     * @return 结果
     */
    List<Game> queryMyLike(@Param("userId")Long userId,@Param("plateName")String plateName,@Param("keyWord")String keyWord,@Param("rank")Integer rank,@Param("address")String address);

    List<GameLike> queryLove(@Param("userId") Long userId ,@Param("array")List<Long> collect);

}
