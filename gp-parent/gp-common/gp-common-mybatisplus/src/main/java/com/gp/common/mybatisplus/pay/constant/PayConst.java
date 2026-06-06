package com.gp.common.mybatisplus.pay.constant;

/**
 * @author axing
 * @version 1.0
 * @date 2023/11/8/008 14:36
 */
public class PayConst {

    //最低提现金额 5U
    //token时长 1天
    //token过期状态码
    public static final Integer expiredCode = -304;
    //接口状态
    public static final String success = "success";
    public static final Integer successCode = 200;


    public static final String apiDomain = "https://api.maskex.com";

    public static final Integer currency_id = 6;
    public static final Integer CURRENCY_USDT_DIGIT = 8;
    public static final Integer usdt_item = 10007;
    public static final Integer currency_id_trx = 8;

    public static final Integer currency_id_BTC = 4;
    public static final Integer currency_id_ETH = 1;
    public static final Integer item_id = 10007;
    public static final String chain_tag = "trc20";

    public static final String getCoinList = "/V1/Market/getCoinList";
    //获取token
    public static final String getUserInfo = "/ThirdApi/User/getUserInfo";
    //获取用户资产
    public static final String getAssets = "/ThirdApi/Assets/getAssets";

    //创建地址链接
    public static final String addDepositAddress = "/ThirdApi/Assets/addDepositAddress";
    //删除地址
    public static final String delDepositAddress = "/ThirdApi/Assets/delDepositAddress";
    //提币
    public static final String withdraw = "/ThirdApi/Assets/withdraw";
    //获取充提列表
    public static final String depositWithdrawList = "/ThirdApi/Assets/depositWithdrawList";
    //获取充提订单详情
    public static final String depositWithdrawInfo = "/ThirdApi/Assets/depositWithdrawInfo";
    //获取风控列表
    public static final String getAmlExchangeList = "/ThirdApi/Assets/getAmlExchangeList";
    //获取地址链接列表
    public static final String getDepositAddressList = "/ThirdApi/Assets/getDepositAddressList";
    //获取地址
    public static final String getDepositAddress = "/ThirdApi/Assets/getDepositAddress";
    //设置交易密码
    public static final String setPaymentPwd = "/ThirdApi/UCenter/setPaymentPwd";
    //修改交易密码
    public static final String updatePaymentPwd = "/ThirdApi/UCenter/updatePaymentPwd";

    //获取商兑汇率
//    public static final String exchangeRate = "https://api.maskexapi.com/V1/Market/symbolList";
    //费率
    //https://api.maskexapi.com/ThirdApi/Coin/getCoinRate?from_item_id=10005&to_item_id=10007
    public static final String coinRate = "https://api.maskexapi.com/ThirdApi/Coin/getCoinRate?from_item_id={}&to_item_id={}";
}
