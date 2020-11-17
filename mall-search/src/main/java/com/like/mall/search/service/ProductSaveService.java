package com.like.mall.search.service;

import com.like.mall.common.To.es.SkuEsModel;

import java.util.List;

/**
 * @author like
 * @since 2020-11-17 13:17
 */
public interface ProductSaveService {

    boolean productStatusUp(List<SkuEsModel> skuEsModels);
}
