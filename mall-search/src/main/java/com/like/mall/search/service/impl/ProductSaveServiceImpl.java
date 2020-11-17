package com.like.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.like.mall.common.To.es.SkuEsModel;
import com.like.mall.search.config.ESConfig;
import com.like.mall.search.constant.EsConstant;
import com.like.mall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author like
 * @since 2020-11-17 13:18
 */
@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {

    @Resource
    RestHighLevelClient esClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) {
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            bulkRequest.add(new IndexRequest(EsConstant.PRODUCT_INDEX)
                    .id(skuEsModel.getSkuId().toString())
                    .source(JSON.toJSONString(skuEsModel), XContentType.JSON));
        }

        boolean hasFailures = false;
        try {
            BulkResponse bulk = esClient.bulk(bulkRequest, ESConfig.COMMON_OPTIONS);
            hasFailures = bulk.hasFailures();
            if (!hasFailures) {
                List<String> ids = Arrays.stream(bulk.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());
                log.error("商品上架失败：id{}", ids);
            }
        } catch (IOException e) {
            log.error("es存储异常：原因{}", e.getMessage());
        }
        return hasFailures;
    }
}
