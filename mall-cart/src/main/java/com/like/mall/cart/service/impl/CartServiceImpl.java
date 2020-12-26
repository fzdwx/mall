package com.like.mall.cart.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.like.mall.cart.feign.ProductFeignService;
import com.like.mall.cart.interceptor.CartInterceptor;
import com.like.mall.cart.service.CartService;
import com.like.mall.cart.vo.Cart;
import com.like.mall.cart.vo.CartItem;
import com.like.mall.cart.vo.UserInfo;
import com.like.mall.common.utils.R;
import com.like.mall.common.vo.SkuInfoEntity;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author like
 * @date 2020-12-24 17:13
 * @contactMe 980650920@qq.com
 * @description
 */
@Service
public class CartServiceImpl implements CartService {
    private static final String cart_prefix = "cart:";
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private ProductFeignService productFeignService;
    @Resource
    private ThreadPoolExecutor thread;

    @Override
    public void addItemToCart(String skuId, Integer num) throws ExecutionException, InterruptedException {
        BoundHashOperations<String, Object, Object> ops = getCartOps();
        CartItem cartItem;

        // 判断这个商品在购物车中是否存在
        Object o = ops.get(JSON.toJSONString(skuId)); // fix 保存格式为json 所以读取格式也要是json
        if (Objects.isNull(o)) {
            cartItem = new CartItem();
            // 添加新商品：
            // 1.查询当前要添加的商品信息
            CompletableFuture<Void> getSkuInfoTask = CompletableFuture.runAsync(() -> {
                R r = productFeignService.info(Long.parseLong(skuId));  // 远程调用
                SkuInfoEntity info = BeanUtil.toBean(r.get("skuInfo"), SkuInfoEntity.class);
                cartItem.setSkuId(info.getSkuId());
                cartItem.setCheck(true);
                cartItem.setTitle(info.getSkuTitle());
                cartItem.setImage(info.getSkuDefaultImg());
                cartItem.setPrice(info.getPrice());
                cartItem.setCount(num);
                cartItem.setTotalPrice(info.getPrice().multiply(new BigDecimal(num)));
            }, thread);
            // 2.查询属性信息
            CompletableFuture<Void> getAttrTask = CompletableFuture.runAsync(() -> {
                List<String> value = productFeignService.getSkuSaleAttrValue(skuId.toString());  // 远程调用
                cartItem.setSkuAttr(value);
            }, thread);

            CompletableFuture.allOf(getAttrTask, getSkuInfoTask).get();
        } else {
            // 1.修改数量
            cartItem = (CartItem) o;
            cartItem.setCount(cartItem.getCount() + num);
            cartItem.setTotalPrice(cartItem.getTotalPrice());
        }
        // 3.保存到redis中
        ops.put(JSON.toJSONString(skuId), cartItem);

    }

    @Override
    public CartItem getCartItem(String skuId) {
        BoundHashOperations<String, Object, Object> ops = getCartOps();
        return BeanUtil.toBean(ops.get(JSON.toJSONString(skuId)), CartItem.class);
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        UserInfo user = CartInterceptor.userInfoLocal.get();
        Cart cart = new Cart();

        // 1.获取离线购物车
        List<CartItem> items = getCartItems(cart_prefix + user.getUserKey());
        // 判断离线购物车中是否有内容

        // 2.获取登录购物车
        Long userId = user.getUserId();
        if (userId != null) {
            // 3.用戶已经登录->合并购物车->清空离线购物车
            if (items != null && items.size() > 0) {
                for (CartItem cartItem : items) {
                    addItemToCart(cartItem.getSkuId().toString(), cartItem.getCount());  // 合并购物车
                }
            }
            deleteCart(cart_prefix + user.getUserKey());  // 清空离线购物车
            items = getCartItems(cart_prefix + userId);   // 获取合并后的购物车内容
        }

        cart.setItems(items);

        return cart;
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> ops = getCartOps();
        CartItem item = getCartItem(skuId.toString());
        item.setCheck(check==1);
        getCartOps().put(JSON.toJSONString(skuId.toString()),JSON.toJSONString(item)); // 保存
    }

    /**
     * 删除购物车
     *
     * @param key user key
     */
    private void deleteCart(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 根据购物项的key,获取对应购物项
     *
     * @param key 关键
     * @return {@link List<CartItem>}
     */
    private List<CartItem> getCartItems(String key) {
        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(key);
        List<Object> values = ops.values();
        if (values != null && values.size() > 0)
            return values.stream()
                    .map(s -> (CartItem) s)
                    .collect(Collectors.toList());
        return null;
    }

    /**
     * 获取购物车的hash ops
     *
     * @return {@link BoundHashOperations<String, Object, Object>}
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfo user = CartInterceptor.userInfoLocal.get();

        // 1.生成redis中的key
        StringBuilder cartKey = new StringBuilder(cart_prefix);
        if (user.getUserId() != null) {
            cartKey.append(user.getUserId());
        } else {
            cartKey.append(user.getUserKey());
        }

        BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(cartKey.toString());
        return ops;
    }
}
