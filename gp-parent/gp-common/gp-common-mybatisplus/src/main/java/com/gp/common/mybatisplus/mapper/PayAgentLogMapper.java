package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.PayAgentLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author axing
 * @date 2026-01-09
 */
public interface PayAgentLogMapper extends BaseMapper<PayAgentLog>
{
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public PayAgentLog selectPayAgentLogById(Integer id);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param payAgentLog 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<PayAgentLog> selectPayAgentLogList(PayAgentLog payAgentLog);

    /**
     * 新增【请填写功能名称】
     *
     * @param payAgentLog 【请填写功能名称】
     * @return 结果
     */
    public int insertPayAgentLog(PayAgentLog payAgentLog);

    /**
     * 修改【请填写功能名称】
     *
     * @param payAgentLog 【请填写功能名称】
     * @return 结果
     */
    public int updatePayAgentLog(PayAgentLog payAgentLog);

    /**
     * 删除【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deletePayAgentLogById(Integer id);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deletePayAgentLogByIds(Integer[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
