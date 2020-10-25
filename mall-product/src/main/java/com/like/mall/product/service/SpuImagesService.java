package com.like.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.like.common.utils.PageUtils;
import com.like.mall.product.entity.SpuImagesEntity;

import java.util.Map;

/**
 * spu图片
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 10:08:21
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

