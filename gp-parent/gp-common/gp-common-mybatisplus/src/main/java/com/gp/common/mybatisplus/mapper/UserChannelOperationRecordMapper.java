package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.UserChannelOperationRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户渠道修改记录Mapper接口
 *
 * @author axing
 * @date 2025-08-19
 */
@Mapper
public interface UserChannelOperationRecordMapper extends BaseMapper<UserChannelOperationRecord> {
    /**
     * 查询用户渠道修改记录
     *
     * @param id 用户渠道修改记录ID
     * @return 用户渠道修改记录
     */
    public UserChannelOperationRecord selectUserChannelOperationRecordById(Long id);

    /**
     * 查询用户渠道修改记录列表
     *
     * @param userChannelOperationRecord 用户渠道修改记录
     * @return 用户渠道修改记录集合
     */
    public List<UserChannelOperationRecord> selectUserChannelOperationRecordList(UserChannelOperationRecord userChannelOperationRecord);

    /**
     * 新增用户渠道修改记录
     *
     * @param userChannelOperationRecord 用户渠道修改记录
     * @return 结果
     */
    public int insertUserChannelOperationRecord(UserChannelOperationRecord userChannelOperationRecord);

    /**
     * 修改用户渠道修改记录
     *
     * @param userChannelOperationRecord 用户渠道修改记录
     * @return 结果
     */
    public int updateUserChannelOperationRecord(UserChannelOperationRecord userChannelOperationRecord);

    /**
     * 删除用户渠道修改记录
     *
     * @param id 用户渠道修改记录ID
     * @return 结果
     */
    public int deleteUserChannelOperationRecordById(Long id);

    /**
     * 批量删除用户渠道修改记录
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserChannelOperationRecordByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
