package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.SystemMessage;
import com.gp.common.mybatisplus.mapper.SystemMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 站内Service业务层处理
 *
 * @author axing
 * @date 2025-06-26
 */
@Service
public class SystemMessageService extends ServiceImpl<SystemMessageMapper, SystemMessage>
{
    @Autowired
    private SystemMessageMapper systemMessageMapper;

    /**
     * 查询站内
     *
     * @param id 站内ID
     * @return 站内
     */

    public SystemMessage selectSystemMessageById(Long id)
    {
        return systemMessageMapper.selectSystemMessageById(id);
    }

    /**
     * 查询站内列表
     *
     * @param param 站内
     * @return 站内
     */

    public List<SystemMessage> selectSystemMessageList(SystemMessage param)
    {
        return systemMessageMapper.selectSystemMessageList(param);
    }

    /**
     * 新增站内
     *
     * @param param 站内
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertSystemMessage(SystemMessage param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改站内
     *
     * @param param 站内
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateSystemMessage(SystemMessage param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除站内
     *
     * @param ids 需要删除的站内ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteSystemMessageByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除站内信息
     *
     * @param id 站内ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteSystemMessageById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
