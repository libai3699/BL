package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.UserCommission;
import com.gp.common.mybatisplus.mapper.UserCommissionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 用户佣金Service业务层处理
 *
 * @author axing
 * @date 2025-04-30
 */
@Service
public class UserCommissionService extends ServiceImpl<UserCommissionMapper, UserCommission> {
    @Resource
    private UserCommissionMapper userCommissionMapper;

    /**
     * 查询用户佣金
     *
     * @param id 用户佣金ID
     * @return 用户佣金
     */

    public UserCommission selectUserCommissionById(Long id) {
        return userCommissionMapper.selectUserCommissionById(id);
    }

    /**
     * 查询用户佣金列表
     *
     * @param param 用户佣金
     * @return 用户佣金
     */

    public List<UserCommission> selectUserCommissionList(UserCommission param) {
        return userCommissionMapper.selectUserCommissionList(param);
    }

    /**
     * 新增用户佣金
     *
     * @param param 用户佣金
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertUserCommission(UserCommission param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改用户佣金
     *
     * @param param 用户佣金
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserCommission(UserCommission param) {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除用户佣金
     *
     * @param ids 需要删除的用户佣金ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserCommissionByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除用户佣金信息
     *
     * @param id 用户佣金ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserCommissionById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }


}
