package com.like.mall.cart.interceptor;

import cn.hutool.core.lang.UUID;
import com.like.mall.cart.vo.UserInfo;
import com.like.mall.common.constant.AuthConstant;
import com.like.mall.common.constant.CartConstant;
import com.like.mall.common.vo.MemberVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author like
 * @date 2020-12-23 20:57
 * @contactMe 980650920@qq.com
 * @description 在执行目标方法之前，判断用户登录状态。并封装给controller目标请求
 */
@Component
public class CartInterceptor implements HandlerInterceptor {

    // 共享数据
    public static ThreadLocal<UserInfo> userInfoLocal = new ThreadLocal<>();

    /**
     * 方法执行前
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfo userInfo = new UserInfo();

        // 封装userInfo
        HttpSession session = request.getSession();
        MemberVo user = (MemberVo) session.getAttribute(AuthConstant.LOGIN_USER);
        if (user != null) {
            // 获取登录用户的购物车 -> userId
            userInfo.setUserId(user.getId());
        }
        // 获取离线购物车 -> user-key
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(CartConstant.User_COOKIE_NAME)) {
                    userInfo.setUserKey(cookie.getValue());
                    userInfo.setTemp(true);
                    break;
                }
            }
        }
        // 用户第一次登录分配一个随机的user-key
        if (StringUtils.isBlank(userInfo.getUserKey())) {
            userInfo.setUserKey(UUID.randomUUID().toString());
        }
        // 目标方法执行前
        userInfoLocal.set(userInfo);
        return true;
    }

    /**
     * 方法执行后
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfo userInfo = userInfoLocal.get();

        // 如果是false就表明是第一次
        if (!userInfo.isTemp()) {
            Cookie cookie = new Cookie(CartConstant.User_COOKIE_NAME, userInfo.getUserKey());
            cookie.setDomain("localhost");
            cookie.setMaxAge(CartConstant.COOKIE_TTL);
            response.addCookie(cookie);
        }
    }
}
