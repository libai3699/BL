package com.gp.common.mybatisplus.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gp.common.mybatisplus.entity.Currency;
import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.entity.UserWallet;
import com.gp.common.mybatisplus.mapper.UserWalletMapper;
import com.gp.common.mybatisplus.until.UserSign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserWalletService extends ServiceImpl<UserWalletMapper, UserWallet> {
    @Autowired
    private UserWalletMapper userWalletMapper;
    @Resource
    private UserSign userSign;
    @Resource
    private UserService userService;

    public BigDecimal getUsdtAmount(TUser tUser, Currency currency) {
        Integer currencyId = currency.getId();

        //校验用户和币种签名
        userSign.checkUserCurrency(tUser, currencyId);
        UserWallet userWallet = this.getOne(Wrappers.lambdaQuery(UserWallet.class)
                .eq(UserWallet::getUserId, tUser.getUserId())
                .eq(UserWallet::getCurrencyId, currency.getId())
        );
        userSign.checkWalletSign(tUser.getUserId(), userWallet);
        return userWallet.getAmount();
    }

    public UserWallet getUsdtWallet(TUser user, Currency currency) {
        Integer currencyId = currency.getId();

        //校验用户和币种签名
        userSign.checkUserCurrency(user, currencyId);
        UserWallet userWallet = this.getOne(Wrappers.lambdaQuery(UserWallet.class)
                .eq(UserWallet::getUserId, user.getUserId())
                .eq(UserWallet::getCurrencyId, currency.getId())
        );
        userSign.checkWalletSign(user.getUserId(), userWallet);
        return userWallet;
    }

    public UserWallet getUsdtWalletByUserTgId(TUser tUser, Currency currency) {
        UserWallet userWallet = this.getOne(Wrappers.lambdaQuery(UserWallet.class)
                .eq(UserWallet::getUserTgId, tUser.getUserTgId())
                .eq(UserWallet::getCurrencyId, currency.getId())
        );
        Long userId = userWallet.getUserId();
        userSign.checkUserCurrency(tUser, currency.getId());
        //钱包初始化失败之后,再次初始化
        if (userWallet == null) {
            userWallet = userService.initWallet(tUser, currency);
        }
        userSign.checkWalletSign(userWallet.getUserId(), userWallet);
        return userWallet;
    }

    public UserWallet getUsdtWalletByAddr(String addr) {
        return this.getOne(Wrappers.lambdaQuery(UserWallet.class)
                .eq(UserWallet::getMaskAddr, addr)
        );
    }

    public List<UserWallet> lists(Long userId) {
        return this.baseMapper.lists(userId);
    }

    /**
     * 查询用户钱包
     *
     * @param id 用户钱包ID
     * @return 用户钱包
     */

    public UserWallet selectUserWalletById(Long id) {
        return userWalletMapper.selectUserWalletById(id);
    }

    /**
     * 查询用户钱包列表
     *
     * @param tUserWallet 用户钱包
     * @return 用户钱包
     */

    public List<UserWallet> selectUserWalletList(UserWallet tUserWallet) {
        return userWalletMapper.selectUserWalletList(tUserWallet);
    }



    public List<TUser> findWalletIdList(String lanKey) {
        return userWalletMapper.findWalletIdList(lanKey);
    }

    /**
     * 获取当前用户结余
     *
     * @param currencyId 币种ID
     * @return
     */
    public BigDecimal countOrderAmount(Integer currencyId) {
        return userWalletMapper.countOrderAmount(currencyId);
    }

    public BigDecimal countOrderAmountByUserIds(Integer currencyId, List<Long> channelUserIdList) {
        return userWalletMapper.countOrderAmountByUserIds(currencyId, channelUserIdList);
    }

    public  BigDecimal getUserAmount(Long userId) {
        UserWallet userWallet = this.getOne(Wrappers.lambdaQuery(UserWallet.class)
                .eq(UserWallet::getUserId, userId)
                .eq(UserWallet::getCurrencyId, CurrencyService.usdtCurrency.getId())
        );
        return userWallet==null?BigDecimal.ZERO:userWallet.getAmount();
    }

    /**
     * 批量查询用户钱包
     *
     * @param userIds 用户ID列表
     * @param currency 币种对象
     * @return 用户ID与钱包的映射Map
     */
    public Map<Long, UserWallet> batchQueryWalletMap(List<Long> userIds, Currency currency) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashMap<>();
        }

        try {
            List<UserWallet> wallets = this.list(Wrappers.lambdaQuery(UserWallet.class)
                    .eq(UserWallet::getCurrencyId, currency.getId())
                    .in(UserWallet::getUserId, userIds));

            if (wallets != null && !wallets.isEmpty()) {
                return wallets.stream()
                        .collect(Collectors.toMap(UserWallet::getUserId, wallet -> wallet, (v1, v2) -> v1));
            }
        } catch (Exception e) {
            // 记录异常但不抛出，返回空Map
        }

        return new HashMap<>();
    }

    public Map<Long, BigDecimal> queryMap(List<Long> userIds) {
        if(CollectionUtils.isEmpty(userIds)){
            return Collections.emptyMap();
        }
        List<Map<String, Object>> list = userWalletMapper.queryMap(userIds);
        return list.stream()
                .collect(Collectors.toMap(
                        m -> (Long) m.get("userId"),
                        m -> (BigDecimal) m.getOrDefault("usdtBalanceAmount", BigDecimal.ZERO)
                ));
    }
}
