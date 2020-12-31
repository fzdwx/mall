package com.like.mall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.common.vo.MemberVo;
import com.like.mall.order.dao.OrderDao;
import com.like.mall.order.entity.OrderEntity;
import com.like.mall.order.feign.CartFeignService;
import com.like.mall.order.feign.MemberFeignService;
import com.like.mall.order.service.OrderService;
import com.like.mall.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.like.mall.order.interceptor.LoginInterceptor.loginUser;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    CartFeignService cartFeignService;
    @Autowired
    private MemberFeignService memberFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() {
        MemberVo user = loginUser.get();
        OrderConfirmVo vo = new OrderConfirmVo();
        // 远程查询用户的地址
        vo.setAddresses(memberFeignService.getUserAddress(user.getId()));
        // 远程查询获取当前购物项信息
        vo.setItems(cartFeignService.getUserCartItems());
        // 设置用户积分
        vo.setIntegration(user.getIntegration());

        vo.setTotal(vo.getTotal());
        vo.setPayPrice(vo.getPayPrice());

        // TODO: 2020/12/31    防重复令牌
        return vo;

    }
}