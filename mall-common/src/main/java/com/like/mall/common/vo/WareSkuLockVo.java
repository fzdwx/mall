package com.like.mall.common.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author like
 * @date 2021-01-02 18:58
 * @contactMe 980650920@qq.com
 * @description
 */
@Data
public class WareSkuLockVo {
    private String orderSn;
    private List<OrderItemVo> locks;

    @Data
    public static class OrderItemVo {
        private Long skuId;
        private String title;
        private String image;
        private List<String> skuAttr;
        private BigDecimal price;
        private Integer count;
        private BigDecimal totalPrice;
    }

}

