package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.OfflineWithdrawalAddresses;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 线下提现相关信息Mapper接口
 *
 * @author axing
 * @date 2025-11-10
 */
public interface OfflineWithdrawalAddressesMapper extends BaseMapper<OfflineWithdrawalAddresses>
{
    /**
     * 查询线下提现相关信息
     *
     * @param id 线下提现相关信息ID
     * @return 线下提现相关信息
     */
    public OfflineWithdrawalAddresses selectOfflineWithdrawalAddressesById(Integer id);

    /**
     * 查询线下提现相关信息列表
     *
     * @param offlineWithdrawalAddresses 线下提现相关信息
     * @return 线下提现相关信息集合
     */
    public List<OfflineWithdrawalAddresses> selectOfflineWithdrawalAddressesList(OfflineWithdrawalAddresses offlineWithdrawalAddresses);

    /**
     * 新增线下提现相关信息
     *
     * @param offlineWithdrawalAddresses 线下提现相关信息
     * @return 结果
     */
    public int insertOfflineWithdrawalAddresses(OfflineWithdrawalAddresses offlineWithdrawalAddresses);

    /**
     * 修改线下提现相关信息
     *
     * @param offlineWithdrawalAddresses 线下提现相关信息
     * @return 结果
     */
    public int updateOfflineWithdrawalAddresses(OfflineWithdrawalAddresses offlineWithdrawalAddresses);

    /**
     * 删除线下提现相关信息
     *
     * @param id 线下提现相关信息ID
     * @return 结果
     */
    public int deleteOfflineWithdrawalAddressesById(Integer id);

    /**
     * 批量删除线下提现相关信息
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteOfflineWithdrawalAddressesByIds(Integer[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
