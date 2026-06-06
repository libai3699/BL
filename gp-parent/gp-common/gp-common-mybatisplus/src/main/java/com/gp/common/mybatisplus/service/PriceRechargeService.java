package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.util.StringUtils;
import com.gp.common.base.constant.DelConstants;
import com.gp.common.base.exception.BusinessException;
import com.gp.common.base.utils.BeanUtils;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.mybatisplus.entity.PriceRecharge;
import com.gp.common.mybatisplus.mapper.PriceRechargeMapper;
import com.gp.common.mybatisplus.until.PriceRechargeSign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 充值列Service业务层处理
 *
 * @author axing
 * @date 2024-05-14
 */
@Service
public class PriceRechargeService extends ServiceImpl<PriceRechargeMapper, PriceRecharge> {
    @Autowired
    private PriceRechargeMapper priceRechargeMapper;
    @Autowired
    private PriceRechargeSign priceRechargeSign;

    /**
     * 查询充值列
     *
     * @param id 充值列ID
     * @return 充值列
     */

    public PriceRecharge selectPriceRechargeById(Long id) {
        return priceRechargeMapper.selectPriceRechargeById(id);
    }

    /**
     * 查询充值列列表
     *
     * @param param 充值列
     * @return 充值列
     */

    public List<PriceRecharge> selectPriceRechargeList(PriceRecharge param) {
        return priceRechargeMapper.selectPriceRechargeList(param);
    }

    /**
     * 查询充值列数量
     *
     * @param param 充值列
     * @return 充值列数量
     */
    public long selectPriceRechargeCount(PriceRecharge param) {
        return priceRechargeMapper.selectPriceRechargeCount(param);
    }

    /**
     * 新增充值列
     *
     * @param param 充值列
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertPriceRecharge(PriceRecharge param) {
        param.setCreateTime(DateUtils.getNowDate());
        priceRechargeSign.dealSign(param);
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改充值列
     *
     * @param param 充值列
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePriceRecharge(PriceRecharge param) {

        PriceRecharge pojo = this.selectPriceRechargeById(param.getId());
        if (Objects.isNull(pojo)) {
            throw new BusinessException(
                    StringUtils.format(MessagesUtils.get("common.param.is.error")
                            , "id", param.getId())
            );
        }
        BeanUtils.copyPropertiesIgnoreNull(param, pojo);
        priceRechargeSign.dealSign(pojo);
        boolean result = this.updateById(pojo);
        return result;
    }

    /**
     * 批量删除充值列
     *
     * @param ids 需要删除的充值列ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePriceRechargeByIds(Long[] ids) {
        List<PriceRecharge> list = new ArrayList<>();
        for (Long id : ids) {
            PriceRecharge priceRecharge = new PriceRecharge();
            priceRecharge.setHasDel(DelConstants.del);
            priceRecharge.setId(id);
            list.add(priceRecharge);
        }
        boolean result = this.updateBatchById(list);
        return result;
    }

    /**
     * 删除充值列信息
     *
     * @param id 充值列ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePriceRechargeById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }
}
