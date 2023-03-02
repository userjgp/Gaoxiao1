package com.atguigu.gulimall.member.exception;

/**
 * @Description:
 * @author jiguangpeng
 * @email 1762928707@qq.com
 * @date 2022-12-05 23:00:33
 **/
public class PhoneException extends RuntimeException {

    public PhoneException() {
        super("存在相同的手机号");
    }
}
