package com.like.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author like
 * @date 2020-12-31 17:13
 * @contactMe 980650920@qq.com
 * @description 订单确认页需要的数据
 */
@Data
public class OrderConfirmVo {
    // 用户收货地址
    private List<MemberAddrVo> addresses;
    // 所有选中的购物项
    private List<OrderItemVo> items;
    // 订单总额
    private BigDecimal total;
    // 优惠券信息
    private Integer integration;
    // 應付的金額
    private BigDecimal payPrice;
    //    发票记录
    // 防重复令牌
    private String orderToken;
    private int count = 0;

    public Integer getCount() {
        for (OrderItemVo i : items) {
            count += i.getCount();
        }
        return count;
    }

    public BigDecimal getTotal() {
        total = new BigDecimal(0);
        if (items == null) return total;
        for (OrderItemVo i : items) {
            total = total.add(i.getPrice().multiply(BigDecimal.valueOf(i.getCount())));
        }
        return this.total;
    }


    public BigDecimal getPayPrice() {
        return total;
    }
}
