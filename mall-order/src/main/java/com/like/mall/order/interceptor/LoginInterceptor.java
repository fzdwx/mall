package com.like.mall.order.interceptor;

import com.like.mall.common.constant.AuthConstant;
import com.like.mall.common.vo.MemberVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author like
 * @date 2020-12-31 13:47
 * @contactMe 980650920@qq.com
 * @description
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (new AntPathMatcher().match("/order/order/status/**", request.getRequestURI())) {
            return true;
        }
        if (new AntPathMatcher().match("pay/notify", request.getRequestURI())) {
            return true;
        }

        HttpSession session = request.getSession();
        MemberVo user = (MemberVo) session.getAttribute(AuthConstant.LOGIN_USER);
        if (user == null) {
            response.sendRedirect("http://localhost:7777/login.html");  // 没有登录
        } else {
            loginUser.set(user);
        }
        return user != null;
    }
}
