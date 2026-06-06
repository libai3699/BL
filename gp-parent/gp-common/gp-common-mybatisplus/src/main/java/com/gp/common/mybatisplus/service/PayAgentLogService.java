package com.gp.common.mybatisplus.service;

import java.util.List;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.PayAgentLogMapper;
import com.gp.common.mybatisplus.entity.PayAgentLog;
import com.gp.common.mybatisplus.service.PayAgentLogService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author axing
 * @date 2026-01-09
 */
@Service
public class PayAgentLogService extends ServiceImpl<PayAgentLogMapper, PayAgentLog>
{
    @Autowired
    private PayAgentLogMapper payAgentLogMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */

    public PayAgentLog selectPayAgentLogById(Integer id)
    {
        return payAgentLogMapper.selectPayAgentLogById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param param 【请填写功能名称】
     * @return 【请填写功能名称】
     */

    public List<PayAgentLog> selectPayAgentLogList(PayAgentLog param)
    {
        return payAgentLogMapper.selectPayAgentLogList(param);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param param 【请填写功能名称】
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertPayAgentLog(PayAgentLog param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param param 【请填写功能名称】
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePayAgentLog(PayAgentLog param)
    {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePayAgentLogByIds(Integer[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePayAgentLogById(Integer id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
