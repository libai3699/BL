package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.UserWeb;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 用户网页登录账号Mapper接口
 *
 * @author axing
 * @date 2026-02-26
 */
public interface UserWebMapper extends BaseMapper<UserWeb>
{
    /**
     * 查询用户网页登录账号
     *
     * @param id 用户网页登录账号ID
     * @return 用户网页登录账号
     */
    public UserWeb selectUserWebById(Long id);

    /**
     * 查询用户网页登录账号列表
     *
     * @param userWeb 用户网页登录账号
     * @return 用户网页登录账号集合
     */
    public List<UserWeb> selectUserWebList(UserWeb userWeb);

    /**
     * 新增用户网页登录账号
     *
     * @param userWeb 用户网页登录账号
     * @return 结果
     */
    public int insertUserWeb(UserWeb userWeb);

    /**
     * 修改用户网页登录账号
     *
     * @param userWeb 用户网页登录账号
     * @return 结果
     */
    public int updateUserWeb(UserWeb userWeb);

    /**
     * 删除用户网页登录账号
     *
     * @param id 用户网页登录账号ID
     * @return 结果
     */
    public int deleteUserWebById(Long id);

    /**
     * 批量删除用户网页登录账号
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserWebByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
