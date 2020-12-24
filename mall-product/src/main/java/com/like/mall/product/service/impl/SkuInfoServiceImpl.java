package com.like.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.product.dao.SkuInfoDao;
import com.like.mall.product.entity.SkuImagesEntity;
import com.like.mall.product.entity.SkuInfoEntity;
import com.like.mall.product.entity.SpuInfoDescEntity;
import com.like.mall.product.service.*;
import com.like.mall.product.vo.SkuItemSaleAttrVo;
import com.like.mall.product.vo.SkuItemVo;
import com.like.mall.product.vo.SpuItemAttrGroupVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfo) {
        save(skuInfo);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> query = new QueryWrapper<>();
        // 1、name
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            query.and(q -> {
                q.eq("sku_id", key).or().like("sku_name", key);
            });
        }
        String catalogId = (String) params.get("catelogId");
        if (StringUtils.isNotBlank(catalogId) && !"0".equalsIgnoreCase(catalogId)) {
            query.eq("catalog_id", catalogId);
        }

        queryCondition(query, params, "branId", "brand_id");

        // 2、范围
        rangeMin(params, query);
        rangeMax(params, query);

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                query
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkuBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }



    @Autowired
    ThreadPoolExecutor threadPool;

    @Override
    public SkuItemVo item(Long skuId) {
        SkuItemVo res = new SkuItemVo();
        AtomicReference<Long> spuId = new AtomicReference<>();
        AtomicReference<Long> catalogId = new AtomicReference<>();

        CompletableFuture<SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            // 1.sku基本信息
            SkuInfoEntity skuInfo = getById(skuId);
            res.setSkuInfoEntity(skuInfo);

            spuId.set(skuInfo.getSpuId());
            catalogId.set(skuInfo.getCatalogId());
            return skuInfo;
        }, threadPool);

        CompletableFuture<Void> spuSaleFuture = skuInfoFuture.thenAcceptAsync((r) -> {
            // 3.spu的销售属性
            List<SkuItemSaleAttrVo> saleAttrVo = skuSaleAttrValueService.getSpuId(skuId);
            res.setSaleAttrs(saleAttrVo);
        }, threadPool);

        CompletableFuture<Void> spuDescFuture = skuInfoFuture.thenAcceptAsync((r) -> {
            // 4.spu的介绍
            SpuInfoDescEntity desc = spuInfoDescService.getById(spuId.get());
            res.setDesc(desc);
        }, threadPool);

        CompletableFuture<Void> spuAttrFuture = skuInfoFuture.thenAcceptAsync((r) -> {
            // 5.spu的规格参数
            List<SpuItemAttrGroupVo> groupVos = attrGroupService.getBySpuId(spuId.get(), catalogId.get());
            res.setGroupAttrs(groupVos);
        }, threadPool);

        CompletableFuture<Void> imagesFuture = CompletableFuture.runAsync(() -> {
            // 2.sku的图片信息
            List<SkuImagesEntity> images = skuImagesService.getBySkuId(skuId);
            res.setImages(images);
        }, threadPool);

        try {
            CompletableFuture.allOf(imagesFuture, skuInfoFuture, spuAttrFuture, spuDescFuture, spuSaleFuture).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }
    @Autowired
    SkuImagesService skuImagesService;
    @Autowired
    SpuInfoDescService spuInfoDescService;
    @Autowired
    AttrGroupService attrGroupService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;

    public void queryCondition(QueryWrapper<SkuInfoEntity> query, Map<String, Object> params, String key, String colName) {
        String status = (String) params.get(key);
        if (StringUtils.isNotBlank(status)) {
            query.and(q -> {
                q.eq(colName, status);
            });
        }
    }

    public void rangeMax(Map<String, Object> params, QueryWrapper<SkuInfoEntity> query) {
        String max = (String) params.get("max");
        if (StringUtils.isNotBlank(max)) {
            BigDecimal bigDecimal = new BigDecimal(max);
            if (bigDecimal.compareTo(new BigDecimal(0)) > 0)
                query.le("price", max);
        }
    }

    public void rangeMin(Map<String, Object> params, QueryWrapper<SkuInfoEntity> query) {
        String min = (String) params.get("min");
        if (StringUtils.isNotBlank(min)) {
            BigDecimal bigDecimal = new BigDecimal(min);
            if (bigDecimal.compareTo(new BigDecimal(0)) > 0)
                query.ge("price", min);
        }
    }

}