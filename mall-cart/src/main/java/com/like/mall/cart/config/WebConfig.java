package com.like.mall.cart.config;

import com.like.mall.cart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author like
 * @date 2020-12-23 21:15
 * @contactMe 980650920@qq.com
 * @description
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器 -> 拦截所有请求
        registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
    }
}
