package com.gp.common.mybatisplus.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.NotifyMessage;
import com.gp.common.mybatisplus.entity.NotifyMessage;

import java.util.List;

/**
 * 通知消息Mapper接口
 *
 * @author axing
 * @date 2023-12-27
 */
public interface NotifyMessageMapper extends BaseMapper<NotifyMessage>
{


    /**
     * 查询通知消息列表
     *
     * @param notifyMessage 通知消息
     * @return 通知消息集合
     */
    public List<NotifyMessage> selectNotifyMessageList(NotifyMessage notifyMessage);



}
