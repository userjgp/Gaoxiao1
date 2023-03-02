package com.atguigu.gulimall.auth.feign;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.vo.SocialUser;
import com.atguigu.gulimall.auth.vo.UserLoginVo;
import com.atguigu.gulimall.auth.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-member")
public interface MemberFeignService {

    @PostMapping("member/member/register")
    public R regist(@RequestBody UserRegistVo Vo);

    @PostMapping(value = "member/member/login")
    public R login(@RequestBody UserLoginVo vo);

    @PostMapping(value = "member/member/oauth2/login")
    public R oauthlogin(@RequestBody SocialUser socialUser) throws Exception;
}
