package com.gp.framework.security.service;

import com.gp.framework.security.LoginUser;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

// TODO: 实现 JWT 解析 / Redis 缓存 / 续签逻辑。当前为空壳，调用任意方法直接返回 null / 抛异常
@Component
public class TokenService {

    public LoginUser getLoginUser(HttpServletRequest request) {
        // TODO: 从请求头 Authorization 中解析 JWT，查询 Redis 获取 LoginUser
        return null;
    }

    public void verifyToken(LoginUser loginUser) {
        // TODO: 校验 token 是否过期，过期则刷新
    }

    public void delLoginUser(String productId, String token) {
        // TODO: 删除 Redis 缓存中的登录信息
    }

    public String getProductId(HttpServletRequest request) {
        // TODO: 从请求头 / token 中解析产品 ID
        return null;
    }

    public String createToken(LoginUser loginUser) {
        // TODO: 生成 JWT，写入 Redis，返回 token 字符串
        throw new UnsupportedOperationException("TokenService.createToken 未实现");
    }
}
