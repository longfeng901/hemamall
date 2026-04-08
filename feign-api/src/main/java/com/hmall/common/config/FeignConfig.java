package com.hmall.common.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @projectName: Hmall
 * @package: com.hmall.common.config
 * @className: FeignConfig
 * @author: xuxiang
 * @description: TODO
 * @date: 2023/10/31 16:39
 * @version: 1.0
 */
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new MyFeignInterceptor();
    }
}
