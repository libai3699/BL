package com.gp.common.mybatisplus.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.core.constant.OrderConstant;
import com.common.core.util.StringUtils;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.constant.AccountChangeTypeConstants;
import com.gp.common.base.constant.BaseGameInfoCons;
import com.gp.common.base.constant.ExchangeTypeConstants;
import com.gp.common.base.constant.TransferTypeConstants;
import com.gp.common.base.exception.BusinessException;
import com.gp.common.base.utils.BeanUtils;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.base.utils.MessagesUtils;
import com.gp.common.base.utils.SnowIdUtil;
import com.gp.common.mybatisplus.entity.Merchant;
import com.gp.common.mybatisplus.entity.MerchantChange;
import com.gp.common.mybatisplus.entity.OrderMerchant;
import com.gp.common.mybatisplus.mapper.MerchantMapper;
import com.gp.common.mybatisplus.nacos.MNacosParam;
import com.gp.common.mybatisplus.param.UpAndDownMerchantScoreParam;
import com.gp.common.mybatisplus.until.MerchantSign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 商户Service业务层处理
 *
 * @author axing
 * @date 2024-08-09
 */
@Service
@Slf4j
public class MerchantService extends ServiceImpl<MerchantMapper, Merchant> {
    @Resource
    private MerchantMapper merchantMapper;

    @Resource
    private MerchantChangeService merchantChangeService;

    @Resource
    private OrderMerchantService orderMerchantService;

    @Resource
    private MerchantSign merchantSign;

    @Resource
    private MNacosParam mNacosParam;

    @Resource
    private CecuUtil cecuUtil;

    /**
     * 查询商户
     *
     * @param merchantId 商户ID
     * @return 商户
     */

    public Merchant selectMerchantById(Long merchantId) {
        return merchantMapper.selectMerchantById(merchantId);
    }

    /**
     * 查询商户列表
     *
     * @param param 商户
     * @return 商户
     */

    public List<Merchant> selectMerchantList(Merchant param) {
        return merchantMapper.selectMerchantList(param);
    }

    /**
     * 新增商户
     *
     * @param param 商户
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean insertMerchant(Merchant param) {
        verifyCode(param.getDomainName(), null);
        verifyDomainName(param.getDomainName(), null);
        param.setCreateTime(DateUtils.getNowDate());
        merchantSign.dealSign(param);
        boolean result = this.save(param);
        return result;
    }

    /**
     * 修改商户
     *
     * @param param 商户
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateMerchant(Merchant param) {
        verifyCode(param.getDomainName(), param.getMerchantId());
        verifyDomainName(param.getDomainName(), param.getMerchantId());
        Merchant pojo = this.selectMerchantById(param.getMerchantId());
        if (Objects.isNull(pojo)) {
            throw new BusinessException(
                    StringUtils.format(MessagesUtils.get("common.param.is.error")
                            , "id", param.getMerchantId())
            );
        }
        param.setUpdateTime(DateUtils.getNowDate());
        BeanUtils.copyPropertiesIgnoreNull(param, pojo);
        //防止修改误操作积分
        pojo.setAmount(pojo.getAmount());
        merchantSign.dealSign(pojo);
        boolean result = this.updateById(pojo);
        return result;
    }

    /**
     * 批量删除商户
     *
     * @param merchantIds 需要删除的商户ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteMerchantByIds(Long[] merchantIds) {
        boolean result = this.removeByIds(Arrays.asList(merchantIds));
        return result;
    }

    /**
     * 删除商户信息
     *
     * @param merchantId 商户ID
     * @return 结果
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteMerchantById(Long merchantId) {
        boolean result = this.removeById(merchantId);
        return result;
    }

    /**
     * 域名唯一校验
     */
    private void verifyDomainName(String domainName, Long merchantId) {
        LambdaQueryWrapper<Merchant> queryWrapper = Wrappers.lambdaQuery(Merchant.class);
        queryWrapper.eq(Merchant::getDomainName, domainName);
        if (null != merchantId) {
            queryWrapper.ne(Merchant::getMerchantId, merchantId);
        }
        int count = this.count(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("商户域名重复");
        }

    }

    /**
     * 商户编码唯一校验
     */
    private void verifyCode(String code, Long merchantId) {
        LambdaQueryWrapper<Merchant> queryWrapper = Wrappers.lambdaQuery(Merchant.class);
        queryWrapper.eq(Merchant::getCode, code);
        if (null != merchantId) {
            queryWrapper.ne(Merchant::getMerchantId, merchantId);
        }
        int count = this.count(queryWrapper);
        if (count > 0) {
            throw new RuntimeException("商户编码重复");
        }

    }

    /**
     * 商户上分操作
     *
     * @param param    请求参数
     * @param username 操作人
     */
    @Transactional(rollbackFor = Exception.class)
    public void upScoreByMerchantId(UpAndDownMerchantScoreParam param, String username) {
        Merchant merchant = this.getById(param.getMerchantId());
        if (Objects.isNull(merchant)) {
            throw new RuntimeException("商户信息不存在");
        }
        //验签
        merchantSign.checkSign(merchant);
        //类型(1 上分, 2 下分)
        Integer orderType = TransferTypeConstants.TOP_UP;
        String orderNo = SnowIdUtil.getId(OrderConstant.OrderMerchantScore);
        OrderMerchant orderMerchant = new OrderMerchant();
        orderMerchant.setOrderNo(orderNo);
        orderMerchant.setCurrencyId(BaseGameInfoCons.Currrency.UsdtCurrencyId);
        orderMerchant.setItemId(BaseGameInfoCons.Currrency.itemId);
        orderMerchant.setChainTag(BaseGameInfoCons.Currrency.chainTag);
        orderMerchant.setItemName(BaseGameInfoCons.Currrency.itemName);
        orderMerchant.setMerchantId(merchant.getMerchantId());
        orderMerchant.setMerchantName(merchant.getMerchantName());
        orderMerchant.setOrderType(orderType);
        orderMerchant.setAmount(param.getAmount());
        orderMerchant.setExchangeType(ExchangeTypeConstants.INTERNAL);
        orderMerchant.setRemark("人工商户上分");
        orderMerchant.setCreateBy(username);
        boolean save = orderMerchantService.save(orderMerchant);
        if (!save) {
            throw new RuntimeException(MessagesUtils.get("user.operate.failure"));
        }
        //2.添加商户账变记录
        MerchantChange merchantChange = new MerchantChange();
        merchantChange.setMerchantId(merchant.getMerchantId());
        merchantChange.setMerchantName(merchant.getMerchantName());
        merchantChange.setCurrencyId(BaseGameInfoCons.Currrency.UsdtCurrencyId);
        merchantChange.setItemId(BaseGameInfoCons.Currrency.itemId);
        merchantChange.setChainTag(BaseGameInfoCons.Currrency.chainTag);
        merchantChange.setItemName(BaseGameInfoCons.Currrency.itemName);
        merchantChange.setOrderNo(orderMerchant.getOrderNo());
        merchantChange.setAccountType(AccountChangeTypeConstants.INCOME);
        merchantChange.setOrderType(param.getOrderType());
        merchantChange.setExchangeType(param.getExchangeType());
        merchantChange.setType(param.getType());
        merchantChange.setAmount(param.getAmount());
        merchantChange.setOldAmount(merchant.getAmount());
        merchantChange.setNewAmount(merchant.getAmount().add(param.getAmount()));
        merchantChange.setCreateTime(DateUtils.getNowDate());
        merchantChange.setRemark("人工商户上分");
        merchantChange.setOperator(username);
        merchantChangeService.save(merchantChange);
        //3.商户积分变动
        log.info("param: {}", JSON.toJSONString(param));
        baseMapper.upScoreByMerchantId(param.getMerchantId(), param.getAmount(), mNacosParam.getSignSecretKey(), System.currentTimeMillis());
    }

    /**
     * 商户下分操作
     *
     * @param param    请求参数
     * @param username 操作人
     */
    @Transactional(rollbackFor = Exception.class)
    public void downScoreByMerchantId(UpAndDownMerchantScoreParam param, String username) {
        Merchant merchant = null;
        if (Objects.isNull(param.getMerchantId())) {
            if (Objects.isNull(param.getCode())) {
                throw new RuntimeException("商户编码不能为空");
            }
            merchant = this.getByCodeMerchant(param.getCode());
        } else {
            merchant = this.getById(param.getMerchantId());
        }

        if (Objects.isNull(merchant)) {
            throw new RuntimeException("商户信息不存在");
        }
        //验签
        merchantSign.checkSign(merchant);
        BigDecimal subtract = merchant.getAmount().subtract(param.getAmount());
//        if (subtract.signum() == -1) {
//            throw new RuntimeException("积分不足");
//        }
        //类型(1 上分, 2 下分)
        Integer orderType = TransferTypeConstants.WITHDRAW;
        //1. 生成订单 t_order_merchant
        String orderNo = SnowIdUtil.getId(OrderConstant.OrderMerchantScore);
        OrderMerchant orderMerchant = new OrderMerchant();
        orderMerchant.setOrderNo(orderNo);
        orderMerchant.setCurrencyId(BaseGameInfoCons.Currrency.UsdtCurrencyId);
        orderMerchant.setItemId(BaseGameInfoCons.Currrency.itemId);
        orderMerchant.setChainTag(BaseGameInfoCons.Currrency.chainTag);
        orderMerchant.setItemName(BaseGameInfoCons.Currrency.itemName);
        orderMerchant.setMerchantId(merchant.getMerchantId());
        orderMerchant.setMerchantName(merchant.getMerchantName());
        orderMerchant.setOrderType(orderType);
        orderMerchant.setAmount(param.getAmount());
        orderMerchant.setExchangeType(ExchangeTypeConstants.INTERNAL);
        orderMerchant.setRemark("人工商户下分");
        orderMerchant.setCreateBy(username);
        boolean save = orderMerchantService.save(orderMerchant);
        if (!save) {
            throw new RuntimeException(MessagesUtils.get("user.operate.failure"));
        }
        //2.添加商户账变记录
        MerchantChange merchantChange = new MerchantChange();
        merchantChange.setMerchantId(merchant.getMerchantId());
        merchantChange.setMerchantName(merchant.getMerchantName());
        merchantChange.setCurrencyId(BaseGameInfoCons.Currrency.UsdtCurrencyId);
        merchantChange.setItemId(BaseGameInfoCons.Currrency.itemId);
        merchantChange.setChainTag(BaseGameInfoCons.Currrency.chainTag);
        merchantChange.setItemName(BaseGameInfoCons.Currrency.itemName);
        merchantChange.setOrderNo(orderMerchant.getOrderNo());
        merchantChange.setAccountType(AccountChangeTypeConstants.EXPENSE);
        merchantChange.setOrderType(param.getOrderType());
        merchantChange.setExchangeType(param.getExchangeType());
        merchantChange.setType(param.getType());
        merchantChange.setAmount(param.getAmount());
        merchantChange.setOldAmount(merchant.getAmount());
        merchantChange.setNewAmount(subtract);
        merchantChange.setCreateTime(DateUtils.getNowDate());
        merchantChange.setRemark("人工商户下分");
        merchantChange.setOperator(username);
        merchantChangeService.save(merchantChange);
        //3.商户积分变动
        baseMapper.upScoreByMerchantId(merchant.getMerchantId(), param.getAmount().negate(), mNacosParam.getSignSecretKey(), System.currentTimeMillis());
    }

    /**
     * 根据域名获取商户信息
     *
     * @param domainName 商户域名
     * @return
     */
    public Merchant getByDomainNameMerchant(String domainName) {
        cecuUtil.cutDbByName("yh_ft_control");
        LambdaQueryWrapper<Merchant> queryWrapper = Wrappers.lambdaQuery(Merchant.class);
        queryWrapper.eq(Merchant::getDomainName, domainName).orderByDesc(Merchant::getCreateTime).last("limit 1 ");
        return this.getOne(queryWrapper);
    }

    /**
     * 根据商户编码获取商户信息
     *
     * @param code
     * @return
     */
    public Merchant getByCodeMerchant(String code) {
        cecuUtil.cutDbByName("yh_ft_control");
        LambdaQueryWrapper<Merchant> queryWrapper = Wrappers.lambdaQuery(Merchant.class);
        queryWrapper.eq(Merchant::getCode, code).orderByDesc(Merchant::getCreateTime).last("limit 1 ");
        return this.getOne(queryWrapper);
    }

}
