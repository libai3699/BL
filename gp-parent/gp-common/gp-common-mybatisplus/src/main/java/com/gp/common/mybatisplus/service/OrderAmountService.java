package com.gp.common.mybatisplus.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.MerchantPay;
import com.gp.common.mybatisplus.entity.OrderAmount;
import com.gp.common.mybatisplus.mapper.OrderAmountMapper;
import com.gp.common.mybatisplus.merchantpay.service.PayWService;
import com.gp.common.mybatisplus.merchantpay.service.RechargeService;
import com.gp.common.mybatisplus.param.MerchantPayParam;
import com.gp.common.mybatisplus.pay.mpay.order.PayCallbackResult;
import com.gp.common.mybatisplus.until.OrderAmountSign;
import com.gp.common.mybatisplus.vo.MerchantPayStatVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderAmountService extends ServiceImpl<OrderAmountMapper, OrderAmount> {
    @Autowired
    private OrderAmountMapper tOrderAmountMapper;
    @Autowired
    private OrderAmountSign orderAmountSign;
    @Resource
    private ChannelService channelService;
    @Resource
    private ShareholderService shareholderService;
    @Resource
    private MerchantPayService merchantPayService;
    @Resource
    private RechargeService rechargeService;
    /**
     * 查询充值订单
     *
     * @param id 充值订单ID
     * @return 充值订单
     */
    public OrderAmount selectOrderAmountById(Long id) {
        return tOrderAmountMapper.selectOrderAmountById(id);
    }

    /**
     * 查询充值订单列表
     *
     * @param tOrderAmount 充值订单
     * @return 充值订单
     */
    public List<OrderAmount> selectOrderAmountList(OrderAmount tOrderAmount) {

        return tOrderAmountMapper.selectOrderAmountList(tOrderAmount);
    }


    /**
     * 查询充值订单数量
     *
     * @param tOrderAmount 充值订单
     * @return 充值订单数量
     */
    public long selectOrderAmountListCount(OrderAmount tOrderAmount) {
        return tOrderAmountMapper.selectOrderAmountListCount(tOrderAmount);
    }

    /**
     * 新增充值订单
     *
     * @param tOrderAmount 充值订单
     * @return 结果
     */
    public Boolean insertOrderAmount(OrderAmount tOrderAmount) {
        tOrderAmount.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(tOrderAmount);
        return result;
    }

    /**
     * 修改充值订单
     *
     * @param tOrderAmount 充值订单
     * @return 结果
     */
    public Boolean updateOrderAmount(OrderAmount tOrderAmount) {
        boolean result = this.updateById(tOrderAmount);
        return result;
    }

    /**
     * 批量删除充值订单
     *
     * @param ids 需要删除的充值订单ID
     * @return 结果
     */
    public Boolean deleteOrderAmountByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除充值订单信息
     *
     * @param id 充值订单ID
     * @return 结果
     */
    public Boolean deleteOrderAmountById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    private List<OrderAmount> queryNoSign(Integer start, Integer limit) {
        return this.tOrderAmountMapper.queryNoSign(start, limit);
    }

    public void dealNoSign() {
        log.info("开始-OrderFlash,{}", new Date());
        //一次查多少条
        Integer start = 0;
        //一次查多少条
        Integer limit = 1000;
        List<OrderAmount> contents = this.queryNoSign(start, limit);
        dealSignContent(contents, start, limit);

        log.info("结束-OrderFlash,{}", new Date());
    }

    private void dealSignContent(List<OrderAmount> contents, Integer start, Integer limit) {
        List<List<OrderAmount>> split = CollUtil.split(contents, 100);
        for (List<OrderAmount> amountChangeList : split) {
            List<OrderAmount> collect = amountChangeList.stream().map(e -> {
                orderAmountSign.dealSign(e);
                return e;
            }).collect(Collectors.toList());
            this.updateBatchById(collect);
        }
        //结束之后取给他 start 加 size
        start = start + contents.size();
        log.info("执行中: {} -- 执行的start !", start);

        contents = this.queryNoSign(start, limit);
        if (CollectionUtil.isEmpty(contents)) {
            return;  // 列表为空则直接返回，避免索引越界异常
        }
        log.info("执行中: {} -- 最后一条 !", contents.get(contents.size() - 1));
        dealSignContent(contents, start, limit);
    }

    public List<OrderAmount> selectOrderAmountListByUser(Long channelId, Integer id, String startTime, String endTime) {
        return this.tOrderAmountMapper.selectOrderAmountListByUser(channelId, id, startTime, endTime);
    }

    public Map<String, Object> selectOrderAmountCount(Long channelId, Integer id, String startTime, String endTime) {
        return this.tOrderAmountMapper.selectOrderAmountCount(channelId, id, startTime, endTime);
    }

    public List<OrderAmount> selectOrderAmountListBychannelId(Long channelId, Integer currencyId, String startTime, String endTime) {
        return this.tOrderAmountMapper.selectOrdzerAmountListBychannelId(channelId, currencyId, startTime, endTime);
    }

    /**
     * 充值订单sql统计
     *
     * @param startTime   创建时间-开始
     * @param endTime     创建时间-结束
     * @param currencyId  币种ID
     * @param orderStatus 订单状态(0 待支付, 1 已支付, 2 已取消)
     * @param userId      用户ID
     * @return
     */
    public Map<String, Object> orderAmountCountMap(String startTime, String endTime, Integer currencyId, Integer orderStatus, Long userId) {

        return tOrderAmountMapper.orderAmountCountMap(startTime, endTime, currencyId, orderStatus, userId);
    }

    public Map<String, Object> selectOrderAmountMapCount(Integer orderStatus, Integer id, String startTime, String endTime) {
        return this.tOrderAmountMapper.selectOrderAmountMapCount(orderStatus, id, startTime, endTime);
    }

    public Map<String, Object> summaryCount(OrderAmount tOrderAmount) {
        return tOrderAmountMapper.summaryCount(tOrderAmount);
    }

    public List<MerchantPayStatVO> payMerchantList(MerchantPayParam param) {
        return tOrderAmountMapper.payMerchantList(param);
    }

    public List<MerchantPayStatVO> payMerchantChannelList(MerchantPayParam param) {
        return tOrderAmountMapper.payMerchantChannelList(param);
    }

    public List<MerchantPayStatVO> payTypeWithdrawOne(MerchantPayParam param) {
        return tOrderAmountMapper.payTypeWithdrawOne(param);
    }



    public Object checkStatus(Long id) {
        OrderAmount orderAmount = this.getById(id);
        //查询商户
        MerchantPay merchantPay = merchantPayService.getById(orderAmount.getPayMerchantId());

        PayWService service = PayWService.getPayService(merchantPay.getCode());

        // 调用 PayWService 验证回调，传入订单号
        PayCallbackResult result = service.callbackPay(merchantPay, service.callbackPayParam(merchantPay, orderAmount));

        // 如果验证成功，则处理充值逻辑
        if (result.isSuccess()) {
            String finalOrderNo = result.getOrderNo();
            String upOrderNo = result.getMerchantOrderNo();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", "后台查询");
            String processResult = rechargeService.processRechargeCallback(finalOrderNo, upOrderNo, jsonObject,
                    merchantPay.getName());
            if ("success".equals(processResult)) {
                return result.getResponseMsg();
            }
            return processResult;
        }

        return "success";
    }
}
