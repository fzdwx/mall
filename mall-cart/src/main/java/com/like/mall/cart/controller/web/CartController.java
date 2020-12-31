package com.like.mall.cart.controller.web;

import com.like.mall.cart.service.CartService;
import com.like.mall.cart.vo.CartItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;
import java.util.List;

/**
 * @author like
 * @date 2020-12-23 20:51
 * @contactMe 980650920@qq.com
 * @description
 */
@Controller
public class CartController {

    @Resource
    private CartService cartService;

    @GetMapping("/getUserCartItems")
    public List<CartItem> getUserCartItems() {
        return cartService.getUserCartItems();
    }

    @GetMapping("/checkItem")
    public String checkItem(@PathParam("skuId") Long skuId, @PathParam("check") Integer check) {
        System.out.println("1");
        cartService.checkItem(skuId, check);
        return "redirect:cartList.html";
    }
}
