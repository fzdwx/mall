package com.like.mall.order.config;

import com.like.mall.order.entity.OrderEntity;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author like
 * @date 2021-01-04 17:49
 * @contactMe 980650920@qq.com
 * @description
 */
@Component
@Slf4j
public class MQConfig {

    @RabbitListener(queues = "order.release.order.queue")
    public void listener(OrderEntity order, Channel channel, Message message) throws IOException {
      log.error("收到过期的订单信息：准备关闭订单{}"+order.getOrderSn());
      channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

    @Bean //创建一个死信队列
    public Queue orderDelayQueue() {
        System.out.println("创建队列");
        // String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "order-event-exchange");
        arguments.put("x-dead-letter-routing-key", "order.release.order");
        arguments.put("x-message-ttl", 60000);
        return new Queue("order.delay.queue", true, false, false, arguments);
    }

    @Bean  // 普通队列
    public Queue orderReleaseOrderQueue() {
        System.out.println("创建队列");
        return new Queue("order.release.order.queue", true, false, false);
    }

    @Bean  // 普通交换机
    public Exchange orderEventExchange() {
        System.out.println("创建交换机");
        // String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        return new TopicExchange("order-event-exchange", true, false);
    }

    @Bean  // 绑定队列和交换机，当key为order.create.order就发送到对应队列
    public Binding orderCreateOrderBinding() {
        System.out.println("创建绑定");
        return new Binding(
                "order.delay.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange", "order.create.order", null);
    }

    @Bean
    public Binding orderReleaseOrderBinding() {
        System.out.println("创建绑定");
        return new Binding(
                "order.release.order.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange", "order.release.order", null);
    }

    /**
     * 訂單釋放直接和庫存釋放進行綁定
     */
    public Binding orderReleaseOtherBinding() {
        System.out.println("创建绑定");
        return new Binding(
                "stock.release.stock.queue", Binding.DestinationType.QUEUE,
                "order-event-exchange", "order.release.other.#", null);
    }


}
