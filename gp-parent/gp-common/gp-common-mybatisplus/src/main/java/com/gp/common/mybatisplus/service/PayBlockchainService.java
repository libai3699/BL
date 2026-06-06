package com.gp.common.mybatisplus.service;

import java.util.List;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.PayBlockchainMapper;
import com.gp.common.mybatisplus.entity.PayBlockchain;
import com.gp.common.mybatisplus.service.PayBlockchainService;

/**
 * 冷钱包Service业务层处理
 *
 * @author axing
 * @date 2026-01-29
 */
@Service
public class PayBlockchainService extends ServiceImpl<PayBlockchainMapper, PayBlockchain>
{
    @Autowired
    private PayBlockchainMapper payBlockchainMapper;

    /**
     * 查询冷钱包
     *
     * @param id 冷钱包ID
     * @return 冷钱包
     */

    public PayBlockchain selectPayBlockchainById(Integer id)
    {
        return payBlockchainMapper.selectPayBlockchainById(id);
    }

    /**
     * 查询冷钱包列表
     *
     * @param param 冷钱包
     * @return 冷钱包
     */

    public List<PayBlockchain> selectPayBlockchainList(PayBlockchain param)
    {
        return payBlockchainMapper.selectPayBlockchainList(param);
    }

    /**
     * 新增冷钱包
     *
     * @param param 冷钱包
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertPayBlockchain(PayBlockchain param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改冷钱包
     *
     * @param param 冷钱包
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePayBlockchain(PayBlockchain param)
    {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除冷钱包
     *
     * @param ids 需要删除的冷钱包ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePayBlockchainByIds(Integer[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除冷钱包信息
     *
     * @param id 冷钱包ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePayBlockchainById(Integer id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
