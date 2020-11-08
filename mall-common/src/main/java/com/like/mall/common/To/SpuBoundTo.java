package com.like.mall.common.To;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author like
 * @since 2020-11-08 14:23
 */
@Data
public class SpuBoundTo {
    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
