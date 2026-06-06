package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.mybatisplus.pay.constant.PayConst;
import com.common.core.util.RedisUtil;
import com.gp.common.base.utils.BeanUtils;
import com.gp.common.base.utils.DateUtils;
import com.gp.common.mybatisplus.entity.Currency;
import com.gp.common.mybatisplus.mapper.CurrencyMapper;
import com.gp.common.mybatisplus.until.CurrencySign;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;


@Service
public class CurrencyService extends ServiceImpl<CurrencyMapper, Currency> {
    @Resource
    private CurrencyMapper tCurrencyMapper;
    @Resource
    RedisUtil redisUtil;
    @Resource
    private CurrencySign currencySign;

    public static Currency usdtCurrency = null;
    //对应mpay肯定不会错
    public static Currency usdtCurrencyAbs = null;

    /**
     * 初始化币种处理
     */
    @PostConstruct
    public void init() {
        CurrencyService.usdtCurrency = this.getById(PayConst.currency_id);

        Currency currency = new Currency();
        currency.setItemId(10007);
        currency.setChainTag("trc20");
        currency.setItemName("USDT");

        CurrencyService.usdtCurrencyAbs = currency;
    }


    /**
     * 开启的币种列表
     *
     * @return 币种列表
     */
    public List<Currency> getOpenCurrencys() {
        return this.list(Wrappers.lambdaQuery(Currency.class).eq(Currency::getStatus, 1));
    }

    public Currency getByItemAndChain(Integer itemId, String chainTag) {
        return this.getOne(Wrappers.lambdaQuery(Currency.class).eq(Currency::getItemId, itemId).eq(Currency::getChainTag, chainTag));

    }

    public Currency getByItem(Integer itemId) {
        return this.getOne(Wrappers.lambdaQuery(Currency.class).eq(Currency::getItemId, itemId).last("limit 1"));

    }

    /**
     * 查询币种
     *
     * @param id 币种ID
     * @return 币种
     */

    public Currency selectCurrencyById(Integer id) {
        return tCurrencyMapper.selectCurrencyById(id);
    }

    /**
     * 查询币种列表
     *
     * @param tCurrency 币种
     * @return 币种
     */

    public List<Currency> selectCurrencyList(Currency tCurrency) {
        return tCurrencyMapper.selectCurrencyList(tCurrency);
    }

    /**
     * 新增币种
     *
     * @param
     * @return 结果
     */

    public Boolean insertCurrency(Currency one) {
        boolean result = this.save(one);
        Integer id = one.getId();
        one = this.getById(id);
        currencySign.dealSign(one);
        this.updateById(one);
        return result;
    }

    /**
     * 修改币种
     *
     * @param param 币种
     * @return 结果
     */

    public Boolean updateCurrency(Currency param) {
        Currency pojo = this.getById(param.getId());
        BeanUtils.copyPropertiesIgnoreNull(param, pojo);
        pojo.setUpdateTime(DateUtils.getNowDate());
        currencySign.dealSign(pojo);
        boolean result = this.updateById(pojo);
        return result;
    }

    /**
     * 批量删除币种
     *
     * @param ids 需要删除的币种ID
     * @return 结果
     */

    public Boolean deleteCurrencyByIds(Integer[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除币种信息
     *
     * @param id 币种ID
     * @return 结果
     */

    public Boolean deleteCurrencyById(Integer id) {
        boolean result = this.removeById(id);
        return result;
    }






}
