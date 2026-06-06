package com.gp.common.mybatisplus.mapper;

import java.math.BigDecimal;
import java.util.List;
import com.gp.common.mybatisplus.entity.PayChannelMoney;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 【请填写功能名称】Mapper接口
 *
 * @author axing
 * @date 2026-01-09
 */
public interface PayChannelMoneyMapper extends BaseMapper<PayChannelMoney>
{
    /**
     * 查询【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public PayChannelMoney selectPayChannelMoneyById(Integer id);

    /**
     * 查询【请填写功能名称】列表
     *
     * @param payChannelMoney 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<PayChannelMoney> selectPayChannelMoneyList(PayChannelMoney payChannelMoney);

    /**
     * 新增【请填写功能名称】
     *
     * @param payChannelMoney 【请填写功能名称】
     * @return 结果
     */
    public int insertPayChannelMoney(PayChannelMoney payChannelMoney);

    /**
     * 修改【请填写功能名称】
     *
     * @param payChannelMoney 【请填写功能名称】
     * @return 结果
     */
    public int updatePayChannelMoney(PayChannelMoney payChannelMoney);

    /**
     * 删除【请填写功能名称】
     *
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deletePayChannelMoneyById(Integer id);

    /**
     * 批量删除【请填写功能名称】
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deletePayChannelMoneyByIds(Integer[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();
    List<Long> selectMoney(@Param("typeCode") String typeCode, @Param("vip") Integer vip);

    Integer randomChannelId(@Param("typeCode") String typeCode, @Param("money") BigDecimal money,
                            @Param("vip") Integer vip);

    Integer maxRateChannel(@Param("typeCode") String typeCode, @Param("money") BigDecimal money,
                           @Param("vip") Integer vip);

    Integer minRateChannel(@Param("typeCode") String typeCode, @Param("money") BigDecimal money,
                           @Param("vip") Integer vip);

    /**
     * 自定义金额兜底：按支付类型 + 金额区间从 t_pay_channel 匹配通道（费率最低）
     */
    Integer minRateChannelByAmount(@Param("typeCode") String typeCode, @Param("money") BigDecimal money,
                                   @Param("vip") Integer vip);

    /**
     * 自定义金额兜底：按支付类型 + 金额区间从 t_pay_channel 匹配通道（随机）
     */
    Integer randomChannelIdByAmount(@Param("typeCode") String typeCode, @Param("money") BigDecimal money,
                                    @Param("vip") Integer vip);

    /**
     * 自定义金额兜底：按支付类型 + 金额区间从 t_pay_channel 匹配通道（费率最高）
     */
    Integer maxRateChannelByAmount(@Param("typeCode") String typeCode, @Param("money") BigDecimal money,
                                   @Param("vip") Integer vip);
}
