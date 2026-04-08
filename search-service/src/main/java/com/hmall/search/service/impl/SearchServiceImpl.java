package com.hmall.search.service.impl;

import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmall.common.client.ItemClient;
import com.hmall.common.dto.Item;
import com.hmall.common.dto.PageDTO;
import com.hmall.search.pojo.ItemDoc;
import com.hmall.search.pojo.RequestParams;
import com.hmall.search.service.ISearchServiceI;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @projectName: Hmall
 * @package: com.hmall.search.service.impl
 * @className: SearchService
 * @author: xuxiang
 * @description: TODO
 * @date: 2023/10/24 20:52
 * @version: 1.0
 */
@Service
public class SearchServiceImpl implements ISearchServiceI {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 自动补全
     *
     * @param prefix
     * @return {@link List}<{@link String}>
     */
    @Override
    public List<String> getSuggestion(String prefix) {
        try {
            //1.准备Request
            SearchRequest request = new SearchRequest("item");
            //2.准备DSL
            request.source().fetchSource(new String[0], null);
            request.source().suggest(new SuggestBuilder()
                    .addSuggestion(
                            "my_suggestion",
                            SuggestBuilders.completionSuggestion("suggestion")
                                    .size(10)
                                    .skipDuplicates(true)
                                    .prefix(prefix)
                    ));
            //3.发请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            //4.解析结果
            //4.1 获取suggestion
            CompletionSuggestion suggestion = response.getSuggest().getSuggestion("my_suggestion");
            //4.2 获取options
            List<CompletionSuggestion.Entry.Option> options = suggestion.getOptions();
            //4.3 循环遍历,获取text
            ArrayList<String> list = new ArrayList<>(options.size());
            for (CompletionSuggestion.Entry.Option option : options) {
                list.add(option.getText().string());
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 聚合
     *
     * @param params
     * @return {@link Map}<{@link String}, {@link List}<{@link String}>>
     */
    @Override
    public Map<String, List<String>> getFilters(RequestParams params) {
        try {
            //1.准备Request
            SearchRequest request = new SearchRequest("item");
            //2.准备DSL
            //2.1不要文档数据
            request.source().size(0);
            //2.2query条件
            buildBasicQuery(request, params);
            //2.3聚合条件
            request.source().aggregation(
                    AggregationBuilders.terms("brandAgg").field("brand").size(20)
            );
            request.source().aggregation(
                    AggregationBuilders.terms("categoryAgg").field("category").size(20)
            );
            //3.发请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            //4.解析结果
            Map<String, List<String>> map = new HashMap<>(2);
            Aggregations aggregations = response.getAggregations();
            //4.1根据名称获取品牌聚合
            Terms brandAgg = aggregations.get("brandAgg");
            List<String> brandList = new ArrayList<>();
            for (Terms.Bucket bucket : brandAgg.getBuckets()) {
                String key = bucket.getKeyAsString();
                brandList.add(key);
            }
            map.put("brand", brandList);
            //4.2根据名称获取品牌聚合
            Terms categoryAgg = aggregations.get("categoryAgg");
            List<String> categoryList = new ArrayList<>();
            for (Terms.Bucket bucket : categoryAgg.getBuckets()) {
                String key = bucket.getKeyAsString();
                categoryList.add(key);
            }
            map.put("category", categoryList);
            return map;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageDTO<ItemDoc> search(RequestParams params) {
        try {
            //1.准备Request
            SearchRequest request = new SearchRequest("item");
            //2.准备DSL
            //2.1 query条件
            buildBasicQuery(request, params);
            //2.2 分页
            Integer page = params.getPage();
            Integer size = params.getSize();
            request.source().from((page - 1) * size).size(size);
            //2.3 排序
            String sortBy = params.getSortBy();
            if ("sold".equals(sortBy)) {
                request.source().sort(sortBy, SortOrder.DESC);
            } else if ("price".equals(sortBy)) {
                request.source().sort(sortBy, SortOrder.ASC);
            }
            //2.4 高亮
            request.source().highlighter(new HighlightBuilder().field("name").requireFieldMatch(false));
            //3.发请求
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            //4.解析结果
            SearchHits searchHits = response.getHits();
            //4.1 total
            long total = searchHits.getTotalHits().value;
            //4.2 数据
            SearchHit[] hits = searchHits.getHits();
            //4.3 遍历
            List<ItemDoc> list = new ArrayList<>(hits.length);
            for (SearchHit hit : hits) {
                //4.4 获取source
                String json = hit.getSourceAsString();
                //4.5 转Java
                ItemDoc itemDoc = MAPPER.readValue(json, ItemDoc.class);
                //4.6 获取高亮
                Map<String, HighlightField> map = hit.getHighlightFields();
                if (map != null && map.size() > 0) {
                    HighlightField field = map.get("name");
                    String value = field.getFragments()[0].string();
                    itemDoc.setName(value);
                }
                list.add(itemDoc);
            }
            return new PageDTO<>(total, list);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    private ItemClient itemClient;

    @Override
    public void saveItemById(Long id) {
        try {
            //1.查询商品数据
            Item item = itemClient.queryItemById(id);
            //2.准备Request
            IndexRequest request = new IndexRequest("item").id(id.toString());
            //3.准备DSL
            request.source(MAPPER.writeValueAsString(new ItemDoc(item)), XContentType.JSON);
            //4.发送请求
            restHighLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteItemById(Long id) {
        try {
            //1.准备Request
            DeleteRequest request = new DeleteRequest("item", id.toString());
            //2.发送请求
            restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void buildBasicQuery(SearchRequest request, RequestParams params) {
        //创建布尔查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 1. key
        String key = params.getKey();
        if (StringUtils.isNotBlank(key)) {
            //非空
            boolQueryBuilder.must(QueryBuilders.matchQuery("all", key));
        } else {
            //空
            boolQueryBuilder.must(QueryBuilders.matchAllQuery());
        }
        // 2. brand
        String brand = params.getBrand();
        if (StringUtils.isNotBlank(brand)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("brand", brand));
        }
        // 3. category
        String category = params.getCategory();
        if (StringUtils.isNotBlank(category)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("category", category));
        }
        // 4. price
        Long maxPrice = params.getMaxPrice();
        Long minPrice = params.getMinPrice();
        if (minPrice != null && maxPrice != null) {
            boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(minPrice * 100).lte(maxPrice * 100));
        }
        //放入request
        request.source().query(boolQueryBuilder);
    }
}
