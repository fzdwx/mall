package com.like.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.product.entity.SpuImagesEntity;

import java.util.List;
import java.util.Map;

/**
 * spu图片
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 11:35:48
 */
public interface SpuImagesService extends IService<SpuImagesEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveImages(Long id, List<String> images);
}

