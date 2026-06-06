package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.GameDealer;

import java.util.List;

/**
 * 游戏商Mapper接口
 *
 * @author axing
 * @date 2024-05-10
 */
public interface GameDealerMapper extends BaseMapper<GameDealer>
{
    /**
     * 查询游戏商
     *
     * @param dealerId 游戏商ID
     * @return 游戏商
     */
    public GameDealer selectGameDealerById(Long dealerId);

    /**
     * 查询游戏商列表
     *
     * @param gameDealer 游戏商
     * @return 游戏商集合
     */
    public List<GameDealer> selectGameDealerList(GameDealer gameDealer);

    /**
     * 查询游戏商数量
     *
     * @param gameDealer 游戏商
     * @return 游戏商数量
     */
    public Long selectGameDealerCount(GameDealer gameDealer);

    /**
     * 新增游戏商
     *
     * @param gameDealer 游戏商
     * @return 结果
     */
    public int insertGameDealer(GameDealer gameDealer);

    /**
     * 修改游戏商
     *
     * @param gameDealer 游戏商
     * @return 结果
     */
    public int updateGameDealer(GameDealer gameDealer);

    /**
     * 删除游戏商
     *
     * @param dealerId 游戏商ID
     * @return 结果
     */
    public int deleteGameDealerById(Long dealerId);

    /**
     * 批量删除游戏商
     *
     * @param dealerIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteGameDealerByIds(Long[] dealerIds);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}