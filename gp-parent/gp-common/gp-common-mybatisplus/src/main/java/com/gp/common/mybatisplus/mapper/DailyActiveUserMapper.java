package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.DailyActiveUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 每日活跃用户Mapper接口
 *
 * @author axing
 * @date 2025-04-29
 */
public interface DailyActiveUserMapper extends BaseMapper<DailyActiveUser> {
    /**
     * 查询每日活跃用户
     *
     * @param id 每日活跃用户ID
     * @return 每日活跃用户
     */
    public DailyActiveUser selectDailyActiveUserById(Long id);

    /**
     * 查询每日活跃用户列表
     *
     * @param dailyActiveUser 每日活跃用户
     * @return 每日活跃用户集合
     */
    public List<DailyActiveUser> selectDailyActiveUserList(DailyActiveUser dailyActiveUser);

    /**
     * 新增每日活跃用户
     *
     * @param dailyActiveUser 每日活跃用户
     * @return 结果
     */
    public int insertDailyActiveUser(DailyActiveUser dailyActiveUser);

    /**
     * 修改每日活跃用户
     *
     * @param dailyActiveUser 每日活跃用户
     * @return 结果
     */
    public int updateDailyActiveUser(DailyActiveUser dailyActiveUser);

    /**
     * 删除每日活跃用户
     *
     * @param id 每日活跃用户ID
     * @return 结果
     */
    public int deleteDailyActiveUserById(Long id);

    /**
     * 批量删除每日活跃用户
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteDailyActiveUserByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();


    /**
     * 新增修改每日活跃用户
     */
    int insertOrUpdate(DailyActiveUser dailyActiveUser);

    void insertOrUpdateData(@Param("vo") DailyActiveUser param);
}
