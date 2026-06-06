package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.ConfigRisk;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ConfigRiskMapper extends BaseMapper<ConfigRisk> {
    /**
     * 查询配置
     *
     * @param configId 配置ID
     * @return 配置
     */
    public ConfigRisk selectConfigRiskById(Integer configId);

    /**
     * 查询配置列表
     *
     * @param tConfigRisk 配置
     * @return 配置集合
     */
    public List<ConfigRisk> selectConfigRiskList(ConfigRisk tConfigRisk);

    /**
     * 新增配置
     *
     * @param tConfigRisk 配置
     * @return 结果
     */
    public int insertConfigRisk(ConfigRisk tConfigRisk);

    /**
     * 修改配置
     *
     * @param tConfigRisk 配置
     * @return 结果
     */
    public int updateConfigRisk(ConfigRisk tConfigRisk);

    /**
     * 删除配置
     *
     * @param configId 配置ID
     * @return 结果
     */
    public int deleteConfigRiskById(Integer configId);

    /**
     * 批量删除配置
     *
     * @param configIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteConfigRiskByIds(Integer[] configIds);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();


    List<ConfigRisk> getConfigUrl(@Param("arr") List<String> arr);

    boolean updateIsDispById(ConfigRisk tConfigRisk);
}
