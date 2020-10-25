package com.like.mall.product.dao;

import com.like.mall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 11:35:49
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
