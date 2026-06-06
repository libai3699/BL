package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.GameType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GameTypeMapper extends BaseMapper<GameType> {

    /**
     * 查询游戏类型
     *
     * @param id 游戏类型ID
     * @return 游戏类型
     */
    public GameType selectGameTypeById(Long id);

    /**
     * 查询游戏类型列表
     *
     * @param gameType 游戏类型
     * @return 游戏类型集合
     */
    public List<GameType> selectGameTypeList(GameType gameType);

    /**
     * 查询游戏类型数量
     *
     * @param gameType 游戏类型
     * @return 游戏类型数量
     */
    public Long selectGameTypeCount(GameType gameType);

    /**
     * 新增游戏类型
     *
     * @param gameType 游戏类型
     * @return 结果
     */
    public int insertGameType(GameType gameType);

    /**
     * 修改游戏类型
     *
     * @param gameType 游戏类型
     * @return 结果
     */
    public int updateGameType(GameType gameType);

    /**
     * 删除游戏类型
     *
     * @param id 游戏类型ID
     * @return 结果
     */
    public int deleteGameTypeById(Integer id);

    /**
     * 批量删除游戏类型
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteGameTypeByIds(Integer[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();


}