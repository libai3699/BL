package com.gp.common.mybatisplus.service;

import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.util.RedisUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.RedisKey;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.PayChannelMoneyMapper;
import com.gp.common.mybatisplus.entity.PayChannelMoney;
import com.gp.common.mybatisplus.service.PayChannelMoneyService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author axing
 * @date 2026-01-09
 */
@Service
public class PayChannelMoneyService extends ServiceImpl<PayChannelMoneyMapper, PayChannelMoney>
{
    @Autowired
    private PayChannelMoneyMapper payChannelMoneyMapper;

    @Resource
    private RedisUtil redisUtil;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */

    public PayChannelMoney selectPayChannelMoneyById(Integer id)
    {
        return payChannelMoneyMapper.selectPayChannelMoneyById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param param 【请填写功能名称】
     * @return 【请填写功能名称】
     */

    public List<PayChannelMoney> selectPayChannelMoneyList(PayChannelMoney param)
    {
        return payChannelMoneyMapper.selectPayChannelMoneyList(param);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param param 【请填写功能名称】
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertPayChannelMoney(PayChannelMoney param)
    {
        boolean result = this.save(param);
        invalidateQuickAmountCache();
        return result;
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param param 【请填写功能名称】
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePayChannelMoney(PayChannelMoney param)
    {
        boolean result = this.updateById(param);
        invalidateQuickAmountCache();
        return result;
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePayChannelMoneyByIds(Integer[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        invalidateQuickAmountCache();
        return result;
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePayChannelMoneyById(Integer id)
    {
        boolean result = this.removeById(id);
        invalidateQuickAmountCache();
        return result;
    }

    /**
     * 带缓存查询指定支付类型 + 开放等级下的快捷金额列表（TTL 5 分钟）
     * 缓存 key 按 dbCode + typeCode + vip(开放等级) 分桶
     */
    public List<Long> getQuickAmountsWithCache(String payTypeCode, Integer vip) {
        if (StrUtil.isBlank(payTypeCode) || vip == null) {
            return payChannelMoneyMapper.selectMoney(payTypeCode, vip);
        }
        String cacheKey = StrUtil.format(RedisKey.payChannelMoneyQuick,
                CecuUtil.getDbCode(), payTypeCode, vip);
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return JSON.parseArray(cached.toString(), Long.class);
        }
        List<Long> moneyList = payChannelMoneyMapper.selectMoney(payTypeCode, vip);
        if (CollUtil.isNotEmpty(moneyList)) {
            redisUtil.set(cacheKey, JSON.toJSONString(moneyList), 5 * 60);
        }
        return moneyList;
    }

    /**
     * 失效当前 dbCode 下所有支付通道快捷金额缓存
     * 写入侧（本 Service 的 insert/update/delete + PayChannelService 的 remove/saveBatch）调用
     */
    public void invalidateQuickAmountCache() {
        String pattern = StrUtil.format(RedisKey.payChannelMoneyQuickPattern, CecuUtil.getDbCode());
        List<String> keys = redisUtil.scanKeysWithPagination(pattern, 1, 2000);
        if (CollUtil.isNotEmpty(keys)) {
            keys.forEach(redisUtil::del);
        }
    }
}
