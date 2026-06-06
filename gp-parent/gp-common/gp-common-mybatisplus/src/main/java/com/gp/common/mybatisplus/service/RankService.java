package com.gp.common.mybatisplus.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.core.util.RedisUtil;
import com.common.core.util.StringUtils;
import com.common.datasource.util.CecuUtil;
import com.github.pagehelper.PageHelper;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.deal.DataProcessor;
import com.gp.common.mybatisplus.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class RankService {
    //存入时间 2分钟
    public static final int twoMinutes = 1 * 60;
    public static final int winCount = 15;

    public static final int winTime = 2;
    public static final String betRankSectionC = "10-200";
    ;
    public static final String betSectionC = "1-200,300-500,500-1000";
    public static final String betWinSectionC = "0.3-2";
    public static final Integer betCount = 500;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private UserService userService;
    @Resource
    private GameService gameService;
    @Resource
    private ConfigRiskService configRiskService;


    public List<WinRank> getRankList() {
        //redis中没有数据，则去数据库中查询
        //先随机30个用户
        //想法是当这个数据到了的话 先随机30个用户 然后同时在另外在随机30个用户
        // 这里从新排序展示 前10个 如果一开始有数据的话就正常返回然后通过定时去 筛选前十的用户每半个小时更新一次 到时候通过定时任务去搞 //这里重要的是游戏和用户的关系
        PageHelper.clearPage(); // 清除 ThreadLocal 中的分页信息
        List<TUser> userList = userService.randomUser(winCount);
        //转成vo
        //排序
        //随机抽取 排名前100的游戏 取一下 到时候主推的话在从游戏中去去
        //看看是否有推荐的游戏
        List<Game> gameList = CollUtil.newArrayList();
        ConfigRisk configRecommendedGame = configRiskService.getConfigOneUrlMaybeNul(ConfigRiskService.recommendedGame);
        if (configRecommendedGame != null) {
            String configValue = configRecommendedGame.getConfigValue();
            List<String> gameIds = Arrays.asList(configValue.split(","));
            LambdaQueryWrapper<Game> q = new LambdaQueryWrapper<Game>().in(Game::getId, gameIds).orderByAsc(Game::getSort);
            gameList = gameService.list(q);
        }
        if (gameList.size() < winCount) {
            List<Game> gameRandom = gameService.randomGame(winCount - (gameList.size()));
            gameList.addAll(gameRandom);
        }
        gameList.forEach(DataProcessor::process);
        //这里的话还需要生成30个 投注量数字 800 到3000的数字 然后在去 生成对应的 反奖数字 返浆倍率是3到5倍 生成30组
        //生成数字
        String betSection = betSectionC;
        ConfigRisk configBetSection = configRiskService.getConfigOneUrlMaybeNul(ConfigRiskService.winRankBetSection);
        if (configBetSection != null) {
            betSection = configBetSection.getConfigValue();
        }
        String betWinSection = betWinSectionC;
        ConfigRisk configBetWin = configRiskService.getConfigOneUrlMaybeNul(ConfigRiskService.winRankBetWinRate);
        if (configBetWin != null) {
            betWinSection = configBetWin.getConfigValue();
        }
        List<BigDecimal> weights = CollUtil.newArrayList(new BigDecimal("1"));
        String[] split = betSection.split(",");
        //大于1的权重
        int count = split.length - 1;
        for (int j = 0; j < count; j++) {
            weights.add(0, new BigDecimal("9"));
        }
        List<Map<String, BigDecimal>> maps = generateRandomWind(winCount, betSection, betWinSection, weights);
        //随机时间
        List<Date> dateList = randomTime(winCount, winTime);

        Random random = new Random();
        //先组册一个WinRankVo
        List<WinRank> winRankList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            TUser userVo = userList.get(i);
            WinRank winRank = new WinRank();
            winRank.setUser(userVo);
            winRank.setGame(gameList.get(random.nextInt(gameList.size())));
            //然后方打码量和反将
            winRank.setBetAmount(maps.get(i).get("betAmount"));
            winRank.setWinAmount(maps.get(i).get("winAmount"));
            winRank.setCreateTime(dateList.get(i));
            winRankList.add(winRank);
        }

        return winRankList;
    }


    private static List<Map<String, BigDecimal>> generateRandomWind(int totalCount, String betSection, String betWinSection, List<BigDecimal> weights) {
        List<Map<String, BigDecimal>> result = new ArrayList<>();

        // 计算总权重
        BigDecimal totalWeight = BigDecimal.ZERO;
        for (BigDecimal weight : weights) {
            totalWeight = totalWeight.add(weight);
        }

        // 计算每个区间的抽样数量
        int[] sampleCounts = new int[weights.size()];
        Integer count = 0;
        for (int i = 0; i < weights.size(); i++) {
            if (i == weights.size() - 1) {
                sampleCounts[i] = totalCount - count;
            } else {
                Double floor = null;
                if (totalWeight.compareTo(BigDecimal.ZERO) == 0) {
                    floor = 0.0;
                } else {
                    floor = weights.get(i).divide(totalWeight, 2, RoundingMode.DOWN)
                            .multiply(new BigDecimal(totalCount)).doubleValue();
                }
                // 计算样本数量，避免非整数值导致的问题
                sampleCounts[i] = floor.intValue();
                count += sampleCounts[i];
            }
        }
        // 填充 items 列表
        String[] sections = betSection.split(","); // 分割字符串得到区间
        for (int i = 0; i < sections.length; i++) {
            String section = sections[i].trim();
            for (int j = 0; j < sampleCounts[i]; j++) {
                Map<String, BigDecimal> map = generateMap(section, betWinSection);
                result.add(map);
            }
        }

        return result;
    }

    private static Map<String, BigDecimal> generateMap(String betSection, String betWinSection) {
        Map<String, BigDecimal> map = new HashMap<>();
        Random random = new Random();
        // 解析betSection
        String[] bounds = betSection.split("-");
        if (bounds.length == 2) {
            try {
                BigDecimal lowerBound = new BigDecimal(bounds[0].trim()).setScale(2, RoundingMode.DOWN);
                BigDecimal upperBound = new BigDecimal(bounds[1].trim()).setScale(2, RoundingMode.DOWN);
                String[] win = betWinSection.split("-");
                double winMin = Double.parseDouble(win[0]);
                double winMax = Double.parseDouble(win[1]);
                // 生成一个在这个范围内的随机值
                BigDecimal betAmount = lowerBound.add(BigDecimal.valueOf(Math.random()).multiply(upperBound.subtract(lowerBound)));
                double betAmountDouble = betAmount.doubleValue();
                double winAmount = betAmountDouble * (winMin + (winMax - winMin) * random.nextDouble()); // 生成赢得金额（3到5倍的betAmount）
                map.put("betAmount", betAmount.setScale(2, RoundingMode.DOWN)); // 将随机生成的值放入Map
                map.put("winAmount", new BigDecimal(winAmount).setScale(2, RoundingMode.DOWN)); // 将随机生成的值放入Map
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format for range: " + betSection);
            }
        }

        return map;
    }

//    public void addWinRank(OrderTerm orderTerm, TUser user, String dbCode) {
//        String key = StringUtils.format(RedisKey.winRankKey, dbCode);
//        Object o = redisUtil.get(key);
//        if (o != null) {
//            //往里塞入数据
//            long expire = redisUtil.getExpire(key);
//            if (expire <= 0) {
//                expire = twoMinutes;
//            }
//            List<WinRank> arr = (List<WinRank>) o;
//            arr.add(0, dealWinRank(orderTerm, user));
//            //然后存回去
//            redisUtil.set(key, arr, expire);
//
//        } else {
//            //不存在的话 重新存一个
//            List<WinRank> rankList = getRankList();
//            rankList.add(0, dealWinRank(orderTerm, user));
//            redisUtil.set(key, rankList, twoMinutes);
//        }
//
//
//    }
    public void addWinRank(OrderTerm orderTerm, TUser user, String dbCode) {

        String key = StringUtils.format(RedisKey.winRankKey, dbCode);

        WinRank winRank = dealWinRank(orderTerm, user);

        // 头插
        redisUtil.rSet(key, winRank);

        // 只保留前100条
        redisUtil.lTrim(key, 0, 50);

        redisUtil.expire(key, twoMinutes, TimeUnit.MINUTES);
    }


    private WinRank dealWinRank(OrderTerm orderTerm, TUser user) {
        Game gameByCodeAndPlateCode = gameService.getGameByCodeAndPlateCode(orderTerm.getGameCode(), orderTerm.getPlateCode());
        if (gameByCodeAndPlateCode == null) {
            return null;
        }
        WinRank winRank = new WinRank();
        winRank.setUser(user);
        winRank.setGame(gameByCodeAndPlateCode);
        winRank.setBetAmount(orderTerm.getBetAmount());
        winRank.setWinAmount(orderTerm.getWin());
        winRank.setCreateTime(orderTerm.getCreateTime()==null? new Date():orderTerm.getCreateTime());
        return winRank;
    }


    private static List<Date> randomTime(Integer count, Integer time) {
        Random random = new Random();

        // 当前时间
        long currentTimeMillis = System.currentTimeMillis();

        // 将指定的时间转换为毫秒
        long timeInMillis = time * 60 * 1000; // time 是分钟，转换为毫秒
        List<Date> dateList = CollUtil.newArrayList();

        // 随机生成指定数量的时间
        for (int i = 0; i < count; i++) {
            // 生成一个在当前时间的前后 time 分钟内的随机时间戳
            long randomTimeMillis = currentTimeMillis - (long) (random.nextDouble() * 2 * timeInMillis) - timeInMillis;

            // 将随机时间戳转换为日期格式
            Date randomDate = new Date(randomTimeMillis);
            dateList.add(randomDate);
        }
        return dateList;
    }


    private static List<Map<String, BigDecimal>> generateRandomWind(int count, String betSection, String betWinSection) {
        List<Map<String, BigDecimal>> betList = new ArrayList<>();
        Random random = new Random();
        String[] split = betSection.split("-");
        double min = Double.parseDouble(split[0]);
        double max = Double.parseDouble(split[1]);
        String[] win = betWinSection.split("-");
        double winMin = Double.parseDouble(win[0]);
        double winMax = Double.parseDouble(win[1]);
        for (int i = 0; i < count; i++) {
            double betAmount = min + (max - min) * random.nextDouble();
            ; // 生成800到3000的随机数
            double winAmount = betAmount * (winMin + (winMax - winMin) * random.nextDouble()); // 生成赢得金额（3到5倍的betAmount）
            Map<String, BigDecimal> betMap = new HashMap<>();
            betMap.put("betAmount", new BigDecimal(betAmount));
            betMap.put("winAmount", new BigDecimal(winAmount));
            betList.add(betMap);
        }

        return betList;
    }

    //    @ApiModelProperty("1 日排行 2 周排行 3 月排行")
    public List<BetRank> getBetRank(@NotNull(message = "type不能为空") Integer type) {
        //redis中没有数据，则去数据库中查询
        //先随机30个用户
        //想法是当这个数据到了的话 先随机30个用户 然后同时在另外在随机30个用户
        // 这里从新排序展示 前10个 如果一开始有数据的话就正常返回然后通过定时去 筛选前十的用户每半个小时更新一次 到时候通过定时任务去搞 //这里重要的是游戏和用户的关系

        //能走到这里说明是之前存在redis中是没有的
        //如果说 每个月和每周都没有的话

        String dbCode = CecuUtil.getDbCode();

        //查一下天
        //  //这里有个弊端是 每天随机的人都不一样导致 每周和每月的最终不一样 要不然 直接抽出500个人存一个月 每一个月都去随机这些人 一个月一轮换
        String userKey = StringUtils.format(RedisKey.betRankUserKey, dbCode);
        Object oUser = redisUtil.get(userKey);
        List<TUser> userList = null;
        if (oUser == null) {
            //随机抽一定人
            PageHelper.clearPage(); // 清除 ThreadLocal 中的分页信息
            userList = userService.randomUser(betCount);
            //存一个月
            redisUtil.set(userKey, userList, DateUtils.getMonthEndTime()); // 保存24小时
        } else {
            userList = (List<TUser>) oUser;
        }
        if (type == 1) {
            String key = StringUtils.format(RedisKey.betRankKey, dbCode, type);
            Object o = redisUtil.get(key);
            if (o != null) {
                //直接返回
                return (List<BetRank>) o;
            } else {
                return getBetRanks(userList);
            }
            //正常走
        } else {
            //去查一下每天的数据
            String keyToday = StringUtils.format(RedisKey.betRankKey, dbCode, 1);
            Object oType = redisUtil.get(keyToday);
            if (oType != null) {
                //直接返回
                return (List<BetRank>) oType;
            } else {
                return getBetRanks(userList);
            }

        }

    }

    private List<BetRank> getBetRanks(List<TUser> userList) {
        //否则就去生成一个每天的数据
        //生成数字 每个小时的投注量
        String betRankSection = betRankSectionC;
        ConfigRisk configBetRankSection = configRiskService.getConfigOneUrlMaybeNul(ConfigRiskService.betRankHourSection);
        if (configBetRankSection != null) {
            betRankSection = configBetRankSection.getConfigValue();
        }
        Long hour = DateUtils.getTodayPassedHour();
        //生成100个投注量
        List<BigDecimal> maps = generateRandomBets(betCount, betRankSection, hour.intValue());
        //组装成
        //生成100个用户的投注量
        //看看今天过了多少小时
        //看看这个每周的用每天的累计 没个月也用每天的统计 等于说 把每一天的都取存到redis中 保留 31到月底的数据 然后每半个小时增加一次
        //再去过滤统计 刷新的话交给定时任务取做
        List<BetRank> betRankList = new ArrayList<>();
        for (int i = 0; i < userList.size(); i++) {
            TUser userVo = userList.get(i);
            BetRank betRank = new BetRank();
            betRank.setUser(userVo);
            //然后方打码量和反将
            betRank.setBetAmount(maps.get(i));
            betRankList.add(betRank);
        }
        //先造加数据
        return betRankList;
    }

    public static void main(String[] args) {
        List<BigDecimal> arr = generateRandomBets(RankService.betCount, betRankSectionC, 22);
        arr.sort(Comparator.reverseOrder());

        System.out.println(arr);
    }


    public static List<BigDecimal> generateRandomBets(int count, String betSection, int hour) {
        List<BigDecimal> betList = new ArrayList<>();
        Random random = new Random();
        String[] split = betSection.split("-");
        double min = Double.parseDouble(split[0]);
        double max = Double.parseDouble(split[1]);
        for (int i = 0; i < count; i++) {
            double betAmount = (min + (max - min) * random.nextDouble()) * hour; // 生成800到3000的随机数
            betList.add(new BigDecimal(betAmount));
        }
        return betList;
    }


}
