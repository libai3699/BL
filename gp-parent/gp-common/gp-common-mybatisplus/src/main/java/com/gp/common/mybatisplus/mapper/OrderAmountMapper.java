package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.OrderAmount;
import com.gp.common.mybatisplus.param.MerchantPayParam;
import com.gp.common.mybatisplus.vo.MerchantPayStatVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OrderAmountMapper extends BaseMapper<OrderAmount> {

    /**
     * 查询充值订单
     *
     * @param id 充值订单ID
     * @return 充值订单
     */

    public OrderAmount selectOrderAmountById(Long id);

    /**
     * 查询充值订单列表
     *
     * @param tOrderAmount 充值订单
     * @return 充值订单集合
     */

    public List<OrderAmount> selectOrderAmountList(OrderAmount tOrderAmount);

    /**
     * 查询充值订单数量
     *
     * @param tOrderAmount 充值订单
     * @return 充值订单数量
     */

    public long selectOrderAmountListCount(OrderAmount tOrderAmount);

    /**
     * 新增充值订单
     *
     * @param tOrderAmount 充值订单
     * @return 结果
     */
    public int insertOrderAmount(OrderAmount tOrderAmount);

    /**
     * 修改充值订单
     *
     * @param tOrderAmount 充值订单
     * @return 结果
     */
    public int updateOrderAmount(OrderAmount tOrderAmount);

    /**
     * 删除充值订单
     *
     * @param id 充值订单ID
     * @return 结果
     */
    public int deleteOrderAmountById(Long id);

    /**
     * 批量删除充值订单
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteOrderAmountByIds(Long[] ids);


    List<OrderAmount> queryNoSign(@Param("start") Integer start, @Param("limit") Integer limit);


    List<OrderAmount> selectOrderAmountListByUser(@Param("channelId") Long channelId, @Param("id") Integer id, @Param("startTime") String startTime, @Param("endTime") String endTime);


    List<OrderAmount> selectOrdzerAmountListBychannelId(@Param("channelId") Long channelId, @Param("currencyId") Integer currencyId, @Param("startTime") String startTime,
                                                        @Param("endTime") String endTime);

    Map<String, Object> selectOrderAmountCount(@Param("channelId") Long channelId, @Param("id") Integer id, @Param("startTime") String startTime, @Param("endTime") String endTime);


    Map<String, Object> orderAmountCountMap(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("currencyId") Integer currencyId, @Param("orderStatus") Integer orderStatus,
                                            @Param("userId") Long userId);
    Map<String, Object> selectOrderAmountMapCount(@Param("orderStatus") Integer orderStatus, @Param("id") Integer id, @Param("startTime") String startTime, @Param("endTime") String endTime);


    Map<String, Object> summaryCount(OrderAmount tOrderAmount);

    int countBefore5Min(Long userId);

    List<Map<String, Object>> countOrder(Long userId);

    List<MerchantPayStatVO> payMerchantList(MerchantPayParam param);

    List<MerchantPayStatVO> payMerchantChannelList(MerchantPayParam param);

    List<MerchantPayStatVO> payTypeWithdrawOne(MerchantPayParam param);
}
