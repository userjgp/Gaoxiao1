package com.atguigu.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.es.SkuEsModel;
import com.atguigu.gulimall.search.Config.GuilimallElastSearchConfig;
import com.atguigu.gulimall.search.constant.Esconstant;
import com.atguigu.gulimall.search.service.ProductSaveService;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
@Service("productSaveService")
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    private RestHighLevelClient esRestClient;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {

//1.在es中建立索引，建立号映射关系（doc/json/product-mapping.json）

        //2. 在ES中保存这些数据
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel skuEsModel : skuEsModels) {
            //构造保存请求
            IndexRequest indexRequest = new IndexRequest(Esconstant.PRODUCT_INDEX);
            indexRequest.id(skuEsModel.getSkuId().toString());
            String jsonString = JSON.toJSONString(skuEsModel);
            indexRequest.source(jsonString, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }


        BulkResponse bulk = esRestClient.bulk(bulkRequest, GuilimallElastSearchConfig.COMMON_OPTIONS);

        //TODO 如果批量错误
        boolean hasFailures = bulk.hasFailures();

        List<String> collect = Arrays.asList(bulk.getItems()).stream().map(item -> {
            return item.getId();
        }).collect(Collectors.toList());



        return hasFailures;
    }

}
