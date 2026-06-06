package com.gp.common.mybatisplus.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.mybatisplus.entity.TUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gp.common.mybatisplus.mapper.UserSignMapper;
import com.gp.common.mybatisplus.entity.UserSign;

/**
 * 用户签到Service业务层处理
 *
 * @author axing
 * @date 2024-05-15
 */
@Service
public class UserSignService extends ServiceImpl<UserSignMapper, UserSign>
{
    @Autowired
    private UserSignMapper userSignMapper;
    @Autowired
    private ActivityAwardService activityAwardService;
    @Autowired
    private SignInService signInService;


    public Boolean queryTodaySign(Long userId) {
       return signInService.checkSign(userId);
    }




}
