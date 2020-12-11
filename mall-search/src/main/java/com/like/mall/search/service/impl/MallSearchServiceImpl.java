package com.like.mall.search.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.like.mall.common.To.es.SkuEsModel;
import com.like.mall.common.utils.R;
import com.like.mall.search.constant.EsConstant;
import com.like.mall.search.feign.ProductServiceFeign;
import com.like.mall.search.service.MallSearchService;
import com.like.mall.search.vo.AttrRespVo;
import com.like.mall.search.vo.SearchParam;
import com.like.mall.search.vo.SearchResData;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.like.mall.search.constant.EsConstant.PRODUCT_PAGE_SIZE;


/**
 * @author like
 * @email 980650920@qq.com
 * @since 2020-12-10 12:07
 */
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Resource
    private RestHighLevelClient esClient;

    @Override
    public SearchResData search(SearchParam param) throws IOException {


        // 1.准备检索请求
        SearchRequest searchRequest = buildSearchConditions(param);
        // 2.执行检索
        SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);

        // 3.封装对象
        return getData(response, param);
    }

    /**
     * 构建搜索条件
     *
     * @param param 参数
     * @return {@link SearchRequest}
     */
    private SearchRequest buildSearchConditions(SearchParam param) {

        // 构建dsl查询语句
        SearchSourceBuilder searchSource = SearchSourceBuilder.searchSource();

        // 1.构建boolQuery
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // 2.must keyword
        if (StringUtils.isNoneBlank(param.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }

        // 3.filter 过滤
        // 三级分类id
        if (param.getCatalog3Id() != null) {
            boolQuery.filter(QueryBuilders.matchQuery("catalogId", param.getCatalog3Id()));
        }
        // 品牌id
        if (param.getBrandId() != null && param.getBrandId().size() > 0) {
            boolQuery.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }
        // 查询是否有库存
        if (param.getHasStock() != null) {
            boolQuery.filter(QueryBuilders.termsQuery("hasStock", param.getHasStock() == 1));
        }
        // 价格区间
        if (StringUtils.isNoneBlank(param.getSkuPrice())) {
            // 1_500 _500 500_
            String[] s = param.getSkuPrice().split("_");
            RangeQueryBuilder query = QueryBuilders.rangeQuery("skuPrice");
            if (s.length == 2) {
                query.gte(s[0]).lte(s[1]);
            } else if (s.length == 1) {
                if (param.getSkuPrice().startsWith("_")) {
                    query.lte(s[0]);
                } else if (param.getSkuPrice().endsWith("_")) {
                    query.gte(s[0]);
                }
            }
            boolQuery.filter(query);
        }
        // 属性
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            for (String attrStr : param.getAttrs()) { // attrs=1_其他:安卓&attrs=2_六寸:五寸:七寸  属性id_属性值:属性值
                BoolQueryBuilder bool = QueryBuilders.boolQuery();

                String[] s = attrStr.split("_");
                // 获取需要查询的属性的信息
                String attrId = s[0];
                String[] attrValues = s[1].split(":");

                bool.must(QueryBuilders.matchQuery("attrs.attrId", attrId));
                bool.must(QueryBuilders.termsQuery("attrs.attrName", attrValues));

                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", bool, ScoreMode.None);
                boolQuery.filter(nestedQueryBuilder);
            }
        }

        // 4.排序
        if (StringUtils.isNoneBlank(param.getSort())) { // saleCount，hotScore，skuPrice  saleCount_desc or saleCount_asc
            String[] s = param.getSort().split("_");
            String field = s[0];
            String sort = s[1];

            // 构建排序条件
            SortOrder sortOrder = SortOrder.ASC;
            if (sort.equals("desc"))
                sortOrder = SortOrder.DESC;
            searchSource.sort(field, sortOrder);
        }

        // 5.分页
        searchSource.size(PRODUCT_PAGE_SIZE)
                .from((param.getPageNum() - 1) * PRODUCT_PAGE_SIZE);

        // 6.高亮
        if (StringUtils.isNoneBlank(param.getKeyword())) {
            searchSource.highlighter(SearchSourceBuilder.highlight()
                    .field("skuTitle")
                    .preTags("<b style='color:red'>").postTags("</b>"));
        }

        // 7.聚合分析
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brandAgg").field("brandId").size(50);  // 找出每一个品牌的id
        brandAgg.subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName").size(1));      // 名字
        brandAgg.subAggregation(AggregationBuilders.terms("brandImgAgg").field("brandImg").size(1));       // 图片
        searchSource.aggregation(brandAgg);

        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalogAgg").field("catalogId").size(50);  // 找出每一个分类的id
        catalogAgg.subAggregation(AggregationBuilders.terms("catalogNameAgg").field("catalogName").size(1));      // 名字
        catalogAgg.subAggregation(AggregationBuilders.terms("catalogImgAgg").field("catalogImg").size(1));       //  图片
        searchSource.aggregation(catalogAgg);

        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attrAgg", "attrs");          // 找出属性的相关信息
        AggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId");
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"));
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"));
        attrAgg.subAggregation(attrIdAgg);
        searchSource.aggregation(attrAgg);


        searchSource.query(boolQuery);

        return new SearchRequest().source(searchSource).indices(EsConstant.PRODUCT_INDEX);
    }

    /**
     * 返回页面需要的数据
     *
     * @param response 响应
     * @param param
     * @return {@link SearchResData}
     */
    private SearchResData getData(SearchResponse response, SearchParam param) {
        SearchResData data = new SearchResData();

        // 准备数据
        // 封装product需要的数据
        SearchHits hits = response.getHits();
        SearchHit[] hitsHits = hits.getHits();
        // 封装其他需要的数据
        Aggregations aggregations = response.getAggregations();

        List<SkuEsModel> skuEsModels = new ArrayList<>();
        List<SearchResData.BrandVo> brandVos = new ArrayList<>();
        List<SearchResData.AttrVo> attrVos = new ArrayList<>();
        List<SearchResData.CatalogVo> catalogVos = new ArrayList<>();

        // 设置产品总信息
        for (SearchHit h : hitsHits) {
            Map<String, Object> source = h.getSourceAsMap();
            SkuEsModel skuEsModel = BeanUtil.mapToBean(source, SkuEsModel.class, true);

            // 设置高亮
            if (StringUtils.isNotBlank(param.getKeyword())) {
                skuEsModel.setSkuTitle(Arrays.toString(h.getHighlightFields().get("skuTitle").fragments()));
            }
            skuEsModels.add(skuEsModel);
        }

        // 设置分类
        ParsedLongTerms catalogAgg = aggregations.get("catalogAgg");
        for (Terms.Bucket bucket : catalogAgg.getBuckets()) {
            SearchResData.CatalogVo catalogVo = new SearchResData.CatalogVo();
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalogNameAgg");

            catalogVo.setCatalogId((Long) bucket.getKey());
            catalogVo.setCatalogName(catalogNameAgg.getBuckets().get(0).getKeyAsString());

            catalogVos.add(catalogVo);
        }

        // 设置品牌
        ParsedLongTerms brandAgg = aggregations.get("brandAgg");
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            SearchResData.BrandVo brandVo = new SearchResData.BrandVo();
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brandImgAgg");
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brandNameAgg");

            brandVo.setBrandId((Long) bucket.getKeyAsNumber());
            brandVo.setBrandImg(brandImgAgg.getBuckets().get(0).getKeyAsString());
            brandVo.setBrandName(brandNameAgg.getBuckets().get(0).getKeyAsString());

            brandVos.add(brandVo);
        }

        // 设置属性
        ParsedNested attrAgg = aggregations.get("attrAgg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");

        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResData.AttrVo attrVo = new SearchResData.AttrVo();
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attrNameAgg");
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");

            attrVo.setAttrId((Long) bucket.getKey());
            attrVo.setAttrName(attrNameAgg.getBuckets().get(0).getKeyAsString());
            attrVo.setAttrValue(attrValueAgg.getBuckets().stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList()));

            attrVos.add(attrVo);
        }

        data.setProducts(skuEsModels);
        data.setBrands(brandVos);
        data.setAttrs(attrVos);
        data.setCatalogs(catalogVos);

        data.setPageNum(param.getPageNum());
        data.setTotal(hits.getTotalHits().value);
        data.setTotalPage(Math.toIntExact(
                hits.getTotalHits().value % PRODUCT_PAGE_SIZE == 0
                        ? hits.getTotalHits().value / PRODUCT_PAGE_SIZE
                        : (hits.getTotalHits().value / PRODUCT_PAGE_SIZE) + 1));


        if (param.getAttrs() != null && param.getAttrs().size() > 0) {

            List<SearchResData.NavVo> navVos = param.getAttrs().stream().map(i -> {
                // 分析每个attrs传过来查询的数值；
                SearchResData.NavVo navVo = new SearchResData.NavVo();
                String[] s = i.split("_");
                navVo.setNavValue(s[1]);

                R r = productServiceFeign.attrInfo(Long.valueOf(s[0]));
                if (r.getCode() == 0) {
                    AttrRespVo attr = (AttrRespVo) r.get("attr");

                    navVo.setNavName(attr.getAttrName());
                }

                // 取消这个面包屑后，我们要跳转到哪个地方，将请求地址置空
                String replace = param.get_queryString().replace("attrs=" + Arrays.toString(s), "");
                navVo.setLink("http://localhost:8889/list.html?" + replace);

                return navVo;
            }).collect(Collectors.toList());

            data.setNavs(navVos);
        }


        return data;
    }

    @Autowired
    private ProductServiceFeign productServiceFeign;
}
