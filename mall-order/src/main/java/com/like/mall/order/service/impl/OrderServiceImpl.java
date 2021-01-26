package com.like.mall.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.To.mq.OrderTo;
import com.like.mall.common.constant.OrderConstant;
import com.like.mall.common.exception.NoStockException;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.common.vo.MemberVo;
import com.like.mall.common.vo.WareSkuLockVo;
import com.like.mall.order.To.OrderCreateTo;
import com.like.mall.order.dao.OrderDao;
import com.like.mall.order.entity.OrderEntity;
import com.like.mall.order.entity.OrderItemEntity;
import com.like.mall.order.entity.PaymentInfoEntity;
import com.like.mall.order.feign.CartFeignService;
import com.like.mall.order.feign.MemberFeignService;
import com.like.mall.order.feign.WareFeignService;
import com.like.mall.order.service.OrderItemService;
import com.like.mall.order.service.OrderService;
import com.like.mall.order.service.PaymentInfoService;
import com.like.mall.order.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.like.mall.order.interceptor.LoginInterceptor.loginUser;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    CartFeignService cartFeignService;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Autowired
    private PaymentInfoService paymentInfoService;
    @Autowired
    private MemberFeignService memberFeignService;
    @Autowired
    private ThreadPoolExecutor executor;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private OrderItemService orderItemService;
    @Autowired
    private WareFeignService wareFeignService;

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

        // 解决feign异步请求头丢失问题
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        // 远程查询用户的地址
        CompletableFuture<Void> getAddress = CompletableFuture.runAsync(() -> {
            vo.setAddresses(memberFeignService.getUserAddress(user.getId()));
            RequestContextHolder.setRequestAttributes(requestAttributes);
        }, executor);
        // 远程查询获取当前购物项信息
        CompletableFuture<Void> getCartItems = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(requestAttributes);
            vo.setItems(cartFeignService.getUserCartItems());
        }, executor);
        // 设置用户积分
        vo.setIntegration(user.getIntegration());
        try {
            CompletableFuture.allOf(getCartItems, getAddress).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        vo.setTotal(vo.getTotal());
        vo.setPayPrice(vo.getPayPrice());
        vo.setCount(vo.getCount());
        // 防重复令牌
        String token = UUID.randomUUID().toString().substring(0, 5);
        vo.setOrderToken(token);  // 保存到页面
        redisTemplate.opsForValue().set(OrderConstant.orderTokenPrefix + user.getId(), token, 30, TimeUnit.MINUTES); // 保存到redis
        return vo;

    }

    //    @GlobalTransactional // 高并发下不适用，全局锁
    @Override
    @Transactional  // 事务
    public OrderSubmitRespVo submitOrder(OrderSubmitVo vo) {
        OrderSubmitRespVo respVo = new OrderSubmitRespVo();
        MemberVo user = loginUser.get();
        // 创建订单，验证令牌，验证价格，锁库存
        // 1.验证令牌(验证和删除必须保证原子性)
        String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                "    return redis.call(\"del\",KEYS[1])\n" +
                "else\n" +
                "    return 0\n" +
                "end";
        String token = vo.getOrderToken();
        ArrayList<String> list = new ArrayList<>();
        list.add(OrderConstant.orderTokenPrefix + user.getId());
        Long execute = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), list, token);
        if (execute != null && execute == 1) {  // 验证成功 -> 执行业务
            // 1.创建订单
            OrderCreateTo order = createOrder(vo);
            // todo 2.计算价格是否相等
            // 3.保存订单
            saveOrder(order);
            // 4.库存锁定
            WareSkuLockVo lockVo = new WareSkuLockVo();
            lockVo.setOrderSn(order.getOrder().getOrderSn());  // 封装需要锁定的数据
            lockVo.setLocks(order.getOrderItems().stream().map(s -> {
                WareSkuLockVo.OrderItemVo orderItemVo = new WareSkuLockVo.OrderItemVo();
                orderItemVo.setCount(s.getSkuQuantity());
                BeanUtils.copyProperties(s, orderItemVo);
                return orderItemVo;
            }).collect(Collectors.toList()));
            // 调用远程接口
            Boolean lock = wareFeignService.orderLockStock(lockVo);
            if (lock) {
                respVo.setCode(0);
                // todo 5.调用远程服务扣减积分 出现异常
                //                int i = 10 / 0;
            } else { // 锁定失败
                respVo.setCode(1);
                throw new NoStockException(0L);
            }
            respVo.setOrder(order.getOrder()); // 订单创建完成
            // 发送消息
            rabbitTemplate.convertAndSend("order-event-exchange", "order.create.order", order.getOrder());
        }
        return respVo;
    }

    /**
     * 关闭订单
     */
    @Override
    public void closeOrder(OrderEntity order) {
        OrderEntity dbOrder = getById(order.getId()); // 查询最新状态
        if (Objects.equals(dbOrder.getStatus(), OrderConstant.CreateNew)) { // 待付款狀態
            // 1、关闭订单
            OrderEntity o = new OrderEntity();
            o.setId(order.getId());
            o.setStatus(OrderConstant.Cancled);
            updateById(o);
            // 2.发送给mq一个
            OrderTo to = new OrderTo(dbOrder);
            try {
                // TODO: 2021/1/18 保证消息一定可以发送出去
                rabbitTemplate.convertAndSend("order-event-exchange", "order.release.order", to);
            } catch (Exception e) {

            }
        }
    }

    @Override
    public PayVo getOrderPay(String orderSn) {
        OrderEntity order = this.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        List<OrderItemEntity> orderItems = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));

        PayVo payVo = new PayVo();
        payVo.setOut_trade_no(order.getOrderSn());
        payVo.setSubject(orderItems.get(0).getSkuName());
        payVo.setTotal_amount(order.getPayAmount().setScale(2, BigDecimal.ROUND_UP).toString());
        payVo.setBody(order.getNote());

        return payVo;
    }

    @Override
    public PageUtils queryPageWithItem(Map<String, Object> params) {
        MemberVo user = loginUser.get();
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
                        .eq("member_id", user.getId())
                        .orderByDesc("id")
        );
        page.setRecords(
                page.getRecords().stream().map(o -> {
                    o.setItems(orderItemService.list(new QueryWrapper<OrderItemEntity>()
                            .eq("order_sn", o.getOrderSn())));
                    return o;
                }).collect(Collectors.toList()));
        return new PageUtils(page);
    }

    @Override
    public Boolean handlerAlipayAsync(PayAsyncVo vo) {
        // 1.保存流水
        PaymentInfoEntity info = new PaymentInfoEntity();
        info.setOrderSn(vo.getOut_trade_no());
        info.setAlipayTradeNo(vo.getTrade_no());
        info.setPaymentStatus(vo.getTrade_status());
        info.setCallbackTime(new Date(vo.getNotify_time()));
        paymentInfoService.save(info);

        // 2.修改狀態
        if (vo.getTrade_status().equals("TRADE_SUCCESS") || vo.getTrade_status().equals("TRADE_FINISHED")) {
            String orderSn = vo.getOut_trade_no();
            updateOrderStatus(orderSn,OrderConstant.PAYED);
        }
        return true;
    }

    private void updateOrderStatus(String orderSn, Integer payed) {
        baseMapper.updateOrderStatus(orderSn,payed);
    }


    private OrderEntity builderOrder(OrderSubmitVo vo, String orderSn) {
        MemberVo user = loginUser.get();
        OrderEntity order = new OrderEntity();
        order.setOrderSn(orderSn);
        String s = vo.getAddr();
        MemberAddrVo addr = BeanUtil.toBean(s, MemberAddrVo.class);
        order.setReceiverCity(addr.getCity()); // 设置收货人信息
        order.setReceiverDetailAddress(addr.getDetailAddress());
        order.setReceiverName(addr.getName());
        order.setReceiverPostCode(addr.getPostCode());
        order.setReceiverPhone(addr.getPhone());
        order.setReceiverProvince(addr.getProvince());
        order.setStatus(1);
        order.setPayAmount(vo.getPayPrice());
        order.setPayType(vo.getPayType());
        order.setMemberId(user.getId());
        // ···
        return order;
    }

    private OrderItemEntity builderOrderItem(OrderItemVo s, String orderSn) {
        OrderItemEntity i = new OrderItemEntity();
        i.setOrderSn(orderSn);
        i.setSkuId(s.getSkuId());
        i.setSkuName(s.getTitle());
        i.setSkuPic(s.getImage());
        i.setSkuPrice(s.getPrice());
        i.setSkuAttrsVals(StringUtils.collectionToDelimitedString(s.getSkuAttr(), ";"));
        i.setSkuQuantity(s.getCount());
        i.setGiftGrowth(s.getPrice().intValue());
        i.setGiftIntegration(s.getPrice().intValue());
        // spu信息不想寫了
        return i;
    }

    private List<OrderItemEntity> builderOrderItems(String orderSn) {
        List<OrderItemEntity> list = new ArrayList<>();
        List<OrderItemVo> userCartItems = cartFeignService.getUserCartItems();
        if (userCartItems != null && userCartItems.size() > 0) {
            list = userCartItems.stream()
                    .map(s -> builderOrderItem(s, orderSn))
                    .collect(Collectors.toList());
        }
        System.out.println(list);
        return list;
    }

    private OrderCreateTo createOrder(OrderSubmitVo vo) {
        OrderCreateTo to = new OrderCreateTo();
        String orderSn = IdWorker.getTimeId();  // 订单号
        to.setOrder(builderOrder(vo, orderSn));
        // 设置订单中的购物项
        to.setOrderItems(builderOrderItems(orderSn));
        to.setPayPrice(vo.getPayPrice());
        return to;
    }

    private void saveOrder(OrderCreateTo to) {
        OrderEntity order = to.getOrder();
        order.setModifyTime(new Date());
        this.save(order);
        orderItemService.saveBatch(to.getOrderItems());
    }
}