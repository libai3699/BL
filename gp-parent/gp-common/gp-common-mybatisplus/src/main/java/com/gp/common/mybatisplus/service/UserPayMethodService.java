package com.gp.common.mybatisplus.service;

import java.util.List;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.UserPayMethodMapper;
import com.gp.common.mybatisplus.entity.UserPayMethod;
import com.gp.common.mybatisplus.service.UserPayMethodService;

/**
 * 用户支付信息Service业务层处理
 *
 * @author axing
 * @date 2026-01-29
 */
@Service
public class UserPayMethodService extends ServiceImpl<UserPayMethodMapper, UserPayMethod>
{
    @Autowired
    private UserPayMethodMapper userPayMethodMapper;

    /**
     * 查询用户支付信息
     *
     * @param id 用户支付信息ID
     * @return 用户支付信息
     */

    public UserPayMethod selectUserPayMethodById(Long id)
    {
        return userPayMethodMapper.selectUserPayMethodById(id);
    }

    /**
     * 查询用户支付信息列表
     *
     * @param param 用户支付信息
     * @return 用户支付信息
     */

    public List<UserPayMethod> selectUserPayMethodList(UserPayMethod param)
    {
        return userPayMethodMapper.selectUserPayMethodList(param);
    }

    /**
     * 新增用户支付信息
     *
     * @param param 用户支付信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertUserPayMethod(UserPayMethod param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改用户支付信息
     *
     * @param param 用户支付信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserPayMethod(UserPayMethod param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除用户支付信息
     *
     * @param ids 需要删除的用户支付信息ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserPayMethodByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除用户支付信息信息
     *
     * @param id 用户支付信息ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserPayMethodById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
