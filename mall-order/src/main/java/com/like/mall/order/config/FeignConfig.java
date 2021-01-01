package com.like.mall.order.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author like
 * @date 2020-12-31 18:41
 * @contactMe 980650920@qq.com
 * @description
 */
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            ServletRequestAttributes reqA = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (reqA == null) return;
            HttpServletRequest request = reqA.getRequest();
            // 同步请求头
            template.header("Cookie", request.getHeader("Cookie"));
        };
    }
}
