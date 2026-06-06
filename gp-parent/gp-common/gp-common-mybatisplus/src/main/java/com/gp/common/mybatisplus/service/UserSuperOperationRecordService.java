package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.UserSuperOperationRecord;
import com.gp.common.mybatisplus.mapper.UserSuperOperationRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 上级用户修改记录Service业务层处理
 *
 * @author axing
 * @date 2025-08-19
 */
@Service
public class UserSuperOperationRecordService extends ServiceImpl<UserSuperOperationRecordMapper, UserSuperOperationRecord> {
    @Autowired
    private UserSuperOperationRecordMapper userSuperOperationRecordMapper;

    /**
     * 查询上级用户修改记录
     *
     * @param id 上级用户修改记录ID
     * @return 上级用户修改记录
     */

    public UserSuperOperationRecord selectUserSuperOperationRecordById(Long id) {
        return userSuperOperationRecordMapper.selectUserSuperOperationRecordById(id);
    }

    /**
     * 查询上级用户修改记录列表
     *
     * @param param 上级用户修改记录
     * @return 上级用户修改记录
     */

    public List<UserSuperOperationRecord> selectUserSuperOperationRecordList(UserSuperOperationRecord param) {
        return userSuperOperationRecordMapper.selectUserSuperOperationRecordList(param);
    }

    /**
     * 新增上级用户修改记录
     *
     * @param param 上级用户修改记录
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertUserSuperOperationRecord(UserSuperOperationRecord param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改上级用户修改记录
     *
     * @param param 上级用户修改记录
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserSuperOperationRecord(UserSuperOperationRecord param) {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除上级用户修改记录
     *
     * @param ids 需要删除的上级用户修改记录ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserSuperOperationRecordByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除上级用户修改记录信息
     *
     * @param id 上级用户修改记录ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserSuperOperationRecordById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }
}
