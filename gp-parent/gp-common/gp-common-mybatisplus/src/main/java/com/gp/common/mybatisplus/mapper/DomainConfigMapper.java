package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.DomainConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 域名配置Mapper接口
 *
 * @author axing
 * @date 2026-02-23
 */
public interface DomainConfigMapper extends BaseMapper<DomainConfig>
{
    /**
     * 查询域名配置
     *
     * @param id 域名配置ID
     * @return 域名配置
     */
    public DomainConfig selectDomainConfigById(Long id);

    /**
     * 查询域名配置列表
     *
     * @param domainConfig 域名配置
     * @return 域名配置集合
     */
    public List<DomainConfig> selectDomainConfigList(DomainConfig domainConfig);

    /**
     * 新增域名配置
     *
     * @param domainConfig 域名配置
     * @return 结果
     */
    public int insertDomainConfig(DomainConfig domainConfig);

    /**
     * 修改域名配置
     *
     * @param domainConfig 域名配置
     * @return 结果
     */
    public int updateDomainConfig(DomainConfig domainConfig);

    /**
     * 删除域名配置
     *
     * @param id 域名配置ID
     * @return 结果
     */
    public int deleteDomainConfigById(Long id);

    /**
     * 批量删除域名配置
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteDomainConfigByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
