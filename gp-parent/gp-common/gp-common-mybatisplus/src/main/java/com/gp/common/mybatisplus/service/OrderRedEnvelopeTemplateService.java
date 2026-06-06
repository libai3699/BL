package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.base.constant.RedPacketTypeConstants;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.OrderRedEnvelopeTemplate;
import com.gp.common.mybatisplus.mapper.OrderRedEnvelopeTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 红包模版Service业务层处理
 *
 * @author axing
 * @date 2024-12-25
 */
@Service
public class OrderRedEnvelopeTemplateService extends ServiceImpl<OrderRedEnvelopeTemplateMapper, OrderRedEnvelopeTemplate> {
    @Autowired
    private OrderRedEnvelopeTemplateMapper orderRedEnvelopeTemplateMapper;

    /**
     * 查询红包模版
     *
     * @param id 红包模版ID
     * @return 红包模版
     */

    public OrderRedEnvelopeTemplate selectOrderRedEnvelopeTemplateById(Long id) {
        return orderRedEnvelopeTemplateMapper.selectOrderRedEnvelopeTemplateById(id);
    }

    /**
     * 查询红包模版列表
     *
     * @param param 红包模版
     * @return 红包模版
     */

    public List<OrderRedEnvelopeTemplate> selectOrderRedEnvelopeTemplateList(OrderRedEnvelopeTemplate param) {
        return orderRedEnvelopeTemplateMapper.selectOrderRedEnvelopeTemplateList(param);
    }

    /**
     * 新增红包模版
     *
     * @param param 红包模版
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertOrderRedEnvelopeTemplate(OrderRedEnvelopeTemplate param) {
        param.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改红包模版
     *
     * @param param 红包模版
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateOrderRedEnvelopeTemplate(OrderRedEnvelopeTemplate param) {
        boolean result = this.updateById(param);
        return result;
    }

    /**
     * 批量删除红包模版
     *
     * @param ids 需要删除的红包模版ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrderRedEnvelopeTemplateByIds(Long[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除红包模版信息
     *
     * @param id 红包模版ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteOrderRedEnvelopeTemplateById(Long id) {
        boolean result = this.removeById(id);
        return result;
    }

    public OrderRedEnvelopeTemplate queryNewUserRedEnvelopeTemplate() {
        LambdaQueryWrapper<OrderRedEnvelopeTemplate> q = new LambdaQueryWrapper<>();
        q.eq(OrderRedEnvelopeTemplate::getRedType, RedPacketTypeConstants.NEW_USER_RED_PACKET);
        q.eq(OrderRedEnvelopeTemplate::getStatus, 1);
        q.le(OrderRedEnvelopeTemplate::getStartTime, new Date());
        q.ge(OrderRedEnvelopeTemplate::getEndTime, new Date());
        q.orderByDesc(OrderRedEnvelopeTemplate::getId);
        q.last("limit 1");
        return orderRedEnvelopeTemplateMapper.selectOne(q);
    }
}
