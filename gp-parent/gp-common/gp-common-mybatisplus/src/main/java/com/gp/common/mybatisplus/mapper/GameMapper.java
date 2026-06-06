package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.Game;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GameMapper extends BaseMapper<Game> {

    /**
     * 查询游戏
     *
     * @param id 游戏ID
     * @return 游戏
     */
    public Game selectGameById(Long id);

    /**
     * 查询游戏列表
     *
     * @param game 游戏
     * @return 游戏集合
     */
    public List<Game> selectGameList(Game game);

    /**
     * 查询游戏数量
     *
     * @param game 游戏
     * @return 游戏数量
     */
    public Long selectGameCount(Game game);

    /**
     * 新增游戏
     *
     * @param game 游戏
     * @return 结果
     */
    public int insertGame(Game game);

    /**
     * 修改游戏
     *
     * @param game 游戏
     * @return 结果
     */
    public int updateGame(Game game);

    /**
     * 删除游戏
     *
     * @param id 游戏ID
     * @return 结果
     */
    public int deleteGameById(Long id);

    /**
     * 批量删除游戏
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteGameByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    List<Game> queryGameByTag(@Param("typeCode") String typeCode, @Param("plateName") String plateName,@Param("address") String address);

    List<Game> queryHot(@Param("address") String address);

    List<Game> queryGameCondition(@Param("typeCode")String typeCode, @Param("plateName")String plateName, @Param("keyWord")String keyWord,@Param("rank") Integer rank
            ,@Param("arr") List<String> recentGameId,
    @Param("address") String address
    );


    List<Game> queryNewGameOrHot(@Param("typeCode")String typeCode, @Param("plateName")String plateName, @Param("keyWord")String keyWord, @Param("rank") Integer rank
            ,@Param("arr") List<String> recentGameId,
                                 @Param("address") String address, @Param("type") Integer type);


    List<Game> randomGame(@Param("limit") Integer i);


    List<String> queryNewOrHotGamePlate(@Param("type") Integer type);

}