package com.like.mall.cart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author like
 * @date 2020-12-12 21:38
 * @contactMe 980650920@qq.com
 * @description
 */
@Configuration
public class ThreadConfig {
    @Bean
    public ThreadPoolExecutor threadPool(ThreadPoolConfigProperties poolConfig) {
        return new ThreadPoolExecutor(poolConfig.getCoreSize(), poolConfig.getMaxSize(), poolConfig.getKeepAlive(), TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10000), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
