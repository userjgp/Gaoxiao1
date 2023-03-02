package com.atguigu.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.common.util.Md5Utils;
import com.atguigu.common.utils.HttpUtils;
import com.atguigu.gulimall.member.dao.MemberLevelDao;
import com.atguigu.gulimall.member.entity.MemberLevelEntity;
import com.atguigu.gulimall.member.exception.PhoneException;
import com.atguigu.gulimall.member.exception.UsernameException;
import com.atguigu.gulimall.member.vo.MemberRegistVo;
import com.atguigu.gulimall.member.vo.MemberUserLoginVo;
import com.atguigu.gulimall.member.vo.SocialUser;
import com.qiniu.util.Md5;
import org.apache.catalina.User;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gulimall.member.dao.MemberDao;
import com.atguigu.gulimall.member.entity.MemberEntity;
import com.atguigu.gulimall.member.service.MemberService;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import org.w3c.dom.Entity;

@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(MemberRegistVo vo) {
        MemberEntity memberEntity = new MemberEntity();
//        设置默认等级
        MemberLevelEntity memberLevelEntity=memberLevelDao.getDefaultevel1();
        memberEntity.setLevelId(memberLevelEntity.getId());

//        检查有户名和手机号是否唯一
        checkephone(vo.getPhone());
        checkeusername(vo.getUserName());


        memberEntity.setMobile(vo.getPhone());
        memberEntity.setUsername(vo.getUserName());
        memberEntity.setNickname(vo.getUserName());
//        加密存储
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(vo.getPassword());

        memberEntity.setPassword(encode);

//        其他的默认信息自行设置
        MemberDao baseMapper = this.baseMapper;

//        保存
        baseMapper.insert(memberEntity);

    }

    @Override
    public void checkephone(String phone) {
        MemberDao baseMapper = this.baseMapper;
        Integer mobile = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));
        if(mobile>0){
            throw new PhoneException();
        }


    }

    @Override
    public void checkeusername(String username) {
        MemberDao baseMapper = this.baseMapper;
        Integer mobile = baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", username));
        if(mobile>0){
            throw new UsernameException();
        }
    }

    @Override
    public MemberEntity login(MemberUserLoginVo vo) {

        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("username", loginacct).or().eq("mobile", loginacct));
        if(memberEntity==null){
            return null;
        }else {
            String password1 = memberEntity.getPassword();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            boolean matches = encoder.matches(password, password1);
            if( matches){
                return  memberEntity;
                //登录成功
            }
        }
        return null;
    }

    @Override
    public MemberEntity login(SocialUser socialUser) throws Exception {
        String uid = socialUser.getUid();
//判断当前社交用户是否已经登陆过
        MemberDao baseMapper = this.baseMapper;
        MemberEntity memberentity = baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));
        if(memberentity!=null){
            MemberEntity memberEntity1 = new MemberEntity();
            memberEntity1.setId(memberentity.getId());
            memberEntity1.setAccessToken(memberentity.getAccessToken());
            memberEntity1.setExpiresIn(socialUser.getExpires_in());

            baseMapper.updateById(memberEntity1);
            memberentity.setAccessToken(socialUser.getAccess_token());
            memberentity.setExpiresIn(socialUser.getExpires_in());

            return  memberentity;

        }else {
            //2、没有查到当前社交用户对应的记录我们就需要注册一个
            MemberEntity register = new MemberEntity();
            //3、查询当前社交用户的社交账号信息（昵称、性别等）
            Map<String,String> query = new HashMap<>();
            query.put("access_token",socialUser.getAccess_token());
            query.put("uid",socialUser.getUid());
            HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), query);

            if (response.getStatusLine().getStatusCode() == 200) {
                //查询成功
                String json = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = JSON.parseObject(json);
                String name = jsonObject.getString("name");
                String gender = jsonObject.getString("gender");
                String profileImageUrl = jsonObject.getString("profile_image_url");

                register.setNickname(name);
                register.setGender("m".equals(gender)?1:0);
                register.setHeader(profileImageUrl);
                register.setCreateTime(new Date());
                register.setSocialUid(socialUser.getUid());
                register.setAccessToken(socialUser.getAccess_token());
                register.setExpiresIn(socialUser.getExpires_in());

                //把用户信息插入到数据库中
                this.baseMapper.insert(register);

            }else {
                System.out.println("查询失败");
            }
            return register;
        }



    }

}