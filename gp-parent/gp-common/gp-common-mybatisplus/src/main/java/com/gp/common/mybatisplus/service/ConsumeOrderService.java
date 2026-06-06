package com.gp.common.mybatisplus.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.constant.OrderWithdrawOrderStatusConstants;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.config.RedisOnlineService;
import com.gp.common.mybatisplus.entity.*;
import com.gp.common.mybatisplus.mapper.*;
import com.gp.common.mybatisplus.mq.OrderEntity;
import com.gp.common.mybatisplus.mqService.ActivityMqService;
import com.gp.common.mybatisplus.mqService.AgentService;
import com.gp.common.mybatisplus.mqService.BrokerageService;
import com.gp.common.mybatisplus.mqService.WheelMqService;
import com.gp.common.mybatisplus.vo.ConsumOrderVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class ConsumeOrderService extends ServiceImpl<CountOrderMapper, CountOrder> {

    @Resource
    private OrderAmountService orderAmountService;

    @Resource
    private UserCountOrderService userCountOrderService;

    @Resource
    private ChannelCountOrderService channelCountOrderService;
    @Resource
    private RedisOnlineService redisOnlineService;

    @Resource
    private ChannelMapper channelMapper;

    @Resource
    private CountOrderService tCountOrderService;

    @Resource
    private OrderPersonService orderPersonService;

    @Resource
    private OrderWithdrawService orderWithdrawService;

    @Resource
    private UserWalletMapper userWalletMapper;


    @Resource
    private UserService userService;

    @Resource
    private OrderTermService orderTermService;

    @Resource
    private AmountChangeService tAmountChangeService;

    @Resource
    private OrderReceiveRebateService orderReceiveRebateService;

    @Resource
    private BrokerageService brokerageService;
    @Resource
    private AgentService agentService;

    @Resource
    private ActivityAwardReceiveService activityAwardReceiveService;

    @Resource
    private ActivityMqService activityMqService;

    @Resource
    private InviteRechargeRewardRecordService inviteRechargeRewardRecordService;

    @Resource
    private PlayWheelTermService playWheelTermService;

    @Resource
    private WheelMqService wheelMqService;

    @Resource
    private UserCountOrderMapper userCountOrderMapper;

    @Resource
    private ChannelCountOrderMapper channelCountOrderMapper;

    @Resource
    private CountOrderMapper countOrderMapper;

    @Resource
    private CurrencyService currencyService;

    @Resource
    private CountOrderServiceEx countOrderServiceEx;

    @Resource
    private OrderLawWithdrawService orderLawWithdrawService;

    @Resource
    private OrderRedEnvelopeSendService orderRedEnvelopeSendService;
    @Resource
    private OrderRedEnvelopeReceiveService orderRedEnvelopeReceiveService;
    @Resource
    private UserCountGameCodeService userCountGameCodeService;
    @Resource
    private ChannelCountGameCodeService channelCountGameCodeService;
    @Resource
    private UserCountGameCodeMapper userCountGameCodeMapper;
    @Resource
    private ChannelCountGameCodeMapper channelCountGameCodeMapper;
    @Resource
    private OrderRedReceiveService orderRedReceiveService;
    @Resource
    private UserWalletService userWalletService;
    /*****
     * 用户自动充值
     * @param orderEntity
     */
    public void orderAuto(OrderEntity orderEntity) {
        try {
            //订单未支付不做处理
            OrderAmount orderAmount = this.getOrderAmountByOrderNoAndStatus(orderEntity.getOrderNo(), 1);
            log.info("用户自动充值-> uuid={}, 当前订单信息={}", orderEntity.getUuid(), orderEntity.getOrderNo());
            orderEntity.setBonusAmount(Optional.ofNullable(orderEntity.getBonusAmount()).orElse(BigDecimal.ZERO));
            //当前账户余额
            ConsumOrderVO amount = getAmount(orderEntity);
            if (Objects.isNull(orderAmount)) {
                //消息已消费
                log.info("用户自动充值 -> uuid={}, 当前订单号:{} 订单数据为null!", orderEntity.getUuid(), orderEntity.getOrderNo());
                return;
            }

            String day = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, new Date());

            Currency currency = currencyService.getById(orderEntity.getCurrencyId());
            // -----------------------------  t_user_count_order  操作 -----------------------------------------
            //订单支付成功--->1,判断充值方式type 0 直冲 1 mpay 2.pix 3 pix越南盾 4.upay 5,pay1818
            //查询t_user_count_order 是否有数据 有 累加 否 添加
            UserCountOrder userCountOrder = this.getUserCountOrder(orderEntity);
            log.info("用户自动充值-->uuid={}, 当前订单信息={}", orderEntity.getUuid(), Objects.isNull(userCountOrder) ? null : userCountOrder.toString());
            if (Objects.isNull(userCountOrder)) {
                userCountOrder = new UserCountOrder();
                userCountOrder.setUserId(orderEntity.getUserId());//用户id
                userCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                userCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                userCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    userCountOrder.setItemId(currency.getItemId());
                    userCountOrder.setChainTag(currency.getChainTag());
                }
                userCountOrder.setRechargeAmount(orderEntity.getMoney());//充值金额-当日
                userCountOrder.setRechargeNum(1);//充值订单数-当日
                userCountOrder.setBonusAmount(orderEntity.getBonusAmount());//彩金金额
                userCountOrder.setUserAmount(amount.getUserAmount());// 查用户余额  用户结余-当日
                userCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//用户收入
                userCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                userCountOrder.setCalculationsTime(new Date());// 当前时间 new data
                userCountOrder.setCreateTime(new Date());
                userCountOrder.setUpdateTime(new Date());
                int result = userCountOrderMapper.saveUserCountOrderData(userCountOrder);
                log.info("用户自动充值->uuid={},添加用户数据: {} 结果: {}", orderEntity.getUuid(), userCountOrder, result);
            } else {
                UserCountOrder updateUserCountOrder = new UserCountOrder();
                updateUserCountOrder.setId(userCountOrder.getId());
                updateUserCountOrder.setRechargeAmount(orderEntity.getMoney());//充值金额-当日
                updateUserCountOrder.setRechargeNum(1);//充值订单数-当日
                updateUserCountOrder.setBonusAmount(orderEntity.getBonusAmount());//彩金金额
                updateUserCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                updateUserCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//用户收入
                updateUserCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                updateUserCountOrder.setCalculationsTime(new Date());//统计计算时间
                updateUserCountOrder.setUpdateTime(new Date());//统计计算时间
                int result = userCountOrderMapper.updateUserCountOrderData(updateUserCountOrder);
                log.info("uuid={}, 用户自动充值->更新用户数据={} 更新结果={}", orderEntity.getUuid(), updateUserCountOrder, result);
            }
            // -----------------------------  t_channel_count_order  操作 -----------------------------------------
            //查询t_channel_count_order 渠道 是否有数据 有 累加 否 添加
            ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
            log.info("uuid={},用户自动充值-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(channelCountOrder) ? null : channelCountOrder.toString());
            if (channelCountOrder == null) {
                channelCountOrder = new ChannelCountOrder();
                channelCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                channelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                channelCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                channelCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    channelCountOrder.setItemId(currency.getItemId());
                    channelCountOrder.setChainTag(currency.getChainTag());
                }
                channelCountOrder.setCreateTime(new Date());//操作时间
                channelCountOrder.setUpdateTime(new Date());//操作时间
                channelCountOrder.setRechargeAmount(orderEntity.getMoney());//充值金额-当日
                channelCountOrder.setRechargeNum(1);//充值订单数-当日
                channelCountOrder.setBonusAmount(orderEntity.getBonusAmount());//彩金金额
                channelCountOrder.setUserAmount(amount.getChannelAmount());//todo查用户余额  结余-当日
                channelCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//todo用户收入 = 金额+彩金
                channelCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                channelCountOrder.setCalculationsTime(new Date());//操作时间
                channelCountOrder.setRechargePeopleNum(countOrderServiceEx.getPeopleNumByChannelId(orderEntity.getNowDay(), orderEntity.getChannelId(), orderEntity.getCurrencyId()));//充值人数
                countOrderServiceEx.findFirstNumOrMoneyByChannelCountOrder(orderEntity.getUuid(), day, orderEntity.getCurrencyId(), orderEntity.getChannelId(), channelCountOrder);
                int result = channelCountOrderMapper.insertChannelCountOrderDataV2(channelCountOrder);
                log.info("uuid={},用户自动充值->添加用户数据: {} 结果: {}", orderEntity.getUuid(), channelCountOrder, result);
            } else {
                ChannelCountOrder updateChannelCountOrder = new ChannelCountOrder();
                updateChannelCountOrder.setId(channelCountOrder.getId());
                updateChannelCountOrder.setRechargeAmount(orderEntity.getMoney());//充值金额-当日
                updateChannelCountOrder.setRechargeNum(1);//充值订单数-当日
                updateChannelCountOrder.setBonusAmount(orderEntity.getBonusAmount());// 彩金金额
                updateChannelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
                updateChannelCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//用户收入
                updateChannelCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());//客损
                updateChannelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                updateChannelCountOrder.setRechargePeopleNum(countOrderServiceEx.getPeopleNumByChannelId(orderEntity.getNowDay(), orderEntity.getChannelId(), orderEntity.getCurrencyId()));//充值人数
                countOrderServiceEx.findFirstNumOrMoneyByChannelCountOrder(orderEntity.getUuid(), day, orderEntity.getCurrencyId(), orderEntity.getChannelId(), updateChannelCountOrder);
                updateChannelCountOrder.setCalculationsTime(new Date());//统计计算时间
                updateChannelCountOrder.setUpdateTime(new Date());//操作时间
                int result = channelCountOrderMapper.updateChannelCountOrderDataV2(updateChannelCountOrder);
                log.info("uuid={},用户自动充值->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateChannelCountOrder, result);
            }

            // -----------------------------  t_count_order  操作 -----------------------------------------
            CountOrder countOrder = this.getCountOrder(orderEntity);
            log.info("uuid={},用户自动充值-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(countOrder) ? null : countOrder.toString());
            if (countOrder == null) {
                countOrder = new CountOrder();
                countOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                countOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    countOrder.setItemId(currency.getItemId());
                    countOrder.setChainTag(currency.getChainTag());
                }
                countOrder.setCreateTime(new Date());//操作时间
                countOrder.setUpdateTime(new Date());//操作时间
                countOrder.setRechargeAmount(orderEntity.getMoney());//充值金额-当日
                countOrder.setRechargeNum(1);//充值订单数-当日
                countOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                countOrder.setBonusAmount(orderEntity.getBonusAmount());//彩金金额
                countOrderServiceEx.findFirstNumOrMoneyByCountOrder(orderEntity.getUuid(), day, orderEntity.getCurrencyId(), countOrder);
                countOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                countOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//用户收入
                countOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                countOrder.setCalculationsTime(new Date());//操作时间
                countOrder.setRechargePeopleNum(countOrderServiceEx.getPeopleNum(orderEntity.getNowDay(), orderEntity.getCurrencyId()));//充值人数

                int result = countOrderMapper.insertCountOrderDataV2(countOrder);
                log.info("uuid={},用户自动充值->添加用户数据: {} 结果: {}", orderEntity.getUuid(), countOrder, result);
            } else {
                CountOrder updateCountOrder = new CountOrder();
                updateCountOrder.setId(countOrder.getId());
                updateCountOrder.setRechargeAmount(orderEntity.getMoney());//充值金额-当日
                updateCountOrder.setRechargeNum(1);//充值订单数-当日
                updateCountOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                updateCountOrder.setBonusAmount(orderEntity.getBonusAmount());//彩金金额
                countOrderServiceEx.findFirstNumOrMoneyByCountOrder(orderEntity.getUuid(), day, orderEntity.getCurrencyId(), updateCountOrder);
                updateCountOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                updateCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//用户收入
                updateCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                updateCountOrder.setCalculationsTime(new Date());//操作时间
                updateCountOrder.setRechargePeopleNum(countOrderServiceEx.getPeopleNum(orderEntity.getNowDay(), orderEntity.getCurrencyId()));//充值人数 // TODO: 2024/6/2  充值人数
                int result = countOrderMapper.updateCountOrderDataV2(updateCountOrder);
                log.info("uuid={},用户自动充值->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateCountOrder, result);
            }
        } catch (Exception e) {
            log.error("orderAuto -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
            throw e;
        }
    }

    /*****
     * 后台手动上分
     * @param orderEntity
     *
     */
    public void topScore(OrderEntity orderEntity) {
        try {
            //当前账户余额
            ConsumOrderVO amount = getAmount(orderEntity);
            //查询订单类型  order_type (1 上分, 2 下分)
            LambdaQueryWrapper<OrderPerson> queryWrapper = Wrappers.lambdaQuery(OrderPerson.class).eq(OrderPerson::getOrderNo, orderEntity.getOrderNo()).eq(OrderPerson::getUserId,
                    orderEntity.getUserId()).eq(OrderPerson::getOrderType, 1).eq(OrderPerson::getCurrencyId, orderEntity.getCurrencyId());
            OrderPerson orderPerson = orderPersonService.getOne(queryWrapper);
            orderEntity.setBonusAmount(Optional.ofNullable(orderEntity.getBonusAmount()).orElse(BigDecimal.ZERO));
            log.info("uuid={}, 后台人工上分-->当前订单信息: {}", orderEntity.getUuid(), Objects.isNull(orderPerson) ? null : orderPerson.toString());
            if (Objects.isNull(orderPerson)) {
                log.info("后台人工上分 -> uuid={}, 当前订单号:{} 订单为null!", orderEntity.getUuid(), orderEntity.getOrderNo());
                return;
            }

            String day = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, new Date());

            Currency currency = currencyService.getById(orderEntity.getCurrencyId());
            // -----------------------------  t_user_count_order  操作 -----------------------------------------
            UserCountOrder userCountOrder = this.getUserCountOrder(orderEntity);
            log.info("uuid={}, 后台人工上分--> 当前用户订单统计数据={}", orderEntity.getUuid(), Objects.isNull(userCountOrder) ? null : userCountOrder.toString());

            if (Objects.isNull(userCountOrder)) {
                userCountOrder = new UserCountOrder();
                userCountOrder.setUserId(orderEntity.getUserId());//用户id
                userCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                userCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                userCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    userCountOrder.setItemId(currency.getItemId());
                    userCountOrder.setChainTag(currency.getChainTag());
                }
                userCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                userCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//用户收入
                userCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                userCountOrder.setCalculationsTime(new Date());//操作时间
                userCountOrder.setCreateTime(new Date());
                userCountOrder.setUpdateTime(new Date());
                userCountOrder.setCustomerRechargeAmount(orderEntity.getMoney());//后台客服手动上分金额
                userCountOrder.setCustomerBonusAmount(orderEntity.getBonusAmount());//后台客服手动上分彩金金额
                if (orderEntity.getMoney().compareTo(BigDecimal.ZERO) > 0) { //充值金额大于0才算
                    userCountOrder.setCustomerRechargeNum(1);//后台客服手动上分订单数量
                }
                int result = userCountOrderMapper.saveUserCountOrderData(userCountOrder);
                log.info("uuid={}, 后台人工上分->添加用户数据: {} 结果: {}", orderEntity.getUuid(), userCountOrder, result);
            } else {
                UserCountOrder updateUserCountOrder = new UserCountOrder();
                updateUserCountOrder.setId(userCountOrder.getId());
                updateUserCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                updateUserCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//用户收入
                updateUserCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                updateUserCountOrder.setCalculationsTime(new Date());//操作时间
                updateUserCountOrder.setUpdateTime(new Date());//操作时间
                updateUserCountOrder.setCustomerRechargeAmount(orderEntity.getMoney());//后台客服手动上分金额
                updateUserCountOrder.setCustomerBonusAmount(orderEntity.getBonusAmount());//后台客服手动上分彩金金额
                if (orderEntity.getMoney().compareTo(BigDecimal.ZERO) > 0) { //充值金额大于0才算
                    updateUserCountOrder.setCustomerRechargeNum(1);//后台客服手动上分订单数量
                }
                int result = userCountOrderMapper.updateUserCountOrderData(updateUserCountOrder);
                log.info("uuid={}, 后台人工上分->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateUserCountOrder, result);
            }

            // -----------------------------  t_channel_count_order  操作 -----------------------------------------
            //查询t_channel_count_order 渠道 是否有数据 有 累加 否 添加
            ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
            log.info("uuid={}, 后台人工上分-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(channelCountOrder) ? null : channelCountOrder.toString());
            if (channelCountOrder == null) {
                channelCountOrder = new ChannelCountOrder();
                channelCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                channelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                channelCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                channelCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    channelCountOrder.setItemId(currency.getItemId());
                    channelCountOrder.setChainTag(currency.getChainTag());
                }
                channelCountOrder.setCreateTime(new Date());//操作时间
                channelCountOrder.setUpdateTime(new Date());//操作时间
                channelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
                channelCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//用户收入
                channelCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                channelCountOrder.setCustomerRechargeAmount(orderEntity.getMoney());//'后台客服手动上分金额'
                channelCountOrder.setCustomerBonusAmount(orderEntity.getBonusAmount());//'后台客服手动上分彩金金额'
                if (orderEntity.getMoney().compareTo(BigDecimal.ZERO) > 0) { //充值金额大于0才算
                    channelCountOrder.setCustomerRechargeNum(1);//''后台客服手动上分订单数量'
                    countOrderServiceEx.findFirstNumOrMoneyByChannelCountOrder(orderEntity.getUuid(), day, orderEntity.getCurrencyId(), orderEntity.getChannelId(), channelCountOrder);
                    channelCountOrder.setRechargePeopleNum(countOrderServiceEx.getPeopleNumByChannelId(orderEntity.getNowDay(), orderEntity.getChannelId(), orderEntity.getCurrencyId()));//'充值人数'

                }
                 channelCountOrder.setCalculationsTime(new Date());
                int result = channelCountOrderMapper.insertChannelCountOrderDataV2(channelCountOrder);
                log.info("uuid={}, 后台人工上分->添加渠道用户数据: {} 结果: {}", orderEntity.getUuid(), channelCountOrder, result);
            } else {
                ChannelCountOrder updateChannelCountOrder = new ChannelCountOrder();
                updateChannelCountOrder.setId(channelCountOrder.getId());
                updateChannelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                updateChannelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
                updateChannelCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//用户收入
                updateChannelCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());//客损
                updateChannelCountOrder.setCustomerRechargeAmount(orderEntity.getMoney());//'后台客服手动上分金额'
                updateChannelCountOrder.setCustomerBonusAmount(orderEntity.getBonusAmount());//'后台客服手动上分彩金金额'
                if (orderEntity.getMoney().compareTo(BigDecimal.ZERO) > 0) { //充值金额大于0才算
                    updateChannelCountOrder.setCustomerRechargeNum(1);//''后台客服手动上分订单数量''
                    countOrderServiceEx.findFirstNumOrMoneyByChannelCountOrder(orderEntity.getUuid(), day, orderEntity.getCurrencyId(), orderEntity.getChannelId(), updateChannelCountOrder);
                    updateChannelCountOrder.setRechargePeopleNum(countOrderServiceEx.getPeopleNumByChannelId(orderEntity.getNowDay(), orderEntity.getChannelId(), orderEntity.getCurrencyId()));//'充值人数'
                }
                updateChannelCountOrder.setCalculationsTime(new Date());
                updateChannelCountOrder.setUpdateTime(new Date());
                int result = channelCountOrderMapper.updateChannelCountOrderDataV2(updateChannelCountOrder);
                log.info("uuid={}, 后台人工上分->更新渠道用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateChannelCountOrder, result);
            }

            // -----------------------------  t_count_order  操作 -----------------------------------------
            CountOrder countOrder = this.getCountOrder(orderEntity);
            log.info("uuid={}, 后台人工上分-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(countOrder) ? null : countOrder.toString());
//            BigDecimal usdtBalance = userWalletService.countOrderAmount(orderEntity.getCurrencyId()).setScale(2, RoundingMode.DOWN);
            if (countOrder == null) {
                countOrder = new CountOrder();
                countOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                countOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    countOrder.setItemId(currency.getItemId());
                    countOrder.setChainTag(currency.getChainTag());
                }

                countOrder.setCreateTime(new Date());//操作时间
                countOrder.setUpdateTime(new Date());//操作时间
                countOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                countOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//用户收入
                countOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                countOrder.setCalculationsTime(new Date());//操作时间
//                countOrder.setUsdtBalance(usdtBalance);
                countOrder.setCustomerRechargeAmount(orderEntity.getMoney());//'后台客服手动上分金额'
                countOrder.setCustomerBonusAmount(orderEntity.getBonusAmount());//'后台客服手动上分彩金金额'
                if (orderEntity.getMoney().compareTo(BigDecimal.ZERO) > 0) { //充值金额大于0才算
                    countOrder.setCustomerRechargeNum(1);//''后台客服手动上分订单数量
                    countOrderServiceEx.findFirstNumOrMoneyByCountOrder(orderEntity.getUuid(), day, orderEntity.getCurrencyId(), countOrder);
                    countOrder.setRechargePeopleNum(countOrderServiceEx.getPeopleNum(orderEntity.getNowDay(), orderEntity.getCurrencyId()));//'充值人数' todo

                }
                countOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                int result = countOrderMapper.insertCountOrderDataV2(countOrder);
                log.info("uuid={}, 后台人工上分->添加用户数据: {} 结果: {}", orderEntity.getUuid(), countOrder, result);
            } else {
                CountOrder updateCountOrder = new CountOrder();

                updateCountOrder.setId(countOrder.getId());
                updateCountOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                updateCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//用户收入
                updateCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                updateCountOrder.setCalculationsTime(new Date());//操作时间
//                updateCountOrder.setUsdtBalance(usdtBalance);
                updateCountOrder.setCustomerRechargeAmount(orderEntity.getMoney());//'后台客服手动上分金额'
                updateCountOrder.setCustomerBonusAmount(orderEntity.getBonusAmount());//'后台客服手动上分彩金金额'
                if (orderEntity.getMoney().compareTo(BigDecimal.ZERO) > 0) { //充值金额大于0才算
                    updateCountOrder.setCustomerRechargeNum(1);//''后台客服手动上分订单数量''
                    countOrderServiceEx.findFirstNumOrMoneyByCountOrder(orderEntity.getUuid(), day, orderEntity.getCurrencyId(), updateCountOrder);
                    updateCountOrder.setRechargePeopleNum(countOrderServiceEx.getPeopleNum(orderEntity.getNowDay(), orderEntity.getCurrencyId()));//'充值人数'
                }
                updateCountOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                updateCountOrder.setUpdateTime(new Date());//操作时间
                int result = countOrderMapper.updateCountOrderDataV2(updateCountOrder);
                log.info("uuid={}, 后台人工上分->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateCountOrder, result);
            }
        } catch (Exception e) {
            log.error("topScore -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
            throw e;
        }
    }

    /*****
     * 用户自动提现
     * @param orderEntity
     */
    public void withdraw(OrderEntity orderEntity) {
        try {
            //当前账户余额
            ConsumOrderVO amount = getAmount(orderEntity);
            LambdaQueryWrapper<OrderWithdraw> queryWrapper = Wrappers.lambdaQuery(OrderWithdraw.class).eq(OrderWithdraw::getUserId, orderEntity.getUserId()).eq(OrderWithdraw::getOrderStatus,
                    OrderWithdrawOrderStatusConstants.WITHDRAWAL_SUCCESS).eq(OrderWithdraw::getOrderNo, orderEntity.getOrderNo()).eq(OrderWithdraw::getCurrencyId,
                    orderEntity.getCurrencyId());
            OrderWithdraw orderWithdraw = orderWithdrawService.getOne(queryWrapper);
            log.info("uuid={},用户提现订单-->当前订单信息: {}", orderEntity.getUuid(), Objects.isNull(orderWithdraw) ? null : orderWithdraw.toString());
            if (orderWithdraw == null) {
                log.info("uuid={},用户提现订单->当前订单号:{} 未体现成功!", orderEntity.getUuid(), orderEntity.getOrderNo());
                return;
            }
            Currency currency = currencyService.getById(orderEntity.getCurrencyId());
            // -----------------------------  t_channel_count_order  操作 -----------------------------------------
            //查询t_user_count_order 是否有数据 有 累加 否 添加
            UserCountOrder userCountOrder = this.getUserCountOrder(orderEntity);
            log.info("uuid={},用户提现订单-->当前订单信息: {}", orderEntity.getUuid(), Objects.isNull(userCountOrder) ? null : userCountOrder.toString());
            if (Objects.isNull(userCountOrder)) {
                userCountOrder = new UserCountOrder();
                userCountOrder.setUserId(orderEntity.getUserId());//用户id
                userCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                userCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                userCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    userCountOrder.setItemId(currency.getItemId());
                    userCountOrder.setChainTag(currency.getChainTag());
                }
                userCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                userCountOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                userCountOrder.setCalculationsTime(new Date());// 当前时间 new data
                userCountOrder.setCreateTime(new Date());
                userCountOrder.setUpdateTime(new Date());
                userCountOrder.setWithdrawAmount(orderEntity.getMoney());//'提现金额-当日'
                userCountOrder.setWithdrawNum(1);//'提现订单数-当日'
                userCountOrder.setRealAmount(orderWithdraw.getRealAmount());//''实际到账金额-当日'
                userCountOrder.setFee(orderWithdraw.getFee());//'手续费-当日',
                int result = userCountOrderMapper.saveUserCountOrderData(userCountOrder);
                log.info("uuid={},用户提现订单->添加用户数据: {} 结果: {}", orderEntity.getUuid(), userCountOrder, result);
            } else {
                UserCountOrder updateUserCountOrder = new UserCountOrder();
                updateUserCountOrder.setId(userCountOrder.getId());
                updateUserCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                updateUserCountOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                updateUserCountOrder.setCalculationsTime(new Date());// 当前时间 new data
                updateUserCountOrder.setUpdateTime(new Date());
                updateUserCountOrder.setWithdrawAmount(orderEntity.getMoney());//'提现金额-当日'
                updateUserCountOrder.setWithdrawNum(1);//'提现订单数-当日'
                updateUserCountOrder.setRealAmount(orderWithdraw.getRealAmount());//''实际到账金额-当日'
                updateUserCountOrder.setFee(orderWithdraw.getFee());//'手续费-当日'
                int result = userCountOrderMapper.updateUserCountOrderData(updateUserCountOrder);
                log.info("uuid={},用户提现订单->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateUserCountOrder, result);
            }

            // -----------------------------  t_channel_count_order  操作 -----------------------------------------
            //查询t_channel_count_order 渠道 是否有数据 有 累加 否 添加
            ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
            log.info("uuid={},用户提现订单-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(channelCountOrder) ? null : channelCountOrder.toString());
            if (channelCountOrder == null) {
                channelCountOrder = new ChannelCountOrder();
                channelCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                channelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                channelCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                channelCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    channelCountOrder.setItemId(currency.getItemId());
                    channelCountOrder.setChainTag(currency.getChainTag());
                }
                channelCountOrder.setCreateTime(new Date());//操作时间
                channelCountOrder.setUpdateTime(new Date());//操作时间
                channelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
                channelCountOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                channelCountOrder.setCalculationsTime(new Date());// 当前时间 new data
                channelCountOrder.setWithdrawAmount(orderEntity.getMoney());//'提现金额-当日'
                channelCountOrder.setWithdrawNum(1);//'提现订单数-当日'
                channelCountOrder.setRealAmount(orderWithdraw.getRealAmount());//''实际到账金额-当日'
                channelCountOrder.setFee(orderWithdraw.getFee());//'手续费-当日',
                channelCountOrder.setWithdrawalPeopleNum(countOrderServiceEx.getWithdrawPeopleNumByChannelId(orderEntity.getNowDay(), orderEntity.getChannelId(), orderEntity.getCurrencyId()));//提现人数,
                int result = channelCountOrderMapper.insertChannelCountOrderDataV2(channelCountOrder);
                log.info("uuid={},用户提现订单->添加用户数据: {} 结果: {}", orderEntity.getUuid(), channelCountOrder, result);
            } else {
                ChannelCountOrder updateChannelCountOrder = new ChannelCountOrder();
                updateChannelCountOrder.setId(channelCountOrder.getId());
                updateChannelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
                updateChannelCountOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                updateChannelCountOrder.setCalculationsTime(new Date());//当前时间 new data
                updateChannelCountOrder.setWithdrawAmount(orderEntity.getMoney());//'提现金额-当日'
                updateChannelCountOrder.setWithdrawNum(1);//'提现订单数-当日'
                updateChannelCountOrder.setRealAmount(orderWithdraw.getRealAmount());//''实际到账金额-当日'
                updateChannelCountOrder.setFee(orderWithdraw.getFee());//'手续费-当日'
                updateChannelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                updateChannelCountOrder.setWithdrawalPeopleNum(countOrderServiceEx.getWithdrawPeopleNumByChannelId(orderEntity.getNowDay(), orderEntity.getChannelId(), orderEntity.getCurrencyId()));
                int result = channelCountOrderMapper.updateChannelCountOrderDataV2(updateChannelCountOrder);
                log.info("uuid={},用户提现订单->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateChannelCountOrder, result);
            }
            // -----------------------------  t_count_order  操作 -----------------------------------------
            CountOrder countOrder = this.getCountOrder(orderEntity);
            log.info("uuid={},用户提现订单-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(countOrder) ? null : countOrder.toString());
            if (countOrder == null) {
                countOrder = new CountOrder();
                countOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                countOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    countOrder.setItemId(currency.getItemId());
                    countOrder.setChainTag(currency.getChainTag());
                }
                countOrder.setCreateTime(new Date());//操作时间
                countOrder.setUpdateTime(new Date());//操作时间
                countOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                countOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                countOrder.setCalculationsTime(new Date());// 当前时间 new data
                countOrder.setWithdrawAmount(orderEntity.getMoney());//'提现金额-当日'
                countOrder.setWithdrawNum(1);//'提现订单数-当日'
                countOrder.setRealAmount(orderWithdraw.getRealAmount());//''实际到账金额-当日'
                countOrder.setFee(orderWithdraw.getFee());//'手续费-当日',
                countOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                countOrder.setWithdrawalPeopleNum(countOrderServiceEx.getWithdrawPeopleNum(orderEntity.getNowDay(), orderEntity.getCurrencyId()));//todo'提现人数,
                int result = countOrderMapper.insertCountOrderDataV2(countOrder);
                log.info("uuid={},用户提现订单->添加用户数据: {} 结果: {}", orderEntity.getUuid(), countOrder, result);
            } else {
                CountOrder updateCountOrder = new CountOrder();
                updateCountOrder.setId(countOrder.getId());
                updateCountOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                updateCountOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                updateCountOrder.setCalculationsTime(new Date());// 当前时间 new data
                updateCountOrder.setWithdrawAmount(orderEntity.getMoney());//'提现金额-当日'
                updateCountOrder.setWithdrawNum(1);//'提现订单数-当日'
                updateCountOrder.setRealAmount(orderWithdraw.getRealAmount());//''实际到账金额-当日'
                updateCountOrder.setFee(orderWithdraw.getFee());//'手续费-当日'
                updateCountOrder.setWithdrawalPeopleNum(countOrderServiceEx.getWithdrawPeopleNum(orderEntity.getNowDay(), orderEntity.getCurrencyId()));
                updateCountOrder.setUpdateTime(new Date());//操作时间
                updateCountOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                int result = countOrderMapper.updateCountOrderDataV2(updateCountOrder);
                log.info("uuid={},用户提现订单->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateCountOrder, result);
            }
        } catch (Exception e) {
            log.error("withdraw -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
            throw e;
        }
    }

    /*****
     * 用户法币提现
     * @param orderEntity
     */
    public void lawWithdraw(OrderEntity orderEntity) {
        try {
            //当前账户余额
            ConsumOrderVO amount = getAmount(orderEntity);
            LambdaQueryWrapper<OrderLawWithdraw> queryWrapper =
                    Wrappers.lambdaQuery(OrderLawWithdraw.class).eq(OrderLawWithdraw::getUserId, orderEntity.getUserId()).eq(OrderLawWithdraw::getOrderStatus,
                            OrderWithdrawOrderStatusConstants.WITHDRAWAL_SUCCESS).eq(OrderLawWithdraw::getOrderNo, orderEntity.getOrderNo()).eq(OrderLawWithdraw::getCurrencyId,
                            orderEntity.getCurrencyId());
            OrderLawWithdraw orderWithdraw = orderLawWithdrawService.getOne(queryWrapper);
            log.info("uuid={},用户提现订单-->当前订单信息: {}", orderEntity.getUuid(), Objects.isNull(orderWithdraw) ? null : orderWithdraw.toString());
            if (orderWithdraw == null) {
                log.info("uuid={},用户提现订单->当前订单号:{} 未体现成功!", orderEntity.getUuid(), orderEntity.getOrderNo());
                return;
            }
            Currency currency = currencyService.getById(orderEntity.getCurrencyId());
            // -----------------------------  t_channel_count_order  操作 -----------------------------------------
            //查询t_user_count_order 是否有数据 有 累加 否 添加
            UserCountOrder userCountOrder = this.getUserCountOrder(orderEntity);
            log.info("uuid={},用户提现订单-->当前订单信息: {}", orderEntity.getUuid(), Objects.isNull(userCountOrder) ? null : userCountOrder.toString());

            if (Objects.isNull(userCountOrder)) {
                userCountOrder = new UserCountOrder();
                userCountOrder.setUserId(orderEntity.getUserId());//用户id
                userCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                userCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                userCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    userCountOrder.setItemId(currency.getItemId());
                    userCountOrder.setChainTag(currency.getChainTag());
                }
                userCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                userCountOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                userCountOrder.setCalculationsTime(new Date());// 当前时间 new data
                userCountOrder.setCreateTime(new Date());
                userCountOrder.setUpdateTime(new Date());
                //法币提现金额
                userCountOrder.setLawWithdrawAmount(orderEntity.getMoney());
                //法币提现订单数量
                userCountOrder.setLawWithdrawNum(1);
                int result = userCountOrderMapper.saveUserCountOrderData(userCountOrder);
                log.info("uuid={},用户提现订单->添加用户数据: {} 结果: {}", orderEntity.getUuid(), userCountOrder, result);
            } else {
                UserCountOrder updateUserCountOrder = new UserCountOrder();
                updateUserCountOrder.setId(userCountOrder.getId());
                updateUserCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                updateUserCountOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                updateUserCountOrder.setCalculationsTime(new Date());// 当前时间 new data
                updateUserCountOrder.setUpdateTime(new Date());
                //法币提现金额
                updateUserCountOrder.setLawWithdrawAmount(orderEntity.getMoney());
                //法币提现订单数量
                updateUserCountOrder.setLawWithdrawNum(1);
                int result = userCountOrderMapper.updateUserCountOrderData(updateUserCountOrder);
                log.info("uuid={},用户提现订单->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateUserCountOrder, result);
            }

            // -----------------------------  t_channel_count_order  操作 -----------------------------------------
            //查询t_channel_count_order 渠道 是否有数据 有 累加 否 添加
            ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
            log.info("uuid={},用户提现订单-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(channelCountOrder) ? null : channelCountOrder.toString());
            if (channelCountOrder == null) {
                channelCountOrder = new ChannelCountOrder();
                channelCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                channelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                channelCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                channelCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    channelCountOrder.setItemId(currency.getItemId());
                    channelCountOrder.setChainTag(currency.getChainTag());
                }
                channelCountOrder.setCreateTime(new Date());//操作时间
                channelCountOrder.setUpdateTime(new Date());//操作时间
                channelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
                channelCountOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                channelCountOrder.setCalculationsTime(new Date());// 当前时间 new data
                //法币提现金额
                channelCountOrder.setLawWithdrawAmount(orderEntity.getMoney());
                //法币提现订单数量
                channelCountOrder.setLawWithdrawNum(1);
                channelCountOrder.setWithdrawalPeopleNum(countOrderServiceEx.getWithdrawPeopleNumByChannelId(orderEntity.getNowDay(), orderEntity.getChannelId(), orderEntity.getCurrencyId()));//提现人数,
                int result = channelCountOrderMapper.insertChannelCountOrderDataV2(channelCountOrder);
                log.info("uuid={},用户提现订单->添加用户数据: {} 结果: {}", orderEntity.getUuid(), channelCountOrder, result);
            } else {
                ChannelCountOrder updateChannelCountOrder = new ChannelCountOrder();
                updateChannelCountOrder.setId(channelCountOrder.getId());
                updateChannelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
                updateChannelCountOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                updateChannelCountOrder.setCalculationsTime(new Date());//当前时间 new data
                updateChannelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                //法币提现金额
                updateChannelCountOrder.setLawWithdrawAmount(orderEntity.getMoney());
                //法币提现订单数量
                updateChannelCountOrder.setLawWithdrawNum(1);
                updateChannelCountOrder.setWithdrawalPeopleNum(countOrderServiceEx.getWithdrawPeopleNumByChannelId(orderEntity.getNowDay(), orderEntity.getChannelId(), orderEntity.getCurrencyId()));
                int result = channelCountOrderMapper.updateChannelCountOrderDataV2(updateChannelCountOrder);
                log.info("uuid={},用户提现订单->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateChannelCountOrder, result);
            }
            // -----------------------------  t_count_order  操作 -----------------------------------------
            CountOrder countOrder = this.getCountOrder(orderEntity);
            log.info("uuid={},用户提现订单-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(countOrder) ? null : countOrder.toString());
            if (countOrder == null) {
                countOrder = new CountOrder();
                countOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                countOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    countOrder.setItemId(currency.getItemId());
                    countOrder.setChainTag(currency.getChainTag());
                }
                countOrder.setCreateTime(new Date());//操作时间
                countOrder.setUpdateTime(new Date());//操作时间
                countOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                countOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                countOrder.setCalculationsTime(new Date());// 当前时间 new data
                //法币提现金额
                countOrder.setLawWithdrawAmount(orderEntity.getMoney());
                //法币提现订单数量
                countOrder.setLawWithdrawNum(1);
                countOrder.setWithdrawalPeopleNum(countOrderServiceEx.getWithdrawPeopleNum(orderEntity.getNowDay(), orderEntity.getCurrencyId()));//todo'提现人数,
                countOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                int result = countOrderMapper.insertCountOrderDataV2(countOrder);
                log.info("uuid={},用户提现订单->添加用户数据: {} 结果: {}", orderEntity.getUuid(), countOrder, result);
            } else {
                CountOrder updateCountOrder = new CountOrder();
                updateCountOrder.setId(countOrder.getId());
                updateCountOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                updateCountOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                updateCountOrder.setCalculationsTime(new Date());// 当前时间 new data
                //法币提现金额
                updateCountOrder.setLawWithdrawAmount(orderEntity.getMoney());
                //法币提现订单数量
                updateCountOrder.setLawWithdrawNum(1);
                updateCountOrder.setWithdrawalPeopleNum(countOrderServiceEx.getWithdrawPeopleNum(orderEntity.getNowDay(), orderEntity.getCurrencyId()));
                updateCountOrder.setUpdateTime(new Date());//操作时间
                updateCountOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                int result = countOrderMapper.updateCountOrderDataV2(updateCountOrder);
                log.info("uuid={},用户提现订单->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateCountOrder, result);
            }
        } catch (Exception e) {
            log.error("lawWithdraw -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
            throw e;
        }

    }

    /*****
     * 后台手动下分(人工下分)
     * @param orderEntity
     */
    public void belowMin(OrderEntity orderEntity) {
        try {
            //当前账户余额
            ConsumOrderVO amount = getAmount(orderEntity);
            //查询订单类型  order_type (1 上分, 2 下分)
            LambdaQueryWrapper<OrderPerson> queryWrapper = Wrappers.lambdaQuery(OrderPerson.class).eq(OrderPerson::getUserId, orderEntity.getUserId()).eq(OrderPerson::getOrderNo,
                    orderEntity.getOrderNo()).eq(OrderPerson::getOrderType, 2).eq(OrderPerson::getCurrencyId, orderEntity.getCurrencyId());
            OrderPerson orderPerson = orderPersonService.getOne(queryWrapper);
            log.info("uuid={}, 后台人工下分-->当前订单信息: {}", orderEntity.getUuid(), Objects.isNull(orderPerson) ? null : orderPerson.toString());
            if (orderPerson == null) {
                log.info("uuid={}, 后台人工下分->当前订单号:{} 不是下分订单", orderEntity.getUuid(), orderEntity.getOrderNo());
                return;
            }
            Currency currency = currencyService.getById(orderEntity.getCurrencyId());
            // -----------------------------  t_user_count_order  操作 -----------------------------------------
            UserCountOrder userCountOrder = this.getUserCountOrder(orderEntity);
            log.info("uuid={}, 后台人工下分-->当前订单信息: {}", orderEntity.getUuid(), Objects.isNull(userCountOrder) ? null : userCountOrder.toString());
            if (userCountOrder == null) {
                userCountOrder = new UserCountOrder();
                userCountOrder.setUserId(orderEntity.getUserId());//用户id
                userCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                userCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                userCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    userCountOrder.setItemId(currency.getItemId());
                    userCountOrder.setChainTag(currency.getChainTag());
                }
                userCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                userCountOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                userCountOrder.setCalculationsTime(new Date());//操作时间
                userCountOrder.setCreateTime(new Date());
                userCountOrder.setUpdateTime(new Date());
                if (orderPerson.getLowerSubtype() == 1) {//下分类型(1.真实提现;2.扣除积分)
                    //后台客服手动下分金额-下分类型是真实提现
                    userCountOrder.setCustomerRealCashWithdrawalAmount(orderEntity.getMoney());
                    //后台客服手动下分实际金额-下分类型是真实提现
                    userCountOrder.setCustomerRealCashWithdrawalRealAmount(orderEntity.getMoney());
                    //后台客服手动下分订单数量-下分类型是真实提现
                    userCountOrder.setCustomerRealCashWithdrawalNum(1);
                } else {
                    userCountOrder.setCustomerLossAmount(BigDecimal.ZERO.subtract(orderEntity.getMoney()));
                    //后台客服手动下分金额-下分类型是扣除积分
                    userCountOrder.setCustomerPointsDeductedAmount(orderEntity.getMoney());
                    userCountOrder.setCustomerPointsDeductedNum(1);//后台客服手动下分订单数量-下分类型是扣除积分
                }
                int result = userCountOrderMapper.saveUserCountOrderData(userCountOrder);
                log.info("uuid={}, 后台人工下分->添加用户数据: {} 结果: {}", orderEntity.getUuid(), userCountOrder, result);
            } else {
                UserCountOrder updateUserCountOrder = new UserCountOrder();
                updateUserCountOrder.setId(userCountOrder.getId());
                updateUserCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                updateUserCountOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                updateUserCountOrder.setCalculationsTime(new Date());//操作时间
                if (orderPerson.getLowerSubtype() == 1) {//下分类型(1.真实提现;2.扣除积分)
                    //后台客服手动下分金额-下分类型是真实提现 todo
                    updateUserCountOrder.setCustomerRealCashWithdrawalAmount(orderEntity.getMoney());//后台客服手动下分订单数量-下分类型是真实提现'
                    //后台客服手动下分实际金额-下分类型是真实提现 todo
                    updateUserCountOrder.setCustomerRealCashWithdrawalRealAmount(orderEntity.getMoney());
                    //后台客服手动下分订单数量-下分类型是真实提现
                    updateUserCountOrder.setCustomerRealCashWithdrawalNum(1);
                } else {
                    //后台客服手动下分金额-下分类型是扣除积分
                    updateUserCountOrder.setCustomerPointsDeductedAmount(orderEntity.getMoney());
                    updateUserCountOrder.setCustomerPointsDeductedNum(1);//后台客服手动下分订单数量-下分类型是扣除积分
                    updateUserCountOrder.setCustomerLossAmount(BigDecimal.ZERO.subtract(orderEntity.getMoney())); //注意这里要用负数
                }
                int result = userCountOrderMapper.updateUserCountOrderData(updateUserCountOrder);
                log.info("后台人工下分->更新用户数据: {} 更新结果: {}", updateUserCountOrder, result);
            }
            // -----------------------------  t_channel_count_order  操作 -----------------------------------------
            //查询t_channel_count_order 渠道 是否有数据 有 累加 否 添加
            ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
            log.info("uuid={}, 后台人工下分-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(channelCountOrder) ? null : channelCountOrder.toString());
            if (channelCountOrder == null) {
                channelCountOrder = new ChannelCountOrder();
                channelCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                channelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                channelCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                channelCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    channelCountOrder.setItemId(currency.getItemId());
                    channelCountOrder.setChainTag(currency.getChainTag());
                }
                channelCountOrder.setCreateTime(new Date());//操作时间
                channelCountOrder.setUpdateTime(new Date());//操作时间
                channelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
                channelCountOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                channelCountOrder.setCalculationsTime(new Date());//操作时间
                channelCountOrder.setWithdrawalPeopleNum(countOrderServiceEx.getWithdrawPeopleNumByChannelId(orderEntity.getNowDay(), orderEntity.getChannelId(), orderEntity.getCurrencyId()));
                if (orderPerson.getLowerSubtype() == 1) {//下分类型(1.真实提现;2.扣除积分)
                    //后台客服手动下分金额-下分类型是真实提现
                    channelCountOrder.setCustomerRealCashWithdrawalAmount(orderEntity.getMoney());//后台客服手动下分订单数量-下分类型是真实提现'
                    //后台客服手动下分实际金额-下分类型是真实提现
                    channelCountOrder.setCustomerRealCashWithdrawalRealAmount(orderEntity.getMoney());
                    ////后台客服手动下分订单数量-下分类型是真实提现
                    channelCountOrder.setCustomerRealCashWithdrawalNum(1);
                } else {
                    //后台客服手动下分金额-下分类型是扣除积分
                    channelCountOrder.setCustomerPointsDeductedAmount(orderEntity.getMoney());
                    channelCountOrder.setCustomerPointsDeductedNum(1);//后台客服手动下分订单数量-下分类型是扣除积分
                    channelCountOrder.setCustomerLossAmount(BigDecimal.ZERO.subtract(orderEntity.getMoney()));
                }
                int result = channelCountOrderMapper.insertChannelCountOrderDataV2(channelCountOrder);
                log.info("uuid={}, 后台人工下分->添加用户数据: {} 结果: {}", orderEntity.getUuid(), channelCountOrder, result);
            } else {
                ChannelCountOrder updateChannelCountOrder = new ChannelCountOrder();
                updateChannelCountOrder.setId(channelCountOrder.getId());
                updateChannelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
                updateChannelCountOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                updateChannelCountOrder.setCalculationsTime(new Date());//操作时间
                updateChannelCountOrder.setUpdateTime(new Date());//操作时间
                updateChannelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                updateChannelCountOrder.setWithdrawalPeopleNum(countOrderServiceEx.getWithdrawPeopleNumByChannelId(orderEntity.getNowDay(), orderEntity.getChannelId(), orderEntity.getCurrencyId()));
                if (orderPerson.getLowerSubtype() == 1) {//下分类型(1.真实提现;2.扣除积分)
                    updateChannelCountOrder.setCustomerRealCashWithdrawalAmount(orderEntity.getMoney());//后台客服手动下分订单数量-下分类型是真实提现'
                    updateChannelCountOrder.setCustomerRealCashWithdrawalRealAmount(orderEntity.getMoney());
                    updateChannelCountOrder.setCustomerRealCashWithdrawalNum(1);
                } else {
                    //后台客服手动下分金额-下分类型是扣除积分
                    updateChannelCountOrder.setCustomerPointsDeductedAmount(orderEntity.getMoney());
                    updateChannelCountOrder.setCustomerPointsDeductedNum(1);//后台客服手动下分订单数量-下分类型是扣除积分
                    updateChannelCountOrder.setCustomerLossAmount(BigDecimal.ZERO.subtract(orderEntity.getMoney()));//客损
                }
                int result = channelCountOrderMapper.updateChannelCountOrderDataV2(updateChannelCountOrder);
                log.info("uuid={}, 后台人工下分->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateChannelCountOrder, result);
            }
            // -----------------------------  t_count_order  操作 -----------------------------------------
            CountOrder countOrder = this.getCountOrder(orderEntity);
            log.info("uuid={}, 后台人工下分-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(countOrder) ? null : countOrder.toString());
            if (countOrder == null) {
                countOrder = new CountOrder();
                countOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                countOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    countOrder.setItemId(currency.getItemId());
                    countOrder.setChainTag(currency.getChainTag());
                }
                countOrder.setCreateTime(new Date());//操作时间
                countOrder.setUpdateTime(new Date());//操作时间
                countOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                countOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                countOrder.setCalculationsTime(new Date());//操作时间
                countOrder.setWithdrawalPeopleNum(countOrderServiceEx.getWithdrawPeopleNum(orderEntity.getNowDay(), orderEntity.getCurrencyId()));
                countOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                if (orderPerson.getLowerSubtype() == 1) {//下分类型(1.真实提现;2.扣除积分)
                    countOrder.setCustomerRealCashWithdrawalAmount(orderEntity.getMoney());//后台客服手动下分订单数量-下分类型是真实提现'
                    countOrder.setCustomerRealCashWithdrawalRealAmount(orderEntity.getMoney());
                    countOrder.setCustomerRealCashWithdrawalNum(1);
                } else {
                    countOrder.setCustomerPointsDeductedAmount(orderEntity.getMoney());
                    countOrder.setCustomerPointsDeductedNum(1);//后台客服手动下分订单数量-下分类型是扣除积分
                    countOrder.setCustomerLossAmount(BigDecimal.ZERO.subtract(orderEntity.getMoney()));
                }
                int result = countOrderMapper.insertCountOrderDataV2(countOrder);
                log.info("uuid={}, 后台人工下分->添加用户数据: {} 结果: {}", orderEntity.getUuid(), countOrder, result);
            } else {
                CountOrder updateCountOrder = new CountOrder();
                updateCountOrder.setId(countOrder.getId());
                updateCountOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                updateCountOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                updateCountOrder.setCalculationsTime(new Date());//操作时间
                updateCountOrder.setWithdrawalPeopleNum(countOrderServiceEx.getWithdrawPeopleNum(orderEntity.getNowDay(), orderEntity.getCurrencyId()));
                updateCountOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                if (orderPerson.getLowerSubtype() == 1) {//下分类型(1.真实提现;2.扣除积分)
                    updateCountOrder.setCustomerRealCashWithdrawalAmount(orderEntity.getMoney());//后台客服手动下分订单数量-下分类型是真实提现'
                    updateCountOrder.setCustomerRealCashWithdrawalRealAmount(orderEntity.getMoney());
                    updateCountOrder.setCustomerRealCashWithdrawalNum(1);
                } else {
                    updateCountOrder.setCustomerPointsDeductedAmount(orderEntity.getMoney());
                    updateCountOrder.setCustomerPointsDeductedNum(1);//后台客服手动下分订单数量-下分类型是扣除积分
                    updateCountOrder.setCustomerLossAmount(BigDecimal.ZERO.subtract(orderEntity.getMoney()));
                }
                updateCountOrder.setUpdateTime(new Date());//操作时间
                int result = countOrderMapper.updateCountOrderDataV2(updateCountOrder);
                log.info("uuid={}, 后台人工下分->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateCountOrder, result);
            }
        } catch (Exception e) {
            log.error("belowMin -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
            throw e;
        }
    }

    /****
     * 用户下注
     * @param orderEntity
     */
    public void botPour(OrderEntity orderEntity) {
        try {
            //当前账户余额
            ConsumOrderVO amount = getAmount(orderEntity);

            LambdaQueryWrapper<OrderTerm> orderTermQuery = Wrappers.lambdaQuery(OrderTerm.class);
            orderTermQuery.eq(OrderTerm::getOrderNo, orderEntity.getOrderNo());
            OrderTerm orderTerm = orderTermService.getOne(orderTermQuery);
            if (Objects.isNull(orderTerm)) {
                orderTerm = orderTermService.selectOrderTermByOrderNo(orderEntity.getOrderNo());
            }
            if (Objects.isNull(orderTerm)) {
                log.info("uuid={}, 订单号={},用户下注->注单数据不存在!", orderEntity.getUuid(), orderEntity.getOrderNo());
                return;
            }
            Currency currency = currencyService.getById(orderEntity.getCurrencyId());
            // -----------------------------  t_user_count_order  操作 -----------------------------------------
            //查询 t_user_count_order 是否有数据 有 累加 否 添加
            UserCountOrder userCountOrder = this.getUserCountOrder(orderEntity);
            log.info("uuid={}, 用户下注-->当前用户: {}", orderEntity.getUuid(), Objects.isNull(userCountOrder) ? null : userCountOrder.toString());
            if (userCountOrder == null) {
                userCountOrder = new UserCountOrder();
                userCountOrder.setUserId(orderEntity.getUserId());//用户id
                userCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                userCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                userCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    userCountOrder.setItemId(currency.getItemId());
                    userCountOrder.setChainTag(currency.getChainTag());
                }
                userCountOrder.setBetAmount(orderTerm.getBetAmount());//用户游戏投注金额(不分输赢)-当日
                userCountOrder.setBetNum(1);
                userCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                userCountOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                userCountOrder.setCalculationsTime(new Date());// 当前时间 new data
                userCountOrder.setCreateTime(new Date());
                userCountOrder.setUpdateTime(new Date());
                int result = userCountOrderMapper.saveUserCountOrderData(userCountOrder);
                log.info("uuid={}, 用户下注->添加用户数据: {} 结果: {}", orderEntity.getUuid(), userCountOrder, result);
            } else {
                UserCountOrder updateUserCountOrder = new UserCountOrder();
                updateUserCountOrder.setId(userCountOrder.getId());
                updateUserCountOrder.setBetAmount(orderTerm.getBetAmount());//用户游戏投注金额(不分输赢)-当日
                updateUserCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                updateUserCountOrder.setBetNum(1);
                updateUserCountOrder.setUserExpenses(orderTerm.getBetAmount());//用户支出
                updateUserCountOrder.setCalculationsTime(new Date());
                updateUserCountOrder.setUpdateTime(new Date());
                int result = userCountOrderMapper.updateUserCountOrderData(updateUserCountOrder);
                log.info("uuid={}, 用户下注->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), userCountOrder, result);
            }
            // -----------------------------  t_channel_count_order  操作 -----------------------------------------
            //查询t_channel_count_order 渠道 是否有数据 有 累加 否 添加
            ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
            log.info("uuid={}, 用户下注-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(channelCountOrder) ? null : channelCountOrder.toString());
            if (channelCountOrder == null) {
                channelCountOrder = new ChannelCountOrder();
                channelCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                channelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                channelCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                channelCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    channelCountOrder.setItemId(currency.getItemId());
                    channelCountOrder.setChainTag(currency.getChainTag());
                }
                channelCountOrder.setCreateTime(new Date());//操作时间
                channelCountOrder.setUpdateTime(new Date());//操作时间
                channelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
                channelCountOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                channelCountOrder.setCalculationsTime(new Date());// 当前时间 new data
                channelCountOrder.setBetAmount(orderTerm.getBetAmount());//用户游戏投注金额(不分输赢)-当日
                channelCountOrder.setBetNum(1);

//                countOrderServiceEx.processingRetainedDataByChannelCountOrderNew(orderEntity.getUuid(),orderEntity.getChannelId(), channelCountOrder, startTime, orderTerm);
                int result = channelCountOrderMapper.insertChannelCountOrderDataV2(channelCountOrder);
                log.info("uuid={}, 用户下注->添加用户数据: {} 结果: {}", orderEntity.getUuid(), channelCountOrder, result);
            } else {
                ChannelCountOrder updateChannelCountOrder = new ChannelCountOrder();
                updateChannelCountOrder.setId(channelCountOrder.getId());
                updateChannelCountOrder.setBetAmount(orderTerm.getBetAmount());//不查下注订单 用户游戏投注金额(不分输赢)-当日
                updateChannelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
                updateChannelCountOrder.setBetNum(1);
                updateChannelCountOrder.setUserExpenses(orderTerm.getBetAmount());//用户支出
                updateChannelCountOrder.setCalculationsTime(new Date());
                updateChannelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
//                countOrderServiceEx.processingRetainedDataByChannelCountOrderNew(orderEntity.getChannelId(), updateChannelCountOrder, startTime, orderTerm);
                updateChannelCountOrder.setUpdateTime(new Date());
                int result = channelCountOrderMapper.updateChannelCountOrderDataV2(updateChannelCountOrder);
                log.info("uuid={}, 用户下注->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), channelCountOrder, result);
            }
            // -----------------------------  t_count_order  操作 -----------------------------------------
            CountOrder countOrder = this.getCountOrder(orderEntity);
            log.info("uuid={}, 用户下注-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(countOrder) ? null : countOrder.toString());
            if (countOrder == null) {
                countOrder = new CountOrder();
                countOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                countOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    countOrder.setItemId(currency.getItemId());
                    countOrder.setChainTag(currency.getChainTag());
                }
                countOrder.setCreateTime(new Date());//操作时间
                countOrder.setUpdateTime(new Date());//操作时间
                countOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                countOrder.setCalculationsTime(new Date());// 当前时间 new data
                countOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                countOrder.setBetAmount(orderTerm.getBetAmount());//用户游戏投注金额(不分输赢)-当日
                countOrder.setBetNum(1);
                countOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
//                countOrderServiceEx.processingRetainedDataByCountOrderNew(orderEntity.getUuid(),countOrder, startTime, orderTerm);
                int result = countOrderMapper.insertCountOrderDataV2(countOrder);
                log.info("uuid={}, 用户下注->添加用户数据: {} 结果: {}", orderEntity.getUuid(), countOrder, result);
            } else {
                CountOrder updateCountOrder = new CountOrder();
                updateCountOrder.setId(countOrder.getId());
                updateCountOrder.setBetAmount(orderTerm.getBetAmount());//用户游戏投注金额(不分输赢)-当日
                updateCountOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                updateCountOrder.setBetNum(1);
                updateCountOrder.setUserExpenses(orderEntity.getMoney());//用户支出
                updateCountOrder.setCalculationsTime(new Date());
                updateCountOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
//                countOrderServiceEx.processingRetainedDataByCountOrderNew(updateCountOrder, startTime, orderTerm);
                updateCountOrder.setUpdateTime(new Date());//操作时间
                int result = countOrderMapper.updateCountOrderDataV2(updateCountOrder);
                log.info("uuid={}, 用户下注->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateCountOrder, result);
            }
        } catch (Exception e) {
            log.error("botPour -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
            throw e;
        }
    }

    /*****
     * 用户投注结算
     * @param orderEntity
     */
    public void settle(OrderEntity orderEntity) {
        try {
            log.info("uuid={}, 用户投注结算->参数!,订单号:{},用户id:{},渠道ID:{}",
                    orderEntity.getUuid(), orderEntity.getOrderNo(), orderEntity.getUserId(), orderEntity.getChannelId());
            //当前账户余额
            ConsumOrderVO amount = getAmount(orderEntity);
            //为了防止mysql删除了该订单,从doris中取
            LambdaQueryWrapper<OrderTerm> orderTermQuery = Wrappers.lambdaQuery(OrderTerm.class);
            orderTermQuery.eq(OrderTerm::getOrderNo, orderEntity.getOrderNo());
            OrderTerm orderTerm = orderTermService.getOne(orderTermQuery);
            if (Objects.isNull(orderTerm)) {
                orderTerm = orderTermService.selectOrderTermByOrderNo(orderEntity.getOrderNo());
            }

            if (Objects.isNull(orderTerm)) {
                log.info("uuid={}, 用户投注结算->注单数据不存在!,订单号:{}", orderEntity.getUuid(), orderEntity.getOrderNo());
                return;
            }
            //结算状态(0 未结算, 1 已结算)
            if (orderTerm.getSettleStatus() == 0 || Objects.isNull(orderTerm.getSettleTime())) {
                log.info("uuid={}, 用户投注结算->注单数据未结算! 订单号={}", orderEntity.getUuid(), orderEntity.getOrderNo());
                return;
            }
            if (Objects.isNull(orderTerm.getWin())) {
                log.info("uuid={}, 用户投注结算-> 赢得金额, win 为NULL! 订单号={}", orderEntity.getUuid(), orderEntity.getOrderNo());
                orderTerm.setWin(BigDecimal.ZERO);
            }
            if (Objects.isNull(orderTerm.getSuperRebate())) {
                log.info("uuid={}, 用户投注结算-> 上级的返水, superRebate 为NULL! 订单号={}", orderEntity.getUuid(), orderEntity.getOrderNo());
                orderTerm.setSuperRebate(BigDecimal.ZERO);
            }
            if (Objects.isNull(orderTerm.getCodeAmount())) {
                log.info("uuid={}, 用户投注结算-> 打码量, codeAmount 为NULL! 订单号={},", orderEntity.getUuid(), orderEntity.getOrderNo());
                orderTerm.setCodeAmount(BigDecimal.ZERO);
            }
            if (Objects.isNull(orderTerm.getRebate())) {
                log.info("uuid={}, 用户投注结算-> 自己的反水, rebate 为NULL! 订单号={}", orderEntity.getUuid(), orderEntity.getOrderNo());
                orderTerm.setRebate(BigDecimal.ZERO);
            }

            Currency currency = currencyService.getById(orderEntity.getCurrencyId());

            UserCountOrder userCountOrder = this.getUserCountOrder(orderEntity);
            log.info("uuid={}, 用户投注结算-->当前订单信息: {}", orderEntity.getUuid(), Objects.isNull(userCountOrder) ? null : userCountOrder.toString());
            if (userCountOrder == null) {
                userCountOrder = new UserCountOrder();
                userCountOrder.setUserId(orderEntity.getUserId());//用户id
                userCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                userCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                userCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    userCountOrder.setItemId(currency.getItemId());
                    userCountOrder.setChainTag(currency.getChainTag());
                }
                if (orderTerm.getWin().compareTo(BigDecimal.ZERO) > 0) {  //说明赢了
                    userCountOrder.setUserEarnings(orderTerm.getWin());//用户收入
                }
                userCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                userCountOrder.setCodeAmount(orderTerm.getCodeAmount());//打码量
                userCountOrder.setSettleAmount(orderTerm.getWin());//结算金额(返奖金额)-当日
                //输赢金额(返奖额减去投注额)-当日
                userCountOrder.setWinLoseAmount(orderTerm.getWinLoss());
                userCountOrder.setEfficientBetAmount(orderTerm.getBetAmount());//有效投注金额(分输赢)-当日',
//                userCountOrder.setWaitRebateAmount(orderTerm.getRebate());//待返水金额',
                userCountOrder.setCustomerLossAmount(orderTerm.getWinLoss());//计算 客损
                userCountOrder.setCalculationsTime(new Date());// 当前时间 new data
                userCountOrder.setCreateTime(new Date());
                userCountOrder.setUpdateTime(new Date());
                //处理上级返水数据
                this.handleSuperRebateData(orderTerm, orderEntity);
                int result = userCountOrderMapper.saveUserCountOrderBySettle(userCountOrder);
                log.info("uuid={}, 用户投注结算->添加用户数据: {} 结果: {}", orderEntity.getUuid(), userCountOrder, result);
            } else {
                UserCountOrder updateUserCountOrder = new UserCountOrder();
                //下列累计操作 在sql 中执行
                updateUserCountOrder.setId(userCountOrder.getId());
                if (orderTerm.getWin().compareTo(BigDecimal.ZERO) > 0) {  //说明赢了
                    updateUserCountOrder.setUserEarnings(orderTerm.getWin());//用户收入
                }
                updateUserCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                updateUserCountOrder.setCodeAmount(orderTerm.getCodeAmount());// 打码量
                updateUserCountOrder.setSettleAmount(orderTerm.getWin());// 结算金额(返奖金额)-当日
                //输赢金额(返奖额减去投注额)-当日
                updateUserCountOrder.setWinLoseAmount(orderTerm.getWinLoss());
                updateUserCountOrder.setEfficientBetAmount(orderTerm.getBetAmount());//有效投注金额(分输赢)-当日',
//                updateUserCountOrder.setWaitRebateAmount(orderTerm.getRebate());//待返水金额',
                updateUserCountOrder.setCustomerLossAmount(orderTerm.getWinLoss());
                updateUserCountOrder.setCalculationsTime(new Date());
                updateUserCountOrder.setUpdateTime(new Date());
                //处理上级返水数据
                this.handleSuperRebateData(orderTerm, orderEntity);
                int result = userCountOrderMapper.updateUserCountOrderBySettle(updateUserCountOrder);
                log.info("uuid={}, 用户下注->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateUserCountOrder, result);
            }
            UserCountGameCode userCountGameCode = this.getUserCountGameTypeCode(orderEntity);
            if (userCountGameCode == null) {
                //统计用户游戏类型打码量
                userCountGameCode = new UserCountGameCode();
                userCountGameCode.setUserId(orderEntity.getUserId());
                userCountGameCode.setDayStr(orderEntity.getNowDay());
                userCountGameCode.setCreateTime(new Date());
                userCountGameCode.setUpdateTime(new Date());
                this.setGameTypeCodeField(userCountGameCode, orderTerm);
                int gameTypeCoderesult = userCountGameCodeMapper.saveUserCountGameCodeBySettle(userCountGameCode);
                log.info("uuid={}, 用户投注结算->添加游戏类型打码量: {} 结果: {}", orderEntity.getUuid(), userCountGameCode, gameTypeCoderesult);
            } else {
                UserCountGameCode updateUserCountGameCode = new UserCountGameCode();
                updateUserCountGameCode.setId(userCountGameCode.getId());
                updateUserCountGameCode.setUpdateTime(new Date());
                this.setGameTypeCodeField(updateUserCountGameCode, orderTerm);
                int gameTypeCoderesult = userCountGameCodeMapper.updateUserCountGameCodeBySettle(updateUserCountGameCode);
                log.info("uuid={}, 用户投注结算->添加游戏类型打码量: {} 结果: {}", orderEntity.getUuid(), updateUserCountGameCode, gameTypeCoderesult);
            }

            // -----------------------------  t_channel_count_order  操作 -----------------------------------------
            ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
            log.info("uuid={}, 用户下注-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(channelCountOrder) ? null : channelCountOrder.toString());
            if (channelCountOrder == null) {
                channelCountOrder = new ChannelCountOrder();
                channelCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                channelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                channelCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                channelCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种

                if (orderTerm.getWin().compareTo(BigDecimal.ZERO) > 0) {  //说明赢了
                    channelCountOrder.setUserEarnings(orderTerm.getWin());//用户收入
                }
                channelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
                channelCountOrder.setCodeAmount(orderTerm.getCodeAmount());//打码量
                channelCountOrder.setSettleAmount(orderTerm.getWin());//结算金额(返奖金额)-当日
                //输赢金额(返奖额减去投注额)-当日
                channelCountOrder.setWinLoseAmount(orderTerm.getWinLoss());
                channelCountOrder.setEfficientBetAmount(orderTerm.getBetAmount());//有效投注金额(分输赢)-当日',
//                channelCountOrder.setWaitRebateAmount(orderTerm.getRebate());//待返水金额',
                //计算 客损 = 输赢 + 已领取的返水 + 已领取的返佣  +  总彩金(=
                // t_count_order表中的 (bonusAmount(彩金金额) + customerBonusAmount(后台客服手动上分彩金金额)  + playWheelTermAward(转盘活动彩金)
                //         activityAwardBonusAmount(用户活动奖励领取奖励-彩金金额) - customerPointsDeductedAmount(后台客服手动下分金额-下分类型是扣除积分) ))
                channelCountOrder.setCustomerLossAmount(orderTerm.getWinLoss());
//                channelCountOrder.setWaitReturnCommissionAmount(orderTerm.getSuperRebate());// 返佣金额-下级用户返水返给自己的金额
                channelCountOrder.setCalculationsTime(new Date());
                int result = channelCountOrderMapper.saveChannelCountOrderBySettle(channelCountOrder);
                log.info("uuid={}, 用户下注->添加用户数据: {} 结果: {}", orderEntity.getUuid(), channelCountOrder, result);
            } else {
                ChannelCountOrder updateChannelCountOrder = new ChannelCountOrder();
                updateChannelCountOrder.setId(channelCountOrder.getId());
                if (orderTerm.getWin().compareTo(BigDecimal.ZERO) > 0) {  //说明赢了
                    updateChannelCountOrder.setUserEarnings(orderTerm.getWin());//用户收入
                }
                updateChannelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                updateChannelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
                updateChannelCountOrder.setCodeAmount(orderTerm.getCodeAmount());//todo 打码量
                updateChannelCountOrder.setSettleAmount(orderTerm.getWin());//todo 结算金额(返奖金额)-当日
                //输赢金额(返奖额减去投注额)-当日
                updateChannelCountOrder.setWinLoseAmount(orderTerm.getWinLoss());
                updateChannelCountOrder.setEfficientBetAmount(orderTerm.getBetAmount());//有效投注金额(分输赢)-当日',
//                updateChannelCountOrder.setWaitRebateAmount(orderTerm.getRebate());//待返水金额',
                //计算 客损 = 输赢  +  总彩金(=
                // t_count_order表中的 (bonusAmount(彩金金额) + customerBonusAmount(后台客服手动上分彩金金额)  + playWheelTermAward(转盘活动彩金)
                //         activityAwardBonusAmount(用户活动奖励领取奖励-彩金金额) - customerPointsDeductedAmount(后台客服手动下分金额-下分类型是扣除积分) ))
                // 已领取返水金额 + 已领取返佣金额
                updateChannelCountOrder.setCustomerLossAmount(orderTerm.getWinLoss());
                updateChannelCountOrder.setCalculationsTime(new Date());
//                updateChannelCountOrder.setWaitReturnCommissionAmount(orderTerm.getSuperRebate());// 返佣金额-下级用户返水返给自己的金额
                int result = channelCountOrderMapper.updateChannelCountOrderBySettle(updateChannelCountOrder);
                log.info("uuid={}, 用户下注->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateChannelCountOrder, result);
            }
            ChannelCountGameCode channelCountGameCode = this.getchannelCountGameTypeCode(orderEntity);
            if (channelCountGameCode == null) {
                //统计用户游戏类型打码量
                channelCountGameCode = new ChannelCountGameCode();
                channelCountGameCode.setChannelId(orderEntity.getChannelId());
                channelCountGameCode.setDayStr(orderEntity.getNowDay());
                channelCountGameCode.setCreateTime(new Date());
                channelCountGameCode.setUpdateTime(new Date());
                this.setChannelGameTypeCodeField(channelCountGameCode, orderTerm);
                int gameTypeCoderesult = channelCountGameCodeMapper.savechannelCountGameCodeBySettle(channelCountGameCode);
                log.info("uuid={}, 用户投注结算->添加渠道游戏类型打码量: {} 结果: {}", orderEntity.getUuid(), channelCountGameCode, gameTypeCoderesult);
            } else {
                ChannelCountGameCode updateUserCountGameCode = new ChannelCountGameCode();
                updateUserCountGameCode.setId(channelCountGameCode.getId());
                updateUserCountGameCode.setUpdateTime(new Date());
                this.setChannelGameTypeCodeField(updateUserCountGameCode, orderTerm);
                int gameTypeCoderesult = channelCountGameCodeMapper.updatechannelCountGameCodeBySettle(updateUserCountGameCode);
                log.info("uuid={}, 用户投注结算->添加渠道游戏类型打码量: {} 结果: {}", orderEntity.getUuid(), updateUserCountGameCode, gameTypeCoderesult);
            }

            // -----------------------------  t_count_order  操作 -----------------------------------------
            CountOrder countOrder = this.getCountOrder(orderEntity);
//            BigDecimal usdtBalance = userWalletService.countOrderAmount(orderEntity.getCurrencyId()).setScale(2, RoundingMode.DOWN);
            log.info("uuid={}, 用户下注-->当前订单信息: {}", orderEntity.getUuid(), Objects.isNull(countOrder) ? null : countOrder.toString());
            if (countOrder == null) {
                countOrder = new CountOrder();
                countOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                countOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (orderTerm.getWin().compareTo(BigDecimal.ZERO) > 0) {  //说明赢了
                    countOrder.setUserEarnings(orderTerm.getWin());//用户收入
                }
                countOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                countOrder.setCodeAmount(orderTerm.getCodeAmount());//打码量
                countOrder.setSettleAmount(orderTerm.getWin());//结算金额(返奖金额)-当日
//                countOrder.setUsdtBalance(usdtBalance);
                //输赢金额(返奖额减去投注额)-当日
                countOrder.setWinLoseAmount(orderTerm.getWinLoss());
                countOrder.setEfficientBetAmount(orderTerm.getBetAmount());//有效投注金额(分输赢)-当日',
//                countOrder.setWaitRebateAmount(orderTerm.getRebate());//待返水金额',
                countOrder.setCustomerLossAmount(orderTerm.getWinLoss());
                countOrder.setCalculationsTime(new Date());//
//                countOrder.setWaitReturnCommissionAmount(orderTerm.getSuperRebate());// 返佣金额-下级用户返水返给自己的金额
                countOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                int result = countOrderMapper.saveCountOrderBySettle(countOrder);
                log.info("uuid={}, 用户下注->添加用户数据: {} 结果: {}", orderEntity.getUuid(), countOrder, result);
            } else {
                CountOrder udpateCountOrder = new CountOrder();
                udpateCountOrder.setId(countOrder.getId());
                if (orderTerm.getWin().compareTo(BigDecimal.ZERO) > 0) {  //说明赢了
                    udpateCountOrder.setUserEarnings(orderTerm.getWin());//用户收入
                }
                udpateCountOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                udpateCountOrder.setCodeAmount(orderTerm.getCodeAmount());// 打码量
                udpateCountOrder.setSettleAmount(orderTerm.getWin());// 结算金额(返奖金额)-当日
//                udpateCountOrder.setUsdtBalance(usdtBalance);
                //输赢金额(返奖额减去投注额)-当日
                udpateCountOrder.setWinLoseAmount(orderTerm.getWinLoss());
                udpateCountOrder.setEfficientBetAmount(orderTerm.getBetAmount());//有效投注金额(分输赢)-当日',
//                udpateCountOrder.setWaitRebateAmount(orderTerm.getRebate());//待返水金额',
                //计算 客损 = 输赢  +  总彩金(=
                // t_count_order表中的 (bonusAmount(彩金金额) + customerBonusAmount(后台客服手动上分彩金金额)  + playWheelTermAward(转盘活动彩金)
                //         activityAwardBonusAmount(用户活动奖励领取奖励-彩金金额) - customerPointsDeductedAmount(后台客服手动下分金额-下分类型是扣除积分) ))
                // 已领取返水金额 + 已领取返佣金额
                udpateCountOrder.setCustomerLossAmount(orderTerm.getWinLoss());//计算 客损
                udpateCountOrder.setCalculationsTime(new Date());// 当前时间 new data
//                udpateCountOrder.setWaitReturnCommissionAmount(orderTerm.getSuperRebate());// 返佣金额-下级用户返水返给自己的金额
                udpateCountOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                int result = countOrderMapper.updateCountOrderBySettle(udpateCountOrder);
                log.info("uuid={}, 用户下注->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), udpateCountOrder, result);
            }
        } catch (Exception e) {
            log.error("settle -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
            throw e;
        }
    }

    private ChannelCountGameCode getchannelCountGameTypeCode(OrderEntity orderEntity) {
        LambdaQueryWrapper<ChannelCountGameCode> queryWrapper =
                Wrappers.lambdaQuery(ChannelCountGameCode.class).eq(ChannelCountGameCode::getDayStr, orderEntity.getNowDay()).eq(ChannelCountGameCode::getChannelId, orderEntity.getChannelId());
        ChannelCountGameCode channelCountGameCode = channelCountGameCodeService.getOne(queryWrapper);
        log.info("getUserCountOrder -> userId={}, nowDay={},  响应={}", orderEntity.getUserId(), orderEntity.getNowDay(), Objects.isNull(channelCountGameCode) ? null :
                JSON.toJSONString(channelCountGameCode));
        return channelCountGameCode;
    }

    private UserCountGameCode getUserCountGameTypeCode(OrderEntity orderEntity) {

        LambdaQueryWrapper<UserCountGameCode> queryWrapper = Wrappers.lambdaQuery(UserCountGameCode.class).eq(UserCountGameCode::getDayStr, orderEntity.getNowDay()).eq(UserCountGameCode::getUserId,
                orderEntity.getUserId());
        UserCountGameCode userCountGameCode = userCountGameCodeService.getOne(queryWrapper);
        log.info("getUserCountOrder -> userId={}, nowDay={},  响应={}", orderEntity.getUserId(), orderEntity.getNowDay(), Objects.isNull(userCountGameCode) ? null :
                JSON.toJSONString(userCountGameCode));
        return userCountGameCode;
    }

    /*****
     * 用户注册
     * @param orderEntity
     */
    public void register(OrderEntity orderEntity) {
        try {
            //.新增  人数
            Long userId = orderEntity.getUserId();
            LambdaQueryWrapper<TUser> userQuery = Wrappers.lambdaQuery(TUser.class);
            userQuery.eq(TUser::getUserId, userId);
            TUser user = userService.getOne(userQuery);
            if (Objects.isNull(user)) {
                log.info("uuid={}, 用户注册-->当前用户数据为空, userId={}", orderEntity.getUuid(), userId);
                return;
            }
            Currency currency = currencyService.getById(orderEntity.getCurrencyId());
            orderEntity.setChannelId(user.getChannelId());
            ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
            log.info("uuid={}, 用户注册-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(channelCountOrder) ? null : channelCountOrder.toString());
            if (channelCountOrder == null) {
                channelCountOrder = new ChannelCountOrder();
                channelCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                channelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                channelCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                channelCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    channelCountOrder.setItemId(currency.getItemId());
                    channelCountOrder.setChainTag(currency.getChainTag());
                }
                channelCountOrder.setCreateTime(new Date());//操作时间
                channelCountOrder.setUpdateTime(new Date());//操作时间
                channelCountOrder.setUserNum(1);
                channelCountOrder.setCalculationsTime(new Date());//操作时间
                int result = channelCountOrderMapper.insertChannelCountOrderDataV2(channelCountOrder);
                log.info("uuid={}, 用户注册->添加用户数据: {} 结果: {}", orderEntity.getUuid(), channelCountOrder, result);
            } else {
                ChannelCountOrder updateChannelCountOrder = new ChannelCountOrder();
                updateChannelCountOrder.setId(channelCountOrder.getId());
                updateChannelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                updateChannelCountOrder.setUserNum(1);
                updateChannelCountOrder.setCalculationsTime(new Date());//操作时间
                updateChannelCountOrder.setUpdateTime(new Date());//操作时间
                int result = channelCountOrderMapper.updateChannelCountOrderDataV2(updateChannelCountOrder);
                log.info("uuid={}, 用户注册->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateChannelCountOrder, result);
            }
            CountOrder countOrder = this.getCountOrder(orderEntity);
            log.info("uuid={}, 用户注册-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(countOrder) ? null : countOrder.toString());
            if (countOrder == null) {
                countOrder = new CountOrder();
                countOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                countOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    countOrder.setItemId(currency.getItemId());
                    countOrder.setChainTag(currency.getChainTag());
                }
                countOrder.setCreateTime(new Date());//操作时间
                countOrder.setUpdateTime(new Date());//操作时间
                countOrder.setUserNum(1);
                countOrder.setCalculationsTime(new Date());//操作时间
                int result = countOrderMapper.insertCountOrderDataV2(countOrder);
                log.info("uuid={}, 用户注册->添加用户数据: {} 结果: {}", orderEntity.getUuid(), countOrder, result);
            } else {
                CountOrder updateCountOrder = new CountOrder();
                updateCountOrder.setId(countOrder.getId());
                updateCountOrder.setUserNum(1);
                updateCountOrder.setCalculationsTime(new Date());//操作时间
                updateCountOrder.setUpdateTime(new Date());//操作时间
                int result = countOrderMapper.updateCountOrderDataV2(updateCountOrder);
                log.info("uuid={}, 用户注册->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateCountOrder, result);
            }
        } catch (Exception e) {
            log.error("register -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
            throw e;
        }
    }

    /*****
     * 用户反水 - 领取动作
     * @param orderEntity
     */
    public void getRebate(OrderEntity orderEntity) {
        try {
            //当前账户余额
            ConsumOrderVO amount = getAmount(orderEntity);
            LambdaQueryWrapper<OrderReceiveRebate> query = Wrappers.lambdaQuery(OrderReceiveRebate.class);
            query.eq(OrderReceiveRebate::getOrderNo, orderEntity.getOrderNo());
            query.last("limit 1");
            OrderReceiveRebate receiveRebate = orderReceiveRebateService.getOne(query);
            if (Objects.isNull(receiveRebate)) {
                log.info("uuid={}, 用户反水 - 领取动作-->OrderReceiveRebate数据为空, orderNo={}", orderEntity.getUuid(), orderEntity.getOrderNo());
                return;
            }
            Currency currency = currencyService.getById(orderEntity.getCurrencyId());
            // -----------------------------  t_user_count_order  操作 -----------------------------------------
            UserCountOrder userCountOrder = this.getUserCountOrder(orderEntity);
            log.info("uuid={},用户反水-->当前订单信息: {}", orderEntity.getUuid(), Objects.isNull(userCountOrder) ? null : userCountOrder.toString());
            if (userCountOrder == null) {
                userCountOrder = new UserCountOrder();
                userCountOrder.setUserId(orderEntity.getUserId());//用户id
                userCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                userCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                userCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    userCountOrder.setItemId(currency.getItemId());
                    userCountOrder.setChainTag(currency.getChainTag());
                }
                userCountOrder.setUserAmount(amount.getUserAmount());
                userCountOrder.setUserEarnings(receiveRebate.getAmount());//用户收入
                userCountOrder.setCalculationsTime(new Date());// 当前时间 new data
                userCountOrder.setCreateTime(new Date());
                userCountOrder.setUpdateTime(new Date());
//                userCountOrder.setAlreadyRebateAmount(receiveRebate.getAmount());// 已返水金额
                userCountOrder.setCustomerLossAmount(receiveRebate.getAmount());
                int result = userCountOrderMapper.saveUserCountOrderData(userCountOrder);
                log.info("uuid={},用户反水->添加用户数据: {} 结果: {}", orderEntity.getUuid(), userCountOrder, result);
            } else {
                UserCountOrder udpateUserCountOrder = new UserCountOrder();
                udpateUserCountOrder.setId(userCountOrder.getId());
                udpateUserCountOrder.setUserAmount(amount.getUserAmount());// 查用户余额  用户结余-当日
                udpateUserCountOrder.setUserEarnings(receiveRebate.getAmount());//用户收入
                udpateUserCountOrder.setCalculationsTime(new Date());// 当前时间 new data
//                udpateUserCountOrder.setAlreadyRebateAmount(receiveRebate.getAmount());// 已返水金额
                udpateUserCountOrder.setCustomerLossAmount(receiveRebate.getAmount());
                udpateUserCountOrder.setUpdateTime(new Date());
                int result = userCountOrderMapper.updateUserCountOrderData(udpateUserCountOrder);
                log.info("uuid={},用户反水->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), userCountOrder, result);
            }
            // -----------------------------  t_channel_count_order  操作 -----------------------------------------
            //查询t_channel_count_order 渠道 是否有数据 有 累加 否 添加
            ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
            log.info("uuid={},用户反水-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(channelCountOrder) ? null : channelCountOrder.toString());
            if (channelCountOrder == null) {
                channelCountOrder = new ChannelCountOrder();
                channelCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                channelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                channelCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                channelCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    channelCountOrder.setItemId(currency.getItemId());
                    channelCountOrder.setChainTag(currency.getChainTag());
                }
                channelCountOrder.setCreateTime(new Date());//操作时间
                channelCountOrder.setUpdateTime(new Date());//操作时间
                channelCountOrder.setUserAmount(amount.getChannelAmount());//  查用户余额  用户结余-当日
                channelCountOrder.setUserEarnings(receiveRebate.getAmount());//用户收入
                channelCountOrder.setCalculationsTime(new Date());// 当前时间 new data
//                channelCountOrder.setAlreadyRebateAmount(receiveRebate.getAmount());// 已返水金额
                channelCountOrder.setCustomerLossAmount(receiveRebate.getAmount());
                int result = channelCountOrderMapper.insertChannelCountOrderDataV2(channelCountOrder);
                log.info("uuid={},用户反水->添加用户数据: {} 结果: {}", orderEntity.getUuid(), channelCountOrder, result);
            } else {
                ChannelCountOrder updateChannelCountOrder = new ChannelCountOrder();
                updateChannelCountOrder.setId(channelCountOrder.getId());
                updateChannelCountOrder.setUserAmount(amount.getChannelAmount());//  查用户余额  用户结余-当日
                updateChannelCountOrder.setUserEarnings(receiveRebate.getAmount());//用户收入
                updateChannelCountOrder.setCalculationsTime(new Date());// 当前时间 new data
//                updateChannelCountOrder.setAlreadyRebateAmount(receiveRebate.getAmount());// 已返水金额
                updateChannelCountOrder.setCustomerLossAmount(receiveRebate.getAmount());
                updateChannelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                updateChannelCountOrder.setUpdateTime(new Date());//操作时间
                int result = channelCountOrderMapper.updateChannelCountOrderDataV2(updateChannelCountOrder);
                log.info("uuid={},用户反水->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateChannelCountOrder, result);
            }
            // -----------------------------  t_count_order  操作 -----------------------------------------
            CountOrder countOrder = this.getCountOrder(orderEntity);
            log.info("uuid={},用户反水-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(countOrder) ? null : countOrder.toString());
            if (countOrder == null) {
                countOrder = new CountOrder();
                countOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                countOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    countOrder.setItemId(currency.getItemId());
                    countOrder.setChainTag(currency.getChainTag());
                }
                countOrder.setCreateTime(new Date());//操作时间
                countOrder.setUpdateTime(new Date());//操作时间
                countOrder.setUserAmount(amount.getOrderAmount());//  查用户余额  用户结余-当日
                countOrder.setUserEarnings(receiveRebate.getAmount());//用户收入
                countOrder.setCalculationsTime(new Date());// 当前时间 new data
//                countOrder.setAlreadyRebateAmount(receiveRebate.getAmount());// 已返水金额
                countOrder.setCustomerLossAmount(receiveRebate.getAmount());
                countOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                int result = countOrderMapper.insertCountOrderDataV2(countOrder);
                log.info("uuid={},用户反水->添加用户数据: {} 结果: {}", orderEntity.getUuid(), countOrder, result);
            } else {
                CountOrder updateCountOrder = new CountOrder();
                updateCountOrder.setId(countOrder.getId());
                updateCountOrder.setUserAmount(amount.getOrderAmount());// 查用户余额  用户结余-当日
                updateCountOrder.setUserEarnings(receiveRebate.getAmount());//用户收入
                updateCountOrder.setCalculationsTime(new Date());// 当前时间 new data
//                updateCountOrder.setAlreadyRebateAmount(receiveRebate.getAmount());//todo 已返水金额
                updateCountOrder.setCustomerLossAmount(receiveRebate.getAmount());
                updateCountOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                int result = countOrderMapper.updateCountOrderDataV2(updateCountOrder);
                log.info("uuid={},用户反水->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateCountOrder, result);
            }
        } catch (Exception e) {
            log.error("getRebate -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
            throw e;
        }
    }

    /**
     * 领取反拥
     *
     * @param orderEntity
     */
    public void getBrokerage(OrderEntity orderEntity) {
        try {
            //当前账户余额
            ConsumOrderVO amountVo = getAmount(orderEntity);
            //领取领取反拥订单
            //查询t_user_count_order 是否有数据 有 累加 否 添加
            LambdaQueryWrapper<OrderReceiveRebate> q = Wrappers.lambdaQuery(OrderReceiveRebate.class).eq(OrderReceiveRebate::getUserId, orderEntity.getUserId()).eq(OrderReceiveRebate::getOrderNo,
                    orderEntity.getOrderNo()).eq(OrderReceiveRebate::getCurrencyId, orderEntity.getCurrencyId());
            OrderReceiveRebate orderReceiveRebate = orderReceiveRebateService.getOne(q);
            if (orderReceiveRebate == null) {
                log.info("领取反拥->当前订单号:{}", orderEntity.getOrderNo());
                return;
            }
            Currency currency = currencyService.getById(orderEntity.getCurrencyId());
            // -----------------------------  t_user_count_order  操作 -----------------------------------------
            UserCountOrder userCountOrder = this.getUserCountOrder(orderEntity);
//            log.info("领取反拥-->当前订单信息: {}", userCountOrder.toString());
            brokerageService.computeBrokerageUserCountOrder(currency, orderEntity, userCountOrder, amountVo);
            // -----------------------------  t_channel_count_order  操作 -----------------------------------------
            ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
//            log.info("领取反拥-->当前订单渠道信息: {}", channelCountOrder.toString());
            brokerageService.computeBrokerageChannelCountOrder(currency, orderEntity, channelCountOrder, amountVo);
            // -----------------------------  t_count_order  操作 -----------------------------------------
            CountOrder countOrder = this.getCountOrder(orderEntity);
            brokerageService.computeBrokerageCountOrder(currency, orderEntity, countOrder, amountVo);
        } catch (Exception e) {
            log.error("getBrokerage -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
            throw e;
        }
    }
    /**
     * 领取代理工资
     *
     * @param orderEntity
     */
    public void getAgent(OrderEntity orderEntity) {
        try {
            //当前账户余额
            ConsumOrderVO amountVo = getAmount(orderEntity);
            //领取代理工资订单
            //查询t_user_count_order 是否有数据 有 累加 否 添加
            LambdaQueryWrapper<OrderReceiveRebate> q = Wrappers.lambdaQuery(OrderReceiveRebate.class).eq(OrderReceiveRebate::getUserId, orderEntity.getUserId()).eq(OrderReceiveRebate::getOrderNo,
                    orderEntity.getOrderNo()).eq(OrderReceiveRebate::getCurrencyId, orderEntity.getCurrencyId());
            OrderReceiveRebate orderReceiveRebate = orderReceiveRebateService.getOne(q);
            if (orderReceiveRebate == null) {
                log.info("领取代理工资订单->当前订单号:{}", orderEntity.getOrderNo());
                return;
            }
            Currency currency = currencyService.getById(orderEntity.getCurrencyId());
            // -----------------------------  t_user_count_order  操作 -----------------------------------------
            UserCountOrder userCountOrder = this.getUserCountOrder(orderEntity);
//            log.info("领取代理工资订单-->当前订单信息: {}", userCountOrder.toString());
            agentService.computeBrokerageUserCountOrder(currency, orderEntity, userCountOrder, amountVo);
            // -----------------------------  t_channel_count_order  操作 -----------------------------------------
            ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
//            log.info("领取代理工资订单-->当前订单渠道信息: {}", channelCountOrder.toString());
            agentService.computeBrokerageChannelCountOrder(currency, orderEntity, channelCountOrder, amountVo);
            // -----------------------------  t_count_order  操作 -----------------------------------------
            CountOrder countOrder = this.getCountOrder(orderEntity);
            agentService.computeBrokerageCountOrder(currency, orderEntity, countOrder, amountVo);
        } catch (Exception e) {
            log.error("getAgent -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
            throw e;
        }
    }
    /**
     * 十.用户活动领取奖励相关
     *
     * @param orderEntity
     */
    public void getActivity(OrderEntity orderEntity) {
        try {
            //当前账户余额
            ConsumOrderVO amountVo = getAmount(orderEntity);
            //领取领取反拥订单
            //查询t_user_count_order 是否有数据 有 累加 否 添加
            LambdaQueryWrapper<ActivityAwardReceive> q =
                    Wrappers.lambdaQuery(ActivityAwardReceive.class).eq(ActivityAwardReceive::getUserId, orderEntity.getUserId()).eq(ActivityAwardReceive::getOrderNo, orderEntity.getOrderNo());
            ActivityAwardReceive activityAwardReceive = activityAwardReceiveService.getOne(q);
            if (activityAwardReceive == null) {
                log.info("活动奖励->当前订单号:{}", orderEntity.getOrderNo());
                return;
            }
            Currency currency = currencyService.getById(orderEntity.getCurrencyId());
            // -----------------------------  t_user_count_order  操作 -----------------------------------------
            UserCountOrder userCountOrder = this.getUserCountOrder(orderEntity);
//            log.info("活动奖励-->当前订单信息: {}", userCountOrder.toString());
            activityMqService.computeActivityUserCountOrder(currency, orderEntity, userCountOrder, amountVo);
            // -----------------------------  t_channel_count_order  操作 -----------------------------------------
            ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
//            log.info("活动奖励-->当前订单渠道信息: {}", channelCountOrder.toString());
            activityMqService.computeActivityChannelCountOrder(currency, orderEntity, channelCountOrder, amountVo);
            // -----------------------------  t_count_order  操作 -----------------------------------------
            CountOrder countOrder = this.getCountOrder(orderEntity);
            activityMqService.computeActivityCountOrder(currency, orderEntity, countOrder, amountVo);
        } catch (Exception e) {
            log.error("getActivity -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
            throw e;
        }
    }

    /**
     * 邀请新人充值奖励
     */
    public void getInviteRechargeReward(OrderEntity orderEntity) {
        try {
            ConsumOrderVO amount = getAmount(orderEntity);
            LambdaQueryWrapper<InviteRechargeRewardRecord> q = Wrappers.lambdaQuery(InviteRechargeRewardRecord.class)
                    .eq(InviteRechargeRewardRecord::getOrderNo, orderEntity.getOrderNo());
            InviteRechargeRewardRecord record = inviteRechargeRewardRecordService.getOne(q);
            if (record == null) {
                log.info("邀请新人充值奖励统计->无记录, orderNo={}", orderEntity.getOrderNo());
                return;
            }
            BigDecimal reward = orderEntity.getBonusAmount() != null
                    ? orderEntity.getBonusAmount()
                    : Optional.ofNullable(record.getRewardAmount()).orElse(BigDecimal.ZERO);
            BigDecimal money = Optional.ofNullable(orderEntity.getMoney()).orElse(BigDecimal.ZERO);
            BigDecimal earningsDelta = money.add(reward);

            Currency currency = currencyService.getById(orderEntity.getCurrencyId());

            UserCountOrder userCountOrder = this.getUserCountOrder(orderEntity);
            if (Objects.isNull(userCountOrder)) {
                userCountOrder = new UserCountOrder();
                userCountOrder.setUserId(orderEntity.getUserId());
                userCountOrder.setChannelId(orderEntity.getChannelId());
                userCountOrder.setDayStr(orderEntity.getNowDay());
                userCountOrder.setCurrencyId(orderEntity.getCurrencyId());
                if (Objects.nonNull(currency)) {
                    userCountOrder.setItemId(currency.getItemId());
                    userCountOrder.setChainTag(currency.getChainTag());
                }
                userCountOrder.setActivityAwardBonusAmount(reward);
                userCountOrder.setUserAmount(amount.getUserAmount());
                userCountOrder.setUserEarnings(earningsDelta);
                userCountOrder.setCustomerLossAmount(reward);
                userCountOrder.setCalculationsTime(new Date());
                userCountOrder.setCreateTime(new Date());
                userCountOrder.setUpdateTime(new Date());
                userCountOrderMapper.saveUserCountOrderData(userCountOrder);
                log.info("邀请新人充值奖励->uuid={}, 新增用户日统计 reward={}", orderEntity.getUuid(), reward);
            } else {
                UserCountOrder updateUser = new UserCountOrder();
                updateUser.setId(userCountOrder.getId());
                updateUser.setActivityAwardBonusAmount(reward);
                updateUser.setUserAmount(amount.getUserAmount());
                updateUser.setUserEarnings(earningsDelta);
                updateUser.setCustomerLossAmount(reward);
                updateUser.setCalculationsTime(new Date());
                updateUser.setUpdateTime(new Date());
                userCountOrderMapper.updateUserCountOrderData(updateUser);
                log.info("邀请新人充值奖励->uuid={}, 更新用户日统计 reward={}", orderEntity.getUuid(), reward);
            }

            ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
            if (channelCountOrder == null) {
                channelCountOrder = new ChannelCountOrder();
                channelCountOrder.setChannelId(orderEntity.getChannelId());
                channelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));
                channelCountOrder.setDayStr(orderEntity.getNowDay());
                channelCountOrder.setCurrencyId(orderEntity.getCurrencyId());
                if (Objects.nonNull(currency)) {
                    channelCountOrder.setItemId(currency.getItemId());
                    channelCountOrder.setChainTag(currency.getChainTag());
                }
                channelCountOrder.setCreateTime(new Date());
                channelCountOrder.setUpdateTime(new Date());
                channelCountOrder.setActivityAwardBonusAmount(reward);
                channelCountOrder.setUserAmount(amount.getChannelAmount());
                channelCountOrder.setUserEarnings(earningsDelta);
                channelCountOrder.setCustomerLossAmount(reward);
                channelCountOrder.setCalculationsTime(new Date());
                channelCountOrderMapper.insertChannelCountOrderDataV2(channelCountOrder);
            } else {
                ChannelCountOrder updateCh = new ChannelCountOrder();
                updateCh.setId(channelCountOrder.getId());
                updateCh.setShareholderId(setShareholderIdFromChannel(orderEntity));
                updateCh.setActivityAwardBonusAmount(reward);
                updateCh.setUserAmount(amount.getChannelAmount());
                updateCh.setUserEarnings(earningsDelta);
                updateCh.setCustomerLossAmount(reward);
                updateCh.setCalculationsTime(new Date());
                updateCh.setUpdateTime(new Date());
                channelCountOrderMapper.updateChannelCountOrderDataV2(updateCh);
            }

            CountOrder countOrder = this.getCountOrder(orderEntity);
            if (countOrder == null) {
                countOrder = new CountOrder();
                countOrder.setDayStr(orderEntity.getNowDay());
                countOrder.setCurrencyId(orderEntity.getCurrencyId());
                if (Objects.nonNull(currency)) {
                    countOrder.setItemId(currency.getItemId());
                    countOrder.setChainTag(currency.getChainTag());
                }
                countOrder.setCreateTime(new Date());
                countOrder.setUpdateTime(new Date());
                countOrder.setUserAmount(amount.getOrderAmount());
                countOrder.setActivityAwardBonusAmount(reward);
                countOrder.setUserEarnings(earningsDelta);
                countOrder.setCustomerLossAmount(reward);
                countOrder.setCalculationsTime(new Date());
                countOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                countOrderMapper.insertCountOrderDataV2(countOrder);
            } else {
                CountOrder updateCo = new CountOrder();
                updateCo.setId(countOrder.getId());
                updateCo.setUserAmount(amount.getOrderAmount());
                updateCo.setActivityAwardBonusAmount(reward);
                updateCo.setUserEarnings(earningsDelta);
                updateCo.setCustomerLossAmount(reward);
                updateCo.setCalculationsTime(new Date());
                updateCo.setUpdateTime(new Date());
                updateCo.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                countOrderMapper.updateCountOrderDataV2(updateCo);
            }
        } catch (Exception e) {
            log.error("getInviteRechargeReward -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
            throw e;
        }
    }

    /**
     * 转盘奖励
     *
     * @param orderEntity
     */
    public void getWheel(OrderEntity orderEntity) {
        try {
            //当前账户余额
            ConsumOrderVO amountVo = getAmount(orderEntity);
            //领取领取反拥订单
            //查询t_user_count_order 是否有数据 有 累加 否 添加
            LambdaQueryWrapper<PlayWheelTerm> q = Wrappers.lambdaQuery(PlayWheelTerm.class).eq(PlayWheelTerm::getUserId, orderEntity.getUserId()).eq(PlayWheelTerm::getOrderNo,
                    orderEntity.getOrderNo());
            PlayWheelTerm playWheelTerm = playWheelTermService.getOne(q);

            if (playWheelTerm == null) {
                log.info("转盘->当前订单号:{}", orderEntity.getOrderNo());
                return;
            }
            Currency currency = currencyService.getById(orderEntity.getCurrencyId());
            // -----------------------------  t_user_count_order  操作 -----------------------------------------
            UserCountOrder userCountOrder = this.getUserCountOrder(orderEntity);
//            log.info("转盘-->当前订单信息: {}", userCountOrder.toString());
            wheelMqService.computeWheelUserCountOrder(currency, orderEntity, userCountOrder, amountVo);
            // -----------------------------  t_channel_count_order  操作 -----------------------------------------
            ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
//            log.info("转盘-->当前订单渠道信息: {}", channelCountOrder.toString());
            wheelMqService.computeWheelChannelCountOrder(currency, orderEntity, channelCountOrder, amountVo);
            // -----------------------------  t_count_order  操作 -----------------------------------------
            CountOrder countOrder = this.getCountOrder(orderEntity);
            wheelMqService.computeWheelCountOrder(currency, orderEntity, countOrder, amountVo);
        } catch (Exception e) {
            log.error("getWheel -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
            throw e;
        }
    }

    /**
     * 红包领取
     *
     * @param orderEntity
     */
    public void getReceive(OrderEntity orderEntity) {
        try {
            //当前账户余额
            ConsumOrderVO amount = getAmount(orderEntity);
            //查询红包发送
            LambdaQueryWrapper<OrderRedEnvelopeReceive> queryWrapper = Wrappers.lambdaQuery(OrderRedEnvelopeReceive.class);
            queryWrapper.eq(OrderRedEnvelopeReceive::getOrderNo, orderEntity.getOrderNo());
            OrderRedEnvelopeReceive envelopeSend = orderRedEnvelopeReceiveService.getOne(queryWrapper);
            if (envelopeSend == null) {
                log.info("红包领取->当前订单号:{}", orderEntity.getOrderNo());
                return;
            }
            Integer redType = envelopeSend.getRedType();
            Currency currency = currencyService.getById(orderEntity.getCurrencyId());
            UserCountOrder userCountOrder = this.getUserCountOrder(orderEntity);
            log.info("uuid={}, 红包领取--> 当前用户订单统计数据={}", orderEntity.getUuid(), Objects.isNull(userCountOrder) ? null : userCountOrder.toString());
            if (Objects.isNull(userCountOrder)) {
                userCountOrder = new UserCountOrder();
                userCountOrder.setUserId(orderEntity.getUserId());//用户id
                userCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                userCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                userCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    userCountOrder.setItemId(currency.getItemId());
                    userCountOrder.setChainTag(currency.getChainTag());
                }
                userCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                userCountOrder.setUserEarnings(orderEntity.getMoney());//userEarnings
                userCountOrder.setTotalPacketAmount(orderEntity.getMoney());//红包彩金
                userCountOrder.setCustomerLossAmount(orderEntity.getMoney());
                userCountOrder.setCalculationsTime(new Date());//操作时间
                userCountOrder.setCreateTime(new Date());
                userCountOrder.setUpdateTime(new Date());
                if (redType == 1) { //1 私人红包,
                    userCountOrder.setPrivatePersonNum(1);
                    userCountOrder.setPrivatePersonAmount(orderEntity.getMoney());
                } else if (redType == 2) {//2 群红包
                    userCountOrder.setGroupNum(1);
                    userCountOrder.setGroupAmount(orderEntity.getMoney());
                } else if (redType == 3) {//,3 新人红包
                    userCountOrder.setNewPersonNum(1);
                    userCountOrder.setNewPersonAmount(orderEntity.getMoney());
                }else if (redType == 4) {//,4 超级红包
                    userCountOrder.setSuperNum(1);
                    userCountOrder.setSuperAmount(orderEntity.getMoney());
                }
                int result = userCountOrderMapper.saveUserCountOrderData(userCountOrder);
                log.info("uuid={}, 红包领取->添加用户数据: {} 结果: {}", orderEntity.getUuid(), userCountOrder, result);
            } else {
                UserCountOrder updateUserCountOrder = new UserCountOrder();
                updateUserCountOrder.setId(userCountOrder.getId());
                updateUserCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
//                updateUserCountOrder.setBonusAmount(orderEntity.getMoney());//彩金
                updateUserCountOrder.setTotalPacketAmount(orderEntity.getMoney());//红包彩金
                updateUserCountOrder.setUserEarnings(orderEntity.getMoney());//用户收入
                updateUserCountOrder.setCustomerLossAmount(orderEntity.getMoney());
                updateUserCountOrder.setCalculationsTime(new Date());//操作时间
                updateUserCountOrder.setUpdateTime(new Date());//操作时间
                if (redType == 1) { //1 私人红包,
                    updateUserCountOrder.setPrivatePersonNum(1);
                    updateUserCountOrder.setPrivatePersonAmount(orderEntity.getMoney());
                } else if (redType == 2) {//2 群红包
                    updateUserCountOrder.setGroupNum(1);
                    updateUserCountOrder.setGroupAmount(orderEntity.getMoney());
                } else if (redType == 3) {//,3 新人红包
                    updateUserCountOrder.setNewPersonNum(1);
                    updateUserCountOrder.setNewPersonAmount(orderEntity.getMoney());
                }else if (redType == 4) {//,4 超级红包
                    updateUserCountOrder.setSuperNum(1);
                    updateUserCountOrder.setSuperAmount(orderEntity.getMoney());
                }
                int result = userCountOrderMapper.updateUserCountOrderData(updateUserCountOrder);
                log.info("uuid={}, 红包领取->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), userCountOrder, result);
            }

            // -----------------------------  t_channel_count_order  操作 -----------------------------------------
            //查询t_channel_count_order 渠道 是否有数据 有 累加 否 添加
            ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
            log.info("uuid={}, 红包领取-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(channelCountOrder) ? null : channelCountOrder.toString());
            if (channelCountOrder == null) {
                channelCountOrder = new ChannelCountOrder();
                channelCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                channelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                channelCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                channelCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    channelCountOrder.setItemId(currency.getItemId());
                    channelCountOrder.setChainTag(currency.getChainTag());
                }
                channelCountOrder.setCreateTime(new Date());//操作时间
                channelCountOrder.setUpdateTime(new Date());//操作时间
                channelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
//                channelCountOrder.setBonusAmount(orderEntity.getMoney());//彩金
                channelCountOrder.setTotalPacketAmount(orderEntity.getMoney());//红包彩金
                channelCountOrder.setUserEarnings(orderEntity.getMoney());//用户收入
                channelCountOrder.setCustomerLossAmount(orderEntity.getMoney());
                if (redType == 1) { //1 私人红包,
                    channelCountOrder.setPrivatePersonNum(1);
                    channelCountOrder.setPrivatePersonAmount(orderEntity.getMoney());
                } else if (redType == 2) {//2 群红包
                    channelCountOrder.setGroupNum(1);
                    channelCountOrder.setGroupAmount(orderEntity.getMoney());
                } else if (redType == 3) {//,3 新人红包
                    channelCountOrder.setNewPersonNum(1);
                    channelCountOrder.setNewPersonAmount(orderEntity.getMoney());
                }else if (redType == 4) {//,4 超级红包
                    channelCountOrder.setSuperNum(1);
                    channelCountOrder.setSuperAmount(orderEntity.getMoney());
                }
                channelCountOrder.setCalculationsTime(new Date());
                channelCountOrder.setUpdateTime(new Date());
                int result = channelCountOrderMapper.insertChannelCountOrderDataV2(channelCountOrder);
                log.info("uuid={}, 红包领取->添加用户数据: {} 结果: {}", orderEntity.getUuid(), channelCountOrder, result);
            } else {
                ChannelCountOrder updateChannelCountOrder = new ChannelCountOrder();
                updateChannelCountOrder.setId(channelCountOrder.getId());
                updateChannelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
//                updateChannelCountOrder.setBonusAmount(orderEntity.getMoney());//彩金
                updateChannelCountOrder.setTotalPacketAmount(orderEntity.getMoney());//红包彩金
                updateChannelCountOrder.setUserEarnings(orderEntity.getMoney());//用户收入
                updateChannelCountOrder.setCustomerLossAmount(orderEntity.getMoney());//客损
                updateChannelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                updateChannelCountOrder.setCalculationsTime(new Date());
                updateChannelCountOrder.setUpdateTime(new Date());
                if (redType == 1) { //1 私人红包,
                    updateChannelCountOrder.setPrivatePersonNum(1);
                    updateChannelCountOrder.setPrivatePersonAmount(orderEntity.getMoney());
                } else if (redType == 2) {//2 群红包
                    updateChannelCountOrder.setGroupNum(1);
                    updateChannelCountOrder.setGroupAmount(orderEntity.getMoney());
                } else if (redType == 3) {//,3 新人红包
                    updateChannelCountOrder.setNewPersonNum(1);
                    updateChannelCountOrder.setNewPersonAmount(orderEntity.getMoney());
                }else if (redType == 4) {//,4 超级红包
                    updateChannelCountOrder.setSuperNum(1);
                    updateChannelCountOrder.setSuperAmount(orderEntity.getMoney());
                }
                int result = channelCountOrderMapper.updateChannelCountOrderDataV2(updateChannelCountOrder);
                log.info("uuid={}, 红包领取->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), userCountOrder, result);
            }

            // -----------------------------  t_count_order  操作 -----------------------------------------
            CountOrder countOrder = this.getCountOrder(orderEntity);
            log.info("uuid={}, 红包领取-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(countOrder) ? null : countOrder.toString());
            if (countOrder == null) {
                countOrder = new CountOrder();
                countOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                countOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    countOrder.setItemId(currency.getItemId());
                    countOrder.setChainTag(currency.getChainTag());
                }
                countOrder.setCreateTime(new Date());//操作时间
                countOrder.setUpdateTime(new Date());//操作时间
                countOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                countOrder.setUserEarnings(orderEntity.getMoney());//用户收入
//                countOrder.setBonusAmount(orderEntity.getMoney());//彩金
                countOrder.setTotalPacketAmount(orderEntity.getMoney());//红包彩金
                countOrder.setCustomerLossAmount(orderEntity.getMoney());
                countOrder.setCalculationsTime(new Date());//操作时间
                countOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));

                if (redType == 1) { //1 私人红包,
                    countOrder.setPrivatePersonNum(1);
                    countOrder.setPrivatePersonAmount(orderEntity.getMoney());
                } else if (redType == 2) {//2 群红包
                    countOrder.setGroupNum(1);
                    countOrder.setGroupAmount(orderEntity.getMoney());
                } else if (redType == 3) {//,3 新人红包
                    countOrder.setNewPersonNum(1);
                    countOrder.setNewPersonAmount(orderEntity.getMoney());
                }else if (redType == 4) {//,4 超级红包
                    countOrder.setSuperNum(1);
                    countOrder.setSuperAmount(orderEntity.getMoney());
                }

                int result = countOrderMapper.insertCountOrderDataV2(countOrder);
                log.info("uuid={}, 红包领取->添加用户数据: {} 结果: {}", orderEntity.getUuid(), countOrder, result);
            } else {
                CountOrder updateCountOrder = new CountOrder();
                updateCountOrder.setId(countOrder.getId());
                updateCountOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                updateCountOrder.setTotalPacketAmount(orderEntity.getMoney());//红包彩金
                updateCountOrder.setUserEarnings(orderEntity.getMoney());//用户收入
//                updateCountOrder.setBonusAmount(orderEntity.getMoney());//彩金
                updateCountOrder.setCustomerLossAmount(orderEntity.getMoney());
                updateCountOrder.setCalculationsTime(new Date());//操作时间
                updateCountOrder.setUpdateTime(new Date());//操作时间
                updateCountOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                if (redType == 1) { //1 私人红包,
                    updateCountOrder.setPrivatePersonNum(1);
                    updateCountOrder.setPrivatePersonAmount(orderEntity.getMoney());
                } else if (redType == 2) {//2 群红包
                    updateCountOrder.setGroupNum(1);
                    updateCountOrder.setGroupAmount(orderEntity.getMoney());
                } else if (redType == 3) {//,3 新人红包
                    updateCountOrder.setNewPersonNum(1);
                    updateCountOrder.setNewPersonAmount(orderEntity.getMoney());
                }else if (redType == 4) {//,4 超级红包
                    updateCountOrder.setSuperNum(1);
                    updateCountOrder.setSuperAmount(orderEntity.getMoney());
                }
                int result = countOrderMapper.updateCountOrderDataV2(updateCountOrder);
                log.info("uuid={}, 红包领取->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateCountOrder, result);
            }
        } catch (Exception e) {
            log.error("getReceive -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
            throw e;
        }

    }

    private UserCountOrder getUserCountOrder(OrderEntity orderEntity) {
        //`day_str`,`user_id`,`currency_id
        LambdaQueryWrapper<UserCountOrder> queryWrapper = Wrappers.lambdaQuery(UserCountOrder.class).eq(UserCountOrder::getDayStr, orderEntity.getNowDay()).eq(UserCountOrder::getUserId,
                orderEntity.getUserId()).eq(UserCountOrder::getCurrencyId, orderEntity.getCurrencyId());
//                .last("for update");
        UserCountOrder userCountOrder = userCountOrderService.getOne(queryWrapper);
        log.info("getUserCountOrder -> userId={}, nowDay={}, currencyId={}, 响应={}", orderEntity.getUserId(), orderEntity.getNowDay(), orderEntity.getCurrencyId(),
                Objects.isNull(userCountOrder) ? null : JSON.toJSONString(userCountOrder));
        return userCountOrder;
    }

    public OrderAmount getOrderAmountByOrderNoAndStatus(String orderNo, int orderStatus) {
        LambdaQueryWrapper<OrderAmount> queryWrapper = Wrappers.lambdaQuery(OrderAmount.class).eq(OrderAmount::getOrderNo, orderNo).eq(OrderAmount::getOrderStatus, orderStatus);
        return orderAmountService.getOne(queryWrapper);
    }

    //处理上级返水数据
    private void handleSuperRebateData(OrderTerm orderTerm, OrderEntity orderEntity) {
        //上级用户ID
        if (Objects.nonNull(orderTerm.getSuperUserId()) && Objects.nonNull(orderTerm.getSuperRebate())) {
            LambdaQueryWrapper<TUser> userQuery = Wrappers.lambdaQuery(TUser.class);
            userQuery.eq(TUser::getUserId, orderTerm.getSuperUserId());
            TUser superUser = userService.getOne(userQuery);
            if (Objects.nonNull(superUser)) {
                LambdaQueryWrapper<UserCountOrder> superUserQueryWrapper =
                        Wrappers.lambdaQuery(UserCountOrder.class).eq(UserCountOrder::getDayStr, orderEntity.getNowDay()).eq(UserCountOrder::getUserId, orderTerm.getSuperUserId()).eq(UserCountOrder::getCurrencyId, orderEntity.getCurrencyId());
//                        .last("for update");
                UserCountOrder superUserCountOrder = userCountOrderService.getOne(superUserQueryWrapper);
                log.info("uuid={}, 上级返佣数据, superUserCountOrder={}", orderEntity.getUuid(), Objects.isNull(superUserCountOrder) ? null : JSON.toJSONString(superUserCountOrder));
                if (Objects.isNull(superUserCountOrder)) {
                    this.saveSuperUserCountOrder(orderTerm, orderEntity);
                } else {
                    this.updateSuperUserCountOrder(orderTerm, orderEntity, superUserCountOrder);
                }
            }
        }
    }

    private void saveSuperUserCountOrder(OrderTerm orderTerm, OrderEntity orderEntity) {
        UserCountOrder superUserCountOrder = new UserCountOrder();
        superUserCountOrder.setUserId(orderTerm.getSuperUserId());//用户id
        superUserCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
        superUserCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
        superUserCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
//        superUserCountOrder.setWaitReturnCommissionAmount(orderTerm.getSuperRebate());//上级的返佣
        int result = userCountOrderMapper.saveUserCountOrderWaitReturnCommissionAmountData(orderEntity.getNowDay(), orderTerm.getSuperUserId(), orderEntity.getCurrencyId(),
                orderEntity.getChannelId(), orderTerm.getSuperRebate());
        log.info("uuid={},上级返佣数据处理, 创建, result={}, superUserCountOrder={}", orderEntity.getUuid(), result, JSON.toJSONString(superUserCountOrder));
    }

    private void updateSuperUserCountOrder(OrderTerm orderTerm, OrderEntity orderEntity, UserCountOrder superUserCountOrder) {
        UserCountOrder updateSuperUserCountOrder = new UserCountOrder();
        updateSuperUserCountOrder.setId(superUserCountOrder.getId());
//        updateSuperUserCountOrder.setWaitReturnCommissionAmount(superUserCountOrder.getWaitReturnCommissionAmount().add(orderTerm.getSuperRebate()));
        int result = userCountOrderMapper.updateUserCountOrderWaitReturnCommissionAmountData(superUserCountOrder.getId(), orderTerm.getSuperRebate());
        log.info("uuid={}, 上级返佣数据处理, 修改, result={}, superUserCountOrder={}", orderEntity.getUuid(), result, JSON.toJSONString(updateSuperUserCountOrder));
    }

    private CountOrder getCountOrder(OrderEntity orderEntity) {
        //(`day_str`,`currency_id`)
        LambdaQueryWrapper<CountOrder> queryWrapper3 = Wrappers.lambdaQuery(CountOrder.class).eq(CountOrder::getDayStr, orderEntity.getNowDay()).eq(CountOrder::getCurrencyId,
                orderEntity.getCurrencyId());
//                .last("limit 1 for update");
        return tCountOrderService.getOne(queryWrapper3);
    }

    private ChannelCountOrder getChannelCountOrder(OrderEntity orderEntity) {
        LambdaQueryWrapper<ChannelCountOrder> queryWrapper2 =
                Wrappers.lambdaQuery(ChannelCountOrder.class)
                        .eq(ChannelCountOrder::getDayStr, orderEntity.getNowDay())
                        .eq(ChannelCountOrder::getCurrencyId, orderEntity.getCurrencyId())
                        .eq(ChannelCountOrder::getChannelId, orderEntity.getChannelId());
        ChannelCountOrder channelCountOrder = channelCountOrderService.getOne(queryWrapper2);
        log.info("getChannelCountOrder -> channelId={}, nowDay={}, currencyId={}, 响应={}", orderEntity.getChannelId(), orderEntity.getNowDay(), orderEntity.getCurrencyId(),
                Objects.isNull(channelCountOrder) ? null : JSON.toJSONString(channelCountOrder));
        return channelCountOrder;
    }

    /**
     * 获取整用户结余 渠道用户
     */
    private ConsumOrderVO getAmount(OrderEntity orderEntity) {

        ConsumOrderVO consumOrderVO = new ConsumOrderVO();
        // 用户结余
        if (orderEntity.getUserId() != null) {
            BigDecimal userAmount = userWalletMapper.countUserAmount(orderEntity.getCurrencyId(), orderEntity.getUserId());
            consumOrderVO.setUserAmount(userAmount);
        } else {
            consumOrderVO.setUserAmount(BigDecimal.ZERO);
        }
        consumOrderVO.setChannelAmount(BigDecimal.ZERO);
        consumOrderVO.setOrderAmount(BigDecimal.ZERO);
        // 用户结余
//        BigDecimal userAmount = userWalletMapper.countUserAmount(orderEntity.getCurrencyId(), orderEntity.getUserId());
//        // 获取渠道用户数据
//        LambdaQueryWrapper<TUser> query = Wrappers.lambdaQuery(TUser.class);
//        query.eq(TUser::getChannelId, orderEntity.getChannelId());
//        int count = userService.count(query);
//        //查询当前去渠道的所有用户
//        if (count == 0) {
//            consumOrderVO.setChannelAmount(BigDecimal.ZERO);
//        } else {
////            List<Long> userIds = userList.stream().map(TUser::getUserId).collect(Collectors.toList());
////            BigDecimal channelAmount = userWalletMapper.countChannelAmount(userIds);
//            BigDecimal channelAmount = userWalletMapper.countChannelAmount(orderEntity.getChannelId());
//            //渠道下用户总结余
//            consumOrderVO.setChannelAmount(channelAmount);
//        }
//        //t_count_order
//        BigDecimal orderAmount = userWalletMapper.countOrderAmount(orderEntity.getCurrencyId());
//        consumOrderVO.setUserAmount(userAmount);
//        consumOrderVO.setOrderAmount(orderAmount);
        log.info("getAmount -> consumOrderVO={}", consumOrderVO);
        return consumOrderVO;
    }

    public Long setShareholderIdFromChannel(OrderEntity orderEntity) {
        Channel channel = channelMapper.selectChannelById(orderEntity.getChannelId());
        if (channel != null && channel.getShareholderId() != null) {
            log.info("当前渠道信息channel -> channel={}", channel);
            return channel.getShareholderId();
        }
        return null;
    }

    /****
     * 转账
     * @param orderEntity
     */
    public void getTransferSend(OrderEntity orderEntity) {
        try {
            //当前账户余额
            ConsumOrderVO amount = getAmount(orderEntity);
            //查询账变记录
            LambdaQueryWrapper<AmountChange> queryWrapper = Wrappers.lambdaQuery(AmountChange.class).eq(AmountChange::getUserId, orderEntity.getUserId()).eq(AmountChange::getOrderNo,
                    orderEntity.getOrderNo()).eq(AmountChange::getOrderType, 12).eq(AmountChange::getType, 21).eq(AmountChange::getCurrencyId, Integer.valueOf(orderEntity.getCurrencyId()));
            AmountChange amountChange = tAmountChangeService.getOne(queryWrapper);
            log.info("uuid={}, 转账 彩金赠送-->当前账变信息: {}", orderEntity.getUuid(), Objects.isNull(amountChange) ? null : amountChange.toString());
            if (amountChange == null) {
                log.info("uuid={}, 转账 彩金赠送-->当前账变信息:{}", orderEntity.getUuid(), orderEntity.getOrderNo());
                return;
            }
            Currency currency = currencyService.getById(orderEntity.getCurrencyId());
            // -----------------------------  t_user_count_order  操作 -----------------------------------------
            UserCountOrder userCountOrder = this.getUserCountOrder(orderEntity);
            log.info("uuid={}, 转账 彩金赠送-->当前订单信息: {}", orderEntity.getUuid(), Objects.isNull(userCountOrder) ? null : userCountOrder.toString());
            if (userCountOrder == null) {
                userCountOrder = new UserCountOrder();
                userCountOrder.setUserId(orderEntity.getUserId());//用户id
                userCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                userCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                userCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    userCountOrder.setItemId(currency.getItemId());
                    userCountOrder.setChainTag(currency.getChainTag());
                }
                userCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                userCountOrder.setUserExpenses(orderEntity.getBonusAmount());//用户支出
                userCountOrder.setCalculationsTime(new Date());//操作时间
                userCountOrder.setCreateTime(new Date());
                userCountOrder.setUpdateTime(new Date());
                int result = userCountOrderMapper.saveUserCountOrderData(userCountOrder);
                log.info("uuid={}, 转账 彩金赠送->添加用户数据: {} 结果: {}", orderEntity.getUuid(), userCountOrder, result);
            } else {
                UserCountOrder updateUserCountOrder = new UserCountOrder();
                updateUserCountOrder.setId(userCountOrder.getId());
                updateUserCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                updateUserCountOrder.setUserExpenses(orderEntity.getBonusAmount());//用户支出
                updateUserCountOrder.setCalculationsTime(new Date());//操作时间
                int result = userCountOrderMapper.updateUserCountOrderData(updateUserCountOrder);
                log.info("转账 彩金赠送->更新用户数据: {} 更新结果: {}", updateUserCountOrder, result);
            }
            // -----------------------------  t_channel_count_order  操作 -----------------------------------------
            //查询t_channel_count_order 渠道 是否有数据 有 累加 否 添加
            ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
            log.info("uuid={}, 转账 彩金赠送-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(channelCountOrder) ? null : channelCountOrder.toString());
            if (channelCountOrder == null) {
                channelCountOrder = new ChannelCountOrder();
                channelCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                channelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                channelCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                channelCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    channelCountOrder.setItemId(currency.getItemId());
                    channelCountOrder.setChainTag(currency.getChainTag());
                }
                channelCountOrder.setCreateTime(new Date());//操作时间
                channelCountOrder.setUpdateTime(new Date());//操作时间
                channelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
                channelCountOrder.setUserExpenses(orderEntity.getBonusAmount());//用户支出
                channelCountOrder.setCalculationsTime(new Date());//操作时间
                channelCountOrder.setCreateTime(new Date());
                channelCountOrder.setUpdateTime(new Date());
                int result = channelCountOrderMapper.insertChannelCountOrderDataV2(channelCountOrder);
                log.info("uuid={}, 转账 彩金赠送->添加用户数据: {} 结果: {}", orderEntity.getUuid(), channelCountOrder, result);
            } else {
                ChannelCountOrder updateChannelCountOrder = new ChannelCountOrder();
                updateChannelCountOrder.setId(channelCountOrder.getId());
                updateChannelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
                updateChannelCountOrder.setUserExpenses(orderEntity.getBonusAmount());//用户支出
                updateChannelCountOrder.setCalculationsTime(new Date());//操作时间
                updateChannelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                updateChannelCountOrder.setUpdateTime(new Date());//操作时间
                int result = channelCountOrderMapper.updateChannelCountOrderDataV2(updateChannelCountOrder);
                log.info("uuid={}, 转账 彩金赠送->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateChannelCountOrder, result);
            }
            // -----------------------------  t_count_order  操作 -----------------------------------------
            CountOrder countOrder = this.getCountOrder(orderEntity);
            log.info("uuid={}, 转账 彩金赠送-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(countOrder) ? null : countOrder.toString());
            if (countOrder == null) {
                countOrder = new CountOrder();
                countOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                countOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    countOrder.setItemId(currency.getItemId());
                    countOrder.setChainTag(currency.getChainTag());
                }
                countOrder.setCreateTime(new Date());//操作时间
                countOrder.setUpdateTime(new Date());//操作时间
                countOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                countOrder.setUserExpenses(orderEntity.getBonusAmount());//用户支出
                countOrder.setCalculationsTime(new Date());//操作时间
                countOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                int result = countOrderMapper.insertCountOrderDataV2(countOrder);
                log.info("uuid={}, 转账 彩金赠送->添加用户数据: {} 结果: {}", orderEntity.getUuid(), countOrder, result);
            } else {
                CountOrder updateCountOrder = new CountOrder();
                updateCountOrder.setId(countOrder.getId());
                updateCountOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                updateCountOrder.setUserExpenses(orderEntity.getBonusAmount());//用户支出
                updateCountOrder.setCalculationsTime(new Date());//操作时间
                updateCountOrder.setUpdateTime(new Date());//操作时间
                updateCountOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                int result = countOrderMapper.updateCountOrderDataV2(updateCountOrder);
                log.info("uuid={}, 转账 彩金赠送: {} 更新结果: {}", orderEntity.getUuid(), updateCountOrder, result);
            }
        } catch (Exception e) {
            log.error("getTransferSend -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
            throw e;
        }
    }

    /****
     * 转账接收
     * @param orderEntity
     */
    public void getTransferReceive(OrderEntity orderEntity) {
        try {
            //当前账户余额
            ConsumOrderVO amount = getAmount(orderEntity);
            //查询账变记录
            LambdaQueryWrapper<AmountChange> queryWrapper = Wrappers.lambdaQuery(AmountChange.class).eq(AmountChange::getUserId, orderEntity.getUserId()).eq(AmountChange::getOrderNo,
                    orderEntity.getOrderNo()).eq(AmountChange::getOrderType, 12).eq(AmountChange::getType, 22).eq(AmountChange::getCurrencyId, orderEntity.getCurrencyId());
            AmountChange amountChange = tAmountChangeService.getOne(queryWrapper);
            log.info("uuid={}, 转账 转账接受-->当前账变信息: {}", orderEntity.getUuid(), Objects.isNull(amountChange) ? null : amountChange.toString());
            if (amountChange == null) {
                log.info("uuid={}, 转账 转账接受-->当前账变信息:{}", orderEntity.getUuid(), orderEntity.getOrderNo());
                return;
            }
            Currency currency = currencyService.getById(orderEntity.getCurrencyId());
            // -----------------------------  t_user_count_order  操作 -----------------------------------------
            UserCountOrder userCountOrder = this.getUserCountOrder(orderEntity);
            log.info("uuid={}, 转账 转账接受-->当前订单信息: {}", orderEntity.getUuid(), Objects.isNull(userCountOrder) ? null : userCountOrder.toString());
            if (userCountOrder == null) {
                userCountOrder = new UserCountOrder();
                userCountOrder.setUserId(orderEntity.getUserId());//用户id
                userCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                userCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                userCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    userCountOrder.setItemId(currency.getItemId());
                    userCountOrder.setChainTag(currency.getChainTag());
                }
                userCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                userCountOrder.setUserEarnings(orderEntity.getBonusAmount());//用户收入
                userCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());//客损
                userCountOrder.setTransferBonusAmount(orderEntity.getBonusAmount());//转账彩金
                userCountOrder.setCalculationsTime(new Date());//操作时间
                userCountOrder.setCreateTime(new Date());
                userCountOrder.setUpdateTime(new Date());
                int result = userCountOrderMapper.saveUserCountOrderData(userCountOrder);
                log.info("uuid={}, 转账 转账接受->添加用户数据: {} 结果: {}", orderEntity.getUuid(), userCountOrder, result);
            } else {
                UserCountOrder updateUserCountOrder = new UserCountOrder();
                updateUserCountOrder.setId(userCountOrder.getId());
                updateUserCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                updateUserCountOrder.setUserEarnings(orderEntity.getBonusAmount());//用户收入
                updateUserCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());//客损
                updateUserCountOrder.setTransferBonusAmount(orderEntity.getBonusAmount());//转账彩金
                updateUserCountOrder.setCalculationsTime(new Date());//操作时间
                int result = userCountOrderMapper.updateUserCountOrderData(updateUserCountOrder);
                log.info("转账 转账接受->更新用户数据: {} 更新结果: {}", updateUserCountOrder, result);
            }
            // -----------------------------  t_channel_count_order  操作 -----------------------------------------
            //查询t_channel_count_order 渠道 是否有数据 有 累加 否 添加
            ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
            log.info("uuid={}, 转账 转账接受-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(channelCountOrder) ? null : channelCountOrder.toString());
            if (channelCountOrder == null) {
                channelCountOrder = new ChannelCountOrder();
                channelCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                channelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                channelCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                channelCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    channelCountOrder.setItemId(currency.getItemId());
                    channelCountOrder.setChainTag(currency.getChainTag());
                }
                channelCountOrder.setCreateTime(new Date());//操作时间
                channelCountOrder.setUpdateTime(new Date());//操作时间
                channelCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                channelCountOrder.setUserEarnings(orderEntity.getBonusAmount());//用户收入
                channelCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());//客损
                channelCountOrder.setTransferBonusAmount(orderEntity.getBonusAmount());//转账彩金
                channelCountOrder.setCalculationsTime(new Date());//操作时间
                channelCountOrder.setCreateTime(new Date());
                channelCountOrder.setUpdateTime(new Date());
                int result = channelCountOrderMapper.insertChannelCountOrderDataV2(channelCountOrder);
                log.info("uuid={}, 转账 转账接受->添加用户数据: {} 结果: {}", orderEntity.getUuid(), channelCountOrder, result);
            } else {
                ChannelCountOrder updateChannelCountOrder = new ChannelCountOrder();
                updateChannelCountOrder.setId(channelCountOrder.getId());
                updateChannelCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                updateChannelCountOrder.setUserEarnings(orderEntity.getBonusAmount());//用户收入
                updateChannelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                updateChannelCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());//客损
                updateChannelCountOrder.setTransferBonusAmount(orderEntity.getBonusAmount());//转账彩金
                updateChannelCountOrder.setCalculationsTime(new Date());//操作时间
                updateChannelCountOrder.setUpdateTime(new Date());//操作时间
                int result = channelCountOrderMapper.updateChannelCountOrderDataV2(updateChannelCountOrder);
                log.info("uuid={}, 转账 转账接受->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateChannelCountOrder, result);
            }
            // -----------------------------  t_count_order  操作 -----------------------------------------
            CountOrder countOrder = this.getCountOrder(orderEntity);
            log.info("uuid={}, 转账 转账接受-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(countOrder) ? null : countOrder.toString());
            if (countOrder == null) {
                countOrder = new CountOrder();
                countOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                countOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    countOrder.setItemId(currency.getItemId());
                    countOrder.setChainTag(currency.getChainTag());
                }
                countOrder.setCreateTime(new Date());//操作时间
                countOrder.setUpdateTime(new Date());//操作时间
                countOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                countOrder.setUserEarnings(orderEntity.getBonusAmount());//用户收入
                countOrder.setCustomerLossAmount(orderEntity.getBonusAmount());//客损
                countOrder.setTransferBonusAmount(orderEntity.getBonusAmount());//转账彩金
                countOrder.setCalculationsTime(new Date());//操作时间
                countOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                int result = countOrderMapper.insertCountOrderDataV2(countOrder);
                log.info("uuid={}, 转账 转账接受->添加用户数据: {} 结果: {}", orderEntity.getUuid(), countOrder, result);
            } else {
                CountOrder updateCountOrder = new CountOrder();
                updateCountOrder.setId(countOrder.getId());
                updateCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                updateCountOrder.setUserEarnings(orderEntity.getBonusAmount());//用户收入
                updateCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());//客损
                updateCountOrder.setTransferBonusAmount(orderEntity.getBonusAmount());//转账彩金
                updateCountOrder.setCalculationsTime(new Date());//操作时间
                updateCountOrder.setUpdateTime(new Date());//操作时间
                updateCountOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                int result = countOrderMapper.updateCountOrderDataV2(updateCountOrder);
                log.info("uuid={}, 转账 转账接受: {} 更新结果: {}", orderEntity.getUuid(), updateCountOrder, result);
            }
        } catch (Exception e) {
            log.error("getTransferReceive -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
            throw e;
        }
    }

    /**
     * 设置用户游戏类型打码量字段
     *
     * @param userCountGameCode 目标对象
     * @param orderTerm         游戏订单
     */
    private void setGameTypeCodeField(UserCountGameCode userCountGameCode, OrderTerm orderTerm) {
        BigDecimal codeAmount = orderTerm.getCodeAmount() != null ? orderTerm.getCodeAmount() : BigDecimal.ZERO;
        BigDecimal betAmount = orderTerm.getBetAmount() != null ? orderTerm.getBetAmount() : BigDecimal.ZERO;
        BigDecimal settleAmount = orderTerm.getWin() != null ? orderTerm.getWin() : BigDecimal.ZERO;
        BigDecimal winLoss = orderTerm.getWinLoss() != null ? orderTerm.getWinLoss() : BigDecimal.ZERO;
        switch (orderTerm.getGameTypeCode()) {
            case "1":
                userCountGameCode.setGameTypeCode1(codeAmount);
                userCountGameCode.setGameTypeBet1(betAmount);
                userCountGameCode.setGameTypeSettle1(settleAmount);
                userCountGameCode.setGameTypeWinLoss1(winLoss);
                break;
            case "2":
                userCountGameCode.setGameTypeCode2(codeAmount);
                userCountGameCode.setGameTypeBet2(betAmount);
                userCountGameCode.setGameTypeSettle2(settleAmount);
                userCountGameCode.setGameTypeWinLoss2(winLoss);
                break;
            case "3":
                userCountGameCode.setGameTypeCode3(codeAmount);
                userCountGameCode.setGameTypeBet3(betAmount);
                userCountGameCode.setGameTypeSettle3(settleAmount);
                userCountGameCode.setGameTypeWinLoss3(winLoss);
                break;
            case "4":
                userCountGameCode.setGameTypeCode4(codeAmount);
                userCountGameCode.setGameTypeBet4(betAmount);
                userCountGameCode.setGameTypeSettle4(settleAmount);
                userCountGameCode.setGameTypeWinLoss4(winLoss);
                break;
            case "5":
                userCountGameCode.setGameTypeCode5(codeAmount);
                userCountGameCode.setGameTypeBet5(betAmount);
                userCountGameCode.setGameTypeSettle5(settleAmount);
                userCountGameCode.setGameTypeWinLoss5(winLoss);
                break;
            case "6":
                userCountGameCode.setGameTypeCode6(codeAmount);
                userCountGameCode.setGameTypeBet6(betAmount);
                userCountGameCode.setGameTypeSettle6(settleAmount);
                userCountGameCode.setGameTypeWinLoss6(winLoss);
                break;
            case "7":
                userCountGameCode.setGameTypeCode7(codeAmount);
                userCountGameCode.setGameTypeBet7(betAmount);
                userCountGameCode.setGameTypeSettle7(settleAmount);
                userCountGameCode.setGameTypeWinLoss7(winLoss);
                break;
            case "8":
                userCountGameCode.setGameTypeCode8(codeAmount);
                userCountGameCode.setGameTypeBet8(betAmount);
                userCountGameCode.setGameTypeSettle8(settleAmount);
                userCountGameCode.setGameTypeWinLoss8(winLoss);
                break;
            case "9":
                userCountGameCode.setGameTypeCode9(codeAmount);
                userCountGameCode.setGameTypeBet9(betAmount);
                userCountGameCode.setGameTypeSettle9(settleAmount);
                userCountGameCode.setGameTypeWinLoss9(winLoss);
                break;
            default:
                log.warn("未知游戏类型码: {}", orderTerm.getGameTypeCode());
        }
    }

    /**
     * 设置用户游戏类型打码量字段
     *
     * @param channelCountGameCode 目标对象
     * @param orderTerm            游戏订单
     */
    private void setChannelGameTypeCodeField(ChannelCountGameCode channelCountGameCode, OrderTerm orderTerm) {
        BigDecimal codeAmount = orderTerm.getCodeAmount() != null ? orderTerm.getCodeAmount() : BigDecimal.ZERO;
        BigDecimal betAmount = orderTerm.getBetAmount() != null ? orderTerm.getBetAmount() : BigDecimal.ZERO;
        BigDecimal settleAmount = orderTerm.getWin() != null ? orderTerm.getWin() : BigDecimal.ZERO;
        BigDecimal winLoss = orderTerm.getWinLoss() != null ? orderTerm.getWinLoss() : BigDecimal.ZERO;
        switch (orderTerm.getGameTypeCode()) {
            case "1":
                channelCountGameCode.setGameTypeCode1(codeAmount);
                channelCountGameCode.setGameTypeBet1(betAmount);
                channelCountGameCode.setGameTypeSettle1(settleAmount);
                channelCountGameCode.setGameTypeWinLoss1(winLoss);
                break;
            case "2":
                channelCountGameCode.setGameTypeCode2(codeAmount);
                channelCountGameCode.setGameTypeBet2(betAmount);
                channelCountGameCode.setGameTypeSettle2(settleAmount);
                channelCountGameCode.setGameTypeWinLoss2(winLoss);
                break;
            case "3":
                channelCountGameCode.setGameTypeCode3(codeAmount);
                channelCountGameCode.setGameTypeBet3(betAmount);
                channelCountGameCode.setGameTypeSettle3(settleAmount);
                channelCountGameCode.setGameTypeWinLoss3(winLoss);
                break;
            case "4":
                channelCountGameCode.setGameTypeCode4(codeAmount);
                channelCountGameCode.setGameTypeBet4(betAmount);
                channelCountGameCode.setGameTypeSettle4(settleAmount);
                channelCountGameCode.setGameTypeWinLoss4(winLoss);
                break;
            case "5":
                channelCountGameCode.setGameTypeCode5(codeAmount);
                channelCountGameCode.setGameTypeBet5(betAmount);
                channelCountGameCode.setGameTypeSettle5(settleAmount);
                channelCountGameCode.setGameTypeWinLoss5(winLoss);
                break;
            case "6":
                channelCountGameCode.setGameTypeCode6(codeAmount);
                channelCountGameCode.setGameTypeBet6(betAmount);
                channelCountGameCode.setGameTypeSettle6(settleAmount);
                channelCountGameCode.setGameTypeWinLoss6(winLoss);
                break;
            case "7":
                channelCountGameCode.setGameTypeCode7(codeAmount);
                channelCountGameCode.setGameTypeBet7(betAmount);
                channelCountGameCode.setGameTypeSettle7(settleAmount);
                channelCountGameCode.setGameTypeWinLoss7(winLoss);
                break;
            case "8":
                channelCountGameCode.setGameTypeCode8(codeAmount);
                channelCountGameCode.setGameTypeBet8(betAmount);
                channelCountGameCode.setGameTypeSettle8(settleAmount);
                channelCountGameCode.setGameTypeWinLoss8(winLoss);
                break;
            case "9":
                channelCountGameCode.setGameTypeCode9(codeAmount);
                channelCountGameCode.setGameTypeBet9(betAmount);
                channelCountGameCode.setGameTypeSettle9(settleAmount);
                channelCountGameCode.setGameTypeWinLoss9(winLoss);
                break;
            default:
                log.warn("未知游戏类型码: {}", orderTerm.getGameTypeCode());
        }
    }

    /**
     * 红包雨
     *
     * @param orderEntity
     */
    public void getRedPacketRain(OrderEntity orderEntity) {
        try {
            //当前账户余额
            ConsumOrderVO amount = getAmount(orderEntity);
            //查询红包雨
            LambdaQueryWrapper<OrderRedReceive> queryWrapper = Wrappers.lambdaQuery(OrderRedReceive.class);
            queryWrapper.eq(OrderRedReceive::getOrderNo, orderEntity.getOrderNo());
            OrderRedReceive redReceive = orderRedReceiveService.getOne(queryWrapper);
            if (redReceive == null) {
                log.info("红包雨->当前订单号:{}", orderEntity.getOrderNo());
                return;
            }
            Integer redType = redReceive.getType();
            Currency currency = currencyService.getById(orderEntity.getCurrencyId());
            UserCountOrder userCountOrder = this.getUserCountOrder(orderEntity);
            log.info("uuid={}, 红包雨--> 当前用户订单统计数据={}", orderEntity.getUuid(), Objects.isNull(userCountOrder) ? null : userCountOrder.toString());
            if (Objects.isNull(userCountOrder)) {
                userCountOrder = new UserCountOrder();
                userCountOrder.setUserId(orderEntity.getUserId());//用户id
                userCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                userCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                userCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    userCountOrder.setItemId(currency.getItemId());
                    userCountOrder.setChainTag(currency.getChainTag());
                }
                userCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                userCountOrder.setUserEarnings(orderEntity.getBonusAmount());//userEarnings
                userCountOrder.setTotalPacketAmount(orderEntity.getBonusAmount());//红包彩金
                userCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                userCountOrder.setCalculationsTime(new Date());//操作时间
                userCountOrder.setCreateTime(new Date());
                userCountOrder.setUpdateTime(new Date());
                if (redType == 1) { //1 红包雨
                    userCountOrder.setRedNum(1);
                    userCountOrder.setRedAmount(orderEntity.getBonusAmount());
                } else if (redType == 2) {//2 红包雨私人红包,
                    userCountOrder.setRedPrivateNum(1);
                    userCountOrder.setRedPrivateAmount(orderEntity.getBonusAmount());
                }
                int result = userCountOrderMapper.saveUserCountOrderData(userCountOrder);
                log.info("uuid={}, 红包雨->添加用户数据: {} 结果: {}", orderEntity.getUuid(), userCountOrder, result);
            } else {
                UserCountOrder updateUserCountOrder = new UserCountOrder();
                updateUserCountOrder.setId(userCountOrder.getId());
                updateUserCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
//                updateUserCountOrder.setBonusAmount(orderEntity.getMoney());//彩金
                updateUserCountOrder.setTotalPacketAmount(orderEntity.getBonusAmount());//红包彩金
                updateUserCountOrder.setUserEarnings(orderEntity.getBonusAmount());//用户收入
                updateUserCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                updateUserCountOrder.setCalculationsTime(new Date());//操作时间
                updateUserCountOrder.setUpdateTime(new Date());//操作时间
                if (redType == 1) { //1 红包雨
                    updateUserCountOrder.setRedNum(1);
                    updateUserCountOrder.setRedAmount(orderEntity.getBonusAmount());
                } else if (redType == 2) {//2 红包雨私人红包,
                    updateUserCountOrder.setRedPrivateNum(1);
                    updateUserCountOrder.setRedPrivateAmount(orderEntity.getBonusAmount());
                }
                int result = userCountOrderMapper.updateUserCountOrderData(updateUserCountOrder);
                log.info("uuid={}, 红包雨->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), userCountOrder, result);
            }

            // -----------------------------  t_channel_count_order  操作 -----------------------------------------
            //查询t_channel_count_order 渠道 是否有数据 有 累加 否 添加
            ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
            log.info("uuid={}, 红包雨-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(channelCountOrder) ? null : channelCountOrder.toString());
            if (channelCountOrder == null) {
                channelCountOrder = new ChannelCountOrder();
                channelCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                channelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                channelCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                channelCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    channelCountOrder.setItemId(currency.getItemId());
                    channelCountOrder.setChainTag(currency.getChainTag());
                }
                channelCountOrder.setCreateTime(new Date());//操作时间
                channelCountOrder.setUpdateTime(new Date());//操作时间
                channelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
//                channelCountOrder.setBonusAmount(orderEntity.getMoney());//彩金
                channelCountOrder.setTotalPacketAmount(orderEntity.getBonusAmount());//红包彩金
                channelCountOrder.setUserEarnings(orderEntity.getBonusAmount());//用户收入
                channelCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                if (redType == 1) { //1 红包雨
                    channelCountOrder.setRedNum(1);
                    channelCountOrder.setRedAmount(orderEntity.getBonusAmount());
                } else if (redType == 2) {//2 红包雨私人红包,
                    channelCountOrder.setRedPrivateNum(1);
                    channelCountOrder.setRedPrivateAmount(orderEntity.getBonusAmount());
                }
                channelCountOrder.setCalculationsTime(new Date());
                channelCountOrder.setUpdateTime(new Date());
                int result = channelCountOrderMapper.insertChannelCountOrderDataV2(channelCountOrder);
                log.info("uuid={}, 红包雨->添加用户数据: {} 结果: {}", orderEntity.getUuid(), channelCountOrder, result);
            } else {
                ChannelCountOrder updateChannelCountOrder = new ChannelCountOrder();
                updateChannelCountOrder.setId(channelCountOrder.getId());
                updateChannelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
//                updateChannelCountOrder.setBonusAmount(orderEntity.getMoney());//彩金
                updateChannelCountOrder.setTotalPacketAmount(orderEntity.getBonusAmount());//红包彩金
                updateChannelCountOrder.setUserEarnings(orderEntity.getBonusAmount());//用户收入
                updateChannelCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());//客损
                updateChannelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                updateChannelCountOrder.setCalculationsTime(new Date());
                updateChannelCountOrder.setUpdateTime(new Date());
                if (redType == 1) { //1 红包雨
                    updateChannelCountOrder.setRedNum(1);
                    updateChannelCountOrder.setRedAmount(orderEntity.getBonusAmount());
                } else if (redType == 2) {//2 红包雨私人红包,
                    updateChannelCountOrder.setRedPrivateNum(1);
                    updateChannelCountOrder.setRedPrivateAmount(orderEntity.getBonusAmount());
                }
                int result = channelCountOrderMapper.updateChannelCountOrderDataV2(updateChannelCountOrder);
                log.info("uuid={}, 红包雨->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), userCountOrder, result);
            }

            // -----------------------------  t_count_order  操作 -----------------------------------------
            CountOrder countOrder = this.getCountOrder(orderEntity);
            log.info("uuid={}, 红包雨-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(countOrder) ? null : countOrder.toString());
            if (countOrder == null) {
                countOrder = new CountOrder();
                countOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                countOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    countOrder.setItemId(currency.getItemId());
                    countOrder.setChainTag(currency.getChainTag());
                }
                countOrder.setCreateTime(new Date());//操作时间
                countOrder.setUpdateTime(new Date());//操作时间
                countOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                countOrder.setUserEarnings(orderEntity.getBonusAmount());//用户收入
//                countOrder.setBonusAmount(orderEntity.getMoney());//彩金
                countOrder.setTotalPacketAmount(orderEntity.getBonusAmount());//红包彩金
                countOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                countOrder.setCalculationsTime(new Date());//操作时间
                countOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));

                if (redType == 1) { //1 红包雨
                    countOrder.setRedNum(1);
                    countOrder.setRedAmount(orderEntity.getBonusAmount());
                } else if (redType == 2) {//2 红包雨私人红包,
                    countOrder.setRedPrivateNum(1);
                    countOrder.setRedPrivateAmount(orderEntity.getBonusAmount());
                }

                int result = countOrderMapper.insertCountOrderDataV2(countOrder);
                log.info("uuid={}, 红包雨->添加用户数据: {} 结果: {}", orderEntity.getUuid(), countOrder, result);
            } else {
                CountOrder updateCountOrder = new CountOrder();
                updateCountOrder.setId(countOrder.getId());
                updateCountOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                updateCountOrder.setTotalPacketAmount(orderEntity.getBonusAmount());//红包彩金
                updateCountOrder.setUserEarnings(orderEntity.getBonusAmount());//用户收入
//                updateCountOrder.setBonusAmount(orderEntity.getMoney());//彩金
                updateCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                updateCountOrder.setCalculationsTime(new Date());//操作时间
                updateCountOrder.setUpdateTime(new Date());//操作时间
                updateCountOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                if (redType == 1) { //1 红包雨
                    updateCountOrder.setRedNum(1);
                    updateCountOrder.setRedAmount(orderEntity.getBonusAmount());
                } else if (redType == 2) {//2 红包雨私人红包,
                    updateCountOrder.setRedPrivateNum(1);
                    updateCountOrder.setRedPrivateAmount(orderEntity.getBonusAmount());
                }
                int result = countOrderMapper.updateCountOrderDataV2(updateCountOrder);
                log.info("uuid={}, 红包雨->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateCountOrder, result);
            }
        } catch (Exception e) {
            log.error("getRedPacketRain -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
            throw e;
        }

    }

    /*****
     * vip等级
     * @param orderEntity
     */
    public void getVipBonus(OrderEntity orderEntity) {

            try {
                //当前账户余额
                ConsumOrderVO amount = getAmount(orderEntity);
                Currency currency = currencyService.getById(orderEntity.getCurrencyId());
                UserCountOrder userCountOrder = this.getUserCountOrder(orderEntity);
                log.info("uuid={}, vip等级--> 当前用户订单统计数据={}", orderEntity.getUuid(), Objects.isNull(userCountOrder) ? null : userCountOrder.toString());
                if (Objects.isNull(userCountOrder)) {
                    userCountOrder = new UserCountOrder();
                    userCountOrder.setUserId(orderEntity.getUserId());//用户id
                    userCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                    userCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                    userCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                    if (Objects.nonNull(currency)) {
                        userCountOrder.setItemId(currency.getItemId());
                        userCountOrder.setChainTag(currency.getChainTag());
                    }
                    userCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                    userCountOrder.setUserEarnings(orderEntity.getBonusAmount());
                    userCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                    userCountOrder.setVipRewardAmount(orderEntity.getBonusAmount());//等级奖励
                    userCountOrder.setVipRewardNum(1);//等级奖励次数
                    userCountOrder.setActivityAwardBonusAmount(orderEntity.getBonusAmount());
                    userCountOrder.setReceiveBonusNum(1);
                    userCountOrder.setCalculationsTime(new Date());//操作时间
                    userCountOrder.setCreateTime(new Date());
                    userCountOrder.setUpdateTime(new Date());

                    int result = userCountOrderMapper.saveUserCountOrderData(userCountOrder);
                    log.info("uuid={}, vip等级->添加用户数据: {} 结果: {}", orderEntity.getUuid(), userCountOrder, result);
                } else {
                    UserCountOrder updateUserCountOrder = new UserCountOrder();
                    updateUserCountOrder.setId(userCountOrder.getId());
                    updateUserCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                    updateUserCountOrder.setUserEarnings(orderEntity.getBonusAmount());//用户收入
                    updateUserCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                    updateUserCountOrder.setVipRewardAmount(orderEntity.getBonusAmount());//等级奖励
                    updateUserCountOrder.setVipRewardNum(1);//等级奖励次数
                    updateUserCountOrder.setActivityAwardBonusAmount(orderEntity.getBonusAmount());
                    updateUserCountOrder.setReceiveBonusNum(1);
                    updateUserCountOrder.setCalculationsTime(new Date());//操作时间
                    updateUserCountOrder.setUpdateTime(new Date());//操作时间

                    int result = userCountOrderMapper.updateUserCountOrderData(updateUserCountOrder);
                    log.info("uuid={}, vip等级->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), userCountOrder, result);
                }

                // -----------------------------  t_channel_count_order  操作 -----------------------------------------
                //查询t_channel_count_order 渠道 是否有数据 有 累加 否 添加
                ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
                log.info("uuid={}, vip等级-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(channelCountOrder) ? null : channelCountOrder.toString());
                if (channelCountOrder == null) {
                    channelCountOrder = new ChannelCountOrder();
                    channelCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                    channelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                    channelCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                    channelCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                    if (Objects.nonNull(currency)) {
                        channelCountOrder.setItemId(currency.getItemId());
                        channelCountOrder.setChainTag(currency.getChainTag());
                    }
                    channelCountOrder.setCreateTime(new Date());//操作时间
                    channelCountOrder.setUpdateTime(new Date());//操作时间
                    channelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
                    channelCountOrder.setUserEarnings(orderEntity.getBonusAmount());//用户收入
                    channelCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                    channelCountOrder.setCalculationsTime(new Date());
                    channelCountOrder.setVipRewardAmount(orderEntity.getBonusAmount());//等级奖励
                    channelCountOrder.setVipRewardNum(1);//等级奖励次数
                    channelCountOrder.setActivityAwardBonusAmount(orderEntity.getBonusAmount());
                    channelCountOrder.setReceiveBonusNum(1);
                    channelCountOrder.setUpdateTime(new Date());
                    int result = channelCountOrderMapper.insertChannelCountOrderDataV2(channelCountOrder);
                    log.info("uuid={}, vip等级->添加用户数据: {} 结果: {}", orderEntity.getUuid(), channelCountOrder, result);
                } else {
                    ChannelCountOrder updateChannelCountOrder = new ChannelCountOrder();
                    updateChannelCountOrder.setId(channelCountOrder.getId());
                    updateChannelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
                    updateChannelCountOrder.setUserEarnings(orderEntity.getBonusAmount());//用户收入
                    updateChannelCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());//客损
                    updateChannelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                    updateChannelCountOrder.setVipRewardAmount(orderEntity.getBonusAmount());//等级奖励
                    updateChannelCountOrder.setVipRewardNum(1);//等级奖励次数
                    updateChannelCountOrder.setActivityAwardBonusAmount(orderEntity.getBonusAmount());
                    updateChannelCountOrder.setReceiveBonusNum(1);
                    updateChannelCountOrder.setCalculationsTime(new Date());
                    updateChannelCountOrder.setUpdateTime(new Date());

                    int result = channelCountOrderMapper.updateChannelCountOrderDataV2(updateChannelCountOrder);
                    log.info("uuid={}, vip等级->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), userCountOrder, result);
                }

                // -----------------------------  t_count_order  操作 -----------------------------------------
                CountOrder countOrder = this.getCountOrder(orderEntity);
                log.info("uuid={}, vip等级-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(countOrder) ? null : countOrder.toString());
                if (countOrder == null) {
                    countOrder = new CountOrder();
                    countOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                    countOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                    if (Objects.nonNull(currency)) {
                        countOrder.setItemId(currency.getItemId());
                        countOrder.setChainTag(currency.getChainTag());
                    }
                    countOrder.setCreateTime(new Date());//操作时间
                    countOrder.setUpdateTime(new Date());//操作时间
                    countOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                    countOrder.setUserEarnings(orderEntity.getBonusAmount());//用户收入
                    countOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                    countOrder.setCalculationsTime(new Date());//操作时间
                    countOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                    countOrder.setVipRewardAmount(orderEntity.getBonusAmount());//等级奖励
                    countOrder.setVipRewardNum(1);//等级奖励次数
                    countOrder.setActivityAwardBonusAmount(orderEntity.getBonusAmount());
                    countOrder.setReceiveBonusNum(1);
                    int result = countOrderMapper.insertCountOrderDataV2(countOrder);
                    log.info("uuid={}, vip等级->添加用户数据: {} 结果: {}", orderEntity.getUuid(), countOrder, result);
                } else {
                    CountOrder updateCountOrder = new CountOrder();
                    updateCountOrder.setId(countOrder.getId());
                    updateCountOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                    updateCountOrder.setUserEarnings(orderEntity.getBonusAmount());//用户收入
                    updateCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                    updateCountOrder.setCalculationsTime(new Date());//操作时间
                    updateCountOrder.setUpdateTime(new Date());//操作时间
                    updateCountOrder.setActivePeopleNum(Math.toIntExact(redisOnlineService.getTodayActiveCount()));
                    updateCountOrder.setVipRewardAmount(orderEntity.getBonusAmount());//等级奖励
                    updateCountOrder.setVipRewardNum(1);//等级奖励次数
                    updateCountOrder.setActivityAwardBonusAmount(orderEntity.getBonusAmount());
                    updateCountOrder.setReceiveBonusNum(1);
                    int result = countOrderMapper.updateCountOrderDataV2(updateCountOrder);
                    log.info("uuid={}, vip等级->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateCountOrder, result);
                }
            } catch (Exception e) {
                log.error("getVipBonus -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
                throw e;
            }

        }


    public void getLotteryAward(OrderEntity orderEntity) {
        try {
            //当前账户余额
            ConsumOrderVO amount = getAmount(orderEntity);
            Currency currency = currencyService.getById(orderEntity.getCurrencyId());
            // -----------------------------  t_user_count_order  操作 -----------------------------------------
            UserCountOrder userCountOrder = this.getUserCountOrder(orderEntity);
            log.info("彩票奖励-->uuid={}, 当前订单信息={}", orderEntity.getUuid(), Objects.isNull(userCountOrder) ? null : userCountOrder.toString());
            if (Objects.isNull(userCountOrder)) {
                userCountOrder = new UserCountOrder();
                userCountOrder.setUserId(orderEntity.getUserId());//用户id
                userCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                userCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                userCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    userCountOrder.setItemId(currency.getItemId());
                    userCountOrder.setChainTag(currency.getChainTag());
                }
                userCountOrder.setBonusAmount(orderEntity.getBonusAmount());//彩金金额
                userCountOrder.setUserAmount(amount.getUserAmount());// 查用户余额  用户结余-当日
                userCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//用户收入
                userCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                userCountOrder.setCalculationsTime(new Date());// 当前时间 new data
                userCountOrder.setCreateTime(new Date());
                userCountOrder.setUpdateTime(new Date());
                int result = userCountOrderMapper.saveUserCountOrderData(userCountOrder);
                log.info("彩票奖励->uuid={},添加用户数据: {} 结果: {}", orderEntity.getUuid(), userCountOrder, result);
            } else {
                UserCountOrder updateUserCountOrder = new UserCountOrder();
                updateUserCountOrder.setId(userCountOrder.getId());
                updateUserCountOrder.setBonusAmount(orderEntity.getBonusAmount());//彩金金额
                updateUserCountOrder.setUserAmount(amount.getUserAmount());//用户结余-当日
                updateUserCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//用户收入
                updateUserCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                updateUserCountOrder.setCalculationsTime(new Date());//统计计算时间
                updateUserCountOrder.setUpdateTime(new Date());//统计计算时间
                int result = userCountOrderMapper.updateUserCountOrderData(updateUserCountOrder);
                log.info("uuid={}, 彩票奖励->更新用户数据={} 更新结果={}", orderEntity.getUuid(), updateUserCountOrder, result);
            }
            // -----------------------------  t_channel_count_order  操作 -----------------------------------------
            //查询t_channel_count_order 渠道 是否有数据 有 累加 否 添加
            ChannelCountOrder channelCountOrder = this.getChannelCountOrder(orderEntity);
            log.info("uuid={},彩票奖励-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(channelCountOrder) ? null : channelCountOrder.toString());
            if (channelCountOrder == null) {
                channelCountOrder = new ChannelCountOrder();
                channelCountOrder.setChannelId(orderEntity.getChannelId());//渠道id
                channelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                channelCountOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                channelCountOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    channelCountOrder.setItemId(currency.getItemId());
                    channelCountOrder.setChainTag(currency.getChainTag());
                }
                channelCountOrder.setCreateTime(new Date());//操作时间
                channelCountOrder.setUpdateTime(new Date());//操作时间
                channelCountOrder.setBonusAmount(orderEntity.getBonusAmount());//彩金金额
                channelCountOrder.setUserAmount(amount.getChannelAmount());//todo查用户余额  结余-当日
                channelCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//todo用户收入 = 金额+彩金
                channelCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                channelCountOrder.setCalculationsTime(new Date());//操作时间
                int result = channelCountOrderMapper.insertChannelCountOrderDataV2(channelCountOrder);
                log.info("uuid={},彩票奖励->添加用户数据: {} 结果: {}", orderEntity.getUuid(), channelCountOrder, result);
            } else {
                ChannelCountOrder updateChannelCountOrder = new ChannelCountOrder();
                updateChannelCountOrder.setId(channelCountOrder.getId());
                updateChannelCountOrder.setBonusAmount(orderEntity.getBonusAmount());// 彩金金额
                updateChannelCountOrder.setUserAmount(amount.getChannelAmount());//用户结余-当日
                updateChannelCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//用户收入
                updateChannelCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());//客损
                updateChannelCountOrder.setShareholderId(setShareholderIdFromChannel(orderEntity));//股东id
                updateChannelCountOrder.setCalculationsTime(new Date());//统计计算时间
                updateChannelCountOrder.setUpdateTime(new Date());//操作时间
                int result = channelCountOrderMapper.updateChannelCountOrderDataV2(updateChannelCountOrder);
                log.info("uuid={},彩票奖励->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateChannelCountOrder, result);
            }

            // -----------------------------  t_count_order  操作 -----------------------------------------
            CountOrder countOrder = this.getCountOrder(orderEntity);
            log.info("uuid={},彩票奖励-->当前订单渠道信息: {}", orderEntity.getUuid(), Objects.isNull(countOrder) ? null : countOrder.toString());
            if (countOrder == null) {
                countOrder = new CountOrder();
                countOrder.setDayStr(orderEntity.getNowDay());//日期(年月日)
                countOrder.setCurrencyId(orderEntity.getCurrencyId());//币种
                if (Objects.nonNull(currency)) {
                    countOrder.setItemId(currency.getItemId());
                    countOrder.setChainTag(currency.getChainTag());
                }
                countOrder.setCreateTime(new Date());//操作时间
                countOrder.setUpdateTime(new Date());//操作时间
                countOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                countOrder.setBonusAmount(orderEntity.getBonusAmount());//彩金金额
                countOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//用户收入
                countOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                countOrder.setCalculationsTime(new Date());//操作时间
                int result = countOrderMapper.insertCountOrderDataV2(countOrder);
                log.info("uuid={},彩票奖励->添加用户数据: {} 结果: {}", orderEntity.getUuid(), countOrder, result);
            } else {
                CountOrder updateCountOrder = new CountOrder();
                updateCountOrder.setId(countOrder.getId());
                updateCountOrder.setUserAmount(amount.getOrderAmount());//用户结余-当日
                updateCountOrder.setBonusAmount(orderEntity.getBonusAmount());//彩金金额
                updateCountOrder.setUserEarnings(orderEntity.getMoney().add(orderEntity.getBonusAmount()));//用户收入
                updateCountOrder.setCustomerLossAmount(orderEntity.getBonusAmount());
                updateCountOrder.setCalculationsTime(new Date());//操作时间
                int result = countOrderMapper.updateCountOrderDataV2(updateCountOrder);
                log.info("uuid={},彩票奖励->更新用户数据: {} 更新结果: {}", orderEntity.getUuid(), updateCountOrder, result);
            }
        } catch (Exception e) {
            log.error("getLotteryAward -> error, uuid={}, 异常类型={}, 异常信息={}", orderEntity.getUuid(), e.getClass().getName(), e.getMessage());
            throw e;
        }
    }
}
