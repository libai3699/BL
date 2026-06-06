package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.UserDefaultChannel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 用户下级默认Mapper接口
 *
 * @author axing
 * @date 2025-10-16
 */
public interface UserDefaultChannelMapper extends BaseMapper<UserDefaultChannel>
{
    /**
     * 查询用户下级默认
     *
     * @param id 用户下级默认ID
     * @return 用户下级默认
     */
    public UserDefaultChannel selectUserDefaultChannelById(Long id);

    /**
     * 查询用户下级默认列表
     *
     * @param userDefaultChannel 用户下级默认
     * @return 用户下级默认集合
     */
    public List<UserDefaultChannel> selectUserDefaultChannelList(UserDefaultChannel userDefaultChannel);

    /**
     * 新增用户下级默认
     *
     * @param userDefaultChannel 用户下级默认
     * @return 结果
     */
    public int insertUserDefaultChannel(UserDefaultChannel userDefaultChannel);

    /**
     * 修改用户下级默认
     *
     * @param userDefaultChannel 用户下级默认
     * @return 结果
     */
    public int updateUserDefaultChannel(UserDefaultChannel userDefaultChannel);

    /**
     * 删除用户下级默认
     *
     * @param id 用户下级默认ID
     * @return 结果
     */
    public int deleteUserDefaultChannelById(Long id);

    /**
     * 批量删除用户下级默认
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserDefaultChannelByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
