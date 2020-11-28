package com.like.mall.product.controller.web;

import com.like.mall.product.entity.CategoryEntity;
import com.like.mall.product.service.CategoryService;
import com.like.mall.product.vo.Catelog2Vo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author like
 * @since 2020-11-19 14:49
 * web 路径跳转
 */
@Controller
public class IndexController {
    @Resource
    CategoryService categoryService;

    @GetMapping({"/", "/index.html"})
    public String indexPage(Model model) {
        // 1.查询所有的一级分类
        List<CategoryEntity> ens = categoryService.getLevelFirstCategory();
        model.addAttribute("data", ens);
        return "index";
    }

    /**
     * 获取所有二级和三级分类的json数据
     *
     * @return {@link Map<String, List<Catelog2Vo>>}
     */
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        return categoryService.getCatalogJson();
    }
}
