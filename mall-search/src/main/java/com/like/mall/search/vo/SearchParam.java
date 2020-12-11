package com.like.mall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * 搜索参数
 *
 * @author like
 * @email 980650920@qq.com
 * @date 2020-12-10 12:11:02
 * 封装页面所有可能传递过来的查询条件
 * 1.全文检索：skuTitle -> keyword
 * 2.排序：saleCount，hotScore，skuPrice
 * 3.过滤：hasStock，skuPrice，brandID，catalog3Id,attrs
 * 4.聚合：attrs
 */
@Data
public class SearchParam {

    /**
     * 关键字,页面传过来的全为匹配字段(skuTitle)
     */
    private String keyword;

    /**
     * 三级分类 id
     */
    private Long catalog3Id;

    /**
     * 排序条件
     * saleCount，hotScore，skuPrice
     * saleCount_desc or saleCount_asc
     */
    private String sort;

    /**
     * 是否只显示有货
     */
    private Integer hasStock = 0;

    /**
     * sku价格
     */
    private String skuPrice;

    /**
     * 品牌Id列表
     */
    private List<Long> brandId;

    /**
     * 属性 attrs=1_其他:安卓&attrs=2_六寸:五寸:七寸
     * 属性id_属性值:属性值
     */
    private List<String> attrs;

    /**
     * 页面号
     */
    private Integer pageNum = 1;

    /**
     * 查询字符串
     */
    private String _queryString;
}
