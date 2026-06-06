package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.GamePlate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GamePlateMapper extends BaseMapper<GamePlate> {

    /**
     * 查询游戏厂商
     *
     * @param id 游戏厂商ID
     * @return 游戏厂商
     */
    public GamePlate selectGamePlateById(Long id);

    /**
     * 查询游戏厂商列表
     *
     * @param gamePlate 游戏厂商
     * @return 游戏厂商集合
     */
    public List<GamePlate> selectGamePlateList(GamePlate gamePlate);

    /**
     * 查询游戏厂商数量
     *
     * @param gamePlate 游戏厂商
     * @return 游戏厂商数量
     */
    public Long selectGamePlateCount(GamePlate gamePlate);

    /**
     * 新增游戏厂商
     *
     * @param gamePlate 游戏厂商
     * @return 结果
     */
    public int insertGamePlate(GamePlate gamePlate);

    /**
     * 修改游戏厂商
     *
     * @param gamePlate 游戏厂商
     * @return 结果
     */
    public int updateGamePlate(GamePlate gamePlate);

    /**
     * 删除游戏厂商
     *
     * @param id 游戏厂商ID
     * @return 结果
     */
    public int deleteGamePlateById(Integer id);

    /**
     * 批量删除游戏厂商
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteGamePlateByIds(Integer[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    List<GamePlate> queryPlateByType(@Param("gameTypeCode") String gameTypeCode);
}