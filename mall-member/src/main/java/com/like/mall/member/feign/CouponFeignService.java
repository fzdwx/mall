package com.like.mall.member.feign;

import com.like.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author like
 * @since 2020-10-25 14:54
 * 远程调用 Coupon 服务中的方法
 */
@FeignClient("mall-coupon") // 要调用远程服务
public interface CouponFeignService {

    /**
     * 远程调用coupon 中的方法，调用地址为coupon服务中的完整调用地址
     */
    @GetMapping("/coupon/coupon/member/list")
    public R memberCoupons();
}
