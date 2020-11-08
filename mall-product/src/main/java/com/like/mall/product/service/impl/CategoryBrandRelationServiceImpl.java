package com.like.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.product.dao.BrandDao;
import com.like.mall.product.dao.CategoryBrandRelationDao;
import com.like.mall.product.dao.CategoryDao;
import com.like.mall.product.entity.BrandEntity;
import com.like.mall.product.entity.CategoryBrandRelationEntity;
import com.like.mall.product.entity.CategoryEntity;
import com.like.mall.product.service.BrandService;
import com.like.mall.product.service.CategoryBrandRelationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Resource
    BrandDao brandDao;
    @Resource
    CategoryDao categoryDao;
    @Resource
    CategoryBrandRelationService categoryBrandRelationService;
    @Resource
    BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long brandId = categoryBrandRelation.getBrandId();
        Long catelogId = categoryBrandRelation.getCatelogId();
        // 1.查询
        BrandEntity brandEntity = brandDao.selectById(brandId);
        CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
        // 2.设置属性
        categoryBrandRelation.setBrandName(brandEntity.getName());
        categoryBrandRelation.setCatelogName(categoryEntity.getName());
        // 3.保存
        save(categoryBrandRelation);
    }

    // 级联更新
    @Override
    public void updateBrand(String name, Long brandId) {
        QueryWrapper<CategoryBrandRelationEntity> query = new QueryWrapper<>();
        CategoryBrandRelationEntity categoryBrandRelationEntity = CategoryBrandRelationEntity
                .builder()
                .brandId(brandId)
                .brandName(name)
                .build();
        query.eq("brand_id", brandId);
        this.update(categoryBrandRelationEntity, query);
    }

    // 级联更新
    @Override
    public void updateCategory(CategoryEntity category) {
        baseMapper.updateCategory(category.getCatId(), category.getName());
    }

    @Override
    public List<BrandEntity> getBrandsByCatId(Long catId) {
        // 1.查询出和当前分类项关联的品牌id
        List<CategoryBrandRelationEntity> categoryBrandRelationEntities = categoryBrandRelationService
                .list(new QueryWrapper<CategoryBrandRelationEntity>()
                                .eq("catelog_id", catId));
        // 2.根据上面的list选出品牌id
        List<Long> brandIdList = categoryBrandRelationEntities.stream()
                .map(CategoryBrandRelationEntity::getBrandId).collect(Collectors.toList());

        // 3.根据brandId查找brand
        return brandService.listByIds(brandIdList);
    }

}