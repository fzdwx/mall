package com.like.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.product.dao.SkuInfoDao;
import com.like.mall.product.entity.SkuInfoEntity;
import com.like.mall.product.service.SkuInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;


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