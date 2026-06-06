package com.gp.common.mybatisplus.service;

import java.util.*;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.core.util.RedisUtil;
import com.common.datasource.util.CecuUtil;
import com.common.rabbitmq.model.MessageBody;
import com.common.rabbitmq.service.RabbitMqTemplate;
import com.gp.common.base.constant.RedisKey;
import com.gp.common.base.constant.mq.MqEnum;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.mybatisplus.entity.ConfigRisk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.gp.common.mybatisplus.mapper.UserBlackIpMapper;
import com.gp.common.mybatisplus.entity.UserBlackIp;
import com.gp.common.mybatisplus.service.UserBlackIpService;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 用户黑名单Service业务层处理
 *
 * @author axing
 * @date 2024-07-11
 */
@Service
@Slf4j
public class UserBlackIpService extends ServiceImpl<UserBlackIpMapper, UserBlackIp>
{
    @Resource
    RedisUtil redisUtil;
    @Resource
    UserService userService;

    @Autowired
    private UserBlackIpMapper userBlackIpMapper;
    @Autowired
    private CecuUtil cecuUtil;
    /**
     * 查询用户黑名单
     *
     * @param id 用户黑名单ID
     * @return 用户黑名单
     */

    public UserBlackIp selectUserBlackIpById(Long id)
    {
        return userBlackIpMapper.selectUserBlackIpById(id);
    }

    /**
     * 查询用户黑名单列表
     *
     * @param param 用户黑名单
     * @return 用户黑名单
     */

    public List<UserBlackIp> selectUserBlackIpList(UserBlackIp param)
    {
        return userBlackIpMapper.selectUserBlackIpList(param);
    }


    /**
     * 获取所有黑名单
     *
     */

    public List<UserBlackIp> getAllBlack()
    {
        return userBlackIpMapper.selectList(new LambdaQueryWrapper<>());
    }
    public void refreshUserBlackIp()
    {
        List<String> dbNameList = cecuUtil.getCecuProp().getAppList();
        for (String product : dbNameList) {
            String key = StrUtil.format(RedisKey.userBlackIp,product);
            cecuUtil.cutDbByCode(product);
            List<UserBlackIp> allBlack = this.getAllBlack();
            List<String> ipList = allBlack.stream()
                    .map(UserBlackIp::getIp) // 提取每个 UserBlackIp 对象的 ip 属性
                    .collect(Collectors.toList()); // 收集为一个列表
            List<String> objects = CollUtil.newArrayList();

            for (String s : ipList) {
                s = s.replaceAll("\\.","|");
                objects.add(s);
            }
            redisUtil.delete(key);
            log.info("存入地址{}",JSON.toJSONString(objects));
            if(CollUtil.isNotEmpty(ipList)){
                redisUtil.storeList(key, objects);
            }
        }
        CecuUtil.cleanDbInfo();

    }


    /**
     * 新增用户黑名单
     *
     * @param param 用户黑名单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertUserBlackIp(UserBlackIp param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        //我希望增加的时候需要现在用户这个ip 用户身上ip 符合的给他token失效 这样会好
        String ip = param.getIp();

        if(StrUtil.isNotEmpty(ip)){
            CompletableFuture.runAsync(this::refreshUserBlackIp);
        }

        return result;
    }

    /**
     * 修改用户黑名单
     *
     * @param param 用户黑名单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserBlackIp(UserBlackIp param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        CompletableFuture.runAsync(this::refreshUserBlackIp);

        return result;
    }

    /**
     * 批量删除用户黑名单
     *
     * @param ids 需要删除的用户黑名单ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserBlackIpByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        CompletableFuture.runAsync(this::refreshUserBlackIp);
        return result;
    }

    /**
     * 删除用户黑名单信息
     *
     * @param id 用户黑名单ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserBlackIpById(Long id)
    {
        boolean result = this.removeById(id);
        CompletableFuture.runAsync(this::refreshUserBlackIp);
        return result;
    }
}
