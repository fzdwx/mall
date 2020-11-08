package com.like.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.product.dao.ProductAttrValueDao;
import com.like.mall.product.entity.ProductAttrValueEntity;
import com.like.mall.product.service.ProductAttrValueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveProductAttrValueEntities(List<ProductAttrValueEntity> productAttrValueEntities) {
        saveBatch(productAttrValueEntities);
    }

    @Override
    public List<ProductAttrValueEntity> baseAttrList(String spuId) {
        return list(new QueryWrapper<ProductAttrValueEntity>()
                .eq("spu_id", spuId));

    }

    @Override
    @Transactional
    public void updateSpuBaseAttr(Long spuId, List<ProductAttrValueEntity> vos) {
        // 1.删除以前的数据
        remove(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id",spuId));

        // 2.保存
        List<ProductAttrValueEntity> collect = vos.stream()
                .peek(i -> i.setSpuId(spuId)).collect(Collectors.toList());
        saveBatch(collect);
    }

}