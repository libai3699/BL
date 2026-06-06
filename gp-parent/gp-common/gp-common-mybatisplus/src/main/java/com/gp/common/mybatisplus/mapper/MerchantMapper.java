package com.gp.common.mybatisplus.mapper;

import java.math.BigDecimal;
import java.util.List;

import com.gp.common.mybatisplus.entity.Merchant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商户Mapper接口
 *
 * @author axing
 * @date 2024-08-09
 */
public interface MerchantMapper extends BaseMapper<Merchant> {
    /**
     * 查询商户
     *
     * @param merchantId 商户ID
     * @return 商户
     */
    public Merchant selectMerchantById(Long merchantId);

    /**
     * 查询商户列表
     *
     * @param merchant 商户
     * @return 商户集合
     */
    public List<Merchant> selectMerchantList(Merchant merchant);

    /**
     * 新增商户
     *
     * @param merchant 商户
     * @return 结果
     */
    public int insertMerchant(Merchant merchant);

    /**
     * 修改商户
     *
     * @param merchant 商户
     * @return 结果
     */
    public int updateMerchant(Merchant merchant);

    /**
     * 删除商户
     *
     * @param merchantId 商户ID
     * @return 结果
     */
    public int deleteMerchantById(Long merchantId);

    /**
     * 批量删除商户
     *
     * @param merchantIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteMerchantByIds(Long[] merchantIds);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    int upScoreByMerchantId(@Param("merchantId") Long merchantId, @Param("amount") BigDecimal amount,
                            @Param("signSecretKey") String signSecretKey, @Param("signTime") Long signTime);
}
