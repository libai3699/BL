package com.gp.common.mybatisplus.service;

import java.util.List;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.OfflineWithdrawalAddressesMapper;
import com.gp.common.mybatisplus.entity.OfflineWithdrawalAddresses;
import com.gp.common.mybatisplus.service.OfflineWithdrawalAddressesService;

/**
 * 线下提现相关信息Service业务层处理
 *
 * @author axing
 * @date 2025-11-10
 */
@Service
public class OfflineWithdrawalAddressesService extends ServiceImpl<OfflineWithdrawalAddressesMapper, OfflineWithdrawalAddresses>
{
    @Autowired
    private OfflineWithdrawalAddressesMapper offlineWithdrawalAddressesMapper;

    /**
     * 查询线下提现相关信息
     *
     * @param id 线下提现相关信息ID
     * @return 线下提现相关信息
     */

    public OfflineWithdrawalAddresses selectOfflineWithdrawalAddressesById(Integer id)
    {
        return offlineWithdrawalAddressesMapper.selectOfflineWithdrawalAddressesById(id);
    }

    /**
     * 查询线下提现相关信息列表
     *
     * @param param 线下提现相关信息
     * @return 线下提现相关信息
     */

    public List<OfflineWithdrawalAddresses> selectOfflineWithdrawalAddressesList(OfflineWithdrawalAddresses param)
    {
        return offlineWithdrawalAddressesMapper.selectOfflineWithdrawalAddressesList(param);
    }

    /**
     * 新增线下提现相关信息
     *
     * @param param 线下提现相关信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertOfflineWithdrawalAddresses(OfflineWithdrawalAddresses param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改线下提现相关信息
     *
     * @param param 线下提现相关信息
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOfflineWithdrawalAddresses(OfflineWithdrawalAddresses param)
    {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除线下提现相关信息
     *
     * @param ids 需要删除的线下提现相关信息ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOfflineWithdrawalAddressesByIds(Integer[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除线下提现相关信息信息
     *
     * @param id 线下提现相关信息ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOfflineWithdrawalAddressesById(Integer id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
