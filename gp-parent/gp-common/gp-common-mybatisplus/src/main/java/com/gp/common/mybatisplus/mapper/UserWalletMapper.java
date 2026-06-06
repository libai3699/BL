package com.gp.common.mybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.entity.UserWallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserWalletMapper extends BaseMapper<UserWallet> {


    public BigDecimal countUserAmount(@Param("currencyId") Integer currencyId, @Param("userId") Long userId);

    public BigDecimal countUserAmountByUserId(@Param("currencyId") Integer currencyId, @Param("channelId") Long channelId);


    List<UserWallet> lists(@Param("userId") Long userId);

    /**
     * 查询用户钱包
     *
     * @param id 用户钱包ID
     * @return 用户钱包
     */
    public UserWallet selectUserWalletById(Long id);

    /**
     * 查询用户钱包列表
     *
     * @param tUserWallet 用户钱包
     * @return 用户钱包集合
     */
    public List<UserWallet> selectUserWalletList(UserWallet tUserWallet);

    /**
     * 新增用户钱包
     *
     * @param tUserWallet 用户钱包
     * @return 结果
     */
    public int insertUserWallet(UserWallet tUserWallet);

    /**
     * 修改用户钱包
     *
     * @param tUserWallet 用户钱包
     * @return 结果
     */
    public int updateUserWallet(UserWallet tUserWallet);

    /**
     * 删除用户钱包
     *
     * @param id 用户钱包ID
     * @return 结果
     */
    public int deleteUserWalletById(Long id);

    /**
     * 批量删除用户钱包
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUserWalletByIds(Long[] ids);

    /**
     * 清空表数据
     *
     * @return 结果
     */
    public int truncate();


//    BigDecimal countChannelAmount( @Param("userIds") List<Long> userId);
    BigDecimal countChannelAmount( @Param("channelId") Long channelId);

    BigDecimal countOrderAmount(@Param("currencyId") Integer currencyId);

    List<TUser> findWalletIdList(@Param("lanKey") String lanKey);

    Map<String, BigDecimal> countChannelAndOrderAmount(@Param("channelId")Long channelId,@Param("currencyId") Integer currencyId);

    BigDecimal countOrderAmountByUserIds(@Param("currencyId") Integer currencyId, @Param("channelUserIdList") List<Long> channelUserIdList);

    List<Map<String, Object>> queryMap(@Param("userIds") List<Long> userIds);
}
