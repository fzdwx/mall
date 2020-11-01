package com.like.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.like.mall.product.entity.CategoryBrandRelationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 品牌分类关联
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 11:35:49
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {

    void updateCategory(@Param("catId") Long catId, @Param("name") String name);
}
