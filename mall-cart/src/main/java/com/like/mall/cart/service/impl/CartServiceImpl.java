package com.like.mall.cart.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.like.mall.cart.feign.ProductFeignService;
import com.like.mall.cart.interceptor.CartInterceptor;
import com.like.mall.cart.service.CartService;
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
    public CartItem addToCart(String skuId, Integer num) throws ExecutionException, InterruptedException {
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

        return cartItem;
    }

    /**
     * 获取购物车
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
