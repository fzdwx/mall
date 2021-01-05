package com.like.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.To.SkuReductionTo;
import com.like.mall.common.To.SkuStockVo;
import com.like.mall.common.To.SpuBoundTo;
import com.like.mall.common.To.es.SkuEsModel;
import com.like.mall.common.constant.ProductConstant;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.common.utils.R;
import com.like.mall.product.dao.SpuInfoDao;
import com.like.mall.product.entity.*;
import com.like.mall.product.feign.CouponFeignService;
import com.like.mall.product.feign.EsFeignService;
import com.like.mall.product.feign.WareFeignService;
import com.like.mall.product.service.*;
import com.like.mall.product.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
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

//    @GlobalTransactional // seata AT 分布式事务
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

        String catalogId = (String) params.get("catalogId");
        if (!"0".equalsIgnoreCase(catalogId))
            queryCondition(query, params, "catelogId", "catalog_id");

        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                query
        );
        return new PageUtils(page);

    }

    @Override
    public boolean up(Long spuId) {
        // 组装需要的数据
        // 1.查出当前spuId对应的所有sku信息以及品牌
        List<SkuInfoEntity> skuInfos = skuInfoService.getSkuBySpuId(spuId);

        // 2.查询所有能被用来检索的属性
        List<ProductAttrValueEntity> baseAttr = productAttrValueService.baseAttrList(String.valueOf(spuId));
        // 收集所有属性的id
        List<Long> attrIdList = baseAttr.stream()
                .map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        // 找到这些属性是可检索属性id
        List<Long> searchAttrId = attrService.selectSearchAttrs(attrIdList);
        HashSet<Long> idSet = new HashSet<>(searchAttrId);
        // 找到这些可检索属性
        List<SkuEsModel.Attrs> attrs = baseAttr.stream()
                .filter(i -> idSet.contains(i.getAttrId()))
                .map(i -> {
                    SkuEsModel.Attrs a = new SkuEsModel.Attrs();
                    a.setAttrId(i.getAttrId());
                    a.setAttrName(i.getAttrName());
                    a.setAttrValue(i.getAttrValue());
                    return a;
                }).collect(Collectors.toList());

        // 3.发送远程调用，查询是否有库存
        Map<Long, Boolean> stockMap = null;
        try {
            List<SkuStockVo> data = wareFeignService.skuHasStock(skuInfos.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList()));
            stockMap = data.stream()
                    .collect(Collectors.toMap(SkuStockVo::getSkuId, SkuStockVo::getHasStock));
        } catch (Exception e) {
            log.error("库存服务查询异常：原因{}", e);
        }

        // 4.将skuInfo封装成skuEsModel
        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> esDataSkuEsModels = skuInfos.stream()
                .map(sku -> {
                    SkuEsModel esModels = new SkuEsModel();
                    BeanUtils.copyProperties(sku, esModels);
                    esModels.setSkuPrice(sku.getPrice());
                    esModels.setSkuImg(sku.getSkuDefaultImg());
                    // 设置是否有销量
                    if (finalStockMap != null) {
                        esModels.setHasStock(finalStockMap.get(sku.getSkuId()));
                    } else {
                        esModels.setHasStock(true);
                    }
                    // TODO 2: 2020/11/17 热度
                    esModels.setHotScore(0L);
                    // 设置品牌相关信息
                    BrandEntity brandInfo = brandService.getById(esModels.getBrandId());
                    esModels.setBrandName(brandInfo.getName());
                    esModels.setBrandImg(brandInfo.getLogo());
                    // 设置分类相关的信息
                    CategoryEntity categoryInfo = categoryService.getById(esModels.getCatalogId());
                    esModels.setCatalogName(categoryInfo.getName());
                    // 设置属性
                    esModels.setAttrs(attrs);
                    return esModels;
                }).collect(Collectors.toList());

        //  5: 保存到es-上架
        R esSave = null;
        boolean flag = false;
        try {
            esSave = esFeignService.productStatusUp(esDataSkuEsModels);
            if (esSave != null) {
                flag = (boolean) esSave.get("hasFailures");
            }
        } catch (Exception e) {
            log.error("es服务保存异常：原因{}", e);
        }

        if (!flag) {
            // 上架成功，修改spu的状态
            this.baseMapper.updateSpuStatus(spuId, ProductConstant.UP);
        } else {
            // 调用失败
            // TODO: 2020/11/17 重复调用，接口幂等性
        } 
        return !flag;
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
    BrandService brandService;
    @Resource
    CategoryService categoryService;

    @Resource
    CouponFeignService couponFeignService;
    @Resource
    WareFeignService wareFeignService;
    @Resource
    EsFeignService esFeignService;

    public void queryCondition(QueryWrapper<SpuInfoEntity> query, Map<String, Object> params, String key, String colName) {
        String status = (String) params.get(key);
        if (StringUtils.isNotBlank(status)) {
            query.and(q -> {
                q.eq(colName, status);
            });
        }
    }
}