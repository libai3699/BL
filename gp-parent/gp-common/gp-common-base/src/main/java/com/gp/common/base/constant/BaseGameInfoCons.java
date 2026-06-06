package com.gp.common.base.constant;

public interface BaseGameInfoCons {

    //通用币种
    interface Currrency {
        //默认统一为USDT, 所有包网统一是USDT或者USD
        Integer UsdtCurrencyId = 6;
        Integer itemId = 10007;
        String chainTag = "trc20";
        String itemName = "usdt";
    }

    //通用注单结果状态
    interface TermOrderStatus {
        Integer 未开奖 = 0;
        Integer 赢 = 1;
        Integer 输 = 2;
        Integer 和 = 3;
        Integer 注单取消 = 4;
    }

    //注单是否需要反水
    interface TermOrderDoRebateStatus {
        String 无反水 = "noRebate";
    }

    //通用注单结算状态
    interface TermSettleStatus {
        Integer 未结算 = 0;
        Integer 已结算 = 1;

    }

    //注单类型(0 单一注单, 1 复合注单, 2 复合子注单)
    interface OrderType {
        Integer 单一注单 = 0;
        Integer 复合注单 = 1;
        Integer 复合子注单 = 2;
    }

    //通用注单反水处理状态
    interface TermRebateStatus {
        Integer 未处理 = 0;
        Integer 已处理 = 1;

    }

    //通用反水,返佣类型
    interface OrderRebateType {
        Integer 反水 = 1;
        Integer 返佣 = 2;

        Integer 代理工资 = 3;

    }

    //用户扩展表类型
    interface UserExtType {
        Integer 转盘次数 = 1;
        /**
         * 打码量/累计打码量
         */
        Integer 打码量 = 2;
        Integer 提现打码量 = 3;
        Integer 未领取反水 = 4;
        Integer 未领取返佣 = 5;
        Integer 彩金 = 6;
        Integer 累计充值 = 7;

        /**
         * 充值彩金
         */
        Integer 充值彩金 = 8;
        /**
         * 转盘彩金
         */
        Integer 转盘彩金 = 9;
        /**
         * 活动彩金
         */
        Integer 活动彩金 = 10;
        /**
         * 未领取代理工资
         */
        Integer 未领取代理工资 = 11;
    }

    //用户扩展表订单表

    /**
     * t_user_ext_change(用户扩展表)表 order_type 订单类型(1 注单反水返佣, 2 反水返佣领取)',
     */
    interface UserExtOrderType {
        Integer 注单 = 1;
        Integer 反水返佣领取订单 = 2;
        Integer 转盘订单 = 3;
        Integer 活动订单 = 4;
        Integer 充值订单 = 5;
        Integer 人工上分下分订单 = 6;
        Integer 人工操作提现打码量订单 = 7;
        Integer 人工操作转盘次数订单 = 8;

        Integer 新人赠送 = 9;

        Integer 人工操作打码量订单 = 10;
        Integer 红包订单 = 11;
        Integer 级差返佣 = 12;
        Integer 代理工资领取 = 13;

        Integer 转账订单 = 14;
    }

    /**
     * 游戏类型
     */
    interface GameTypeCode {
        //电子 -- 打码量是下注额
        String Slots = "1";
        //体育 -- 打码量是游戏输赢
        String Sports = "2";
        //视讯 -- 打码量是游戏输赢
        String Table = "3";
        //彩票 -- 打码量是下注额
        String Keno = "4";
        //棋牌 -- 打码量是下注额
        String Poker = "5";
        //区块链 -- 打码量是游戏下注额
        String Blockchain = "6";
        //捕鱼 -- 打码量是游戏下注额
        String FishGame = "7";
        //宾果 -- 打码量是游戏下注额
        String Bingo = "8";
    }

    //用户扩展表类型  账变类型

    /**
     * t_user_ext_change(用户扩展表)表  type 帐变类型(1 用户返水, 2 上级返佣, 3 反水领取, 4 返佣领取)',
     */
    interface UserExtChangeType {
        //下单导致
        Integer 用户返水 = 1;
        Integer 上级返佣 = 2;
        Integer 反水领取 = 3;
        Integer 返佣领取 = 4;

        Integer 转盘消耗 = 5;
        Integer 转盘增加 = 6;
        Integer 彩金增加 = 7;
        Integer 提现打码量增加 = 8;
        Integer 人工上分 = 9;
        Integer 人工下分 = 10;
        Integer 打码量增加 = 11;
        Integer 人工增加提现打码量 = 12;
        Integer 人工减少提现打码量 = 13;
        Integer 人工增加转盘次数 = 14;
        Integer 人工减少转盘次数 = 15;
        Integer 累计充值增加 = 16;

        Integer 人工增加打码量 = 17;
        Integer 人工减少打码量 = 18;

        Integer 代理工资 = 19;

        Integer 代理工资领取 = 20;
    }

}
