package com.like.mall.product.controller;

import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.R;
import com.like.mall.product.entity.AttrEntity;
import com.like.mall.product.entity.AttrGroupEntity;
import com.like.mall.product.service.AttrGroupService;
import com.like.mall.product.service.AttrService;
import com.like.mall.product.service.CategoryService;
import com.like.mall.product.vo.AttrGroupRelationVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 属性分组
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 11:35:49
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    AttrService attrService;

    /**
     * 删除属性分组和属性的关联关系
     *
     * @param vos 参数个数
     * @return {@link R}
     */
    @PostMapping("/attr/relation/delete")
    public R delAttrAndAttrGroupRelation(@RequestBody List<AttrGroupRelationVo> vos) {
        Long count = attrGroupService.delAttrAndAttrGroupRelation(vos);
        return R.ok().put("count",count);
    }

    /**
     * 查询属性中
     * 1.当前分类
     * 2.没有被关联
     *
     * @param attrGroupId attr组id
     */
    @GetMapping("/{attrGroupId}/noattr/relation")
    public R attrNoRelation(@RequestParam Map<String, Object> params,
                            @PathVariable Long attrGroupId) {
        PageUtils page = attrService.getNoRelationAttr(params, attrGroupId);

        return R.ok().put("page", page);
    }

    /**
     * 获取attrGroup所关联的属性（基本属性）
     *
     * @param attrGroupId attr组id
     * @return {@link R}
     */
    @GetMapping("/{attrGroupId}/attr/relation")
    public R attrRelation(@PathVariable Long attrGroupId) {
        List<AttrEntity> attrs = attrService.getAttrGroupById(attrGroupId);

        return R.ok()
                .put("data", attrs);
    }

    /**
     * 商品系统-平台属性-属性分组
     * - 根据分类信息显示对应的分组信息
     * - 默认显示所有
     * -
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable Long catelogId) {
        PageUtils page = /*  attrGroupService.queryPage(params);*/
                attrGroupService.queryPage(params, catelogId);
        return R
                .ok()
                .put("page", page);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId) {
        AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);
        // 前端回显数据添加分类路径
        attrGroup.setCatelogPath(categoryService.findCatelogPath(attrGroup.getCatelogId()));
        return R
                .ok()
                .put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup) {
        attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds) {
        attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
