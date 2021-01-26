package com.like.mall.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.like.mall.order.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 13:00:41
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    void updateOrderStatus(@Param("orderSn") String orderSn, @Param("payed") Integer payed);
}
