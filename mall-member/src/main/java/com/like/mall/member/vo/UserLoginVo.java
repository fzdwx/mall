package com.like.mall.member.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author like
 * @date 2020-12-16 18:53
 * @contactMe 980650920@qq.com
 * @description
 */
@Data
public class UserLoginVo implements Serializable {

    private String loginName;
    private String password;
}
