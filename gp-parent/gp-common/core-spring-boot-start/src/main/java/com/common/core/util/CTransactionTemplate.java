package com.common.core.util;

import com.gp.common.base.exception.BizRuntimeException;
import com.gp.common.base.utils.LogUtil;
import lombok.Data;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 事物链模板
 *
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 14:42
 */
@Data
public class CTransactionTemplate {

    private TransactionTemplate transactionTemplate;

    /**
     * 事务链执行
     *
     * @param callback
     */
    public void doTransaction(CTransactionCallback callback) {

        transactionTemplate.execute((transactionStatus) -> {
            try {
                callback.execute();
            } catch (BizRuntimeException e) {
                LogUtil.error(e, "biz exception then transaction is rollback !");
                transactionStatus.setRollbackOnly();
                throw e;
            } catch (Exception e) {
                LogUtil.error(e, "unknown exception then transaction is rollback !");
                transactionStatus.setRollbackOnly();
                throw e;
            }

            return transactionStatus;
        });

    }
}
