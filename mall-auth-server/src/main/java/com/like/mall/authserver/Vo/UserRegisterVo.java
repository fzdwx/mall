package com.like.mall.authserver.Vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author like
 * @date 2020-12-15 18:20
 * @contactMe 980650920@qq.com
 * @description
 */
@Data
public class UserRegisterVo {

    @NotEmpty(message = "用户名不能为空")
    @Length(min = 6,max = 18,message = "用户名必须在6-18位之间")
    private String username;
    @NotEmpty(message = "密码不能为空")
    @Length(min = 6,max = 18,message = "密码必须在6-18位之间")
    private String password;
    @NotEmpty(message = "手机号不能为空")
    @Pattern(regexp = "^[1][0-9]{10}$",message = "手机号格式不正确")
    private String mobile;
    @NotEmpty(message = "验证码不能为空")
    private String code;
}
