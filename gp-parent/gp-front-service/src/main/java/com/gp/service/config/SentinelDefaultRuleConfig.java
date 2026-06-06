package com.gp.service.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SentinelDefaultRuleConfig implements CommandLineRunner {

    @Override
    public void run(String... args) {
        List<FlowRule> rules = new ArrayList<>();

        // 默认限流规则，给所有接口添加一个通用 QPS 限制
        // 注意：这里不写死具体 URL，而是写一个“*默认模板*”，后续手动应用到接口上
        // 更好的方式是结合 Nacos 配置动态加载

        // 例子：/user/** 默认限流 50 QPS
//        FlowRule userRule = new FlowRule();
//        userRule.setResource("/user/**");    // 匹配 /user 下所有接口
//        userRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
//        userRule.setCount(50);
//        rules.add(userRule);

        // 所有路径统一一秒最多 1000次
        FlowRule orderRule = new FlowRule();
        orderRule.setResource("/");
        orderRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        orderRule.setCount(5000);
        rules.add(orderRule);

        // 初始化规则
        FlowRuleManager.loadRules(rules);
    }
}
