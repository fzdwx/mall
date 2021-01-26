package com.like.mall.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.R;
import com.like.mall.order.entity.OrderEntity;
import com.like.mall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 订单
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 13:00:41
 */
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/listWithItem")
    public R listWithItem(@RequestParam Map<String, Object> params) {
        // 1.查询当前登录用户的所有订单
        PageUtils pageUtils= orderService.queryPageWithItem(params);
        return  R.ok().put("page",pageUtils);
    }

    @GetMapping("/status/{orderSn}")
    public Integer getOrderStatus(@PathVariable String orderSn) {
        OrderEntity order = orderService.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return order == null ? null : order.getStatus();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody OrderEntity order) {
        orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody OrderEntity order) {
        orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
