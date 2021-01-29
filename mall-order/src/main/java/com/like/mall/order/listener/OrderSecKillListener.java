package com.like.mall.order.listener;

import com.like.mall.common.To.SecKillOrderTo;
import com.like.mall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author like
 * @date 2021-01-29 17:14
 * @contactMe 980650920@qq.com
 * @description
 */
@Component
@RabbitListener(queues = "order.release.order.queue")
public class OrderSecKillListener {

    @Autowired
    private OrderService orderService;
    @RabbitHandler
    public void listener(SecKillOrderTo sec, Channel channel, Message message) throws IOException {
        orderService.createSecKillOrder(sec);
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
            e.printStackTrace();
        }
    }
}
