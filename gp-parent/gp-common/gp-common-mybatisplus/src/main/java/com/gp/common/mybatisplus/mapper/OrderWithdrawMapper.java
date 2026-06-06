package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.OrderWithdraw;
import com.gp.common.mybatisplus.param.MerchantPayParam;
import com.gp.common.mybatisplus.vo.MerchantPayVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderWithdrawMapper extends BaseMapper<OrderWithdraw> {

    Map<String, Object> countWithdraw(
            @Param("startTime") String startTime, @Param("endTime") String endTime
            , @Param("currencyId") Integer currencyId, @Param("userId") Long userId,
            @Param("orderStatus") Integer orderStatus);

    Map<String, Object> countWithdrawByUserIdList(
            @Param("startTime") String startTime, @Param("endTime") String endTime
            , @Param("currencyId") Integer currencyId, @Param("channelId") Long channelId
    );

    /**
     * 查询提现订单
     *
     * @param id 提现订单ID
     * @return 提现订单
     */
    public OrderWithdraw selectOrderWithdrawById(Long id);

    /**
     * 查询提现订单列表
     *
     * @param orderWithdraw 提现订单
     * @return 提现订单集合
     */
    public List<OrderWithdraw> selectOrderWithdrawList(OrderWithdraw orderWithdraw);

    /**
     * 查询提现订单数量
     *
     * @param orderWithdraw 提现订单
     * @return 提现订单数量
     */
    public long selectOrderWithdrawCount(OrderWithdraw orderWithdraw);

    /**
     * 新增提现订单
     *
     * @param orderWithdraw 提现订单
     * @return 结果
     */
    public Boolean insertOrderWithdraw(OrderWithdraw orderWithdraw);

    /**
     * 修改提现订单
     *
     * @param orderWithdraw 提现订单
     * @return 结果
     */
    public Boolean updateOrderWithdraw(OrderWithdraw orderWithdraw);

    /**
     * 批量删除提现订单
     *
     * @param ids 需要删除的提现订单ID
     * @return 结果
     */
    public Boolean deleteOrderWithdrawByIds(Long[] ids);

    /**
     * 删除提现订单信息
     *
     * @param id 提现订单ID
     * @return 结果
     */
    public Boolean deleteOrderWithdrawById(Long id);

    List<OrderWithdraw> queryNoSign(@Param("start") Integer start, @Param("limit") Integer limit);

    List<OrderWithdraw> selectOrderWithdrawListBrchannelId(@Param("channelId") Long channelId, @Param("currencyId") Integer currencyId, @Param(
            "startTime") String startTime,
                                                           @Param("endTime") String endTime);

    Map<String, Object> summaryCount(OrderWithdraw tOrderWithdraw);

    List<MerchantPayVo> summary(MerchantPayParam param);

    List<MerchantPayVo> payChannelList(MerchantPayParam param);

    List<MerchantPayVo> payTypeWithdrawOne(MerchantPayParam param);

    List<MerchantPayVo> payTypeWithdrawTwo(MerchantPayParam param);
}
