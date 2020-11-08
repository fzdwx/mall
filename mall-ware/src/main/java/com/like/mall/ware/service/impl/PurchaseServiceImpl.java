package com.like.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.constant.WareConstant;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.ware.dao.PurchaseDao;
import com.like.mall.ware.entity.PurchaseDetailEntity;
import com.like.mall.ware.entity.PurchaseEntity;
import com.like.mall.ware.service.PurchaseDetailService;
import com.like.mall.ware.service.PurchaseService;
import com.like.mall.ware.vo.MergeVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
                        .eq("status", 0).or().eq("status", 1)
        );

        return new PageUtils(page);

    }

    @Override
    public void merge(MergeVo vo) {
        Long purchaseId = vo.getPurchaseId();
        if (purchaseId == null) {
            // 1.没有采购单就新建一个
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.Purchase.create); // 采购单的状态为新建
            purchaseEntity.setCreateTime(new Date());
            purchaseEntity.setUpdateTime(new Date());
            this.save(purchaseEntity);
            purchaseId = purchaseEntity.getId();
        }
        // 2.修改采购需求单的id和状态
        List<Long> items = vo.getItems();
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> purchaseDetailEntities = items.stream()
                .map(i -> {
                    PurchaseDetailEntity purchaseDetail = new PurchaseDetailEntity();
                    purchaseDetail.setId(i);
                    purchaseDetail.setPurchaseId(finalPurchaseId);
                    purchaseDetail.setStatus(WareConstant.PurchaseDetail.assigned);  // 修改采购需求单的状态为已分配
                    return purchaseDetail;
                }).collect(Collectors.toList());

        purchaseDetailService.updateBatchById(purchaseDetailEntities);

        // 修改采购单的时间
        PurchaseEntity purchase = new PurchaseEntity();
        purchase.setId(purchaseId);
        purchase.setUpdateTime(new Date());
        updateById(purchase);
    }

    @Resource
    PurchaseDetailService purchaseDetailService;
}