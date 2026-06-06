package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gp.common.base.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.mybatisplus.mapper.CustomerMapper;
import com.gp.common.mybatisplus.entity.Customer;

@Service
public class CustomerService extends ServiceImpl<CustomerMapper, Customer> {
    @Autowired
    private CustomerMapper tCustomerMapper;

    /**
     * 查询客服
     *
     * @param id 客服ID
     * @return 客服
     */

    public Customer selectCustomerById(Integer id) {
        return tCustomerMapper.selectCustomerById(id);
    }

    /**
     * 查询客服列表
     *
     * @param tCustomer 客服
     * @return 客服
     */
    public List<Customer> selectCustomerList(Customer tCustomer) {
        return tCustomerMapper.selectCustomerList(tCustomer);
    }

    /**
     * 新增客服
     *
     * @param tCustomer 客服
     * @return 结果
     */
    public Boolean insertCustomer(Customer tCustomer) {
        tCustomer.setCreateTime(DateUtils.getNowDate());
        boolean result = this.save(tCustomer);
        return result;
    }

    /**
     * 修改客服
     *
     * @param tCustomer 客服
     * @return 结果
     */
    public Boolean updateCustomer(Customer tCustomer) {
        boolean result = this.updateById(tCustomer);
        return result;
    }

    /**
     * 批量删除客服
     *
     * @param ids 需要删除的客服ID
     * @return 结果
     */
    public Boolean deleteCustomerByIds(Integer[] ids) {
        boolean result = this.removeByIds(Arrays.asList(ids));
        return result;
    }

    /**
     * 删除客服信息
     *
     * @param id 客服ID
     * @return 结果
     */
    public Boolean deleteCustomerById(Integer id) {
        boolean result = this.removeById(id);
        return result;
    }

    public List<Customer> getCustomerAgency(String lanKey) {
        LambdaQueryWrapper<Customer> q = new LambdaQueryWrapper<>();
        q.eq(Customer::getLanKey, lanKey);
        q.eq(Customer::getType, 3);
        List<Customer> customers = tCustomerMapper.selectList(q);
        if (customers.isEmpty()) {
            String findLan = Locale.US.toString();
            if (lanKey.equals(Locale.TAIWAN.toString())) {
                findLan = Locale.CHINA.toString();
            }
            q.clear();
            q.eq(Customer::getLanKey, findLan);
            q.eq(Customer::getType, 3);
            customers = this.list(q);
        }
        return customers;
    }

    /**
     * 1 财务客服, 2 反馈客服 3代理客服
     *
     * @param type
     * @param language
     * @return
     */
    public Customer queryCustomer(Integer type, String language) {
        LambdaQueryWrapper<Customer> queryWrapperCW = Wrappers.lambdaQuery(Customer.class);
        queryWrapperCW.eq(Customer::getLanKey, language);
        queryWrapperCW.eq(Customer::getType, type);
        queryWrapperCW.last("limit 1");
        Customer customerCW = this.getOne(queryWrapperCW);
        //如果语言是繁体的话,去找中文客服
        if (customerCW == null) {
            String findLan = Locale.US.toString();
            if (language.equals(Locale.TAIWAN.toString())) {
                findLan = Locale.CHINA.toString();
            }
            queryWrapperCW.clear();
            queryWrapperCW.eq(Customer::getLanKey, findLan);
            queryWrapperCW.eq(Customer::getType, type);
            queryWrapperCW.last("limit 1");
            customerCW = this.getOne(queryWrapperCW);
        }
        return customerCW;
    }
}
