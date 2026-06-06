package com.gp.common.mybatisplus.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.base.utils.MD5Util;
import com.gp.common.mybatisplus.entity.UserWeb;
import com.gp.common.mybatisplus.mapper.UserWebMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 用户网页登录账号Service业务层处理
 *
 * @author axing
 * @date 2026-02-26
 */
@Service
public class UserWebService extends ServiceImpl<UserWebMapper, UserWeb> {
    @Autowired
    private UserWebMapper userWebMapper;

    /**
     * 查询用户网页登录账号
     *
     * @param id 用户网页登录账号ID
     * @return 用户网页登录账号
     */

    public UserWeb selectUserWebById(Long id) {
        return userWebMapper.selectUserWebById(id);
    }

    /**
     * 查询用户网页登录账号列表
     *
     * @param param 用户网页登录账号
     * @return 用户网页登录账号
     */

    public List<UserWeb> selectUserWebList(UserWeb param) {
        return userWebMapper.selectUserWebList(param);
    }

    /**
     * 新增用户网页登录账号
     *
     * @param param 用户网页登录账号
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertUserWeb(UserWeb param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改用户网页登录账号
     *
     * @param param 用户网页登录账号
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserWeb(UserWeb param) {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除用户网页登录账号
     *
     * @param ids 需要删除的用户网页登录账号ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserWebByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除用户网页登录账号信息
     *
     * @param id 用户网页登录账号ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserWebById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    public void setPassword(UserWeb userWeb, String password) {
        String salt = userWeb.getSalt();
        if (StrUtil.isEmpty(salt)) {
            salt = MD5Util.getSalt();
        }
        userWeb.setSalt(salt);
        userWeb.setPassword(MD5Util.encryptSalt(password, salt));
    }
}
