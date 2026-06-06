package com.gp.common.mybatisplus.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.util.RedisUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.PayType;
import com.gp.common.mybatisplus.mapper.PayTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 支付类型Service业务层处理
 *
 * @author axing
 * @date 2025-06-12
 */
@Service
public class PayTypeService extends ServiceImpl<PayTypeMapper, PayType>
{
    @Autowired
    private PayTypeMapper payTypeMapper;

    @Resource
    private RedisUtil redisUtil;

    /**
     * 查询支付类型
     *
     * @param id 支付类型ID
     * @return 支付类型
     */

    public PayType selectPayTypeById(Integer id)
    {
        return payTypeMapper.selectPayTypeById(id);
    }

    /**
     * 查询支付类型列表
     *
     * @param param 支付类型
     * @return 支付类型
     */

    public List<PayType> selectPayTypeList(PayType param)
    {
        return payTypeMapper.selectPayTypeList(param);
    }

    /**
     * 新增支付类型
     *
     * @param param 支付类型
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertPayType(PayType param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        invalidateMapCache(CecuUtil.getDbCode());
        return result;
    }

    /**
     * 修改支付类型
     *
     * @param param 支付类型
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePayType(PayType param)
    {
        PayType old = this.getById(param.getId());
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        String dbCode = CecuUtil.getDbCode();
        redisUtil.del(StrUtil.format(RedisKey.payTypeCacheById, dbCode, param.getId()));
        if (old != null && StrUtil.isNotBlank(old.getCode())) {
            redisUtil.del(StrUtil.format(RedisKey.payTypeCacheByCode, dbCode, old.getCode()));
        }
        invalidateMapCache(dbCode);
        return result;
    }

    /**
     * 批量删除支付类型
     *
     * @param ids 需要删除的支付类型ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePayTypeByIds(Integer[] ids)
    {
        List<PayType> oldList = this.listByIds(Arrays.asList(ids));
        boolean result = this.removeByIds(Arrays.asList(ids));
        String dbCode = CecuUtil.getDbCode();
        oldList.forEach(item -> {
            redisUtil.del(StrUtil.format(RedisKey.payTypeCacheById, dbCode, item.getId()));
            if (StrUtil.isNotBlank(item.getCode())) {
                redisUtil.del(StrUtil.format(RedisKey.payTypeCacheByCode, dbCode, item.getCode()));
            }
        });
        invalidateMapCache(dbCode);
        return result;
    }

    /**
     * 删除支付类型信息
     *
     * @param id 支付类型ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePayTypeById(Integer id)
    {
        PayType old = this.getById(id);
        boolean result = this.removeById(id);
        String dbCode = CecuUtil.getDbCode();
        redisUtil.del(StrUtil.format(RedisKey.payTypeCacheById, dbCode, id));
        if (old != null && StrUtil.isNotBlank(old.getCode())) {
            redisUtil.del(StrUtil.format(RedisKey.payTypeCacheByCode, dbCode, old.getCode()));
        }
        invalidateMapCache(dbCode);
        return result;
    }

    public List<PayType> getAllList() {
        return payTypeMapper.getAllList();
    }

    /**
     * 带缓存查询支付类型（TTL 5分钟，更新/删除时自动失效）
     */
    public PayType getByIdWithCache(Long id) {
        if (id == null) return null;
        String cacheKey = StrUtil.format(RedisKey.payTypeCacheById, CecuUtil.getDbCode(), id);
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return JSON.parseObject(cached.toString(), PayType.class);
        }
        PayType payType = this.getById(id);
        if (payType != null) {
            redisUtil.set(cacheKey, JSON.toJSONString(payType), 5 * 60);
        }
        return payType;
    }

    public PayType getByCodeWithCache(String payTypeCode) {
        if (StrUtil.isBlank(payTypeCode)) return null;
        String cacheKey = StrUtil.format(RedisKey.payTypeCacheByCode, CecuUtil.getDbCode(), payTypeCode);
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return JSON.parseObject(cached.toString(), PayType.class);
        }
        PayType payType = this.getOne(Wrappers.lambdaQuery(PayType.class).eq(PayType::getCode, payTypeCode));
        if (payType != null) {
            redisUtil.set(cacheKey, JSON.toJSONString(payType), 5 * 60);
        }
        return payType;
    }

    /** 全量Map缓存（id→PayType），一次Redis交互替代循环内多次getByIdWithCache */
    public Map<Long, PayType> getMapByIdCached() {
        String cacheKey = StrUtil.format(RedisKey.payTypeMapById, CecuUtil.getDbCode());
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            List<PayType> list = JSON.parseArray(cached.toString(), PayType.class);
            return list.stream().collect(Collectors.toMap(e -> Long.valueOf(e.getId()), Function.identity()));
        }
        List<PayType> list = this.list();
        if (!list.isEmpty()) {
            redisUtil.set(cacheKey, JSON.toJSONString(list), 5 * 60);
        }
        return list.stream().collect(Collectors.toMap(e -> Long.valueOf(e.getId()), Function.identity()));
    }

    /** 全量Map缓存（code→PayType），一次Redis交互替代循环内多次getByCodeWithCache */
    public Map<String, PayType> getMapByCodeCached() {
        String cacheKey = StrUtil.format(RedisKey.payTypeMapByCode, CecuUtil.getDbCode());
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            List<PayType> list = JSON.parseArray(cached.toString(), PayType.class);
            return list.stream().filter(e -> StrUtil.isNotBlank(e.getCode()))
                    .collect(Collectors.toMap(PayType::getCode, Function.identity()));
        }
        List<PayType> list = this.list();
        if (!list.isEmpty()) {
            redisUtil.set(cacheKey, JSON.toJSONString(list), 5 * 60);
        }
        return list.stream().filter(e -> StrUtil.isNotBlank(e.getCode()))
                .collect(Collectors.toMap(PayType::getCode, Function.identity()));
    }

    private void invalidateMapCache(String dbCode) {
        redisUtil.del(StrUtil.format(RedisKey.payTypeMapById, dbCode));
        redisUtil.del(StrUtil.format(RedisKey.payTypeMapByCode, dbCode));
        redisUtil.del(StrUtil.format(RedisKey.payTypeRechargeList, dbCode));
    }

    /**
     * 带缓存查询充值可用的支付类型列表（status=1 && isRechange=1，按 sort 升序，TTL 5 分钟）
     * 缓存不含用户等级过滤与头像域名拼接，由调用方应用（保证 Nacos fileAdmin 变更实时生效）
     */
    public List<PayType> getRechargeListWithCache() {
        String cacheKey = StrUtil.format(RedisKey.payTypeRechargeList, CecuUtil.getDbCode());
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return JSON.parseArray(cached.toString(), PayType.class);
        }
        List<PayType> list = this.list(
                Wrappers.lambdaQuery(PayType.class)
                        .eq(PayType::getStatus, 1)
                        .eq(PayType::getIsRechange, 1)
                        .orderByAsc(PayType::getSort));
        if (list != null && !list.isEmpty()) {
            redisUtil.set(cacheKey, JSON.toJSONString(list), 5 * 60);
        }
        return list == null ? new java.util.ArrayList<>() : list;
    }
}
