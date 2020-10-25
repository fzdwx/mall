package com.like.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.coupon.entity.SeckillSkuNoticeEntity;

import java.util.Map;

/**
 * 秒杀商品通知订阅
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 12:44:42
 */
public interface SeckillSkuNoticeService extends IService<SeckillSkuNoticeEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

