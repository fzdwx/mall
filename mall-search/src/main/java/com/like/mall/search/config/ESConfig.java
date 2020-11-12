package com.like.mall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author like
 * @since 2020-11-12 20:39
 * Elasticsearch 配置类
 */
@Configuration
public class ESConfig {

    @Bean
    public RestHighLevelClient esClient() {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost("47.112.150.204",9200,"http"))
        );
    }

}
