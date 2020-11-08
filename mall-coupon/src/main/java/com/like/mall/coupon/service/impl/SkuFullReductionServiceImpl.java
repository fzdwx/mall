package com.like.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.To.SkuReductionTo;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.coupon.dao.SkuFullReductionDao;
import com.like.mall.coupon.entity.MemberPriceEntity;
import com.like.mall.coupon.entity.SkuFullReductionEntity;
import com.like.mall.coupon.entity.SkuLadderEntity;
import com.like.mall.coupon.service.MemberPriceService;
import com.like.mall.coupon.service.SkuFullReductionService;
import com.like.mall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuReduction(SkuReductionTo skuReductionTo) {
        // - sku优惠满减信息 跨库：mall-sale sms_sku_ladder sms_sku_full_reduction sms_member_price
        SkuLadderEntity skuLadder = new SkuLadderEntity();
        skuLadder.setSkuId(skuReductionTo.getSkuId());
        skuLadder.setFullCount(skuReductionTo.getFullCount());
        skuLadder.setDiscount(skuReductionTo.getDiscount());
        skuLadder.setAddOther(skuReductionTo.getCountStatus());
//        skuLadder.setPrice();
        if (skuLadder.getFullCount() > 0) skuLadderService.save(skuLadder);

        SkuFullReductionEntity skuFullReduction = new SkuFullReductionEntity();
        BeanUtils.copyProperties(skuReductionTo, skuFullReduction);
        if (skuFullReduction.getFullPrice().compareTo(new BigDecimal(0)) > 0)
            this.save(skuFullReduction);

        List<MemberPriceEntity> memberPriceEntityList = skuReductionTo.getMemberPrice().stream()
                .map(item -> {
                    MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                    memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
                    memberPriceEntity.setMemberLevelId(item.getId());
                    memberPriceEntity.setMemberLevelName(item.getName());
                    memberPriceEntity.setMemberPrice(item.getPrice());
                    memberPriceEntity.setAddOther(1);
                    return memberPriceEntity;
                })
                .filter(i-> i.getMemberPrice().compareTo(new BigDecimal(0)) > 0)
                .collect(Collectors.toList());
        memberPriceService.saveBatch(memberPriceEntityList);
    }

    @Resource
    SkuLadderService skuLadderService;
    @Resource
    MemberPriceService memberPriceService;
}