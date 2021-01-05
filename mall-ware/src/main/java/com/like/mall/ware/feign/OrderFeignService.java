package com.like.mall.ware.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("mall-order")
public interface OrderFeignService {
    @GetMapping("order/order/status/{orderSn}")
    public Integer getOrderStatus(@PathVariable String orderSn);
}
