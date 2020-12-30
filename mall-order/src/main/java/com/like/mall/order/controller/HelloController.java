package com.like.mall.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author like
 * @date 2020-12-30 16:58
 * @contactMe 980650920@qq.com
 * @description
 */
@Controller
public class HelloController {


    @GetMapping("list.html")
    public String page() {
        return "list";
    }

    @GetMapping("confirm.html")
    public String confirm() {
        return "confirm";
    }

    @GetMapping("pay.html")
    public String pay() {
        return "pay";
    }

    @GetMapping("detail.html")
    public String detail() {
        return "detail";
    }
}
