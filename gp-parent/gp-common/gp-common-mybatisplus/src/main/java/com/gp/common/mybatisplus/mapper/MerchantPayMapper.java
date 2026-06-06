package com.gp.common.mybatisplus.mapper;

import java.util.List;

import com.gp.common.base.annotation.PassUrl;
import com.gp.common.mybatisplus.entity.MerchantPay;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商户Mapper接口
 *
 * @author axing
 * @date 2024-03-11
 */
public interface MerchantPayMapper extends BaseMapper<MerchantPay>
{
    /**
     * 查询商户
     *
     * @param id 商户ID
     * @return 商户
     */
    public MerchantPay selectMerchantPayById(Long id);

    /**
     * 查询商户列表
     *
     * @param merchantPay 商户
     * @return 商户集合
     */
    public List<MerchantPay> selectMerchantPayList(MerchantPay merchantPay);


    /**
     * 删除商户
     *
     * @param id 商户ID
     * @return 结果
     */
    public int deleteMerchantPayById(Long id);

    /**
     * 批量删除商户
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteMerchantPayByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    MerchantPay getByAppId(@Param("appId") String appId,@Param("code") String code);
}
