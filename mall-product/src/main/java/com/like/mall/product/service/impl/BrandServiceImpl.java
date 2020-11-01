package com.like.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.product.dao.BrandDao;
import com.like.mall.product.entity.BrandEntity;
import com.like.mall.product.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 1.组装查询条件
        QueryWrapper<BrandEntity> query = new QueryWrapper<>();
        // - 获取key
        String key = params
                .get("key")
                .toString();
        if (StringUtils.isNotBlank(key)) {
            query.eq("brand_id", key).or().like("name", key);
        }
        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                query
                                           );

        return new PageUtils(page);
    }

}