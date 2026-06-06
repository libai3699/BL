package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.mybatisplus.entity.UserExtChange;
import com.gp.common.mybatisplus.mapper.UserExtChangeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 用户账变Service业务层处理
 *
 * @author axing
 * @date 2024-05-16
 */
@Service
public class UserExtChangeService extends ServiceImpl<UserExtChangeMapper, UserExtChange>
{
    @Autowired
    private UserExtChangeMapper userExtChangeMapper;
//    @Resource
//    private ThreadPoolTaskExecutor esThreadPoolTaskExecutor;
//    @Resource
//    private RabbitMqTemplate rabbitMqTemplate;
//    @Override
//    public boolean save(UserExtChange userExtChange) {
//        userExtChange.setCreateTime(new Date());
//        // 先调用父类的方法保存到数据库
//        boolean result = super.save(userExtChange);
//        // 保存成功后将数据同步到 Elasticsearch
//        if (result) {
//            userExtChange.setUpdateVersion(EsVersionUtil.generateVersion(1));
//            String dbCode = CecuUtil.getDbCode();
//            //执行下注统计
//            esThreadPoolTaskExecutor.execute(() -> {
//                rabbitMqTemplate.sendMq(MqEnum.extAmountEsMq.getExchange(), MqEnum.extAmountEsMq.getKey(), MessageBody.builder()
//                        .data(userExtChange)
//                        .productId(dbCode)
//                        .build());
//            });
//        }
//        return result;
//    }

//    public boolean saveBatch(List<UserExtChange> userExtChanges) {
//        ArrayList<UserExtChange> arr = CollUtil.newArrayList();
//        for (UserExtChange amountChange : userExtChanges) {
//            amountChange.setUpdateVersion(EsVersionUtil.generateVersion(1));
//            amountChange.setCreateTime(amountChange.getCreateTime() != null ? amountChange.getCreateTime() : new Date());
//            arr.add(amountChange);
//            // 先调用父类的方法保存到数据库
//        }
//        boolean result = super.saveBatch(arr);
//        // 保存成功后将数据同步到 Elasticsearch
//        if (result) {
//            String dbCode = CecuUtil.getDbCode();
//            //执行下注统计
//            esThreadPoolTaskExecutor.execute(() -> {
//                rabbitMqTemplate.sendMq(MqEnum.extAmountListEsMq.getExchange(), MqEnum.extAmountListEsMq.getKey(), MessageBody.builder()
//                        .data(userExtChanges)
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
    public UserExtChange selectUserExtChangeById(Long id)
    {
        return userExtChangeMapper.selectUserExtChangeById(id);
    }

    public BigDecimal queryTotalMoney(Long userId, Integer type, Date startTime, Date endTime) {
        return userExtChangeMapper.queryTotalMoney(userId, type, startTime, endTime);
    }


    public List<UserExtChange> selectUserExtChangeListByUser(Long channelId, String startTime, String endTime) {
        return userExtChangeMapper.selectUserExtChangeListByUser(channelId,startTime,endTime);
    }

    /**
     * 查询用户账变列表
     *
     * @param param 用户账变
     * @return 用户账变
     */

    public List<UserExtChange> selectUserExtChangeTodayList(UserExtChange param)
    {
        return userExtChangeMapper.selectUserExtChangeTodayList(param);
    }


    public Long selectUserExtChangeTodayCount(UserExtChange param) {
        return userExtChangeMapper.selectUserExtChangeTodayCount(param);
    }



}
