package com.like.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.To.SkuReductionTo;
import com.like.mall.common.To.SpuBoundTo;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.common.utils.R;
import com.like.mall.product.dao.SpuInfoDao;
import com.like.mall.product.entity.*;
import com.like.mall.product.feign.CouponFeignService;
import com.like.mall.product.service.*;
import com.like.mall.product.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    @Transactional
    public void saveSpuInfo(SpuSaveVo vo) {
        // 1.保存spu基本信息 spu_info
        SpuInfoEntity spuInfo = new SpuInfoEntity();
        BeanUtils.copyProperties(vo, spuInfo);
        spuInfo.setCreateTime(new Date());
        spuInfo.setUpdateTime(new Date());
        this.saveBaseSpuInfo(spuInfo);

        // 2.保存spu描述图片 spu_info_desc
        List<String> decript = vo.getDecript();
        SpuInfoDescEntity spuInfoDesc = new SpuInfoDescEntity();
        spuInfoDesc.setSpuId(spuInfo.getId());
        spuInfoDesc.setDecript(String.join(",", decript));
        spuInfoDescService.saveSpuInfoDesc(spuInfoDesc);

        // 3.保存spu图片集   spu_images
        List<String> images = vo.getImages();
        spuImagesService.saveImages(spuInfo.getId(), images);

        // 4.保存spu规格参数 product_attr_value
        List<BaseAttrs> baseAttrs = vo.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream()
                .map(i -> {
                    AttrEntity attr = attrService.getById(i.getAttrId());
                    return ProductAttrValueEntity.builder()
                            .attrId(i.getAttrId())
                            .attrName(attr.getAttrName())
                            .attrValue(i.getAttrValues())
                            .quickShow(i.getShowDesc())
                            .spuId(spuInfo.getId())
                            .build();
                }).collect(Collectors.toList());
        productAttrValueService.saveProductAttrValueEntities(productAttrValueEntities);

        // 5.保存spu的积分信息  跨库：  mall-sale spu_bounds
        Bounds bounds = vo.getBounds();
        // product 要给 coupon 发送数据 -> To:SpuBoundTo,创建于公共包common中
        SpuBoundTo spuBoundTo = new SpuBoundTo();
        BeanUtils.copyProperties(bounds, spuBoundTo);
        spuBoundTo.setSpuId(spuInfo.getId());
        R saveSpuBounds = couponFeignService.saveSpuBounds(spuBoundTo);
        if (saveSpuBounds.getCode() != 0) {
            log.error("远程调用coupon服务失败{}(saveSpuBounds):" + spuBoundTo);
        }

        // 6.保存spu对应的sku信息
        List<Skus> skus = vo.getSkus();
        if (!skus.isEmpty()) {
            skus.forEach(sku -> {
                // - sku基本信息 sku_info
                SkuInfoEntity skuInfo = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfo);
                skuInfo.setBrandId(spuInfo.getBrandId());
                skuInfo.setCatalogId(spuInfo.getCatalogId());
                skuInfo.setSaleCount(0L);
                skuInfo.setSpuId(spuInfo.getId());
                String defaultImg = "";
                for (Images image : sku.getImages()) {
                    if (image.getDefaultImg() == 1) {
                        defaultImg = image.getImgUrl();
                    }
                }
                skuInfo.setSkuDefaultImg(defaultImg);
                skuInfoService.saveSkuInfo(skuInfo);

                // - sku图片信息 sku_images
                Long skuId = skuInfo.getSkuId();
                List<SkuImagesEntity> skuImages = sku.getImages().stream()
                        .map(i -> {
                            SkuImagesEntity skuImage = new SkuImagesEntity();
                            skuImage.setSkuId(skuId);
                            skuImage.setImgUrl(i.getImgUrl());
                            skuImage.setDefaultImg(i.getDefaultImg());
                            return skuImage;
                        }).filter(e -> !e.getImgUrl().isEmpty())
                        .collect(Collectors.toList());
                skuImagesService.saveBatch(skuImages);

                // - sku销售属性信息 sku_sal_attr_value
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = sku.getAttr().stream().map(a -> {
                    SkuSaleAttrValueEntity skuSaleAttrValue = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, skuSaleAttrValue);
                    skuSaleAttrValue.setSkuId(skuId);
                    return skuSaleAttrValue;
                }).collect(Collectors.toList());
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntities);

                // - sku优惠满减信息 跨库：mall-sale sms_sku_ladder sms_sku_full_reduction sms_spu_bounds
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() <= 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) > 0) {
                    R saveSkuReduction = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (saveSkuReduction.getCode() != 0) {
                        log.error("远程调用coupon服务失败{}(saveSkuReduction):" + skuReductionTo);
                    }
                }

            });
        }
    }

    @Override
    public void saveBaseSpuInfo(SpuInfoEntity spuInfo) {
        baseMapper.insert(spuInfo);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> query = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (StringUtils.isNotBlank(key)) {
            query.and(q -> {
                q.eq("id", key).or().like("spu_name", key);
            });
        }
        queryCondition(query, params, "status", "publish_status");
        queryCondition(query, params, "branId", "brand_id");

        queryCondition(query, params, "catelogId", "catalog_id");

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                query
        );
        return new PageUtils(page);

    }

    public void queryCondition(QueryWrapper<SpuInfoEntity> query, Map<String, Object> params, String key, String colName) {
        String status = (String) params.get(key);
        if (StringUtils.isNotBlank(status)) {
            query.and(q -> {
                q.eq(colName, status);
            });
        }
    }

    @Resource
    SpuInfoDescService spuInfoDescService;
    @Resource
    SpuImagesService spuImagesService;
    @Resource
    AttrService attrService;
    @Resource
    ProductAttrValueService productAttrValueService;
    @Resource
    SkuInfoService skuInfoService;
    @Resource
    SkuImagesService skuImagesService;
    @Resource
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Resource
    CouponFeignService couponFeignService;
}