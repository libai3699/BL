package com.gp.common.mybatisplus.cache;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.constant.CommonConstant;
import com.common.core.util.RedisUtil;
import com.common.core.util.StringUtils;
import com.common.datasource.util.CecuUtil;
import com.common.mybatisplus.domain.PageDTO;
import com.common.mybatisplus.domain.PageDomain;
import com.common.mybatisplus.utils.PageUtil;
import com.common.mybatisplus.utils.TableSupportUtil;
import com.gp.common.base.constant.DelConstants;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.utils.AdminUtil;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.mybatisplus.deal.DataProcessor;
import com.gp.common.mybatisplus.entity.*;
import com.gp.common.mybatisplus.entity.Currency;
import com.gp.common.mybatisplus.manage.UserExtChangeManage;
import com.gp.common.mybatisplus.mapper.ActivityAwardMapper;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import com.gp.common.mybatisplus.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.language.bm.Languages;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.RedissonPermitExpirableSemaphore;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 活动奖励Service业务层处理
 *
 * @author axing
 * @date 2024-05-12
 */
@Service
@Slf4j
public class RedisClientApiCache {
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private PlayWheelConfigService playWheelConfigService;

    //转盘信息缓存
    @Resource
    private GameService gameService;
    @Resource
    private ChannelService channelService;
    @Resource
    private ConfigRiskService configRiskService;
    @Resource
    private ConfigAmountService configAmountService;
    @Resource
    private GamePlateService gamePlateService;
    @Resource
    private GameTypeService gameTypeService;
    @Resource
    private MybatisBotService mybatisBotService;
    @Resource
    private HomepagePopupService homepagePopupService;
    @Resource
    private CecuUtil cecuUtil;
    @Resource
    private MNacosParam mNacosParam;
    @Resource
    private CurrencyService currencyService;
    @Resource
    private BotItemService botItemService;

    public List<PlayWheelConfig> getWheelInfo() {
        String dbCode = CecuUtil.getDbCode();
        String key = StrUtil.format(RedisKey.wheelInfoKey, dbCode);
        Object o = redisUtil.get(key);
        if (o == null) {
            //获取所有转盘的选项,之后可以放在redis中
            LambdaQueryWrapper<PlayWheelConfig> q = new LambdaQueryWrapper<>();
            q.eq(PlayWheelConfig::getStatus, CommonConstant.OPEN);
            List<PlayWheelConfig> list = playWheelConfigService.list(q);

            redisUtil.set(key, list, 1, TimeUnit.HOURS);
            return list;
        } else {
            return (List<PlayWheelConfig>) o;
        }
    }

    public void resetWheelInfo() {
        List<String> dbNameList = cecuUtil.getCecuProp().getAppList();
        for (String s : dbNameList) {
            cecuUtil.cutDbByCode(s);
            String key = StrUtil.format(RedisKey.wheelInfoKey, s);
            //获取所有转盘的选项,之后可以放在redis中
            LambdaQueryWrapper<PlayWheelConfig> q = new LambdaQueryWrapper<>();
            q.eq(PlayWheelConfig::getStatus, CommonConstant.OPEN);
            List<PlayWheelConfig> list = playWheelConfigService.list(q);
            redisUtil.set(key, list, 1, TimeUnit.HOURS);
        }
        //清除切库信息,恢复默认数据库
        CecuUtil.cleanDbInfo();
    }

    /**
     * 获取所有游戏板块列表（带 Redis 缓存，5分钟 TTL）
     */
    public List<GamePlate> getAllPlateList() {
        String dbCode = CecuUtil.getDbCode();
        String key = StrUtil.format(RedisKey.gamePlateAllKey, dbCode);
        Object cached = redisUtil.get(key);
        if (cached != null) {
            return JSON.parseArray(cached.toString(), GamePlate.class);
        }
        List<GamePlate> list = gamePlateService.queryPlateByType(null);
        redisUtil.set(key, JSON.toJSONString(list), 5, TimeUnit.MINUTES);
        return list;
    }

    /**
     * 获取顶级游戏标签列表（带 Redis 缓存，5分钟 TTL）
     */
    public List<GameType> getTopTagList() {
        String dbCode = CecuUtil.getDbCode();
        String key = StrUtil.format(RedisKey.gameTopTagKey, dbCode);
        Object cached = redisUtil.get(key);
        if (cached != null) {
            return JSON.parseArray(cached.toString(), GameType.class);
        }
        List<GameType> list = gameTypeService.queryTopTag();
        redisUtil.set(key, JSON.toJSONString(list), 5, TimeUnit.MINUTES);
        return list;
    }


    public HashMap getCacheHotGame(String typeCode, String plateName, String address) {
        PageDomain pageDomain = TableSupportUtil.buildPageRequest();
        return getCacheHotGame(typeCode, plateName, address, pageDomain.getPageNum(), pageDomain.getPageSize());
    }

    public HashMap getCacheHotGame(String typeCode, String plateName, String address, Integer pageNum, Integer pageSize) {
       if (StrUtil.isEmpty(typeCode)) {
            typeCode = "0";
        }
        Long total = 0l;
        Integer size = 0;
        Integer current = 0;
        Integer pages = 0;
        Boolean hasPreviousPage = false;
        Boolean hasNextPage = false;
        String dbCode = CecuUtil.getDbCode();

        String redisKey = StrUtil.format(RedisKey.hotGameKey, dbCode, pageNum, pageSize, typeCode, plateName, address);
        Object o = redisUtil.get(redisKey);
        List<Game> list = CollUtil.newArrayList();
        if (o == null) {
            PageUtil.startPage();
            if (typeCode.equals("0")) {
                list = gameService.queryHot(address);
            } else {
                list = gameService.queryGameByTag(typeCode, plateName, address);
            }
            PageDTO<Game> gameVoPageDTO = PageUtil.buildPageDto(list, Game.class);
            total = gameVoPageDTO.getTotal();
            size = gameVoPageDTO.getSize();
            current = gameVoPageDTO.getCurrent();
            pages = gameVoPageDTO.getPages();
            hasPreviousPage = gameVoPageDTO.getHasPreviousPage();
            hasNextPage = gameVoPageDTO.getHasNextPage();
            HashMap<String, Object> map = new HashMap<>();
            map.put("total", total);
            map.put("size", size);
            map.put("current", current);
            map.put("pages", pages);
            map.put("hasPreviousPage", hasPreviousPage);
            map.put("hasNextPage", hasNextPage);
            map.put("records", list);
            dealPlateRefreshUrl(list);
            redisUtil.set(redisKey, map, 5, TimeUnit.MINUTES);
            return map;
        } else {
            return (HashMap) o;
        }
    }

    public void dealPlateRefreshUrl(List<Game> list) {
        List<String> collect = list.stream().map(Game::getPlateCode)
                .distinct()
                .collect(Collectors.toList());
        Map<String, GamePlate> mapPlate;
        List<GamePlate> gamePlates = null;
        if (CollectionUtils.isNotEmpty(collect)) {
            gamePlates = gamePlateService.queryPlateListByCode(collect);
        }
        if (gamePlates == null || gamePlates.size() == 0) {
            mapPlate = new HashMap<>();
        } else {
            mapPlate = gamePlates.stream()
                    .collect(Collectors.toMap(
                            GamePlate::getPlateCode, // 键映射函数
                            Function.identity(),      // 值映射函数
                            (existing, replacement) -> existing // 合并函数，保留第一个遇到的值
                    ));
        }
        list.forEach(e -> {
            e.setIsRefreshUrl(mapPlate.get(e.getPlateCode()) == null ? 0 : mapPlate.get(e.getPlateCode()).getIsRefreshUrl());

        });
    }


    public void resetCacheHotGame() {
        List<String> dbNameList = cecuUtil.getCecuProp().getAppList();
        for (String s : dbNameList) {
            System.out.println("库是" + s);
            String key = StrUtil.format(RedisKey.hotGameKeyStar, s);
            System.out.println("库是key" + key);
            redisUtil.deletePatternMatchingKeys(key);
            String keyPlate = StrUtil.format(RedisKey.gamePlateKeyStar, s);
            System.out.println("库是key" + keyPlate);
            redisUtil.deletePatternMatchingKeys(keyPlate);
        }

    }

    /**
     * 显式 Redis 缓存（5min TTL）— 替代原 @Cacheable("queryHomePopupType")
     * key: {dbCode}:ft:cache:popup:{lanKey}:{type}
     */
    public List<HomepagePopup> queryHomePopupType(String dbCode, String lanKey, Integer type) {
        String cacheKey = StrUtil.format("{}:ft:cache:popup:{}:{}", dbCode, lanKey, type);
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            return JSON.parseArray(cached.toString(), HomepagePopup.class);
        }

        LambdaQueryWrapper<HomepagePopup> q = new LambdaQueryWrapper<>();
        q.eq(HomepagePopup::getHasDel, DelConstants.noDel);
        q.eq(HomepagePopup::getStatus, CommonConstant.OPEN);
        q.eq(HomepagePopup::getType, type);
        String language = LocaleContextHolder.getLocale().toString();
        q.eq(HomepagePopup::getLanKey, language);
        q.orderByAsc(HomepagePopup::getSort);
        List<HomepagePopup> homepagePopups = homepagePopupService.list(q);
        if (homepagePopups == null || homepagePopups.isEmpty()) {
            q.clear();
            q.eq(HomepagePopup::getHasDel, DelConstants.noDel);
            q.eq(HomepagePopup::getStatus, CommonConstant.OPEN);
            q.eq(HomepagePopup::getType, type);
            q.eq(HomepagePopup::getLanKey, Locale.US.toString());
            q.orderByAsc(HomepagePopup::getSort);
            homepagePopups = homepagePopupService.list(q);
        }
        homepagePopups.forEach(e -> {
            e.setTitleImgUrl(StringUtils.isEmpty(e.getTitleImgUrl()) ? "" : AdminUtil.perfectAdmin(mNacosParam.getFileAdmin(), e.getTitleImgUrl()));
            e.setEventsImgUrl(StringUtils.isEmpty(e.getEventsImgUrl()) ? "" : AdminUtil.perfectAdmin(mNacosParam.getFileAdmin(), e.getEventsImgUrl()));
        });
        redisUtil.set(cacheKey, JSON.toJSONString(homepagePopups), 5, TimeUnit.MINUTES);
        return homepagePopups;
    }

    public Channel queryChannelById(Long channelId) {
        Channel channel = new Channel();
        channel.setId(0l);
        channel.setChannelName("");
        channel.setChannelCode("");
        if (channelId == 0) {
            return channel;
        }
        String dbCode = CecuUtil.getDbCode();
        String redisKey = StrUtil.format(RedisKey.channelKey, dbCode, channelId);
        Object o = redisUtil.get(redisKey);
        if (o == null) {
            //获取所有转盘的选项,之后可以放在redis中
            Channel channel2 = channelService.getById(channelId);
            if (channel2 == null) {
                redisUtil.set(redisKey, channel, 1, TimeUnit.HOURS);
                return channel;
            } else {
                redisUtil.set(redisKey, channel2, 1, TimeUnit.HOURS);
                return channel2;
            }
        } else {
            return (Channel) o;
        }
    }

    public Channel queryCacheSet(Long channelId) {
        String dbCode = CecuUtil.getDbCode();
        String redisKey = StrUtil.format(RedisKey.channelKey, dbCode, channelId);
        if(redisUtil.hasKey(redisKey)) {
            return (Channel) redisUtil.get(redisKey);
        }
        //获取所有转盘的选项,之后可以放在redis中
        Channel channel2 = channelService.getById(channelId);
        if (channel2 == null) {
            return null;
        } else {
            redisUtil.set(redisKey, channel2, 1, TimeUnit.HOURS);
            return channel2;
        }
}

/**
 * 把这些厂商资料放到缓存中
 *
 * @param plateCode
 * @return
 */
public GamePlate queryPlate(String plateCode) {
    String dbCode = CecuUtil.getDbCode();
    String redisKey = StrUtil.format(RedisKey.gamePlateKey, dbCode, plateCode);
    Object o = redisUtil.get(redisKey);
    if (o == null) {
        GamePlate plateByCode = gamePlateService.getPlateByCode(plateCode);
        if (plateByCode == null) {
            redisUtil.set(redisKey, GAME_NULL_MARKER, 5, TimeUnit.MINUTES);
            return null;
        }
        redisUtil.set(redisKey, plateByCode, 5, TimeUnit.MINUTES);
        return plateByCode;
    } else if (GAME_NULL_MARKER.equals(o)) {
        return null;
    } else {
        return (GamePlate) o;
    }
}

/**
 * 根据厂商编码和游戏编码查询游戏信息（带缓存）
 */
private static final String GAME_NULL_MARKER = "NULL";

public Game queryGame(String plateCode, String gameCode) {
    String dbCode = CecuUtil.getDbCode();
    // gameCode为空时用占位符，避免缓存key冲突
    String cacheGameCode = StrUtil.isBlank(gameCode) ? "default" : gameCode;
    String redisKey = StrUtil.format(RedisKey.gameKey, dbCode, plateCode, cacheGameCode);
    Object o = redisUtil.get(redisKey);
    if (o == null) {
        Game game = gameService.getOne(Wrappers.lambdaQuery(Game.class)
                .eq(Game::getPlateCode, plateCode)
                .eq(StrUtil.isNotBlank(gameCode), Game::getGameCode, gameCode));
        if (game == null) {
            // 缓存空值防穿透，短TTL
            redisUtil.set(redisKey, GAME_NULL_MARKER, 5, TimeUnit.MINUTES);
            return null;
        }
        redisUtil.set(redisKey, game, 5, TimeUnit.MINUTES);
        return game;
    } else if (GAME_NULL_MARKER.equals(o)) {
        return null;
    } else {
        return (Game) o;
    }
}

/**
 * 根据厂商编码 + gameCode + gameCode2 查询游戏（带缓存），适用于 JDB 等双编码厂商
 */
public Game queryGameWithCode2(String plateCode, String gameCode, String gameCode2) {
    if (StrUtil.isBlank(gameCode) || StrUtil.isBlank(gameCode2)) {
        return null;
    }
    String dbCode = CecuUtil.getDbCode();
    String redisKey = StrUtil.format(RedisKey.gameKey, dbCode, plateCode, gameCode + ":" + gameCode2);
    Object o = redisUtil.get(redisKey);
    if (o == null) {
        Game game = gameService.getOne(Wrappers.lambdaQuery(Game.class)
                .eq(Game::getPlateCode, plateCode)
                .eq(Game::getGameCode, gameCode)
                .eq(Game::getGameCode2, gameCode2));
        if (game == null) {
            redisUtil.set(redisKey, GAME_NULL_MARKER, 5, TimeUnit.MINUTES);
            return null;
        }
        redisUtil.set(redisKey, game, 5, TimeUnit.MINUTES);
        return game;
    } else if (GAME_NULL_MARKER.equals(o)) {
        return null;
    } else {
        return (Game) o;
    }
}

/**
 * 根据厂商编码 + gameCode2 查询游戏（带缓存），适用于 PG 等用 gameCode2 标识游戏的厂商
 */
public Game queryGameByCode2(String plateCode, String gameCode2) {
    if (StrUtil.isBlank(gameCode2)) {
        return null;
    }
    String dbCode = CecuUtil.getDbCode();
    String redisKey = StrUtil.format(RedisKey.gameKey, dbCode, plateCode, "c2:" + gameCode2);
    Object o = redisUtil.get(redisKey);
    if (o == null) {
        Game game = gameService.getOne(Wrappers.lambdaQuery(Game.class)
                .eq(Game::getPlateCode, plateCode)
                .eq(Game::getGameCode2, gameCode2));
        if (game == null) {
            redisUtil.set(redisKey, GAME_NULL_MARKER, 5, TimeUnit.MINUTES);
            return null;
        }
        redisUtil.set(redisKey, game, 5, TimeUnit.MINUTES);
        return game;
    } else if (GAME_NULL_MARKER.equals(o)) {
        return null;
    } else {
        return (Game) o;
    }
}

public void resetGame(String plateCode, String gameCode) {
    List<String> dbNameList = cecuUtil.getCecuProp().getAppList();
    for (String s : dbNameList) {
        String key = StrUtil.format(RedisKey.gameKey, s, plateCode, gameCode);
        redisUtil.del(key);
    }
}

public void resetGamePlate() {
    List<String> dbNameList = cecuUtil.getCecuProp().getAppList();
    for (String dbCode : dbNameList) {
        cecuUtil.cutDbByCode(dbCode);
        List<GamePlate> plates = gamePlateService.list();
        if (plates != null) {
            for (GamePlate plate : plates) {
                String key = StrUtil.format(RedisKey.gamePlateKey, dbCode, plate.getPlateCode());
                redisUtil.del(key);
            }
        }
    }
    CecuUtil.cleanDbInfo();
}

public void resetGameCache() {
    List<String> dbNameList = cecuUtil.getCecuProp().getAppList();
    for (String dbCode : dbNameList) {
        // 游标扫描 {dbCode}:ft:gameCache:* 批量删除
        String pattern = StrUtil.format(RedisKey.gameCachePattern, dbCode);
        redisUtil.deletePatternMatchingKeys(pattern);
    }
    // 清理总表游戏缓存
    String pattern = StrUtil.format(RedisKey.gameCachePattern, "null");
    redisUtil.deletePatternMatchingKeys(pattern);
}

/**
 * ConfigRisk
 *
 * @param item
 * @return
 */

public ConfigRisk queryCacheConfigRisk(String item) {
    String dbCode = CecuUtil.getDbCode();
    String key = StrUtil.format(RedisKey.configRiskKey, dbCode);

    Object o = redisUtil.hget(key, item);
    if (o == null) {
        //获取所有转盘的选项,之后可以放在redis中
        Map<String, ConfigRisk> configRiskAll = configRiskService.getConfigRiskKey();
        ConfigRisk one = configRiskAll.get(item);

        if (one == null) {
            Assert.isFalse(true, MessagesUtils.get("bot.config.error"));
        }
        redisUtil.hmset(key, configRiskAll);
        redisUtil.expire(key, 1, TimeUnit.HOURS);
        return one;
    } else {
        return (ConfigRisk) o;
    }
}

/**
 * ConfigRisk
 *
 * @param item
 * @return
 */

public ConfigRisk queryCacheConfigRiskMaybeNull(String item) {
    String dbCode = CecuUtil.getDbCode();
    String key = StrUtil.format(RedisKey.configRiskKey, dbCode);

    Object o = redisUtil.hget(key, item);
    if (o == null) {
        //获取所有转盘的选项,之后可以放在redis中
        Map<String, ConfigRisk> configRiskAll = configRiskService.getConfigRiskKey();
        ConfigRisk one = configRiskAll.get(item);
        if (one == null) {
            return null;
        }
        redisUtil.hmset(key, configRiskAll);
        redisUtil.expire(key, 1, TimeUnit.HOURS);
        return one;
    } else {
        return (ConfigRisk) o;
    }
}

public void resetCacheConfigRisk() {
    List<String> dbNameList = cecuUtil.getCecuProp().getAppList();
    for (String s : dbNameList) {
        String key = StrUtil.format(RedisKey.configRiskKey, s);
        redisUtil.del(key);
    }

}

public ConfigAmount queryConfigAmount(String item) {
    String dbCode = CecuUtil.getDbCode();
    String key = StrUtil.format(RedisKey.configAmountKey, dbCode);
    Object o = redisUtil.hget(key, item);
    if (o == null) {
        Map<String, ConfigAmount> configAmountAll = configAmountService.getConfigAmountKey();
        ConfigAmount one = configAmountAll.get(item);
        if (one == null) {
            Assert.isFalse(true, MessagesUtils.get("bot.config.error"));
        }
        redisUtil.hmset(key, configAmountAll);
        redisUtil.expire(key, 1, TimeUnit.HOURS);
        return one;
    } else {
        return (ConfigAmount) o;
    }
}

public ConfigAmount queryConfigAmountMaybeNull(String item) {
    String dbCode = CecuUtil.getDbCode();
    String key = StrUtil.format(RedisKey.configAmountKey, dbCode);
    Object o = redisUtil.hget(key, item);
    if (o == null) {
        Map<String, ConfigAmount> configAmountAll = configAmountService.getConfigAmountKey();
        ConfigAmount one = configAmountAll.get(item);
        if (one == null) {
            return null;
        }
        redisUtil.hmset(key, configAmountAll);
        redisUtil.expire(key, 1, TimeUnit.HOURS);
        return one;
    } else {
        return (ConfigAmount) o;
    }
}

public void resetConfigAmount() {
    List<String> dbNameList = cecuUtil.getCecuProp().getAppList();
    for (String s : dbNameList) {
        String key = StrUtil.format(RedisKey.configAmountKey, s);
        redisUtil.del(key);
    }
}

public void resetMybatisConfigAmount() {
    mybatisBotService.changeMerchantConfig();
}

public Currency queryCacheCurrencyById(Integer currencyId) {
    String dbCode = CecuUtil.getDbCode();
    String redisKey = StrUtil.format(RedisKey.currency, dbCode, currencyId);
    Object o = redisUtil.get(redisKey);
    if (o == null) {
        Currency currencyById = currencyService.getById(currencyId);
        if (currencyById == null) {
            log.info("币种不存在不存在{}", currencyId);
            Assert.isFalse(true, MessagesUtils.get("bot.currency.BZBCZ"));
        }
        redisUtil.set(redisKey, currencyById, 1, TimeUnit.HOURS);
        return currencyById;
    } else {
        return (Currency) o;
    }
}


}
