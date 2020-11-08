package com.like.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.product.dao.SpuImagesDao;
import com.like.mall.product.entity.SpuImagesEntity;
import com.like.mall.product.service.SpuImagesService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(
                new Query<SpuImagesEntity>().getPage(params),
                new QueryWrapper<SpuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveImages(Long id, List<String> images) {
        if (!images.isEmpty()) {
            List<SpuImagesEntity> spuImages = images.stream()
                    .map(i -> {
                        SpuImagesEntity spuImage = new SpuImagesEntity();
                        spuImage.setId(id);
                        spuImage.setImgUrl(i);
                        return spuImage;
                    }).collect(Collectors.toList());
            saveBatch(spuImages);
        }
    }

}