package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.PayType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PayTypeMapper extends BaseMapper<PayType> {
    /**
     * 查询支付类型
     *
     * @param id 支付类型ID
     * @return 支付类型
     */
    PayType selectPayTypeById(Integer id);

    /**
     * 查询支付类型列表
     *
     * @param payType 支付类型
     * @return 支付类型集合
     */
    List<PayType> selectPayTypeList(PayType payType);

    /**
     * 新增支付类型
     *
     * @param payType 支付类型
     * @return 结果
     */
    int insertPayType(PayType payType);

    /**
     * 修改支付类型
     *
     * @param payType 支付类型
     * @return 结果
     */
    int updatePayType(PayType payType);

    /**
     * 删除支付类型
     *
     * @param id 支付类型ID
     * @return 结果
     */
    int deletePayTypeById(Integer id);

    /**
     * 批量删除支付类型
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deletePayTypeByIds(Integer[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    int truncate();

    List<PayType> getAllList();
}