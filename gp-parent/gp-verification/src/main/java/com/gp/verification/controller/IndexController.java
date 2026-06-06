package com.gp.verification.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/index")
public class IndexController {

    @GetMapping(value = "/aws/elb/health")
    public String index(){
        return "verification";
    }
}
