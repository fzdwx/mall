package com.like.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.exception.NoStockException;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.common.utils.R;
import com.like.mall.common.vo.WareSkuLockVo;
import com.like.mall.ware.dao.WareSkuDao;
import com.like.mall.ware.entity.WareSkuEntity;
import com.like.mall.ware.feign.ProductFeignService;
import com.like.mall.ware.service.WareSkuService;
import com.like.mall.ware.vo.SkuStockVo;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Resource
    ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> query = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if (StringUtils.isNotBlank(skuId)) {
            query.eq("sku_id", skuId);

        }
        String wareId = (String) params.get("wareId");
        if (StringUtils.isNotBlank(wareId)) {
            query.eq("ware_id", wareId);

        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                query
        );

        return new PageUtils(page);
    }

    @Override
    public void addStock(Long skuId, Long wareId, Integer skuNum) {
        // 1.新增库存
        List<WareSkuEntity> list = this.list(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        if (list.size() == 0) {
            WareSkuEntity wareSku = new WareSkuEntity();
            wareSku.setSkuId(skuId);
            wareSku.setStock(skuNum);
            wareSku.setWareId(wareId);
            try {
                R info = productFeignService.info(skuId);
                Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
                if (info.getCode() == 0) {
                    wareSku.setSkuName((String) data.get("skuName"));
                }
            } catch (Exception e) {
            }
            //         todo    wareSku.setSkuName();
            save(wareSku);
        } else {
            // 2.添加
            baseMapper.addStock(skuId, wareId, skuNum);
        }
    }

    @Override
    public List<SkuStockVo> skuHasStock(List<Long> skuIds) {
        return skuIds.stream()
                .map(i -> {
                    SkuStockVo skuStockVo = new SkuStockVo();
                    Long count = this.baseMapper.getSkuStock(i);
                    skuStockVo.setSkuId(i);
                    skuStockVo.setHasStock(count != null && count > 0);
                    return skuStockVo;
                }).collect(Collectors.toList());
    }

    /**
     * 订单锁定库存
     */
    @Override
    @Transactional(rollbackFor = NoStockException.class)
    public Boolean orderLockStock(WareSkuLockVo vo) {
        // 1.按照下单的收货地址，找到一个就近的仓库，锁定库存
        // 找到每个商品在哪个仓库的库存足够，在锁定
        List<WareSkuLockVo.OrderItemVo> locks = vo.getLocks();
        List<SkuWareHasStock> stockList = locks.stream().map(s -> {
            SkuWareHasStock stock = new SkuWareHasStock();
            stock.setSkuId(s.getSkuId());
            // 查询这个商品在哪里有库存
            stock.setWareId(listWareIdHasSkuStock(s.getSkuId(), s.getCount()));
            stock.setCount(s.getCount());
            return stock;
        }).collect(Collectors.toList());
        // 2.锁定库存
        for (SkuWareHasStock hasStock : stockList) {
            List<Long> ware = hasStock.getWareId();
            if (ware == null || ware.size() <= 0) { // 库存不足
                throw new NoStockException(hasStock.getSkuId());
            }
            boolean skuIsStock = false;
            for (Long wareId : ware) {
                boolean b = baseMapper.lockSkuStock(hasStock.skuId, wareId, hasStock.getCount());
                if (b) {  // 锁定成功
                    skuIsStock = true;
                    break;
                } else { // 失败
                    skuIsStock =false;
                }
            }
            // 循环完后，如果有一次锁定失败就结束
            if (!skuIsStock) {
                throw new NoStockException(hasStock.getSkuId());
            }
        }
        return true;
    }

    private List<Long> listWareIdHasSkuStock(Long skuId, Integer count) {
        System.out.println(skuId);
        System.out.println(count);
        return baseMapper.listWareIdHasSkuStock(skuId, count);
    }

    @Data
    class SkuWareHasStock {
        private Long skuId;
        private List<Long> wareId;
        private Integer count;
    }
}