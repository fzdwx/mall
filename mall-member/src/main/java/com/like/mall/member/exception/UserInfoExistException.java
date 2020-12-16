package com.like.mall.member.exception;

import com.like.mall.member.entity.MemberEntity;

/**
 * @author like
 * @date 2020-12-15 19:15
 * @contactMe 980650920@qq.com
 * @description
 */
public class UserInfoExistException extends RuntimeException {
    public UserInfoExistException(MemberEntity member) {
        super(check(member));
    } 

    public static String check(MemberEntity member) {
        String res = "";
        if (member.getUsername() != null) {
            res = "用户名已经存在";
        } else if (member.getMobile() != null) {
            res = "手机号码已经存在";
        } else if (member.getEmail() != null) {
            res = "邮箱已经存在";
        }
        return res;
    }
}

