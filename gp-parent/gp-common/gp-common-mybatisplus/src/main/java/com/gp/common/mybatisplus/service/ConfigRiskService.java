package com.gp.common.mybatisplus.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.util.RedisUtil;
import com.common.core.util.StringUtils;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.cache.RedisClientApiCache;
import com.gp.common.mybatisplus.entity.ConfigRisk;
import com.gp.common.mybatisplus.mapper.ConfigRiskMapper;
import com.gp.common.mybatisplus.until.ConfigRiskSign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ConfigRiskService extends ServiceImpl<ConfigRiskMapper, ConfigRisk> {

    @Resource
    private ConfigRiskSign configRiskSign;

    @Autowired
    private ConfigRiskMapper tConfigRiskMapper;
    @Resource
    private RedisClientApiCache redisClientApiCache;

    public static final String risk_win_amount = "risk_win_amount";

    public static final String hw_addr_pic = "hw_addr_pic";

    public static final String hw_shop_name = "hw_shop_name";

    public static final String hw_shop_account = "hw_shop_account";

    public static final String foot_ball_game_id = "foot_ball_game_id";

    public static final String password_error_times = "password_error_times";
    public static final String rechargeGiveBonus = "rechargeGiveBonus";

    public static final String sms_send_error_times = "sms_send_error_times";
    public static final String mail_send_error_times = "mail_send_error_times";
    public static final String register_times = "register_times";

    //禁止提现时间(小时)
    public static final String ban_password_error_time = "ban_password_error_time";
    public static final String notifyBot = "notifyBot";
    /**
     * 推广链接
     */

    public static final String landPageAdmin = "landPageAdmin";
    /**
     * tiktok token
     */
    public static final String tiktokToken = "tiktokToken_";
    /**
     * tiktok Piex
     */
    public static final String tiktokPiexID = "tiktokPiexID_";

    /**
     * tiktok facebook
     */
    public static final String facebookToken = "facebookToken_";
    /**
     * tiktok facebook
     */
    public static final String facebookPiexID = "facebookPiexID_";

    /**
     * 中奖排行榜投注区间
     */
    public static final String winRankBetSection = "winRankBetSection";
    /**
     * 中奖排行榜中奖区间
     */
    public static final String winRankBetWinRate = "winRankBetWinRate";
    /**
     * 投注排行榜没半个小时增长区间
     */
    public static final String betRankHourSection = "betRankHourSection";
    /**
     * 推荐游戏
     */
    public static final String recommendedGame = "recommendedGame";
    /**
     * 用户地址数量
     */
    public static final String userAddressCount = "userAddressCount";
    /**
     * 是否开启代理模式
     */
    public static final String isAgentModel = "isAgentModel";
    /**
     * 极差模式配置(1-活跃人数 2-打码量)
     */
    public static final String rangeModeConfig = "range_mode_config";
    /**
     * 是否开启代理模式
     */
    public static final String defaultUpUserId = "defaultUpUserId";
    public static final String defaultUpChannelId = "defaultUpChannelId";

    public static final String appDownUrl = "appDownUrl";

    public static final String isTOSyncVIP1 = "isTOSyncVIP1";

    public static final String isSetRebate = "isSetRebate";

    public static final String isTransfer = "isTransfer";
    //上级反水
    public static final String userRebate = "userRebate";
    //返佣
    public static final String superUserRebate = "superUserRebate";
    public static final String botAdd = "botAdd";
    public static final String transferNum = "transferNum";
    public static final String isLevelBonus = "isLevelBonus";
    public static final String noCheckYZM = "noCheckYZM";
    public static final String checkUp = "checkUp";
    public static final String filterGameType = "filterGameType";

    public static final String isOpenAccount = "isOpenAccount";

    // 分红手续费比例(官方收取比例)
    public static final String transactionFeeRate = "transactionFeeRate";
    // 充值手续费比例
    public static final String rechargeFeeRate = "rechargeFeeRate";
    // 提现手续费比例
    public static final String withdrawFeeRate = "withdrawFeeRate";
    // 管理费比例
    public static final String managementFeeRate = "managementFeeRate";
    // 彩票充值上级分佣比例和层级
    public static final String lotteryRechargeRatio = "lotteryRechargeRatio";

    public static final String lotteryLossRatio = "lotteryLossRatio";

    public static final String DZGameId = "DZGameId";

    //腾讯直播appid
    public static final String tencentLiveAppId = "tencentLiveAppId";

    //腾讯直播key
    public static final String tencentLiveKey = "tencentLiveKey";

    //腾讯直播房间号
    public static final String tencentLiveRoomNo = "tencentLiveRoomNo";

    public static final String isOfficialWithdraw = "isOfficialWithdraw";

    //分享文案
    public static final String shareUrlContent = "shareUrlContent";

    public static final String firstChargeNotBet = "firstChargeNotBet";
    //是否开启色站
    public static final String videoUrl = "videoUrl";
    //福利通告群
    public static final String flNoticeGroup = "flNoticeGroup";

    //福利通告群
    public static final String withdrawFee = "withdrawFee";

    //5分钟内,能刷新的支付订单数量
    public static final String Min5PayCount = "Min5PayCount";
    //支付跳转的域名
    public static final String payDomain = "payDomain";

    public Long tencentLiveAppId() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(tencentLiveAppId);
        if (configOneUrlMaybeNul == null) {
            log.error("tencentLiveAppId is null");
            return null;
        }
        return Long.parseLong(configOneUrlMaybeNul.getConfigValue());
    }

    public String tencentLiveKey() {
        return getValue(tencentLiveKey);
    }

    public String tencentLiveRoomNo() {
        return getValue(tencentLiveRoomNo);
    }

    public Boolean noCheckYZM() {
        Boolean flag = false;
        String value = getValue(noCheckYZM);
        if (value != null) {
            return "1".equals(value);
        }
        return flag;
    }

    public Boolean checkUp() {
        Boolean flag = false;
        String value = getValue(checkUp);
        if (value != null) {
            return "1".equals(value);
        }
        return flag;
    }

    public String payDomain() {
        String value = getValue(payDomain);
        if (StrUtil.isBlank(value)) {
            throw new RuntimeException("配置不完整,请联系客服!");
        } else {
            return value;
        }
    }

    public String hw_addr_pic() {
        return getValue(hw_addr_pic);
    }

    public String shareUrlContent() {
        return getValue(shareUrlContent);
    }

    public String hw_shop_name() {
        return getValue(hw_shop_name);
    }

    public String hw_shop_account() {
        return getValue(hw_shop_account);
    }

    public String foot_ball_game_id() {
        return getValue(foot_ball_game_id);
    }

    public String DZGameId() {
        return getValue(DZGameId);
    }

    public String sms_send_error_times() {
        return getValue(sms_send_error_times);
    }

    public String mail_send_error_times() {
        return getValue(mail_send_error_times);
    }

    public String register_times() {
        return getValue(register_times);
    }

    public String filterGameType() {
        return getValue(filterGameType);
    }

    public String lotteryRechargeRatio() {
        return getValue(lotteryRechargeRatio);
    }

    public String lotteryLossRatio() {
        return getValue(lotteryLossRatio);
    }

    public String userAddressCount() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(userAddressCount);
        if (configOneUrlMaybeNul == null) {
            return "3";
        }
        return configOneUrlMaybeNul.getConfigValue();
    }

    /**
     * 分红手续费比例
     *
     * @return 分红手续费比例 未配置时返回0
     */
    public BigDecimal transactionFeeRate() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(transactionFeeRate);
        if (configOneUrlMaybeNul == null) {
            return BigDecimal.ZERO;
        }
        if (configOneUrlMaybeNul.getConfigValue().equals("0")) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(configOneUrlMaybeNul.getConfigValue());
        } catch (NumberFormatException e) {
            log.warn("Invalid BigDecimal value for transactionFeeRate: {}", configOneUrlMaybeNul.getConfigValue());
            return BigDecimal.ZERO;
        }
    }

    /**
     * 充值手续费比例
     *
     * @return 充值手续费比例 未配置时返回0
     */
    public BigDecimal rechargeFeeRate() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(rechargeFeeRate);
        if (configOneUrlMaybeNul == null) {
            return BigDecimal.ZERO;
        }
        if (configOneUrlMaybeNul.getConfigValue().equals("0")) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(configOneUrlMaybeNul.getConfigValue());
        } catch (NumberFormatException e) {
            log.warn("Invalid BigDecimal value for rechargeFeeRate: {}", configOneUrlMaybeNul.getConfigValue());
            return BigDecimal.ZERO;
        }
    }

    /**
     * 提现手续费比例
     *
     * @return 提现手续费比例 未配置时返回0
     */
    public BigDecimal withdrawFeeRate() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(withdrawFeeRate);
        if (configOneUrlMaybeNul == null) {
            return BigDecimal.ZERO;
        }
        if (configOneUrlMaybeNul.getConfigValue().equals("0")) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(configOneUrlMaybeNul.getConfigValue());
        } catch (NumberFormatException e) {
            log.warn("Invalid BigDecimal value for withdrawFeeRate: {}", configOneUrlMaybeNul.getConfigValue());
            return BigDecimal.ZERO;
        }
    }

    /**
     * 管理费比例
     *
     * @return 管理费比例 未配置时返回0
     */
    public BigDecimal managementFeeRate() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(managementFeeRate);
        if (configOneUrlMaybeNul == null) {
            return BigDecimal.ZERO;
        }
        if (configOneUrlMaybeNul.getConfigValue().equals("0")) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(configOneUrlMaybeNul.getConfigValue());
        } catch (NumberFormatException e) {
            log.warn("Invalid BigDecimal value for managementFeeRate: {}", configOneUrlMaybeNul.getConfigValue());
            return BigDecimal.ZERO;
        }
    }

    /**
     * 是否同步vip1的数据到vip2-5
     *
     * @return true-同步 false-不同步
     */
    public boolean isTOSyncVIP1() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(isTOSyncVIP1);
        if (configOneUrlMaybeNul == null) {
            return Boolean.FALSE;
        }
        return "1".equals(configOneUrlMaybeNul.getConfigValue());
    }

    public Integer isAgentModelInt() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(isAgentModel);
        if (configOneUrlMaybeNul == null) {
            return 0;
        }
        return Integer.valueOf(configOneUrlMaybeNul.getConfigValue());
    }

    /**
     * 极差模式
     * return true-极差模式 false-非极差模式
     */
    public Boolean isAgentModel() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(isAgentModel);
        if (configOneUrlMaybeNul == null) {
            return false;
        }
        return "1".equals(configOneUrlMaybeNul.getConfigValue());
    }

    /**
     * 极差模式配置(1-活跃人数 2-打码量)
     */
    public int rangeModeConfig() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(rangeModeConfig);
        if (configOneUrlMaybeNul == null) {
            return 1;
        }
        return Integer.parseInt(configOneUrlMaybeNul.getConfigValue());
    }

    // 普通和代理模式  -> 传统代理模式
    public Boolean isCommonModel() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(isAgentModel);
        if (configOneUrlMaybeNul == null) {
            return true;
        }
        return "2".equals(configOneUrlMaybeNul.getConfigValue()) || "0".equals(configOneUrlMaybeNul.getConfigValue());
    }

    /**
     * 彩票模式
     *
     * @return true-彩票模式 false-非彩票模式
     */
    public Boolean isLotteryModel() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(isAgentModel);
        if (configOneUrlMaybeNul == null) {
            return false;
        }
        return "3".equals(configOneUrlMaybeNul.getConfigValue());
    }

    public Boolean isOpenAccount() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(isOpenAccount);
        if (configOneUrlMaybeNul == null) {
            return false;
        }
        return "1".equals(configOneUrlMaybeNul.getConfigValue());
    }

    public Boolean isLevelBonus() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(isLevelBonus);
        if (configOneUrlMaybeNul == null) {
            return false;
        }
        return "1".equals(configOneUrlMaybeNul.getConfigValue());
    }

    public Long defaultUpUserId() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(defaultUpUserId);
        if (configOneUrlMaybeNul == null) {
            return null;
        }
        if (configOneUrlMaybeNul.getConfigValue().equals("0")) {
            return null;
        }
        return Long.parseLong(configOneUrlMaybeNul.getConfigValue());
    }

    public Long transferNum() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(transferNum);
        if (configOneUrlMaybeNul == null) {
            return null;
        }
        if (configOneUrlMaybeNul.getConfigValue().equals("0")) {
            return null;
        }
        return Long.parseLong(configOneUrlMaybeNul.getConfigValue());
    }

    public Long defaultUpChannelId() {

        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(defaultUpChannelId);
        if (configOneUrlMaybeNul == null) {
            return null;
        }
        if (configOneUrlMaybeNul.getConfigValue().equals("0")) {
            return null;
        }
        return Long.parseLong(configOneUrlMaybeNul.getConfigValue());
    }

    public String appDownUrl() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(appDownUrl);
        if (configOneUrlMaybeNul == null) {
            return null;
        }
        return configOneUrlMaybeNul.getConfigValue();
    }

    public Boolean isTransfer() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(isTransfer);
        if (configOneUrlMaybeNul == null) {
            return false;
        }
        return "1".equals(configOneUrlMaybeNul.getConfigValue());
    }

    public Boolean isSetRebate() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(isSetRebate);
        if (configOneUrlMaybeNul == null) {
            return false;
        }
        return "1".equals(configOneUrlMaybeNul.getConfigValue());
    }

    public String userRebate() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(userRebate);
        if (configOneUrlMaybeNul == null) {
            return null;
        }
        return configOneUrlMaybeNul.getConfigValue();
    }

    public String superUserRebate() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(superUserRebate);
        if (configOneUrlMaybeNul == null) {
            return null;
        }
        return configOneUrlMaybeNul.getConfigValue();
    }

    public String botAdd() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(botAdd);
        if (configOneUrlMaybeNul == null) {
            return null;
        }
        return configOneUrlMaybeNul.getConfigValue();
    }

    public Boolean firstChargeNotBet() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(firstChargeNotBet);
        if (configOneUrlMaybeNul == null) {
            return false;
        }
        return "1".equals(configOneUrlMaybeNul.getConfigValue());
    }

    public String videoH5() {
        return getValue(videoUrl);
    }

    @Cacheable(value = "risk_win_amount", key = "#dbCode+ '-risk_win_amount'")
    public List<BigDecimal> risk_win_amount(String dbCode) {
        return getRiskWinAmount(risk_win_amount);
    }

    @Resource
    RedisUtil redisUtil;
    @Resource
    private CecuUtil cecuUtil;

    @PostConstruct
    public void init() {
        List<String> dbNameList = cecuUtil.getCecuProp().getAppList();
        for (String s : dbNameList) {
            String key = StrUtil.format(RedisKey.configRiskKey, s);
            if (redisUtil.hasKey(key)) {
                continue; // 如果 Redis 中已经存在该键，则跳过加载过程
            }
            cecuUtil.cutDbByCode(s);
            Map<String, ConfigRisk> configRiskKey = this.getConfigRiskKey();
            redisUtil.hmset(key, configRiskKey);
            redisUtil.expire(key, 1, TimeUnit.HOURS);

        }
        //清除切库信息,恢复默认数据库
        CecuUtil.cleanDbInfo();
    }

    public Map<String, ConfigRisk> getConfigRiskKey() {
        List<ConfigRisk> configRisks = this.list();
        Map<String, ConfigRisk> configRiskMap = configRisks.stream()
                .collect(Collectors.toMap(ConfigRisk::getConfigKey, Function.identity()));
        return configRiskMap;
    }

    public String ban_password_error_time() {
        return getValue(ban_password_error_time);
    }

    public String getValue(String key) {
//        ConfigRisk one = redisClientApiCache.queryCacheConfigRisk(key);
        //去判断签名
//        configRiskSign.checkSign(one);
//        String configValue = one.getConfigValue();
        ConfigRisk configRisk = this.getOne(Wrappers.lambdaQuery(ConfigRisk.class).eq(ConfigRisk::getConfigKey, key));
        if (configRisk == null) {
            return null;
        } else {
            return configRisk.getConfigValue();
        }
    }

    public List<BigDecimal> getRiskWinAmount(String key) {
        List<ConfigRisk> list = this.list(Wrappers.lambdaQuery(ConfigRisk.class).like(ConfigRisk::getConfigKey, key));
        List<BigDecimal> gameNames = list.stream()
                .map(config -> new BigDecimal(config.getConfigValue()))
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        return gameNames;
    }

    public String password_error_times() {
        String value = getValue(password_error_times);
        if (StringUtils.isEmpty(value)) {
            value = "5";
        }
        return value;
    }

    /**
     * 查询配置
     *
     * @param configId 配置ID
     * @return 配置
     */
    public ConfigRisk selectConfigRiskById(Integer configId) {
        return tConfigRiskMapper.selectConfigRiskById(configId);
    }

    /**
     * 查询配置列表
     *
     * @param tConfigRisk 配置
     * @return 配置
     */
    public List<ConfigRisk> selectConfigRiskList(ConfigRisk tConfigRisk) {
        return tConfigRiskMapper.selectConfigRiskList(tConfigRisk);
    }

    /**
     * 新增配置
     *
     * @param tConfigRisk 配置
     * @return 结果
     */
    public Boolean insertConfigRisk(ConfigRisk tConfigRisk) {
        tConfigRisk.setCreateTime(DateUtils.getNowDate());
        //去增加签名
        configRiskSign.dealSign(tConfigRisk);
        boolean result = this.save(tConfigRisk);
        redisClientApiCache.resetCacheConfigRisk();
        return result;
    }

    /**
     * 修改配置
     *
     * @param tConfigRisk 配置
     * @return 结果
     */
    public Boolean updateConfigRisk(ConfigRisk tConfigRisk) {
        configRiskSign.dealSign(tConfigRisk);
        tConfigRisk.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(tConfigRisk);
        redisClientApiCache.resetCacheConfigRisk();
        return result;
    }

    public Boolean isAgentMoney() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(isAgentModel);
        if (configOneUrlMaybeNul == null) {
            return true;
        }
        return "0".equals(configOneUrlMaybeNul.getConfigValue()) || "2".equals(configOneUrlMaybeNul.getConfigValue());
    }

    /**
     * 批量删除配置
     *
     * @param configIds 需要删除的配置ID
     * @return 结果
     */
    public Boolean deleteConfigRiskByIds(Integer[] configIds) {
        boolean result = this.removeByIds(Arrays.asList(configIds));
        redisClientApiCache.resetCacheConfigRisk();
        return result;
    }

    public List<ConfigRisk> getConfigUrl(List<String> arr) {
        List<ConfigRisk> arrResult = CollUtil.newArrayList();

        for (String s : arr) {
            ConfigRisk one = redisClientApiCache.queryCacheConfigRisk(s);
            arrResult.add(one);
        }

        return arrResult;
    }

    public List<ConfigRisk> getConfigUrlMaybeNul(List<String> arr) {
        List<ConfigRisk> arrResult = CollUtil.newArrayList();

        for (String s : arr) {
            ConfigRisk one = redisClientApiCache.queryCacheConfigRiskMaybeNull(s);
            if (one != null) {
                arrResult.add(one);
            }

        }

        return arrResult;
    }

    public ConfigRisk getConfigOneUrlMaybeNul(String key) {
        return redisClientApiCache.queryCacheConfigRiskMaybeNull(key);
    }

    public boolean updateIsDispById(ConfigRisk tConfigRisk) {
        configRiskSign.dealSign(tConfigRisk);
        tConfigRisk.setUpdateTime(DateUtils.getNowDate());
        boolean result = tConfigRiskMapper.updateIsDispById(tConfigRisk);
        redisClientApiCache.resetCacheConfigRisk();
        return result;
    }

    public Boolean isOfficialWithdraw() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(isOfficialWithdraw);
        if (configOneUrlMaybeNul == null) {
            return false;
        }
        return "1".equals(configOneUrlMaybeNul.getConfigValue());
    }

    public String flNoticeGroup() {
        return getValue(flNoticeGroup);
    }

    public String withdrawFee() {
        ConfigRisk configOneUrlMaybeNul = getConfigOneUrlMaybeNul(withdrawFee);
        if (configOneUrlMaybeNul == null) {
            return null;
        }
        return configOneUrlMaybeNul.getConfigValue();
    }

}
