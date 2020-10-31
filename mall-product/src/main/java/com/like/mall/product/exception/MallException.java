package com.like.mall.product.exception;

import com.like.mall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author like
 * @since 2020-10-31 8:35
 */
@Slf4j
@RestControllerAdvice
public class MallException {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidationError(MethodArgumentNotValidException e) {
        log.error("数据校验出现问题：", e.getMessage(), e.getClass());
        Map<String, String> map = new HashMap<>();
        e
                .getBindingResult()
                .getFieldErrors()
                .forEach(fieldError -> {
                    map.put(fieldError.getField(), fieldError.getDefaultMessage());
                });
        return R.error(400,"数据校验出现问题").put("data",map);
    }

    @ExceptionHandler
    public R handleException(Throwable e) {
        return  R.error();
    }
}
