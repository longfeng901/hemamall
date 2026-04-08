package com.hmall.user.interceptors;


import com.hmall.user.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @projectName: Hmall
 * @package: com.hmall.order.interceptors
 * @className: UserInterceptor
 * @author: xuxiang
 * @description: TODO
 * @date: 2023/10/31 11:37
 * @version: 1.0
 */
@Slf4j
public class UserInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取请求头
        String authorization = request.getHeader("authorization");
        if (StringUtils.isBlank(authorization)){
            log.warn("非法用户访问!请求路径:{}",request.getRequestURI());
            //没有用户信息,未登录403禁止
            response.setStatus(403);
            return false;
        }
        //2.转换用户id
        Long userId = Long.valueOf(authorization);
        //3.存入ThreadLocal
        UserHolder.setUser(userId);
        //放行
        return true;
    }



    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //用完清理
        UserHolder.removeUser();
    }
}
