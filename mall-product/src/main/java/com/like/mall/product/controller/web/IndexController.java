package com.like.mall.product.controller.web;

import com.like.mall.product.entity.CategoryEntity;
import com.like.mall.product.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import java.util.List;

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
        model.addAttribute("data",ens);
        return "index";
    }
}
