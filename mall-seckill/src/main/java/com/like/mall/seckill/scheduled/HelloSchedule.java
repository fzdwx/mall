package com.like.mall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author like
 * @date 2021-01-27 17:22
 * @contactMe 980650920@qq.com
 * @description
 */
@Component
@Slf4j
public class HelloSchedule {

    @Async
    @Scheduled(cron = "* * * * * ?")
    public void hello() {
        log.info("hello");
    }
}
