package com.like.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.like.mall.product.entity.AttrEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品属性
 * 
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 11:35:49
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> selectSearchAttrs(@Param("attrIdList") List<Long> attrIdList);
}
