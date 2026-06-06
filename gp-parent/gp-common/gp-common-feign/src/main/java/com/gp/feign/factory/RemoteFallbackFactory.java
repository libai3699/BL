//package com.gp.feign.factory;
//
//
//import com.gp.common.base.feign.SendVo;
//import com.gp.feign.api.BotRemoteService;
//import feign.hystrix.FallbackFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.RequestBody;
//
///**
// * 日志服务降级处理
// *
// * @author ruoyi
// */
//@Component
//public class RemoteFallbackFactory implements FallbackFactory<BotRemoteService>
//{
//    private static final Logger log = LoggerFactory.getLogger(RemoteFallbackFactory.class);
//
//    @Override
//    public BotRemoteService create(Throwable throwable)
//    {
//        log.error("日志服务调用失败:{}", throwable.getMessage());
//        return new BotRemoteService()
//        {
//            @Override
//            public String sendReceiveMoney(SendVo sendVo) {
//                return "失败了";
//            }
//        };
//
//    }
//}
