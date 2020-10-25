package com.like.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.like.common.utils.PageUtils;
import com.like.mall.product.entity.CategoryBrandRelationEntity;

import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 10:08:21
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

