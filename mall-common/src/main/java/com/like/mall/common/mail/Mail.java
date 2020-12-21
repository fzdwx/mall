package com.like.mall.common.mail;

import cn.hutool.extra.mail.MailUtil;
import org.springframework.stereotype.Component;

/**
 * @author like
 * @date 2020-12-21 15:55
 * @contactMe 980650920@qq.com
 * @description
 */
@Component
public class Mail {

    public static void sendMail(String to, String title, String content) {
        MailUtil.send(to, title, content, false);
    }
}
