package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.NotifyMessage;
import com.gp.common.mybatisplus.mapper.NotifyMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class NotifyMessageService extends ServiceImpl<NotifyMessageMapper, NotifyMessage> {
    @Autowired
    private NotifyMessageMapper notifyMessageMapper;

    /**
     * /**
     * 查询通知消息列表
     *
     * @param tNotifyMessage 通知消息
     * @return 通知消息
     */
    public List<NotifyMessage> selectNotifyMessageList(NotifyMessage tNotifyMessage) {
        return notifyMessageMapper.selectNotifyMessageList(tNotifyMessage);
    }

    /**
     * 新增通知消息
     *
     * @param tNotifyMessage 通知消息
     * @return 结果
     */

    public Boolean insertNotifyMessage(NotifyMessage tNotifyMessage) {
        tNotifyMessage.setCreateTime(DateUtils.getNowDate());
        return this.save(tNotifyMessage);
    }

    /**
     * 批量删除通知消息
     *
     * @param ids 需要删除的通知消息ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTNotifyMessageByIds(Long[] ids) {
        return this.removeByIds(Arrays.asList(ids));
    }

    /**
     * 删除通知消息信息
     *
     * @param id 通知消息ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTNotifyMessageById(Long id) {
        return this.removeById(id);
    }
}
