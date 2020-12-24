package com.like.mall.product.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.R;
import com.like.mall.product.entity.BrandEntity;
import com.like.mall.product.entity.CategoryBrandRelationEntity;
import com.like.mall.product.service.CategoryBrandRelationService;
import com.like.mall.product.vo.BrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 品牌分类关联
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 11:35:49
 * 商品系统-品牌管理-关联分类
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 获取分类关系品牌列表
     *
     * @param catId 分类id
     * @return {@link R}
     */
    @GetMapping("/brands/list")
    public R catRelationBrandList(@RequestParam(required = true) Long catId) {
        List<BrandEntity> entities = categoryBrandRelationService.getBrandsByCatId(catId);

        List<BrandVo> brandVos = entities.stream()
                .map(i -> BrandVo.builder()
                        .brandName(i.getName())
                        .brandId(i.getBrandId())
                        .build())
                .collect(Collectors.toList());
        return R.ok().put("data",brandVos);
    }

    /**
     * 获取当前品牌关联的所有分类列表
     *
     * @param brandId
     * @return
     */
    @GetMapping("/catelog/list")
    public R catelogList(@RequestParam("brandId") Long brandId) {
        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.list(
                new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));
        return R.ok().put("data", data);
    }

    /**
     * 保存品牌和分类之间的关联关系
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.saveDetail(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
