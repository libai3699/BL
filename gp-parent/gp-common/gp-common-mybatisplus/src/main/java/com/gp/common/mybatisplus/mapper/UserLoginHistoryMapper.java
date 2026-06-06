package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.vo.UserHistoryLoginInfo;
import com.gp.common.mybatisplus.entity.UserLoginHistory;
import com.gp.common.mybatisplus.vo.UserLoginStatVO;
import com.gp.common.mybatisplus.vo.UserSharedIpVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户登录历史Mapper接口
 *
 * @author axing
 * @date 2025-01-22
 */
public interface UserLoginHistoryMapper extends BaseMapper<UserLoginHistory>
{
    /**
     * 查询用户登录历史
     *
     * @param id 用户登录历史ID
     * @return 用户登录历史
     */
    public UserLoginHistory selectUserLoginHistoryById(Long id);

    /**
     * 查询用户登录历史列表
     *
     * @param userLoginHistory 用户登录历史
     * @return 用户登录历史集合
     */
    public List<UserLoginHistory> selectUserLoginHistoryList(UserLoginHistory userLoginHistory);

    /**
     * 新增用户登录历史
     *
     * @param userLoginHistory 用户登录历史
     * @return 结果
     */
    public int insertUserLoginHistory(UserLoginHistory userLoginHistory);

    /**
     * 修改用户登录历史
     *
     * @param userLoginHistory 用户登录历史
     * @return 结果
     */
    public int updateUserLoginHistory(UserLoginHistory userLoginHistory);

    /**
     * 删除用户登录历史
     *
     * @param id 用户登录历史ID
     * @return 结果
     */
    public int deleteUserLoginHistoryById(Long id);

    /**
     * 批量删除用户登录历史
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserLoginHistoryByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    /**
     * 按历史登录IP查询用户列表
     *
     * @param ip 历史登录IP
     * @param userId 当前用户ID，可为空，用于排除自己
     * @return 用户列表
     */
    List<UserSharedIpVO> selectSharedIpUserList(@Param("ip") String ip);

    /**
     * 获取当前用户历史登录信息
     *
     * @param userId 用户ID
     * @return 历史登录信息
     */
    UserHistoryLoginInfo selectCurrentUserHistoryLoginInfo(@Param("userId") Long userId);

    /**
     * 按IP列表查询共用IP用户
     *
     * @param ips IP列表
     * @param excludeUserId 排除用户ID
     * @return 用户列表
     */
    List<UserSharedIpVO> selectSharedIpUserListByIps(@Param("ips") List<String> ips, @Param("excludeUserId") Long excludeUserId);

    /**
     * 根据IP统计用户数量排名
     * 查询每个IP下每个用户最新一次登录记录，按IP分组统计用户数量
     *
     * @return IP统计列表，按用户数量降序
     */
    List<UserLoginStatVO> selectUserCountRankByIp();

    /**
     * 根据浏览器指纹统计用户数量排名
     * 查询每个浏览器指纹下每个用户最新一次登录记录，按浏览器指纹分组统计用户数量
     *
     * @return 浏览器指纹统计列表，按用户数量降序
     */
    List<UserLoginStatVO> selectUserCountRankByDevice();

}
