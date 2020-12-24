package com.like.mall.cart.service;

import com.like.mall.cart.vo.CartItem;

import java.util.concurrent.ExecutionException;

/**
 * @author like
 * @date 2020-12-23 20:49
 * @contactMe 980650920@qq.com
 * @description
 */
public interface CartService {

    public CartItem addToCart(String skuId, Integer num) throws ExecutionException, InterruptedException;
}
