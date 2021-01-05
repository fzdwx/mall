package com.like.mall.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.like.mall.ware.entity.WareSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    Long getSkuStock(@Param("i") Long i);

    List<Long> listWareIdHasSkuStock(@Param("skuId") Long skuId, @Param("count") Integer count);

    boolean lockSkuStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("count") Integer count);

    void unLock(@Param("wareId") Long wareId, @Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);
}
