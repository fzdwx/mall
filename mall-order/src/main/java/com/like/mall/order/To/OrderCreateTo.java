package com.like.mall.order.To;

import com.like.mall.order.entity.OrderEntity;
import com.like.mall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author like
 * @date 2021-01-02 17:36
 * @contactMe 980650920@qq.com
 * @description
 */
@Data
public class OrderCreateTo {
    private OrderEntity order;
    private List<OrderItemEntity> orderItems;

    private BigDecimal payPrice;
}
