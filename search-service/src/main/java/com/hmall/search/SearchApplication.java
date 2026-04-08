package com.hmall.search;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;

/**
 * @projectName: Hmall
 * @package: com.hmall.search
 * @className: SearchApplication
 * @author: xuxiang
 * @description: TODO
 * @date: 2023/10/24 20:43
 * @version: 1.0
 */
@EnableFeignClients(basePackages = "com.hmall.common.client")
@SpringBootApplication
public class SearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(SearchApplication.class, args);
    }
}
