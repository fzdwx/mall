package com.like.mall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author like
 * @date 2020-12-23 19:52
 * @contactMe 980650920@qq.com
 * @description 购物车内容
 */
@Data
public class Cart {
    List<CartItem> items;
    private Integer countNum;           // 商品数量
    private Integer countType;         // 商品类型的个数
    private BigDecimal totalAmount;   // 当前购物车总价格
    private BigDecimal reduce = new BigDecimal(0);       // 优惠价格

    public Integer getCountNum() {
        int count = 0;
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                count += item.getCount();
            }
        }
        setCountNum(count);
        return count;
    }

    public void setCountNum(Integer countNum) {
        this.countNum = countNum;
    }

    public Integer getCountType() {
        int count = 0;
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                count += 1;
            }
        }
        setCountType(count);
        return countType;
    }

    public void setCountType(Integer countType) {
        this.countType = countType;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal count = new BigDecimal(0);
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                count = count.add(item.getTotalPrice());
            }
        }
        count = count.subtract(reduce);
        setTotalAmount(count);
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
