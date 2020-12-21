package com.like.mall.authserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @author like
 * @date 2020-12-21 16:28
 * @contactMe 980650920@qq.com
 * @description
 */
@Configuration
public class SessionConfig {

    @Bean
    public RedisSerializer<Object>redisSerializer() {
        return RedisSerializer.json();
    }

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookie = new DefaultCookieSerializer();
        cookie.setDomainName("localhost"); // cookie作用域，由于我没有搭建域名服务器，所以localhost就可以了
        return cookie;
    }
}
