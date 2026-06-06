package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.param.QueryUserParam;
import com.gp.common.mybatisplus.param.UserLanDto;
import com.gp.common.mybatisplus.vo.ChannelRegisterStatVO;
import com.gp.common.mybatisplus.vo.OrderStatVO;
import com.gp.common.mybatisplus.vo.UserIdAmountVO;
import com.gp.common.mybatisplus.vo.UserListWagerExtAggVO;
import com.gp.common.mybatisplus.vo.UserRwAddressHitVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<TUser> {
    /**
     * 查询用户
     *
     * @param userId 用户ID
     * @return 用户
     */
    public TUser selectTUserById(Long userId);

    /**
     * 查询用户列表
     *
     * @param param 用户
     * @return 用户集合
     */
    List<TUser> selectTUserList(QueryUserParam param);

    List<TUser> selectUserList(QueryUserParam param);

    /**
     * 查询用户数量
     *
     * @param param 用户
     * @return 用户数量
     */
    public Long selectTUserCount(QueryUserParam param);

    /**
     * 新增用户
     *
     * @param tUser 用户
     * @return 结果
     */
    public int insertTUser(TUser tUser);

    /**
     * 修改用户
     *
     * @param tUser 用户
     * @return 结果
     */
    public int updateTUser(TUser tUser);

    /**
     * 用户资料变更审计（标签改存 t_user_merchant_tag 后仍更新用户主表时间与操作人）
     */
    int touchUserUpdateAudit(@Param("userId") Long userId,
                             @Param("updateTime") Date updateTime,
                             @Param("updateBy") String updateBy);

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 结果
     */
    public int deleteTUserById(Long userId);

    /**
     * 批量删除用户
     *
     * @param userIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteTUserByIds(Long[] userIds);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();

    public OrderStatVO countUser();

    public OrderStatVO countUserByTime(@Param("startTime") String startTime, @Param("endTime") String endTime);

    /**
     * 根据语言查询用户
     *
     * @param userLanDto 用户
     * @return 结果
     */
    public List<TUser> selectUserTgIdByLanKey(UserLanDto userLanDto);

    List<Long> getCountByChannelId(@Param("channelId") Long id);

    List<Long> getCountByChannelIds(@Param("arr") ArrayList<Long> arr);

    List<Long> getCountByChannelIdToday(@Param("channelId") Long id, @Param("date") Date date);

    List<Long> getCountByChannelIdsToday(@Param("arr") ArrayList<Long> arr, @Param("date") Date date);

    List<TUser> randomUser(@Param("limit") Integer i);

    List<TUser> exportUserBasicInfo(QueryUserParam param);

    List<ChannelRegisterStatVO> selectTodayRegisterByChannel(@Param("start")Date start, @Param("end")Date end);
    List<TUser> selectUserBasePage(QueryUserParam param);

    /**
     * 按冲提地址反查：充值已支付仅 from_address；法币提现成功 from_addr / bind_data.address
     */
    List<UserRwAddressHitVO> selectUsersByRwAddress(@Param("address") String address);

    /**
     * 控制后台用户列表：人工下分真实提现汇总（currency_id=6, order_type=2, lower_subtype=1）
     */
    List<UserIdAmountVO> sumOrderPersonWithdrawAmountByUserIds(@Param("userIds") List<Long> userIds);

    /** 链上提现成功（currency_id=6, order_status=4） */
    List<UserIdAmountVO> sumOrderWithdrawAmountByUserIds(@Param("userIds") List<Long> userIds);

    /** 法币提现成功（currency_id=6, order_status=4） */
    List<UserIdAmountVO> sumOrderLawWithdrawAmountByUserIds(@Param("userIds") List<Long> userIds);

    /** 用户日报客损汇总（t_user_count_order.customer_loss_amount） */
    List<UserIdAmountVO> sumCustomerLossAmountByUserIds(@Param("userIds") List<Long> userIds);

    /** 提现打码量(type=3) 与 累计打码量(type=2)，用于未完成打码 */
    List<UserListWagerExtAggVO> sumUserExtWagerByUserIds(@Param("userIds") List<Long> userIds);
}
