package com.like.mall.ware.controller;

import com.like.mall.common.utils.PageUtils;
import com.like.mall.common.utils.R;
import com.like.mall.common.vo.WareSkuLockVo;
import com.like.mall.ware.entity.WareSkuEntity;
import com.like.mall.ware.service.WareSkuService;
import com.like.mall.ware.vo.SkuStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 商品库存
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-10-25 13:02:25
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    @PostMapping("/lock/order")
    public Boolean orderLockStock(@RequestBody WareSkuLockVo vo) {
        try {
            Boolean b = wareSkuService.orderLockStock(vo);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    // 查询sku是否有库存
    @PostMapping("/hasStock")
    public  List<SkuStockVo> skuHasStock(@RequestBody List<Long> skuIds) {
       return   wareSkuService.skuHasStock(skuIds);

    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
