package com.hmall.search.feign;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hmall.common.client.ItemClient;
import com.hmall.common.dto.Item;
import com.hmall.common.dto.PageDTO;
import com.hmall.search.pojo.ItemDoc;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;

/**
 * @projectName: Hmall
 * @package: com.hmall.search.feign
 * @className: FeignTest
 * @author: xuxiang
 * @description: TODO
 * @date: 2023/10/28 20:48
 * @version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FeignTest {

    @Autowired
    private ItemClient itemClient;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testQueryItem() throws IOException {
        int page = 1;
        while (true) {
            PageDTO<Item> pageDTO = itemClient.queryItemByPage(page, 500);
            List<Item> list = pageDTO.getList();
            if (list.size() <= 0) {
                break;
            }
            //1.准备BulkRequest
            BulkRequest request = new BulkRequest();
            //2.准备DSL
            //遍历
            for (Item item : list) {
                if (item.getStatus()==2){
                    //下架商品直接跳过
                    continue;
                }
                //将Item转为ItemDoc
                ItemDoc itemDoc = new ItemDoc(item);
                //添加新增请求
                request.add(new IndexRequest("item")
                        .id(itemDoc.getId().toString())
                        .source(MAPPER.writeValueAsString(itemDoc), XContentType.JSON));
            }
            //3.发请求,批量处理
            BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);

            page++;
        }
    }


}
