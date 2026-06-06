package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.UserSign;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 用户签到Mapper接口
 *
 * @author axing
 * @date 2024-05-15
 */
public interface UserSignMapper extends BaseMapper<UserSign>
{
    /**
     * 查询用户签到
     *
     * @param id 用户签到ID
     * @return 用户签到
     */
    public UserSign selectUserSignById(Long id);

    /**
     * 查询用户签到列表
     *
     * @param userSign 用户签到
     * @return 用户签到集合
     */
    public List<UserSign> selectUserSignList(UserSign userSign);

    /**
     * 新增用户签到
     *
     * @param userSign 用户签到
     * @return 结果
     */
    public int insertUserSign(UserSign userSign);

    /**
     * 修改用户签到
     *
     * @param userSign 用户签到
     * @return 结果
     */
    public int updateUserSign(UserSign userSign);

    /**
     * 删除用户签到
     *
     * @param id 用户签到ID
     * @return 结果
     */
    public int deleteUserSignById(Long id);

    /**
     * 批量删除用户签到
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserSignByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
