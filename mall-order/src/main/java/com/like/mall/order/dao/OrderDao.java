package com.like.mall.order.dao;

import com.like.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 13:00:41
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
