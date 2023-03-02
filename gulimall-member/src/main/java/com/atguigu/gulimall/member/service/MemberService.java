package com.atguigu.gulimall.member.service;

import com.atguigu.gulimall.member.exception.PhoneException;
import com.atguigu.gulimall.member.exception.UsernameException;
import com.atguigu.gulimall.member.vo.MemberRegistVo;
import com.atguigu.gulimall.member.vo.MemberUserLoginVo;
import com.atguigu.gulimall.member.vo.SocialUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gulimall.member.entity.MemberEntity;

import java.util.Map;
import com.atguigu.common.utils.PageUtils;
/**
 * 会员
 *
 * @author jiguangpeng
 * @email 1762928707@qq.com
 * @date 2022-12-05 22:56:40
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegistVo vo);

    void checkephone(String phone) throws PhoneException;

    void checkeusername(String username)throws UsernameException;

    MemberEntity login(MemberUserLoginVo vo);

    MemberEntity login(SocialUser socialUser) throws Exception;
}

