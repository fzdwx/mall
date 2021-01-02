package com.like.mall.ware.exception;

/**
 * @author like
 * @date 2021-01-02 19:31
 * @contactMe 980650920@qq.com
 * @description
 */
public class NoStockException extends RuntimeException {

    public NoStockException(Long skuId) {
        super("没有足够的库存："+skuId);
    }
}
