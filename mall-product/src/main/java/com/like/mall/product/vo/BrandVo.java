package com.like.mall.product.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author like
 * @since 2020-11-08 10:55
 */
@Data
@Builder
public class BrandVo {

    private Long brandId;
    private String brandName;
}
