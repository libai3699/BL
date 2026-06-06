package com.gp.common.mybatisplus.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.common.core.constant.OrderConstant;
import com.common.core.util.StringUtils;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.*;
import com.gp.common.base.constant.mq.MqEnum;
import com.gp.common.base.utils.*;
import com.gp.common.mybatisplus.entity.*;
import com.gp.common.mybatisplus.mq.RebateDealEntity;
import com.gp.common.mybatisplus.mqService.MqSendEntityService;
import com.gp.common.mybatisplus.param.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
public class LotteryRebateService {
    @Resource
    private CecuUtil cecuUtil;
    @Resource
    private ChannelService channelService;
    @Resource
    private ConfigRiskService configRiskService;
    @Resource
    private UserCountGameCodeService userCountGameCodeService;
    @Resource
    private UserService userService;

    @Resource
    private UserChannelService userChannelService;
    @Resource
    private MqSendEntityService mqSendEntityService;

    public void settlementLotteryRebate(String product, String day) {
        List<String> dbNameList = cecuUtil.getCecuProp().getAppList();
        if (StringUtils.isNotEmpty(product)) {
            dbNameList = CollUtil.newArrayList(product);
        }
        log.info("settlementLotteryRebate传递进来的day:{},product:{}", day, product);

        for (String s : dbNameList) {
            try {
                cecuUtil.cutDbByCode(s);
                //先看看他的模式如果是彩票代理模式是的话往下走
                Boolean lotteryModel = configRiskService.isLotteryModel();
                if (!lotteryModel) {
                    log.info("产品{}不是彩票代理模式，跳过处理", s);
                    continue;
                }

                Date yesterdayDate = DateUtils.addDateDays(new Date(), -1);
                if (StringUtils.isNotEmpty(day)) {
                    yesterdayDate = DateUtils.parseToDate(day, DateUtils.YYYY_MM_DD);
                }

                //查询所以投注的用户 有渠道的
                List<UserCountGameCode> userCountGameCodes = userCountGameCodeService.queryAllUserCountGameCode(yesterdayDate);
                log.info("产品{}查询到{}个用户需要处理返佣", s, userCountGameCodes.size());

                for (UserCountGameCode userCountGameCode : userCountGameCodes) {
                    try {
                        // 处理单个用户的所有游戏类型
                        processUserLotteryRebate(s, userCountGameCode, yesterdayDate);
                    } catch (Exception e) {
                        log.error("处理用户{}的彩票返佣时发生异常，产品: {}, 错误: {}",
                                userCountGameCode.getUserId(), s, e.getMessage(), e);
                        // 继续处理下一个用户
                    }
                }
            } catch (Exception e) {
                log.error("处理产品{}的彩票返佣时发生异常，错误: {}", s, e.getMessage(), e);
                // 继续处理下一个产品
            }
        }
    }

    /**
     * 处理单个用户的所有游戏类型返佣
     */
    private void processUserLotteryRebate(String productCode, UserCountGameCode userCountGameCode, Date yesterdayDate) {
        Long userId = userCountGameCode.getUserId();

        // 获取所有游戏类型的投注金额
        BigDecimal gameTypeCode1 = userCountGameCode.getGameTypeCode1();
        BigDecimal gameTypeCode2 = userCountGameCode.getGameTypeCode2();
        BigDecimal gameTypeCode3 = userCountGameCode.getGameTypeCode3();
        BigDecimal gameTypeCode4 = userCountGameCode.getGameTypeCode4();
        BigDecimal gameTypeCode5 = userCountGameCode.getGameTypeCode5();
        BigDecimal gameTypeCode6 = userCountGameCode.getGameTypeCode6();
        BigDecimal gameTypeCode7 = userCountGameCode.getGameTypeCode7();
        BigDecimal gameTypeCode8 = userCountGameCode.getGameTypeCode8();
        BigDecimal gameTypeCode9 = userCountGameCode.getGameTypeCode9();

        // 每个游戏类型单独处理，避免相互影响
        processGameTypeRebate(productCode, userCountGameCode, gameTypeCode1, "1", yesterdayDate);
        processGameTypeRebate(productCode, userCountGameCode, gameTypeCode2, "2", yesterdayDate);
        processGameTypeRebate(productCode, userCountGameCode, gameTypeCode3, "3", yesterdayDate);
        processGameTypeRebate(productCode, userCountGameCode, gameTypeCode4, "4", yesterdayDate);
        processGameTypeRebate(productCode, userCountGameCode, gameTypeCode5, "5", yesterdayDate);
        processGameTypeRebate(productCode, userCountGameCode, gameTypeCode6, "6", yesterdayDate);
        processGameTypeRebate(productCode, userCountGameCode, gameTypeCode7, "7", yesterdayDate);
        processGameTypeRebate(productCode, userCountGameCode, gameTypeCode8, "8", yesterdayDate);
        processGameTypeRebate(productCode, userCountGameCode, gameTypeCode9, "9", yesterdayDate);
    }

    /**
     * 处理单个游戏类型的返佣
     */
    private void processGameTypeRebate(String productCode, UserCountGameCode userCountGameCode,
                                     BigDecimal gameTypeCodeBig, String typeCode, Date yesterdayDate) {
        try {
            shareLotteryRebate(productCode, userCountGameCode, gameTypeCodeBig, typeCode, yesterdayDate);
        } catch (Exception e) {
            log.error("处理用户{}游戏类型{}时发生异常，产品: {}, 错误: {}",
                    userCountGameCode.getUserId(), typeCode, productCode, e.getMessage(), e);
            // 继续处理下一个游戏类型
        }
    }

    private void shareLotteryRebate(String s, UserCountGameCode userCountGameCode, BigDecimal gameTypeCodeBig, String typeCode, Date yesterdayDate) {
        try {
            //去找顶级
            Long userId = userCountGameCode.getUserId();
            TUser user = userService.getById(userId);
            if (user == null) {
                log.warn("用户{}不存在，跳过处理", userId);
                return;
            }

            //先看看他的这个p_path 找一下他的第一个代理然后往下一个一个往下走
            String pPath = user.getPPath();
            //去去查他的素偶
            Long channelId = user.getChannelId();
            if (channelId == null || channelId == 0L) {
                return; // 跳过处理
            }
            //去查询渠道
            Channel channel = channelService.getById(channelId);
            //如果没有渠道 就不计算
            if (ObjectUtils.isEmpty(channel)) {
                log.warn("用户{}的渠道{}不存在，跳过处理", userId, channelId);
                return;
            }
            //这里先分自己
            //如果没有上级 就不计算
            //然后找一下有没有配置游戏
            if (gameTypeCodeBig.compareTo(BigDecimal.ZERO) > 0) {
                Long channelUserId = channel.getUserId();
                shareSelf(gameTypeCodeBig, typeCode, yesterdayDate, channelId, user, s);
                if (ObjectUtils.isEmpty(pPath)) {
                    return;
                }
                shareUp(gameTypeCodeBig, typeCode, yesterdayDate, channelUserId, channelId, userId, pPath, user, s);
            }
        } catch (Exception e) {
            log.error("处理用户{}游戏类型{}返佣时发生异常，产品: {}, 错误: {}",
                    userCountGameCode.getUserId(), typeCode, s, e.getMessage(), e);
            throw e; // 重新抛出异常，让上层处理
        }
    }

    private void shareSelf(BigDecimal gameTypeCodeBig, String typeCode, Date yesterdayDate, Long channelId, TUser user, String productId) {
        try {
            Long userId = user.getUserId();
            UserChannel selfUserChannel = userChannelService.getChannelLotteryConfig(userId, channelId);
            if (selfUserChannel == null) {
                log.warn("用户{}在渠道{}下没有返佣配置，跳过自己返佣", userId, channelId);
                return;
            }
            BigDecimal selfBig = userChannelService.dealUserChannelRatio(selfUserChannel, typeCode);
            String orderPrefix = OrderConstant.receiveRebateOrder;
            String dateYYYMMDD = DateUtils.getDateYYYMMDD(yesterdayDate);
            //RRO-{}-{}-{}
            String orderNoPrefix = StrUtil.format(orderPrefix, channelId, userId, dateYYYMMDD);
            //如果下级为空的话 钱都留给上级了
            String orderNo = SnowIdUtil.getId(orderNoPrefix);
            BigDecimal shareBig = gameTypeCodeBig.multiply(selfBig);
            if (shareBig.compareTo(BigDecimal.ZERO) <= 0) {
                return;
            }
            log.info("<gameTypeCodeBig>:{},selfBig {}", gameTypeCodeBig, selfBig);
            //如果下级为空的话 钱都留给上级了
            //给他上级
            ChangeExtValueVo changeExtValueVo2 = new ChangeExtValueVo();
            changeExtValueVo2.setUserId(userId);
            changeExtValueVo2.setExtType(UserExtTypeCons.未领取返佣);
            changeExtValueVo2.setUpdateValue(shareBig);
            changeExtValueVo2.setOrderNo(orderNo);
            changeExtValueVo2.setOrderType(BaseGameInfoCons.UserExtOrderType.注单);
            changeExtValueVo2.setAccountType(AccountChangeTypeConstants.INCOME);
            changeExtValueVo2.setChangeType(BaseGameInfoCons.UserExtChangeType.上级返佣);
            changeExtValueVo2.setOperator(user.getUserTgName());
            mqSendEntityService.sendRebateConsume(RebateDealEntity.builder().changeExtValueVo(changeExtValueVo2).productId(productId).build(), MqEnum.superRebateMq);
        } catch (Exception e) {
            log.error("处理用户{}自己返佣时发生异常，渠道: {}, 游戏类型: {}, 错误: {}",
                    user.getUserId(), channelId, typeCode, e.getMessage(), e);
            throw e;
        }
    }

    private void shareUp(BigDecimal gameTypeCodeBig, String typeCode, Date yesterdayDate, Long channelUserId, Long channelId, Long userId, String pPath, TUser user, String productId) {
        try {
            //顶级的渠道比例
            UserChannel userChannel = userChannelService.getChannelLotteryConfig(channelUserId, channelId);
            if (userChannel == null) {
                log.warn("渠道用户{}在渠道{}下没有返佣配置，跳过上级返佣", channelUserId, channelId);
                return;
            }
            BigDecimal topRatio = userChannelService.dealUserChannelRatio(userChannel, typeCode);
            log.info("用户{}游戏类型 {}渠道 {},顶级渠道的比例为{}", userId, gameTypeCodeBig, channelId, topRatio);
            //总比例
            //他的上级 比如说是 1,2,3,5 这样 然后去截取 比如说他的渠道所有人是 2 如果说 1,11,121,131 这样的话不行
            //1035,861,1057
            String channelPath = channelUserId + ",";
            log.info("渠道路径1:{}", channelPath);
            log.info("11111:{}", channelPath);
            String subPath = "";
            //加一个0 把这样他每次都给上一级分钱的话这样可以
            pPath = pPath + "," + user.getUserId();
            if (pPath.startsWith(channelPath)) {
                channelPath = channelUserId + ",";
            } else {
                channelPath = "," + channelUserId + ",";
            }
            log.info("渠道路径pPath:{}", pPath);
            log.info("渠道路径2:{}", channelPath);
            int i = pPath.lastIndexOf(channelPath);
            log.info("<UNK11>:{}", i);
            //这里也可能是上架就是顶级 他的渠道是 5  他的p_path 也是 5 那不行他要搞下级 1,2  比如他 的上级是101 渠道的用户也是101 p_path 101 的话有问题 100
            //1,2,3,4  自己是2
            //还是统一吧
            if (i == -1) {
                log.warn("用户{}的渠道路径{}不包含渠道用户{}，跳过上级返佣", userId, pPath, channelUserId);
                return;
            }
            subPath = pPath.substring(i + channelPath.length());

            log.info("<subPath>:{}", subPath);
            BigDecimal upShareBig = BigDecimal.ZERO;
            BigDecimal downShareBig = BigDecimal.ZERO;
            //然后看看 他的下级
            String[] split = subPath.split(",");
            // 1,2,3,4,5 他顶级 是1 的话  2,3,4,5
            for (String downUserIdStr : split) {
                try {
                    //下级是 100 上级是101
                    UserChannel downChannelLotteryConfig = userChannelService.getChannelLotteryConfig(Long.parseLong(downUserIdStr), channelId);
                    TUser tUser = userService.getById(downUserIdStr);
                    log.info("<tUser>:{}", tUser);
                    if (tUser == null) {
                        log.warn("下级用户{}不存在，跳过处理", downUserIdStr);
                        continue;
                    }
                    //需要看看他的上级是不是顶级渠道
                    UserChannel upChannelLotteryConfig = userChannelService.getChannelLotteryConfig(tUser.getSuperUserId(), channelId);
                    if (upChannelLotteryConfig == null) {
                        upShareBig = BigDecimal.ZERO;
                    } else {
                        upShareBig = userChannelService.dealUserChannelRatio(upChannelLotteryConfig, typeCode);
                    }
                    if(upShareBig.compareTo(BigDecimal.ZERO) < 0){
                        upShareBig = BigDecimal.ZERO;
                    }
                    log.info("下级用户id {}:比例为{}", downUserIdStr, downChannelLotteryConfig);
                    if (downChannelLotteryConfig == null) {
                        downShareBig = BigDecimal.ZERO;
                    } else {
                        downShareBig = userChannelService.dealUserChannelRatio(downChannelLotteryConfig, typeCode);
                    }
                    if(downShareBig.compareTo(BigDecimal.ZERO) < 0){
                        continue;
                    }
                    BigDecimal shareBig = gameTypeCodeBig.multiply((upShareBig.subtract(downShareBig)));
                    if (shareBig.compareTo(BigDecimal.ZERO) <= 0) {
                        continue;
                    }
                    Long userIdF = tUser.getSuperUserId();
                    String orderPrefix = OrderConstant.receiveRebateOrder;
                    String dateYYYMMDD = DateUtils.getDateYYYMMDD(yesterdayDate);
                    String orderNoPrefix = StrUtil.format(orderPrefix, channelId, userIdF, dateYYYMMDD);
                    //如果下级为空的话 钱都留给上级了
                    String orderNo = SnowIdUtil.getId(orderNoPrefix);
                    log.info("<gameTypeCodeBig>:{},upShareBig {},downShareBig{}", gameTypeCodeBig, upShareBig, downShareBig);
                    //如果下级为空的话 钱都留给上级了
                    //给他上级
                    ChangeExtValueVo changeExtValueVo2 = new ChangeExtValueVo();
                    changeExtValueVo2.setUserId(userIdF);
                    changeExtValueVo2.setExtType(UserExtTypeCons.未领取返佣);
                    changeExtValueVo2.setUpdateValue(shareBig);
                    changeExtValueVo2.setOrderNo(orderNo);
                    changeExtValueVo2.setOrderType(BaseGameInfoCons.UserExtOrderType.注单);
                    changeExtValueVo2.setAccountType(AccountChangeTypeConstants.INCOME);
                    changeExtValueVo2.setChangeType(BaseGameInfoCons.UserExtChangeType.上级返佣);
                    changeExtValueVo2.setOperator(user.getUserTgName());
                    mqSendEntityService.sendRebateConsume(RebateDealEntity.builder().changeExtValueVo(changeExtValueVo2).productId(productId).build(), MqEnum.superRebateMq);
                } catch (Exception e) {
                    log.error("处理下级用户{}返佣时发生异常，产品: {}, 错误: {}",
                            downUserIdStr, productId, e.getMessage(), e);
                    // 继续处理下一个下级用户
                }
            }
        } catch (Exception e) {
            log.error("处理用户{}上级返佣时发生异常，渠道: {}, 游戏类型: {}, 错误: {}",
                    userId, channelId, typeCode, e.getMessage(), e);
            throw e;
        }
    }

    public static void main(String[] args) {
        Long channelUserId = 1L;
        String pPath = "1";
        String userId = "1";
        //1035,861,1057
        String channelPath = channelUserId + ",";
        log.info("渠道路径1:{}", channelPath);
        log.info("11111:{}", channelPath);
        String subPath = "";
        //加一个0 把这样他每次都给上一级分钱的话这样可以
        pPath = pPath + "," +userId;
        if (pPath.startsWith(channelPath)) {
            channelPath = channelUserId + ",";
        } else {
            channelPath = "," + channelUserId + ",";
        }
        log.info("渠道路径pPath:{}", pPath);
        log.info("渠道路径2:{}", channelPath);
        int i = pPath.lastIndexOf(channelPath);
        log.info("<UNK11>:{}", i);
        //这里也可能是上架就是顶级 他的渠道是 5  他的p_path 也是 5 那不行他要搞下级 1,2  比如他 的上级是101 渠道的用户也是101 p_path 101 的话有问题 100
        //1,2,3,4  自己是2
        //还是统一吧
        if (i == -1) {
            return;
        }
        subPath = pPath.substring(i + channelPath.length());
        System.out.println(subPath);
    }
}
