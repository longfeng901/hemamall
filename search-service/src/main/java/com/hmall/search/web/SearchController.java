package com.hmall.search.web;

import com.hmall.common.dto.PageDTO;
import com.hmall.search.pojo.ItemDoc;
import com.hmall.search.pojo.RequestParams;
import com.hmall.search.service.ISearchServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @projectName: Hmall
 * @package: com.hmall.search.web
 * @className: SearchController
 * @author: xuxiang
 * @description: TODO
 * @date: 2023/10/24 20:51
 * @version: 1.0
 */
@RestController
@RequestMapping("search")
public class SearchController {

    @Autowired
    private ISearchServiceI searchServiceI;

    /**
     * 自动补全
     *
     * @param prefix
     * @return {@link List}<{@link String}>
     */
    @GetMapping("/suggestion")
    public List<String> getSuggestion(@RequestParam("key") String prefix) {
        return searchServiceI.getSuggestion(prefix);
    }

    /**
     * 聚合
     *
     * @param params
     * @return {@link Map}<{@link String}, {@link List}<{@link String}>>
     */
    @PostMapping("filters")
    public Map<String, List<String>> getFilters(@RequestBody RequestParams params) {
        return searchServiceI.getFilters(params);
    }

    @PostMapping("list")
    public PageDTO<ItemDoc>search(@RequestBody RequestParams params) {
        return searchServiceI.search(params);
    }
}
