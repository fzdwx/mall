package com.like.mall.cart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author like
 * @date 2020-12-22 21:09
 * @contactMe 980650920@qq.com
 * @description
 */
@Controller
public class IndexController {


    @GetMapping("/cartList")
    public String cartList() {
        return "cartList";
    }


    @GetMapping("/success")
    public String success() {
        return "success";
    }
}
