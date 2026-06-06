package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.UserCommission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 用户佣金Mapper接口
 *
 * @author axing
 * @date 2025-04-30
 */
public interface UserCommissionMapper extends BaseMapper<UserCommission>
{
    /**
     * 查询用户佣金
     *
     * @param id 用户佣金ID
     * @return 用户佣金
     */
    public UserCommission selectUserCommissionById(Long id);

    /**
     * 查询用户佣金列表
     *
     * @param userCommission 用户佣金
     * @return 用户佣金集合
     */
    public List<UserCommission> selectUserCommissionList(UserCommission userCommission);

    /**
     * 新增用户佣金
     *
     * @param userCommission 用户佣金
     * @return 结果
     */
    public int insertUserCommission(UserCommission userCommission);

    /**
     * 修改用户佣金
     *
     * @param userCommission 用户佣金
     * @return 结果
     */
    public int updateUserCommission(UserCommission userCommission);

    /**
     * 删除用户佣金
     *
     * @param id 用户佣金ID
     * @return 结果
     */
    public int deleteUserCommissionById(Long id);

    /**
     * 批量删除用户佣金
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserCommissionByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
