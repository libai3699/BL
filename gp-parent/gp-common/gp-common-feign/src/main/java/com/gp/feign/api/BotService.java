package com.gp.feign.api;


import com.gp.common.base.constant.ServiceNameConstants;
import com.gp.common.base.feign.SendNotifyVo;
import com.gp.feign.dto.RedEnvelope;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 日志服务
 *
 * @author ruoyi
 */
@FeignClient(contextId = "BotService",path = "/bot",value = ServiceNameConstants.tg_ft_bot)
public interface BotService
{



    /**
     * 异步发送通知(不在乎失败成功)
     */
    @PostMapping("/sendNotifyAsy")
    public String sendNotifyAsy(@RequestBody SendNotifyVo sendNotifyVo);


    /**
     * 发系统通知(所有人)
     *
     * @param sendNotifyVo 通知参数
     * @return 结果
     */
    @PostMapping("/sendNotifyAll")
    public String sendNotifyAll(@RequestBody SendNotifyVo sendNotifyVo);

    /**
     * 发送红包
     * @param redEnvelope
     */
    @PostMapping("/sendRedEnvelope")
    void sendRedEnvelope(@RequestBody RedEnvelope redEnvelope);
}
