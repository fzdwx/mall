package com.like.mall.order.controller;

import com.like.mall.order.entity.OrderEntity;
import com.like.mall.order.service.OrderService;
import com.like.mall.order.vo.OrderConfirmVo;
import com.like.mall.order.vo.OrderSubmitRespVo;
import com.like.mall.order.vo.OrderSubmitVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author like
 * @date 2020-12-30 16:58
 * @contactMe 980650920@qq.com
 * @description
 */
@Controller
public class HelloController {

    @Resource
    RabbitTemplate rabbitTemplate;

    @Resource
    private OrderService orderService;

    @ResponseBody
    @GetMapping("/createOrderTest")
    public String createOrderTest() {
        OrderEntity order = new OrderEntity();
        order.setOrderSn("sn:test");
        order.setModifyTime(new Date());
        rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order);
        return "ok";
    }

    /**
     * 去结算
     */
    @GetMapping("/toTrade")
    public String toTrade(Model model) {
        // 展示订单确认的数据
        OrderConfirmVo vs = orderService.confirmOrder();
        model.addAttribute("orderConfirm", vs);
        return "confirm";
    }

    /**
     * 提交订单
     */
    @PostMapping(value = "/submitOrder")
    public String submitOrder(OrderSubmitVo vo,Model model) {
        // 创建订单，验证令牌，验证价格，锁库存
        OrderSubmitRespVo respVo = orderService.submitOrder(vo);
        if (respVo.getCode() != 0) {  // 失败
            return "redirect:http://localhost:9000/toTrade";
        }
        model.addAttribute("order",respVo.getOrder());
        return "pay";
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
