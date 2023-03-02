package com.atguigu.gulimall.cart.interceptor;

import com.atguigu.common.vo.MemberResponseVo;
import com.atguigu.gulimall.cart.to.UserInfoTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

import static com.atguigu.common.constant.AuthServerConstant.LOGIN_USER;
import static com.atguigu.common.constant.CartConstant.TEMP_USER_COOKIE_NAME;
import static com.atguigu.common.constant.CartConstant.TEMP_USER_COOKIE_TIMEOUT;

/**
 * @Description: 在执行目标方法之前，判断用户的登录状态.并封装传递给controller目标请求
 * @Created: with IntelliJ IDEA.
 * @author: nb纪广鹏
 **/

public class CartInterceptor implements HandlerInterceptor {


    public static ThreadLocal<UserInfoTo> toThreadLocal = new ThreadLocal<>();

    /***
     * 目标方法执行之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        MemberResponseVo attribute = (MemberResponseVo) session.getAttribute(LOGIN_USER);
        UserInfoTo userInfoTo = new UserInfoTo();
        if(attribute!=null){
//            登录
            userInfoTo.setUserId(attribute.getId());
        }
        Cookie[] cookies = request.getCookies();
        if(cookies!=null&&cookies.length>0){
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if(name.equals(TEMP_USER_COOKIE_NAME)){
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTempUser(true);
                }

            }
        }
//        如果没有临时用户，一定分配一个
        if(StringUtils.isEmpty(userInfoTo.getUserKey())){
            String s = UUID.randomUUID().toString();
            userInfoTo.setUserKey(s);
        }


        toThreadLocal.set(userInfoTo);
        return true;
    }


    /**
     * 业务执行之后，分配临时用户来浏览器保存
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
            UserInfoTo userInfoTo = toThreadLocal.get();
    if(!userInfoTo.getTempUser()){
        Cookie cookie = new Cookie(TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
        cookie.setDomain("gulimall.com");
        cookie.setMaxAge(TEMP_USER_COOKIE_TIMEOUT);
        response.addCookie(cookie);
    }


    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
