package com.like.mall.seckill.feign;

import com.like.mall.seckill.vo.SeckillSessionEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient("mall-coupon")
public interface CouponFeignService {

    @GetMapping("coupon/seckillsession/lates3DaySession")
    public List<SeckillSessionEntity> getLast3DaySession();
}
