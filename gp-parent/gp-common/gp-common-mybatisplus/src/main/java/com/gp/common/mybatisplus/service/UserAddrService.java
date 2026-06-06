package com.gp.common.mybatisplus.service;

import java.util.List;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gp.common.base.constant.CurrencyConstants;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.UserAddrMapper;
import com.gp.common.mybatisplus.entity.UserAddr;
import com.gp.common.mybatisplus.service.UserAddrService;

/**
 * 用户钱包地址Service业务层处理
 *
 * @author axing
 * @date 2024-11-27
 */
@Service
public class UserAddrService extends ServiceImpl<UserAddrMapper, UserAddr>
{
    @Autowired
    private UserAddrMapper userAddrMapper;

    /**
     * 查询用户钱包地址
     *
     * @param id 用户钱包地址ID
     * @return 用户钱包地址
     */

    public UserAddr selectUserAddrById(Long id)
    {
        return userAddrMapper.selectUserAddrById(id);
    }

    /**
     * 查询用户钱包地址列表
     *
     * @param param 用户钱包地址
     * @return 用户钱包地址
     */

    public List<UserAddr> selectUserAddrList(UserAddr param)
    {
        return userAddrMapper.selectUserAddrList(param);
    }

    /**
     * 新增用户钱包地址
     *
     * @param param 用户钱包地址
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertUserAddr(UserAddr param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改用户钱包地址
     *
     * @param param 用户钱包地址
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserAddr(UserAddr param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除用户钱包地址
     *
     * @param ids 需要删除的用户钱包地址ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserAddrByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除用户钱包地址信息
     *
     * @param id 用户钱包地址ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserAddrById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }

    public List<UserAddr> getUserAddr(Long userId) {
        return this.list(Wrappers.lambdaQuery(UserAddr.class)
                .eq(UserAddr::getUserId, userId)
                .eq(UserAddr::getItemId, CurrencyConstants.usdt_Item)
                .eq( UserAddr::getCurrencyId, CurrencyConstants.usdt)
                .eq(UserAddr::getChainTag, CurrencyConstants.usdt_chainTag)
        );
    }
}
