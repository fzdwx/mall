package com.like.mall.product.feign;

import com.like.mall.common.To.SkuReductionTo;
import com.like.mall.common.To.SpuBoundTo;
import com.like.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author like
 * @since 2020-11-08 14:19
 * 调用远程优惠服务
 */
@FeignClient("mall-coupon")
public interface CouponFeignService {

    /**
     * 调用远程coupon服务保存spu积分信息
     *
     * @param spuBoundTo spu积分TO
     * @return {@link R}
     */
    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundTo spuBoundTo);


    /**
     * 保存sku
     *
     * @param skuReductionTo sku优惠信息
     * @return {@link R}
     */
    @PostMapping("/coupon/skufullreduction/saveinfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
