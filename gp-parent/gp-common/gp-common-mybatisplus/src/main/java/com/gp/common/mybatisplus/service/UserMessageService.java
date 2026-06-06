package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.UserMessage;
import com.gp.common.mybatisplus.mapper.UserMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class UserMessageService extends ServiceImpl<UserMessageMapper, UserMessage> {
    @Autowired
    private UserMessageMapper userMessageMapper;

    /**
     * 查询用户消息
     *
     * @param id 用户消息ID
     * @return 用户消息
     */

    public UserMessage selectUserMessageById(Long id) {
        return userMessageMapper.selectUserMessageById(id);
    }

    /**
     * 查询用户消息列表
     *
     * @param userMessage 用户消息
     * @return 用户消息
     */

    public List<UserMessage> selectUserMessageList(UserMessage userMessage) {
        return userMessageMapper.selectUserMessageList(userMessage);
    }

    /**
     * 查询用户消息数量
     *
     * @param userMessage 用户消息
     * @return 用户消息数量
     */
    public Long selectUserMessageCount(UserMessage userMessage) {
        return userMessageMapper.selectUserMessageCount(userMessage);
    }

    /**
     * 新增用户消息
     *
     * @param userMessage 用户消息
     * @return 结果
     */

    public Boolean insertUserMessage(UserMessage userMessage) {
        userMessage.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(userMessage);
        return result;
    }

    /**
     * 修改用户消息
     *
     * @param userMessage 用户消息
     * @return 结果
     */

    public Boolean updateUserMessage(UserMessage userMessage) {
        boolean result = this.updateById(userMessage);
        return result;
    }

    /**
     * 批量删除用户消息
     *
     * @param ids 需要删除的用户消息ID
     * @return 结果
     */

    public Boolean deleteUserMessageByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除用户消息信息
     *
     * @param id 用户消息ID
     * @return 结果
     */

    public Boolean deleteUserMessageById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

}
