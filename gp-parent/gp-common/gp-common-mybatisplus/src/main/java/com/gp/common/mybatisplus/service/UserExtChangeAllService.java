//package com.gp.common.mybatisplus.service;
//
//import java.util.List;
//import com.gp.common.base.utils.DateUtils;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.gp.common.mybatisplus.entity.UserExtChange;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Arrays;
//import com.gp.common.mybatisplus.mapper.UserExtChangeAllMapper;
//import com.gp.common.mybatisplus.entity.UserExtChangeAll;
//import com.gp.common.mybatisplus.service.UserExtChangeAllService;
//
///**
// * 用户账变Service业务层处理
// *
// * @author axing
// * @date 2024-10-19
// */
//@Service
//public class UserExtChangeAllService extends ServiceImpl<UserExtChangeAllMapper, UserExtChangeAll>
//{
//    @Autowired
//    private UserExtChangeAllMapper userExtChangeAllMapper;
//
//    /**
//     * 查询用户账变
//     *
//     * @param id 用户账变ID
//     * @return 用户账变
//     */
//
//    public UserExtChangeAll selectUserExtChangeAllById(Long id)
//    {
//        return userExtChangeAllMapper.selectUserExtChangeAllById(id);
//    }
//
//    /**
//     * 查询用户账变列表
//     *
//     * @param param 用户账变
//     * @return 用户账变
//     */
//
//    public List<UserExtChangeAll> selectUserExtChangeAllList(UserExtChangeAll param)
//    {
//        return userExtChangeAllMapper.selectUserExtChangeAllList(param);
//    }
//
//    /**
//     * 新增用户账变
//     *
//     * @param param 用户账变
//     * @return 结果
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public Boolean insertUserExtChangeAll(UserExtChangeAll param)
//    {
//        param.setCreateTime(DateUtils.getNowDate());
//        boolean result = this.save(param);
//        return result;
//    }
//
//    /**
//     * 修改用户账变
//     *
//     * @param param 用户账变
//     * @return 结果
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public Boolean updateUserExtChangeAll(UserExtChangeAll param)
//    {
//        boolean result = this.updateById(param);
//        return result;
//    }
//
//    /**
//     * 批量删除用户账变
//     *
//     * @param ids 需要删除的用户账变ID
//     * @return 结果
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public Boolean deleteUserExtChangeAllByIds(Long[] ids)
//    {
//        boolean result = this.removeByIds(Arrays.asList(ids));
//        return result;
//    }
//
//    /**
//     * 删除用户账变信息
//     *
//     * @param id 用户账变ID
//     * @return 结果
//     */
//    @Transactional(rollbackFor = Exception.class)
//    public Boolean deleteUserExtChangeAllById(Long id)
//    {
//        boolean result = this.removeById(id);
//        return result;
//    }
//
//    public List<UserExtChangeAll> selectUserExtChangeAllByUser(Long channelId, String startTime, String endTime) {
//        return userExtChangeAllMapper.selectUserExtChangeAllByUser(channelId,startTime,endTime);
//    }
//}
