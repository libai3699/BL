package com.common.core.util;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.function.Supplier;

public class TxExecutor {

    private static final PlatformTransactionManager txManager =
            SpringUtil.getBean(PlatformTransactionManager.class);

    /**
     * 同步事务执行，return 前事务一定已提交
     * @param txLogic 事务逻辑
     * @return 返回事务中业务返回结果
     */
    public static <T> T run(Supplier<T> txLogic) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionStatus status = txManager.getTransaction(def);
        try {
            T result = txLogic.get(); // 执行业务逻辑
            txManager.commit(status); // ✅ 事务提交完成
            return result;            // ✅ return 前已提交
        } catch (Exception e) {
            e.printStackTrace();
            txManager.rollback(status);
            throw e;
        }
    }

    public static void run(Runnable runnable) {
        run(() -> {
            runnable.run();
            return null;
        });
    }
}
