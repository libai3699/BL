package com.gp.common.base.constant;

public class CurrencyConstants {
    public static final int usdt = 6;
    public static final int trx = 8;
    public static final int eth = 1;
    public static final int btc = 4;

    public static final int usdt_Item = 10007;
    public static final String usdt_chainTag = "trc20";

    public static String getEmoji(Integer currencyId) {
        if (currencyId == CurrencyConstants.trx) {
            return EmojiCons.trx;
        } else if (currencyId == CurrencyConstants.usdt) {
            return EmojiCons.usdt;
        } else if (currencyId == CurrencyConstants.eth) {
            return EmojiCons.eth;
        } else if (currencyId == CurrencyConstants.btc) {
            return EmojiCons.btc;
        } else {
            return EmojiCons.钱袋子;
        }
    }
}
