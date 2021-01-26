package com.like.mall.member.controller.web;

import com.like.mall.common.utils.R;
import com.like.mall.member.feign.OrderFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

/**
 * @author like
 * @date 2021-01-21 17:08
 * @contactMe 980650920@qq.com
 * @description
 */
@Controller
public class MemberWebController {
    @Autowired
    OrderFeignService orderFeignService;

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, Model model) {
        // 1.获取支付宝传递过来的所有请求数据

        HashMap<String, Object> params = new HashMap<>();
        params.put("page", pageNum.toString());
        // 查出当前登录用户的所有订单列表数据
        R r = orderFeignService.listWithItem(params);
        model.addAttribute("orders", r);
        return "orderList";
    }
}

