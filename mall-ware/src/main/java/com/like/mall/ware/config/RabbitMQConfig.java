package com.like.mall.ware.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author like
 * @date 2020-12-30 14:25
 * @contactMe 980650920@qq.com
 * @description
 */
@Configuration
public class RabbitMQConfig {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @PostConstruct
    public void initRabbitTemplate() {
        // 设置确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("confirm{correlationData:" + correlationData + ",ack:" + ack + ",cause:" + cause);
            }
        });

        //  设置消息抵达队列回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 只要消息没有投递给指定的队列，就会触发这个失败回调
             *
             * @param message    消息
             * @param replyCode  回复代码
             * @param replyText  回复文本
             * @param exchange   交换
             * @param routingKey 路由的关键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("Fail Message{" + message + "},replyCode:" + replyCode + ",replyText{" + replyText + "},exchange:[" + exchange + "],routingKey:[" + routingKey + "]");
            }
        });
    }
}
