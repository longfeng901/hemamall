package com.hmall.user.config;


import com.hmall.user.interceptors.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @projectName: Hmall
 * @package: com.hmall.order.config
 * @className: MvcConfig
 * @author: xuxiang
 * @description: TODO
 * @date: 2023/10/31 14:06
 * @version: 1.0
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor(new UserInterceptor()).addPathPatterns("/**");
    }
}
