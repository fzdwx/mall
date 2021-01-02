package com.like.mall.order.vo;


import lombok.Data;

import java.math.BigDecimal;

/**
 * 封装页面提交的数据
 */
@Data
public class OrderSubmitVo {
    private String addr; // 收货地址的id
    private Integer payType; // 支付方式
    // 无需提交需要购买的商品，去购物车在获取一次
    // 优惠、发票···
    private String orderToken; //防重令牌
    private BigDecimal payPrice; // 应付价格，可以添加验价功能
    // 用户相关信息，session中获取
}
