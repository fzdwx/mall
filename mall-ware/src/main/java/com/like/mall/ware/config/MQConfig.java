package com.like.mall.ware.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

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

    @RabbitListener(queues = "stock.release.stock.queue")
    public void handler(Message message) {

    }

    @Bean  // 库存服务的交换机
    public Exchange stockExchange() {
        System.out.println("创建交换机");
        // String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        return new TopicExchange("stock-event-exchange", true, false);
    }

    @Bean //创建一个死信队列
    public Queue stockDelayQueue() {
        System.out.println("创建队列");
        // String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "stock-event-exchange");
        arguments.put("x-dead-letter-routing-key", "stock.release");
        arguments.put("x-message-ttl", 120000);
        return new Queue("stock.delay.queue", true, false, false, arguments);
    }

    @Bean  // 普通队列
    public Queue stockReleaseStockQueue() {
        System.out.println("创建队列");
        return new Queue("stock.release.stock.queue", true, false, false);
    }

    @Bean // release
    public Binding stockReleaseBinding() {
        System.out.println("创建绑定");
        return new Binding(
                "stock.release.stock.queue", Binding.DestinationType.QUEUE,
                "stock-event-exchange", "stock.release.#", null);
    }


    @Bean // 库存锁定绑定
    public Binding stockLockedBinding() {
        System.out.println("创建绑定");
        return new Binding(
                "stock.delay.queue", Binding.DestinationType.QUEUE,
                "stock-event-exchange", "stock.locked", null);
    }
}
