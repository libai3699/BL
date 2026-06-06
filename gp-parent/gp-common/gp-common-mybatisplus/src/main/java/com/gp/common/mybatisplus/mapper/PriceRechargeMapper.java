package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.PriceRecharge;

import java.util.List;

/**
 * 充值列Mapper接口
 *
 * @author axing
 * @date 2024-05-14
 */
public interface PriceRechargeMapper extends BaseMapper<PriceRecharge>
{
    /**
     * 查询充值列
     *
     * @param id 充值列ID
     * @return 充值列
     */
    public PriceRecharge selectPriceRechargeById(Long id);

    /**
     * 查询充值列列表
     *
     * @param priceRecharge 充值列
     * @return 充值列集合
     */
    public List<PriceRecharge> selectPriceRechargeList(PriceRecharge priceRecharge);

    /**
     * 查询充值列数量
     *
     * @param priceRecharge 充值列
     * @return 充值列数量
     */
    public long selectPriceRechargeCount(PriceRecharge priceRecharge);

    /**
     * 新增充值列
     *
     * @param priceRecharge 充值列
     * @return 结果
     */
    public int insertPriceRecharge(PriceRecharge priceRecharge);

    /**
     * 修改充值列
     *
     * @param priceRecharge 充值列
     * @return 结果
     */
    public int updatePriceRecharge(PriceRecharge priceRecharge);

    /**
     * 删除充值列
     *
     * @param id 充值列ID
     * @return 结果
     */
    public int deletePriceRechargeById(Long id);

    /**
     * 批量删除充值列
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deletePriceRechargeByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
