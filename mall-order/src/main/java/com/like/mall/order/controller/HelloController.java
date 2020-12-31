package com.like.mall.order.controller;

import com.like.mall.order.service.OrderService;
import com.like.mall.order.vo.OrderConfirmVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;

/**
 * @author like
 * @date 2020-12-30 16:58
 * @contactMe 980650920@qq.com
 * @description
 */
@Controller
public class HelloController {

    @Resource
    private OrderService orderService;

    /**
     * 去结算
     *
     * @return {@link String}
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model) {
        // 展示订单确认的数据
        OrderConfirmVo vs = orderService.confirmOrder();
        model.addAttribute("orderConfirm",vs);
        return "confirm";
    }

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
