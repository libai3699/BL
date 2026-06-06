package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.UserBlackIp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 用户黑名单Mapper接口
 *
 * @author axing
 * @date 2024-07-11
 */
public interface UserBlackIpMapper extends BaseMapper<UserBlackIp>
{
    /**
     * 查询用户黑名单
     *
     * @param id 用户黑名单ID
     * @return 用户黑名单
     */
    public UserBlackIp selectUserBlackIpById(Long id);

    /**
     * 查询用户黑名单列表
     *
     * @param userBlackIp 用户黑名单
     * @return 用户黑名单集合
     */
    public List<UserBlackIp> selectUserBlackIpList(UserBlackIp userBlackIp);

    /**
     * 新增用户黑名单
     *
     * @param userBlackIp 用户黑名单
     * @return 结果
     */
    public int insertUserBlackIp(UserBlackIp userBlackIp);

    /**
     * 修改用户黑名单
     *
     * @param userBlackIp 用户黑名单
     * @return 结果
     */
    public int updateUserBlackIp(UserBlackIp userBlackIp);

    /**
     * 删除用户黑名单
     *
     * @param id 用户黑名单ID
     * @return 结果
     */
    public int deleteUserBlackIpById(Long id);

    /**
     * 批量删除用户黑名单
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserBlackIpByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
