package com.gp.common.base.constant;

/**
 * @author Administrator
 */
public interface RedisKey {

    /**
     * redis 地址 ssl地址
     */
    public String redissAddr = "rediss://{}:{}";

    /**
     * redis 地址
     */
    public String redisAddr = "redis://{}:{}";

    /**
     * 限流key
     */
    public String limitFormat = "ft:back:limit:{}:{}";
    public String frontLimitFormat = "ft:front:limit:{}:{}";
    public String frontLimitFormatParam = "ft:front:limit:{}:{}:{}";

    public static final String password_user_fail_count = "{}:ft:password:user:fail:count:{}";

    //短信
    public static final String sms_user_send_count = "{}:ft:sms:user:send:count:{}";
    public static final String mail_user_send_count = "{}:ft:mail:user:send:count:{}";

    public static final String WGID = "{}:ft:wg:black:id:{}";
    public static final String WGIP = "{}:ft:wg:black:ip:{}";

    public static final String USER_SIGN_KEY = ":ft:user:sign:";

    public static final String USER_RECHARGE = "{}:ft:user:recharge:{}";
    public static final String USER_RECHARGE_TODAY = "{}:ft:user:recharge:today:{}";
    public static final String USER_RECHARGE_TODAY_24 = "{}:ft:user:recharge:today:24:{}";
    public static final String USER_RECHARGE_WEEK = "{}:ft:user:recharge:week:{}";
    public static final String USER_RECHARGE_MONTH = "{}:ft:user:recharge:month:{}";
    public static final String USER_RECHARGE_FIRST = "{}:ft:user:recharge:first:{}";
    public static final String USER_RECHARGE_SECOND = "{}:ft:user:recharge:second:{}";
    public static final String USER_RECHARGE_THREE = "{}:ft:user:recharge:three:{}";

    public static String today_win = "{}:ft:user:today:win:{}";
    public static String risk_send_today = "{}:ft:user:send:today:{}:{}";

    public static String wheelInfoKey = "{}:ft:cache:WheelInfoKey";
    public static String hotGameKey = "{}:ft:gameCache:hotGame:{}:{}:{}:{}:{}";
    public static String hotGameKeyStar = "{}:ft:gameCache:hotGame:*";
    public static String channelKey = "{}:ft:cache:channelId:{}";
    public static String gamePlateKey = "{}:ft:gameCache:plate:{}";
    public static String gamePlateKeyStar = "{}:ft:gameCache:plate:*";
    public static String gameKey = "{}:ft:gameCache:game:{}:{}";
    public static String gamePlateAllKey = "{}:ft:gameCache:plateAll";
    public static String gameTopTagKey = "{}:ft:gameCache:topTag";
    /**
     * 游戏缓存统一前缀，用于游标扫描批量清理
     */
    public static String gameCachePattern = "{}:ft:gameCache:*";

    public static final String mPayRechargeNum = "{}:ft:mPay:recharge:num:{}";
    public static final String UPayRechargeNum = "{}:ft:uPay:recharge:num:{}";
    public static final String Pay1818RechargeNum = "{}:ft:uPay:pay1818:num:{}";
    public static final String HCPayRechargeNum = "{}:ft:HCuPay:recharge:num:{}";
    /**
     * param1 游戏平台
     * param2 session
     */
    public static final String gameUserPlateSesson = "ft:game:user:{}:{}";
    public static final String gameUserTokenPlateSesson = "ft:game:userToken:{}:{}";

    public static final String configRiskKey = "{}:ft:risk:config";
    public static final String configAmountKey = "{}:ft:amount:config";

    public static String registerNumKeyIp = "{}:ft:cache:register:num:{}";
    public static String loginErrorNumKey = "{}:ft:cache:error:num:{}";
    public static String loginWebErrorNumKey = "{}:ft:login:web:error:num:{}";
    public static String resetPasswordNumError = "{}:ft:cache:resetPassword:error:num:{}";

    public static String registerNumKeyError = "{}:ft:cache:register:error:num:{}";
    public static String gameUrl = "ft:gameUrl:{}";
    public static String payUrl = "ft:pay:{}:{}";

    public static final String lawRechargeNum = "{}:ft:law:recharge:num:{}";

    /**
     * 充值5分钟内请求计数（Redis计数器替代DB count查询）
     */
    public static final String payCount5Min = "{}:ft:pay:count5min:{}";
    /**
     * 支付通道缓存
     */
    public static final String payChannelCacheById = "{}:ft:cache:payChannelCacheById:{}";
    /**
     * 支付通道缓存
     */
    public static final String payChannelCacheByCode = "{}:ft:cache:payChannelCacheByCode:{}";
    /**
     * 支付商户缓存
     */
    public static final String merchantPayCacheById = "{}:ft:cache:merchantPayCacheById:{}";
    /**
     * 支付商户缓存
     */
    public static final String merchantPayCacheByCode = "{}:ft:cache:merchantPayCacheByCode:{}";
    /**
     * 支付类型缓存
     */
    public static final String payTypeCacheById = "{}:ft:cache:payTypeById:{}";
    /**
     * 支付类型缓存
     */
    public static final String payTypeCacheByCode = "{}:ft:cache:payTypeByCode:{}";
    /**
     * 充值可用支付类型列表缓存 param1=dbCode（仅状态过滤，未做用户等级过滤）
     */
    public static final String payTypeRechargeList = "{}:ft:cache:payTypeRechargeList";
    /**
     * 支付类型全量Map缓存（id→entity）
     */
    public static final String payTypeMapById = "{}:ft:cache:payTypeMapById";
    /**
     * 支付类型全量Map缓存（code→entity）
     */
    public static final String payTypeMapByCode = "{}:ft:cache:payTypeMapByCode";
    /**
     * 支付通道快捷金额缓存 param1=dbCode param2=typeCode param3=vip
     */
    public static final String payChannelMoneyQuick = "{}:ft:cache:payChannelMoneyQuick:{}:{}";
    /**
     * 支付通道快捷金额缓存失效 pattern param1=dbCode
     */
    public static final String payChannelMoneyQuickPattern = "{}:ft:cache:payChannelMoneyQuick:*";
    /**
     * 支付通道全量Map缓存（id→entity）
     */
    public static final String payChannelMapById = "{}:ft:cache:payChannelMapById";
    /**
     * 支付通道全量Map缓存（code→entity）
     */
    public static final String payChannelMapByCode = "{}:ft:cache:payChannelMapByCode";
    /**
     * 支付商户全量Map缓存（id→entity）
     */
    public static final String merchantPayMapById = "{}:ft:cache:merchantPayMapById";
    /**
     * 支付商户全量Map缓存（code→entity）
     */
    public static final String merchantPayMapByCode = "{}:ft:cache:merchantPayMapByCode";

    public static final String userBlackIp = "{}:ft:user:black:ip";
    //校验提现什么的
    public static final String checkMessage = "{}:ft:back:checkMessage";

    /**
     * 所有商户信息
     */
    public static final String CONTROL_MERCHANTS = "CONTROL_MERCHANTS_CODE";

    /**
     * 商户积分余额锁
     */
    public static final String MERCHANT_BALANCE = "merchant:balance:{}:{}";
    /**
     * 商户配置缓存
     */
    public static final String MERCHANT_CONFIG = "{}:merchant:config";
    public static final String MERCHANT_CONFIG_All = "*:merchant:config";

    public static final String USER_BET = "{}:ft:user:bet:{}:{}";
    public static final String USER_BET_TODAY = "{}:ft:user:bet:today:{}:{}";
    public static final String USER_BET_WEEK = "{}:ft:user:bet:week:{}:{}";
    public static final String USER_BET_MONTH = "{}:ft:user:bet:month:{}:{}";
    public static final String USER_BET_FIRST = "{}:ft:user:bet:first:{}:{}";
    public static final String USER_BET_SECOND = "{}:ft:user:bet:second:{}:{}";
    public static final String USER_BET_THREE = "{}:ft:user:bet:three:{}:{}";

    public static String userBetKeyStar = "{}:ft:user:bet:*";

    public static String userRechargeKeyStar = "{}:ft:user:recharge:*";
    public static String recentGameKey = "{}:ft:recent:gameId:{}";

    /**
     * DB 彩票结算缓存
     */
    public static String DBLotterySettleKey = "{}:ft:db:lottery:settle";

    /**
     * 中奖排行榜缓存
     */

    public static String winRankKey = "{}:ft:rank:win3:rank";
    /**
     * 投注排行user
     */
    public static String betRankUserKey = "{}:ft:game:user:betRank:user";
    /**
     * 投注排行榜缓存
     */
    public static String betRankKey = "{}:ft:game:user:betRank:user:rank:{}";
    /**
     * 游戏在线人数
     */
    public static String onlineKey = "{}:ft:game:online:{}";
    /**
     * 中奖的list
     */
    public static String winReportListKey = "{}:ft:game:win:report";

    public static final String currency = "{}:wallet:currency:{}";

    public static final String RED_PACKAGE_KEY = "ft:redpackage:";

    public static final String RED_PACKAGE_CONSUME_KEY = "ft:redpackage:consume:";

    public static final String newUserKey = "{}:ft:newUserKey:{}";

    public static final String transfer = "{}:ft:transfer";

    public static final String captcha_user_fail_count = "{}:ft:captcha:user:fail:count:{}";

    public static String shareholder = "{}:ft:cache:shareholder:{}";
    public static String redPacketRainKey = "{}:ft:cache:redPacketRainKey:{}:{}";
    /**
     * 红包雨活动已领取总金额 param1=dbCode param2=redRainId
     */
    public static String redPacketRainTotalClaimedKey = "{}:ft:cache:redPacketRainTotalClaimed:{}";
    /**
     * stkc最新订单号缓存
     */
    String stkcOrderIdKey = "game:stkc:lastOrderId";

    /**
     * stkc修改订单最新订单号KEY
     */
    String stkcOrderIdUpdateKey = "game:stkc:lastOrderId:update";

    /**
     * 导出压缩包KEY 1-产品code 2-用户id 3-导出类型
     */
    String exportZipKey = "{}:export:zip:key:{}:{}";

    /**
     * 图片域名的key 1-产品code 2-省份
     */
    String imageDomainKey = "{}:image:domain:key:{}";

    /**
     * 网页域名的key 1-产品code 2-省份
     */
    String webDomainKey = "{}:web:domain:key:{}";

    /**
     * 同IP登录排行缓存key 1-产品code
     */
    String loginIpRank = "{}:ft:risk:login:ipRank";
    /**
     * 同设备登录排行缓存key 1-产品code
     */
    String loginDeviceRank = "{}:ft:risk:login:deviceRank";

}
