package com.like.mall.product.controller;

import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.R;
import com.like.mall.product.entity.ProductAttrValueEntity;
import com.like.mall.product.service.AttrService;
import com.like.mall.product.service.ProductAttrValueService;
import com.like.mall.product.vo.AttrRespVo;
import com.like.mall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 商品属性
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 11:35:49
 * com.like.mall.product.controller.AttrController
 * com.like.mall.product.controller.AttrController
 */
@RestController()
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;
    @Resource
    ProductAttrValueService productAttrValueService;

    /**
     * 更新spu基本属性（规格参数）
     *
     * @param spuId spu id
     * @param vos   vos
     * @return {@link R}
     */
    @PostMapping("/update/{spuId}")
    public R updateSpuBaseAttr(@PathVariable Long spuId,
                               @RequestBody List<ProductAttrValueEntity> vos) {
        productAttrValueService.updateSpuBaseAttr(spuId,vos);

        return R.ok();

    }

    /**
     * 获取spu的规格信息（基本属性）
     *
     * @param spuId spu id
     * @return {@link R}
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrList(@PathVariable String spuId) {
        List<ProductAttrValueEntity> list = productAttrValueService.baseAttrList(spuId);

        return R.ok().put("data", list);
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVo attr) {
        attrService.updateDetail(attr);

        return R.ok();
    }

    /**
     * 信息：数据回显
     * - 添加显示完整的分类路径
     * - 分组信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId) {
        AttrRespVo vo = attrService.getAttrInfo(attrId);
        return R.ok()
                .put("attr", vo);
    }

    /**
     * 查询当前分类的属性（[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]）
     */
    @GetMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("catelogId") Long catelogId,
                          @PathVariable String attrType) {
        PageUtils page = attrService.queryBaseAttrPage(params, catelogId, attrType);
        return R.ok()
                .put("page", page);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attr) {
        // 保存attr 和 attr group 连接表
        attrService.saveAttr(attr);

        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok()
                .put("page", page);
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
