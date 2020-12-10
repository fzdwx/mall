package com.like.mall.product.config;

import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

/**
 * @author like
 * @email 980650920@qq.com
 * @since 2020-12-09 13:35
 */
@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)  // 读取配置文件中的配置
public class CacheConfig {

    /**
     * 配置文件中的配置没有用上
     *
     * @return {@link RedisCacheConfiguration}
     */
    @Bean
    public RedisCacheConfiguration  redisCacheConfiguration() {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        // 序列化机制：使用json格式缓存
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()));
        // 设置ttl时间
        config = config.entryTtl(Duration.ofDays(1));
        // 默认缓存空值，设置为不缓存空值
//        config = config.disableCachingNullValues();
        return config;
    }
}


