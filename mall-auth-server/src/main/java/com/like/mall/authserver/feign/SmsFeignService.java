package com.like.mall.authserver.feign;

import com.like.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author like
 * @date 2020-12-14 20:51
 * @contactMe 980650920@qq.com
 * @description
 */
@FeignClient("mall-party")
public interface SmsFeignService {

    @GetMapping("/sms/sendSms")
    public R sendSms(@RequestParam("mobile") String mobile, @RequestParam("code") String code);
}
