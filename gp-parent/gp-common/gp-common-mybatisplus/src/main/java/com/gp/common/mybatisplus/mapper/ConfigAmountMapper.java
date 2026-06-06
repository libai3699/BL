package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.ConfigAmount;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ConfigAmountMapper extends BaseMapper<ConfigAmount> {
    /**
     * 查询金额配置
     *
     * @param configId 金额配置ID
     * @return 金额配置
     */
    public ConfigAmount selectConfigAmountById(Integer configId);

    /**
     * 查询金额配置列表
     *
     * @param tConfigAmount 金额配置
     * @return 金额配置集合
     */
    public List<ConfigAmount> selectConfigAmountList(ConfigAmount tConfigAmount);

    /**
     * 新增金额配置
     *
     * @param tConfigAmount 金额配置
     * @return 结果
     */
    public int insertConfigAmount(ConfigAmount tConfigAmount);

    /**
     * 修改金额配置
     *
     * @param tConfigAmount 金额配置
     * @return 结果
     */
    public int updateConfigAmount(ConfigAmount tConfigAmount);

    /**
     * 删除金额配置
     *
     * @param configId 金额配置ID
     * @return 结果
     */
    public int deleteConfigAmountById(Integer configId);

    /**
     * 批量删除金额配置
     *
     * @param configIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteConfigAmountByIds(Integer[] configIds);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    boolean updateIsDispById(ConfigAmount tConfigAmount);
}