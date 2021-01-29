package com.like.mall.seckill.controller;

import com.like.mall.common.utils.R;
import com.like.mall.seckill.service.SecKillService;
import com.like.mall.seckill.vo.SeckillSkuRelationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author like
 * @date 2021-01-28 13:09
 * @contactMe 980650920@qq.com
 * @description
 */
@RestController
public class SecKillController {

    @Autowired
    private SecKillService secKillService;

    @GetMapping("curtimeSecKillSku")
    public R getCurTime() {
        List<SeckillSkuRelationEntity> data = secKillService.getCurTimeSkus();
        return R.ok().put("data", data);
    }

    @GetMapping("/sku/secKill/{skuId}")
    public R getSkuSecKillInfo(@PathVariable String skuId) {
        SeckillSkuRelationEntity s = secKillService.getSkuSecKillInfo(skuId);
        return R.ok().put("to", s);
    }

    public R secKill(@RequestParam("killId") String killId,
                     @RequestParam("key") String key,
                     @RequestParam("num") Integer num) {
        String orderSn =  secKillService.kill(killId,key,num);
        return R.ok();

    }
}
