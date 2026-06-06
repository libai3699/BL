package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.UserSuperOperationRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 上级用户修改记录Mapper接口
 *
 * @author axing
 * @date 2025-08-19
 */
@Mapper
public interface UserSuperOperationRecordMapper extends BaseMapper<UserSuperOperationRecord> {
    /**
     * 查询上级用户修改记录
     *
     * @param id 上级用户修改记录ID
     * @return 上级用户修改记录
     */
    public UserSuperOperationRecord selectUserSuperOperationRecordById(Long id);

    /**
     * 查询上级用户修改记录列表
     *
     * @param userSuperOperationRecord 上级用户修改记录
     * @return 上级用户修改记录集合
     */
    public List<UserSuperOperationRecord> selectUserSuperOperationRecordList(UserSuperOperationRecord userSuperOperationRecord);

    /**
     * 新增上级用户修改记录
     *
     * @param userSuperOperationRecord 上级用户修改记录
     * @return 结果
     */
    public int insertUserSuperOperationRecord(UserSuperOperationRecord userSuperOperationRecord);

    /**
     * 修改上级用户修改记录
     *
     * @param userSuperOperationRecord 上级用户修改记录
     * @return 结果
     */
    public int updateUserSuperOperationRecord(UserSuperOperationRecord userSuperOperationRecord);

    /**
     * 删除上级用户修改记录
     *
     * @param id 上级用户修改记录ID
     * @return 结果
     */
    public int deleteUserSuperOperationRecordById(Long id);

    /**
     * 批量删除上级用户修改记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserSuperOperationRecordByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
