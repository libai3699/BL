package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.vo.UserHistoryLoginInfo;
import com.gp.common.mybatisplus.entity.UserLoginHistory;
import com.gp.common.mybatisplus.mapper.UserLoginHistoryMapper;
import com.gp.common.mybatisplus.vo.UserLoginStatVO;
import com.gp.common.mybatisplus.vo.UserSharedIpVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 用户登录历史Service业务层处理
 *
 * @author axing
 * @date 2025-01-22
 */
@Service
public class UserLoginHistoryService extends ServiceImpl<UserLoginHistoryMapper, UserLoginHistory> {
    @Autowired
    private UserLoginHistoryMapper userLoginHistoryMapper;

    /**
     * 查询用户登录历史
     *
     * @param id 用户登录历史ID
     * @return 用户登录历史
     */

    public UserLoginHistory selectUserLoginHistoryById(Long id) {
        return userLoginHistoryMapper.selectUserLoginHistoryById(id);
    }

    /**
     * 查询用户登录历史列表
     *
     * @param param 用户登录历史
     * @return 用户登录历史
     */

    public List<UserLoginHistory> selectUserLoginHistoryList(UserLoginHistory param) {
        return userLoginHistoryMapper.selectUserLoginHistoryList(param);
    }

    public List<UserSharedIpVO> selectSharedIpUserList(String ip) {
        return userLoginHistoryMapper.selectSharedIpUserList(ip);
    }

    public UserHistoryLoginInfo selectCurrentUserHistoryLoginInfo(Long userId) {
        UserHistoryLoginInfo info = userLoginHistoryMapper.selectCurrentUserHistoryLoginInfo(userId);
        if (info == null) {
            return null;
        }
        info.setIpList(parseDistinctIpList(info.getIpListStr()));
        return info;
    }

    public List<UserSharedIpVO> selectSharedIpUserListByIps(List<String> ips, Long excludeUserId) {
        if (ips == null || ips.isEmpty()) {
            return new ArrayList<>();
        }
        return userLoginHistoryMapper.selectSharedIpUserListByIps(ips, excludeUserId);
    }

    private List<String> parseDistinctIpList(String ipListStr) {
        if (ipListStr == null || ipListStr.trim().isEmpty()) {
            return new ArrayList<>();
        }
        String[] ipArray = ipListStr.split(",");
        Set<String> ipSet = new LinkedHashSet<>();
        for (String ip : ipArray) {
            if (ip != null && !ip.trim().isEmpty()) {
                ipSet.add(ip.trim());
            }
        }
        return new ArrayList<>(ipSet);
    }

    /**
     * 新增用户登录历史
     *
     * @param param 用户登录历史
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertUserLoginHistory(UserLoginHistory param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改用户登录历史
     *
     * @param param 用户登录历史
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserLoginHistory(UserLoginHistory param) {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除用户登录历史
     *
     * @param ids 需要删除的用户登录历史ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserLoginHistoryByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除用户登录历史信息
     *
     * @param id 用户登录历史ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserLoginHistoryById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    /**
     * 根据IP统计用户数量排名
     * 查询每个IP下每个用户最新一次登录记录，按IP分组统计用户数量
     *
     * @return IP统计列表，按用户数量降序
     */
    public List<UserLoginStatVO> selectUserCountRankByIp() {
        return userLoginHistoryMapper.selectUserCountRankByIp();
    }

    /**
     * 根据浏览器指纹统计用户数量排名
     * 查询每个浏览器指纹下每个用户最新一次登录记录，按浏览器指纹组统计用户数量
     *
     * @return 浏览器指纹统计列表，按用户数量降序
     */
    public List<UserLoginStatVO> selectUserCountRankByDevice() {
        return userLoginHistoryMapper.selectUserCountRankByDevice();
    }
}
