package com.like.mall.member.config;


import com.like.mall.member.interceptor.LoginInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author like
 * @date 2020-12-31 13:47
 * @contactMe 980650920@qq.com
 * @description
 */
//@Configuration
public class WebConfig implements WebMvcConfigurer {

    private LoginInterceptor loginInterceptor;

    public WebConfig(LoginInterceptor loginInterceptor) {this.loginInterceptor = loginInterceptor;}

    @Override

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
    }
}
