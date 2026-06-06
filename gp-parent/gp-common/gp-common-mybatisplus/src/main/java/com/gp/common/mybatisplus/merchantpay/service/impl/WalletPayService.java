package com.gp.common.mybatisplus.merchantpay.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.common.core.constant.HttpStatus;
import com.common.core.log.ErrorTelegramUtil;
import com.common.core.nacos.BNacosParam;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.exception.MyPayException;
import com.gp.common.base.utils.MessagesUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gp.common.mybatisplus.entity.*;
import com.gp.common.mybatisplus.merchantpay.constants.*;
import com.gp.common.mybatisplus.pay.mpay.WalletBase;
import com.gp.common.mybatisplus.service.OrderWithdrawService;
import com.gp.common.mybatisplus.service.PayChannelService;
import com.gp.common.mybatisplus.merchantpay.base.JumpUtil;
import com.gp.common.mybatisplus.merchantpay.service.PayWService;
import com.gp.common.mybatisplus.pay.constant.PayConst;
import com.gp.common.mybatisplus.pay.mpay.WalletCreateOrder;
import com.gp.common.mybatisplus.pay.mpay.WalletCreateOrderResult;
import com.gp.common.mybatisplus.pay.mpay.WalletParams;
import com.gp.common.mybatisplus.pay.mpay.constant.MaskPayConst;
import com.gp.common.mybatisplus.pay.mpay.createAddress.CreateAddress;
import com.gp.common.mybatisplus.pay.mpay.createAddress.CreateAddressDo;
import com.gp.common.mybatisplus.pay.mpay.createAddress.CreateAddressDto;
import com.gp.common.mybatisplus.pay.mpay.order.*;
import com.gp.common.mybatisplus.pay.mpay.param.MerchantGoldWithdrawDto;
import com.gp.common.mybatisplus.pay.mpay.param.MerchantPayDto;
import com.gp.common.mybatisplus.pay.mpay.param.MerchantQueryOrderDto;
import com.gp.common.mybatisplus.pay.mpay.param.MerchantSendMoneyDto;
import com.gp.common.mybatisplus.pay.mpay.param.MerchantWithdrawDto;
import com.gp.common.mybatisplus.pay.mpay.util.SignUntil;
import com.gp.common.mybatisplus.pay.service.PayService;
import com.gp.common.mybatisplus.service.CurrencyService;
import com.gp.common.mybatisplus.until.RequestUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author axing
 */
@Slf4j
@Service
public class WalletPayService extends PayWService {

    @Resource
    private BNacosParam bNacosParam;
    @Resource
    private ErrorTelegramUtil errorTelegramUtil;
    @Resource
    private OrderWithdrawService orderWithdrawService;
    @Resource
    private PayChannelService payChannelService;

    /** 支付方式：区块链冷钱包（直充） */
    public static final String USDT_BLACK = "usdt-black";
    /** 支付方式：商户充值（小程序） */
    public static final String USDT_MERCHANT = "usdt-merchant";

    /** 支付方式：区块链冷钱包（直充） 提现通道 */
    public static final String USDT_BLACK_WITHDRAW = "usdt-black-withdraw";
    /** 支付方式：商户充值（小程序） 提现通道 */
    public static final String USDT_MERCHANT_WITHDRAW = "usdt-merchant-withdraw";

    /** 支付类型 → 收款参数解析器，新增类型只需在此注册一行 */
    private static final Map<String, Function<JSONObject, Map<String, String>>> WITHDRAW_PARSERS = new HashMap<>();
    static {
        WITHDRAW_PARSERS.put(PayTypeCons.PAY_USDT, USDTPayTypeAttribute::parse);
        // 历史类型兼容：PAY_KD 也按地址型提现参数解析
        WITHDRAW_PARSERS.put(PayTypeCons.PAY_KD, USDTPayTypeAttribute::parse);
        WITHDRAW_PARSERS.put(PayTypeCons.MPAY, MpayPayTypeAttribute::parse);
    }

    // ========== 区块链 ==========
    private String createAddressUrl = "/merchant/createAddress"; // 创建充值地址
    private String queryRechargeUrl = "/merchant/queryRecharge"; // 查询链上充值记录
    private String goldWalletWithdraw = "/merchant/goldWalletWithdraw"; // 链上提现(冷钱包)
    private String queryWithdrawOrder = "/merchant/queryWithdrawOrder"; // 查询链上提现订单状态

    // ========== 小程序 ==========
    private String createOrderUrlUrl = "/merchant/pay"; // 创建收款订单(商户下单)
    private String queryOrderInfoUrl = "/merchant/queryOrder"; // 查询收款订单状态
    private String withdrawUrl = "/merchant/sendMoney"; // TG内部转账提现

    @Override
    protected void doRegister() {
        registerPayWService(PayMerchantCons.WALLET_PAY);
    }

    @Override
    public JSONObject getNotifyRequestParam(HttpServletRequest request) {
        String jsonStr = "";
        try {
            Map<String, String> paramMap = RequestUntil.doParamCompatible(request);
            if (paramMap.isEmpty()) {
                jsonStr = RequestUntil.readRequestBody(request);
            } else {
                jsonStr = JSON.toJSONString(paramMap);
            }
        } catch (Exception e) {
            log.error("WalletPayNotify param error", e);
        }
        return JSON.parseObject(jsonStr);
    }

    @Override
    public String queryMoney(MerchantPay merchantPay) {
        return "暂不支持";
    }

    @Override
    public OrderInfoDo queryOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String appKey = filterByApp(walletParams, "appKey");
        String domainName = filterByApp(walletParams, "domainName");
        String appSecret = filterByApp(walletParams, "appSecret");
        long nowTime = System.currentTimeMillis();
        MerchantQueryOrderDto merchantQueryOrderDto = new MerchantQueryOrderDto();
        BeanUtils.copyProperties(queryOrderInfo, merchantQueryOrderDto);
        merchantQueryOrderDto.setTimestamp(nowTime);
        merchantQueryOrderDto.setAppKey(appKey);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(merchantQueryOrderDto);
        String sign = SignUntil.generateSign(jsonObject, appSecret);
        merchantQueryOrderDto.setSign(sign);
        OrderInfoDo orderInfoDo = doRequest(domainName + queryOrderInfoUrl, merchantQueryOrderDto, OrderInfoDo.class);
        Integer code = orderInfoDo.getCode();
        String msg = orderInfoDo.getMsg();
        if (code != HttpStatus.SUCCESS) {
            errorTelegramUtil.dealErrorMsg("mpay查询订单异常: " + msg);
            throw new MyPayException(MessagesUtils.get("bot.pay.CXDDSB"), msg);
        }
        return orderInfoDo;
    }

    @Override
    public OrderInfoDo queryWithdrawOrder(QueryOrderInfo queryOrderInfo) {
        String payParam = queryOrderInfo.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String appKey = filterByApp(walletParams, "appKey");
        String domainName = filterByApp(walletParams, "domainName");
        String appSecret = filterByApp(walletParams, "appSecret");
        long nowTime = System.currentTimeMillis();
        MerchantQueryOrderDto merchantQueryOrderDto = new MerchantQueryOrderDto();
        BeanUtils.copyProperties(queryOrderInfo, merchantQueryOrderDto);
        merchantQueryOrderDto.setTimestamp(nowTime);
        merchantQueryOrderDto.setAppKey(appKey);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(merchantQueryOrderDto);
        String sign = SignUntil.generateSign(jsonObject, appSecret);
        merchantQueryOrderDto.setSign(sign);

        // 通过订单号确定通道类型
        String payMethod = "";
        try {
            OrderWithdraw orderWithdraw = orderWithdrawService.getOne(Wrappers.lambdaQuery(OrderWithdraw.class)
                    .eq(OrderWithdraw::getOrderNo, queryOrderInfo.getOrderNo()));
            if (orderWithdraw != null) {
                PayChannel payChannel = payChannelService.getById(orderWithdraw.getPayChannelId());
                if (payChannel != null) {
                    payMethod = payChannel.getPayMethod();
                }
            }
        } catch (Exception e) {
            log.warn("查询提现订单所属通道失败, orderNo:{}", queryOrderInfo.getOrderNo());
        }

        // 如果是小程序提现，走通用的 queryOrder 逻辑
        if (USDT_MERCHANT_WITHDRAW.equals(payMethod)) {
            OrderInfoDo orderInfoDo = doRequest(domainName + queryOrderInfoUrl, merchantQueryOrderDto,
                    OrderInfoDo.class);
            if (orderInfoDo.getCode() != HttpStatus.SUCCESS) {
                String msg = orderInfoDo.getMsg();
                errorTelegramUtil.dealErrorMsg("mpay查询小程序提现订单异常: " + msg);
                throw new MyPayException(MessagesUtils.get("bot.pay.CXDDSB"), msg);
            }
            return orderInfoDo;
        }

        // 默认走区块链提现查询逻辑
        OrderWithdrawInfoDo orderWithdrawInfoDo = doRequest(domainName + queryWithdrawOrder, merchantQueryOrderDto,
                OrderWithdrawInfoDo.class);
        Integer code = orderWithdrawInfoDo.getCode();
        String msg = orderWithdrawInfoDo.getMsg();
        if (code != HttpStatus.SUCCESS) {
            errorTelegramUtil.dealErrorMsg("mpay查询区块链提现订单异常: " + msg);
            throw new MyPayException(MessagesUtils.get("bot.pay.CXDDSB"), msg);
        }

        OrderWithdrawInfoDo.OrderInfo data = orderWithdrawInfoDo.getData();
        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(data.getOrderNo());
        orderInfo.setCurrencyId(PayConst.currency_id);
        orderInfo.setAmount(data.getAmount());
        Integer statusSys = 0;
        // 0 待审核, 1 风险审核通过, 2 财务通过, 3 已拒绝, 4 提现成功, 5 提现失败, 6 上游下单成功, 7 上游下单失败
        Integer status = data.getOrderStatus();
        if (status == 0 || status == 1 || status == 2 || status == 6) {
            statusSys = 0;
        } else if (status == 4) {
            statusSys = 1;
        } else if (status == 3 || status == 5 || status == 7) {
            statusSys = 2;
        }
        orderInfo.setOrderStatus(statusSys);
        orderInfo.setActualAmount(data.getActualAmount());
        orderInfo.setMerchantOrderNo(data.getMerchantOrderNo());
        orderInfo.setPayTime(data.getCreateTime());
        orderInfo.setCreateTime(data.getCreateTime());
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    /**
     *
     * @param createOrder
     * @return
     */
    @Override
    public WalletCreateOrderResult createOrder(CreateOrder createOrder) {
        String payParam = createOrder.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String appKey = filterByApp(walletParams, "appKey");
        String domainName = filterByApp(walletParams, "domainName");
        String appSecret = filterByApp(walletParams, "appSecret");
        long nowTime = System.currentTimeMillis();

        // 支付通道
        PayChannel payChannel = createOrder.getPayChannel();
        // 支付类型
        // PayType payType = createOrder.getPayType();

        if (USDT_BLACK.equals(payChannel.getPayMethod())) {
            // 如果是mpay的usdt的,访问mpay获取充值地址,组装成url
            String address = PayService.initUserAndGetPayToken(createOrder.getUserId(), payParam,
                    CurrencyService.usdtCurrencyAbs.getItemId(),
                    CurrencyService.usdtCurrencyAbs.getChainTag(),
                    CurrencyService.usdtCurrencyAbs.getItemName());
            String url = JumpUtil.doBlackJumpUrl(createOrder.getDomain(), createOrder.getOriginalAmount(),
                    CurrencyService.usdtCurrencyAbs.getItemName(),
                    CurrencyService.usdtCurrencyAbs.getChainTag(), address);

            // 返回结果
            WalletCreateOrderResult result = new WalletCreateOrderResult();
            // result.setUpOrderNo();
            result.setUrl(url);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("addr", address);
            jsonObject.put("url", url);
            result.setUpInfo(jsonObject.toJSONString());
            return result;
        } else if (USDT_MERCHANT.equals(payChannel.getPayMethod())) {
            // 如果是mpay的商户订单,访问mpay获取充值链接
            MerchantPayDto merchantPayParam = new MerchantPayDto();
            merchantPayParam.setMerchantOrderNo(createOrder.getOrderNo());
            // usdt；应付算法币金额与 XPay 小程序一致：使用 createOrder.amount（已含汇率换算）
            merchantPayParam.setCurrencyId(MaskPayConst.currency_id);
            BigDecimal payAmount = createOrder.getOriginalAmount();
            if (payAmount == null) {
                throw new MyPayException(MessagesUtils.get("bot.pay.ZFYC"), "订单金额为空");
            }
            merchantPayParam.setAmount(payAmount);
            merchantPayParam.setTimestamp(nowTime);
            merchantPayParam.setAppKey(appKey);
            merchantPayParam.setLanKey(createOrder.getLanKey());

            JSONObject jsonObject = (JSONObject) JSON.toJSON(merchantPayParam);
            String sign = SignUntil.generateSign(jsonObject, appSecret);
            merchantPayParam.setSign(sign);
            WalletCreateOrder walletCreateOrder = doRequest(domainName + createOrderUrlUrl, merchantPayParam,
                    WalletCreateOrder.class);
            // 解析出需要的结果级
            Integer code = walletCreateOrder.getCode();
            String msg = walletCreateOrder.getMsg();
            WalletCreateOrder.OrderInfo data = walletCreateOrder.getData();

            if (code != HttpStatus.SUCCESS) {
                errorTelegramUtil.dealErrorMsg("mpay下单异常 : " + msg);
                throw new MyPayException(MessagesUtils.get("bot.pay.ZFYC"), msg);
            }

            // 返回结果
            WalletCreateOrderResult result = new WalletCreateOrderResult();
            result.setUpOrderNo(data.getOrderNo());
            result.setUrl(data.getUrl());
            result.setUpInfo(JSON.toJSONString(walletCreateOrder));
            return result;
        } else {
            throw new RuntimeException("Mpay通道不存在: payMethod=" + payChannel.getPayMethod());
        }
    }

    @Override
    public JSONObject callbackPayParam(MerchantPay merchantPay, OrderAmount orderAmount) {
        JSONObject jsonObject = new JSONObject();
        // MPay 查单用平台单号(up_order_no)；回调体里的 orderNo 也是平台单号
        String queryOrderNo = StrUtil.isNotBlank(orderAmount.getUpOrderNo())
                ? orderAmount.getUpOrderNo()
                : orderAmount.getOrderNo();
        jsonObject.put("orderNo", queryOrderNo);
        jsonObject.put("merchantOrderNo", orderAmount.getOrderNo());
        return jsonObject;
    }

    @Override
    public PayCallbackResult callbackPay(MerchantPay merchantPay, JSONObject jsonObject) {
        try {
            String orderNo = jsonObject.getString("orderNo");
            log.info("Wallet支付充值回调 - 订单号: {}", orderNo);

            String payParam = merchantPay.getParamStr();
            // ===== 主动查单逻辑，二次确认订单状态 =====
            try {
                QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
                queryOrderInfo.setOrderNo(orderNo);
                queryOrderInfo.setPayParam(payParam);

                OrderInfoDo orderInfoDo = queryOrder(queryOrderInfo);
                log.info("Wallet支付充值回调查单 - 返回结果: orderStatus={}",
                        orderInfoDo != null && orderInfoDo.getData() != null ? orderInfoDo.getData().getOrderStatus()
                                : "null");

                if (orderInfoDo == null || orderInfoDo.getData() == null
                        || orderInfoDo.getData().getOrderStatus() != 1) {
                    log.error("Wallet支付充值回调查单 - 订单未完成");
                    return PayCallbackResult.failed("fail");
                }
                OrderInfoDo.OrderInfo data = orderInfoDo.getData();
                // MPay：data.orderNo=平台单号(SH)，data.merchantOrderNo=我方下单号(OA)，与
                // t_order_amount.order_no 一致
                String localOrderNo = StrUtil.blankToDefault(data.getMerchantOrderNo(),
                        jsonObject.getString("merchantOrderNo"));
                String platformOrderNo = StrUtil.blankToDefault(data.getOrderNo(), orderNo);
                if (StrUtil.isBlank(localOrderNo)) {
                    log.error("Wallet支付充值回调 - 缺少商户订单号 merchantOrderNo，无法匹配本地订单");
                    return PayCallbackResult.failed("fail");
                }
                log.info("Wallet支付充值回调 - 订单验证成功: platformOrderNo={}, localOrderNo={}", platformOrderNo,
                        localOrderNo);
                return PayCallbackResult.builder()
                        .orderNo(localOrderNo)
                        .merchantOrderNo(platformOrderNo)
                        .orderStatus(1)
                        .responseMsg("success")
                        .isSuccess(true)
                        .build();
            } catch (Exception e) {
                log.error("Wallet支付充值回调查单 - 异常: {}", e.getMessage(), e);
                return PayCallbackResult.failed("fail");
            }

        } catch (Exception e) {
            log.error("Wallet支付充值回调异常", e);
            return PayCallbackResult.failed("fail");
        }
    }

    @Override
    public String createAddress(CreateAddress createAddress) {
        String payParam = createAddress.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String appKey = filterByApp(walletParams, "appKey");
        String domainName = filterByApp(walletParams, "domainName");
        String appSecret = filterByApp(walletParams, "appSecret");
        long nowTime = System.currentTimeMillis();
        CreateAddressDto createAddressDto = new CreateAddressDto();
        BeanUtils.copyProperties(createAddress, createAddressDto);
        createAddressDto.setTimestamp(nowTime);
        createAddressDto.setAppKey(appKey);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(createAddressDto);
        String sign = SignUntil.generateSign(jsonObject, appSecret);
        createAddressDto.setSign(sign);
        CreateAddressDo createAddressDo = doRequest(domainName + createAddressUrl, createAddressDto,
                CreateAddressDo.class);
        Integer code = createAddressDo.getCode();
        String msg = createAddressDo.getMsg();
        if (code != HttpStatus.SUCCESS) {
            errorTelegramUtil.dealErrorMsg("mpay创建用户地址异常: " + msg);
            throw new MyPayException(MessagesUtils.get("bot.pay.CJDZYC"), msg);
        }
        return createAddressDo.getData();
    }

    @Override
    public WithdrawResultDo withdraw(WithdrawOrder withdrawOrder) {
        PayChannel payChannel = withdrawOrder.getPayChannel();

        String payParam = withdrawOrder.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String appKey = filterByApp(walletParams, "appKey");
        String domainName = filterByApp(walletParams, "domainName");
        String appSecret = filterByApp(walletParams, "appSecret");
        long nowTime = System.currentTimeMillis();

        // 金额保留4位小数
        BigDecimal amount = withdrawOrder.getAmount();
        if (amount != null) {
            amount = amount.setScale(4, RoundingMode.HALF_UP);
        }

        String url;
        String logPrefix;
        WalletBase requestDto;

        if (USDT_MERCHANT_WITHDRAW.equals(payChannel.getPayMethod())) {
            // 商户提现：源头出款，不依赖支付卡，直接发到 tgUserId
            url = domainName + withdrawUrl;
            logPrefix = "mpay小程序提现异常";

            MerchantSendMoneyDto sendMoneyDto = new MerchantSendMoneyDto();
            sendMoneyDto.setAppKey(appKey);
            sendMoneyDto.setOrderNo(withdrawOrder.getOrderNo());
            sendMoneyDto.setAmount(amount);
            sendMoneyDto.setCurrencyId(MaskPayConst.currency_id);
            sendMoneyDto.setTimestamp(nowTime);
            // TG 提现专属字段
            Long tgUserId = withdrawOrder.getTgUserId();
            if (tgUserId == null) {
                throw new RuntimeException("飞机用户ID(tgUserId)不能为空");
            }
            sendMoneyDto.setTgUserId(tgUserId);
            sendMoneyDto.setUserTgName(withdrawOrder.getUserTgName());
            sendMoneyDto.setUserTgUsername(withdrawOrder.getUserTgUsername());

            // 计算签名
            JSONObject jsonObject = (JSONObject) JSON.toJSON(sendMoneyDto);
            sendMoneyDto.setSign(SignUntil.generateSign(jsonObject, appSecret));
            requestDto = sendMoneyDto;

        } else if (USDT_BLACK_WITHDRAW.equals(payChannel.getPayMethod())) {
            // 冷钱包提现：必须从支付卡解析 address
            Map<String, Object> extParamMap = withdrawOrder.getWithdrawParam();
            if (MapUtil.isEmpty(extParamMap)) {
                throw new MyPayException("订单缺少支付卡信息");
            }
            JSONObject payCardjsonObject = JSON.parseObject(JSON.toJSONString(extParamMap));

            if (withdrawOrder.getPayType() == null) {
                throw new MyPayException("订单缺少支付类型");
            }
            String payTypeCode = withdrawOrder.getPayType().getCode();
            Function<JSONObject, Map<String, String>> parser = WITHDRAW_PARSERS.get(payTypeCode);
            if (parser == null) {
                throw new MyPayException("不支持的支付类型: " + payTypeCode);
            }
            Map<String, String> account = parser.apply(payCardjsonObject);
            String address = account.getOrDefault("address", "");

            url = domainName + goldWalletWithdraw;
            logPrefix = "mpay冷钱包提现异常";

            MerchantGoldWithdrawDto goldWithdrawDto = new MerchantGoldWithdrawDto();
            goldWithdrawDto.setAppKey(appKey);
            goldWithdrawDto.setOrderNo(withdrawOrder.getOrderNo());
            goldWithdrawDto.setAmount(amount);
            goldWithdrawDto.setCurrencyId(MaskPayConst.currency_id);
            goldWithdrawDto.setTimestamp(nowTime);

            // 冷钱包提现专属字段
            String finalAddress = StrUtil.isNotBlank(address) ? address : withdrawOrder.getAddress();
            if (StrUtil.isBlank(finalAddress)) {
                throw new RuntimeException("提现地址(address)不能为空");
            }
            goldWithdrawDto.setAddress(finalAddress);

            // 计算签名
            JSONObject jsonObject = (JSONObject) JSON.toJSON(goldWithdrawDto);
            goldWithdrawDto.setSign(SignUntil.generateSign(jsonObject, appSecret));
            requestDto = goldWithdrawDto;

        } else {
            throw new RuntimeException("未知通道");
        }

        // 发送请求
        WithdrawResultDo withdrawResultDo = doRequest(url, requestDto, WithdrawResultDo.class);
        Integer code = withdrawResultDo.getCode();
        String msg = withdrawResultDo.getMsg();
        if (code != HttpStatus.SUCCESS) {
            errorTelegramUtil.dealErrorMsg(logPrefix + " : " + msg);
            throw new MyPayException(MessagesUtils.get("bot.pay.TXYC"), msg);
        }

        WithdrawResultDo.Result resultDoData = withdrawResultDo.getData();
        if (resultDoData != null) {
            resultDoData.setUpOrderNo(resultDoData.getOrderNo());
            resultDoData.setUpInfo(JSON.toJSONString(withdrawResultDo));
        }
        return withdrawResultDo;
    }

    @Override
    public JSONObject callbackWithdrawParam(MerchantPay merchantPay, OrderLawWithdraw orderLawWithdraw) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("merchantOrderNo", orderLawWithdraw.getOrderNo());
        return jsonObject;
    }

    @Override
    public PayCallbackResult callbackWithdraw(MerchantPay merchantPay, JSONObject jsonObject) {
        try {
            String orderNo = jsonObject.getString("merchantOrderNo");
            log.info("Wallet支付提现回调 - 订单号: {}", orderNo);

            String payParam = merchantPay.getParamStr();
            // ===== 主动查单逻辑，二次确认提现订单状态 =====
            try {
                QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
                queryOrderInfo.setOrderNo(orderNo);
                queryOrderInfo.setPayParam(payParam);

                OrderInfoDo orderInfoDo = queryWithdrawOrder(queryOrderInfo);
                log.info("Wallet支付提现回调查单 - 返回结果: orderStatus={}",
                        orderInfoDo != null && orderInfoDo.getData() != null ? orderInfoDo.getData().getOrderStatus()
                                : "null");

                if (orderInfoDo == null || orderInfoDo.getData() == null) {
                    log.error("Wallet支付提现回调查单 - 查询结果为空");
                    return PayCallbackResult.failed("fail");
                }

                // 检查订单状态: orderStatus=1 表示成功
                if (orderInfoDo.getData().getOrderStatus() == 1) {
                    return PayCallbackResult.success(orderNo, "success");
                } else if (orderInfoDo.getData().getOrderStatus() == 2) {
                    return PayCallbackResult.builder().orderNo(orderNo).orderStatus(2).responseMsg("success")
                            .isSuccess(true).build();
                } else {
                    return PayCallbackResult.builder().orderNo(orderNo).orderStatus(0).responseMsg("success")
                            .isSuccess(false).build();
                }
            } catch (Exception e) {
                log.error("Wallet支付提现回调查单 - 异常: {}", e.getMessage(), e);
                return PayCallbackResult.failed("fail");
            }
        } catch (Exception e) {
            log.error("Wallet支付提现回调异常", e);
            return PayCallbackResult.failed("fail");
        }
    }

    @Override
    public JSONObject reverseCheckOrderWithdraw(MerchantPay merchantPay, Map<String, Object> requestMap) {
        return null;
    }

    // @Override
    // public WithdrawResultDo LWithdraw(WithdrawOrder withdrawOrder) {
    // String payParam = withdrawOrder.getPayParam();
    // List<WalletParams> walletParams = JSON.parseArray(payParam,
    // WalletParams.class);
    // String appKey = filterByApp(walletParams, "appKey");
    // String domainName = filterByApp(walletParams, "domainName");
    // String appSecret = filterByApp(walletParams, "appSecret");
    // long nowTime = System.currentTimeMillis();
    // MerchantWithdrawDto merchantWithdrawDto = new MerchantWithdrawDto();
    // BeanUtils.copyProperties(withdrawOrder, merchantWithdrawDto);
    // merchantWithdrawDto.setCurrencyId(MaskPayConst.currency_id);
    // merchantWithdrawDto.setAmount(withdrawOrder.getAmount());
    // merchantWithdrawDto.setTimestamp(nowTime);
    // merchantWithdrawDto.setAppKey(appKey);
    // JSONObject jsonObject = (JSONObject) JSON.toJSON(merchantWithdrawDto);
    // String sign = SignUntil.generateSign(jsonObject, appSecret);
    // merchantWithdrawDto.setSign(sign);
    // WithdrawResultDo withdrawResultDo = doRequest(domainName + withdrawUrl,
    // merchantWithdrawDto,
    // WithdrawResultDo.class);
    // Integer code = withdrawResultDo.getCode();
    // String msg = withdrawResultDo.getMsg();
    // if (code != HttpStatus.SUCCESS) {
    // errorTelegramUtil.dealErrorMsg("mpay提现异常" + msg);
    // throw new MyPayException(MessagesUtils.get("bot.pay.TXYC"), msg);
    // }
    // WithdrawResultDo.Result data = withdrawResultDo.getData();
    // data.setUpOrderNo(data.getOrderNo());
    // return withdrawResultDo;
    // }

    @Override
    public RechargeOrderInfoDo.RechargeOrderInfo queryRechargeOrder(QueryRechargeOrder queryRechargeOrder) {
        String payParam = queryRechargeOrder.getPayParam();
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);
        String appKey = filterByApp(walletParams, "appKey");
        String domainName = filterByApp(walletParams, "domainName");
        String appSecret = filterByApp(walletParams, "appSecret");
        long nowTime = System.currentTimeMillis();
        QueryRechargeOrderDto queryRechargeOrderDto = new QueryRechargeOrderDto();
        BeanUtils.copyProperties(queryRechargeOrder, queryRechargeOrderDto);
        queryRechargeOrderDto.setTimestamp(nowTime);
        queryRechargeOrderDto.setAppKey(appKey);
        JSONObject jsonObject = (JSONObject) JSON.toJSON(queryRechargeOrderDto);
        String sign = SignUntil.generateSign(jsonObject, appSecret);
        queryRechargeOrderDto.setSign(sign);
        RechargeOrderInfoDo rechargeOrderInfoDo = doRequest(domainName + queryRechargeUrl, queryRechargeOrderDto,
                RechargeOrderInfoDo.class);
        Integer code = rechargeOrderInfoDo.getCode();
        String msg = rechargeOrderInfoDo.getMsg();
        if (code != HttpStatus.SUCCESS) {
            errorTelegramUtil.dealErrorMsg("mpay查询商户充值订单失败: " + msg);
            throw new MyPayException(MessagesUtils.get("bot.pay.CXDDSB"), msg);
        }
        RechargeOrderInfoDo.RechargeOrderInfo data = rechargeOrderInfoDo.getData();
        return data;
    }

}
