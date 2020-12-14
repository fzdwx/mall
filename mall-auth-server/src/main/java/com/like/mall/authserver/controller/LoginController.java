package com.like.mall.authserver.controller;

import com.like.mall.authserver.feign.SmsFeignService;
import com.like.mall.common.constant.AutoConstant;
import com.like.mall.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author like
 * @date 2020-12-14 18:37
 * @contactMe 980650920@qq.com
 * @description
 */
@RestController
public class LoginController {

    @Autowired
    private SmsFeignService smsFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/sms/sendSms")
    public R sendSms(@RequestParam("mobile") String mobile) {
        // 1.接口防刷
        String s = redisTemplate.opsForValue().get(AutoConstant.SMS_CODE_CACHE_PREFIX + mobile);

        // 60s内不能在次发送
        if (s != null && System.currentTimeMillis() - Long.parseLong(s.split("_")[1]) < 60000) {
            return R.error();
        }

        // 生成验证码
        String code = UUID.randomUUID().toString().substring(0, 5) + "_" + System.currentTimeMillis();

        // 2.验证码再次校验  sms:code:13789983260,12345   10min有效期
        redisTemplate.opsForValue().set(AutoConstant.SMS_CODE_CACHE_PREFIX + mobile, code, 10, TimeUnit.MINUTES);
        return smsFeignService.sendSms(mobile, code.substring(0, 5));
    }

}
