package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.PayBlockchain;

import java.util.List;

/**
 * 冷钱包Mapper接口
 *
 * @author axing
 * @date 2026-01-29
 */
public interface PayBlockchainMapper extends BaseMapper<PayBlockchain>
{
    /**
     * 查询冷钱包
     *
     * @param id 冷钱包ID
     * @return 冷钱包
     */
    public PayBlockchain selectPayBlockchainById(Integer id);

    /**
     * 查询冷钱包列表
     *
     * @param payBlockchain 冷钱包
     * @return 冷钱包集合
     */
    public List<PayBlockchain> selectPayBlockchainList(PayBlockchain payBlockchain);



}
