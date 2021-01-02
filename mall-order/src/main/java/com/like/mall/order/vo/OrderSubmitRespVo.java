package com.like.mall.order.vo;

import com.like.mall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @author like
 * @date 2021-01-02 17:17
 * @contactMe 980650920@qq.com
 * @description
 */
@Data
public class OrderSubmitRespVo {
    private OrderEntity order;
    private Integer code = 1;  //默认失败， 0 是成功
}
