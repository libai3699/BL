package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.GameTypeCountOrder;
import com.gp.common.mybatisplus.param.QueryGameTypeCountOrderParam;

import java.util.List;

/**
 * 游戏类型注单相关统计Mapper接口
 *
 * @author axing
 * @date 2025-06-13
 */
public interface GameTypeCountOrderMapper extends BaseMapper<GameTypeCountOrder>
{


    List<GameTypeCountOrder> selectSumByMonth(QueryGameTypeCountOrderParam param);
    /**
     * 查询游戏类型注单相关统计
     *
     * @param id 游戏类型注单相关统计ID
     * @return 游戏类型注单相关统计
     */
    public GameTypeCountOrder selectGameTypeCountOrderById(Long id);

    /**
     * 查询游戏类型注单相关统计列表
     *
     * @param gameTypeCountOrder 游戏类型注单相关统计
     * @return 游戏类型注单相关统计集合
     */
    public List<GameTypeCountOrder> selectGameTypeCountOrderList(GameTypeCountOrder gameTypeCountOrder);

    /**
     * 新增游戏类型注单相关统计
     *
     * @param gameTypeCountOrder 游戏类型注单相关统计
     * @return 结果
     */
    public int insertGameTypeCountOrder(GameTypeCountOrder gameTypeCountOrder);

    /**
     * 修改游戏类型注单相关统计
     *
     * @param gameTypeCountOrder 游戏类型注单相关统计
     * @return 结果
     */
    public int updateGameTypeCountOrder(GameTypeCountOrder gameTypeCountOrder);

    /**
     * 删除游戏类型注单相关统计
     *
     * @param id 游戏类型注单相关统计ID
     * @return 结果
     */
    public int deleteGameTypeCountOrderById(Long id);

    /**
     * 批量删除游戏类型注单相关统计
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteGameTypeCountOrderByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();


    GameTypeCountOrder selectSumOneByMonthStr(QueryGameTypeCountOrderParam param);

    GameTypeCountOrder countSum(QueryGameTypeCountOrderParam param);

    List<GameTypeCountOrder> getByGameTypeSumList(QueryGameTypeCountOrderParam param);

    List<GameTypeCountOrder> getByTypeCodeMonthList(QueryGameTypeCountOrderParam param);
}
