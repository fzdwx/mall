package com.like.mall.cart.service;

import com.like.mall.cart.vo.Cart;
import com.like.mall.cart.vo.CartItem;

import java.util.concurrent.ExecutionException;

/**
 * @author like
 * @date 2020-12-23 20:49
 * @contactMe 980650920@qq.com
 * @description
 */
public interface CartService {

    public void addItemToCart(String skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 获取购物车中的购物项
     *
     * @param skuId sku id
     * @return {@link CartItem}
     */
    CartItem getCartItem(String skuId);

    Cart getCart() throws ExecutionException, InterruptedException;

    /**
     * 改变选择状态
     *
     * @param skuId sku id
     * @param check 检查
     */
    void checkItem(Long skuId, Integer check);
}
