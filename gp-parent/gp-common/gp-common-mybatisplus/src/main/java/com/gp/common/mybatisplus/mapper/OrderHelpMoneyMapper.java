package com.gp.common.mybatisplus.mapper;

import java.util.List;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.common.datasource.param.DataSource;
import com.gp.common.mybatisplus.entity.OrderHelpMoney;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.OrderHelpMoneySum;
import com.gp.common.mybatisplus.mq.HelpMoneyEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 用户救济金申领Mapper接口
 *
 * @author axing
 * @date 2024-08-30
 */
public interface OrderHelpMoneyMapper extends BaseMapper<OrderHelpMoney>
{
    /**
     * 查询用户救济金申领
     *
     * @param id 用户救济金申领ID
     * @return 用户救济金申领
     */

    public OrderHelpMoney selectOrderHelpMoneyById(Integer id);

    /**
     * 查询用户救济金申领列表
     *
     * @param orderHelpMoney 用户救济金申领
     * @return 用户救济金申领集合
     */

    public List<OrderHelpMoney> selectOrderHelpMoneyList(OrderHelpMoney orderHelpMoney);

    /**
     * 新增用户救济金申领
     *
     * @param orderHelpMoney 用户救济金申领
     * @return 结果
     */
    public int insertOrderHelpMoney(OrderHelpMoney orderHelpMoney);

    /**
     * 修改用户救济金申领
     *
     * @param orderHelpMoney 用户救济金申领
     * @return 结果
     */
    public int updateOrderHelpMoney(OrderHelpMoney orderHelpMoney);

    /**
     * 删除用户救济金申领
     *
     * @param id 用户救济金申领ID
     * @return 结果
     */
    public int deleteOrderHelpMoneyById(Integer id);

    /**
     * 批量删除用户救济金申领
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteOrderHelpMoneyByIds(Integer[] ids);



    void insertHelpMoney(HelpMoneyEntity helpMoneyEntity);

    void upsertOrderHelpMoneyByCount(HelpMoneyEntity helpMoneyEntity);

    List<OrderHelpMoneySum> querySumByDate(@Param("dayStr") String dayStr);
}
