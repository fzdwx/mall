package com.like.mall.seckill.service;

import com.alibaba.fastjson.JSON;
import com.like.mall.seckill.feign.CouponFeignService;
import com.like.mall.seckill.vo.SeckillSessionEntity;
import com.like.mall.seckill.vo.SeckillSkuRelationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author like
 * @date 2021-01-27 18:34
 * @contactMe 980650920@qq.com
 * @description
 */
@Service
public class SecKillServiceImpl implements SecKillService {

    private final static String Session_CACHE_PREFIX = "seckill:session";
    private final static String SKUKILL_CACHE_PREFIX = "seckill_skus";
    @Autowired
    CouponFeignService couponFeignService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void upload() {
        // 1.掃描最近三天需要參加秒殺活動的商品
        List<SeckillSessionEntity> list = couponFeignService.getLast3DaySession();

        // 2.缓存活动信息到redis
        saveSessionInfos(list);

        // 3.缓存活动的关联商品信息
        saveSessionSkuInfos(list);

    }

    private void saveSessionInfos(List<SeckillSessionEntity> list) {
        list.forEach(s -> {
            Long start = s.getStartTime().getTime();
            long end = s.getStartTime().getTime();
            String key = Session_CACHE_PREFIX + start + "_" + end;
            List<String> skuIds = s.getRelationSku().stream().map(sv -> sv.getSkuId().toString()).collect(Collectors.toList());
            redisTemplate.opsForList().leftPushAll(key, skuIds);
        });
    }

    private void saveSessionSkuInfos(List<SeckillSessionEntity> list) {
        for (SeckillSessionEntity s : list) {
            BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            for (SeckillSkuRelationEntity sr : s.getRelationSku()) {
                sr.setSkuInfoEntity();
                ops.put(sr.getId(), JSON.toJSONString(sr));
            }
        }
    }
}
