package com.like.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.order.entity.OrderEntity;
import com.like.mall.order.vo.*;

import java.util.Map;

/**
 * 订单
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 13:00:41
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder();

    OrderSubmitRespVo submitOrder(OrderSubmitVo vo);

    void closeOrder(OrderEntity order);

    /**
     * 得到订单支付信息
     *
     * @param orderSn 订单sn
     * @return {@link PayVo}
     */
    PayVo getOrderPay(String orderSn);

    PageUtils queryPageWithItem(Map<String, Object> params);

    Boolean handlerAlipayAsync(PayAsyncVo vo);
}

