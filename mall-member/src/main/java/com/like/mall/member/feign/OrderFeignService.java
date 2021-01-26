package com.like.mall.member.feign;


import com.like.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient("mall-order")
public interface OrderFeignService {

    @PostMapping("order/order/listWithItem")
    public R listWithItem(@RequestBody Map<String, Object> params);
}
