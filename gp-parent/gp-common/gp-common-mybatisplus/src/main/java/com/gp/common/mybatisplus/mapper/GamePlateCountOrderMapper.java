package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.GamePlateCountOrder;
import com.gp.common.mybatisplus.param.QueryGamePlateCountOrderParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 游戏厂商注单相关统计Mapper接口
 *
 * @author axing
 * @date 2024-06-13
 */
@Mapper
public interface GamePlateCountOrderMapper extends BaseMapper<GamePlateCountOrder>
{
    /**
     * 查询游戏厂商注单相关统计
     *
     * @param id 游戏厂商注单相关统计ID
     * @return 游戏厂商注单相关统计
     */
    public GamePlateCountOrder selectGamePlateCountOrderById(Long id);

    /**
     * 查询游戏厂商注单相关统计列表
     *
     * @param gamePlateCountOrder 游戏厂商注单相关统计
     * @return 游戏厂商注单相关统计集合
     */
    public List<GamePlateCountOrder> selectGamePlateCountOrderList(GamePlateCountOrder gamePlateCountOrder);

    /**
     * 新增游戏厂商注单相关统计
     *
     * @param gamePlateCountOrder 游戏厂商注单相关统计
     * @return 结果
     */
    public int insertGamePlateCountOrder(GamePlateCountOrder gamePlateCountOrder);

    /**
     * 修改游戏厂商注单相关统计
     *
     * @param gamePlateCountOrder 游戏厂商注单相关统计
     * @return 结果
     */
    public int updateGamePlateCountOrder(GamePlateCountOrder gamePlateCountOrder);

    /**
     * 删除游戏厂商注单相关统计
     *
     * @param id 游戏厂商注单相关统计ID
     * @return 结果
     */
    public int deleteGamePlateCountOrderById(Long id);

    /**
     * 批量删除游戏厂商注单相关统计
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteGamePlateCountOrderByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    GamePlateCountOrder countSum(QueryGamePlateCountOrderParam param);


    List<GamePlateCountOrder> selectSumByMonth(QueryGamePlateCountOrderParam param);


    GamePlateCountOrder selectSumOneByMonthStr(QueryGamePlateCountOrderParam param);

    List<GamePlateCountOrder> getByPlateCodeSumList(QueryGamePlateCountOrderParam param);

    List<GamePlateCountOrder> getByPlateCodeMonthList(QueryGamePlateCountOrderParam param);
}
