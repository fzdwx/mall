package com.like.mall.ware.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.Query;
import com.like.mall.ware.dao.WareInfoDao;
import com.like.mall.ware.entity.WareInfoEntity;
import com.like.mall.ware.service.WareInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

        @Override
        public PageUtils queryPage(Map<String, Object> params) {
            QueryWrapper<WareInfoEntity> query = new QueryWrapper<>();
            String key = (String) params.get("key");
            if (StringUtils.isNotBlank(key)) {
                query.eq("id",key)
                        .or().like("name",key)
                        .or().like("address",key)
                        .or().like("areacode",key);
            }
            IPage<WareInfoEntity> page = this.page(
                    new Query<WareInfoEntity>().getPage(params),
                    query
            );

            return new PageUtils(page);
        }

}