package com.like.mall.common.To;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author like
 * @since 2020-11-08 14:50
 */
@Data
public class SkuReductionTo {
    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;
}
