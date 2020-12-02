package com.like.mall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author like
 * @email 980650920@qq.com
 * @since 2020-12-02 14:13
 */
@Configuration
public class RedissonConfig {

    /**
     * 所有对redisson的使用都是通过RedissonClient对象
     *
     * @return {@link RedissonClient}* @throws IOException ioexception
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        Config config = new Config();
        // 1、使用单节点模式
        config.useSingleServer()
                .setAddress("redis://47.112.150.204:6379");
        return Redisson.create(config);
    }
}
