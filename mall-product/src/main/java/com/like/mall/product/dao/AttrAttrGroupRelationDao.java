package com.like.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.like.mall.product.entity.AttrAttrgroupRelationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 11:35:49
 */
@Mapper
public interface AttrAttrGroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    Long delAttrAndAttrGroupRelation(@Param("entities") List<AttrAttrgroupRelationEntity> entities);
}
