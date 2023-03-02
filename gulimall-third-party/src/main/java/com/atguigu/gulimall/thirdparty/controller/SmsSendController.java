package com.atguigu.gulimall.thirdparty.controller;


import com.atguigu.common.utils.R;
import com.atguigu.gulimall.thirdparty.component.SmsCompont;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sms")
public class SmsSendController {
    @Autowired
    SmsCompont smsCompont;

    /**
     * 提供给别的服务进行调用
     * @param phone
     * @param code
     * @return
     */
    @GetMapping("/sendCode")
    public R SendCode(@RequestParam("phone") String phone, @RequestParam("code") String code){
        smsCompont.sendSmsCode(phone,code);
        return R.ok();
    }
}
