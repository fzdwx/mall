package com.like.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.vo.WareSkuLockVo;
import com.like.mall.ware.entity.WareSkuEntity;
import com.like.mall.ware.vo.SkuStockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 13:02:25
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuStockVo> skuHasStock(List<Long> skuIds);

    Boolean orderLockStock(WareSkuLockVo vo);
}

