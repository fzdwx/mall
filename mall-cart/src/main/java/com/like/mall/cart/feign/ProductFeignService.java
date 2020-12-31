package com.like.mall.cart.feign;

import com.like.mall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author like
 * @date 2020-12-24 17:25
 * @contactMe 980650920@qq.com
 * @description
 */
@FeignClient("mall-product")
public interface ProductFeignService {

    @GetMapping("/product/skuinfo/{skuId}/prece")
    public BigDecimal getPrice(@PathVariable("skuId") Serializable skuId);

    @RequestMapping("product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);

    @GetMapping("product/skusaleattrvalue/getAttrSale/{skuId}")
    public List<String> getSkuSaleAttrValue(@PathVariable String skuId);
}
