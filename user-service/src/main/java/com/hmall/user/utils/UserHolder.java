package com.hmall.user.utils;

/**
 * @projectName: Hmall
 * @package: com.hmall.order.utils
 * @className: UserHolder
 * @author: xuxiang
 * @description: TODO
 * @date: 2023/10/31 11:22
 * @version: 1.0
 */
public class UserHolder {
    private static final ThreadLocal<Long> tl = new ThreadLocal<>();

    public static void setUser(Long userId) {
        tl.set(userId);
    }

    public static Long getUser(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}
