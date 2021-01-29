package com.like.mall.common.To;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author like
 * @date 2021-01-29 17:00
 * @contactMe 980650920@qq.com
 * @description
 */
@Data
public class SecKillOrderTo {

    private String orderSn;
    private Long promotionSessionId;
    private Long skuId;
    private BigDecimal seckillPrice;
    private Integer num;
    private Long memberId;
}
