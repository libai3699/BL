package com.gp.framework.security.handle;

import com.alibaba.fastjson.JSON;
import com.common.core.constant.HttpStatus;
import com.common.core.result.AjaxResult;
import com.gp.common.utils.ServletUtils;
import com.gp.framework.security.LoginUser;
import com.gp.framework.security.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    @Autowired
    private TokenService tokenService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        LoginUser loginUser = tokenService.getLoginUser(request);
        String productId = tokenService.getProductId(request);
        if (loginUser != null) {
            log.info("退出登录, 用户名: {}, 产品ID: {}", loginUser.getUsername(), productId);
            tokenService.delLoginUser(productId, loginUser.getToken());
            // TODO: 异步记录退出日志（原项目通过 AsyncManager + AsyncFactory.recordLogininfor 实现）
        }
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.success("退出成功")));
    }
}
