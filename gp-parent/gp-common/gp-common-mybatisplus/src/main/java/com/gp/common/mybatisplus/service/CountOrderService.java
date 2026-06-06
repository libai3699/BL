package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.CountOrder;
import com.gp.common.mybatisplus.mapper.CountOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 每日订单统计Service业务层处理
 *
 * @author axing
 * @date 2024-05-09
 */
@Service
public class CountOrderService extends ServiceImpl<CountOrderMapper, CountOrder> {
    @Autowired
    private CountOrderMapper countOrderMapper;

    /**
     * 查询每日订单统计
     *
     * @param id 每日订单统计ID
     * @return 每日订单统计
     */

    public CountOrder selectCountOrderById(Long id) {
        return countOrderMapper.selectCountOrderById(id);
    }

    /**
     * 查询每日订单统计列表
     *
     * @param param 每日订单统计
     * @return 每日订单统计
     */

    public List<CountOrder> selectCountOrderList(CountOrder param) {
        return countOrderMapper.selectCountOrderList(param);
    }

    /**
     * 新增每日订单统计
     *
     * @param param 每日订单统计
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertCountOrder(CountOrder param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改每日订单统计
     *
     * @param param 每日订单统计
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateCountOrder(CountOrder param) {
        param.setUpdateTime(DateUtils.getNowDate());
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除每日订单统计
     *
     * @param ids 需要删除的每日订单统计ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteCountOrderByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除每日订单统计信息
     *
     * @param id 每日订单统计ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteCountOrderById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    public Map<String, BigDecimal> getTotalBetAmount() {

        return countOrderMapper.getTotalBetAmount();
    }
}
