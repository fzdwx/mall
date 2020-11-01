package com.like.mall.common.valid;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @author like
 * @since 2020-11-01 9:43
 * ListValue的校验器
 * 思路：
 * 判断传入的值是否是ints里面的
 */

public class ListValueConstraint implements ConstraintValidator<ListValue, Integer> {

    private Set<Integer> set = new HashSet<Integer>();

    @Override
    public void initialize(ListValue constraintAnnotation) {
        // 添加到集合中
        int[] ints = constraintAnnotation.ints();
        for (int i : ints) {
            set.add(i);
        }

    }
    /**
     * 执行校验
     * @param value 需要校验的值
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return set.contains(value);
    }
}
