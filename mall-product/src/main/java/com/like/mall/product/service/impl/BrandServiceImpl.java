package com.like.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.product.dao.BrandDao;
import com.like.mall.product.entity.BrandEntity;
import com.like.mall.product.service.BrandService;
import com.like.mall.product.service.CategoryBrandRelationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Resource
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 1.组装查询条件
        QueryWrapper<BrandEntity> query = new QueryWrapper<>();
        // - 获取key
        String key = params
                .get("key")
                .toString();
        if (StringUtils.isNotBlank(key)) {
            query
                    .eq("brand_id", key)
                    .or()
                    .like("name", key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                query
                                           );

        return new PageUtils(page);
    }

    // 级联更更新
    @Override
    public void updateDetail(BrandEntity brand) {
        // - 更新自己
        updateById(brand);
        if (StringUtils.isNotBlank(brand.getName())) {
            // - 如果需要更新品牌名,就修改分类和品牌关联表中品牌表的信息
            categoryBrandRelationService.updateBrand(brand.getName(), brand.getBrandId());
        }
        // TODO: 2020/11/1 其他级联更新
    }

}