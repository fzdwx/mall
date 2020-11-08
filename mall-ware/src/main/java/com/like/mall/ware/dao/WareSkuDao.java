package com.like.mall.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.like.mall.ware.entity.WareSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 商品库存
 * 
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 13:02:25
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);
}
