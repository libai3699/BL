package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.exception.ConfigPriceException;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.mybatisplus.entity.OrderRedEnvelopeSend;
import com.gp.common.mybatisplus.mapper.OrderRedEnvelopeSendMapper;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import com.gp.common.mybatisplus.until.OrderRedEnvelopeSendSign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * 红包发送Service业务层处理
 *
 * @author axing
 * @date 2024-12-25
 */
@Service
public class OrderRedEnvelopeSendService extends ServiceImpl<OrderRedEnvelopeSendMapper, OrderRedEnvelopeSend> {
    @Autowired
    private OrderRedEnvelopeSendMapper orderRedEnvelopeSendMapper;
    @Autowired
    private OrderRedEnvelopeSendSign orderRedEnvelopeSendSign;
    @Autowired
    private MNacosParam mNacosParam;

    /**
     * 查询红包发送
     *
     * @param id 红包发送ID
     * @return 红包发送
     */

    public OrderRedEnvelopeSend selectOrderRedEnvelopeSendById(Long id) {
        return orderRedEnvelopeSendMapper.selectOrderRedEnvelopeSendById(id);
    }

    /**
     * 查询红包发送列表
     *
     * @param param 红包发送
     * @return 红包发送
     */

    public List<OrderRedEnvelopeSend> selectOrderRedEnvelopeSendList(OrderRedEnvelopeSend param) {
        return orderRedEnvelopeSendMapper.selectOrderRedEnvelopeSendList(param);
    }

    /**
     * 新增红包发送
     *
     * @param param 红包发送
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertOrderRedEnvelopeSend(OrderRedEnvelopeSend param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改红包发送
     *
     * @param param 红包发送
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOrderRedEnvelopeSend(OrderRedEnvelopeSend param) {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除红包发送
     *
     * @param ids 需要删除的红包发送ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrderRedEnvelopeSendByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除红包发送信息
     *
     * @param id 红包发送ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrderRedEnvelopeSendById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    public void checkRedEnvelope(OrderRedEnvelopeSend orderRedEnvelopeSend, BigDecimal amount) {
        orderRedEnvelopeSendSign.checkSign(orderRedEnvelopeSend);
        //校验钱
        BigDecimal lastAmount = orderRedEnvelopeSend.getLastAmount();
        if (amount.compareTo(lastAmount) > 0 || lastAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new ConfigPriceException(MessagesUtils.get("bot.config.error"), "这个红包发送订单金额有问题=" + "产品Id"+ CecuUtil.getDbCode() + "，" + orderRedEnvelopeSend.getOrderNo());
        }

    }

    public int updateByeReceive(Long id, BigDecimal amount) {
        String signSecretKey = mNacosParam.getSignSecretKey();
        long signTime = System.currentTimeMillis();
        Integer row = this.baseMapper.updateByeReceive(id, amount, signSecretKey, signTime);
        return row;
    }


    public Long selectOrderRedEnvelopeSendCount(OrderRedEnvelopeSend param) {
        return this.baseMapper.selectOrderRedEnvelopeSendCount(param);
    }
}
