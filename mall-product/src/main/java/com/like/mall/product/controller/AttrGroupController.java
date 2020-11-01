package com.like.mall.product.controller;

import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.R;
import com.like.mall.product.entity.AttrGroupEntity;
import com.like.mall.product.service.AttrGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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

    /**
     * 商品系统-平台属性-属性分组
     * - 根据分类信息显示对应的分组信息
     * - 默认显示所有
     * -
     */
    @RequestMapping("/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable Long catelogId) {
        PageUtils page= /*  attrGroupService.queryPage(params);*/
        attrGroupService.queryPage(params,catelogId);
        return R
                .ok()
                .put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
		AttrGroupEntity attrGroup = attrGroupService.getById(attrGroupId);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

}
