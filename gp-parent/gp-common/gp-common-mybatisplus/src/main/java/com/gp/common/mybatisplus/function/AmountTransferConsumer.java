package com.gp.common.mybatisplus.function;

import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.entity.UserWallet;

/**
 * @author axing
 * @version 1.0
 * @date 2023/6/2 19:59:30
 */
@FunctionalInterface
public interface AmountTransferConsumer {


    void accept(UserWallet fromOldUserWallet, UserWallet fromNewUserWallet, UserWallet toOldUserWallet, UserWallet toNewUserWallet);
}
