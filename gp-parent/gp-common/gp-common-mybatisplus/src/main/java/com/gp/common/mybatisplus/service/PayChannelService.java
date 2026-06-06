package com.gp.common.mybatisplus.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.util.RedisUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.enums.CodeEnum;
import com.gp.common.base.exception.BusinessException;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.PayChannel;
import com.gp.common.mybatisplus.entity.PayChannelMoney;
import com.gp.common.mybatisplus.entity.PayType;
import com.gp.common.mybatisplus.mapper.PayChannelMapper;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author axing
 * @date 2026-01-09
 */
@Service
public class PayChannelService extends ServiceImpl<PayChannelMapper, PayChannel> {
    @Resource
    private PayChannelMapper payChannelMapper;

    @Resource
    private PayChannelMoneyService payChannelMoneyService;

    @Resource
    private RedisUtil redisUtil;

    @Resource
    private PayTypeService payTypeService;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */

    public PayChannel selectPayChannelById(Integer id) {
        return payChannelMapper.selectPayChannelById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param param 【请填写功能名称】
     * @return 【请填写功能名称】
     */

    public List<PayChannel> selectPayChannelList(PayChannel param) {
        return payChannelMapper.selectPayChannelList(param);
    }

    /**
     * 获取提现启用的通道(包含商户启用,并且支持提现)
     *
     * @return
     */
    public List<PayChannel> listWithMerchant(Long payTypeId) {
        return payChannelMapper.listWithMerchant(payTypeId);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param param 【请填写功能名称】
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertPayChannel(PayChannel param) {
        //充值最低和最高不能为0
        if (param.getRechargeMin().compareTo(BigDecimal.ZERO) == 0) {
            String msg = "最低不能为0";
            if (param.getType() == 1) {
                msg = "充值" + msg;
            }
            if (param.getType() == 2) {
                msg = "提现" + msg;
            }
            throw new BusinessException(CodeEnum.Error.getCode(), msg);
        }
        if (param.getRechargeMax().compareTo(BigDecimal.ZERO) == 0) {
            String msg = "最高不能为0";
            if (param.getType() == 1) {
                msg = "充值" + msg;
            }
            if (param.getType() == 2) {
                msg = "提现" + msg;
            }
            throw new BusinessException(CodeEnum.Error.getCode(), msg);
        }
        param.setCreateTime(DateUtils.getNowDate());
        boolean save = this.save(param);
        invalidateMapCache(CecuUtil.getDbCode());
        //提现类型无快捷金额
        if (Objects.equals(param.getType(), 2)) {
            return save;
        }
        //新增快捷金额
        savePayChannelMoney(param);
        //失效快捷金额缓存
        payChannelMoneyService.invalidateQuickAmountCache();
        return save;
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param param 【请填写功能名称】
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePayChannel(PayChannel param) {
        //充值最低和最高不能为0
        if (param.getRechargeMin().compareTo(BigDecimal.ZERO) == 0) {
            String msg = "最低不能为0";
            if (param.getType() == 1) {
                msg = "充值" + msg;
            }
            if (param.getType() == 2) {
                msg = "提现" + msg;
            }
            throw new BusinessException(CodeEnum.Error.getCode(), msg);
        }
        if (param.getRechargeMax().compareTo(BigDecimal.ZERO) == 0) {
            String msg = "最高不能为0";
            if (param.getType() == 1) {
                msg = "充值" + msg;
            }
            if (param.getType() == 2) {
                msg = "提现" + msg;
            }
            throw new BusinessException(CodeEnum.Error.getCode(), msg);
        }

        PayChannel byId = this.getById(param.getId());
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        String dbCode = CecuUtil.getDbCode();
        redisUtil.del(StrUtil.format(RedisKey.payChannelCacheById, dbCode, param.getId()));
        if (byId != null && StrUtil.isNotBlank(byId.getCode())) {
            redisUtil.del(StrUtil.format(RedisKey.payChannelCacheByCode, dbCode, byId.getCode()));
        }
        invalidateMapCache(dbCode);
        //提现类型无快捷金额
        if (Objects.equals(param.getType(), 2)) {
            return result;
        }
        //判断快捷金额是否变化
        if (!Objects.equals(byId.getQuickAmount(), param.getQuickAmount()) || !Objects.equals(byId.getPayRate(), param.getPayRate())
                || !Objects.equals(byId.getOpenLevel(), param.getOpenLevel()) || !Objects.equals(byId.getOpenLevelMax(), param.getOpenLevelMax())
                || !Objects.equals(byId.getCode(), param.getCode())) {
            //先删除快捷金额
            payChannelMoneyService.remove(Wrappers.lambdaQuery(PayChannelMoney.class).eq(PayChannelMoney::getChannelId, param.getId()));
            //新增快捷金额
            savePayChannelMoney(param);
            //失效快捷金额缓存
            payChannelMoneyService.invalidateQuickAmountCache();
        }

        return result;
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePayChannelByIds(Integer[] ids) {
        List<Integer> list = Arrays.asList(ids);
        boolean result = this.removeByIds(list);
        //删除快捷金额
        payChannelMoneyService.remove(Wrappers.lambdaQuery(PayChannelMoney.class).in(PayChannelMoney::getChannelId, list));
        // 清除缓存
        String dbCode = CecuUtil.getDbCode();
        list.forEach(id -> redisUtil.del(StrUtil.format(RedisKey.payChannelCacheById, dbCode, id)));
        invalidateMapCache(dbCode);
        payChannelMoneyService.invalidateQuickAmountCache();
        return result;
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePayChannelById(Integer id) {
        PayChannel byId = this.getById(id);
        boolean result = this.removeById(id);
        //删除快捷金额
        payChannelMoneyService.remove(Wrappers.lambdaQuery(PayChannelMoney.class).eq(PayChannelMoney::getChannelId, id));
        String dbCode = CecuUtil.getDbCode();
        redisUtil.del(StrUtil.format(RedisKey.payChannelCacheById, dbCode, id));
        if (byId != null && StrUtil.isNotBlank(byId.getCode())) {
            redisUtil.del(StrUtil.format(RedisKey.payChannelCacheByCode, dbCode, byId.getCode()));
        }
        invalidateMapCache(dbCode);
        payChannelMoneyService.invalidateQuickAmountCache();
        return result;
    }

    public PayChannel getByIdWithCache(Long id) {
        if (id == null)
            return null;
        String cacheKey = StrUtil.format(RedisKey.payChannelCacheById, CecuUtil.getDbCode(), id);
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return JSON.parseObject(cached.toString(), PayChannel.class);
        }
        PayChannel payChannel = this.getById(id);
        if (payChannel != null) {
            redisUtil.set(cacheKey, JSON.toJSONString(payChannel), 5 * 60);
        }
        return payChannel;
    }

    public PayChannel getByCodeWithCache(String code) {
        if (StrUtil.isBlank(code))
            return null;
        String cacheKey = StrUtil.format(RedisKey.payChannelCacheByCode, CecuUtil.getDbCode(), code);
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return JSON.parseObject(cached.toString(), PayChannel.class);
        }
        PayChannel payChannel = this.getOne(Wrappers.lambdaQuery(PayChannel.class).eq(PayChannel::getCode, code));
        if (payChannel != null) {
            redisUtil.set(cacheKey, JSON.toJSONString(payChannel), 5 * 60);
        }
        return payChannel;
    }

    /**
     * 全量Map缓存（id→PayChannel），一次Redis交互替代循环内多次getByIdWithCache
     */
    public Map<Long, PayChannel> getMapByIdCached() {
        String cacheKey = StrUtil.format(RedisKey.payChannelMapById, CecuUtil.getDbCode());
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            List<PayChannel> list = JSON.parseArray(cached.toString(), PayChannel.class);
            return list.stream().collect(Collectors.toMap(PayChannel::getId, Function.identity()));
        }
        List<PayChannel> list = this.list();
        if (!list.isEmpty()) {
            redisUtil.set(cacheKey, JSON.toJSONString(list), 5 * 60);
        }
        return list.stream().collect(Collectors.toMap(PayChannel::getId, Function.identity()));
    }

    /**
     * 全量Map缓存（code→PayChannel），一次Redis交互替代循环内多次getByCodeWithCache
     */
    public Map<String, PayChannel> getMapByCodeCached() {
        String cacheKey = StrUtil.format(RedisKey.payChannelMapByCode, CecuUtil.getDbCode());
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            List<PayChannel> list = JSON.parseArray(cached.toString(), PayChannel.class);
            return list.stream().filter(e -> StrUtil.isNotBlank(e.getCode()))
                    .collect(Collectors.toMap(PayChannel::getCode, Function.identity()));
        }
        List<PayChannel> list = this.list();
        if (!list.isEmpty()) {
            redisUtil.set(cacheKey, JSON.toJSONString(list), 5 * 60);
        }
        return list.stream().filter(e -> StrUtil.isNotBlank(e.getCode()))
                .collect(Collectors.toMap(PayChannel::getCode, Function.identity()));
    }

    /**
     * 保存支付通道快捷金额
     *
     * @param param 支付通道参数
     */
    private void savePayChannelMoney(PayChannel param) {
        String[] split = param.getQuickAmount().split(",");
        List<PayChannelMoney> payChannelMoneyList = new ArrayList<>();
        PayType payType = payTypeService.getById(param.getPayTypeId());
        if (payType == null) {
            //支付类型不存在
            throw new BusinessException(CodeEnum.Error.getCode(), "支付类型不存在");
        }

        //split快捷金额必须在充值最低和充值最高之间
        for (String amount : split) {
            BigDecimal money = new BigDecimal(amount);
            if (param.getRechargeMin() != null && money.compareTo(param.getRechargeMin()) < 0) {
                throw new BusinessException(CodeEnum.Error.getCode(), "快捷金额" + amount + "不能低于充值最低金额" + param.getRechargeMin());
            }
            if (param.getRechargeMax() != null && money.compareTo(param.getRechargeMax()) > 0) {
                throw new BusinessException(CodeEnum.Error.getCode(), "快捷金额" + amount + "不能高于充值最高金额" + param.getRechargeMax());
            }
        }

        //将充值最低和最高金额加入到快捷金额列表中（如果不存在）
        List<String> quickAmountList = getStrings(param, split);

        for (String amount : quickAmountList) {
            PayChannelMoney payChannelMoney = new PayChannelMoney();
            payChannelMoney.setChannelId(param.getId());
            payChannelMoney.setMoney(Long.parseLong(amount));
            payChannelMoney.setChannelPayRate(param.getPayRate());
            if (StrUtil.isBlank(param.getCode())) {
                payChannelMoney.setTypeCode(payType.getCode());
            } else {
                payChannelMoney.setTypeCode(param.getCode());
            }
            payChannelMoney.setOpenLevelMin(param.getOpenLevel());
            payChannelMoney.setOpenLevelMax(param.getOpenLevelMax());
            payChannelMoneyList.add(payChannelMoney);
        }
        payChannelMoneyService.saveBatch(payChannelMoneyList);
    }

    private static @NonNull List<String> getStrings(PayChannel param, String[] split) {
        List<String> quickAmountList = new ArrayList<>(Arrays.asList(split));
        if (param.getRechargeMin() != null) {
            String minAmount = param.getRechargeMin().stripTrailingZeros().toPlainString();
            if (!quickAmountList.contains(minAmount)) {
                quickAmountList.add(minAmount);
            }
        }
        if (param.getRechargeMax() != null) {
            String maxAmount = param.getRechargeMax().stripTrailingZeros().toPlainString();
            if (!quickAmountList.contains(maxAmount)) {
                quickAmountList.add(maxAmount);
            }
        }
        return quickAmountList;
    }

    private void invalidateMapCache(String dbCode) {
        redisUtil.del(StrUtil.format(RedisKey.payChannelMapById, dbCode));
        redisUtil.del(StrUtil.format(RedisKey.payChannelMapByCode, dbCode));
    }
}
