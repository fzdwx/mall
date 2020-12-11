package com.like.mall.search.vo;

import com.like.mall.common.To.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * @author like
 * @date 2020-12-10 14:34
 * @email 980650920@qq.com
 */
@Data
public class SearchResData {

    /**
     * 查询到的所有的商品信息
     */
    private List<SkuEsModel> products;

    /**
     * 页面号
     */
    private Integer pageNum;

    /**
     * 总共有多少条数据
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer totalPage;

    /**
     * 当前结果查询到的所有品牌
     */
    private List<BrandVo> brands;

    /**
     * 当前结果查询到的所有属性
     */
    private List<AttrVo> attrs;
    /**
     * 当前结果查询到的所有分类信息
     */
    private List<CatalogVo> catalogs;

    //面包屑导航
    private List<NavVo> navs;

    @Data
    public static class NavVo{
        private String navName;
        private String navValue;
        private String Link;
    }


    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }


    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }
}
