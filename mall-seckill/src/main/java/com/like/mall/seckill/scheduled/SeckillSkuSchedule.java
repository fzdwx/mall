package com.like.mall.seckill.scheduled;

import com.like.mall.seckill.service.SecKillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author like
 * @date 2021-01-27 18:28
 * @contactMe 980650920@qq.com
 * @description 秒殺商品的定時上架
 * 每天晚上3點：上架最近三天需要秒殺的商品
 * 當天00：00：00 - 23：59：59
 * 明天00：00：00 - 23：59：59
 * 后天00：00：00 - 23：59：59
 */
@Service
@Slf4j
public class SeckillSkuSchedule {

    @Scheduled(cron = "0 0 3 * * ?")
    public void upload() {
        // 1.重複上架無需處理
        secKillService.upload();
    }
    @Autowired
    SecKillService secKillService;
}
