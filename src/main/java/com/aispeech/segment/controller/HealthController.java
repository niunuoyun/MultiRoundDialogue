package com.aispeech.segment.controller;

import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private static final RateLimiter rateLimiter = RateLimiter.create(10);

    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);


//    @Value("${sqa.request.url}")
//    private String SQA_URL;

    @GetMapping(value = "/healthz")
    public String health(){
        return "SUCESS";
    }

    @GetMapping(value = "rate")
    public String ceshi(){
        if(rateLimiter.tryAcquire()){
            return "sucess";
        }else {
            return "error";
        }
    }
}
