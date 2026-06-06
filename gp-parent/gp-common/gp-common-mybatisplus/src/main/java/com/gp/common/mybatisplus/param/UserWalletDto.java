package com.gp.common.mybatisplus.param;

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
public class UserWalletDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 最大id
     */
    private UserWallet oldWallet;
    /**
     * 批量查询数量
     */
    private UserWallet newWallet;

}
