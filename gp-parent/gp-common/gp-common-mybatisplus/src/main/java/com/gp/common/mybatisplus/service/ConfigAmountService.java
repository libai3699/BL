package com.gp.common.mybatisplus.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.util.RedisUtil;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.cache.RedisClientApiCache;
import com.gp.common.mybatisplus.entity.ConfigAmount;
import com.gp.common.mybatisplus.entity.ConfigRisk;
import com.gp.common.mybatisplus.mapper.ConfigAmountMapper;
import com.gp.common.mybatisplus.until.ConfigAmountSign;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ConfigAmountService extends ServiceImpl<ConfigAmountMapper, ConfigAmount> {
    //提现手续费


    public static final String minRechargeAmount = "minRechargeAmount";
    public static final String minWithdrawAmount = "minWithdrawAmount";

    public static final String withdrawFee = "withdrawFee";
    public static final String wheelBonusRatio = "wheelBonusRatio";
    public static final String taskBonusRatio = "taskBonusRatio";
    public static final String upBonusRatio = "upBonusRatio";
    public static final String upAmountRatio = "upAmountRatio";
    public static final String myPayGive = "myPayGive";
    public static final String reportAmount = "reportAmount";
    public static final String reportAmountBS = "reportAmountBS";
    public static final String giveWheelNum = "giveWheelNum";
    public static final String signLevel = "signLevel";


    public static final String USDTTWO = "USDTTWO";

    public static final String reportType = "reportType";

    public static final String reportTM = "reportTM";
    @Resource
    private ConfigAmountSign configAmountSign;
    @Resource
    RedisUtil redisUtil;
    @Resource
    private RedisClientApiCache redisClientApiCache;

    @Resource
    private ConfigAmountMapper tConfigAmountMapper;
    @Resource
    private CecuUtil cecuUtil;

    public Boolean reportTM() {
        ConfigAmount configAmount = getValueMaybeNullConfig(reportTM);
        if (configAmount == null) {
            return false;
        }
        return "1".equals(configAmount.getConfigValue()) ;
    }

    public Boolean reportType() {
        ConfigAmount valueMaybeNullConfig = getValueMaybeNullConfig(reportType);
        if (valueMaybeNullConfig == null) {
            return false;
        }
        return "1".equals(valueMaybeNullConfig.getConfigValue());
    }

    public BigDecimal minRechargeAmount() {
        return new BigDecimal(getValue(minRechargeAmount));
    }


    public BigDecimal minWithdrawAmount() {
        return new BigDecimal(getValue(minWithdrawAmount));
    }


    public BigDecimal wheelBonusRatio() {
        return new BigDecimal(getValue(wheelBonusRatio));
    }

    public BigDecimal taskBonusRatio() {
        return new BigDecimal(getValue(taskBonusRatio));
    }

    public BigDecimal upBonusRatio() {
        return new BigDecimal(getValue(upBonusRatio));
    }

    public BigDecimal upAmountRatio() {
        return new BigDecimal(getValue(upAmountRatio));
    }



    public BigDecimal withdrawFee() {
        return new BigDecimal(getValue(withdrawFee));
    }

    public BigDecimal myPayGive() {
        return new BigDecimal(getValue(myPayGive));
    }

    public BigDecimal giveWheelNum() {
        return new BigDecimal(getValue(giveWheelNum));
    }

    public Integer signLevel() {
        return Integer.parseInt(getValue(signLevel));
    }



    public BigDecimal USDTTOLaw(String language) {
        return new BigDecimal(getValueNoCache(USDTTWO + language));
    }
    public BigDecimal USDTTWINR() {
        return new BigDecimal(getValueNoCache("USDTTWOhi"));
    }
    public BigDecimal USDTTWRMB() {
        return new BigDecimal(getValueNoCache("USDTTWOzh"));
    }

    public BigDecimal reportAmount() {
        return new BigDecimal(getValue(reportAmount));
    }
    public BigDecimal reportAmountBS() {
        String valueMaybeNull = getValueMaybeNull(reportAmountBS);
        if (valueMaybeNull == null) {
            return new BigDecimal("3");
        }
        return new BigDecimal(valueMaybeNull);
    }

    @PostConstruct
    public void init() {
        List<String> dbNameList = cecuUtil.getCecuProp().getAppList();
        for (String s : dbNameList) {
            String key = StrUtil.format(RedisKey.configAmountKey, s);
            if (redisUtil.hasKey(key)) {
                continue; // 如果 Redis 中已经存在该键，则跳过加载过程
            }
            cecuUtil.cutDbByCode(s);
            Map<String, ConfigAmount> configRiskKey = getConfigAmountKey();
            redisUtil.hmset(key, configRiskKey);
            redisUtil.expire(key, 1, TimeUnit.HOURS);

        }
        //清除切库信息,恢复默认数据库
        CecuUtil.cleanDbInfo();

    }

    public Map<String, ConfigAmount> getConfigAmountKey() {
        List<ConfigAmount> configAmounts = this.list();
        Map<String, ConfigAmount> configAmount = configAmounts.stream()
                .collect(Collectors.toMap(ConfigAmount::getConfigKey, Function.identity()));
        return configAmount;
    }

    public String getValue(String key) {
        ConfigAmount one = redisClientApiCache.queryConfigAmount(key);
        if (one == null) {
            return "1000";
        }
        return one.getConfigValue();
    }
    public String getValueMaybeNull(String key) {
        ConfigAmount one = redisClientApiCache.queryConfigAmountMaybeNull(key);
        if (one == null) {
            return null;
        }
        return one.getConfigValue();
    }
    public ConfigAmount getValueMaybeNullConfig(String key) {
        return redisClientApiCache.queryConfigAmountMaybeNull(key);
    }


    public String getValueNoCache(String key) {
        ConfigAmount one = this.getOne(Wrappers.lambdaQuery(ConfigAmount.class).eq(ConfigAmount::getConfigKey, key));
        if (one == null) {
            return "1";
        }
        return one.getConfigValue();
    }


    /**
     * 查询金额配置
     *
     * @param configId 金额配置ID
     * @return 金额配置
     */
    public ConfigAmount selectConfigAmountById(Integer configId) {
        return tConfigAmountMapper.selectConfigAmountById(configId);
    }

    /**
     * 查询金额配置列表
     *
     * @param tConfigAmount 金额配置
     * @return 金额配置
     */
    public List<ConfigAmount> selectConfigAmountList(ConfigAmount tConfigAmount) {
        return tConfigAmountMapper.selectConfigAmountList(tConfigAmount);
    }

    /**
     * 新增金额配置
     *
     * @param tConfigAmount 金额配置
     * @return 结果
     */
    public Boolean insertConfigAmount(ConfigAmount tConfigAmount) {
        tConfigAmount.setCreateTime(DateUtils.getNowDate());
        //增加签名
        configAmountSign.dealSign(tConfigAmount);
        boolean result = this.save(tConfigAmount);
        redisClientApiCache.resetConfigAmount();
        return result;
    }

    /**
     * 修改金额配置
     *
     * @param tConfigAmount 金额配置
     * @return 结果
     */
    public Boolean updateConfigAmount(ConfigAmount tConfigAmount) {
        tConfigAmount.setUpdateTime(DateUtils.getNowDate());
        configAmountSign.dealSign(tConfigAmount);
        boolean result = this.updateById(tConfigAmount);
        redisClientApiCache.resetConfigAmount();
        return result;
    }

    /**
     * 批量删除金额配置
     *
     * @param configIds 需要删除的金额配置ID
     * @return 结果
     */
    public Boolean deleteConfigAmountByIds(Integer[] configIds) {
        boolean result = this.removeByIds(Arrays.asList(configIds));
        redisClientApiCache.resetConfigAmount();
        return result;
    }


    public boolean updateIsDispById(ConfigAmount tConfigAmount) {
        configAmountSign.dealSign(tConfigAmount);
        boolean result = tConfigAmountMapper.updateIsDispById(tConfigAmount);
        redisClientApiCache.resetConfigAmount();
        return result;
    }
}
