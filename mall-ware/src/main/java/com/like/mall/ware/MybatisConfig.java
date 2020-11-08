package com.like.mall.ware;

import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author like
 * @since 2020-11-08 20:47
 */
@Configuration
@EnableTransactionManagement
@MapperScan("com.like.mall.ware.dao")
public class MybatisConfig {
    // 分页插件
    @Bean
    public PaginationInnerInterceptor paginationInnerInterceptor() {
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        // 设置请求的页面大于最后页面的操作，true 返回首页
        paginationInnerInterceptor.setOverflow(true);
        // 设置最大分页
        paginationInnerInterceptor.setMaxLimit(500L);
        return paginationInnerInterceptor;
    }
}
