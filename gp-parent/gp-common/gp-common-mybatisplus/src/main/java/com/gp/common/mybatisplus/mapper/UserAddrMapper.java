package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.UserAddr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 用户钱包地址Mapper接口
 *
 * @author axing
 * @date 2024-11-27
 */
public interface UserAddrMapper extends BaseMapper<UserAddr>
{
    /**
     * 查询用户钱包地址
     *
     * @param id 用户钱包地址ID
     * @return 用户钱包地址
     */
    public UserAddr selectUserAddrById(Long id);

    /**
     * 查询用户钱包地址列表
     *
     * @param userAddr 用户钱包地址
     * @return 用户钱包地址集合
     */
    public List<UserAddr> selectUserAddrList(UserAddr userAddr);

    /**
     * 新增用户钱包地址
     *
     * @param userAddr 用户钱包地址
     * @return 结果
     */
    public int insertUserAddr(UserAddr userAddr);

    /**
     * 修改用户钱包地址
     *
     * @param userAddr 用户钱包地址
     * @return 结果
     */
    public int updateUserAddr(UserAddr userAddr);

    /**
     * 删除用户钱包地址
     *
     * @param id 用户钱包地址ID
     * @return 结果
     */
    public int deleteUserAddrById(Long id);

    /**
     * 批量删除用户钱包地址
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserAddrByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
