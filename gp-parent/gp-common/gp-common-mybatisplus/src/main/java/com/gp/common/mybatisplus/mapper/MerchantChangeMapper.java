package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.MerchantChange;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 商户账变Mapper接口
 *
 * @author axing
 * @date 2024-08-09
 */
public interface MerchantChangeMapper extends BaseMapper<MerchantChange>
{
    /**
     * 查询商户账变
     *
     * @param id 商户账变ID
     * @return 商户账变
     */
    public MerchantChange selectMerchantChangeById(Long id);

    /**
     * 查询商户账变列表
     *
     * @param merchantChange 商户账变
     * @return 商户账变集合
     */
    public List<MerchantChange> selectMerchantChangeList(MerchantChange merchantChange);

    /**
     * 新增商户账变
     *
     * @param merchantChange 商户账变
     * @return 结果
     */
    public int insertMerchantChange(MerchantChange merchantChange);

    /**
     * 修改商户账变
     *
     * @param merchantChange 商户账变
     * @return 结果
     */
    public int updateMerchantChange(MerchantChange merchantChange);

    /**
     * 删除商户账变
     *
     * @param id 商户账变ID
     * @return 结果
     */
    public int deleteMerchantChangeById(Long id);

    /**
     * 批量删除商户账变
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteMerchantChangeByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
