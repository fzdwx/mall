package com.like.mall.party.controller;

import com.like.mall.common.utils.R;
import com.like.mall.party.component.Sms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author like
 * @date 2020-12-14 20:23
 * @contactMe 980650920@qq.com
 * @description
 */
@RestController
@RequestMapping("/sms")
public class SmsController {
    @Autowired
    private Sms sms;

    /**
     * 发送短信验证码
     *
     * @param mobile 移动
     * @param code   代码
     * @return {@link R}
     */
    @GetMapping("/sendSms")
    public R sendSms(@RequestParam("mobile") String mobile,@RequestParam("code")  String code) {
        sms.sendSms(mobile, code);
        return R.ok();
    }
}
