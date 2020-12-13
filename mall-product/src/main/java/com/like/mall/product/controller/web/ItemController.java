package com.like.mall.product.controller.web;

import com.like.mall.product.service.SkuInfoService;
import com.like.mall.product.vo.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author like
 * @date 2020-12-12 17:35
 * @contactMe 980650920@qq.com
 * @description
 */
@Controller
public class ItemController {

    /**
     * 查看sku的详细信息
     *
     * @param skuId sku id
     * @return {@link String}
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable Long skuId, Model model) {
        SkuItemVo skuItemVo = skuInfoService.item(skuId);
        model.addAttribute("item",skuItemVo);
        return "item";
    }

    @Autowired
    private SkuInfoService skuInfoService;
}
