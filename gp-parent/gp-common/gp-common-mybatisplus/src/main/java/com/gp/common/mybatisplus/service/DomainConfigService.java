package com.gp.common.mybatisplus.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.util.RedisUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.DomainConfig;
import com.gp.common.mybatisplus.mapper.DomainConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 域名配置Service业务层处理
 *
 * @author axing
 * @date 2026-02-23
 */
@Service
public class DomainConfigService extends ServiceImpl<DomainConfigMapper, DomainConfig> {
    @Autowired
    private DomainConfigMapper domainConfigMapper;

    @Resource
    private RedisUtil redisUtil;
    // 缓存过期时间配置（单位：秒）
    private static final int CACHE_EXPIRE_TIME = 60 * 60;


    /**
     * 查询域名配置
     *
     * @param id 域名配置ID
     * @return 域名配置
     */

    public DomainConfig selectDomainConfigById(Long id) {
        return domainConfigMapper.selectDomainConfigById(id);
    }

    /**
     * 查询域名配置列表
     *
     * @param param 域名配置
     * @return 域名配置
     */

    public List<DomainConfig> selectDomainConfigList(DomainConfig param) {
        return domainConfigMapper.selectDomainConfigList(param);
    }

    /**
     * 新增域名配置
     *
     * @param param 域名配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertDomainConfig(DomainConfig param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改域名配置
     *
     * @param param 域名配置
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDomainConfig(DomainConfig param) {
        //更新缓存
        String redisKey = RedisKey.imageDomainKey;
        if (param.getDomainType() == 2) {
            redisKey = RedisKey.webDomainKey;
        }
        String key = StrUtil.format(redisKey, CecuUtil.getDbCode(), param.getRegionCode());
        redisUtil.set(key, param.getDomainName(), CACHE_EXPIRE_TIME);
        param.setUpdateTime(DateUtils.getNowDate());
        return this.updateById(param);
    }

    /**
     * 批量删除域名配置
     *
     * @param ids 需要删除的域名配置ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDomainConfigByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除域名配置信息
     *
     * @param id 域名配置ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteDomainConfigById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }
}
