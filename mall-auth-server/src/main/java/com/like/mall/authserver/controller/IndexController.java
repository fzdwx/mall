package com.like.mall.authserver.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author like
 * @date 2020-12-14 18:37
 * @contactMe 980650920@qq.com
 * @description
 */
@Controller
public class IndexController {

    /**
     * 去登录页面
     *
     * @return {@link String}
     */
    @GetMapping("/login.html")
    public  String loginHtml() {
        return "login";
    }

    /**
     * 去注册页面
     *
     * @return {@link String}
     */
    @GetMapping("/reg.html")
    public  String regHtml() {
        return "reg";
    }
}
