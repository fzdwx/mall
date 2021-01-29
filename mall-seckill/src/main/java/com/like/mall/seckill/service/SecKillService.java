package com.like.mall.seckill.service;

import com.like.mall.seckill.vo.SeckillSkuRelationEntity;

import java.util.List;

public interface SecKillService {
    void upload();

    List<SeckillSkuRelationEntity> getCurTimeSkus();


    SeckillSkuRelationEntity getSkuSecKillInfo(String skuId);

    String kill(String killId, String key, Integer num);
}
