package com.like.mall.product.vo;

import com.like.mall.product.entity.SkuImagesEntity;
import com.like.mall.product.entity.SkuInfoEntity;
import com.like.mall.product.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @author like
 * @date 2020-12-12 17:41
 * @contactMe 980650920@qq.com
 * @description
 */
@Data
public class SkuItemVo {

    private SkuInfoEntity skuInfoEntity;

    private List<SkuImagesEntity> images;

    private SpuInfoDescEntity desc;

    private List<SpuItemAttrGroupVo> groupAttrs;
    private List<SkuItemSaleAttrVo> saleAttrs;

    SeckillSkuRelationEntity seckillInfo;
}


