package com.like.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.like.common.utils.PageUtils;
import com.like.mall.product.entity.SpuInfoDescEntity;

import java.util.Map;

/**
 * spu信息介绍
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 10:08:20
 */
public interface SpuInfoDescService extends IService<SpuInfoDescEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

