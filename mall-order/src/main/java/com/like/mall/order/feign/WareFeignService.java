package com.like.mall.order.feign;

import com.like.mall.common.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author like
 * @date 2021-01-02 19:04
 * @contactMe 980650920@qq.com
 * @description
 */
@FeignClient("mall-ware")
public interface WareFeignService {

    @PostMapping("ware/waresku/lock/order")
    public Boolean orderLockStock(@RequestBody WareSkuLockVo vo);
}
