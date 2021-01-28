package com.like.mall.seckill.service;

import com.alibaba.fastjson.JSON;
import com.like.mall.common.utils.R;
import com.like.mall.common.vo.SkuInfoEntity;
import com.like.mall.seckill.feign.CouponFeignService;
import com.like.mall.seckill.feign.ProductFeignService;
import com.like.mall.seckill.vo.SeckillSessionEntity;
import com.like.mall.seckill.vo.SeckillSkuRelationEntity;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
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
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:"; // + 商品随机码
    private final CouponFeignService couponFeignService;
    private final StringRedisTemplate redisTemplate;
    private final ProductFeignService productFeignService;
    private final RedissonClient redissonClient;

    public SecKillServiceImpl(CouponFeignService couponFeignService, StringRedisTemplate redisTemplate, ProductFeignService productFeignService, RedissonClient redissonClient) {
        this.couponFeignService = couponFeignService;
        this.redisTemplate = redisTemplate;
        this.productFeignService = productFeignService;
        this.redissonClient = redissonClient;
    }

    @Override
    public void upload() {
        // 1.掃描最近三天需要參加秒殺活動的商品
        List<SeckillSessionEntity> list = couponFeignService.getLast3DaySession();

        // 2.缓存活动信息到redis
        saveSessionInfos(list);

        // 3.缓存活动的关联商品信息
        saveSessionSkuInfos(list);

    }

    @Override
    public List<SeckillSkuRelationEntity> getCurTimeSkus() {
        long now = new Date().getTime();
        Set<String> keys = redisTemplate.keys(Session_CACHE_PREFIX + "*");
        if (keys == null || keys.size() <= 0) return null;

        for (String key : keys) {
            String replace = key.replace(Session_CACHE_PREFIX, "");
            String[] s = replace.split("_");
            long start = Long.parseLong(s[0]);
            long end = Long.parseLong(s[1]);
            if (now >= start && now <= end) {
                // 查询当前场次
                List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                BoundHashOperations<String, Object, Object> ops =
                        redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
                List<Object> data = ops.multiGet(Collections.singleton(range));
                if (data == null) return null;

                return data.stream().map(o -> JSON.parseObject(o.toString(), SeckillSkuRelationEntity.class)).collect(Collectors.toList());
            }
        }
        return null;
    }

    @Override
    public SeckillSkuRelationEntity getSkuSecKillInfo(String skuId) {
        BoundHashOperations<String, String, String> ops =
                redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
        Set<String> keys = ops.keys();
        if (keys == null || keys.size() <= 0) return null;
        for (String key : keys) {
            String regx = "\\d_" + skuId;
            if (Pattern.matches(regx, key)) {
                String s = ops.get(key);
                SeckillSkuRelationEntity sec = JSON.parseObject(s, SeckillSkuRelationEntity.class);
                long now = new Date().getTime();
                long start = sec.getStartTime();
                long end = sec.getEndTime();
                if (now >= start && now <= end) {

                } else {
                    sec.setRandomCode(null);
                }
                return sec;
            }
        }

        return null;

    }

    private void saveSessionInfos(List<SeckillSessionEntity> list) {
        list.forEach(s -> {
            long start = s.getStartTime().getTime();
            long end = s.getStartTime().getTime();
            String key = Session_CACHE_PREFIX + start + "_" + end;
            Boolean aBoolean = redisTemplate.hasKey(key);
            if (aBoolean != null && aBoolean) return;
            List<String> skuIds = s.getRelationSku().stream().map(sv -> sv.getSkuId().toString()).collect(Collectors.toList());

            redisTemplate.opsForList().leftPushAll(key, skuIds);
        });
    }

    private void saveSessionSkuInfos(List<SeckillSessionEntity> list) {
        for (SeckillSessionEntity s : list) {
            BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            for (SeckillSkuRelationEntity sr : s.getRelationSku()) {
                Boolean aBoolean = redisTemplate.hasKey(sr.getId().toString());
                if (aBoolean != null && aBoolean) return;

                // 1,sku基本信息
                R r = productFeignService.skuInfo(sr.getSkuId());
                if (r.getCode() == 0) {
                    SkuInfoEntity skuInfo = ((SkuInfoEntity) r.get("skuInfo"));
                    sr.setSkuInfo(skuInfo);
                }
                // 2.设置秒杀时间范围
                sr.setStartTime(s.getStartTime().getTime());
                sr.setEndTime(s.getEndTime().getTime());
                // 3.随机码
                String randomCode = UUID.randomUUID().toString();
                sr.setRandomCode(randomCode);
                // 4.redisson 信号量  - 限流
                RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
                semaphore.trySetPermits(sr.getSeckillCount().intValue());

                ops.put(sr.getId().toString(), JSON.toJSONString(sr));
            }
        }
    }
}
