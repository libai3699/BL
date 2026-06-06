package com.gp.common.mybatisplus.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.Currency;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CurrencyMapper extends BaseMapper<Currency> {



    /**
     * 查询币种
     *
     * @param id 币种ID
     * @return 币种
     */
    public Currency selectCurrencyById(Integer id);

    /**
     * 查询币种列表
     *
     * @param tCurrency 币种
     * @return 币种集合
     */
    public List<Currency> selectCurrencyList(Currency tCurrency);

    /**
     * 新增币种
     *
     * @param tCurrency 币种
     * @return 结果
     */
    public int insertCurrency(Currency tCurrency);

    /**
     * 修改币种
     *
     * @param tCurrency 币种
     * @return 结果
     */
    public int updateCurrency(Currency tCurrency);

    /**
     * 删除币种
     *
     * @param id 币种ID
     * @return 结果
     */
    public int deleteCurrencyById(Integer id);

    /**
     * 批量删除币种
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteCurrencyByIds(Integer[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}

