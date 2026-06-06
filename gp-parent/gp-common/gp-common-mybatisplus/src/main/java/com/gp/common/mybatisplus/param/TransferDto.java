package com.gp.common.mybatisplus.param;

import com.gp.common.mybatisplus.entity.TUser;
import com.gp.common.mybatisplus.entity.UserWallet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author axing
 * @version 1.0
 * @date 2024/1/8/008 20:51
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 来自用户
     */
    private UserWallet fromUser;
    /**
     * 结束用户
     */
    private UserWallet toUser;
}
