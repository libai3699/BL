package com.gp.common.mybatisplus.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.util.StringUtils;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.enums.CodeEnum;
import com.gp.common.base.exception.BusinessException;
import com.gp.common.base.utils.BeanUtils;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.mybatisplus.entity.MerchantPay;
import com.gp.common.mybatisplus.entity.OrderLawWithdraw;
import com.gp.common.mybatisplus.mapper.OrderLawWithdrawMapper;
import com.gp.common.mybatisplus.merchantpay.service.PayWService;
import com.gp.common.mybatisplus.merchantpay.service.WithdrawService;
import com.gp.common.mybatisplus.pay.mpay.order.PayCallbackResult;
import com.gp.common.mybatisplus.until.OrderLawWithdrawSign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 法币提现订单Service业务层处理
 *
 * @author axing
 * @date 2024-06-29
 */
@Service
public class OrderLawWithdrawService extends ServiceImpl<OrderLawWithdrawMapper, OrderLawWithdraw> {
    @Autowired
    private OrderLawWithdrawMapper orderLawWithdrawMapper;

    @Resource
    private OrderLawWithdrawSign orderLawWithdrawSign;

    @Resource
    private ShareholderService shareholderService;

    @Resource
    private ChannelService channelService;

    @Resource
    private UserService userService;
    @Resource
    private MerchantPayService merchantPayService;
    @Resource
    private WithdrawService withdrawService;

    /**
     * 查询法币提现订单
     *
     * @param id 法币提现订单ID
     * @return 法币提现订单
     */

    public OrderLawWithdraw selectOrderLawWithdrawById(Long id) {
        return orderLawWithdrawMapper.selectOrderLawWithdrawById(id);
    }

    /**
     * 查询法币提现订单列表
     *
     * @param param 法币提现订单
     * @return 法币提现订单
     */

    public List<OrderLawWithdraw> selectOrderLawWithdrawList(OrderLawWithdraw param) {
        List<OrderLawWithdraw> list = orderLawWithdrawMapper.selectOrderLawWithdrawList(param);
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(e -> {
                e.setFlag(orderLawWithdrawSign.checkSignFlag(e));
            });
        }

        return list;
    }

    /**
     * 与 {@link #selectOrderLawWithdrawList(OrderLawWithdraw)} 同 WHERE 的全量汇总，供列表合计行（须在分页前调用）
     */
    public Map<String, Object> getTodayTotalBetAmount(OrderLawWithdraw param) {
        return orderLawWithdrawMapper.getTodayTotalBetAmount(param);
    }

    /**
     * 新增法币提现订单
     *
     * @param param 法币提现订单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertOrderLawWithdraw(OrderLawWithdraw param) {
        param.setCreateTime(DateUtils.getNowDate());
        orderLawWithdrawSign.dealSign(param);
        return this.save(param);
    }

    /**
     * 修改法币提现订单
     *
     * @param param 法币提现订单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOrderLawWithdrawV2(OrderLawWithdraw param) {
        OrderLawWithdraw pojo = this.selectOrderLawWithdrawById(param.getId());
        if (Objects.isNull(pojo)) {
            throw new BusinessException(
                    StringUtils.format(MessagesUtils.get("common.param.is.error")
                            , "id", param.getId())
            );
        }
        BeanUtils.copyPropertiesIgnoreNull(param, pojo);
        orderLawWithdrawSign.dealSign(pojo);
        return this.updateById(pojo);
    }

    /**
     * 修改法币提现订单
     *
     * @param param 法币提现订单
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOrderLawWithdraw(OrderLawWithdraw param) {
        return this.updateById(param);
    }

    /**
     * 批量删除法币提现订单
     *
     * @param ids 需要删除的法币提现订单ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrderLawWithdrawByIds(Long[] ids) {
        return this.removeByIds(Arrays.asList(ids));
    }

    /**
     * 删除法币提现订单信息
     *
     * @param id 法币提现订单ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrderLawWithdrawById(Long id) {
        return this.removeById(id);
    }

    public List<OrderLawWithdraw> selectOrderLawWithdrawListByUser(Long channelId, Integer id, String startTime, String endTime) {
        return orderLawWithdrawMapper.selectOrderLawWithdrawListByUser(channelId, id, startTime, endTime);
    }

    public Map<String, Object> selectOrderLawWithdrawMapByUser(Long channelId, Integer id, String startTime, String endTime) {
        return orderLawWithdrawMapper.selectOrderLawWithdrawMapByUser(channelId, id, startTime, endTime);
    }

    public List<OrderLawWithdraw> selectOrderLawWithdrawListBychannelId(Long channelId, Integer currencyId, String startTime, String endTime) {
        return orderLawWithdrawMapper.selectOrderLawWithdrawListBychannelId(channelId, currencyId, startTime, endTime);
    }

    /**
     * 充值订单sql统计
     *
     * @param startTime   创建时间-开始
     * @param endTime     创建时间-结束
     * @param currencyId  币种ID
     * @param orderStatus 订单状态(0 待审核, 1 风险审核通过, 2 财务通过, 3 已拒绝, 4 提现成功, 5 提现失败, 6 上游下单成功, 7 上游下单失败
     * @param userId      用户ID
     * @return
     */
    public Map<String, Object> orderLawWithdrawCountMap(String startTime, String endTime, Integer currencyId, Long userId, Integer orderStatus) {
        return orderLawWithdrawMapper.orderLawWithdrawCountMap(startTime, endTime, currencyId, userId, orderStatus);
    }

    public Object checkStatus(Long id) {
        OrderLawWithdraw orderLawWithdraw = this.getById(id);
        //查询商户
        MerchantPay merchantPay = merchantPayService.getById(orderLawWithdraw.getPayMerchantId());

        PayWService service = PayWService.getPayService(merchantPay.getCode());

        // 调用 PayWService 验证回调
        PayCallbackResult result = service.callbackWithdraw(merchantPay, service.callbackWithdrawParam(merchantPay, orderLawWithdraw));

        // 如果验证成功，则调用业务层处理提现单
        if (result != null && result.isSuccess()) {
            JSONObject notifyData = new JSONObject();
            notifyData.put("msg", "后台查询");
            String processResult = withdrawService.processWithdrawCallback(result.getOrderNo(),
                    result.getMerchantOrderNo(),
                    result.getOrderStatus(), notifyData, CecuUtil.getDbCode());
            if ("success".equals(processResult)) {
                return result.getResponseMsg();
            }
            return processResult;
        }
        return "success";
    }



    /**
     * 无回调通道专用：上游同步返回成功即视为转账成功，直接推终态为"提现成功"。
     * 仅供 NoCallbackWithdrawChannels 注册过的通道使用（上游无查单接口 / 无异步回调）。
     * processWithdrawCallback 自带幂等 + 分布式锁，若未来上游补发回调也不会重复处理。
     */
    public String markSyncSuccess(Long id) {
        OrderLawWithdraw orderLawWithdraw = this.getById(id);
        JSONObject notifyData = new JSONObject();
        notifyData.put("msg", "无回调通道 - 下单同步成功即终态");
        notifyData.put("source", "sync_no_callback");
        return withdrawService.processWithdrawCallback(
                orderLawWithdraw.getOrderNo(),
                orderLawWithdraw.getUpOrderNo(),
                1, // 1=成功
                notifyData,
                CecuUtil.getDbCode());
    }

    public Object manualStatus(Long id, Integer status) {
        OrderLawWithdraw orderLawWithdraw = this.getById(id);
        //只有订单状态为6-上游下单成功才能操作
        if (orderLawWithdraw.getOrderStatus() != 6) {
            throw new BusinessException(CodeEnum.Error.getCode(), "上游未下单成功，不能操作");
        }
        JSONObject notifyData = new JSONObject();
        notifyData.put("msg", "手动状态处理");
        return withdrawService.processWithdrawCallback(orderLawWithdraw.getOrderNo(),
                orderLawWithdraw.getUpOrderNo(),
                status, notifyData, CecuUtil.getDbCode());
    }
}
