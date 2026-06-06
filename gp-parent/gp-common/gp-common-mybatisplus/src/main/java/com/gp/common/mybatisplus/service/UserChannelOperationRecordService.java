package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.UserChannelOperationRecord;
import com.gp.common.mybatisplus.mapper.UserChannelOperationRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 用户渠道修改记录Service业务层处理
 *
 * @author axing
 * @date 2025-08-19
 */
@Service
public class UserChannelOperationRecordService extends ServiceImpl<UserChannelOperationRecordMapper, UserChannelOperationRecord> {
    @Autowired
    private UserChannelOperationRecordMapper userChannelOperationRecordMapper;

    /**
     * 查询用户渠道修改记录
     *
     * @param id 用户渠道修改记录ID
     * @return 用户渠道修改记录
     */

    public UserChannelOperationRecord selectUserChannelOperationRecordById(Long id) {
        return userChannelOperationRecordMapper.selectUserChannelOperationRecordById(id);
    }

    /**
     * 查询用户渠道修改记录列表
     *
     * @param param 用户渠道修改记录
     * @return 用户渠道修改记录
     */

    public List<UserChannelOperationRecord> selectUserChannelOperationRecordList(UserChannelOperationRecord param) {
        return userChannelOperationRecordMapper.selectUserChannelOperationRecordList(param);
    }

    /**
     * 新增用户渠道修改记录
     *
     * @param param 用户渠道修改记录
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertUserChannelOperationRecord(UserChannelOperationRecord param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改用户渠道修改记录
     *
     * @param param 用户渠道修改记录
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateUserChannelOperationRecord(UserChannelOperationRecord param) {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除用户渠道修改记录
     *
     * @param ids 需要删除的用户渠道修改记录ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserChannelOperationRecordByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除用户渠道修改记录信息
     *
     * @param id 用户渠道修改记录ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteUserChannelOperationRecordById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }
}
