package com.like.mall.order.feign;

import com.like.mall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("mall-cart")
public interface CartFeignService {

    @GetMapping("/getUserCartItems")
    public List<OrderItemVo> getUserCartItems();
}
