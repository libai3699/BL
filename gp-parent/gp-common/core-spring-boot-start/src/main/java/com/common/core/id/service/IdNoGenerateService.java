package com.common.core.id.service;


import com.common.core.id.common.IdNoTypeEnum;

/**
 * 单号生成接口
 *
 * @author mq
 */
public interface IdNoGenerateService {

    /**
     * 根据单据编号类型 生成单据编号
     *
     * @param idNoTypeEnum 单据编号类型
     * @author mengqiang
     * @date 2019/1/1
     */
    String generateFormNo(IdNoTypeEnum idNoTypeEnum);
    /**
     * 生成充值订单号
     *
     * @author mengqiang
     * @date 2019/1/1
     */
    String generatePayNo();
    /**
     * 生成下发订单号
     *
     * @author mengqiang
     * @date 2019/1/1
     */
    String generateWithdrowNo();
}
