package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.UserPayMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 用户支付信息Mapper接口
 *
 * @author axing
 * @date 2026-01-29
 */
public interface UserPayMethodMapper extends BaseMapper<UserPayMethod>
{
    /**
     * 查询用户支付信息
     *
     * @param id 用户支付信息ID
     * @return 用户支付信息
     */
    public UserPayMethod selectUserPayMethodById(Long id);

    /**
     * 查询用户支付信息列表
     *
     * @param userPayMethod 用户支付信息
     * @return 用户支付信息集合
     */
    public List<UserPayMethod> selectUserPayMethodList(UserPayMethod userPayMethod);

    /**
     * 新增用户支付信息
     *
     * @param userPayMethod 用户支付信息
     * @return 结果
     */
    public int insertUserPayMethod(UserPayMethod userPayMethod);

    /**
     * 修改用户支付信息
     *
     * @param userPayMethod 用户支付信息
     * @return 结果
     */
    public int updateUserPayMethod(UserPayMethod userPayMethod);

    /**
     * 删除用户支付信息
     *
     * @param id 用户支付信息ID
     * @return 结果
     */
    public int deleteUserPayMethodById(Long id);

    /**
     * 批量删除用户支付信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserPayMethodByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
