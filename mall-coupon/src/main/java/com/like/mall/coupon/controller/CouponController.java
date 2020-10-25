package com.like.mall.coupon.controller;

import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.R;
import com.like.mall.coupon.entity.CouponEntity;
import com.like.mall.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 优惠券信息
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 12:44:43
 */
@RestController
@RequestMapping("coupon/coupon")
@RefreshScope // 动态刷新config
public class CouponController {
    @Autowired
    private CouponService couponService;
    // 从配置文件中获取
    @Value("${my.name}")
    private String myName;

    @GetMapping("/test/config")
    public R testConfigToNacos() {
        return R
                .ok()
                .put("user", myName);
    }

    /**
     * 测试 member 远程调用方法
     *
     * @return r
     */
    @GetMapping("/member/list")
    public R memberCoupons() {
        return R
                .ok()
                .put("coupons", "这里是coupons");
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = couponService.queryPage(params);

        return R
                .ok()
                .put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        CouponEntity coupon = couponService.getById(id);

        return R
                .ok()
                .put("coupon", coupon);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CouponEntity coupon) {
        couponService.save(coupon);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CouponEntity coupon) {
        couponService.updateById(coupon);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        couponService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
