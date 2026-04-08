package com.hmall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @projectName: Hmall
 * @package: com.hmall.search.config
 * @className: ElasticsearchConfig
 * @author: xuxiang
 * @description: TODO
 * @date: 2023/10/28 21:18
 * @version: 1.0
 */
@Configuration
public class ElasticsearchConfig {

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        return new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.200.129:9200")
        ));
    }
}
