package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.EmailBlacklist;
import com.gp.common.mybatisplus.mapper.EmailBlacklistMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * 邮箱黑名单Service业务层处理
 *
 * @author axing
 * @date 2025-04-09
 */
@Service
public class EmailBlacklistService extends ServiceImpl<EmailBlacklistMapper, EmailBlacklist>
{
    @Autowired
    private EmailBlacklistMapper emailBlacklistMapper;

    /**
     * 查询邮箱黑名单
     *
     * @param id 邮箱黑名单ID
     * @return 邮箱黑名单
     */

    public EmailBlacklist selectEmailBlacklistById(Long id)
    {
        return emailBlacklistMapper.selectEmailBlacklistById(id);
    }

    /**
     * 查询邮箱黑名单列表
     *
     * @param param 邮箱黑名单
     * @return 邮箱黑名单
     */

    public List<EmailBlacklist> selectEmailBlacklistList(EmailBlacklist param)
    {
        return emailBlacklistMapper.selectEmailBlacklistList(param);
    }

    /**
     * 新增邮箱黑名单
     *
     * @param param 邮箱黑名单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertEmailBlacklist(EmailBlacklist param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改邮箱黑名单
     *
     * @param param 邮箱黑名单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateEmailBlacklist(EmailBlacklist param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除邮箱黑名单
     *
     * @param ids 需要删除的邮箱黑名单ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteEmailBlacklistByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除邮箱黑名单信息
     *
     * @param id 邮箱黑名单ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteEmailBlacklistById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
