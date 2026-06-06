package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.Bank;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 银行卡Mapper接口
 *
 * @author axing
 * @date 2025-06-19
 */
public interface BankMapper extends BaseMapper<Bank>
{
    /**
     * 查询银行卡
     *
     * @param id 银行卡ID
     * @return 银行卡
     */
    public Bank selectBankById(Long id);

    /**
     * 查询银行卡列表
     *
     * @param bank 银行卡
     * @return 银行卡集合
     */
    public List<Bank> selectBankList(Bank bank);

    /**
     * 新增银行卡
     *
     * @param bank 银行卡
     * @return 结果
     */
    public int insertBank(Bank bank);

    /**
     * 修改银行卡
     *
     * @param bank 银行卡
     * @return 结果
     */
    public int updateBank(Bank bank);

    /**
     * 删除银行卡
     *
     * @param id 银行卡ID
     * @return 结果
     */
    public int deleteBankById(Long id);

    /**
     * 批量删除银行卡
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteBankByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
