package com.like.mall.cart.vo;

import lombok.Data;

/**
 * @author like
 * @date 2020-12-23 21:00
 * @contactMe 980650920@qq.com
 * @description
 */
@Data
public class UserInfo {
    private Long userId;
    private String userKey;

    private boolean temp = false;
}
