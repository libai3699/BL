package com.common.datasource.interceptor;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.core.toolkit.TableNameParser;
import com.baomidou.mybatisplus.extension.handlers.AbstractSqlParserHandler;
import com.common.datasource.param.CecuProp;
import com.common.datasource.util.CecuUtil;
import com.gp.common.base.utils.LogUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * mybatis截取处理
 * </p>
 * @author axing
 * @version 1.0
 * @date 2023/10/31/031 11:19
 */
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
@Component
public class CecuMybatisPlusSqlInterceptor extends AbstractSqlParserHandler implements Interceptor {

    @Resource
    private CecuProp cecuProp;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        if (cecuProp.isCecuSwitch()) {
            StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
            MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
            this.sqlParser(metaObject);
            BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
            // 执行的SQL语句
            PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
            mpBs.sql(changeTable(mpBs.sql()));
        }
        return invocation.proceed();

    }

    protected String changeTable(String sql) {

        TableNameParser parser = new TableNameParser(sql);
        List<TableNameParser.SqlToken> names = new ArrayList<>();
        parser.accept(names::add);
        if (sql.contains("ON DUPLICATE KEY UPDATE")) {
            names = CollUtil.newArrayList(names.get(0));
        }
        if (sql.contains("(TRIM(TRAILING '0' FROM")) {
            names = CollUtil.newArrayList(names.get(0));
        }
        StringBuilder builder = new StringBuilder();
        int last = 0;
        for (TableNameParser.SqlToken name : names) {
            int start = name.getStart();
            if (start != last) {
                builder.append(sql, last, start);
                String value = name.getValue();
                builder.append(CecuUtil.cutDb(value));
            }
            last = name.getEnd();
        }
        if (last != sql.length()) {
            builder.append(sql.substring(last));
        }
        return builder.toString();
    }

    /**
     * 生成拦截对象的代理
     *
     * @param target 目标对象
     * @return 代理对象
     */
    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

}
