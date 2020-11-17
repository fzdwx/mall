package com.like.mall.product.feign;

import com.like.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author like
 * @since 2020-11-17 12:55
 */
@FeignClient("mall-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/hasStock")
    public R skuHasStock(@RequestBody List<Long> skuIds);
}
