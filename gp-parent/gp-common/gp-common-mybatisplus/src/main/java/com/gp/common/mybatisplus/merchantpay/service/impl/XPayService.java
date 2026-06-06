package com.gp.common.mybatisplus.merchantpay.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.common.core.log.ErrorTelegramUtil;

import com.common.core.merchantpay.xpay.XPayRequest;
import com.common.core.merchantpay.xpay.XPayResponse;
import com.common.core.prop.ProxyProp;
import com.gp.common.base.exception.MyPayException;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.mybatisplus.entity.MerchantPay;
import com.gp.common.mybatisplus.entity.OrderAmount;
import com.gp.common.mybatisplus.entity.OrderLawWithdraw;
import com.gp.common.mybatisplus.merchantpay.constants.MpayPayTypeAttribute;
import com.gp.common.mybatisplus.merchantpay.constants.PayMerchantCons;
import com.gp.common.mybatisplus.merchantpay.constants.USDTPayTypeAttribute;
import com.gp.common.mybatisplus.merchantpay.xpay.XPayCallbackHelper;
import com.gp.common.mybatisplus.merchantpay.constants.PayTypeCons;
import com.gp.common.mybatisplus.merchantpay.service.PayWService;
import com.gp.common.mybatisplus.entity.PayChannel;
import com.gp.common.mybatisplus.merchantpay.base.JumpUtil;
import com.gp.common.mybatisplus.pay.mpay.WalletCreateOrderResult;
import com.gp.common.mybatisplus.pay.mpay.WalletParams;
import com.gp.common.mybatisplus.pay.mpay.createAddress.CreateAddress;
import com.gp.common.mybatisplus.pay.mpay.order.*;
import com.gp.common.mybatisplus.pay.service.PayService;
import com.gp.common.mybatisplus.service.CurrencyService;
import com.common.datasource.util.CecuUtil;
import com.gp.common.mybatisplus.until.RequestUntil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;


/**
 * XPay 支付服务
 * 支持创建充值地址
 * @author axing
 */
@Slf4j
@Service
public class XPayService extends PayWService {

    @Resource
    private ErrorTelegramUtil errorTelegramUtil;


    // ========== 区块链 ==========
    private static final String CREATE_ADDRESS_URL = "/address/create";          // 创建充值地址
    private static final String QUERY_RECHARGE_STATUS_URL = "/recharge/status";  // 查询链上充值状态
    private static final String WITHDRAW_CREATE_URL = "/withdraw/create";        // 链上提现(冷钱包)
    private static final String QUERY_WITHDRAW_URL = "/withdraw/status";         // 查询链上提现状态

    // ========== 小程序 ==========
    private static final String CREATE_ORDER_URL = "/payment/create";            // 创建收款订单
    private static final String QUERY_STATUS_URL = "/payment/status";            // 查询收款订单状态
    private static final String TRANSFER_URL = "/transfer/create";               // TG内部转账提现

    // ========== 配置参数key ==========
    private static final String CONFIG_DOMAIN_NAME = "domainName";
    private static final String CONFIG_APP_ID = "appId";
    private static final String CONFIG_APP_SECRET = "appSecret";
    /** 支付方式：区块链冷钱包（直充） */
    public static final String USDT_BLACK = "usdt-black";
    /** 支付方式：商户充值（小程序） */
    public static final String USDT_MERCHANT = "usdt-merchant";

    /** 支付方式：区块链冷钱包（直充） 提现通道*/
    public static final String USDT_BLACK_WITHDRAW = "usdt-black-withdraw";
    /** 支付方式：商户充值（小程序） 提现通道*/
    public static final String USDT_MERCHANT_WITHDRAW = "usdt-merchant-withdraw";

    /** 支付类型 → 收款参数解析器，新增类型只需在此注册一行 */
    private static final Map<String, Function<JSONObject, Map<String, String>>> WITHDRAW_PARSERS = new HashMap<>();
    static {
        WITHDRAW_PARSERS.put(PayTypeCons.PAY_USDT, USDTPayTypeAttribute::parse);
        WITHDRAW_PARSERS.put(PayTypeCons.MPAY,  MpayPayTypeAttribute::parse);
    }

    // XPay 响应状态码
    private static final int SUCCESS_CODE = 0;
    // 请求超时时间（毫秒）
    private static final int REQUEST_TIMEOUT = 60000;
    // 默认订单过期时间（秒）
    private static final int DEFAULT_EXPIRE_SECONDS = 300;

    @Override
    protected void doRegister() {
        registerPayWService(CollUtil.newArrayList(
                PayMerchantCons.XPAY
        ));
    }

    @Override
    public JSONObject getNotifyRequestParam(HttpServletRequest request) {
        String jsonStr = "";
        try {
            Map<String, String> paramMap = RequestUntil.doParamCompatible(request);
            if (paramMap == null || paramMap.isEmpty()) {
                jsonStr = RequestUntil.readRequestBody(request);
            } else {
                jsonStr = JSON.toJSONString(paramMap);
            }
        } catch (Exception e) {
            log.error("XPayNotify param error", e);
        }
        return JSON.parseObject(jsonStr);
    }

    @Override
    public String queryMoney(MerchantPay merchantPay) {
        return "暂不支持";
    }

    /**
     * XPay 查询订单
     *
     * @param queryOrderInfo 查询订单请求参数
     * @return 订单信息
     */
    @Override
    public OrderInfoDo queryOrder(QueryOrderInfo queryOrderInfo) {
        try {
            XPayResponse statusResponse = queryPaymentStatus(
                queryOrderInfo.getOrderNo(), 
                queryOrderInfo.getPayParam()
            );

            OrderInfoDo orderInfoDo = new OrderInfoDo();
            OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
            
            orderInfo.setOrderNo(statusResponse.getRequest_no());
            orderInfo.setMerchantOrderNo(statusResponse.getRequest_no());
            orderInfo.setAmount(new java.math.BigDecimal(statusResponse.getAmount()));
            orderInfo.setActualAmount(new java.math.BigDecimal(statusResponse.getAmount()));

            // 转换订单状态
            // XPay: 1-待支付, 2-已完成, 3-已取消, 4-已过期
            // 系统: 0-待支付, 1-已支付, 2-已取消
            switch (statusResponse.getStatus()) {
                case 1:
                    orderInfo.setOrderStatus(0); // 待支付
                    break;
                case 2:
                    orderInfo.setOrderStatus(1); // 已支付
                    orderInfo.setPayTime(statusResponse.getPay_time());
                    break;
                case 3:
                case 4:
                    orderInfo.setOrderStatus(2); // 已取消/已过期
                    break;
                default:
                    orderInfo.setOrderStatus(2); // 未知状态当作已取消
                    break;
            }
            // 设置时间
            orderInfo.setCreateTime(statusResponse.getCreate_time());
            // 设置备注（如果有自定义ID）
            if (StrUtil.isNotBlank(statusResponse.getPid())) {
                orderInfo.setAttch(statusResponse.getPid());
            }
            
            orderInfoDo.setData(orderInfo);

            log.info("XPay查询订单: request_no={}, status={}, order_status={}",
                statusResponse.getRequest_no(), 
                statusResponse.getStatus(),
                orderInfo.getOrderStatus());
            
            return orderInfoDo;
            
        } catch (Exception e) {
            log.error("XPay查询订单异常: {}", e.getMessage(), e);
            OrderInfoDo errorResult = new OrderInfoDo();
            errorResult.setMsg(e.getMessage());
            return errorResult;
        }
    }

    /**
     * XPay 查询提现订单
     *
     * @param queryOrderInfo 查询订单请求参数
     * @return 订单信息
     */
    @Override
    public OrderInfoDo queryWithdrawOrder(QueryOrderInfo queryOrderInfo) {
        try {
            XPayResponse withdrawResponse = queryWithdrawStatus(
                queryOrderInfo.getOrderNo(), 
                queryOrderInfo.getPayParam()
            );

            OrderInfoDo orderInfoDo = new OrderInfoDo();
            OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
            
            orderInfo.setOrderNo(withdrawResponse.getWithdraw_no());
            orderInfo.setMerchantOrderNo(withdrawResponse.getWithdraw_no());
            orderInfo.setAmount(new java.math.BigDecimal(withdrawResponse.getAmount()));
            orderInfo.setActualAmount(new java.math.BigDecimal(withdrawResponse.getActual_amount()));

            // 转换订单状态
            // XPay: 1-待审核, 2-待打款, 3-已完成, 4-已拒绝, 5-已失败, 6-已取消
            // 系统: 0-待支付, 1-已支付, 2-已取消
            switch (withdrawResponse.getStatus()) {
                case 1:
                case 2:
                    orderInfo.setOrderStatus(0); // 待审核/待打款 -> 待支付
                    break;
                case 3:
                    orderInfo.setOrderStatus(1); // 已完成 -> 已支付
                    if (withdrawResponse.getStatus_time() != null) {
                        orderInfo.setPayTime(withdrawResponse.getStatus_time());
                    }
                    break;
                case 4:
                case 5:
                case 6:
                    orderInfo.setOrderStatus(2); // 已拒绝/已失败/已取消 -> 已取消
                    break;
                default:
                    orderInfo.setOrderStatus(2); // 未知状态当作已取消
                    break;
            }

            // 设置时间
            orderInfo.setCreateTime(withdrawResponse.getCreate_time());

            // 设置备注（失败/拒绝原因）
            if (StrUtil.isNotBlank(withdrawResponse.getReject_reason())) {
                orderInfo.setRemark(withdrawResponse.getReject_reason());
            } else if (StrUtil.isNotBlank(withdrawResponse.getFail_reason())) {
                orderInfo.setRemark(withdrawResponse.getFail_reason());
            } else if (StrUtil.isNotBlank(withdrawResponse.getRemark())) {
                orderInfo.setRemark(withdrawResponse.getRemark());
            }

            // 设置自定义ID
            if (StrUtil.isNotBlank(withdrawResponse.getPid())) {
                orderInfo.setAttch(withdrawResponse.getPid());
            }
            
            orderInfoDo.setData(orderInfo);

            log.info("XPay查询提现订单: withdraw_no={}, status={}, order_status={}",
                withdrawResponse.getWithdraw_no(), 
                withdrawResponse.getStatus(),
                orderInfo.getOrderStatus());
            
            return orderInfoDo;
            
        } catch (Exception e) {
            log.error("XPay查询提现订单异常: {}", e.getMessage(), e);
            OrderInfoDo errorResult = new OrderInfoDo();
            errorResult.setMsg(e.getMessage());
            return errorResult;
        }
    }
    
    /**
     * XPay 查询提现订单状态
     * API文档: POST /withdraw/status
     *
     * @param withdrawNo 提现单号或商户自定义ID
     * @param payParams 支付参数JSON字符串（包含配置信息）
     * @return 提现订单状态响应
     */
    public XPayResponse queryWithdrawStatus(String withdrawNo, String payParams) {
        try {
            // 解析并验证配置
            XPayConfig config = parseAndValidateConfig(payParams);
            XPayRequest request = new XPayRequest();
            request.setWithdraw_no(withdrawNo);

            String url = config.apiUrl + QUERY_WITHDRAW_URL;
            log.info("XPay查询提现订单状态请求: URL={}, withdraw_no/pid={}", url, withdrawNo);
            
            XPayResponse response = doXPayRequest(url, request, config.appId, config.appSecret);

            if (response == null) {
                throw new MyPayException(MessagesUtils.get("bot.pay.CXDDSB"), "XPay");
            }
            
            if (response.getCode() != SUCCESS_CODE) {
                String errorMsg = "XPay查询提现订单状态失败: " + response.getMessage();
                log.error(errorMsg);
                throw new MyPayException(MessagesUtils.get("bot.pay.CXDDSB"), response.getMessage());
            }
            
            log.info("XPay查询提现订单状态成功: withdraw_no={}, status={}, status_text={}",
                response.getWithdraw_no(),
                response.getStatus(),
                response.getStatus_text());
            
            return response;
            
        } catch (MyPayException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = "XPay查询提现订单状态异常: " + e.getMessage();
            log.error(errorMsg, e);
            errorTelegramUtil.dealErrorMsg(errorMsg);
            throw new MyPayException(MessagesUtils.get("bot.pay.CXDDSB"), e.getMessage());
        }
    }
    
    /**
     * 查询链上充值订单状态
     * 支持通过 recharge_no 或 pid 查询
     *
     * @param rechargeNo 充值单号或商户用户UID
     * @param payParams 支付参数（JSON格式）
     * @return 充值订单状态响应
     */
    public XPayResponse queryRechargeStatus(String rechargeNo, String payParams) {
        try {
            XPayConfig config = parseAndValidateConfig(payParams);
            XPayRequest request = new XPayRequest();
            request.setRecharge_no(rechargeNo);
            String url = config.apiUrl + QUERY_RECHARGE_STATUS_URL;
            log.info("XPay查询链上充值订单状态请求: URL={}, recharge_no/pid={}", url, rechargeNo);

            XPayResponse response = doXPayRequest(url, request, config.appId, config.appSecret);

            if (response == null) {
                throw new MyPayException(MessagesUtils.get("bot.pay.CXDDSB"), "XPay返回响应为空");
            }

            if (response.getCode() != SUCCESS_CODE) {
                String errorMsg = "XPay查询链上充值订单状态失败: " + response.getMessage();
                log.error(errorMsg);
                throw new MyPayException(MessagesUtils.get("bot.pay.CXDDSB"), response.getMessage());
            }

            log.info("XPay查询链上充值订单状态成功: recharge_no={}, status={}, status_text={}",
                response.getRecharge_no(),
                response.getStatus(),
                response.getStatus_text());

            return response;

        } catch (MyPayException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = "XPay查询链上充值订单状态异常: " + e.getMessage();
            log.error(errorMsg, e);
            errorTelegramUtil.dealErrorMsg(errorMsg);
            throw new MyPayException(MessagesUtils.get("bot.pay.CXDDSB"), e.getMessage());
        }
    }

    /**
     * XPay 创建充值订单
     * API文档: POST /payments/create
     *
     * @param createOrder 创建订单请求参数
     * @return 返回订单信息（包含支付链接）
     */
    @Override
    public WalletCreateOrderResult createOrder(CreateOrder createOrder) {
        try {
            String payParam = createOrder.getPayParam();
            PayChannel payChannel = createOrder.getPayChannel();
            XPayConfig config = parseAndValidateConfig(payParam);

            if (USDT_BLACK.equals(payChannel.getPayMethod())) {
                // 二维码地址模式：调 /address/create 获取链上充值地址，拼跳转URL
                String dbCode = CecuUtil.getDbCode();
                String address = PayService.initUserAndGetPayTokenXPay(
                        createOrder.getUserId(), payParam,
                        CurrencyService.usdtCurrencyAbs.getItemId(), dbCode);
                String jumpUrl = JumpUtil.doBlackJumpUrl(
                        createOrder.getDomain(), createOrder.getOriginalAmount(),
                        CurrencyService.usdtCurrencyAbs.getItemName(),
                        CurrencyService.usdtCurrencyAbs.getChainTag(), address);

                WalletCreateOrderResult result = new WalletCreateOrderResult();
                result.setUrl(jumpUrl);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("addr", address);
                jsonObject.put("url", jumpUrl);
                result.setUpInfo(jsonObject.toJSONString());
                return result;

            } else if (USDT_MERCHANT.equals(payChannel.getPayMethod())) {
                // 小程序模式：调 /payment/create 获取支付链接
                XPayRequest request = buildCreateOrderRequest(createOrder);
                String url = config.apiUrl + CREATE_ORDER_URL;
                log.info("XPay创建订单请求(小程序): URL={}, amount={}, pid={}",
                        url, request.getAmount(), request.getPid());
                XPayResponse response = doXPayOrderRequest(url, request, config.appId, config.appSecret);
                return buildOrderResult(response);

            } else {
                throw new RuntimeException("XPay通道不存在: payMethod=" + payChannel.getPayMethod());
            }
        } catch (MyPayException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = "XPay创建订单异常: " + e.getMessage();
            log.error(errorMsg, e);
            errorTelegramUtil.dealErrorMsg(errorMsg);
            throw new MyPayException(MessagesUtils.get("bot.pay.ZFYC"), e.getMessage());
        }
    }

    @Override
    public JSONObject callbackPayParam(MerchantPay merchantPay, OrderAmount orderAmount) {
        JSONObject jsonObject = new JSONObject();
        // 后台 checkStatus 走 callbackPay → queryPaymentStatus，必须使用 XPay 收款 request_no，
        // 不能传本系统 order_no（创建收款请求时 pid 可能与 request_no 不同，误传会 404「收款请求不存在」）
        jsonObject.put("orderNo", resolveXPayQueryRequestNo(orderAmount));
        return jsonObject;
    }

    /**
     * 从订单解析用于 XPay /payment/status 的 request_no：优先开户响应 JSON，其次 up_order_no，最后才 order_no。
     */
    private static String resolveXPayQueryRequestNo(OrderAmount orderAmount) {
        if (orderAmount == null) {
            return "";
        }
        if (StrUtil.isNotBlank(orderAmount.getUpPayInfo())) {
            try {
                JSONObject info = JSON.parseObject(orderAmount.getUpPayInfo());
                if (info != null) {
                    String r = info.getString("request_no");
                    if (StrUtil.isBlank(r)) {
                        r = info.getString("requestNo");
                    }
                    if (StrUtil.isNotBlank(r)) {
                        return r;
                    }
                }
            } catch (Exception e) {
                log.warn("XPay 解析 up_pay_info 取 request_no 失败: orderId={}, err={}", orderAmount.getId(),
                        e.getMessage());
            }
        }
        if (StrUtil.isNotBlank(orderAmount.getUpOrderNo())) {
            return orderAmount.getUpOrderNo();
        }
        return StrUtil.blankToDefault(orderAmount.getOrderNo(), "");
    }

    /**
     * 从法币提现单解析用于 XPay /withdraw/status 的 withdraw_no：优先上游下单响应 JSON，其次 up_order_no，最后 order_no。
     */
    private static String resolveXPayWithdrawQueryKey(OrderLawWithdraw w) {
        if (w == null) {
            return "";
        }
        if (StrUtil.isNotBlank(w.getUpOrderInfo())) {
            try {
                JSONObject info = JSON.parseObject(w.getUpOrderInfo());
                if (info != null) {
                    String r = info.getString("withdraw_no");
                    if (StrUtil.isBlank(r)) {
                        r = info.getString("withdrawNo");
                    }
                    if (StrUtil.isNotBlank(r)) {
                        return r;
                    }
                }
            } catch (Exception e) {
                log.warn("XPay 解析 up_order_info 取 withdraw_no 失败: id={}, err={}", w.getId(), e.getMessage());
            }
        }
        if (StrUtil.isNotBlank(w.getUpOrderNo())) {
            return w.getUpOrderNo();
        }
        return StrUtil.blankToDefault(w.getOrderNo(), "");
    }

    /**
     * 链上充值(order_type=1)、内部转账充值(order_type=5)：业务单号均为 order_id，
     * 主动查单统一走 {@link #queryRechargeStatus}（/open/recharge/status），与收款请求 /payment/status 不同。
     */
    private OrderInfoDo queryOrderInfoByRechargeOrderId(String rechargeOrderId, String payParam) {
        XPayResponse response = queryRechargeStatus(rechargeOrderId, payParam);
        OrderInfoDo orderInfoDo = new OrderInfoDo();
        OrderInfoDo.OrderInfo orderInfo = new OrderInfoDo.OrderInfo();
        orderInfo.setOrderNo(StrUtil.blankToDefault(response.getRecharge_no(), rechargeOrderId));
        orderInfo.setMerchantOrderNo(orderInfo.getOrderNo());
        if (StrUtil.isNotBlank(response.getAmount())) {
            orderInfo.setAmount(new BigDecimal(response.getAmount()));
            orderInfo.setActualAmount(orderInfo.getAmount());
        }
        // 与收款请求一致：2-已完成 → 系统已支付(1)
        Integer st = response.getStatus();
        if (st != null && st == 2) {
            orderInfo.setOrderStatus(1);
            orderInfo.setPayTime(response.getPay_time());
        } else {
            orderInfo.setOrderStatus(0);
        }
        if (StrUtil.isNotBlank(response.getPid())) {
            orderInfo.setAttch(response.getPid());
        }
        orderInfo.setCreateTime(response.getCreate_time());
        orderInfoDo.setData(orderInfo);
        return orderInfoDo;
    }

    private static final class PayCallbackVerifyContext {
        final OrderInfoDo orderInfoDo;
        final String merchantBizNo;

        PayCallbackVerifyContext(OrderInfoDo orderInfoDo, String merchantBizNo) {
            this.orderInfoDo = orderInfoDo;
            this.merchantBizNo = merchantBizNo;
        }
    }

    /**
     * 按回调类型选查单接口：1/5 → recharge/status；其它 → payment/status（收款 request_no 等）。
     */
    private PayCallbackVerifyContext loadPayCallbackVerifyContext(String payParam, JSONObject notify) throws Exception {
        if (notify == null) {
            log.error("XPay支付充值回调 - notify 为空");
            return null;
        }
        Integer orderType = XPayCallbackHelper.parseOrderType(notify);
        String orderId = notify.getString("order_id");
        if (XPayCallbackHelper.isRechargeStatusByOrderId(orderType, orderId)) {
            log.info("XPay支付充值回调 - 链上/内部充值查单 order_type={}, order_id={}", orderType, orderId);
            return new PayCallbackVerifyContext(queryOrderInfoByRechargeOrderId(orderId, payParam), orderId);
        }
        String payKey = XPayCallbackHelper.resolvePaymentStatusBusinessKey(notify);
        if (StrUtil.isBlank(payKey)) {
            log.error("XPay支付充值回调 - 缺少订单号字段: {}", notify);
            return null;
        }
        log.info("XPay支付充值回调 - 收款查单 key={}", payKey);
        QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
        queryOrderInfo.setOrderNo(payKey);
        queryOrderInfo.setPayParam(payParam);
        return new PayCallbackVerifyContext(queryOrder(queryOrderInfo), payKey);
    }

    @Override
    public PayCallbackResult callbackPay(MerchantPay merchantPay, JSONObject notifyData) {
        try {
            String payParam = merchantPay != null ? merchantPay.getParamStr() : null;
            PayCallbackVerifyContext ctx;
            try {
                ctx = loadPayCallbackVerifyContext(payParam, notifyData);
            } catch (Exception e) {
                log.error("XPay支付充值回调查单 - 异常: {}", e.getMessage(), e);
                return PayCallbackResult.failed("fail");
            }
            if (ctx == null) {
                return PayCallbackResult.failed("fail");
            }
            OrderInfoDo orderInfoDo = ctx.orderInfoDo;
            String merchantBizNo = ctx.merchantBizNo;

            log.info("XPay支付充值回调查单 - 返回结果: orderStatus={}",
                    orderInfoDo != null && orderInfoDo.getData() != null ? orderInfoDo.getData().getOrderStatus()
                            : "null");

            if (orderInfoDo == null || orderInfoDo.getData() == null
                    || orderInfoDo.getData().getOrderStatus() != 1) {
                log.error("XPay支付充值回调查单 - 订单未完成");
                return PayCallbackResult.failed("fail");
            }

            String sysOrderNo = orderInfoDo.getData().getAttch();
            if (StrUtil.isBlank(sysOrderNo)) {
                sysOrderNo = merchantBizNo;
            }
            log.info("XPay支付充值回调 - 订单验证成功: merchantBizNo={}, sysOrderNo={}", merchantBizNo, sysOrderNo);
            PayCallbackResult result = PayCallbackResult.success(sysOrderNo, "success");
            result.setMerchantOrderNo(merchantBizNo);
            if (orderInfoDo.getData().getAmount() != null) {
                result.setAmount(orderInfoDo.getData().getAmount());
            }
            return result;

        } catch (Exception e) {
            log.error("XPay支付充值回调异常", e);
            return PayCallbackResult.failed("fail");
        }
    }

    /**
     * 构建创建订单请求对象
     */
    private XPayRequest buildCreateOrderRequest(CreateOrder createOrder) {
        XPayRequest request = new XPayRequest();
        request.setAmount(createOrder.getOriginalAmount().toPlainString());
        request.setCurrency_code("USDT");
        request.setProtocol_code("TRC-20");
        request.setPid(createOrder.getOrderNo());
        request.setExpire_seconds(DEFAULT_EXPIRE_SECONDS);
        request.setRemark(StrUtil.isNotBlank(createOrder.getRemark()) ? createOrder.getRemark() : "");
        return request;
    }
    
    /**
     * 构建订单返回结果
     */
    private WalletCreateOrderResult buildOrderResult(XPayResponse response) {
        if (response == null) {
            throw new MyPayException(MessagesUtils.get("bot.pay.ZFYC"), "XPay返回响应为空");
        }
        
        if (response.getCode() != SUCCESS_CODE) {
            String errorMsg = "XPay创建订单失败: " + response.getMessage();
            log.error(errorMsg);
            errorTelegramUtil.dealErrorMsg(errorMsg);
            throw new MyPayException(MessagesUtils.get("bot.pay.ZFYC"), response.getMessage());
        }
        
        log.info("XPay创建订单成功: request_no={}, payment_url={}, expire_time={}",
            response.getRequest_no(), 
            response.getPayment_url(),
            response.getExpire_time());
        WalletCreateOrderResult walletCreateOrderResult = new WalletCreateOrderResult();
        walletCreateOrderResult.setUpOrderNo(response.getRequest_no());
        walletCreateOrderResult.setUrl(response.getPayment_url());
        walletCreateOrderResult.setUpInfo(JSON.toJSONString(response));
        return walletCreateOrderResult;
    }
    
    /**
     * 构建转账请求
     */
    private XPayRequest buildTransferRequest(WithdrawOrder withdrawOrder) {
        XPayRequest request = new XPayRequest();
        request.setTg_id(withdrawOrder.getTgUserId());
        request.setTg_username(withdrawOrder.getUserTgUsername());
        request.setTg_first_name(withdrawOrder.getUserTgName());
        request.setAmount(withdrawOrder.getLawAmount().toPlainString());
        request.setCurrency_code("USDT");
        request.setProtocol_code("TRC-20");
        request.setPid(withdrawOrder.getOrderNo());
        request.setRemark("提现");
        return request;
    }

    /**
     * 构建提现返回结果
     */
    private WithdrawResultDo buildWithdrawResult(XPayResponse response, WithdrawOrder withdrawOrder) {
        WithdrawResultDo withdrawResultDo = new WithdrawResultDo();
        WithdrawResultDo.Result result = new WithdrawResultDo.Result();

        result.setOrderNo(withdrawOrder.getOrderNo());
        result.setUpOrderNo(response.getTransfer_no());
        result.setUpInfo(JSON.toJSONString(response));
        result.setAmount(withdrawOrder.getLawAmount());
        result.setCreateTime(System.currentTimeMillis() / 1000);

        // 设置到 withdrawResultDo
        withdrawResultDo.data = result;
        withdrawResultDo.setCode(response.getCode());
        withdrawResultDo.setMsg(response.getMessage());
        
        return withdrawResultDo;
    }
    
    /**
     * XPay 查询收款请求状态
     * API文档: POST /payment/status
     *
     * @param requestNo 收款请求号
     * @param payParams 支付参数JSON字符串（包含配置信息）
     * @return 订单状态响应
     */
    public XPayResponse queryPaymentStatus(String requestNo, String payParams) {
        try {
            XPayConfig config = parseAndValidateConfig(payParams);
            XPayRequest request = new XPayRequest();
            request.setRequest_no(requestNo);

            String url = config.apiUrl + QUERY_STATUS_URL;
            log.info("XPay查询订单状态请求: URL={}, request_no={}", url, requestNo);
            
            XPayResponse response = doXPayRequest(url, request, config.appId, config.appSecret);

            if (response == null) {
                throw new MyPayException(MessagesUtils.get("bot.pay.CXDDSB"), "XPay返回响应为空");
            }
            
            if (response.getCode() != SUCCESS_CODE) {
                String errorMsg = "XPay查询订单状态失败: " + response.getMessage();
                log.error(errorMsg);
                throw new MyPayException(MessagesUtils.get("bot.pay.CXDDSB"), response.getMessage());
            }
            
            log.info("XPay查询订单状态成功: request_no={}, status={}, status_text={}",
                response.getRequest_no(),
                response.getStatus(),
                response.getStatus_text());
            
            return response;
            
        } catch (MyPayException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = "XPay查询订单状态异常: " + e.getMessage();
            log.error(errorMsg, e);
            errorTelegramUtil.dealErrorMsg(errorMsg);
            throw new MyPayException(MessagesUtils.get("bot.pay.CXDDSB"), e.getMessage());
        }
    }

    /**
     * XPay 转账（提现）
     * API文档: POST /transfers/create
     *
     * @param withdrawOrder 提现订单信息
     * @return 转账结果
     */
    @Override
    public WithdrawResultDo withdraw(WithdrawOrder withdrawOrder) {
        try {
            // 从 withdrawParam(bindData) 中取 address
            Map<String, Object> withdrawParam = withdrawOrder.getWithdrawParam();
            if (MapUtil.isNotEmpty(withdrawParam)) {
                String addr = String.valueOf(withdrawParam.getOrDefault("address", ""));
                if (StrUtil.isNotBlank(addr)) {
                    withdrawOrder.setAddress(addr);
                }
            }
            // 验证提现地址
            if (StrUtil.isBlank(withdrawOrder.getAddress())) {
                String errorMsg = "XPay链上提现失败: 提现地址为空";
                log.error(errorMsg + ", orderNo={}", withdrawOrder.getOrderNo());
                throw new MyPayException(MessagesUtils.get("bot.pay.TXSB"), errorMsg);
            }

            // 解析并验证配置
            XPayConfig config = parseAndValidateConfig(withdrawOrder.getPayParam());
            XPayRequest request = buildLWithdrawRequest(withdrawOrder);

            String url = config.apiUrl + WITHDRAW_CREATE_URL;
            log.info("XPay链上提现请求: URL={}, to_address={}, amount={}, pid={}",
                url, request.getTo_address(), request.getAmount(), request.getPid());
            
            XPayResponse response = doXPayRequest(url, request, config.appId, config.appSecret);

            if (response == null) {
                throw new MyPayException(MessagesUtils.get("bot.pay.TXSB"), "XPay返回响应为空");
            }
            
            if (response.getCode() != SUCCESS_CODE) {
                String errorMsg = "XPay链上提现失败: " + response.getMessage();
                log.error(errorMsg);
                throw new MyPayException(MessagesUtils.get("bot.pay.TXSB"), response.getMessage());
            }

            WithdrawResultDo result = buildLWithdrawResult(response, withdrawOrder);
            
            log.info("XPay链上提现成功: withdraw_no={}, amount={}, fee_amount={}, actual_amount={}, status={}",
                response.getWithdraw_no(), 
                response.getAmount(),
                response.getFee_amount(),
                response.getActual_amount(),
                response.getStatus());
            
            return result;
            
        } catch (MyPayException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = "XPay链上提现异常: " + e.getMessage();
            log.error(errorMsg, e);
            errorTelegramUtil.dealErrorMsg(errorMsg);
            throw new MyPayException(MessagesUtils.get("bot.pay.TXSB"), e.getMessage());
        }
    }

    @Override
    public JSONObject callbackWithdrawParam(MerchantPay merchantPay, OrderLawWithdraw orderLawWithdraw) {
        JSONObject jsonObject = new JSONObject();
        // 后台 checkStatus → queryWithdrawStatus 须传 XPay withdraw_no，不能只用本系统 order_no（作 pid 时常为 LOW…）
        jsonObject.put("orderNo", resolveXPayWithdrawQueryKey(orderLawWithdraw));
        if (orderLawWithdraw != null && StrUtil.isNotBlank(orderLawWithdraw.getOrderNo())) {
            jsonObject.put("pid", orderLawWithdraw.getOrderNo());
        }
        return jsonObject;
    }

    /**
     * 查单成功后组装回调结果：orderNo=本系统提现单号，merchantOrderNo=XPay withdraw_no。
     */
    private PayCallbackResult buildWithdrawVerifyResult(OrderInfoDo.OrderInfo data, String queryKeyUsed,
            JSONObject notifyData) {
        String xpayWithdrawNo = data.getOrderNo();
        String internalNo = StrUtil.blankToDefault(data.getAttch(),
                notifyData != null ? notifyData.getString("pid") : null);
        if (StrUtil.isBlank(internalNo)) {
            internalNo = queryKeyUsed;
        }
        int st = data.getOrderStatus();
        if (st == 1) {
            PayCallbackResult r = PayCallbackResult.success(internalNo, "success");
            r.setMerchantOrderNo(xpayWithdrawNo);
            return r;
        }
        if (st == 2) {
            return PayCallbackResult.builder().orderNo(internalNo).merchantOrderNo(xpayWithdrawNo).orderStatus(2)
                    .responseMsg("success").isSuccess(true).build();
        }
        return PayCallbackResult.builder().orderNo(internalNo).merchantOrderNo(xpayWithdrawNo).orderStatus(0)
                .responseMsg("success").isSuccess(false).build();
    }

    @Override
    public PayCallbackResult callbackWithdraw(MerchantPay merchantPay, JSONObject notifyData) {
        try {
            // 兼容不同回调字段命名
            String orderNo = notifyData != null ? notifyData.getString("orderNo") : null;
            if (StrUtil.isBlank(orderNo) && notifyData != null) {
                orderNo = notifyData.getString("withdraw_no");
            }
            if (StrUtil.isBlank(orderNo) && notifyData != null) {
                orderNo = notifyData.getString("order_id");
            }

            if (StrUtil.isBlank(orderNo)) {
                log.error("XPay支付提现回调 - 缺少订单号字段: {}", notifyData);
                return PayCallbackResult.failed("fail");
            }

            log.info("XPay支付提现回调 - 订单号: {}", orderNo);

            String payParam = merchantPay != null ? merchantPay.getParamStr() : null;
            // ===== 主动查单逻辑，二次确认提现订单状态 =====
            try {
                QueryOrderInfo queryOrderInfo = new QueryOrderInfo();
                queryOrderInfo.setOrderNo(orderNo);
                queryOrderInfo.setPayParam(payParam);

                OrderInfoDo orderInfoDo = queryWithdrawOrder(queryOrderInfo);
                log.info("XPay支付提现回调查单 - 返回结果: orderStatus={}",
                        orderInfoDo != null && orderInfoDo.getData() != null ? orderInfoDo.getData().getOrderStatus()
                                : "null");

                if (orderInfoDo == null || orderInfoDo.getData() == null) {
                    log.error("XPay支付提现回调查单 - 查询结果为空");
                    return PayCallbackResult.failed("fail");
                }

                // processWithdrawCallback 按本系统 order_no 查单：须用 XPay 返回的 pid(attch)，勿把查单用的 withdraw_no 当系统单号
                return buildWithdrawVerifyResult(orderInfoDo.getData(), orderNo, notifyData);
            } catch (Exception e) {
                log.error("XPay支付提现回调查单 - 异常: {}", e.getMessage(), e);
                return PayCallbackResult.failed("fail");
            }
        } catch (Exception e) {
            log.error("XPay支付提现回调异常", e);
            return PayCallbackResult.failed("fail");
        }
    }

    @Override
    public JSONObject reverseCheckOrderWithdraw(MerchantPay merchantPay, Map<String, Object> requestMap) {
        return null;
    }

//    @Override
//    public WithdrawResultDo LWithdraw(WithdrawOrder withdrawOrder) {
//        try {
//            // 验证 tgUserId
//            if (withdrawOrder.getTgUserId() == null || withdrawOrder.getTgUserId() == 0) {
//                String errorMsg = "XPay转账失败: TG用户ID不能为空";
//                log.error(errorMsg + ", orderNo={}", withdrawOrder.getOrderNo());
//                throw new MyPayException(MessagesUtils.get("bot.pay.TXSB"), errorMsg);
//            }
//            XPayConfig config = parseAndValidateConfig(withdrawOrder.getPayParam());
//            XPayRequest request = buildTransferRequest(withdrawOrder);
//            String url = config.apiUrl + TRANSFER_URL;
//            log.info("XPay转账请求: URL={}, tg_id={}, amount={}, pid={}",
//                url, request.getTg_id(), request.getAmount(), request.getPid());
//            XPayResponse response = doXPayRequest(url, request, config.appId, config.appSecret);
//            if (response == null) {
//                throw new MyPayException(MessagesUtils.get("bot.pay.TXYC"), "XPay返回响应为空");
//            }
//            if (response.getCode() != SUCCESS_CODE) {
//                String errorMsg = "XPay转账失败: " + response.getMessage();
//                log.error(errorMsg);
//                throw new MyPayException(MessagesUtils.get("bot.pay.TXYC"), response.getMessage());
//            }
//            WithdrawResultDo result = buildWithdrawResult(response, withdrawOrder);
//            log.info("XPay转账成功: transfer_no={}, amount={}, to_tg_id={}, is_new_user={}",
//                response.getTransfer_no(), response.getAmount(),
//                response.getTo_tg_id(), response.getIs_new_user());
//            return result;
//        } catch (MyPayException e) {
//            throw e;
//        } catch (Exception e) {
//            String errorMsg = "XPay转账异常: " + e.getMessage();
//            log.error(errorMsg, e);
//            errorTelegramUtil.dealErrorMsg(errorMsg);
//            throw new MyPayException(MessagesUtils.get("bot.pay.TXYC"), e.getMessage());
//        }
//    }

        /**
         * 构建链上提现请求对象
         */
        private XPayRequest buildLWithdrawRequest(WithdrawOrder withdrawOrder) {
            XPayRequest request = new XPayRequest();
            // 协议ID，从withdrawParam解析，默认TRC20
            Integer protocolId = parseProtocolIdFromParam(withdrawOrder.getWithdrawParam());
            request.setProtocol_id(protocolId);
            // 币种代码
            request.setCurrency_code("USDT");
            // 提现目标地址
            request.setTo_address(withdrawOrder.getAddress());
            // 提现金额
            request.setAmount(withdrawOrder.getAmount().toPlainString());
            // 商户自定义ID
            request.setPid(withdrawOrder.getOrderNo());
            // 备注
            request.setRemark("User withdraw: " + withdrawOrder.getOrderNo());
            return request;
        }
        
        /**
         * 从withdrawParam解析协议ID
         * 默认返回1 (TRC20)
         */
        private Integer parseProtocolIdFromParam( Map<String, Object> withdrawParam) {
            if (!MapUtil.isEmpty(withdrawParam)) {
                return 1;
            }
            try {
//                List<WalletParams> params = JSON.parseArray(withdrawParam, WalletParams.class);
                List<WalletParams> params =
                        JSON.parseArray(JSON.toJSONString(withdrawParam), WalletParams.class);
                String protocolIdStr = filterByApp(params, "protocolId");
                if (StrUtil.isNotBlank(protocolIdStr)) {
                    return Integer.parseInt(protocolIdStr);
                }
            } catch (Exception e) {
                log.warn("解析protocolId失败，使用默认值1: {}", e.getMessage());
            }
            return 1;
        }
        
        /**
         * 构建链上提现结果
         */
    /**
     * 构建链上提现结果
     */
    private WithdrawResultDo buildLWithdrawResult(XPayResponse response, WithdrawOrder withdrawOrder) {
        WithdrawResultDo result = new WithdrawResultDo();
        WithdrawResultDo.Result data = new WithdrawResultDo.Result();

        // XPay返回的提现单号
        data.setUpOrderNo(response.getWithdraw_no());
        // 上游返回信息
        data.setUpInfo(JSON.toJSONString(response));
        // 原订单号
        data.setOrderNo(withdrawOrder.getOrderNo());

        result.setCode(0);
        result.setData(data);

        return result;
    }
    /**
     * 查询充值订单状态
     *
     * @param queryRechargeOrder 查询订单请求参数
     * @return 订单信息
     */
    @Override
    public RechargeOrderInfoDo.RechargeOrderInfo queryRechargeOrder(QueryRechargeOrder queryRechargeOrder) {
        try {
            if (queryRechargeOrder == null) {
                return null;
            }

            String payParam = queryRechargeOrder.getPayParam();

            // 优先使用上游单号；为空时用 clientUserId 当作 pid 查询（XPay支持 recharge_no 或 pid）
            String queryKey = queryRechargeOrder.getUpOrderNo();
            if (StrUtil.isBlank(queryKey)) {
                queryKey = queryRechargeOrder.getClientUserId();
            }
            if (StrUtil.isBlank(queryKey)) {
                throw new MyPayException(MessagesUtils.get("bot.pay.CXDDSB"), "缺少 upOrderNo/clientUserId");
            }

            XPayResponse response = queryRechargeStatus(queryKey, payParam);

            RechargeOrderInfoDo.RechargeOrderInfo info = new RechargeOrderInfoDo.RechargeOrderInfo();
            if (StrUtil.isNotBlank(response.getAmount())) {
                info.setAmount(new BigDecimal(response.getAmount()));
            }
            info.setChainTag(StrUtil.blankToDefault(response.getChain(), response.getProtocol_code()));
            info.setTxid(response.getTx_hash());
            info.setFromAddress(response.getFrom_address());
            info.setToAddress(response.getTo_address());
            info.setUpOrderNo(StrUtil.blankToDefault(response.getRecharge_no(), queryRechargeOrder.getUpOrderNo()));
            info.setCreateTime(response.getCreate_time());
            return info;
        } catch (MyPayException e) {
            throw e;
        } catch (Exception e) {
            log.error("XPay查询充值订单异常: {}", e.getMessage(), e);
            throw new MyPayException(MessagesUtils.get("bot.pay.CXDDSB"), e.getMessage());
        }
    }

    /**
     * XPay 创建充值地址
     * API文档: POST /addresses/create
     *
     * 签名算法:
     * sign = HMAC-SHA256(timestamp + app_id + nonce + body, app_secret)
     *
     * 请求头:
     * - X-App-ID: 应用ID（AppID）
     * - X-Timestamp: 请求时间戳（Unix秒），有效期5分钟
     * - X-Nonce: 16~64位随机字符串
     * - X-Sign: HMAC-SHA256 签名
     * - Content-Type: application/json
     *
     * @param createAddress 创建地址请求参数
     * @return 返回创建的地址
     */
    @Override
    public String createAddress(CreateAddress createAddress) {
        try {
            XPayConfig config = parseAndValidateConfig(createAddress.getPayParam());
            XPayRequest request = buildCreateAddressRequest(createAddress);

            String url = config.apiUrl + CREATE_ADDRESS_URL;
            log.info("XPay创建地址请求: URL={}, protocolId={}, pid={}",
                    url, request.getProtocol_id(), request.getPid());

            XPayResponse response = doXPayRequest(url, request, config.appId, config.appSecret);

            return extractAddressFromResponse(response);

        } catch (MyPayException e) {
            throw e;
        } catch (Exception e) {
            String errorMsg = "XPay创建地址异常: " + e.getMessage();
            log.error(errorMsg, e);
            errorTelegramUtil.dealErrorMsg(errorMsg);
            throw new MyPayException(MessagesUtils.get("bot.pay.CJDZYC"), e.getMessage());
        }
    }

    /**
     * 解析并验证XPay配置
     */
    private XPayConfig parseAndValidateConfig(String payParam) {
        List<WalletParams> walletParams = JSON.parseArray(payParam, WalletParams.class);

        String apiUrl = filterByApp(walletParams, CONFIG_DOMAIN_NAME);
        String appId = filterByApp(walletParams, CONFIG_APP_ID);
        String appSecret = filterByApp(walletParams, CONFIG_APP_SECRET);

        if (StrUtil.hasBlank(apiUrl, appId, appSecret)) {
            throw new MyPayException(
                    MessagesUtils.get("bot.pay.CJDZYC"),
                    "XPay配置参数缺失: 需要 domainName, appId, appSecret");
        }

        return new XPayConfig(apiUrl, appId, appSecret);
    }

    /**
     * 构建创建地址请求对象
     */
    private XPayRequest buildCreateAddressRequest(CreateAddress createAddress) {
        XPayRequest request = new XPayRequest();
        request.setProtocol_id(createAddress.getProtocolId());
        request.setPid(createAddress.getPid());
        request.setCount(1);
        return request;
    }

    /**
     * 从响应中提取地址信息
     */
    private String extractAddressFromResponse(XPayResponse response) {
        if (response == null) {
            throw new MyPayException(MessagesUtils.get("bot.pay.CJDZYC"), "XPay返回响应为空");
        }

        if (response.getCode() != SUCCESS_CODE) {
            String errorMsg = "XPay创建地址失败: " + response.getMessage();
            log.error(errorMsg);
            errorTelegramUtil.dealErrorMsg(errorMsg);
            throw new MyPayException(MessagesUtils.get("bot.pay.CJDZYC"), response.getMessage());
        }

        if (CollUtil.isEmpty(response.getAddresses())) {
            throw new MyPayException(MessagesUtils.get("bot.pay.CJDZYC"), "XPay未返回地址信息");
        }

        XPayResponse.AddressInfo addressInfo = response.getAddresses().get(0);
        log.info("XPay创建地址成功: address={}, protocol={}, pid={}",
                addressInfo.getAddress(),
                addressInfo.getProtocol_code(),
                addressInfo.getPid());

        return addressInfo.getAddress();
    }

    /**
     * XPay配置内部类
     */
    private static class XPayConfig {
        private final String apiUrl;
        private final String appId;
        private final String appSecret;

        XPayConfig(String apiUrl, String appId, String appSecret) {
            this.apiUrl = apiUrl;
            this.appId = appId;
            this.appSecret = appSecret;
        }
    }

    /**
     * 生成 XPay HMAC-SHA256 签名
     * 
     * 签名算法: sign = HMAC-SHA256(timestamp + app_id + body, app_secret)
     *
     * @param timestamp Unix时间戳（秒）
     * @param appId     应用ID
     * @param body      请求体JSON字符串
     * @param appSecret 应用密钥
     * @return HMAC-SHA256签名（小写hex字符串）
     */
    private String generateXPaySign(long timestamp, String appId, String nonce, String body, String appSecret) {
        String signData = timestamp + appId + nonce + body;
        String sign = SecureUtil.hmacSha256(appSecret).digestHex(signData);
        log.debug("XPay签名 - 原文: {}, 结果: {}", signData, sign);
        return sign;
    }

    /**
     * 执行 XPay HTTP 请求（创建地址）
     * 
     * 请求头说明:
     * - X-App-ID: 应用ID（AppID）
     * - X-Timestamp: 请求时间戳（Unix秒），有效期5分钟
     * - X-Sign: HMAC-SHA256 签名
     * - Content-Type: application/json
     * 
     * @param url       请求URL
     * @param request   请求对象
     * @param appId     应用ID
     * @param appSecret 应用密钥
     * @return 响应对象
     */
    private XPayResponse doXPayRequest(String url, Object request, String appId, String appSecret) {
        String result = executeXPayRequest(url, request, appId, appSecret);
        return JSON.parseObject(result, XPayResponse.class);
    }

    /**
     * 执行 XPay HTTP 请求（创建订单）
     *
     * @param url       请求URL
     * @param request   请求对象
     * @param appId     应用ID
     * @param appSecret 应用密钥
     * @return 响应对象
     */
    private XPayResponse doXPayOrderRequest(String url, Object request, String appId, String appSecret) {
        String result = executeXPayRequest(url, request, appId, appSecret);
        return JSON.parseObject(result, XPayResponse.class);
    }

    /**
     * 执行 XPay HTTP 请求（查询订单状态）
     *
     * @param url       请求URL
     * @param request   请求对象
     * @param appId     应用ID
     * @param appSecret 应用密钥
     * @return 响应对象
     */
    private String executeXPayRequest(String url, Object request, String appId, String appSecret) {
        String result = null;
        try {
            // 获取代理配置
            ProxyProp proxyProp = SpringUtil.getBean(ProxyProp.class);

            // 生成时间戳（Unix秒）
            long timestamp = System.currentTimeMillis() / 1000;

            // 生成随机 Nonce (32位)
            String nonce = IdUtil.fastSimpleUUID();

            // 序列化请求体
            String body = JSON.toJSONString(request);

            // 生成签名
            String sign = generateXPaySign(timestamp, appId, nonce, body, appSecret);

            log.info("XPay请求 - Timestamp: {}, Nonce: {}, Body: {}", timestamp, nonce, body);

            // 构建并配置HTTP请求
            HttpRequest httpRequest = buildHttpRequest(url, appId, timestamp, nonce, sign, body, proxyProp);

            // 执行请求
            result = httpRequest.execute().body();
            log.info("XPay响应结果: {}", result);

            return result;

        } catch (HttpException e) {
            String errorMsg = String.format("XPay请求失败 - URL: %s, Error: %s, Response: %s",
                    url, e.getMessage(), result);
            log.error(errorMsg, e);
            throw new MyPayException(MessagesUtils.get("bot.pay.CJDZYC"), errorMsg);
        }
    }

    /**
     * 构建HTTP请求
     */
    private HttpRequest buildHttpRequest(String url, String appId, long timestamp, String nonce,
            String sign, String body, ProxyProp proxyProp) {
        HttpRequest httpRequest = HttpRequest.post(url)
                .header("Content-Type", "application/json")
                .header("X-App-ID", appId)
                .header("X-Timestamp", String.valueOf(timestamp))
                .header("X-Nonce", nonce)
                .header("X-Sign", sign)
                .body(body)
                .timeout(REQUEST_TIMEOUT);

        // 配置代理
        if (StrUtil.isNotBlank(proxyProp.getHost())) {
            httpRequest.setProxy(new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(proxyProp.getHost(), proxyProp.getPort())));
        }

        return httpRequest;
    }
}