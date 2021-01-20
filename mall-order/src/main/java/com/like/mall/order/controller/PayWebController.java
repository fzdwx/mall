package com.like.mall.order.controller;

import com.alipay.api.AlipayApiException;
import com.like.mall.order.config.AlipayTemplate;
import com.like.mall.order.service.OrderService;
import com.like.mall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author like
 * @date 2021-01-20 17:07
 * @contactMe 980650920@qq.com
 * @description
 */
@Controller
public class PayWebController {
    @Autowired
    AlipayTemplate payTemplate;
    @Autowired
    OrderService orderService;

    @GetMapping(value = "/payOrder",produces = "text/html")
    @ResponseBody()
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
        PayVo pay = orderService.getOrderPay(orderSn);
        return payTemplate.pay(pay);
    }
}
