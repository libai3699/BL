package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.SystemMessage;

import java.util.List;

/**
 * 站内Mapper接口
 *
 * @author axing
 * @date 2025-06-26
 */
public interface SystemMessageMapper extends BaseMapper<SystemMessage>
{
    /**
     * 查询站内
     *
     * @param id 站内ID
     * @return 站内
     */
    public SystemMessage selectSystemMessageById(Long id);

    /**
     * 查询站内列表
     *
     * @param systemMessage 站内
     * @return 站内集合
     */
    public List<SystemMessage> selectSystemMessageList(SystemMessage systemMessage);

    /**
     * 新增站内
     *
     * @param systemMessage 站内
     * @return 结果
     */
    public int insertSystemMessage(SystemMessage systemMessage);

    /**
     * 修改站内
     *
     * @param systemMessage 站内
     * @return 结果
     */
    public int updateSystemMessage(SystemMessage systemMessage);

    /**
     * 删除站内
     *
     * @param id 站内ID
     * @return 结果
     */
    public int deleteSystemMessageById(Long id);

    /**
     * 批量删除站内
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteSystemMessageByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
