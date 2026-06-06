package com.gp.service.auth;

import cn.dev33.satoken.interceptor.SaRouteInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @author axing
 * @version 1.0
 * @date 2022/7/17 00:31:27
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 的路由拦截器，自定义认证规则
        registry.addInterceptor(new SaRouteInterceptor((req, res, handler) -> {
            //当是接口请求时
            if (handler instanceof HandlerMethod) {
                // 登录校验
                SaRouter.match("/**")
                        //登录
                        .notMatch("/user/login")
                        .notMatch("/user/loginAndUserInfo")
                        .notMatch("/user/login2")
                        //获取游戏链接
                        .notMatch("/pre/**")
                        //新加借口需要放开权限的 start
//                        //登录注册还有找回密码
                        .notMatch("/H5user/login")
                        .notMatch("/H5user/loginWeb")
                        .notMatch("/H5user/register")
                        .notMatch("/H5user/resetPassword")
                        .notMatch("/H5user/telegramLogin")
//                        //活动的话主要有中 英文 可能需呀改动一下 看看能让他查下所以的活动吧 应该还有新加的 欧洲杯
                        .notMatch("/activity/queryAllActivity")
                        .notMatch("/activity/queryHomeShowActivity")
                        //站内信
                        .notMatch("/index/querySystemMsg")
                        //轮播图
                        .notMatch("/banner/*")
                        //首页弹窗
                        .notMatch("/homePopup/*")
                        //买币网址
                        .notMatch("/coin/*")
                        //客服管理
                        .notMatch("/customer/list")

                        //配置项
                        .notMatch("/config/*")
                        //还有就是首页
                        .notMatch("/index/queryTopTag")

                        .notMatch("/index/queryGameByTag")
//                        //发送验证吗
                        .notMatch("/mail/sendCodeByEmail")
//                        //转盘
                        .notMatch("/wheel/getWheelInfo")



                        //退出接口
                        .notMatch("/user/loginOut")
                        //健康检测
                        .notMatch("/index/aws/elb/health")
                        .notMatch("/indexYL/**")
                        .notMatch("/index365/**")
                        //新加韩语借口
                        .notMatch("/indexTr/**")
                        .notMatch("/indexTr/**")
                        //红包雨
                        .notMatch("/redPacketRain/**")
                        .notMatch("/user/getLanguageList")
                        .notMatch("/tiktok/getPixIdAndToken")
                        .notMatch("/tiktok/sendTiktokEvent")
                        .notMatch("/rank/winningRank")
                        .notMatch("/rank/betRank")
                        .notMatch("/agency/getTeamData")
//                        .notMatch("/file/**")
                        //swagger
                        .notMatch("/app/queryAppDown")
                        .notMatch("/swagger-ui.html/**")
                        .notMatch("/swagger-resources/**")
                        .notMatch("/v2/**")
                        .check(r -> StpUtil.checkLogin());
            }
        })).addPathPatterns("/**");

    }


}
