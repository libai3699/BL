package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.Shareholder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 股东Mapper接口
 *
 * @author axing
 * @date 2025-05-20
 */
public interface ShareholderMapper extends BaseMapper<Shareholder>
{
    /**
     * 查询股东
     *
     * @param id 股东ID
     * @return 股东
     */
    public Shareholder selectShareholderById(Long id);

    /**
     * 查询股东列表
     *
     * @param shareholder 股东
     * @return 股东集合
     */
    public List<Shareholder> selectShareholderList(Shareholder shareholder);

    /**
     * 查询股东列表数量
     *
     * @param shareholder 股东
     * @return 股东集合数量
     */
    public int selectShareholderListCount(Shareholder shareholder);

    /**
     * 新增股东
     *
     * @param shareholder 股东
     * @return 结果
     */
    public int insertShareholder(Shareholder shareholder);

    /**
     * 修改股东
     *
     * @param shareholder 股东
     * @return 结果
     */
    public int updateShareholder(Shareholder shareholder);

    /**
     * 删除股东
     *
     * @param id 股东ID
     * @return 结果
     */
    public int deleteShareholderById(Long id);

    /**
     * 批量删除股东
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteShareholderByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    String getShareholderName(Long shareholderId);
}