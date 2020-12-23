package com.like.mall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author like
 * @date 2020-12-23 19:53
 * @contactMe 980650920@qq.com
 * @description 购物项
 */
@Data
public class CartItem {

    private Long skuId;
    private Boolean check = true;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice;

    public BigDecimal getTotalPrice() {
        totalPrice = price.multiply(new BigDecimal(count));
        return totalPrice;
    }
}
