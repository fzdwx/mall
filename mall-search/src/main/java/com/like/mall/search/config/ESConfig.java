package com.like.mall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
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

    public static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
       /* builder.addHeader("Authorization","Bearer"+TOKEN)
                .setHttpAsyncResponseConsumerFactory(new HttpAsyncResponseConsumerFactory
                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));*/
        COMMON_OPTIONS = builder.build();
    }

    /**
     * 注册es客户端
     *
     * @return {@link RestHighLevelClient}
     */
    @Bean
    public RestHighLevelClient esClient() {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost("47.112.150.204", 9200, "http"))
        );
    }

}
