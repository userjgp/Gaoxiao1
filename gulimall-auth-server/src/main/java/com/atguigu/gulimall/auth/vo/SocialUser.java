package com.atguigu.gulimall.auth.vo;

import lombok.Data;

/**
 * @Description: 社交用户信息
 * @Created: with IntelliJ IDEA.
 *  * @author jiguangpeng
 *  * @email 1762928707@qq.com
 *  * @date 2022-12-05 23:00:33

 **/

@Data
public class SocialUser {

    private String access_token;

    private String remind_in;

    private long expires_in;

    private String uid;

    private String isRealName;

}
