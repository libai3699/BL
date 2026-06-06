package com.gp.common.mybatisplus.service;

import java.util.List;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.BankMapper;
import com.gp.common.mybatisplus.entity.Bank;
import com.gp.common.mybatisplus.service.BankService;

/**
 * 银行卡Service业务层处理
 *
 * @author axing
 * @date 2025-06-19
 */
@Service
public class BankService extends ServiceImpl<BankMapper, Bank>
{
    @Autowired
    private BankMapper bankMapper;

    /**
     * 查询银行卡
     *
     * @param id 银行卡ID
     * @return 银行卡
     */

    public Bank selectBankById(Long id)
    {
        return bankMapper.selectBankById(id);
    }

    /**
     * 查询银行卡列表
     *
     * @param param 银行卡
     * @return 银行卡
     */

    public List<Bank> selectBankList(Bank param)
    {
        return bankMapper.selectBankList(param);
    }

    /**
     * 新增银行卡
     *
     * @param param 银行卡
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertBank(Bank param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改银行卡
     *
     * @param param 银行卡
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateBank(Bank param)
    {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除银行卡
     *
     * @param ids 需要删除的银行卡ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteBankByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除银行卡信息
     *
     * @param id 银行卡ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteBankById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }


}
