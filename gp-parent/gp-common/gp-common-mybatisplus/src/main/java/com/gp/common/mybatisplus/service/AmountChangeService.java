package com.gp.common.mybatisplus.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.AmountChange;
import com.gp.common.mybatisplus.entity.OrderAmountHeard;
import com.gp.common.mybatisplus.entity.OrderGameCareerHeard;
import com.gp.common.mybatisplus.mapper.AmountChangeMapper;
import com.gp.common.mybatisplus.until.AmountChangeSign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AmountChangeService extends ServiceImpl<AmountChangeMapper, AmountChange> {
    @Autowired
    private AmountChangeMapper tAmountChangeMapper;
    @Autowired
    private AmountChangeSign amountChangeSign;
//    @Resource
//    private EsAmountChangeService esAmountChangeService;
//    @Resource
//    private ThreadPoolTaskExecutor esThreadPoolTaskExecutor;
//    @Resource
//    private RabbitMqTemplate rabbitMqTemplate;

//    @Override
//    public boolean save(AmountChange amountChange) {
//        amountChange.setCreateTime(new Date());
//        // 先调用父类的方法保存到数据库
//        boolean result = super.save(amountChange);
//        // 保存成功后将数据同步到 Elasticsearch
//        if (result) {
////            esAmountChangeService.save(BeanCopyUtil.copyProperties(amountChange, EsAmountChange.class), CecuUtil.getDbCode());
//            amountChange.setUpdateVersion(EsVersionUtil.generateVersion(1));
//            String dbCode = CecuUtil.getDbCode();
//            //执行下注统计
//            esThreadPoolTaskExecutor.execute(() -> {
//                rabbitMqTemplate.sendMq(MqEnum.amountEsMq.getExchange(), MqEnum.amountEsMq.getKey(), MessageBody.builder()
//                        .data(amountChange)
//                        .productId(dbCode)
//                        .build());
//            });
//        }
//        return result;
//    }

//    public boolean saveAll(List<AmountChange> amountChanges) {
//        ArrayList<AmountChange> arr = CollUtil.newArrayList();
//        for (AmountChange amountChange : amountChanges) {
//            amountChange.setUpdateVersion(EsVersionUtil.generateVersion(1));
//            amountChange.setCreateTime(amountChange.getCreateTime() != null ? amountChange.getCreateTime() : new Date());
//            arr.add(amountChange);
//            // 先调用父类的方法保存到数据库
//        }
//        boolean result = super.saveBatch(arr);
//        // 保存成功后将数据同步到 Elasticsearch
//        if (result) {
////            esAmountChangeService.saveAll(BeanCopyUtil.copyToList(arr, EsAmountChange.class), CecuUtil.getDbCode());
//
//            String dbCode = CecuUtil.getDbCode();
//            //执行下注统计
//            esThreadPoolTaskExecutor.execute(() -> {
//                rabbitMqTemplate.sendMq(MqEnum.amountListEsMq.getExchange(), MqEnum.amountListEsMq.getKey(), MessageBody.builder()
//                        .data(amountChanges)
//                        .productId(dbCode)
//                        .build());
//            });
//        }
//        return result;
//    }

    /**
     * 查询用户账变
     *
     * @param id 用户账变ID
     * @return 用户账变
     */
    public AmountChange selectAmountChangeById(Long id) {
        return tAmountChangeMapper.selectAmountChangeById(id);
    }

    /**
     * 查询这个币总下的最后一条记录
     *
     * @param userId
     * @param currencyId
     * @return
     */
    public AmountChange getLastAmountChange(Long userId, Integer currencyId) {
        return tAmountChangeMapper.getLastAmountChange(userId, currencyId);
    }

    public List<AmountChange> queryNoSign(Integer start, Integer limit) {
        return baseMapper.queryNoSign(start, limit);
    }

    public void dealNoSign() {
        log.info("开始-dealAmountChange,{}", new Date());
        //一次查多少条
        Integer start = 0;
        //一次查多少条
        Integer limit = 1000;
        List<AmountChange> contents = this.queryNoSign(start, limit);
        dealSignContent(contents, start, limit);

        log.info("结束-dealAmountChange,{}", new Date());
    }

    private void dealSignContent(List<AmountChange> contents, Integer start, Integer limit) {
        List<List<AmountChange>> split = CollUtil.split(contents, 100);
        for (List<AmountChange> amountChangeList : split) {
            List<AmountChange> collect = amountChangeList.stream().map(e -> {
                amountChangeSign.dealSign(e);
                return e;
            }).collect(Collectors.toList());
            this.updateBatchById(collect);
        }
        //结束之后取给他 start 加 size
        start = start + contents.size();
        log.info("执行中: {} -- 执行的start !", start);

        contents = this.queryNoSign(start, limit);
        if (CollectionUtil.isEmpty(contents)) {
            return;  // 列表为空则直接返回，避免索引越界异常
        }
        log.info("执行中: {} -- 最后一条 !", contents.get(contents.size() - 1));
        dealSignContent(contents, start, limit);
    }

    public OrderAmountHeard queryAmountHeard(Integer type, Long userId, Date startTime, Date endTime) {
        OrderAmountHeard orderAmountHeard = tAmountChangeMapper.queryAmountHeard(type, userId, startTime, endTime);
        if (orderAmountHeard == null) {
            orderAmountHeard = new OrderAmountHeard();
        }
        return orderAmountHeard;
    }

    public OrderGameCareerHeard queryOrderGameCareerHeard(Long userId, Date startTime, Date endTime) {
        OrderGameCareerHeard orderGameCareerHeard = tAmountChangeMapper.queryOrderGameCareerHeard(userId, startTime, endTime);
        if (orderGameCareerHeard == null) {
            orderGameCareerHeard = new OrderGameCareerHeard();
        }
        return orderGameCareerHeard;
    }

    public List<AmountChange> selectAmountChangeListBychannelId(Long channelId, Integer id, String startTime, String endTime) {
        return tAmountChangeMapper.selectAmountChangeListBychannelId(channelId, id, startTime, endTime);
    }

    public Map<String, Object> selectAmountChangeMapBychannelId(Long channelId, Integer id, String startTime, String endTime, Integer accountType,
                                                                Integer orderType, Integer type) {
        return tAmountChangeMapper.selectAmountChangeMapBychannelId(channelId, id, startTime, endTime, accountType, orderType, type);
    }

    public List<AmountChange> selectAmountChangeListByTime(Long channelId, String startTime, String endTime) {
        return tAmountChangeMapper.selectAmountChangeListByTime(channelId, startTime, endTime);
    }

    /**
     * 查询用户账变列表
     *
     * @param tAmountChange 用户账变
     * @return 用户账变
     */
    public List<AmountChange> selectAmountChangeTodayList(AmountChange tAmountChange) {
        return tAmountChangeMapper.selectAmountChangeTodayList(tAmountChange);
    }


    public long selectAmountChangeListByTimeSql(Long channelId, String startTime, String endTime) {
        return tAmountChangeMapper.selectAmountChangeListByTimeSql(channelId, startTime, endTime);
    }

    public Map<String, Object> amountChangeCountMap(String startTime, String endTime, Long userId, Integer currencyId) {
        return baseMapper.amountChangeCountMap(startTime, endTime, userId, currencyId);
    }

    public int selectAmountChangeListByTimeCount(Long channelId, String startTime, String endTime) {
        return baseMapper.selectAmountChangeListByTimeCount(channelId, startTime, endTime);
    }

    public Long selectAmountChangeTodayCount(AmountChange tAmountChange) {
        return baseMapper.selectAmountChangeTodayCount(tAmountChange);
    }


    public long selectAmountShareholderByTimeSql(Long shareholderId, String startTime, String endTime) {
        return tAmountChangeMapper.selectAmountShareholderByTimeSql(shareholderId, startTime, endTime);
    }

    /**
     * 获取剩余积分
     *
     * @param userIds 用户id
     * @param day     日期(yyyy-MM-dd)
     */
    public BigDecimal getRemainIntegral(List<Long> userIds, String day, Integer currencyId) {
        //获取昨日0点-23:59:59
        Date date = DateUtils.parseToDate(day, DateUtils.YYYY_MM_DD);
        //减1天
        date = DateUtils.addDateDays(date, -1);
        Date endTime = DateUtils.getEndTimeByDate(date);
        return tAmountChangeMapper.getRemainIntegral(endTime, userIds, currencyId);
    }
}
