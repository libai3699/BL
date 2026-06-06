package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.EmailBlacklist;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 邮箱黑名单Mapper接口
 *
 * @author axing
 * @date 2025-04-09
 */
public interface EmailBlacklistMapper extends BaseMapper<EmailBlacklist>
{
    /**
     * 查询邮箱黑名单
     *
     * @param id 邮箱黑名单ID
     * @return 邮箱黑名单
     */
    public EmailBlacklist selectEmailBlacklistById(Long id);

    /**
     * 查询邮箱黑名单列表
     *
     * @param emailBlacklist 邮箱黑名单
     * @return 邮箱黑名单集合
     */
    public List<EmailBlacklist> selectEmailBlacklistList(EmailBlacklist emailBlacklist);

    /**
     * 新增邮箱黑名单
     *
     * @param emailBlacklist 邮箱黑名单
     * @return 结果
     */
    public int insertEmailBlacklist(EmailBlacklist emailBlacklist);

    /**
     * 修改邮箱黑名单
     *
     * @param emailBlacklist 邮箱黑名单
     * @return 结果
     */
    public int updateEmailBlacklist(EmailBlacklist emailBlacklist);

    /**
     * 删除邮箱黑名单
     *
     * @param id 邮箱黑名单ID
     * @return 结果
     */
    public int deleteEmailBlacklistById(Long id);

    /**
     * 批量删除邮箱黑名单
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteEmailBlacklistByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
