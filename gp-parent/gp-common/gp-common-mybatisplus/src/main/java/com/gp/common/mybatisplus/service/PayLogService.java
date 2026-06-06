package com.gp.common.mybatisplus.service;

import java.util.List;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.PayLogMapper;
import com.gp.common.mybatisplus.entity.PayLog;
import com.gp.common.mybatisplus.service.PayLogService;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author axing
 * @date 2026-01-09
 */
@Service
public class PayLogService extends ServiceImpl<PayLogMapper, PayLog>
{
    @Autowired
    private PayLogMapper payLogMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */

    public PayLog selectPayLogById(Long id)
    {
        return payLogMapper.selectPayLogById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param param 【请填写功能名称】
     * @return 【请填写功能名称】
     */

    public List<PayLog> selectPayLogList(PayLog param)
    {
        return payLogMapper.selectPayLogList(param);
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param param 【请填写功能名称】
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertPayLog(PayLog param)
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
    public Boolean updatePayLog(PayLog param)
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
    public Boolean deletePayLogByIds(Long[] ids)
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
    public Boolean deletePayLogById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
