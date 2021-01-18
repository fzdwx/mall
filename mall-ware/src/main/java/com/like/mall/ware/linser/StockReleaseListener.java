package com.like.mall.ware.linser;

import com.like.mall.common.To.mq.OrderTo;
import com.like.mall.common.To.mq.StockLocked;
import com.like.mall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author like
 * @date 2021-01-05 18:55
 * @contactMe 980650920@qq.com
 * @description
 */
@Service
@Slf4j
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {
    @Resource
    WareSkuService wareSkuService;

    @RabbitHandler
//    @RabbitListener(queues = "stock.release.stock.queue")
    public void handlerStockLockedRelease(StockLocked info, Message message, Channel channel) throws IOException {
        log.info("收到库存解锁请求,需要解锁的skuId:" + info.getSkuId() + "数量：" + info.getSkuNum());
        try {
            wareSkuService.unLock(info);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            log.error("结束库存出现异常{}"+e.getMessage());
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

    @RabbitHandler
    public void handlerOrderCloseRelease(OrderTo info, Message message, Channel channel) throws IOException {
        log.info("收到订单关闭解锁请求,需要解锁的订单号:" + info.getOrderSn());
        try {
            wareSkuService.unLockStock(info);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
}
