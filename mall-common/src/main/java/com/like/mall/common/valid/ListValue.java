package com.like.mall.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * @author  like
 * @date 2020/11/01
 * 自定义JSR303校验注解
 */
@Documented
@Constraint(validatedBy = { ListValueConstraint.class}) // 通过什么校验器进行校验
@Target({METHOD,FIELD,ANNOTATION_TYPE,CONSTRUCTOR,PARAMETER,TYPE_USE}) // 可以标注在哪些属性上
@Retention(RUNTIME)
public @interface ListValue {

    // 错误信息
    String message() default "{com.like.mall.valid.common.ListValue.message}";
    // 校验分组
    Class<?>[] groups() default {};
    //
    Class <? extends Payload> [] payload() default{};

    int[] ints() default { };
}

