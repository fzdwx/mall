package com.like.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * @author like
 * @since 2020-10-25 20:34
 * 解决跨域配置类
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // 1.配置跨域
        corsConfiguration.addAllowedHeader("*");                // 请求头
        corsConfiguration.addAllowedMethod("*");               // 请求方法
        corsConfiguration.addAllowedOrigin("*");              // 请求来源
        corsConfiguration.setAllowCredentials(true);         // 是否允许携带cookie

        // 允许所有请求
        source.registerCorsConfiguration("/**",corsConfiguration);
        return new CorsWebFilter(source);
    }
}
