package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.UserMessage;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMessageMapper extends BaseMapper<UserMessage> {
    /**
     * 查询用户消息
     *
     * @param id 用户消息ID
     * @return 用户消息
     */
    public UserMessage selectUserMessageById(Long id);

    /**
     * 查询用户消息列表
     *
     * @param userMessage 用户消息
     * @return 用户消息集合
     */
    public List<UserMessage> selectUserMessageList(UserMessage userMessage);

    /**
     * 查询用户消息数量
     *
     * @param userMessage 用户消息
     * @return 用户消息数量
     */
    public Long selectUserMessageCount(UserMessage userMessage);

    /**
     * 新增用户消息
     *
     * @param userMessage 用户消息
     * @return 结果
     */
    public int insertUserMessage(UserMessage userMessage);

    /**
     * 修改用户消息
     *
     * @param userMessage 用户消息
     * @return 结果
     */
    public int updateUserMessage(UserMessage userMessage);

    /**
     * 删除用户消息
     *
     * @param id 用户消息ID
     * @return 结果
     */
    public int deleteUserMessageById(Long id);

    /**
     * 批量删除用户消息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserMessageByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();
}
