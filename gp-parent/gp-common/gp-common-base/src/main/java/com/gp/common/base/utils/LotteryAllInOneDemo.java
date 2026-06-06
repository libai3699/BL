package com.gp.common.base.utils;

import cn.hutool.core.collection.CollUtil;
import lombok.Data;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class LotteryAllInOneDemo {

    public static void main(String[] args) throws Exception {
        Map<String, Integer> bets = new HashMap<>();
        // ===== 快三 =====
        bets.put("快三:和值:10", 500);
        bets.put("快三:大小:大", 200);
        bets.put("快三:单双:单", 300);
        // ===== 11选5（下注前二，但开奖展示 5 位）=====
        bets.put("11选5:前二:01-02", 200);
        // ===== 时时彩（5位）=====
        bets.put("时时彩:万位:5", 300);
        bets.put("时时彩:个位:大", 400);
        bets.put("时时彩:总和:单", 200);
        // bets.put("时时彩:直选:1-2-3-4-5", 100); // 开此行测试全量直选
        // ===== 赛车（10位）=====
        bets.put("赛车:冠亚和:13", 800);
        bets.put("赛车:龙虎:龙", 500);
        // ===== 六合彩（7位：前六+特码）=====
        bets.put("六合彩:特码:17", 1000);
        bets.put("六合彩:大小:大", 400);
        bets.put("六合彩:单双:单", 600);
        bets.put("六合彩:色波:红", 500);

        System.out.println("===== 单线程执行耗时 =====");
        long t1 = System.currentTimeMillis();
        calcK3(bets);
        calc11X5(bets);
        calcSSC(bets);
        calcCar(bets);
        calcLHC(bets);
        long t2 = System.currentTimeMillis();
        System.out.println("总耗时: " + (t2 - t1) + " ms");

        System.out.println("\n===== 并发执行耗时 =====");
        ExecutorService pool = Executors.newFixedThreadPool(5);
        List<Callable<Void>> tasks = Arrays.asList(
                () -> {
                    calcK3(bets);
                    return null;
                },
                () -> {
                    calc11X5(bets);
                    return null;
                },
                () -> {
                    calcSSC(bets);
                    return null;
                },
                () -> {
                    calcCar(bets);
                    return null;
                },
                () -> {
                    calcLHC(bets);
                    return null;
                }
        );
        long t3 = System.currentTimeMillis();
        pool.invokeAll(tasks);
        pool.shutdown();
        long t4 = System.currentTimeMillis();
        System.out.println("并发总耗时: " + (t4 - t3) + " ms");
    }

    // ====================== 快三（3位） ======================
    private static void calcK3(Map<String, Integer> bets) {
        int totalBet = total(bets, "快三");
        List<Result> results = new ArrayList<>();
        for (int d1 = 1; d1 <= 6; d1++) {
            for (int d2 = 1; d2 <= 6; d2++) {
                for (int d3 = 1; d3 <= 6; d3++) {
                    int sum = d1 + d2 + d3;
                    double payout = 0;
                    if (bets.containsKey("快三:和值:" + sum)) payout += bets.get("快三:和值:" + sum) * 9.0;
                    if (bets.containsKey("快三:大小:" + (sum >= 11 ? "大" : "小")))
                        payout += bets.get("快三:大小:" + (sum >= 11 ? "大" : "小")) * 1.96;
                    if (bets.containsKey("快三:单双:" + (sum % 2 == 0 ? "双" : "单")))
                        payout += bets.get("快三:单双:" + (sum % 2 == 0 ? "双" : "单")) * 1.96;
                    results.add(new Result(Arrays.asList(d1, d2, d3), payout, totalBet - payout, totalBet));
                }
            }
        }
        printResult("快三", results, LotteryAllInOneDemo::fmtDefault);
    }

    // ====================== 11选5（开奖展示 5 位） ======================
    private static void calc11X5(Map<String, Integer> bets) {
        int totalBet = total(bets, "11选5");
        List<Result> results = new ArrayList<>();
        Random r = new Random();
        for (int i = 1; i <= 11; i++) {
            for (int j = 1; j <= 11; j++) {
                if (i == j) continue;
                String key = String.format("11选5:前二:%02d-%02d", i, j);
                double payout = 0;
                if (bets.containsKey(key)) payout += bets.get(key) * 95.0;
                List<Integer> open = build11x5Open(i, j, r);
                results.add(new Result(open, payout, totalBet - payout, totalBet));
            }
        }
        printResult("11选5", results, LotteryAllInOneDemo::fmt11x5);
    }

    // ====================== 时时彩（5位，混合模式） ======================
    private static void calcSSC(Map<String, Integer> bets) {
        boolean hasZhiXuan = bets.keySet().stream().anyMatch(k -> k.startsWith("时时彩:直选"));
        int totalBet = total(bets, "时时彩");
        List<Result> results = new ArrayList<>();
        Random rand = new Random();

        if (hasZhiXuan) {
            for (int w = 0; w < 10; w++)
                for (int q = 0; q < 10; q++)
                    for (int b = 0; b < 10; b++)
                        for (int s = 0; s < 10; s++)
                            for (int g = 0; g < 10; g++) {
                                int[] nums = {w, q, b, s, g};
                                double payout = calcSSCPayout(nums, bets);
                                results.add(new Result(Arrays.asList(w, q, b, s, g), payout, totalBet - payout, totalBet));
                            }
            printResult("时时彩 全量", results, LotteryAllInOneDemo::fmtDefault);
        } else {
            for (int g = 0; g < 10; g++) {
                double payout = 0;
                if (bets.containsKey("时时彩:个位:" + (g >= 5 ? "大" : "小")))
                    payout += bets.get("时时彩:个位:" + (g >= 5 ? "大" : "小")) * 1.96;
                int w = rand.nextInt(10), q = rand.nextInt(10), b = rand.nextInt(10), s = rand.nextInt(10);
                results.add(new Result(Arrays.asList(w, q, b, s, g), payout, totalBet - payout, totalBet));
            }
            for (int w = 0; w < 10; w++) {
                double payout = 0;
                if (bets.containsKey("时时彩:万位:" + w))
                    payout += bets.get("时时彩:万位:" + w) * 9.5;
                int q = rand.nextInt(10), b = rand.nextInt(10), s = rand.nextInt(10), g = rand.nextInt(10);
                results.add(new Result(Arrays.asList(w, q, b, s, g), payout, totalBet - payout, totalBet));
            }
            printResult("时时彩 快速", results, LotteryAllInOneDemo::fmtDefault);
        }
    }

    private static double calcSSCPayout(int[] nums, Map<String, Integer> bets) {
        double payout = 0;
        if (bets.containsKey("时时彩:万位:" + nums[0]))
            payout += bets.get("时时彩:万位:" + nums[0]) * 9.5;
        if (bets.containsKey("时时彩:个位:" + (nums[4] >= 5 ? "大" : "小")))
            payout += bets.get("时时彩:个位:" + (nums[4] >= 5 ? "大" : "小")) * 1.96;
        int sum = Arrays.stream(nums).sum();
        if (bets.containsKey("时时彩:总和:" + (sum % 2 == 0 ? "双" : "单")))
            payout += bets.get("时时彩:总和:" + (sum % 2 == 0 ? "双" : "单")) * 1.96;
        String zhxKey = String.format("时时彩:直选:%d-%d-%d-%d-%d", nums[0], nums[1], nums[2], nums[3], nums[4]);
        if (bets.containsKey(zhxKey)) payout += bets.get(zhxKey) * 90000;
        return payout;
    }

    // ====================== 赛车（10位） ======================
    private static void calcCar(Map<String, Integer> bets) {
        int totalBet = total(bets, "赛车");
        List<Result> results = new ArrayList<>();
        List<Integer> cars = new ArrayList<>();
        for (int i = 1; i <= 10; i++) cars.add(i);
        Random rand = new Random();
        for (int t = 0; t < 200; t++) {
            Collections.shuffle(cars, rand);
            int gyh = cars.get(0) + cars.get(1);
            boolean longWin = cars.get(0) > cars.get(9);
            double payout = 0;
            if (bets.containsKey("赛车:冠亚和:" + gyh)) payout += bets.get("赛车:冠亚和:" + gyh) * 9.0;
            if (bets.containsKey("赛车:龙虎:" + (longWin ? "龙" : "虎")))
                payout += bets.get("赛车:龙虎:" + (longWin ? "龙" : "虎")) * 1.96;
            results.add(new Result(new ArrayList<>(cars), payout, totalBet - payout, totalBet));
        }
        printResult("赛车", results, LotteryAllInOneDemo::fmtDefault);
    }

    // ====================== 六合彩（7位：前六+特码） ======================
    private static void calcLHC(Map<String, Integer> bets) {
        int totalBet = total(bets, "六合彩");
        List<Result> results = new ArrayList<>();
        Random r = new Random();
        for (int tema = 1; tema <= 49; tema++) {
            double payout = 0;
            if (bets.containsKey("六合彩:特码:" + tema)) payout += bets.get("六合彩:特码:" + tema) * 48.0;
            if (bets.containsKey("六合彩:大小:" + (tema >= 25 ? "大" : "小")))
                payout += bets.get("六合彩:大小:" + (tema >= 25 ? "大" : "小")) * 1.96;
            if (bets.containsKey("六合彩:单双:" + (tema % 2 == 0 ? "双" : "单")))
                payout += bets.get("六合彩:单双:" + (tema % 2 == 0 ? "双" : "单")) * 1.96;
            if (bets.containsKey("六合彩:色波:" + getColor(tema)))
                payout += bets.get("六合彩:色波:" + getColor(tema)) * 2.9;
            List<Integer> open = buildLhcOpen(tema, r);
            results.add(new Result(open, payout, totalBet - payout, totalBet));
        }
        printResult("六合彩", results, LotteryAllInOneDemo::fmtLhc);
    }

    // ========= 辅助：构造 11选5 的 5 位开奖号码（固定前二 + 随机后三位） =========
    private static List<Integer> build11x5Open(int first, int second, Random r) {
        List<Integer> pool = new ArrayList<>();
        for (int n = 1; n <= 11; n++) if (n != first && n != second) pool.add(n);
        Collections.shuffle(pool, r);
        List<Integer> open = new ArrayList<>(5);
        open.add(first);
        open.add(second);
        open.add(pool.get(0));
        open.add(pool.get(1));
        open.add(pool.get(2));
        return open;
    }

    // ========= 辅助：构造 六合彩 的 7 位开奖号码（前六随机 + 特码） =========
    private static List<Integer> buildLhcOpen(int tema, Random r) {
        List<Integer> pool = new ArrayList<>();
        for (int n = 1; n <= 49; n++) if (n != tema) pool.add(n);
        Collections.shuffle(pool, r);
        List<Integer> front6 = new ArrayList<>(pool.subList(0, 6));
        Collections.sort(front6);
        front6.add(tema);
        return front6;
    }

    private static String getColor(int num) {
        int[] red = {1, 2, 7, 8, 12, 13, 18, 19, 23, 24, 29, 30, 34, 35, 40, 45, 46};
        int[] blue = {3, 4, 9, 10, 14, 15, 20, 25, 26, 31, 36, 37, 41, 42, 47, 48};
        Set<Integer> redSet = new HashSet<>(), blueSet = new HashSet<>();
        for (int r : red) redSet.add(r);
        for (int b : blue) blueSet.add(b);
        if (redSet.contains(num)) return "红";
        if (blueSet.contains(num)) return "蓝";
        return "绿";
    }

    // ====================== 公共方法 ======================
    private static int total(Map<String, Integer> bets, String prefix) {
        return bets.entrySet().stream().filter(e -> e.getKey().startsWith(prefix))
                .mapToInt(Map.Entry::getValue).sum();
    }


    private static Result pickFirstByKillRate(List<Result> results, double minKill, double maxKill) {

        if (results == null || results.isEmpty()) {
            throw new IllegalArgumentException("results is empty");
        }
        //投注额低于200随机
        if (results.get(0).getBet() <= 200.00) {
            return results.get(new Random().nextInt(results.size()));
        }
        Collections.shuffle(results, new Random());
        // 遍历找第一个符合杀率区间的结果
        for (Result r : results) {
            double k = (r.bet > 0 ? r.profit / r.bet : 0.0);

            // 忽略异常杀率（比如 -200%、1000% 这种离谱的值）
            if (k < -1.0 || k > 1.0) {
                continue;
            }

            if (k >= minKill && k <= maxKill) {
                return r;
            }
        }

        // 如果没有符合条件的，兜底：
        // 先尝试找一个“最接近 minKill/maxKill 的结果”
        Result fallback = results.get(0);
        double minDiff = Double.MAX_VALUE;
        for (Result r : results) {
            double k = (r.bet > 0 ? r.profit / r.bet : 0.0);
            double diff = (k < minKill) ? (minKill - k) : (k > maxKill ? (k - maxKill) : 0);
            if (diff < minDiff) {
                minDiff = diff;
                fallback = r;
            }
        }
        return fallback;
    }


    private static void printResult(String game, List<Result> results,
                                    java.util.function.Function<List<Integer>, String> formatter) {
        Result chosen = pickFirstByKillRate(results, 0.1, 0.3);
        double killRate = chosen.bet > 0 ? chosen.profit / chosen.bet : 0;
        String numsStr = (formatter == null) ? chosen.nums.toString() : formatter.apply(chosen.nums);
        System.out.println("[" + game + "] 开奖号码: " + numsStr +
                " | 投注=" + chosen.bet +
                " | 派彩=" + chosen.payout +
                " | 客损=" + chosen.profit +
                " | 杀率=" + String.format("%.2f%%", killRate * 100));
    }

    private static String fmtDefault(List<Integer> nums) {
        return nums.toString();
    }

    private static String fmt11x5(List<Integer> nums) {
        List<String> s = new ArrayList<>(nums.size());
        for (int n : nums) s.add(String.format("%02d", n));
        return s.toString();
    }

    private static String fmtLhc(List<Integer> nums) {
        List<String> s = new ArrayList<>(nums.size());
        for (int i = 0; i < nums.size(); i++) {
            if (i == nums.size() - 1) s.add(nums.get(i) + "(特)");
            else s.add(String.valueOf(nums.get(i)));
        }
        return s.toString();
    }

    @Data
    static class Result {
        List<Integer> nums;  // 开奖号码
        double payout;       // 派彩
        double profit;       // 客损
        double bet;          // 投注总额

        Result(List<Integer> nums, double payout, double profit, double bet) {
            this.nums = nums;
            this.payout = payout;
            this.profit = profit;
            this.bet = bet;
        }
    }
}
