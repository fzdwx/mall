package com.like.mall.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author like
 * @date 2020-12-12 21:40
 * @contactMe 980650920@qq.com
 * @description
 */
@ConfigurationProperties(prefix = "thread.pool")
@Component
@Data
public class ThreadPoolConfigProperties {

    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAlive;
}
