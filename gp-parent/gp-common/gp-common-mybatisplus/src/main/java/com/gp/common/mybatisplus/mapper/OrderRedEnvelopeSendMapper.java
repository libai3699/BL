package com.gp.common.mybatisplus.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.common.datasource.param.DataSource;
import com.gp.common.mybatisplus.entity.OrderRedEnvelopeSend;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 红包发送Mapper接口
 *
 * @author axing
 * @date 2024-12-25
 */
public interface OrderRedEnvelopeSendMapper extends BaseMapper<OrderRedEnvelopeSend>
{
    /**
     * 查询红包发送
     *
     * @param id 红包发送ID
     * @return 红包发送
     */
    public OrderRedEnvelopeSend selectOrderRedEnvelopeSendById(Long id);

    /**
     * 查询红包发送列表
     *
     * @param orderRedEnvelopeSend 红包发送
     * @return 红包发送集合
     */
    public List<OrderRedEnvelopeSend> selectOrderRedEnvelopeSendList(OrderRedEnvelopeSend orderRedEnvelopeSend);

    /**
     * 新增红包发送
     *
     * @param orderRedEnvelopeSend 红包发送
     * @return 结果
     */
    public int insertOrderRedEnvelopeSend(OrderRedEnvelopeSend orderRedEnvelopeSend);

    /**
     * 修改红包发送
     *
     * @param orderRedEnvelopeSend 红包发送
     * @return 结果
     */
    public int updateOrderRedEnvelopeSend(OrderRedEnvelopeSend orderRedEnvelopeSend);

    /**
     * 删除红包发送
     *
     * @param id 红包发送ID
     * @return 结果
     */
    public int deleteOrderRedEnvelopeSendById(Long id);

    /**
     * 批量删除红包发送
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteOrderRedEnvelopeSendByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    int updateByeReceive(@Param("id") Long id, @Param("amount") BigDecimal amount, @Param("signSecretKey") String signSecretKey, @Param("signTime") Long signTime);

    Integer updateByeReceiveNoMoney(@Param("id") Long id,  @Param("signSecretKey") String signSecretKey, @Param("signTime") Long signTime);


    Long selectOrderRedEnvelopeSendCount(OrderRedEnvelopeSend param);
}
