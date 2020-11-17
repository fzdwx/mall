package com.like.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.like.mall.product.entity.SpuInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 11:35:48
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    void updateSpuStatus(@Param("spuId") Long spuId, @Param("status") Integer status);
}
