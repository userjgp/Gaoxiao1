package com.atguigu.gulimall.member.exception;

/**
 * @Description:
 * @author jiguangpeng
 * @email 1762928707@qq.com
 * @date 2022-12-05 23:00:33
 **/
public class UsernameException extends RuntimeException {


    public UsernameException() {
        super("存在相同的用户名");
    }
}
