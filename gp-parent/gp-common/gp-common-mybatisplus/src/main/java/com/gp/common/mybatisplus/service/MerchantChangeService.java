package com.gp.common.mybatisplus.service;

import java.util.List;
import com.gp.common.base.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import com.gp.common.mybatisplus.mapper.MerchantChangeMapper;
import com.gp.common.mybatisplus.entity.MerchantChange;
import com.gp.common.mybatisplus.service.MerchantChangeService;

/**
 * 商户账变Service业务层处理
 *
 * @author axing
 * @date 2024-08-09
 */
@Service
public class MerchantChangeService extends ServiceImpl<MerchantChangeMapper, MerchantChange>
{
    @Autowired
    private MerchantChangeMapper merchantChangeMapper;

    /**
     * 查询商户账变
     *
     * @param id 商户账变ID
     * @return 商户账变
     */

    public MerchantChange selectMerchantChangeById(Long id)
    {
        return merchantChangeMapper.selectMerchantChangeById(id);
    }

    /**
     * 查询商户账变列表
     *
     * @param param 商户账变
     * @return 商户账变
     */

    public List<MerchantChange> selectMerchantChangeList(MerchantChange param)
    {
        return merchantChangeMapper.selectMerchantChangeList(param);
    }

    /**
     * 新增商户账变
     *
     * @param param 商户账变
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertMerchantChange(MerchantChange param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改商户账变
     *
     * @param param 商户账变
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateMerchantChange(MerchantChange param)
    {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除商户账变
     *
     * @param ids 需要删除的商户账变ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteMerchantChangeByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除商户账变信息
     *
     * @param id 商户账变ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteMerchantChangeById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
