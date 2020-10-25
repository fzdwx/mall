package com.like.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.like.common.utils.PageUtils;
import com.like.mall.product.entity.SpuCommentEntity;

import java.util.Map;

/**
 * 商品评价
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 10:08:21
 */
public interface SpuCommentService extends IService<SpuCommentEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

