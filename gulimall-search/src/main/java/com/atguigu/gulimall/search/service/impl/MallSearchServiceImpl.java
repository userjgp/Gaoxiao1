package com.atguigu.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.Config.GuilimallElastSearchConfig;
import com.atguigu.gulimall.search.constant.Esconstant;
import com.atguigu.gulimall.search.feign.ProductFeignService;
import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.AttrResponseVo;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.lang.model.type.ArrayType;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient esRestClient;
@Autowired
    ProductFeignService productFeignService;


    @Override
    public SearchResult search(SearchParam param) {

        SearchResult result=null;
//构造dsl
        SearchRequest request = buildSearchRequrest(param);


        try{
            //执行检索请求
            SearchResponse response= esRestClient.search(request, GuilimallElastSearchConfig.COMMON_OPTIONS);
//分析响应数据封装成我们需要的格式
            result=buildSearchResult(response,param);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 构建结果数据
     * @param response
     * @param param
     * @return
     */

    private SearchResult buildSearchResult(SearchResponse response, SearchParam param) {
        SearchResult searchResult = new SearchResult();
        //1、返回的所有查询到的商品
        SearchHits hits = response.getHits();
        SearchHit[] hits1 = hits.getHits();
        List<SkuEsModel> skuEsModels = new ArrayList<>();
        if(hits1!=null && hits1.length>0){
            for (SearchHit documentFields : hits1) {
                String sourceAsString = documentFields.getSourceAsString();
                SkuEsModel skuEsModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if(!StringUtils.isEmpty(param.getKeyword())){
                    HighlightField skuTitle = documentFields.getHighlightFields().get("skuTitle");
                    String string = skuTitle.getFragments()[0].string();
                    skuEsModel.setSkuTitle(string);
                }
                skuEsModels.add(skuEsModel);
            }
        }
        searchResult.setProduct(skuEsModels);

        //2、当前商品涉及到的所有属性信息
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        //获取属性信息的聚合
        ParsedNested attrsAgg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attrIdAgg = attrsAgg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            //1、得到属性的id
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);

            //2、得到属性的名字
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attr_name_agg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);

            //3、得到属性的所有值
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attr_value_agg");
            List<String> attrValues = attrValueAgg.getBuckets().stream().map(item -> item.getKeyAsString()).collect(Collectors.toList());
            attrVo.setAttrValue(attrValues);

            attrVos.add(attrVo);
        }

        searchResult.setAttrs(attrVos);


        //3、当前商品涉及到的所有品牌信息
        ArrayList<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brand_agg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brand_agg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
//            得到品牌的id
            long brandId = bucket.getKeyAsNumber().longValue();
//            得到品牌的名字
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brand_name_agg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);
//            得到品牌的图片
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brand_img_agg");
            String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);

            brandVos.add(brandVo);
        }

        searchResult.setBrands(brandVos);
        //4、当前商品涉及到的所有分类信息
        ParsedLongTerms catalog_agg = response.getAggregations().get("catalog_agg");
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            //得到分类id
            String keyAsString = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(keyAsString));

            //得到分类名
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name_agg");
            if(catalogNameAgg!=null){
                String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
                catalogVo.setCatalogName(catalogName);
            }

            catalogVos.add(catalogVo);
        }
        searchResult.setCatalogs( catalogVos);
        

        //5、分页信息-页码
        searchResult.setPageNum(param.getPageNum());
        //5、1分页信息、总记录数
        long total = hits.getTotalHits().value;
        searchResult.setTotal(total);
        //5、2分页信息-总页码-计算
        int totalPages = (int)total % Esconstant.PRODUCT_PAGESIZE == 0 ?
                (int)total / Esconstant.PRODUCT_PAGESIZE : ((int)total / Esconstant.PRODUCT_PAGESIZE + 1);
        searchResult.setTotalPages(totalPages);

        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }

        searchResult.setPageNavs(pageNavs);
//6、构建面包屑导航
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            List<SearchResult.NavVo> collect = param.getAttrs().stream().map(attr -> {
            SearchResult.NavVo navVo = new SearchResult.NavVo();
//            attrs=2_5cun:6fun
            String[] s = attr.split("_");
            navVo.setNavValue(s[1]);

            R r = productFeignService.attrInfo(Long.parseLong(s[0]));
            if(r.getCode()==0){
                AttrResponseVo data = r.getData("attr", new TypeReference<AttrResponseVo>(){});
                navVo.setNavName(data.getAttrName());
            }else {
                navVo.setNavName(s[0]);
            }

            //2、取消了这个面包屑以后，我们要跳转到哪个地方，将请求的地址url里面的当前置空
                String encode = null;
                try {
                    encode = URLEncoder.encode(attr,"UTF-8");
                    encode.replace("+","%20");  //浏览器对空格的编码和Java不一样，差异化处理
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            String replace = param.get_queryString().replace("&attrs=" + encode, "");

            navVo.setLink("http://search.gulimall.com/list.html?" + replace);
            return navVo;
        }).collect(Collectors.toList());

            searchResult.setNavs(collect);

        }
//       List<SearchResult.NavVo> navVos = new ArrayList<>();

        return searchResult;
    }

    /**
     * 准备检索请求
     * @return
     */
    private SearchRequest buildSearchRequrest(SearchParam param) {
//构建dsl语句
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
/**
 *模糊匹配，过滤（按照属性，分类，品牌，价格区间，库存）
 */
//构建boolbuider
//        must-模糊匹配
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if(!StringUtils.isEmpty(param.getKeyword())){
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
        }
//filter-按照三级分类查询
        if(param.getCatalog3Id()!=null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId",param.getCatalog3Id()));
        }
//        filter_品牌id
        if(param.getBrandId()!=null && param.getBrandId().size()>0){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));

        }
//        filter_按照所有指定的属性进行查询
        if(param.getAttrs()!=null && param.getAttrs().size()>0){

//            attrs=1_5寸：8寸
            for (String attr : param.getAttrs()) {
                BoolQueryBuilder boolQueryBuilder1 = QueryBuilders.boolQuery();
                String[] s = attr.split("_");
                String attrId=s[0]; //检索的属性id
                String[] attrValues= s[1].split(":"); //这个属性检索用的值
                boolQueryBuilder1.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                boolQueryBuilder1.must(QueryBuilders.termsQuery("attrs.attrValue",attrValues));
                NestedQueryBuilder attrsquery = QueryBuilders.nestedQuery("attrs", boolQueryBuilder1, ScoreMode.None);
                boolQueryBuilder.filter(attrsquery);
            }

        }
        // filter_按照所有有库存
        if(null != param.getHasStock()){
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock",param.getHasStock() == 1));
        }        // filter_按照价格区间
        if(!StringUtils.isEmpty(param.getSkuPrice())){
            RangeQueryBuilder skuPrice = QueryBuilders.rangeQuery("skuPrice");
            String[] s = param.getSkuPrice().split("_");
            if(s.length==2){
//                区间
                skuPrice.gte(s[0]).lte(s[1]);
            }else if(s.length==1){
                if(param.getSkuPrice().startsWith("_")){
                    skuPrice.lte(s[0]);
                }
                if(param.getSkuPrice().endsWith("_")){
                    skuPrice.gte(s[0]);
                }
            }

            boolQueryBuilder.filter(skuPrice);
        }


//        把以前所有的条件都拿来封装
        searchSourceBuilder.query(boolQueryBuilder);
/**
 *排序，分页，高亮
 */

        if(!StringUtils.isEmpty(param.getSort())){
            String sort = param.getSort();
            //sort=hostscore_asc/desc
            String[] s = sort.split("_");
            SortOrder order=s[1].equalsIgnoreCase("asc")?SortOrder.ASC: SortOrder.DESC;
            searchSourceBuilder.sort(s[0], order);
        }

//        分页
//        pageNum:1 from:0 size:5
//        from=(页码-1)*size
        searchSourceBuilder.from((param.getPageNum()-1)*Esconstant.PRODUCT_PAGESIZE);
        searchSourceBuilder.size(Esconstant.PRODUCT_PAGESIZE);

//高亮
        if(!StringUtils.isEmpty(param.getKeyword()))
        {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b stytle='color:red'>");
            highlightBuilder.postTags("</b>");

            searchSourceBuilder.highlighter(highlightBuilder);
        }

/**
 *聚合分析
 */
//品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");

        brand_agg.field("brandId").size(50);
//        todo 聚合brand
        //品牌聚合子聚合
        TermsAggregationBuilder brand_name_agg = brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));

        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));

        searchSourceBuilder.aggregation(brand_agg);


        //        todo 聚合catalog
//        分类聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_agg_agg").field("catalogName").size(1));

        searchSourceBuilder.aggregation(catalog_agg);


        //        todo 聚合attr

//        属性聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
//        聚合出所有attr_id
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
//        聚合分析出attr_id对应名字
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
//聚合分析出attrid对应的所有可能的属性值attrvalue
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        attr_agg.subAggregation(attr_id_agg);

        searchSourceBuilder.aggregation(attr_agg);




        SearchRequest request = new SearchRequest(new String[]{Esconstant.PRODUCT_INDEX}, searchSourceBuilder);
        return request;
    }
}
