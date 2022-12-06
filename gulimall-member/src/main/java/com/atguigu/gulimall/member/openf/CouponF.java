package com.atguigu.gulimall.member.openf;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-coupon")
public interface CouponF {
    @RequestMapping("coupon/coupon/member/list")
    public R membercoupons();
}
