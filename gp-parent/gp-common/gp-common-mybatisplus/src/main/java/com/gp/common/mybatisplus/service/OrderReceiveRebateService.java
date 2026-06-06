package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.Currency;
import com.gp.common.mybatisplus.entity.OrderReceiveRebate;
import com.gp.common.mybatisplus.mapper.OrderReceiveRebateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 领取返水返佣记录Service业务层处理
 *
 * @author axing
 * @date 2024-05-17
 */
@Service
public class OrderReceiveRebateService extends ServiceImpl<OrderReceiveRebateMapper, OrderReceiveRebate>
{
    @Autowired
    private OrderReceiveRebateMapper orderReceiveRebateMapper;

    @Resource
    private CurrencyService currencyService;

    /**
     * 查询领取返水返佣记录
     *
     * @param id 领取返水返佣记录ID
     * @return 领取返水返佣记录
     */

    public OrderReceiveRebate selectOrderReceiveRebateById(Long id)
    {
        return orderReceiveRebateMapper.selectOrderReceiveRebateById(id);
    }

    /**
     * 查询领取返水返佣记录列表
     *
     * @param param 领取返水返佣记录
     * @return 领取返水返佣记录
     */

    public List<OrderReceiveRebate> selectOrderReceiveRebateList(OrderReceiveRebate param)
    {
        List<OrderReceiveRebate> list = orderReceiveRebateMapper.selectOrderReceiveRebateList(param);
        if( list.size() == 0 ){
            return list;
        }
        List<Currency> currencyList = currencyService.list();
        Map<Integer, Currency> currencyMap = currencyList.stream()
                .collect(Collectors.toMap(Currency::getId, Function.identity()));
        for (OrderReceiveRebate it : list) {
            Currency currency = currencyMap.get(it.getCurrencyId());
            if(Objects.nonNull(currency)){
                it.setCurrencyName(currency.getItemName()+"-"+currency.getChainTag());
            }
        }
        return list;
    }
    
    /**
     * 查询领取返水返佣记录列表数量
     *
     * @param param 领取返水返佣记录
     * @return 领取返水返佣记录数量
     */
    public int selectOrderReceiveRebateListCount(OrderReceiveRebate param)
    {
        return orderReceiveRebateMapper.selectOrderReceiveRebateListCount(param);
    }

    /**
     * 新增领取返水返佣记录
     *
     * @param param 领取返水返佣记录
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertOrderReceiveRebate(OrderReceiveRebate param)
    {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改领取返水返佣记录
     *
     * @param param 领取返水返佣记录
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOrderReceiveRebate(OrderReceiveRebate param)
    {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除领取返水返佣记录
     *
     * @param ids 需要删除的领取返水返佣记录ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrderReceiveRebateByIds(Long[] ids)
    {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除领取返水返佣记录信息
     *
     * @param id 领取返水返佣记录ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrderReceiveRebateById(Long id)
    {
        boolean result = this.removeById(id);
        return result;
    }
}
