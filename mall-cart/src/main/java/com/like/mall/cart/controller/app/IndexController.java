package com.like.mall.cart.controller.app;

import com.like.mall.cart.vo.UserInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static com.like.mall.cart.interceptor.CartInterceptor.userInfoLocal;

/**
 * @author like
 * @date 2020-12-22 21:09
 * @contactMe 980650920@qq.com
 * @description
 */
@Controller
public class IndexController {


    /**
     * 购物车列表
     * 浏览器有一个cookie：user-key，用来表示用户的身份
     * 登录：按session
     * 没有登录：user-key
     * 第一次：创建user-key
     *
     * @return {@link String}
     */
    @GetMapping("/cartList.html")
    public String cartList( ) {
        // 获取当前登录用户的信息
        UserInfo userinfo = userInfoLocal.get();
        return "cartList";
    }


    @GetMapping("/success")
    public String success() {
        return "success";
    }
}
