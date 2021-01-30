package com.like.mall.product.feign;

import com.like.mall.common.utils.R;
import com.like.mall.product.feign.fallback.SecKillFeignServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author like
 * @date 2021-01-28 15:49
 * @contactMe 980650920@qq.com
 * @description
 */
@FeignClient(value = "mall-seckill",fallback = SecKillFeignServiceFallBack.class)
public interface SecKillFeignService {
    @GetMapping("/sku/secKill/{skuId}")
    public R getSkuSecKillInfo(@PathVariable String skuId);
}
