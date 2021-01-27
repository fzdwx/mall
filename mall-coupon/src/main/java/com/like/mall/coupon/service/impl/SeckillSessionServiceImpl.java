package com.like.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.coupon.dao.SeckillSessionDao;
import com.like.mall.coupon.entity.SeckillSessionEntity;
import com.like.mall.coupon.entity.SeckillSkuRelationEntity;
import com.like.mall.coupon.service.SeckillSessionService;
import com.like.mall.coupon.service.SeckillSkuRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("seckillSessionService")
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    SeckillSkuRelationService seckillSkuRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SeckillSessionEntity> page = this.page(
                new Query<SeckillSessionEntity>().getPage(params),
                new QueryWrapper<SeckillSessionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SeckillSessionEntity> getSeckillSession() {
        List<SeckillSessionEntity> data = list(new QueryWrapper<SeckillSessionEntity>().between("start_time", startTime(), endTime()));
        if (data == null || data.size() <= 0) return null;

        List<Long> ids = data.stream().map(SeckillSessionEntity::getId).collect(Collectors.toList());

        Map<Long, List<SeckillSkuRelationEntity>> idToRelationMap = seckillSkuRelationService
                .list(new QueryWrapper<SeckillSkuRelationEntity>()
                        .in("promotion_session_id", ids))
                .stream()
                .collect(Collectors.groupingBy(SeckillSkuRelationEntity::getPromotionId));

        for (SeckillSessionEntity seckill : data) {
            seckill.setRelationSku(idToRelationMap.get(seckill.getId()));
        }
        return data;
    }

    private String endTime() {
        return LocalDateTime.of(LocalDate.now().plusDays(2), LocalTime.now()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private String startTime() {
        return LocalDateTime.of(LocalDate.MIN, LocalTime.now()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

}