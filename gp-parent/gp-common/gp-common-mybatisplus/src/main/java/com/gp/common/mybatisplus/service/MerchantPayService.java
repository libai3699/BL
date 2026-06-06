package com.gp.common.mybatisplus.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.constant.CommonConstant;
import com.common.core.util.RedisUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.MerchantPay;
import com.gp.common.mybatisplus.mapper.MerchantPayMapper;
import com.gp.common.mybatisplus.merchantpay.constants.PayMerchantCons;
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
 * 商户Service业务层处理
 *
 * @author axing
 * @date 2024-03-11
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class MerchantPayService extends ServiceImpl<MerchantPayMapper, MerchantPay>
{
    @Autowired
    private MerchantPayMapper merchantPayMapper;

    @Resource
    private RedisUtil redisUtil;

    /**
     * 查询商户
     *
     * @param id 商户ID
     * @return 商户
     */

    public MerchantPay selectMerchantPayById(Long id)
    {
        return merchantPayMapper.selectMerchantPayById(id);
    }

    /**
     * 查询商户列表
     *
     * @param merchantPay 商户
     * @return 商户
     */

    public List<MerchantPay> selectMerchantPayList(MerchantPay merchantPay)
    {
        return merchantPayMapper.selectMerchantPayList(merchantPay);
    }

    /**
     * 新增商户
     *
     * @param merchantPay 商户
     * @return 结果
     */

    public Boolean insertMerchantPay(MerchantPay merchantPay)
    {
        merchantPay.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(merchantPay);
        invalidateMapCache(CecuUtil.getDbCode());
        return result;
    }

    /**
     * 修改商户
     *
     * @param merchantPay 商户
     * @return 结果
     */

    public Boolean updateMerchantPay(MerchantPay merchantPay)
    {
        merchantPay.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(merchantPay);
        String dbCode = CecuUtil.getDbCode();
        redisUtil.del(StrUtil.format(RedisKey.merchantPayCacheById, dbCode, merchantPay.getId()));
        if (StrUtil.isNotBlank(merchantPay.getCode())) {
            redisUtil.del(StrUtil.format(RedisKey.merchantPayCacheByCode, dbCode, merchantPay.getCode()));
        }
        invalidateMapCache(dbCode);
        return result;
    }

    /**
     * 批量删除商户
     *
     * @param ids 需要删除的商户ID
     * @return 结果
     */

    public Boolean deleteMerchantPayByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        String dbCode = CecuUtil.getDbCode();
        Arrays.stream(ids).forEach(id -> redisUtil.del(StrUtil.format(RedisKey.merchantPayCacheById, dbCode, id)));
        invalidateMapCache(dbCode);
        return result;
    }

    /**
     * 删除商户信息
     *
     * @param id 商户ID
     * @return 结果
     */

    public Boolean deleteMerchantPayById(Long id)
    {
        MerchantPay byId = this.getById(id);
        boolean result = this.removeById(id);
        String dbCode = CecuUtil.getDbCode();
        redisUtil.del(StrUtil.format(RedisKey.merchantPayCacheById, dbCode, id));
        if (byId != null && StrUtil.isNotBlank(byId.getCode())) {
            redisUtil.del(StrUtil.format(RedisKey.merchantPayCacheByCode, dbCode, byId.getCode()));
        }
        invalidateMapCache(dbCode);
        return result;
    }

    public MerchantPay getByIdWithCache(Long id) {
        if (id == null) return null;
        String cacheKey = StrUtil.format(RedisKey.merchantPayCacheById, CecuUtil.getDbCode(), id);
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return JSON.parseObject(cached.toString(), MerchantPay.class);
        }
        MerchantPay merchantPay = this.getById(id);
        if (merchantPay != null) {
            redisUtil.set(cacheKey, JSON.toJSONString(merchantPay), 5 * 60);
        }
        return merchantPay;
    }

    public MerchantPay getByCodeWithCache(String code) {
        if (StrUtil.isBlank(code)) return null;
        String cacheKey = StrUtil.format(RedisKey.merchantPayCacheByCode, CecuUtil.getDbCode(), code);
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return JSON.parseObject(cached.toString(), MerchantPay.class);
        }
        MerchantPay merchantPay = this.getOne(Wrappers.lambdaQuery(MerchantPay.class)
                .eq(MerchantPay::getCode, code)
                .eq(MerchantPay::getStatus, CommonConstant.OPEN)
                .last("limit 1"));
        if (merchantPay != null) {
            redisUtil.set(cacheKey, JSON.toJSONString(merchantPay), 5 * 60);
        }
        return merchantPay;
    }

    public MerchantPay getByAppId(String appId,String code) {
        return merchantPayMapper.getByAppId(appId,code);
    }

    public String queryCreateAddress() {
        MerchantPay merchantPay = this.queryMerchantPayByType(PayMerchantCons.WALLET_PAY);
        return  merchantPay == null?"":merchantPay.getParamStr();
    }



    public MerchantPay queryMerchantPayByType(String payCode) {
        LambdaQueryWrapper<MerchantPay> q = new LambdaQueryWrapper<>();
        q.last("limit 1");
        q.eq(MerchantPay::getCode,  payCode);
        q.eq(MerchantPay::getStatus, CommonConstant.OPEN);
        return merchantPayMapper.selectOne(q);
    }


//    public PayEnum queryPayCodeByType(String language) {
//        if(language.equals(MessagesUtils.Spain.toString())){
//            return PayEnum.PIX_PAY;
//        }
//        if(language.equals( MessagesUtils.Viet.toString())){
//            return PayEnum.PIX_YND_PAY;
//        }
//        return null;
//    }

    public MerchantPay queryMerchantPayByCode(String payCode) {
        LambdaQueryWrapper<MerchantPay> q = new LambdaQueryWrapper<>();
        q.last("limit 1");
        q.eq(MerchantPay::getCode,  payCode);
        q.eq(MerchantPay::getStatus, CommonConstant.OPEN);
        return merchantPayMapper.selectOne(q);
    }

    /** 全量Map缓存（id→MerchantPay），一次Redis交互替代循环内多次getByIdWithCache */
    public Map<Long, MerchantPay> getMapByIdCached() {
        String cacheKey = StrUtil.format(RedisKey.merchantPayMapById, CecuUtil.getDbCode());
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            List<MerchantPay> list = JSON.parseArray(cached.toString(), MerchantPay.class);
            return list.stream().collect(Collectors.toMap(MerchantPay::getId, Function.identity()));
        }
        List<MerchantPay> list = this.list();
        if (!list.isEmpty()) {
            redisUtil.set(cacheKey, JSON.toJSONString(list), 5 * 60);
        }
        return list.stream().collect(Collectors.toMap(MerchantPay::getId, Function.identity()));
    }

    /** 全量Map缓存（code→MerchantPay），一次Redis交互替代循环内多次getByCodeWithCache */
    public Map<String, MerchantPay> getMapByCodeCached() {
        String cacheKey = StrUtil.format(RedisKey.merchantPayMapByCode, CecuUtil.getDbCode());
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            List<MerchantPay> list = JSON.parseArray(cached.toString(), MerchantPay.class);
            return list.stream().filter(e -> StrUtil.isNotBlank(e.getCode()))
                    .collect(Collectors.toMap(MerchantPay::getCode, Function.identity()));
        }
        List<MerchantPay> list = this.list();
        if (!list.isEmpty()) {
            redisUtil.set(cacheKey, JSON.toJSONString(list), 5 * 60);
        }
        return list.stream().filter(e -> StrUtil.isNotBlank(e.getCode()))
                .collect(Collectors.toMap(MerchantPay::getCode, Function.identity()));
    }

    private void invalidateMapCache(String dbCode) {
        redisUtil.del(StrUtil.format(RedisKey.merchantPayMapById, dbCode));
        redisUtil.del(StrUtil.format(RedisKey.merchantPayMapByCode, dbCode));
    }
}
