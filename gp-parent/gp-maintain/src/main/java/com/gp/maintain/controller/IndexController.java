package com.gp.maintain.controller;


import com.common.core.util.Ip2RegionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/index")
public class IndexController {

    @GetMapping(value = "/aws/elb/health")
    public String index(){
        return "maintain";
    }

    @Resource
    private Ip2RegionUtil ip2RegionUtil;


//    @GetMapping(value = "/test")
//    public String test(String ip){
//        return ip2RegionUtil.getRegion(ip);
//    }


}

