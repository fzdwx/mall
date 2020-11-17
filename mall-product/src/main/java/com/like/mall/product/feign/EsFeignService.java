package com.like.mall.product.feign;

import com.like.mall.common.To.es.SkuEsModel;
import com.like.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author like
 * @since 2020-11-17 13:38
 */
@FeignClient("mall-search")
public interface EsFeignService {

    @PostMapping("/search/save/product")
    R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels);
}
