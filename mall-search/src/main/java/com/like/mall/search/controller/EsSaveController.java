package com.like.mall.search.controller;

import com.like.mall.common.To.es.SkuEsModel;
import com.like.mall.common.utils.R;
import com.like.mall.search.service.ProductSaveService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author like
 * @since 2020-11-17 13:13
 */
@RestController
@RequestMapping("/search/save")
public class EsSaveController {
    ProductSaveService productService;
    /**
     * 上架产品
     *
     * @param skuEsModels sku es模型
     * @return {@link R}
     */
    @PostMapping("/product")
    public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModels) {
        boolean b = productService.productStatusUp(skuEsModels);
        return R.ok().put("hasFailures",b);
    }
}
