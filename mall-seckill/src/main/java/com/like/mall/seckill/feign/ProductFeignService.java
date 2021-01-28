package com.like.mall.seckill.feign;

import com.like.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("mall-product")
public interface ProductFeignService {

    @RequestMapping("product/skuinfo/info/{skuId}")
    public R skuInfo(@PathVariable("skuId") Long skuId);
}

