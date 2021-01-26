package com.like.mall.order.listener;

import com.like.mall.order.service.OrderService;
import com.like.mall.order.vo.PayAsyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author like
 * @date 2021-01-26 16:24
 * @contactMe 980650920@qq.com
 * @description
 */
@RestController
public class OrderPayedListener {

    @Autowired
    OrderService orderService;

    /**
     * 表示收到了支付宝发送的异步通知，返回success。
     */
    @PostMapping("/pay/notify")
    public String handlerAlipay(PayAsyncVo vo, HttpServletRequest request) {
        // 1.驗證是否是支付寶發送的

        Boolean statusCode = orderService.handlerAlipayAsync(vo);
        return statusCode ?"success":"no";
    }
}
