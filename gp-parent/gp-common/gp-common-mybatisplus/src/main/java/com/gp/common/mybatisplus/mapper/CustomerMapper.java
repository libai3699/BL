package com.gp.common.mybatisplus.mapper;

import java.util.List;
import com.gp.common.mybatisplus.entity.Customer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 客服Mapper接口
 *
 * @author axing
 * @date 2024-07-15
 */
public interface CustomerMapper extends BaseMapper<Customer>
{
    /**
     * 查询客服
     *
     * @param id 客服ID
     * @return 客服
     */
    public Customer selectCustomerById(Integer id);

    /**
     * 查询客服列表
     *
     * @param customer 客服
     * @return 客服集合
     */
    public List<Customer> selectCustomerList(Customer customer);

    /**
     * 新增客服
     *
     * @param customer 客服
     * @return 结果
     */
    public int insertCustomer(Customer customer);

    /**
     * 修改客服
     *
     * @param customer 客服
     * @return 结果
     */
    public int updateCustomer(Customer customer);

    /**
     * 删除客服
     *
     * @param id 客服ID
     * @return 结果
     */
    public int deleteCustomerById(Integer id);

    /**
     * 批量删除客服
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteCustomerByIds(Integer[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

}
